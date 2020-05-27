package com.mkweb.config;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Stream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;

import com.mkweb.data.AbsXmlData;
import com.mkweb.logger.MkLogger;

public class SQLXmlConfigs extends AbsXmlData {
	private HashMap<String, AbsXmlData> sql_configs = new HashMap<String, AbsXmlData>();
	private File defaultFile = null;
	private static SQLXmlConfigs sxc = null;
	private long lastModified = 0L;
	private MkLogger mklogger = MkLogger.Me();
	public static SQLXmlConfigs Me() {
		if(sxc == null)
			sxc = new SQLXmlConfigs();
		return sxc;
	}
	
	public void setSqlConfigs(File sqlConfigs) {
		sql_configs.clear();
		defaultFile = sqlConfigs;
		
		mklogger.info("=*=*=*=*=*=*=* MkWeb Sql  Configs Start*=*=*=*=*=*=*=*=");
		if(defaultFile == null || !defaultFile.exists())
		{
			mklogger.error("Config file is not exists or null");
			return;
		}
		
		NodeList nodeList = setNodeList(defaultFile);
		lastModified = defaultFile.lastModified();
		
		for(int i = 0; i < nodeList.getLength(); i++)
		{
			Node node = nodeList.item(i);
			if(node.getNodeType() == Node.ELEMENT_NODE) {
				NamedNodeMap attributes = node.getAttributes();
				String ID = attributes.getNamedItem("id").getNodeValue();
				String DB = attributes.getNamedItem("db").getNodeValue();
				String query = node.getTextContent();
				
				AbsXmlData xmlData = new AbsXmlData();
				
				xmlData.setControlName("MkSQL");
				xmlData.setServiceName(ID);
				xmlData.setData(query);

				sql_configs.put(ID, xmlData);
				
				mklogger.info("SQL ID :\t\t\t" + ID);
				mklogger.info("SQL DB :\t\t\t" + DB);

				query = query.trim();
				String queryMsg = "";
				
				String[] queryBuffer = query.split("\n");
				
				for (int j = 0; j < queryBuffer.length; j++) {
					String tempQuery = queryBuffer[j].trim();
					queryMsg += "\t\t\t\t\t\t\t\t" + tempQuery + "\n";
				}
				
				mklogger.info("query  :\n" + queryMsg + "\n");
				
			}
		}
		
		mklogger.info("=*=*=*=*=*=*=* MkWeb Sql  Configs  Done*=*=*=*=*=*=*=*=");
	}
	
	public AbsXmlData getControlService(String serviceName) {
		if(lastModified != defaultFile.lastModified()) {
			setSqlConfigs(defaultFile);
			mklogger.info("==============Reload SQL Config files==============");
			mklogger.info("========Caused by : different modified time========");
			mklogger.info("==============Reload SQL Config files==============");
		}
		
		return sql_configs.get(serviceName);
	}
}
