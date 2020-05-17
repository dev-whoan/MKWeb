package com.mkweb.config;

import java.io.File;
import java.util.HashMap;

import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.mkweb.abst.AbsXmlData;
import com.mkweb.logger.MkLogger;

public class PageConfigs extends AbsXmlData{
	private static HashMap<String, AbsXmlData> page_configs = new HashMap<String, AbsXmlData>();
	private static File defaultFile = null;
	
	private static PageConfigs pc = null;
	
	String[] ni_list = {
		"dir",
		"name",
		"debug",
		"id",
		"obj",
		"rst"
	};
	
	public static PageConfigs getInstance() {
		if(pc == null)
			pc = new PageConfigs();
		return pc;
	}
	
	public static void setSqlConfigs(File pageConfigs) {
		page_configs.clear();
		defaultFile = pageConfigs;
		
		MkLogger.info("=*=*=*=*=*=*=* MkWeb Page Configs Start*=*=*=*=*=*=*=*=");
		MkLogger.info("=            " + pageConfigs.getName() +"              =");
		if(defaultFile == null || !defaultFile.exists())
		{
			MkLogger.error("Config file is not exists or null");
			return;
		}
		
		NodeList nodeList = setNodeList(defaultFile);
		
		for(int i = 0; i < nodeList.getLength(); i++)
		{
			Node node = nodeList.item(i);
			if(node.getNodeType() == Node.ELEMENT_NODE) {
				Element elem = (Element) node;
				
				int controlCount = elem.getElementsByTagName("Control").getLength();
				
				for(int j = 0; j < controlCount; j++) {
					
				}
				MkLogger.info(elem);
				
			}
		}
		
		MkLogger.info("=*=*=*=*=*=*=* MkWeb Page Configs  Done*=*=*=*=*=*=*=*=");
	}
	
	public static AbsXmlData getControlService(AbsXmlData xmlData) {
		if(xmlData == null) {
			MkLogger.error("[Page] : Input xml data is null");
			return null;
		}
		
		String key = xmlData.getServiceName();
		return page_configs.get(key);
	}
}
