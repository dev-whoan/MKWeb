package com.mkweb.config;

import java.io.File;

import java.io.FileReader;
import java.io.IOException;
import java.util.*;

import com.mkweb.data.MkFileServerAttributes;
import com.mkweb.data.MkSqlJsonData;
import com.mkweb.entity.MkSqlConfigCan;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.mkweb.data.MkFtpData;
import com.mkweb.utils.MkJsonData;
import com.mkweb.logger.MkLogger;

public class MkFTPConfigs {
	private HashMap<String, ArrayList<MkFtpData>> ftp_configs = new HashMap<String, ArrayList<MkFtpData>>();
	private File[] defaultFiles = null;
	private static MkFTPConfigs mfd = null;
	private long[] lastModified = null;
	private static final String TAG = "[FTP Configs]";
	private static final MkLogger mklogger = new MkLogger(TAG);
	private String filePrefix = null;
	private static MkFileServerAttributes ftpAttributes;
	private ArrayList<String> queries;
	public static MkFTPConfigs Me() {
		if(mfd == null) 
			mfd = new MkFTPConfigs();
	
		return mfd;
	}
	
	public void setPrefix(String filePrefix) {
		if(this.filePrefix == null)
			this.filePrefix = filePrefix;
	}
	
	public String getPrefix() {	return this.filePrefix;	}

	public MkFileServerAttributes getAttributes(){	return ftpAttributes;	}

	private boolean setFtpQuery(){
		queries = new ArrayList<>();
		ArrayList<MkSqlJsonData> sqlControl = MkSqlConfig.Me().getControl(ftpAttributes.getSqlControlName(), false);
		if(sqlControl.size() != 4) {
			mklogger.error("Sql services for ftp must include `select`, `insert`, `update`, `delete`!");
			return false;
		}
/*
            case "table":{  return getTableNameAttr();  }
            case "control":{    return getControllerNameAttr(); }
            case "service":{    return getServiceNameAttr();    }
            case "filedir":{    return getFileDirectoryAttr();  }
            case "format":{ return getFileFormat();   }
            case "filehash":{   return getFileHashAttr();   }
            case "filealive":{  return getFileAliveAttr();  }
*/
		try{
			String[] crud = {"select", "insert", "update", "delete"};
			String serviceColumns = ftpAttributes.get("control") + "," + ftpAttributes.get("service") + "," + ftpAttributes.get("filedir") + "," + ftpAttributes.get("format");
			String whereAs = ftpAttributes.get("seq") + " = @" + ftpAttributes.get("seq") + "@;";
			for(int i = 0; i < sqlControl.size(); i++){
				String[] sqlInfo = new String[5];

				sqlInfo[0] = crud[i];
				sqlInfo[1] = (i == 1) ? serviceColumns : ftpAttributes.get("seq") + "," + ftpAttributes.get("filehash") + "," + ftpAttributes.get("filealive") + "," + ftpAttributes.get("format");
				sqlInfo[2] = (i == 1) ? ("@" + ftpAttributes.get("control") + "@,@" + ftpAttributes.get("service") + "@,@" + ftpAttributes.get("filedir") + "@,@" + ftpAttributes.get("format") + "@")
						: "";
				sqlInfo[3] = (i != 1) ? whereAs : "";

				if(i == 2){
					sqlInfo[1] = ftpAttributes.get("filehash") + ", " + ftpAttributes.get("filealive");
					sqlInfo[2] = "@"+ftpAttributes.get("filehash")+"@,@"+ftpAttributes.get("filealive")+"@";
					sqlInfo[3] = ftpAttributes.get("seq") + " =@" + ftpAttributes.get("seq")+"@";
				}

				if(i == 3){
					sqlInfo[1] = "";
					sqlInfo[2] = "";
					sqlInfo[3] = ftpAttributes.get("seq") + "=@" + ftpAttributes.get("seq") + "@";
				}

				sqlControl.get(i).setRawSql(sqlInfo);
				HashMap<String, Object> tableData = new HashMap<>();
				tableData.put("from", ftpAttributes.getTableNameAttr());
				sqlControl.get(i).setData(MkSqlConfig.Me().createSQL(sqlInfo, tableData, false));
			}

			mklogger.info("=*=*=*=*=*=*=* Setting FTP SQLs  Done! *=*=*=*=*=*=*=*=");
		} catch (Exception e){
			mklogger.error("(func setFtpQuery) Error occured while generating ftp query: " + e.getMessage());
			mklogger.info("=*=*=*=*=*=*=* Setting FTP SQLs  Fail. *=*=*=*=*=*=*=*=");
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public void setFtpConfigs(File[] ftpConfigs) {
		ftp_configs.clear();
		defaultFiles = ftpConfigs;
		ArrayList<MkFtpData> ftpJsonData = null;
		lastModified = new long[ftpConfigs.length];
		int lmi = 0;
		if(ftpConfigs != null && ftpConfigs.length > 0){
			ftpAttributes = new MkFileServerAttributes()
					.setTableName(MkConfigReader.Me().get("mkweb.ftp.db.table"))
					.setSequenceAttr(MkConfigReader.Me().get("mkweb.ftp.db.table.attr.seq"))
					.setControllerAttr(MkConfigReader.Me().get("mkweb.ftp.db.table.attr.name.control"))
					.setServiceAttr(MkConfigReader.Me().get("mkweb.ftp.db.table.attr.name.service"))
					.setFileDirectory(MkConfigReader.Me().get("mkweb.ftp.db.table.attr.file.directory"))
					.setFormatAttr(MkConfigReader.Me().get("mkweb.ftp.db.table.attr.file.format"))
					.setFileHashAttr(MkConfigReader.Me().get("mkweb.ftp.db.table.attr.file.hash"))
					.setFileAliveAttr(MkConfigReader.Me().get("mkweb.ftp.db.table.attr.file.alive"))
					.setSqlControlName(MkConfigReader.Me().get("mkweb.ftp.db.controller"));
			mklogger.debug(
					String.format("table: %s, sequence: %s, controlatt: %s, serviceatt: %s" +
							    "\nfiledir: %s, fileformat: %s, filehash: %s, filealive: %s, sqlControl: %s",
							ftpAttributes.getTableNameAttr(),
							ftpAttributes.getSequenceAttr(),
							ftpAttributes.getControllerNameAttr(),
							ftpAttributes.getServiceNameAttr(),
							ftpAttributes.getFileDirectoryAttr(),
							ftpAttributes.getFileFormatAttr(),
							ftpAttributes.getFileHashAttr(),
							ftpAttributes.getFileAliveAttr(),
							ftpAttributes.getSqlControlName()
					)
			);
		}

		if(ftpAttributes == null){
			mklogger.error("You need to set databases to use MkFileTransfer. You may missed attributes for `mkweb.ftp.table` at `MkWeb.conf`");
			return;
		}

		for(File defaultFile : defaultFiles)
		{
			if(defaultFile.isDirectory())
				continue;
			
			lastModified[lmi++] = defaultFile.lastModified();
			mklogger.info("=*=*=*=*=*=*=* MkWeb FTP  Configs Start*=*=*=*=*=*=*=*=");
			mklogger.info("File: " + defaultFile.getAbsolutePath());
			mklogger.info("=            " + defaultFile.getName() +"              =");
			if(defaultFile == null || !defaultFile.exists())
			{
				mklogger.error("Config file is not exists or null");
				return;
			}

			try(FileReader reader = new FileReader(defaultFile)){
				ftpJsonData = new ArrayList<MkFtpData>();
				JSONParser jsonParser = new JSONParser();
				JSONObject jsonObject = (JSONObject) jsonParser.parse(reader);
				JSONObject ftpObject = (JSONObject) jsonObject.get("Controller");
				
				String ftpName = ftpObject.get("name").toString();
				String ftpControllerPath = ftpObject.get("path").toString();
				Object ftpAuth = ftpObject.get("auth");
				ftpAuth = (ftpAuth != null) ? ftpAuth.toString() : "no";
				
				String ftpDebugLevel = ftpObject.get("debug").toString();
				
				JSONArray serviceArray = (JSONArray) ftpObject.get("services");

				for (Object o : serviceArray) {
					JSONObject serviceObject = (JSONObject) o;
					String serviceId = null;
					String servicePath = null;
					String serviceDirPrefix = null;    //"dir"
					String serviceType = null;
					Object maxCounts = null;
					boolean serviceHashDirPrefix = false;

					String[] serviceAllowFileFormat = null;
					try {
						serviceId = serviceObject.get("id").toString();
						servicePath = serviceObject.get("servicepath").toString();
						Object prefix = serviceObject.get("dir");
						mklogger.debug("prefix : " + prefix);
						if (prefix != null) {
							serviceDirPrefix = prefix.toString();
							serviceHashDirPrefix = serviceObject.get("hash_dir").toString().contentEquals("true");
						}
						maxCounts = serviceObject.get("max_count");
						if (maxCounts != null) {
							try {
								maxCounts = Integer.parseInt(maxCounts.toString());
							} catch (NumberFormatException e) {
								mklogger.error("Failed to create service. The maxcount is not number format : " + serviceId);
								continue;
							}
						}

						if (ftpControllerPath.charAt(ftpControllerPath.length() - 1) == '/') {
							servicePath = (servicePath.charAt(0) == '/' ? (ftpControllerPath.substring(0, ftpControllerPath.length() - 1) + servicePath) : (ftpControllerPath + servicePath));
						} else {
							servicePath = (servicePath.charAt(0) == '/' ? (ftpControllerPath + servicePath) : (ftpControllerPath + "/" + servicePath));
						}

						if (!createDirectory(servicePath)) {
							mklogger.error("Failed to create directory. Please check your IO permissions. [" + servicePath + "]");
							return;
						}

						MkJsonData mjd = new MkJsonData(serviceObject.get("format").toString());
						if (!mjd.setJsonObject()) {
							mklogger.debug("Failed to set MkJsonObject service name : " + serviceId);
							continue;
						}

						JSONObject serviceFormatData = mjd.getJsonObject();
						serviceAllowFileFormat = new String[serviceFormatData.size()];
						for (int j = 0; j < serviceAllowFileFormat.length; j++) {
							serviceAllowFileFormat[j] = serviceFormatData.get("" + (j + 1)).toString();
						}

					} catch (Exception e) {
						mklogger.debug("Failed to create ftp controller. " + e.getMessage());
						e.printStackTrace();
						return;
					}

					MkFtpData result = new MkFtpData();
					result.setControlName(ftpName);
					result.setServiceType("ftp");
					result.setPath(servicePath);
					result.setDebugLevel(ftpDebugLevel);
					result.setServiceName(serviceId);
					result.setData(serviceAllowFileFormat);
					result.setDirPrefix(serviceDirPrefix);
					result.setHashDirPrefix(serviceHashDirPrefix);
					result.setMaxCount((int) maxCounts);
					result.setAuth(ftpAuth.toString());

					ftpJsonData.add(result);
					printFTPInfo(result, "info");
				}
				
				ftp_configs.put(ftpName, ftpJsonData);
				
			} catch (IOException | ParseException e) {
				mklogger.error(e.getMessage());
				e.printStackTrace();
			}
			mklogger.info("=*=*=*=*=*=*=* Setting   FTP   SQLs... *=*=*=*=*=*=*=*=");
			setFtpQuery();
			mklogger.info("=*=*=*=*=*=*=* MkWeb FTP  Configs  Done*=*=*=*=*=*=*=*=");
		}
	}
	
	public void printFTPInfo(MkFtpData jsonData, String type) {
		String tempMsg = "\n===============================FTP  Control================================="
				+ "\n|Controller:\t" + jsonData.getControlName()
				+ "\n|FTP ID:\t" + jsonData.getServiceName()
				+ "\n|FTP Path:\t" + jsonData.getPath()
				+ "\n|FTP Prefix:\t" + jsonData.getDirPrefix() + "\t\tFile Counts:\t" + jsonData.getMaxCount()
				+ "\n|Debug Level:\t" + jsonData.getDebugLevel()
				+ "\n|File Formats:\t" + Arrays.toString(jsonData.getData())
				+ "\n============================================================================";
		
		mklogger.temp(tempMsg, false);
		mklogger.flush(type);
	}
	
	private void reloadControls() {
		for(int i = 0; i < defaultFiles.length; i++)
		{
			if(lastModified[i] != defaultFiles[i].lastModified()){
				setFtpConfigs(defaultFiles);
				mklogger.info("==============Reload FTP Config files==============");
				mklogger.info("========Caused by : different modified time========");
				mklogger.info("==============Reload FTP Config files==============");
				break;
			}
		}
	}
	
	public ArrayList<MkFtpData> getControl(String controlName) {
		reloadControls();
		return ftp_configs.get(controlName);
	}
	
	public ArrayList<MkFtpData> getControlByServiceName(String serviceName){
		reloadControls();

		Iterator<String> iter  = ftp_configs.keySet().iterator();
		String resultControlName = null;
		ArrayList<MkFtpData> jsonData = null;
		while(iter.hasNext()) {
			String controlName = iter.next();
			jsonData = getControl(controlName);
			for(MkFtpData curData : jsonData) {
				if(serviceName.contentEquals(curData.getServiceName())) {
					resultControlName = controlName;
					break;
				}
			}
			
			if(resultControlName != null) {
				break;
			}
			jsonData = null;
		}
		
		return jsonData;
	}
	
	private boolean createDirectory(String path){
		String targetDir = (filePrefix + path);
		File folder = new File(targetDir);
		boolean isDirExists = folder.exists();
		if(!isDirExists)
		{
			mklogger.info("The directory is not exists. Creating new one...");
			try {
				isDirExists = folder.mkdirs();
				folder.setReadable(true, false);
				folder.setExecutable(true, false);
				if(!isDirExists) {
					mklogger.error("Failed to create path. [" + targetDir +"]");
					return false;
				}
			} catch (Exception e) {
				mklogger.error("Failed to create path. [" + targetDir +"] " + e.getMessage());
				return false;
			}
		}
		
		if(isDirExists) {
			mklogger.info("Success to create path. [" + targetDir +"]");
		}
		
		return isDirExists;
	}
}
