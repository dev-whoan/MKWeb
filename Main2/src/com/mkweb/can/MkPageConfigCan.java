package com.mkweb.can;

import java.io.File;
import java.util.ArrayList;
import com.mkweb.data.PageXmlData;

public abstract class MkPageConfigCan extends PageXmlData {
	protected String[] svc_list = null;
	protected String[] ctr_list = null;
	protected String[] ctr_info = null;
	
	protected void setSlList(String[] sl_list) {
		this.svc_list = sl_list;
	}
	protected void setClList(String[] cl_list) {
		this.ctr_list = cl_list;
	}
	protected void setClInfo(String[] cl_info) {
		this.ctr_info = cl_info;
	}
	
	protected String[] getSlList() {
		return this.svc_list;
	}
	protected String[] getClList() {
		return this.ctr_list;
	}
	protected String[] getClInfo() {
		return this.ctr_info;
	}
	
	public abstract ArrayList<PageXmlData> getControl(String k);
	public abstract void setPageConfigs(File[] pageConfigs);
	public abstract void printPageInfo(PageXmlData xmlData, String type);
	protected abstract PageXmlData setPageXmlData(String pageParamName, ArrayList<String> pageParam, String serviceName, String[] ctr_info, String[] sqlInfo, String PRM_NAME, String VAL_INFO, String STRUCTURE);
	
}
