package com.mkweb.tag;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import com.mkweb.config.PageConfigs;
import com.mkweb.config.SQLXmlConfigs;
import com.mkweb.data.AbsXmlData;
import com.mkweb.data.PageXmlData;
import com.mkweb.database.MkDbAccessor;
import com.mkweb.logger.MkLogger;
import com.mkweb.web.PageInfo;

public class tagSEL extends SimpleTagSupport {
	private String obj;
	private String rst = "get";
	private String TAG = "[tagSEL]";
	private MkLogger mklogger = MkLogger.Me();
	//Log 하기
	public void setObj(String obj) {
		this.obj = obj;
	}
	
	public void setRst(String rst) {
		this.rst = rst;
	}
	
	public String getResultId() {
		return this.rst;
	}
	
	private PageInfo getPageControl(HttpServletRequest request) {
		Object o = request.getAttribute("mkPage");
		if(o == null) {	return null;	}
		
		String controlName = o.toString();
		
		return new PageInfo(controlName);
	}
	
	public void doTag() throws JspException, IOException{
		MkDbAccessor DA;
		ArrayList<Object> dbResult = new ArrayList<Object>();
		
		HttpServletRequest request = (HttpServletRequest) ((PageContext)getJspContext()).getRequest();
		HttpServletResponse response = (HttpServletResponse) ((PageContext)getJspContext()).getResponse();
		
		request.setCharacterEncoding("UTF-8");
		
		ArrayList<String> requestParams = new ArrayList<>();
		Enumeration params = request.getParameterNames();
		
		while(params.hasMoreElements()) {
			String name = (String)params.nextElement();
			requestParams.add(name);
		}
		
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
		
		int paramID = -1;
		for(int i = 0; i < requestParams.size(); i++) {
			if(requestParams.get(i).equals(pageParameter.get(rstID))) {
				paramID = i;
				break;
			}
		}
		
		if(rstID == -1) {
			mklogger.error(TAG + " Tag 'rst("+this.rst+")' is not matched with Page-config 'rst'.");
			return;
		}
		
		AbsXmlData resultXmlData = SQLXmlConfigs.Me().getControlService(pageSqlInfo.get(rstID)[0]);
		
		if(resultXmlData == null) {
			mklogger.error(TAG + "There is no sql service named : " + pageSqlInfo.get(rstID)[0]);
			return;
		}
		
		String befQuery = resultXmlData.getData();
		
		String query = null;
		query = setQuery(befQuery);
		
		if(query == null)
			query = befQuery;
		ArrayList<String> preQueryParameter = null;
		if(paramID != -1) {
			
			preQueryParameter = prepareQueryParameter(pageValue.get(rstID), pageParameter.get(rstID), request.getParameter(requestParams.get(paramID)), 1);
			
			if(preQueryParameter == null) {
				if(!query.equals(befQuery)) {
					mklogger.error(TAG + "파라미터랑 쿼리 파라미터랑 다름.");
					
				}
			}
		}else {
			preQueryParameter = prepareQueryParameter(pageValue.get(rstID), pageParameter.get(rstID), null, 2);
		}
		
		if(this.obj == "list")
		{
			DA = new MkDbAccessor();
			DA.setPreparedStatement(query);
			if(preQueryParameter != null)
				DA.setRequestValue(preQueryParameter);
			dbResult = DA.executeSEL();
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
	
	private String setQuery(String query) {
		String befQuery = query;
		String[] testQueryList = befQuery.split("@");
		String[] replaceTarget = null;
		
		if(testQueryList.length == 1)
			testQueryList = null;
		
		if(testQueryList != null) {
			if(testQueryList.length > 0)
			{
				replaceTarget = new String[(testQueryList.length-1)/2];
				for(int i = 0; i < replaceTarget.length; i++) {
					replaceTarget[i] = testQueryList[(i*2)+1];
				}
			}else {	return null;	}
		}
		
		if(replaceTarget != null) {
			for(int i = 0; i < replaceTarget.length; i++) {
				befQuery = befQuery.replaceFirst(("@" + replaceTarget[i]+ "@"), "?");
			}
		}else {
			return null;
		}
	
		return befQuery;
	}
	
	private ArrayList<String> prepareQueryParameter(String pageValue, String pageParam, String reqValue, int type){
		ArrayList<String> result = null;
		
		if(type == 1)
			pageValue = "@set(" + pageParam + pageValue.split("=")[0].split(pageParam)[1] +" = '"+reqValue+"')";

		result = new ArrayList<String>();

		String[] pvSetList = pageValue.split("@set");
		
		if(pvSetList.length <= 1) {
			mklogger.error(TAG + "잘못된 설정");
			return null;
		}
		
		for(int i = 1; i < pvSetList.length; i++)
		{
			String[] equalSplit = pvSetList[i].split("="); 
			
			if(equalSplit.length <= 1){
				mklogger.error(TAG + "잘못된 설정 =가 없음");
				return null;
			}

			String[] lastSplit = equalSplit[i].split("\\)");

			if(lastSplit.length != 1) {
				mklogger.error(TAG + "잘못된 설정 끝 문자 인식 불가능 ");
				return null;
			}

			String value = lastSplit[0].trim();

			value = value.substring(1, value.length()-1);
			result.add(value);
		}
		
		return result;
	}
}
