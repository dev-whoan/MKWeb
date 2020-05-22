package com.mkweb.data;

import java.io.File;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.mkweb.impl.XmlData;

public class PageXmlData implements XmlData {
	
	private String serviceName = null;
	private String logicalDir = null;
	private String controlName = null;
	private String pageDir = null;
	private String pageName = null;			//페이지 네임
	private String debug = null;
	private String value = null;
	private String parameter = null;
	private String[] sql = null;
	private String Tag = null;
	private static String absPath = "/WEB-INF";
	
	public String getTag() { return this.Tag; }

	public static NodeList setNodeList(File defaultFile) {
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = null;
		Document doc = null;
		
		try {
			dBuilder = dbFactory.newDocumentBuilder();
			doc = dBuilder.parse(defaultFile);
		}catch(Exception e) {
	//		mkLog.error(e);
			e.getStackTrace();
		}
		
		Node root = doc.getFirstChild();
		NodeList nodeList = root.getChildNodes();
		
		return nodeList;
	}
	public void setControlName(String controlName)	{	this.controlName = controlName;	}
	public void setPageName(String pageName) {	this.pageName = pageName;	}
	public void setDebug(String debug) {	this.debug = debug;	}
	public void setServiceName(String serviceName) {	this.serviceName = serviceName;	}
	public void setData(String data) {	this.value = data;	}
	public void setParameter(String data) {	this.parameter = data;	}
	public void setSql(String[] data) {	this.sql = data;	}
	public void setDir(String dir) {	this.pageDir = dir;	}
	public void setLogicalDir(String dir) {	this.logicalDir = dir;	}

	public String getServiceName() {	return this.serviceName;	}
	public String getControlName() {	return this.controlName;	}
	public String getPageName() {	return this.pageName;	}
	public String getDebug() {	return this.debug;	}
	public String getData() {	return this.value;	}
	public String getParameter() {	return this.parameter;	}
	public String[] getSql() {	return this.sql;	}
	public String getDir() {	return this.pageDir;	}
	public String getLogicalDir() {	return this.logicalDir;	}
	public static String getAbsPath()	{	return absPath;	}
	public String getMyInfo() {	return "Control: " + (this.controlName) + " | Service: " + (this.serviceName) + " | Tag: " + (getTag());	}
}
