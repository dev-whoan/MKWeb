package com.mkweb.web;

import java.util.ArrayList;

import com.mkweb.config.MkPageConfigs;
import com.mkweb.data.PageXmlData;
import com.mkweb.logger.MkLogger;

public class PageInfo {
	private String pageControlName = null;
	private String pageParamsName = null;
	private ArrayList<String> pageParams = null;
	private ArrayList<String> pageServiceName = null;
	private ArrayList<String> pageParameter = null;
	private ArrayList<String[]> pageSqlInfo = null;
	private ArrayList<String> pageValue = null;
	
	private String TAG = "[PageInfo]";
	private MkLogger mklogger = MkLogger.Me();
	private boolean set = false;
	private boolean isApi = false;
	
	public PageInfo(String requestPageName, boolean isApi) {
		this.pageControlName = requestPageName;
		this.isApi = isApi;
		set = false;
		setPageInfo();
	}
	
	private void setPageInfo() {
		ArrayList<PageXmlData> pxd = MkPageConfigs.Me().getControl(this.pageControlName);
		pageServiceName = new ArrayList<String>();
		pageParameter = new ArrayList<String>();
		pageSqlInfo = new ArrayList<String[]>();
		pageValue = new ArrayList<String>();
		if(pxd == null)	{
			mklogger.error(TAG + " pageControlName is invalid : " + pageControlName);
			return;
		}
		pageParams = pxd.get(0).getPageStaticParams();
		pageParamsName = pxd.get(0).getPageStaticParamsName();
		for(int i = 0; i < pxd.size(); i++) {
			PageXmlData xmlData = pxd.get(i);
			pageServiceName.add(i, xmlData.getServiceName());
			pageParameter.add(i, xmlData.getParameter());
			pageSqlInfo.add(i, xmlData.getSql());
			pageValue.add(i, xmlData.getData());
		}
		set = true;
	}
	public boolean isSet()	{	return this.set;	}
	public boolean isApiPage() {	return this.isApi;	}
	
	public String getPageControlName(){	return this.pageControlName;	}
	public String getPageStaticParamsName() {	return this.pageParamsName;	}
	public ArrayList<String> getPageStaticParams(){	return this.pageParams;	}
	public ArrayList<String> getPageServiceName() {	return this.pageServiceName;	}
	public ArrayList<String> getPageParameter() {	return this.pageParameter;	}
	public ArrayList<String[]> getPageSqlInfo()	{	return this.pageSqlInfo;	}
	public ArrayList<String> getPageValue()	{	return this.pageValue;	}
}
