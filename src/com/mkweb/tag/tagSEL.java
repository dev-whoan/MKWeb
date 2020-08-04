package com.mkweb.tag;

import java.io.IOException;




import java.util.ArrayList;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import com.mkweb.config.MkPageConfigs;
import com.mkweb.data.PageXmlData;
import com.mkweb.database.MkDbAccessor;
import com.mkweb.logger.MkLogger;
import com.mkweb.security.CheckPageInfo;

public class tagSEL extends SimpleTagSupport {
	private String obj;
	private String like = "yes";
	private String name = "get";
	private String TAG = "[tagSEL]";
	private MkLogger mklogger = MkLogger.Me();
	//Log 하기
	public void setObj(String obj) {
		this.obj = obj;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setLike(String like) {
		this.like = like;
	}

	public String getName() {
		return this.name;
	}

	private ArrayList<PageXmlData> getPageControl(HttpServletRequest request) {
		Object o = request.getAttribute("mkPage");
		if(o == null) {	return null;	}

		String controlName = o.toString();

		return MkPageConfigs.Me().getControl(controlName);
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
		
		ArrayList<PageXmlData> pageInfo = getPageControl(request);
		boolean isSet = (pageInfo == null || pageInfo.size() == 0) ? false : true;
		ArrayList<String> pageStaticParams = null;
		String pageStaticParamsName = null;
		ArrayList<String> requestServiceName = null;
		ArrayList<String> pageParameter = null;
		ArrayList<String[]> pageSqlInfo = null;
		ArrayList<LinkedHashMap<String, Boolean>> pageValue = null;
		
		if(isSet) {
			pageStaticParams = new ArrayList<>();
			pageStaticParamsName = "";
			pageParameter = new ArrayList<>();
			pageSqlInfo = new ArrayList<>();
			pageValue = new ArrayList<>();
			requestServiceName = new ArrayList<>();
			//pageConfig Parameters
			for(int i = 0; i < pageInfo.size(); i++) {
				pageParameter.add(pageInfo.get(i).getParameter());
				pageSqlInfo.add(pageInfo.get(i).getSql());
				pageValue.add(pageInfo.get(i).getPageValue());
				requestServiceName.add(pageInfo.get(i).getServiceName());
			}
		}
		
		int serviceIndex = -1;
		for(int i = 0; i < requestServiceName.size(); i++) {
			if(this.name.equals(requestServiceName.get(i))) {
				serviceIndex = i;
				break;
			}
		}
		
		mklogger.debug(TAG + " serviceIndex: " + serviceIndex);
				
		if(serviceIndex == -1) {
			mklogger.error(TAG + " Tag 'name(" + this.name + ")' is not matched with Page-config-service 'name'.");
		//	response.sendError(500);
			return;
		}

		requestParams = cpi.getRequestPageParameterName(request, pageStaticParams, pageStaticParamsName);
		requestValues = cpi.getRequestParameterValues(request, pageParameter.get(serviceIndex), pageStaticParams, pageStaticParamsName);
		
		if(!cpi.comparePageValueWithRequest(
				pageValue.get(serviceIndex), 
				requestValues,
				pageStaticParams,
				false)
		){
			mklogger.error(TAG + " Request Value is not authorized. Please check page config.");
		//	response.sendError(500);
			return;
		}
		
		LinkedHashMap<String, Boolean> rqvHash = new LinkedHashMap<>();
		LinkedHashMap<String, Boolean> pvHash = pageValue.get(serviceIndex);
		
		if(requestValues != null && requestValues.size() > 0) {
			for(String s : requestValues) {
				rqvHash.put(s, true);
			}
		}
		
		boolean doCheckPageValue = (pvHash != null && pvHash.size() > 0);
		
		requestValues.clear();
		requestValues = null;
		
		if(doCheckPageValue) {
		    Set entrySet = rqvHash.keySet();
		    Iterator iter = entrySet.iterator();
		    Set pvEntrySet = null;
		    Iterator pvIter = null;
		    
		    while(iter.hasNext()) {
				String key = (String) iter.next();
				pvEntrySet = pvHash.keySet();
			    pvIter = pvEntrySet.iterator();
			    
				while(pvIter.hasNext()) {
					String pvKey = (String) pvIter.next();
					if(key.contentEquals(pvKey)) {
						if(requestValues == null)
							requestValues = new ArrayList<>();
						requestValues.add(key);
					}
				}
			}
		}
		
		String befQuery = cpi.regularQuery(requestServiceName.get(serviceIndex));

		String query = null;
		query = cpi.setQuery(befQuery);
		if(query == null)
			query = befQuery;
		
		boolean rvPassed = true;
		
		mklogger.debug(TAG + " psp: " + pageStaticParams);
		
		if(requestValues != null && pageStaticParams != null) {
			if(requestValues.size() > pageStaticParams.size()) {
				rvPassed = false;
			} else {
				boolean isDone = false;
				int sameCount = 0;
				for(String rqv : requestValues) {
					boolean passNow = false;
					for(String psp : pageStaticParams) {
						if(rqv.contentEquals(psp)) {
							passNow = true;
							sameCount++;
							if(sameCount >= requestValues.size())
								isDone = true;
							break;
						}
					}
					if(isDone)
						break;
					if(passNow)
						continue;
					
				}
				mklogger.debug(TAG + " isDone : " + isDone);
				rvPassed = isDone;
			}
		}
		
		if(!rvPassed) {
			
			if(requestParams != null && pageParameter.get(serviceIndex) != null) {
				if(!requestParams.contentEquals(pageParameter.get(serviceIndex))) {
					if(!requestParams.contentEquals(pageStaticParamsName)) {
						mklogger.error(TAG + " Request parameter is invalid(1). Please check page config. (" + requestParams + ")");
						//	response.sendError(500);
						return;
					}
				}
			}else {
				if( (requestParams != null && pageParameter.get(serviceIndex) == null) || (requestParams == null && pageParameter.get(serviceIndex) != null))
				{
					if(!requestParams.contentEquals(pageStaticParamsName)) {
						mklogger.error(TAG + " Request parameter is invalid(2). Please check page config. (" + requestParams + ")");
						//	response.sendError(500);
						return;	
					}
				}
			}
		}

		if(this.obj == "list")
		{
			DA = new MkDbAccessor();
			DA.setPreparedStatement(query);
			if(requestValues != null) {
				String[] reqs = new String[requestValues.size()];
				String tempValue = "";
				for(int i = 0; i < reqs.length; i++) {
					mklogger.debug(TAG + "params: " + requestParams + " || Values : " + requestValues.get(i));
					tempValue = request.getParameter(requestParams + "." + requestValues.get(i));
					if(tempValue == null)
						tempValue = request.getParameter(requestValues.get(i));
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

			LinkedHashMap<String, Object> result = new LinkedHashMap<String, Object>();

			if(dbResult != null && dbResult.size() > 0)
			{
				for(int i = 0; i < dbResult.size(); i++)
				{
					result = (LinkedHashMap<String, Object>) dbResult.get(i);
					((PageContext)getJspContext()).getRequest().setAttribute("mkw", result);
					getJspBody().invoke(null);
				}
				((PageContext)getJspContext()).getRequest().removeAttribute("mkw");
			}else {
				return;
			}
		}else if(this.obj =="map") {
			DA = new MkDbAccessor();
		}

	}
}
