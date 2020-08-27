package com.mkweb.security;

import java.util.ArrayList;


import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Set;

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
			mklogger.error(TAG, "There is no sql service named : " + serviceName);
			return null;
		}

		return resultXmlData.getData();
	}

	private String isPageParamValid(ArrayList<String> pageStaticParams, String reqParams) {
		if(pageStaticParams == null || pageStaticParams.size() == 0) {
			mklogger.debug(TAG, " null here (1)");
			return null;
		}
		for(int i = 0; i < pageStaticParams.size(); i++) {
			if(reqParams.contentEquals(pageStaticParams.get(i))) {
				mklogger.debug(TAG, " ippv return here : " + reqParams);
				return reqParams;
			}
		}
		mklogger.debug(TAG, " null here (2)");
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

	// 고치세요~~
	public String getRequestPageParameterName(HttpServletRequest request, ArrayList<String> pageStaticParams, String pageStaticParamsName) {
		Enumeration params = request.getParameterNames();
		String requestParams = null;
		while(params.hasMoreElements()) {
			String name = params.nextElement().toString().trim();
			mklogger.debug(TAG, " name : " + name + " || staticParams : " + pageStaticParams);
			if(name.contains(".")) {
				String nname = name.split("\\.")[0];
				if(requestParams != null) {
					if( !requestParams.contentEquals("") && !requestParams.contentEquals(nname)) {
						if(!requestParams.contentEquals(pageStaticParamsName)) {
							mklogger.debug(TAG, " param null here (1)");
							return null;
						}
					}
				}		
				
				if(pageStaticParamsName != null) {
					if(!nname.contentEquals(pageStaticParamsName)) {
						requestParams = nname;
						mklogger.debug(TAG, " success to set parameter (1)");
					}
				}	
				else {
					requestParams = nname;
					mklogger.debug(TAG, " success to set parameter (2)");
				}
			} else {
				requestParams = isPageParamValid(pageStaticParams, name);
				mklogger.debug(TAG, " success to set parameter (3)");
			}
				
				
			if(requestParams != null) {
				mklogger.debug(TAG, " try to return (1)");
				if(pageStaticParams != null) {
					if(!requestParams.contentEquals(pageStaticParamsName)) {
						mklogger.debug(TAG, " success to return (1)");
						return requestParams;
					}
				}	
				else {
					mklogger.debug(TAG, " success to return (2)");
					return requestParams;
				}
			}
		}
		mklogger.debug(TAG, " param null here (2)");
		return null;
	}

	private boolean isPageStaticValue(ArrayList<String> pageStaticValues, String value) {
		for(String staticValue : pageStaticValues) 
			if(value.contentEquals(staticValue))
				return true;
		
		return false;
	}
	
	public ArrayList<String> getRequestParameterValues(HttpServletRequest request, String parameter, ArrayList<String> pageStaticValues, String pageStaticParamsName){
		ArrayList<String> requestValues = new ArrayList<String>();
		Enumeration params = request.getParameterNames();

		while(params.hasMoreElements()) {
			String name = params.nextElement().toString().trim();
			String[] nname = name.split("\\.");
			
			if(name.contains(".")) {
				if(pageStaticParamsName != null) {
					if(!nname[0].contentEquals(pageStaticParamsName) && nname[0].contentEquals(parameter))
					{
						requestValues.add(nname[1]);
						mklogger.debug(TAG,  " added(1) : " + nname[1]);
					}
						
				}else {
					if(nname[0].contentEquals(parameter)) {
						requestValues.add(nname[1]);
						mklogger.debug(TAG,  " added(2) : " + nname[1]);
					}
				}
			}else {
				// .이 포함되어 있지 않으면 Page Static Parameter에 포함되는지 확인한다.
				mklogger.debug(TAG, pageStaticValues);
				if(isPageStaticValue(pageStaticValues, name)) {
					requestValues.add(name);
					mklogger.debug(TAG, " added(3) : " + name);
				}else {
					mklogger.error(TAG, " Request value is not authorized! ("+name+")");
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

	public boolean comparePageValueWithRequest(LinkedHashMap<String, Boolean> pageValue, ArrayList<String> reqValue, ArrayList<String> staticValues, boolean isApi) {
		HashMap<String, Boolean> passValues = new HashMap<>();
		LinkedHashMap<String, Boolean> pv = null;
		if(pageValue != null)
			pv = new LinkedHashMap<>(pageValue);
		else
			pv = new LinkedHashMap<>();
		
		LinkedHashMap<String, Boolean> rv = new LinkedHashMap<>();
		
		if(staticValues != null) {
			for(String sp : staticValues) {
				passValues.put(sp, true);
			}
		}
		
		boolean passExists = passValues.size() != 0 ? true : false;
		
		for(int i = 0; i < reqValue.size(); i++) {
			/*
			if(passExists) {
				if(passValues.get(reqValue.get(i)) == null || !passValues.get(reqValue.get(i))) {
					rv.put(reqValue.get(i).trim(), true);
					mklogger.debug(TAG, "rv.put (1) : " + reqValue.get(i).trim());
				}
			}else {
				rv.put(reqValue.get(i).trim(), true);
				mklogger.debug(TAG, "rv.put (2) : " + reqValue.get(i).trim());
			}
			*/
			rv.put(reqValue.get(i).trim(), true);
		}
		
		boolean result = false;
		boolean apiResult = false;
		
		if(pv.size() > 0 && pv != null) {
			Set<String> entrySet = pv.keySet();
			Iterator iter = entrySet.iterator();
			
			
			while(iter.hasNext()) {
				String key = (String) iter.next();
				mklogger.debug(TAG, "pv key : " + key + " || rv.get(key): " + rv.get(key));
				result = rv.get(key) != null ? rv.get(key) : false;
				
				if(result)
					apiResult = true;
			}
		}else {
			return true;
		}
		
		
		
		return (!isApi ? result : apiResult);
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
}
