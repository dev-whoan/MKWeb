package com.mkweb.data;

import java.io.File;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.mkweb.impl.XmlData;

public class SqlXmlData extends AbsXmlData {
	   private String db = null;
	   private boolean allowSingle = false;
	   private ArrayList<String> columnData = null;
	   
	   public String getDB() { return this.db; }
	   public boolean getAllowSingle() {	return this.allowSingle;	}
	   public ArrayList<String>	getColumnData(){	return this.columnData;	}
	   
	   
	   public void setDB(String db) { this.db = db;	}
	   public void setAllowSingle(String as) {	this.allowSingle = (as.equals("yes") ? true : false);	}
	   public void setColumnData(ArrayList<String> cd) {	this.columnData = cd;	}
}