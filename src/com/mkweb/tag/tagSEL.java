package com.mkweb.tag;

import java.io.IOException;


import java.util.ArrayList;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import com.mkweb.database.MkDbAccessor;
import com.mkweb.logger.MkLogger;
import com.mkweb.security.CheckPageInfo;
import com.mkweb.web.PageInfo;

public class tagSEL extends SimpleTagSupport {
	private String obj;
	private String like = "yes";
	private String rst = "get";
	private String TAG = "[tagSEL]";
	private MkLogger mklogger = MkLogger.Me();
	//Log го╠Б
	public void setObj(String obj) {
		this.obj = obj;
	}
	
	public void setRst(String rst) {
		this.rst = rst;
	}
	
	public void setLike(String like) {
		this.like = like;
	}
	
	public String getResultId() {
		return this.rst;
	}
	
	private PageInfo getPageControl(HttpServletRequest request) {
		Object o = request.getAttribute("mkPage");
		if(o == null) {	return null;	}
		
		String controlName = o.toString();
		
		return new PageInfo(controlName, false);
	}
	
	public void doTag() throws JspException, IOException{
		MkDbAccessor DA;
		CheckPageInfo cpi = new CheckPageInfo();
		ArrayList<Object> dbResult = new ArrayList<Object>();
		
		HttpServletRequest request = (HttpServletRequest) ((PageContext)getJspContext()).getRequest();
		HttpServletResponse response = (HttpServletResponse) ((PageContext)getJspContext()).getResponse();
		
		request.setCharacterEncoding("UTF-8");
		
		String requestParams = null;
		ArrayList<String> requestValues = new ArrayList<String>();
		
		requestParams = cpi.getRequestPageParameterName(request);
		requestValues = cpi.getRequestParameterValues(request);
		
		PageInfo pageInfo = getPageControl(request);
		
		if(!pageInfo.isSet()) {
			mklogger.error(TAG + " PageInfo is not set!");
			return;
		}
		
		//pageConfig Parameters
		ArrayList<String> pageParameter = pageInfo.getPageParameter();
		ArrayList<String[]> pageSqlInfo = pageInfo.getPageSqlInfo();
		ArrayList<String> pageValue = pageInfo.getPageValue();
		
		int rstID = -1;
		for(int i = 0; i < pageSqlInfo.size(); i++) {
			if(this.rst.equals(pageSqlInfo.get(i)[2])) {
				rstID = i;
				break;
			}
		}
		
		if(rstID == -1) {
			mklogger.error(TAG + " Tag 'rst(" + this.rst + ")' is not matched with Page-config 'rst'.");
			return;
		}
		
		if(!cpi.comparePageValueWithRequest(pageValue.get(rstID), requestValues)) {
			mklogger.error(TAG + " Request Value is not authorized. Please check page config.");
			return;
		}
		
		String befQuery = cpi.regularQuery(pageSqlInfo.get(rstID)[0]);
		
		String query = null;
		query = cpi.setQuery(befQuery);
		
		if(query == null)
			query = befQuery;
		
		if(!requestParams.equals(pageParameter.get(rstID))) {
			mklogger.error(TAG + " Request parameter is invalid. Please check page config. (" + requestParams + ")");
			return;
		}

		if(this.obj == "list")
		{
			DA = new MkDbAccessor();
			DA.setPreparedStatement(query);
			if(requestValues != null) {
				String[] reqs = new String[requestValues.size()];
				String tempValue = "";
				for(int i = 0; i < reqs.length; i++) {
					tempValue = request.getParameter(requestParams + "." + requestValues.get(i));
					if(this.like.equals("no"))
					{
						if(tempValue.contains("%"))
							tempValue = tempValue.replaceAll("%", " ");
						
						reqs[i] = tempValue;
					}else {
						reqs[i] = tempValue;
					}
				}
				tempValue = null;
				DA.setRequestValue(reqs);
				reqs = null;
			}
			if(this.like.equals("no"))
				dbResult = DA.executeSEL(false);
			else
				dbResult = DA.executeSELLike(false);
			
			HashMap<String, Object> result = new HashMap<String, Object>();
			
			if(dbResult != null && dbResult.size() > 0)
			{
				for(int i = 0; i < dbResult.size(); i++)
				{
					result = (HashMap<String, Object>) dbResult.get(i);
					
					((PageContext)getJspContext()).getRequest().setAttribute(this.rst, result);
					getJspBody().invoke(null);
				}
				((PageContext)getJspContext()).getRequest().removeAttribute(this.rst);
			}else {
				return;
			}
		}else if(this.obj =="map") {
			DA = new MkDbAccessor();
		}
		
	}
}
