package com.mkweb.data;

import java.io.File;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.mkweb.impl.XmlData;

public class PageXmlData extends AbsXmlData {
	
	private String logicalDir = null;
	private String pageDir = null;
	private String pageName = null;			//페이지 네임
	private String debug = null;
	private String parameter = null;
	private String[] sql = null;
	
	public void setPageName(String pageName) {	this.pageName = pageName;	}
	public void setDebug(String debug) {	this.debug = debug;	}
	public void setParameter(String param) {	this.parameter = param;	}
	public void setSql(String[] sql) {	this.sql = sql;	}
	public void setDir(String dir) {	this.pageDir = dir;	}
	public void setLogicalDir(String dir) {	this.logicalDir = dir;	}

	public String getPageName() {	return this.pageName;	}
	public String getDebug() {	return this.debug;	}
	public String getParameter() {	return this.parameter;	}
	public String[] getSql() {	return this.sql;	}
	public String getDir() {	return this.pageDir;	}
	public String getLogicalDir() {	return this.logicalDir;	}
	
	public String getMyInfo() {	return "Control: " + (this.controlName) + " | Service: " + (this.serviceName) + " | Tag: " + (getTag());	}
}