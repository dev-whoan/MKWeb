package com.mkweb.logger;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.mkweb.abst.AbsXmlData;

public class MkLogger extends AbsXmlData{
	private static HashMap<String, String> log_configs = new HashMap<String, String>();

	private static File defaultFile = null;
	private static String TAG = "[MkLogger]";
	
	private static String logMsg = null;

	private static void Log(Object msg, String caller) {
		try {
			if(log_configs.get("print_date").toString().equals("yes"))
			{
				Date d = new Date();
				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
				if(log_configs.get("print_time").toString().equals("yes"))
					dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
				
				TAG = "("+dateFormat.format(d)+")[MkLogger]";
			}
			
			if(log_configs.get("use_catalina").toString().equals("yes"))
				System.out.println(TAG + msg.toString());
			else {
				File logFile = new File(log_configs.get("log_location").toString());
				BufferedWriter bw = new BufferedWriter(new FileWriter(logFile, true));
				PrintWriter pw = new PrintWriter(bw, true);
				
				pw.println(TAG + msg.toString());
			}
		}catch (IOException e){
			System.out.println(TAG + " Logging error occured in function info():: original logging message: " + msg);
			System.out.println(TAG + " IOException: " + e.getMessage());
		}catch(Exception e) {
			System.out.println(TAG + " MKLogger::function " + caller + " has occured some errors!");
			e.printStackTrace();
		}
	}
	
	public static void flush(int msgType) {
		if(logMsg != null)
		{
			if(msgType == 1)
				info(logMsg);
			else if(msgType == 2)
				warn(logMsg);
			else if(msgType == 3)
				error(logMsg);
			
			logMsg = null;
		}
		
			
	}
	
	public static void temp(Object msg, boolean doFlush) {
		if(logMsg != null) {
			if(doFlush)
			{
				warn("--AUTO FLUSHING--: " + logMsg);
				logMsg = null;
				return;
			}
			
			logMsg += "\n" + msg.toString();
		}else {
			logMsg = msg.toString();
		}
	}
	
	public static void info(Object msg)  {
		switch(log_configs.get("log_level").toString())
		{
		case "info":
			Log("[INFO]" + msg, "info");
			break;
		}
	}
	
	public static void warn(Object msg)  {
		switch(log_configs.get("log_level").toString())
		{
		case "info": case "warn":
			Log("[*WARN*]" + msg, "warn");
			break;
		}
	}
	
	public static void error(Object msg) {
		switch(log_configs.get("log_level").toString())
		{
		case "info": case "warn": case "error":
			Log("[**ERROR**]" + msg, "error");
			break;
		}
	}
	
	public static void debug(Object msg) {
		switch(log_configs.get("log_level").toString())
		{
		case "info": case "warn": case "error":
			Log("[^_^ DEBUG ^_^]\n" + msg, "debug");
			break;
		}
	}
	
	private static void createFile(){
		String absPath = log_configs.get("log_location").toString();
		File file = new File(absPath);
		if(!file.exists())
		{
			System.out.println(absPath);
			System.out.println("MkLogger File is not exists. Creating new one...");
			
			
			String[] directoryPath = absPath.split("/");
			
			String dirPath = "";
			for(int i = 0; i < directoryPath.length; i++) {
				if(i != (directoryPath.length-1))
					if(i != 0)
						dirPath += "/" + directoryPath[i];
					else
						dirPath += directoryPath[i];
				else
					break;
			}
			System.out.println("dirPath: " + dirPath);
			boolean isDirExists = false;
			if(!new File(dirPath).exists())
				isDirExists = new File(dirPath).mkdirs(); 
			else
				isDirExists = true;
			
			if(isDirExists)
			{
				try {
					file.createNewFile();
					info("MkLogger File is not exists. Creating new one...");
					info("Logger Path: " + absPath);
					info("Success to create log file!");
				} catch (IOException e) {
					System.out.println("Failed to create file: " + defaultFile.getAbsolutePath() + "\n" + e.getMessage());
				}
			}
		}
	}
	
	public static void setLogConfig(File logConfigs) {
		log_configs.clear();
		logMsg = "";
		TAG = "[MkLogger]";
		defaultFile = logConfigs;
		try {
			logMsg += ("\n[Initializing][MkLogger]" + "=*=*=*=*=*=*=* MkWeb Log Configs Start*=*=*=*=*=*=*=*=\n");
			logMsg += ("[Initializing][MkLogger]" + "=                   Start setting...                 =\n");

			if(defaultFile == null || !defaultFile.exists())
			{
				logMsg += ("[Initializing][MkLogger]" + "Config file is not exists or null\n");
				return;
			}
			
			NodeList nodeList = setNodeList(defaultFile);
			
			String[] config_key = {"log_level", "print_date", "print_time", "use_catalina", "log_location"};
			for(int i = 1; i < nodeList.getLength(); i++)
			{
				Node node = nodeList.item(i);
				if(node.getNodeType() == Node.ELEMENT_NODE) {
					NamedNodeMap attributes = node.getAttributes();
					
					String value = attributes.getNamedItem(config_key[(int) Math.floor(i/2)]).getNodeValue();
					
					log_configs.put(config_key[(int) Math.floor(i/2)], value);
					logMsg += ("[Initializing][MkLogger]" + config_key[(int) Math.floor(i/2)] + "\t:\t\t\t" + value + "\n");
				}
			}
			
			createFile();
			
			info(logMsg);
			logMsg = null;
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}
}
