package com.mkweb.can;

import java.io.File;
import java.util.ArrayList;
import com.mkweb.data.PageXmlData;

public abstract class MkPageConfigCan extends PageXmlData {
	protected String[] sl_list = null;
	protected String[] cl_list = null;
	protected String[] cl_info = null;
	
	protected void setSlList(String[] sl_list) {
		this.sl_list = sl_list;
	}
	protected void setClList(String[] cl_list) {
		this.cl_list = cl_list;
	}
	protected void setClInfo(String[] cl_info) {
		this.cl_info = cl_info;
	}
	
	protected String[] getSlList() {
		return this.sl_list;
	}
	protected String[] getClList() {
		return this.cl_list;
	}
	protected String[] getClInfo() {
		return this.cl_info;
	}
	
	public abstract ArrayList<PageXmlData> getControl(String k);
	public abstract void setPageConfigs(File[] pageConfigs);
	public abstract void printPageInfo(PageXmlData xmlData, String type);
	protected abstract PageXmlData setPageXmlData(String pageParamName, ArrayList<String> pageParam, String serviceName, String[] cl_info, String[] sqlInfo, String PRM_NAME, String VAL_INFO, String STRUCTURE);
	
}
