package com.mkweb.config;

import java.io.File;

import java.util.ArrayList;
import java.util.HashMap;

import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.mkweb.can.MkPageConfigCan;
import com.mkweb.data.PageXmlData;
import com.mkweb.logger.MkLogger;

public class MkPageConfigs extends MkPageConfigCan{
	private HashMap<String, ArrayList<PageXmlData>> page_configs = new HashMap<String, ArrayList<PageXmlData>>();
	private File[] defaultFiles = null;

	private static MkPageConfigs pc = null;
	private long lastModified[]; 
	private MkLogger mklogger = MkLogger.Me();

	private String TAG = "[PageConfigs]";

	public static MkPageConfigs Me() {
		if(pc == null)
			pc = new MkPageConfigs();
		return pc;
	}
	private String[] cl_list = {
			"name",
			"debug",
			"dir",
			"dir_key",
			"page"
	};
	private String[] cl_info = new String[cl_list.length];
	private String[] sl_list = {
			"id",
			"obj",
			"rst",
			"method"	
	};
	@Override
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
			mklogger.info(TAG + "File: " + defaultFile.getAbsolutePath());
			mklogger.info("=            " + defaultFile.getName() +"              =");
			if(defaultFile == null || !defaultFile.exists())
			{
				mklogger.error("Config file is not exists or null");
				return;
			}
			NodeList nodeList = setNodeList(defaultFile);

			if(nodeList != null) {
				for(int i = 0; i < nodeList.getLength(); i++)
				{
					Node node = nodeList.item(i);

					if(node.getNodeName().equals("Control"))
					{
						if(node.getNodeType() == Node.ELEMENT_NODE) {
							for(int cl = 0; cl < cl_list.length; cl++) {
								if(node.getAttributes().getNamedItem(cl_list[cl]) != null)
								{
									cl_info[cl] = node.getAttributes().getNamedItem(cl_list[cl]).getNodeValue();
								}
							}
							/*
							String controlName = node.getAttributes().getNamedItem("name").getNodeValue();
							String logicalDir = node.getAttributes().getNamedItem("dir_key").getNodeValue();
							String pageName = node.getAttributes().getNamedItem("page").getNodeValue();
							String pageDir = node.getAttributes().getNamedItem("dir").getNodeValue();
							String debug = node.getAttributes().getNamedItem("debug").getNodeValue();
							 */
							xmlData = new ArrayList<PageXmlData>();
							Element elem = (Element) node;
							NodeList services = elem.getElementsByTagName("Service");
							
							if(services.item(0) != null) {
								for(int j = 0; j < services.getLength(); j++) {
									Node service = services.item(j);
									NodeList service_param = service.getChildNodes();

									if(service.getNodeType() == Node.ELEMENT_NODE)
									{
										String[] SQL_INFO = new String[sl_list.length];
										String PRM_NAME = null;
										String VAL_INFO = null;
										for(int k = 0; k < service_param.getLength(); k++) {
											Node service_info = service_param.item(k);

											if(service_info.getNodeType() == Node.ELEMENT_NODE)
											{
												NamedNodeMap attributes = service_info.getAttributes();

												switch(service_info.getNodeName()) {
												case "Sql":
													for(int sli = 0; sli < sl_list.length; sli++) {
														Node tN = attributes.getNamedItem(sl_list[sli]);
														SQL_INFO[sli] = (tN != null ? tN.getNodeValue() : null);
													}
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
										PageXmlData curData = setPageXmlData(serviceName, cl_info, SQL_INFO, PRM_NAME, VAL_INFO, null);
										printPageInfo(curData, "info");
										xmlData.add(curData);
										page_configs.put(cl_info[0], xmlData);
									}
								}
							}else {
								String[] temp_sql = new String[sl_list.length];
								for(String s : temp_sql) {	s = "no data";	}
								
								PageXmlData curData = setPageXmlData("No Service", cl_info, temp_sql, "No Parameter", "No Value", null);
								printPageInfo(curData, "info");
								xmlData.add(curData);
								page_configs.put(cl_info[0], xmlData);
							}
							
						}
					}
				}
			}else {
				mklogger.info(TAG + " No Page Control has found. If you set Page configs, please check Page config files and web.xml.");
			}


			mklogger.info("=*=*=*=*=*=*=* MkWeb Page Configs  Done*=*=*=*=*=*=*=*=");
		}
	}

	@Override
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
			mklogger.error(TAG + " : Input String data is null");
			return null;
		}

		if(page_configs.get(k) == null)
		{
			mklogger.error(TAG + " : The control is unknown. [called control name: " + k + "]");
			return null;
		}
		return page_configs.get(k);
	}
	
	@Override
	protected PageXmlData setPageXmlData(String serviceName, String[] cl_info, String[] sqlInfo, String PRM_NAME, String VAL_INFO, String STRUCTURE) {
		PageXmlData result = new PageXmlData();
		result.setControlName(cl_info[0]);
		result.setServiceName(serviceName);
		result.setLogicalDir(cl_info[3]);
		result.setDir(cl_info[2]);
		
		result.setPageName(cl_info[4]);
		result.setDebug(cl_info[1]);

		result.setSql(sqlInfo);
		result.setParameter(PRM_NAME);
		result.setData(VAL_INFO);
		return result;
	}

	@Override
	public void printPageInfo(PageXmlData xmlData, String type) {
		String controlName = xmlData.getControlName();
		String serviceName = xmlData.getServiceName();
		String logicalDir = xmlData.getLogicalDir();

		String pageDir = xmlData.getDir();
		String pageName = xmlData.getPageName();
		String debugLevel = xmlData.getDebug();

		String[] SQL_INFO = xmlData.getSql();
		String sql_info = "SQL:\t";
		for(int i = 0; i < SQL_INFO.length; i++) {
			if(i != SQL_INFO.length-1)
				sql_info += SQL_INFO[i] + "\t";
			else
				sql_info += SQL_INFO[i];
		}
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
		String tempMsg = "\n忙式式式式式式式式式式式式式式式式式式式式式式式式式式Page Control  :  " + controlName + "式式式式式式式式式式式式式式式式式式式式式式式式式式式式"
				+ "\n弛View Dir:\t" + pageDir + "\t\tView Page:\t" + pageName + "\n弛Logical Dir:\t" + logicalDir
				+ "\t\tDebug Level:\t" + debugLevel
				+ "\n弛Service Name:\t" + serviceName + "\tParameter:\t" + PRM_NAME;

		if(type == "info") {
			tempMsg += "\n弛SQL:\t" + sql_info
					+ "\n弛Value:\t" + valMsg
					+ "\n戌式式式式式式式式式式式式式式式式式式式式式式式式式式式式式式式式式式式式式式式式式式式式式式式式式式式式式式式式式式式式式式式式式式式式式式式式式式式";
		}else {
			tempMsg += "\n戌式式式式式式式式式式式式式式式式式式式式式式式式式式式式式式式式式式式式式式式式式式式式式式式式式式式式式式式式式式式式式式式式式式式式式式式式式式式";
		}
		mklogger.temp(tempMsg, false);
		mklogger.flush("info");
	}
}
