package com.mkweb.security;

import java.util.ArrayList;

import java.util.Arrays;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;

import com.mkweb.config.MkPageConfigs;
import com.mkweb.config.MkRestApiPageConfigs;
import com.mkweb.config.MkSQLXmlConfigs;
import com.mkweb.data.PageXmlData;
import com.mkweb.data.SqlXmlData;
import com.mkweb.logger.MkLogger;

public class CheckPageInfo {
	private String TAG = "[CheckPageInfo]";
	private MkLogger mklogger = MkLogger.Me();
	
	public String regularQuery(String serviceName) {
		SqlXmlData resultXmlData = MkSQLXmlConfigs.Me().getControlService(serviceName);
		
		if(resultXmlData == null) {
			mklogger.error(TAG + "There is no sql service named : " + serviceName);
			return null;
		}
		
		return resultXmlData.getData();
	}
	
	public String getRequestPageParameterName(HttpServletRequest request) {
		Enumeration params = request.getParameterNames();
		String requestParams = null;
		while(params.hasMoreElements()) {
			String name = params.nextElement().toString().trim();
			if(name.contains(".")) {
				requestParams = name.split("\\.")[0]; 
			}else {
				mklogger.error(TAG + " Invalid request type.");
				return null;
			}
		}
		return requestParams;
	}
	
	public ArrayList<String> getRequestParameterValues(HttpServletRequest request){
		ArrayList<String> requestValues = new ArrayList<String>();
		Enumeration params = request.getParameterNames();
		while(params.hasMoreElements()) {
			String name = params.nextElement().toString().trim();
			if(name.contains(".")) {
				requestValues.add(name.split("\\.")[1]);
			}else {
				mklogger.error(TAG + " Invalid request type.");
				return null;
			}
		}
		
		return requestValues;
	}
	
	public String setApiQuery(String query, ArrayList<String> key) {
		String befQuery = query;
		String[] testQueryList = befQuery.split("@CONDITION@");
		if(testQueryList.length == 1)
			return befQuery;

		String condition = " WHERE ";
		for(int i = 0; i < key.size(); i++) {
			condition += key.get(i) + "=" + "?";
			
			if(i < key.size() -1)
				condition += " AND ";
		}
		befQuery = befQuery.replace("@CONDITION@", condition);

		return befQuery;
	}
	
	public String setApiQueryLike(String query, ArrayList<String> key) {
		String befQuery = query;
		String[] testQueryList = befQuery.split("@CONDITION@");
		if(testQueryList.length == 1)
			return befQuery;
		
		String condition = " WHERE ";
		for(int i = 0; i < key.size(); i++) {
			condition += key.get(i) + " LIKE " + "?";
			
			if(i < key.size() -1)
				condition += " AND ";
		}
		befQuery = befQuery.replace("@CONDITION@", condition);
		
		return befQuery;
	}
	
	public String setQuery(String query) {
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
	
	public boolean comparePageValueWithRequest(String pageValue, ArrayList<String> reqValue) {
		String pv = "";
		pageValue = pageValue.trim();
		
		String[] pvSetList = pageValue.split("=");
		try {
			for(int i = 0; i < (pvSetList.length-1); i++) {
				pv += pvSetList[i].split("\\.")[1].trim();
			}
		}catch(java.lang.NullPointerException e) {
			mklogger.error(TAG + " Wrong split tried on Page value. Please check pageValue. You need to follow MKWeb parameter rule." + e.getMessage());
			return false;
		}catch(java.lang.ArrayIndexOutOfBoundsException e1) {
			mklogger.error(TAG + " Wrong index on Page value. " + e1.getMessage());
			return false;
		}
		pv = pv.trim();
		String rv = "";
		try {
			for(int i = 0; i < reqValue.size(); i++) {
				rv += reqValue.get(i).trim();
			}
		}catch(java.lang.NullPointerException e) {
			mklogger.error(TAG + " Wrong split tried on Request value. Please check what user requested. You need to follow MKWeb parameter rule." + e.getMessage());
			mklogger.error(TAG + " User Request: " + rv);
			return false;
		}catch(java.lang.ArrayIndexOutOfBoundsException e1) {
			mklogger.error(TAG + " Wrong index on Request value. " + e1.getMessage());
			return false;
		}
		
		rv = rv.trim();
		char[] pvArray = pv.toCharArray();
		char[] rvArray = rv.toCharArray();
		Arrays.sort(pvArray);
		Arrays.sort(rvArray);
		
		pv = String.valueOf(pvArray);
		rv = String.valueOf(rvArray);
		
		mklogger.info(TAG + " Compare: PV(" + pv + ") vs RV(" + rv + ")");
		
		return (pv.equals(rv));
	}
	
    public boolean isValidPageConnection(String requestControlName, String[] requestDir) {
		ArrayList<PageXmlData> resultXmlData = MkPageConfigs.Me().getControl(requestControlName);
		
		if(resultXmlData == null || resultXmlData.size() < 1)
			return false;
		PageXmlData xmlData = resultXmlData.get(0);
		/*
		 * 오직허용: log_dir + page control name
		 * requestDir = URI / 자른거.
		 * mkPage = request page control name
		 */
		String AllowPath = xmlData.getLogicalDir();
		String userLogicalDir = "";
		
		if(requestDir != null) {
			for(int i = 1; i < requestDir.length-1; i++) 
				userLogicalDir += "/" + requestDir[i];
		}
		
		if(userLogicalDir.equals(""))
			userLogicalDir = "/";
		
		String c1 = userLogicalDir + requestControlName;
		String c2 = AllowPath + xmlData.getControlName();
		
		if(!c1.equals(c2))
			return false;
		
		return true;
	}
    
    public boolean isValidApiPageConnection(String requestControlName, String[] requestDir) {
		ArrayList<PageXmlData> resultXmlData = MkRestApiPageConfigs.Me().getControl(requestControlName);
		
		if(resultXmlData == null || resultXmlData.size() < 1)
			return false;
		PageXmlData xmlData = resultXmlData.get(0);
		/*
		 * 오직허용: log_dir + page control name
		 * requestDir = URI / 자른거.
		 * mkPage = request page control name
		 */
		String AllowPath = xmlData.getLogicalDir();
		String userLogicalDir = "";
		
		if(requestDir != null) {
			for(int i = 1; i < requestDir.length-1; i++) 
				userLogicalDir += "/" + requestDir[i];
		}
		
		if(userLogicalDir.equals(""))
			userLogicalDir = "/";
		
		String c1 = userLogicalDir + requestControlName;
		String c2 = AllowPath + xmlData.getControlName();
		
		if(!c1.equals(c2)){
			return false;
		}
		
		return true;
	}
	
}
