package com.mkweb.web;

import java.util.ArrayList;

import com.mkweb.config.PageConfigs;
import com.mkweb.data.PageXmlData;
import com.mkweb.logger.MkLogger;

public class PageInfo {
	private String pageControlName = null;
	private String pageServiceName = null;
	private String pageParameter = null;
	private String[] pageSqlInfo = null;
	private String pageValue = null;
	
	private String TAG = "[PageInfo]";
	private MkLogger mklogger = MkLogger.Me();
	private boolean set = false;
	
	public PageInfo(String requestPageName) {
		this.pageControlName = requestPageName;
		set = false;
		setPageInfo();
	}
	
	private void setPageInfo() {
		ArrayList<PageXmlData> pxd = PageConfigs.Me().getControl(this.pageControlName);
		
		if(pxd == null)	{
			mklogger.error(TAG + " pageControlName is invalid : " + pageControlName);
			return;
		}
		PageXmlData xmlData = pxd.get(0);
		
		pageServiceName = xmlData.getServiceName();
		pageParameter = xmlData.getParameter();
		pageSqlInfo = xmlData.getSql();
		pageValue = xmlData.getData();
		set = true;
	}
	public boolean isSet()	{	return this.set;	}
	public String getPageControlName(){	return this.pageControlName;	}
	public String getPageServiceName() {	return this.pageServiceName;	}
	public String getPageParameter() {	return this.pageParameter;	}
	public String[] getPageSqlInfo()	{	return this.pageSqlInfo;	}
	public String getPageValue()	{	return this.pageValue;	}
}
