package com.mkweb.config;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.mkweb.can.MkSqlConfigCan;
import com.mkweb.data.SqlXmlData;
import com.mkweb.logger.MkLogger;

public class MkRestApiSqlConfigs extends MkSqlConfigCan {
	private HashMap<String, SqlXmlData> api_sql_configs = new HashMap<String, SqlXmlData>();

	private File defaultFile = null;

	private static MkRestApiSqlConfigs mrasc = null;
	private long lastModified; 
	private MkLogger mklogger = MkLogger.Me();
	private String controlName = "MkApiSQL";

	private String TAG = "[MkRestApiSqlConfigs]";

	public static MkRestApiSqlConfigs Me() {
		if(mrasc == null)
			mrasc = new MkRestApiSqlConfigs();
		return mrasc;
	}
	private String[] svc_list = {
			"id",
			"db",
			"allow_single",
			"allow_like"
	};
	private String[] svc_info = new String[svc_list.length];

	public void setSqlConfigs(File sqlConfigs) {
		api_sql_configs.clear();
		defaultFile = sqlConfigs;

		mklogger.info("=*=*=*=*=* MkWeb Rest Api Sql Configs Start*=*=*=*=*=*=");
		mklogger.info(TAG + "File: " + defaultFile.getAbsolutePath());
		if(defaultFile == null || !defaultFile.exists())
		{
			mklogger.error("Config file is not exists or null");
			return;
		}

		NodeList nodeList = setNodeList(defaultFile);

		if(nodeList != null) {
			lastModified = defaultFile.lastModified();

			for(int i = 0; i < nodeList.getLength(); i++)
			{
				Node node = nodeList.item(i);
				if(node.getNodeType() == Node.ELEMENT_NODE) {
					NamedNodeMap attributes = node.getAttributes();

					for(int sli = 0; sli < svc_list.length; sli++) {
						Node tN = attributes.getNamedItem(svc_list[sli]);
						svc_info[sli] = (tN != null ? tN.getNodeValue() : null);
					}
					NodeList queryNodes = node.getChildNodes();
					ArrayList<String> sqlColumns = null;
					
					String sqlQuerys = null;
					if(queryNodes != null) {
						for(int qn = 0; qn < queryNodes.getLength(); qn++) {
							Node sqlInfo = queryNodes.item(qn);
							if(sqlInfo.getNodeType() == Node.ELEMENT_NODE) {
								if(sqlInfo.getNodeName().contentEquals("columns")) {
									String col = sqlInfo.getTextContent();
									String[] colSplits = col.split("@");
									sqlColumns = new ArrayList<>();
									for(int k = 1; k < colSplits.length; k++) {
										sqlColumns.add(colSplits[k].trim());
									}
						//			sqlColumns = (ArrayList<String>) Arrays.asList(colSplit
								}else if(sqlInfo.getNodeName().contentEquals("query")) {
									sqlQuerys = sqlInfo.getTextContent();
								}
							}
						}
					}
					
					
					SqlXmlData xmlData = new SqlXmlData();

					xmlData.setControlName(this.controlName);
					//ID = 0, DB = 1  
					xmlData.setServiceName(svc_info[0]);
					xmlData.setDB(svc_info[1]);
					xmlData.setAllowSingle(svc_info[2]);
					xmlData.setAllowLike(svc_info[3]);
					xmlData.setData(sqlQuerys);
					xmlData.setColumnData(sqlColumns);

					api_sql_configs.put(svc_info[0], xmlData);

					mklogger.info("SQL ID :\t\t\t" + svc_info[0]);
					mklogger.info("SQL DB :\t\t\t" + svc_info[1]);
					mklogger.info("Allow  :\t\t\t" + svc_info[2]);
					mklogger.info("");

					sqlQuerys = sqlQuerys.trim();
					String queryMsg = "";

					String[] queryBuffer = sqlQuerys.split("\n");

					for (int j = 0; j < queryBuffer.length; j++) {
						String tempQuery = queryBuffer[j].trim();
						queryMsg += "\t\t\t\t\t\t\t\t" + tempQuery + "\n";
					}

					mklogger.info("query  :\n" + queryMsg + "\n");

				}
			}
		}else {
			mklogger.info(TAG + " No SQL Service has found. If you set SQL service, please check SQL config and web.xml.");
		}


		mklogger.info("=*=*=*=*=* MkWeb Rest Api Sql Configs Done*=*=*=*=*=*=");
	}

	public SqlXmlData getControlService(String serviceName) {
		if(lastModified != defaultFile.lastModified()) {
			setSqlConfigs(defaultFile);
			mklogger.info("==============Reload Rest Api SQL Config files==============");
			mklogger.info("==========Caused by   :    different modified time==========");
			mklogger.info("==============Reload Rest Api SQL Config files==============");
		}

		return api_sql_configs.get(serviceName);
	}
}
