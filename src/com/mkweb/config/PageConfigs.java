package com.mkweb.config;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.mkweb.data.PageXmlData;
import com.mkweb.logger.MkLogger;

public class PageConfigs extends PageXmlData{
	private HashMap<String, ArrayList<PageXmlData>> page_configs = new HashMap<String, ArrayList<PageXmlData>>();
	private File[] defaultFiles = null;
	
	private static PageConfigs pc = null;
	private long lastModified[]; 
	private MkLogger mklogger = MkLogger.Me();
	
	public static PageConfigs Me() {
		if(pc == null)
			pc = new PageConfigs();
		return pc;
	}
	
	public void setPageConfigs(File[] pageConfigs) {
		page_configs.clear();
		defaultFiles = pageConfigs;
		ArrayList<PageXmlData> xmlData = null;
		lastModified = new long[pageConfigs.length];
		int lmi = 0;
		for(File defaultFile : defaultFiles)
		{
			lastModified[lmi++] = defaultFile.lastModified();
			mklogger.info("=*=*=*=*=*=*=* MkWeb Page Configs Start*=*=*=*=*=*=*=*=");
			mklogger.info("=            " + defaultFile.getName() +"              =");
			if(defaultFile == null || !defaultFile.exists())
			{
				mklogger.error("Config file is not exists or null");
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
						String logicalDir = node.getAttributes().getNamedItem("dir_key").getNodeValue();
						String pageName = node.getAttributes().getNamedItem("page").getNodeValue();
						String pageDir = node.getAttributes().getNamedItem("dir").getNodeValue();
						String debug = node.getAttributes().getNamedItem("debug").getNodeValue();
						
						xmlData = new ArrayList<PageXmlData>();
						Element elem = (Element) node;
						NodeList services = elem.getElementsByTagName("Service");
						mklogger.debug(services.item(0));
						if(services.item(0) != null) {
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
									printPageInfo(curData, "info");
									xmlData.add(curData);
								}
							}
						}else {
							PageXmlData curData = new PageXmlData();
							curData.setControlName(controlName);
							curData.setServiceName("No Service");
							curData.setLogicalDir(logicalDir);
							curData.setDir(pageDir);
							
							curData.setPageName(pageName);
							curData.setDebug(debug);
							String[] sqlInfo = {"no", "-sql-", "data"};
							curData.setSql(sqlInfo);
							curData.setParameter("No Parameter");
							curData.setData("No Value");
							printPageInfo(curData, "info");
							xmlData.add(curData);
						}
						page_configs.put(controlName, xmlData);
					}
				}
			}
			
			mklogger.info("=*=*=*=*=*=*=* MkWeb Page Configs  Done*=*=*=*=*=*=*=*=");
		}
	}
	
	public void printPageInfo(PageXmlData xmlData, String type) {
		
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
		String tempMsg = "\n������������������������������������������������������Page Control  :  " + controlName + "��������������������������������������������������������"
				  + "\n��View Dir:\t" + pageDir + "\t\tView Page:\t" + pageName + "\n��Logical Dir:\t" + logicalDir
				 + "\t\tDebug Level:\t" + debugLevel
				 + "\n��Service Name:\t" + serviceName + "\tParameter:\t" + PRM_NAME;
		
		if(type == "info") {
			tempMsg += "\n��SQL:\t" + SQL_INFO[0] + "\t" + SQL_INFO[1]+ "\t" + SQL_INFO[2]
					 + "\n��Value:\t" + valMsg
					 + "\n��������������������������������������������������������������������������������������������������������������������������������������������������������";
		}else {
			tempMsg += "\n��������������������������������������������������������������������������������������������������������������������������������������������������������";
		}
		mklogger.temp(tempMsg, false);
		mklogger.flush("info");
	}
	
	public ArrayList<PageXmlData> getControl(String k) {
		for(int i = 0; i < defaultFiles.length; i++)
		{
			if(lastModified[i] != defaultFiles[i].lastModified()){
				setPageConfigs(defaultFiles);
				mklogger.info("==============Reload Page Config files==============");
				mklogger.info("========Caused by  : different modified time========");
				mklogger.info("==============Reload Page Config files==============");
				break;
			}
		}
		
		if(k == null) {
			mklogger.error("[Page] : Input String data is null");
			return null;
		}
		
		if(page_configs.get(k) == null)
		{
			mklogger.error("[Page] : The control is unknown. [called control name: " + k + "]");
			return null;
		}
		return page_configs.get(k);
	}
}