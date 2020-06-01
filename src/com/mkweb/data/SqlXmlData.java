package com.mkweb.data;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.mkweb.impl.XmlData;

public class SqlXmlData extends AbsXmlData {
	   private String db = null;
	   
	   public void setDB(String db) { this.db = db;	}
	   public String getDB() { return this.db; }
}