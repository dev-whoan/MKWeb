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

import com.mkweb.abst.AbsXmlData;
import com.mkweb.logger.MkLogger;

public class SQLXmlConfigs extends AbsXmlData {

	private static HashMap<String, AbsXmlData> sql_configs = new HashMap<String, AbsXmlData>();
	private static File defaultFile = null;
	private static SQLXmlConfigs sxc = null;
	
	public static SQLXmlConfigs getInstance() {
		if(sxc == null)
			sxc = new SQLXmlConfigs();
		return sxc;
	}
	
	public static void setSqlConfigs(File sqlConfigs) {
		sql_configs.clear();
		defaultFile = sqlConfigs;
		
		MkLogger.info("=*=*=*=*=*=*=* MkWeb Sql  Configs Start*=*=*=*=*=*=*=*=");
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
				NamedNodeMap attributes = node.getAttributes();
				String ID = attributes.getNamedItem("id").getNodeValue();
				String DB = attributes.getNamedItem("db").getNodeValue();
				String query = node.getTextContent();
				
				AbsXmlData xmlData = new AbsXmlData();
				
				xmlData.setControlName("MkSQL");
				xmlData.setServiceName(ID);
				xmlData.setNode(node);
				xmlData.setData(query);

				sql_configs.put(ID, xmlData);
				
				MkLogger.info("SQL ID :\t\t\t" + ID);
				MkLogger.info("SQL DB :\t\t\t" + DB);

				query = query.trim();
				String queryMsg = "";
				
				String[] queryBuffer = query.split("\n");
				
				for (int j = 0; j < queryBuffer.length; j++) {
					String tempQuery = queryBuffer[j].trim();
					queryMsg += "\t\t\t\t\t\t\t\t" + tempQuery + "\n";
				}
				
				MkLogger.info("query  :\n" + queryMsg + "\n");
				
			}
		}
		
		MkLogger.info("=*=*=*=*=*=*=* MkWeb Sql  Configs  Done*=*=*=*=*=*=*=*=");
	}
	
	public static AbsXmlData getControlService(AbsXmlData xmlData) {
		if(xmlData == null) {
			MkLogger.error("[Sql] : Input xml data is null");
			return null;
		}
		
		String key = xmlData.getServiceName();
		return sql_configs.get(key);
	}
}
