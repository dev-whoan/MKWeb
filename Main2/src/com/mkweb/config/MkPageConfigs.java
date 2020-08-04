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
	private String[] ctr_list = {
			"name",
			"debug",
			"dir",
			"dir_key",
			"page"
	};
	private String[] ctr_info = new String[ctr_list.length];
	private String[] svc_list = {
			"id",
			"obj",
			"rst",
			"method"	
	};
	private ArrayList<String> setPageParamToStrig(String pageParam) {
		if(pageParam == null)
			return null;
		String[] tempPageParam = pageParam.split("@set" + "\\(");
		String[] tempPageParam2 = new String[tempPageParam.length];
		if(tempPageParam.length == 1) 
			return null;
		
		for(int i = 0; i < tempPageParam.length; i++) {
			tempPageParam2[i] = tempPageParam[i].split("=")[0];
		}
		
		if(tempPageParam2.length == 1)
			return null;
		
		ArrayList<String> result = new ArrayList<String>();
		
		for(int i = 1; i < tempPageParam2.length; i++) {
			result.add(tempPageParam2[i].trim());
		}
		
		return result;
	}
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
							for(int cl = 0; cl < ctr_list.length; cl++) {
								if(node.getAttributes().getNamedItem(ctr_list[cl]) != null)
								{
									ctr_info[cl] = node.getAttributes().getNamedItem(ctr_list[cl]).getNodeValue();
								}
							}
							xmlData = new ArrayList<PageXmlData>();
							Element elem = (Element) node;
							NodeList services = elem.getElementsByTagName("Service");
							
							//새로 추가하는 부분
							NodeList pageParamNodes = elem.getElementsByTagName("PageParams");
							String pageParamsName = null;
							ArrayList<String> pageParam = null;
							if(pageParamNodes.item(0) != null) {
								Node pageParamNode = pageParamNodes.item(0);
								pageParamsName = pageParamNode.getAttributes().getNamedItem("name") != null ?
										pageParamNode.getAttributes().getNamedItem("name").getNodeValue():
											null;
								pageParam = setPageParamToStrig(pageParamNode.getTextContent());
							}else {
								pageParam = null;
							}
							
							if(services.item(0) != null) {
								for(int j = 0; j < services.getLength(); j++) {
									Node service = services.item(j);
									NodeList service_param = service.getChildNodes();

									if(service.getNodeType() == Node.ELEMENT_NODE)
									{
										String[] SQL_INFO = new String[svc_list.length];
										String PRM_NAME = null;
										String VAL_INFO = null;
										for(int k = 0; k < service_param.getLength(); k++) {
											Node service_info = service_param.item(k);

											if(service_info.getNodeType() == Node.ELEMENT_NODE)
											{
												NamedNodeMap attributes = service_info.getAttributes();

												switch(service_info.getNodeName()) {
												case "Sql":
													for(int sli = 0; sli < svc_list.length; sli++) {
														Node tN = attributes.getNamedItem(svc_list[sli]);
														SQL_INFO[sli] = (tN != null ? tN.getNodeValue() : null);
													}
													break;
												case "Parameter":
													PRM_NAME = attributes.getNamedItem("name") != null ?
															   attributes.getNamedItem("name").getNodeValue() :
															   null;
													break;
												case "Value":
													VAL_INFO = service_info.getTextContent();
													VAL_INFO = VAL_INFO.trim();
													break;
												}
											}
										}

										String serviceName = service.getAttributes().getNamedItem("type").getNodeValue() + "." + SQL_INFO[0];
										PageXmlData curData = setPageXmlData(pageParamsName, pageParam, serviceName, ctr_info, SQL_INFO, PRM_NAME, VAL_INFO, null);
										printPageInfo(curData, "info");
										xmlData.add(curData);
										page_configs.put(ctr_info[0], xmlData);
									}
								}
							}else {
								String[] temp_sql = new String[svc_list.length];
								for(String s : temp_sql) {	s = "no data";	}
								
								PageXmlData curData = setPageXmlData(pageParamsName, pageParam, "No Service", ctr_info, temp_sql, "No Parameter", "No Value", null);
								printPageInfo(curData, "info");
								xmlData.add(curData);
								page_configs.put(ctr_info[0], xmlData);
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
	protected PageXmlData setPageXmlData(String pageParamName, ArrayList<String> pageParam, String serviceName, String[] ctr_info, String[] sqlInfo, String PRM_NAME, String VAL_INFO, String STRUCTURE) {
		PageXmlData result = new PageXmlData();
		result.setPageStaticParamName(pageParamName);
		result.setPageStaticParams(pageParam);
		result.setControlName(ctr_info[0]);
		result.setServiceName(serviceName);
		result.setLogicalDir(ctr_info[3]);
		result.setDir(ctr_info[2]);
		
		result.setPageName(ctr_info[4]);
		result.setDebug(ctr_info[1]);

		result.setSql(sqlInfo);
		result.setParameter(PRM_NAME);
		result.setData(VAL_INFO);
		return result;
	}

	@Override
	public void printPageInfo(PageXmlData xmlData, String type) {
		String controlName = xmlData.getControlName();
		String pageParamName = xmlData.getPageStaticParamsName();
		ArrayList<String> pageParams = xmlData.getPageStaticParams();
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

		String valMsg = "No Page Value";
		String[] valBuffer = null;
		if(VAL_INFO != null)
		{
			valBuffer = VAL_INFO.split("\n");
		
			for (int ab = 0; ab < valBuffer.length; ab++) {
				String tempVal = valBuffer[ab].trim();
				if(valMsg == "")
					valMsg = tempVal;
				else
					valMsg += ("\n\t" + tempVal);
			}
		}
		String PRM = "";
		if(pageParams != null) {
			for(int pr = 0; pr < pageParams.size(); pr++) {
				PRM += pageParams.get(pr);
				if(pr < pageParams.size() - 1)
					PRM += ", ";
			}
		}else {
			PRM = null;
		}
		String tempMsg = "\n┌──────────────────────────Page Control  :  " + controlName + "────────────────────────────"
				+ "\n│View Dir:\t" + pageDir + "\t\tView Page:\t" + pageName
				+ "\n│Logical Dir:\t" + logicalDir + "\t\tDebug Level:\t" + debugLevel
				+ "\n│Static Param Name:\t" + pageParamName + "\t\tStatic Param Value:\t" + PRM
				+ "\n│Service Name:\t" + serviceName + "\tParameter:\t" + PRM_NAME;

		if(type == "info") {
			tempMsg += "\n│SQL:\t" + sql_info
					+ "\n│Value:\t" + valMsg
					+ "\n└───────────────────────────────────────────────────────────────────────────";
		}else {
			tempMsg += "\n└───────────────────────────────────────────────────────────────────────────";
		}
		mklogger.temp(tempMsg, false);
		mklogger.flush("info");
	}
}
