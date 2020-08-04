package com.mkweb.security;

import java.util.ArrayList;


import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashMap;

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
	
	private String isPageParamValid(ArrayList<String> pageStaticParams, String reqParams) {
		if(pageStaticParams == null || pageStaticParams.size() == 0) {
			return null;
		}
		for(int i = 0; i < pageStaticParams.size(); i++) {
			if(reqParams.contentEquals(pageStaticParams.get(i))) {
				return reqParams;
			}
		}
		return null;
	}
	
	private String getPageStaticParameter(ArrayList<String> pageStaticParams, String comparison){
		String result = null;
		
		for(String s : pageStaticParams) {
			if(s.contentEquals(comparison)) {
				result = s;
				break;
			}
		}
		
		return result;
	}
	
	public String getRequestPageParameterName(HttpServletRequest request, ArrayList<String> pageStaticParams, String pageStaticParamsName) {
		Enumeration params = request.getParameterNames();
		String requestParams = null;
		while(params.hasMoreElements()) {
			String name = params.nextElement().toString().trim();
			
			if(name.contains(".")) {
				String nname = name.split("\\.")[0];
				if(requestParams != null) {
					if( !requestParams.contentEquals("") && !requestParams.contentEquals(nname)) {
						if(!requestParams.contentEquals(pageStaticParamsName)) {
							return null;
						}
					}
				}
				if(pageStaticParamsName != null) {
					if(!nname.contentEquals(pageStaticParamsName))
						requestParams = nname; 
				}
				else
					requestParams = nname;
			} else {
				requestParams = isPageParamValid(pageStaticParams, name);
			}
			
			if(requestParams != null && !requestParams.contentEquals(pageStaticParamsName)) {
				return requestParams;
			}
		}
		
		return null;
	}
	
	public ArrayList<String> getRequestParameterValues(HttpServletRequest request, String rstID, ArrayList<String> pageParams, String pageStaticParamsName){
		ArrayList<String> requestValues = new ArrayList<String>();
		Enumeration params = request.getParameterNames();

		while(params.hasMoreElements()) {
			String name = params.nextElement().toString().trim();
			String[] nname = name.split("\\.");
			if(name.contains(".")) {
				if(pageStaticParamsName != null) {
					if(!nname[0].contentEquals(pageStaticParamsName) && nname[0].contentEquals(rstID))
						requestValues.add(nname[1]);
				}else {
					if(nname[0].contentEquals(rstID))
						requestValues.add(nname[1]);
				}
				
			}else {
				if(isPageParamValid(pageParams, name) != null) {
					requestValues.add(getPageStaticParameter(pageParams, name));
				}else {
					mklogger.error(TAG + " (getRequestParameterValues) Invalid request parameter : " + name );
					return null;
				}
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
		if(befQuery != null) {
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
		}
		return befQuery;
	}
	
	public boolean comparePageValueWithRequest(String pageValue, ArrayList<String> reqValue, ArrayList<String> staticParams, boolean isApi) {
		String pv = "";
		HashMap<String, Boolean> passValues = new HashMap<>();
		pageValue = pageValue.trim();
		
		if(staticParams != null) {
			for(int i = 0; i < staticParams.size(); i++) {
				passValues.put(staticParams.get(i).trim(), true);
			}
		}
		String[] pvSetList = pageValue.split("=");
		
		try {
			boolean passExists = false;
			if(passValues.size() != 0)
				passExists = true;
			for(int i = 0; i < (pvSetList.length-1); i++) {
				String t = pvSetList[i].split("\\.")[1].trim();
				if(passExists) {
					if(passValues.get(t) == null) {
						pv += t;
						break;
					}
				}else {
					pv += t;
				}
			//	pv += pvSetList[i].split("\\.")[1].trim();
			}
		}catch(java.lang.NullPointerException e) {
			mklogger.error(TAG + " Wrong split tried on Page value(PageValue). Please check pageValue. You need to follow MKWeb parameter rule." + e.getMessage());
			return false;
		}catch(java.lang.ArrayIndexOutOfBoundsException e1) {
			mklogger.error(TAG + " Wrong index on Page value(PageValue). " + e1.getMessage());
			return false;
		}
		pv = pv.trim();
		String rv = "";
		try {
			boolean passExists = false;
			if(passValues.size() != 0)
				passExists = true;
			for(int i = 0; i < reqValue.size(); i++) {
				if(passExists) {
					if(passValues.get(reqValue.get(i)) == null) {
						rv += reqValue.get(i).trim();
						break;
					}
				}else {
					rv += reqValue.get(i).trim();
				}
			}
		}catch(java.lang.NullPointerException e) {
			mklogger.error(TAG + " Wrong split tried on Request value(RealValue). Please check what user requested. You need to follow MKWeb parameter rule." + e.getMessage());
			mklogger.error(TAG + " User Request: " + rv);
			return false;
		}catch(java.lang.ArrayIndexOutOfBoundsException e1) {
			mklogger.error(TAG + " Wrong index on Request value(RealValue). " + e1.getMessage());
			return false;
		}
		
		rv = rv.trim();
		if(isApi)
			return pv.contains(rv);
		else {
			char[] pvArray = pv.toCharArray();
			char[] rvArray = rv.toCharArray();
			Arrays.sort(pvArray);
			Arrays.sort(rvArray);
			
			pv = String.valueOf(pvArray);
			rv = String.valueOf(rvArray);
			
			mklogger.info(TAG + " Compare: PV(" + pv + ") vs RV(" + rv + ")");
			
			return (pv.equals(rv));
		}
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
		String userLogicalDir = "";
		
		if(requestDir != null) {
			for(int i = 1; i < requestDir.length-1; i++) 
				userLogicalDir += "/" + requestDir[i];
		}
		
		if(userLogicalDir.equals(""))
			userLogicalDir = "/";
		
		String c1 = userLogicalDir + requestControlName;
		String c2 = "/" + xmlData.getControlName();
		
		if(!c1.equals(c2)){
			return false;
		}
		
		return true;
	}
	
    public LinkedHashMap<String, Boolean> pageValueToHashMap(String pageValue){
    	LinkedHashMap<String, Boolean> result = null;
    	if(pageValue == null || pageValue.length() == 0) 
    		return null;
    	
    	result = new LinkedHashMap<>();
    	String[] splits = pageValue.split("@set");
    	
    	if(splits.length == 1)
    		return null;
    	for(int i = 1; i < splits.length; i++) {
    		String s = null;
    		splits[i] = splits[i].trim();
    		
    		if(splits[i].contains(".")) {
    			s = splits[i].split("\\.")[1];
    		}else {
    			s = splits[i];
    		}
    		
    		if(!s.contains("=")) {
    			mklogger.error(TAG + " Invalid Page Value. There is no equal character. :: " + s);
    			return null;
    		}
    		
    		s = s.split("=")[0].trim();
    		result.put(s, true);
    	}
    	return result;
    }
}
