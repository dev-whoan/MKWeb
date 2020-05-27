package com.mkweb.tag;

import java.io.IOException;
import java.util.ArrayList;
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
		
		PageInfo pageInfo = getPageControl(request);
		
		if(!pageInfo.isSet()) {
			mklogger.error(TAG + " PageInfo is not set!");
			return;
		}
		
		String pageParameter = pageInfo.getPageParameter();
		String[] pageSqlInfo = pageInfo.getPageSqlInfo();
		String pageValue = pageInfo.getPageValue();
		
		AbsXmlData resultXmlData = SQLXmlConfigs.Me().getControlService(pageSqlInfo[0]);
		
		if(resultXmlData == null) {
			mklogger.error("There is no sql service named : " + pageSqlInfo[0]);
			return;
		}
		
		String befQuery = resultXmlData.getData();
		String query = getQueryValue(befQuery, pageParameter, pageValue);
		
		if(query == null)
		{
			query = befQuery;
		}
		
		if(this.obj == "list")
		{
			DA = new MkDbAccessor();
			
			dbResult = DA.executeSEL(query);
			HashMap<String, Object> result = new HashMap<String, Object>();
			
			if(dbResult.size() > 0)
			{
				for(int i = 0; i < dbResult.size(); i++)
				{
					result = (HashMap<String, Object>) dbResult.get(i);
					
					((PageContext)getJspContext()).getRequest().setAttribute(this.rst, result);
					getJspBody().invoke(null);
				}
				((PageContext)getJspContext()).getRequest().removeAttribute(this.rst);
			}else {
				try {
					RequestDispatcher dispatcher = ((PageContext)getJspContext()).getServletContext().getRequestDispatcher("/500.jsp");
					dispatcher.forward(request, response);
				} catch (ServletException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				return;
			}
			
			//getJspContext()).getRequest().setAttribute
			
		}else if(this.obj =="map") {
			DA = new MkDbAccessor();
		}
	}
	
	private String getQueryValue(String query, String pageParam, String pageValue) {
		String befQuery = query;
		String[] testQueryList = befQuery.split("@");
		String testQuery = "";
		String[] replaceTarget = null;
		if(testQueryList.length > 0)
		{
			replaceTarget = new String[(testQueryList.length-1)/2];
			for(int i = 0; i < replaceTarget.length; i++)
				replaceTarget[i] = testQueryList[(i*2)+1];
		}else {	return null;	}
		
		String[] pvSetList = null;
		
		if(pageValue != null && pageValue.length() > 0)
		{
			pvSetList = new String[pageValue.split("@set").length - 1]; 
					
			if(pvSetList.length > 0)
			{
				for(int i = 0; i < pvSetList.length; i++) {
					pvSetList[i] = pageValue.split("@set")[i+1].trim(); 
					pvSetList[i] = pvSetList[i].split("}")[0];
				}
			}
		}else {
			mklogger.error(TAG + " Page config is not matched with Sql config. ");
			return null;
		}
		if(pvSetList != null && replaceTarget != null) {
			if(pvSetList.length == replaceTarget.length) {
				for(int i = 0; i < pvSetList.length; i++){
			//		(${param.user_name = '최기현'
					String pp = pageParam + ".";
			//		user_name = '최기현'
					String[] alpha = pvSetList[i].split(pp); //[1].split("=")[0];
					
					if(alpha == null || alpha.length != 2) {
						mklogger.error(TAG + "Page Parameter setting is invalid. Please Check <Page Parameter> and <Page Value> :: " + pageParam);
						return null;
					}
			//		user_name
					String[] beta = alpha[1].split("=");
					if(beta == null || beta.length != 2) {
						mklogger.error(TAG + "<Page Value> syntax error. Cannot find equal character(\"=\").");
						return null;
					}
					String charlie = beta[0].trim();
					
					if(!charlie.equals(replaceTarget[i])) {
						mklogger.temp(TAG + " Page config is not matched with Sql config.", false);
						mklogger.temp("================================Page Value(" + charlie + ") != Sql Value(" + replaceTarget[i] + ")", false);
						mklogger.flush("error");
						
						return null;
					}
					
					String pvSetValue = pvSetList[i].split("=")[1];
					befQuery = befQuery.replaceFirst(("@" + replaceTarget[i]+ "@"), pvSetValue);
				}
			}else {
				mklogger.error(TAG + " Page config is not matched with Sql config. ");
				return null;
			}
		}else {
			mklogger.error(TAG + " Page config is not matched with Sql config. ");
			return null;
		}
		
		return befQuery;
	}
}
