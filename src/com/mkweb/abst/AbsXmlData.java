package com.mkweb.abst;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.mkweb.impl.XmlData;

public class AbsXmlData implements XmlData {
	
	protected String serviceName = null;
	protected String controlName = null;
	protected String data = null;
	protected Node node = null;
	protected String Tag = null;
	protected NodeList nodeList = null;
	
	public String getTag() { return this.Tag; }
	
	public void setNode(Node node) { this.node = node; }
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

	public void setServiceName(String serviceName) { this.serviceName = serviceName; }
	public String getServiceName() {	return this.serviceName;	}
	
	public void setControlName(String controlName)	{	this.controlName = controlName;	}
	public String getControlName() {	return this.controlName;	}
	
	public void setData(String data) {	this.data = data;	}
	public String getData() {	return this.data;	}
	
	public String Me() {	return "Control: " + (this.controlName) + " | Service: " + (this.serviceName) + " | Tag: " + (getTag());	}   
}
