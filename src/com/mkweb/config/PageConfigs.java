package com.mkweb.config;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.mkweb.abst.PageXmlData;
import com.mkweb.logger.MkLogger;

public class PageConfigs extends PageXmlData{
	private static HashMap<String, ArrayList<PageXmlData>> page_configs = new HashMap<String, ArrayList<PageXmlData>>();
	private static File[] defaultFiles = null;
	
	private static PageConfigs pc = null;
	
	public static PageConfigs getInstance() {
		if(pc == null)
			pc = new PageConfigs();
		return pc;
	}
	
	public static void setPageConfigs(File[] pageConfigs) {
		page_configs.clear();
		defaultFiles = pageConfigs;
		ArrayList<PageXmlData> xmlData = null;
		for(File defaultFile : defaultFiles)
		{
			MkLogger.info("=*=*=*=*=*=*=* MkWeb Page Configs Start*=*=*=*=*=*=*=*=");
			MkLogger.info("=            " + defaultFile.getName() +"              =");
			if(defaultFile == null || !defaultFile.exists())
			{
				MkLogger.error("Config file is not exists or null");
				return;
			}
			NodeList nodeList = setNodeList(defaultFile);
			
			for(int i = 0; i < nodeList.getLength(); i++)
			{
				Node node = nodeList.item(i);
				
				if(node.getNodeName().equals("Control"))
				{
					if(node.getNodeType() == Node.ELEMENT_NODE) {
						String controlName = node.getAttributes().getNamedItem("name").getNodeValue();
						xmlData = new ArrayList<PageXmlData>();
						Element elem = (Element) node;
						NodeList services = elem.getElementsByTagName("Service");
						
						for(int j = 0; j < services.getLength(); j++) {
							Node service = services.item(j);
							NodeList service_param = service.getChildNodes();
							
							if(service.getNodeType() == Node.ELEMENT_NODE)
							{
								String[] SQL_INFO = new String[3];
								String PRM_NAME = null;
								String VAL_INFO = null;
								for(int k = 0; k < service_param.getLength(); k++) {
									Node service_info = service_param.item(k);
									
									if(service_info.getNodeType() == Node.ELEMENT_NODE)
									{
										NamedNodeMap attributes = service_info.getAttributes();
										
										switch(service_info.getNodeName()) {
										case "Sql":
											SQL_INFO[0] = attributes.getNamedItem("id").getNodeValue();
											SQL_INFO[1] = attributes.getNamedItem("obj").getNodeValue();
											SQL_INFO[2] = attributes.getNamedItem("rst").getNodeValue();
											break;
										case "Parameter":
											PRM_NAME = attributes.getNamedItem("name").getNodeValue();
											break;
										case "Value":
											VAL_INFO = service_info.getTextContent();
											VAL_INFO = VAL_INFO.trim();
											break;
										}
									}
								}
								
								String serviceName = service.getAttributes().getNamedItem("type").getNodeValue() + "." + SQL_INFO[0];
								
								String logicalDir = node.getAttributes().getNamedItem("dir_key").getNodeValue();
								String pageName = node.getAttributes().getNamedItem("page").getNodeValue();
								String pageDir = node.getAttributes().getNamedItem("dir").getNodeValue();
								String debug = node.getAttributes().getNamedItem("debug").getNodeValue();
								PageXmlData curData = new PageXmlData();
								curData.setControlName(controlName);
								curData.setServiceName(serviceName);
								curData.setLogicalDir(logicalDir);
								curData.setDir(pageDir);
								
								curData.setPageName(pageName);
								curData.setDebug(debug);
								
								curData.setSql(SQL_INFO);
								curData.setParameter(PRM_NAME);
								curData.setData(VAL_INFO);
								printPageInfo(curData);
								xmlData.add(curData);
							}
						}
						page_configs.put(controlName, xmlData);
					}
				}
			}
			
			MkLogger.info("=*=*=*=*=*=*=* MkWeb Page Configs  Done*=*=*=*=*=*=*=*=");
		}
	}
	
	public static void printPageInfo(PageXmlData xmlData) {
		
		String controlName = xmlData.getControlName();
		String serviceName = xmlData.getServiceName();
		String logicalDir = xmlData.getLogicalDir();
		
		String pageDir = xmlData.getDir();
		String pageName = xmlData.getPageName();
		String debugLevel = xmlData.getDebug();
		
		String[] SQL_INFO = xmlData.getSql();
		String PRM_NAME = xmlData.getParameter();
		String VAL_INFO = xmlData.getData();
		
		String valMsg = "";
		String[] valBuffer = VAL_INFO.split("\n");
		for (int ab = 0; ab < valBuffer.length; ab++) {
			String tempVal = valBuffer[ab].trim();
			if(valMsg == "")
				valMsg = tempVal;
			else
				valMsg += ("\n\t" + tempVal);
		}
		
		MkLogger.temp("\n忙式式式式式式式式式式式式式式式式式式Page  Control: " + controlName + "式式式式式式式式式式式式式式式式式式忖", false);
		MkLogger.temp("\n弛View Dir\t\t: " + pageDir + "\t\t\t 弛", false);
		MkLogger.temp("\n弛View Page\t\t: " + pageName + "\t\t\t 弛", false);
		MkLogger.temp("\n弛Logical Dir\t\t: " + logicalDir + "\t\t\t 弛", false);
		MkLogger.temp("\n弛Debug Level\t\t: " + debugLevel + "\t\t\t\t 弛", false);
		MkLogger.temp("\n弛Service Name\t\t: " + serviceName + "\t\t 弛", false);
		MkLogger.temp("\n弛SQL\t\t\t: " + SQL_INFO[0] + " . " + SQL_INFO[1] + " . " + SQL_INFO[2] + "\t 弛", false);
		MkLogger.temp("\n弛Parameter\t\t: " + PRM_NAME + "\t\t\t\t 弛", false);
		MkLogger.temp("\n弛VALUE\t\t\t:  \n弛\t" + valMsg + "\t\t\t 弛", false);
		MkLogger.temp("\n戌式式式式式式式式式式式式式式式式Page Control Setting End式式式式式式式式式式式式式式式式戎", false);
		MkLogger.flush(1);
	}
	
	public static ArrayList<PageXmlData> getControl(String k) {
		if(k == null) {
			MkLogger.error("[Page] : Input String data is null");
			return null;
		}
		
		if(page_configs.get(k) == null)
		{
			MkLogger.error("[Page] : The control is unknown.");
			return null;
		}
		return page_configs.get(k);
	}
}
