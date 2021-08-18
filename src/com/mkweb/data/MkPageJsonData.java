package com.mkweb.data;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class MkPageJsonData extends AbsJsonData {
	/*
	 * 0: Default device
	 * 1~: additional device settings
	 */
	private ArrayList<MkDeviceData> device = null;

	private String debug = null;
	/* Page Parameters */
	private String parameter = null;
	private LinkedHashMap<String, Boolean> pageValue = null;
	/* Page Parameters */
	/* Page Static Parameters */
	private boolean isPageStatic = false;
	/* Page Static Parameters */
	private String objectType = null;
	private String method = null;
	private int authorizedRequire = 0;
	private boolean post = false;
	private boolean get = false;
	private boolean put = false;
	private boolean delete = false;
	private boolean options = false;
	private boolean head = false;
	private boolean isApi = false;
	private String pageLastURI = null;
	private String serviceURI = null;
	
	public void setDebug(String debug) {	this.debug = debug;	}
	public void setParameter(String param) {	this.parameter = param;	}
	public void setObjectType(String objectType) {	this.objectType = objectType;	}
	public void setMethod(String method) {	this.method = method;	}
	public void setPost(String post) {	this.post = (post == null || post.equals("no") ? false : ( post.equals("yes") ? true : false) );	}
	public void setGet(String get) {	this.get = (get == null || get.equals("no") ? false : ( get.equals("yes") ? true : false) );	}
	public void setPut(String put) {	this.put = (put == null || put.equals("no") ? false : ( put.equals("yes") ? true : false) );	}
	public void setDelete(String delete) {	this.delete = (delete == null || delete.equals("no") ? false : ( delete.equals("yes") ? true : false) );	}
	public void setOptions(String options) {	this.options = (options == null || options.equals("no") ? false : ( options.equals("yes") ? true : false) );	}
	public void setHead(String head) {	this.head = (head == null || head.equals("no") ? false : ( head.equals("yes") ? true : false) );	}
	public void setPageValue(LinkedHashMap<String, Boolean> pageValue) { this.pageValue = pageValue;	}
	public void setPageStatic(boolean isPageStatic) {	this.isPageStatic = isPageStatic;	}
	public void setAPI(boolean ia) {	this.isApi = ia;	}
	public void setLastURI(String pageLastURI) {	this.pageLastURI = pageLastURI;	}
	public void setServiceURI(String serviceURI) {	this.serviceURI = serviceURI;	}
	
	public void setDevice(int index, MkDeviceData device) {	if(this.device == null) {	this.device = new ArrayList<>();	}	this.device.set(index, device);	}
	public void setDevice(ArrayList<MkDeviceData> device) {	this.device = device;	}

	public String getDebug() {	return this.debug;	}
	public String getParameter() {	return this.parameter;	}
	public String getObjectType() {	return this.objectType;	}
	public String getMethod() {	return this.method;	}
	public boolean getPost() {	return this.post;	}
	public boolean getGet() {	return this.get;	}
	public boolean getPut() {	return this.put;	}
	public boolean getDelete() {	return this.delete;	}
	public boolean getOptions() {	return this.options;	}
	public boolean getHead() {	return this.head;	}

	public LinkedHashMap<String, Boolean> getPageValue(){	return this.pageValue;	}
	public boolean IsApiPage() {	return this.isApi;	}
	
	public String getLastURI() {	return this.pageLastURI;	}
	public String getServiceURI() {	return this.serviceURI;		}
	
	public ArrayList<MkDeviceData> getAllDevices(){	return this.device;	}
	public MkDeviceData getDevice(int index) {	return this.device.get(index);	}
	
	public boolean getPageStatic() {	return this.isPageStatic;	}
}