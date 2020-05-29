package com.mkweb.security;

import java.util.ArrayList;
import java.util.Arrays;

import com.mkweb.logger.MkLogger;

public class CheckPageInfo {
	private String TAG = "[CheckPageInfo]";
	private MkLogger mklogger = MkLogger.Me();
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
				pv += pvSetList[i].split("\\.")[1];
			}
		}catch(java.lang.NullPointerException e) {
			mklogger.error(TAG + " Wrong split tried on Page value. Please check pageValue. You need to follow MKWeb parameter rule." + e.getMessage());
			return false;
		}catch(java.lang.ArrayIndexOutOfBoundsException e1) {
			mklogger.error(TAG + " Wrong index on Page value. " + e1.getMessage());
			return false;
		}
		
		String rv = "";
		try {
			for(int i = 0; i < reqValue.size(); i++) {
				rv += reqValue.get(i);
			}
		}catch(java.lang.NullPointerException e) {
			mklogger.error(TAG + " Wrong split tried on Request value. Please check what user requested. You need to follow MKWeb parameter rule." + e.getMessage());
			mklogger.error(TAG + " User Request: " + rv);
			return false;
		}catch(java.lang.ArrayIndexOutOfBoundsException e1) {
			mklogger.error(TAG + " Wrong index on Request value. " + e1.getMessage());
			return false;
		}
		char[] pvArray = pv.toCharArray();
		char[] rvArray = rv.toCharArray();
		Arrays.sort(pvArray);
		Arrays.sort(rvArray);
		
		return (new String(pvArray).trim().equals(new String(rvArray).trim()));
	}
	
}
