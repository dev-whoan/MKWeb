package com.mkweb.data;

import java.util.HashMap;

import com.mkweb.logger.MkLogger;

public class PageXmlData extends AbsXmlData {
	private String logicalDir = null;
	private String pageDir = null;
	private String pageName = null;			//페이지 네임
	private String debug = null;
	private String parameter = null;
	private String[] sql = null;
	private boolean authorizedRequire = false;		//이거 클래스 필요한거임; 지우지마셈
	private boolean post = false;
	private boolean get = false;
	private boolean put = false;
	private boolean delete = false;
	private boolean options = false;
	private boolean head = false;
	private String structure = null;
	
	public void setPageName(String pageName) {	this.pageName = pageName;	}
	public void setDebug(String debug) {	this.debug = debug;	}
	public void setParameter(String param) {	this.parameter = param;	}
	public void setSql(String[] sql) {	this.sql = sql;	}
	public void setDir(String dir) {	this.pageDir = dir;	}
	public void setLogicalDir(String dir) {	this.logicalDir = dir;	}
	public void setAuthorizedRequire(String ar) {	this.authorizedRequire = (ar == null || ar.equals("no") ? false : ( ar.equals("yes") ? true : false) );	}
	public void setPost(String post) {	this.post = (post == null || post.equals("no") ? false : ( post.equals("yes") ? true : false) );	}
	public void setGet(String get) {	this.get = (get == null || get.equals("no") ? false : ( get.equals("yes") ? true : false) );	}
	public void setPut(String put) {	this.put = (put == null || put.equals("no") ? false : ( put.equals("yes") ? true : false) );	}
	public void setDelete(String delete) {	this.delete = (delete == null || delete.equals("no") ? false : ( delete.equals("yes") ? true : false) );	}
	public void setOptions(String options) {	this.options = (options == null || options.equals("no") ? false : ( options.equals("yes") ? true : false) );	}
	public void setHead(String head) {	this.head = (head == null || head.equals("no") ? false : ( head.equals("yes") ? true : false) );	}
	public void setStructure(String str) {	this.structure = str;	}

	public String getPageName() {	return this.pageName;	}
	public String getDebug() {	return this.debug;	}
	public String getParameter() {	return this.parameter;	}
	public String[] getSql() {	return this.sql;	}
	public String getDir() {	return this.pageDir;	}
	public String getLogicalDir() {	return this.logicalDir;	}
	public boolean getAuthorizedRequire() {	return this.authorizedRequire;	}
	public boolean getPost() {	return this.post;	}
	public boolean getGet() {	return this.get;	}
	public boolean getPut() {	return this.put;	}
	public boolean getDelete() {	return this.delete;	}
	public boolean getOptions() {	return this.options;	}
	public boolean getHead() {	return this.head;	}
	public boolean isMethodAllowed(String method) {
		HashMap<String, Boolean> map = new HashMap<>();
		map.put("post", getPost());
		map.put("get", getGet());
		map.put("put", getPut());
		map.put("delete", getDelete());
		map.put("options", getOptions());
		map.put("head", getHead());
		
		return map.get(method);
	}
	public String getStructure() {	return this.structure;	}
	
	public String getMyInfo() {	return "Control: " + (this.controlName) + " | Service: " + (this.serviceName) + " | Tag: " + (getTag());	}
}