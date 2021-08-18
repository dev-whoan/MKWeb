package com.mkweb.config;

import java.io.File;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.mkweb.utils.MkJsonData;
import com.mkweb.data.MkSqlJsonData;
import com.mkweb.entity.MkSqlConfigCan;
import com.mkweb.logger.MkLogger;

public class MkSQLConfigs extends MkSqlConfigCan {
	private HashMap<String, ArrayList<MkSqlJsonData>> sql_configs = new HashMap<String, ArrayList<MkSqlJsonData>>();
	private File[] defaultFiles = null;
	private static MkSQLConfigs sxc = null;
	private long[] lastModified = null;
	
	private static final String TAG = "[MkSQLConfigs]";
	private static final MkLogger mklogger = new MkLogger(TAG);

	public static MkSQLConfigs Me() {
		if(sxc == null)
			sxc = new MkSQLConfigs();
		return sxc;
	}

	public void setSqlConfigs(File[] sqlConfigs) {
		sql_configs.clear();
		defaultFiles = sqlConfigs;
		ArrayList<MkSqlJsonData> sqlJsonData = null;
		lastModified = new long[sqlConfigs.length];
		int lmi = 0;
		for(File defaultFile : defaultFiles)
		{
			if(defaultFile.isDirectory())
				continue;
			
			lastModified[lmi++] = defaultFile.lastModified();
			mklogger.info("=*=*=*=*=*=*=* MkWeb Sql  Configs Start*=*=*=*=*=*=*=*=");
			mklogger.info("File: " + defaultFile.getAbsolutePath());
			mklogger.info("=            " + defaultFile.getName() +"              =");
			if(defaultFile == null || !defaultFile.exists())
			{
				mklogger.error("Config file is not exists or null");
				return;
			}

			try(FileReader reader = new FileReader(defaultFile)){
				sqlJsonData = new ArrayList<MkSqlJsonData>();
				JSONParser jsonParser = new JSONParser();
				JSONObject jsonObject = (JSONObject) jsonParser.parse(reader);
				JSONObject sqlObject = (JSONObject) jsonObject.get("Controller");
				
				String sqlName = sqlObject.get("name").toString();
				String sqlDebugLevel = sqlObject.get("debug").toString();

				String sqlDB = sqlObject.get("db").toString();
				String sqlAPI = sqlObject.get("api").toString();
				
				JSONArray serviceArray = (JSONArray) sqlObject.get("services");

				for (Object o : serviceArray) {
					JSONObject serviceObject = (JSONObject) o;
					String serviceId = null;
					boolean serviceAuth = false;
					String[] serviceQuery = null;
					HashMap<String, Object> tableData = null;
					try {
						serviceId = serviceObject.get("id").toString();
						serviceAuth = (serviceObject.get("auth") != null && serviceObject.get("auth").toString().contentEquals("yes"));

						String serviceColumns = null;
						String serviceDatas = null;

						MkJsonData mjd = new MkJsonData(serviceObject.get("query").toString());
						if (!mjd.setJsonObject()) {
							mklogger.debug("Failed to set MkJsonObject service name : " + serviceId);
							return;
						}

						JSONObject serviceQueryData = mjd.getJsonObject();
						// -1 for table object
						serviceQuery = new String[serviceQueryData.size() - 1];

						//serviceQuery.length != 5
						if (serviceQueryData.size() != 5) {
							mklogger.error("[Controller: " + controlName + " | service: " + serviceId + "] The format of query is not valid. Please check your page configs.");
							continue;
						}

						serviceQuery[0] = serviceQueryData.get("crud").toString();
						//	serviceQuery[2] = serviceQueryData.get("table").toString();
						serviceQuery[3] = serviceQueryData.get("where").toString();    // [4]

						//존재 안하면!! table이 없는거니까 잘못된거임!
						if (serviceQueryData.get("table") == null) {
							mklogger.error("Failed to set Table data. Service name " + serviceId);
							continue;
						}

						if (serviceQueryData.get("table") instanceof JSONObject) {
							mklogger.debug("JSONObject!!");
						} else {
							mklogger.error("Failed to set Table data. Table must be instance of JSONObject. Service name : " + serviceId);
							continue;
						}

						tableData = (HashMap<String, Object>) serviceQueryData.get("table");

						MkJsonData serviceColumn = new MkJsonData(serviceQueryData.get("column").toString());
						if (!serviceColumn.setJsonObject()) {
							mklogger.error("Failed to set MkJsonObject service name : " + serviceId + "(column)");
							continue;
						}
						JSONObject jsonColumns = serviceColumn.getJsonObject();
						serviceColumns = "";
						for (int k = 0; k < jsonColumns.size(); k++) {
							serviceColumns += jsonColumns.get("" + (k + 1)).toString();

							if (k < jsonColumns.size() - 1)
								serviceColumns += ",";
						}

						MkJsonData serviceData = new MkJsonData(serviceQueryData.get("data").toString());

						if (!serviceData.setJsonObject()) {
							mklogger.debug("Failed to set MkJsonObject service name : " + serviceId + "(data)");
							continue;
						}
						JSONObject jsonDatas = serviceData.getJsonObject();
						serviceDatas = "";
						for (int k = 0; k < jsonDatas.size(); k++) {
							serviceDatas += "@" + jsonDatas.get("" + (k + 1)).toString() + "@";

							if (k < jsonDatas.size() - 1)
								serviceDatas += ",";
						}

						serviceQuery[1] = serviceColumns;
						serviceQuery[2] = serviceDatas;        // [3]
					} catch (NullPointerException npe) {
						mklogger.error("[Controller: " + controlName + "(" + serviceId + ")] The service SQL doesn't have attributes. Please check the SQL config.");
						continue;
					}

					MkSqlJsonData sqlData = new MkSqlJsonData();

					Object join = tableData.get("join");

					if (join != null) {
						JSONObject joinObject = (JSONObject) join;
						String[] catchme = {"type", "joinfrom", "on"};
						for (int cm = 0; cm < catchme.length; cm++) {
							try {
								tableData.put(catchme[cm], joinObject.get(catchme[cm]).toString());
							} catch (NullPointerException e) {
								mklogger.error("[Controller: " + controlName + "(" + serviceId + ")] You must to set \"" + catchme[cm] + "\" in \"table\" to use join. This controller will not be registered.");
								return;
							}
						}
					}

					String[] finalQuery = createSQL(serviceQuery, tableData, false);
					sqlData.setRawSql(serviceQuery);
					sqlData.setControlName(sqlName);
					sqlData.setServiceType(serviceQuery[0]);
					//ID = 0, DB = 1
					sqlData.setServiceName(serviceId);
					sqlData.setTableData(tableData);
					sqlData.setDB(sqlDB);
					sqlData.setData(finalQuery);
					sqlData.setDebugLevel(sqlDebugLevel);
					sqlData.setAuth(serviceAuth ? "yes" : "no");
					sqlData.setApiSQL((sqlAPI.toLowerCase().contentEquals("yes")));

					sqlJsonData.add(sqlData);
					printSqlInfo(sqlData, "info");
				}
				
				sql_configs.put(sqlName, sqlJsonData);
			} catch (FileNotFoundException e) {
				mklogger.error(e.getMessage());
				e.printStackTrace();
			} catch (IOException e) {
				mklogger.error(e.getMessage());
				e.printStackTrace();
			} catch (ParseException e) {
				mklogger.error(e.getMessage());
				e.printStackTrace();
			}
			mklogger.info("=*=*=*=*=*=*=* MkWeb Sql  Configs  Done*=*=*=*=*=*=*=*=");
		}
	}

	public void printSqlInfo(MkSqlJsonData jsonData, String type) {
		String tempMsg = "\n===============================SQL  Control================================="
				+ "\n|Controller: \t" + jsonData.getControlName()
				+ "\n|SQL ID:\t" + jsonData.getServiceName() + "\t\t API:\t" + jsonData.IsApiSql()
				+ "\n|SQL Auth:\t" + (jsonData.getAuth() == 2)
				+ "\n|SQL DB:\t" + jsonData.getDB() + "\t\t Type:\t" + jsonData.getServiceType()
				+ "\n|SQL Table:\t" + jsonData.getTableData()
				+ "\n|SQL Query:\t" + jsonData.getData()[0].trim()
				+ "\n|Debug Level:\t" + jsonData.getDebugLevel()
				+ "\n============================================================================";
		
		mklogger.temp(tempMsg, false);
		mklogger.flush(type);
	}

	private void updateConfigs(){
		for(int i = 0; i < defaultFiles.length; i++)
		{
			if(lastModified[i] != defaultFiles[i].lastModified()){
				setSqlConfigs(defaultFiles);
				mklogger.info("==============Reload SQL Config files==============");
				mklogger.info("========Caused by : different modified time========");
				mklogger.info("==============Reload SQL Config files==============");
				break;
			}
		}
	}

	public ArrayList<MkSqlJsonData> getControl(String controlName) {
		updateConfigs();

		return sql_configs.get(controlName);
	}

	public MkSqlJsonData getServiceInfoByServiceName(ArrayList<MkSqlJsonData> control, String serviceName){
		for(MkSqlJsonData service : control){
			if(service.getServiceName().contentEquals(serviceName)){
				return service;
			}
		}
		return null;
	}

	public String getServiceTypeByServiceName(ArrayList<MkSqlJsonData> control, String serviceName){
		for(MkSqlJsonData service : control){
			if(service.getServiceName().contentEquals(serviceName)){
				return service.getServiceType();
			}
		}
		return null;
	}

	public ArrayList<MkSqlJsonData> getControlByServiceName(String serviceName){
		updateConfigs();
		
		Set iter = sql_configs.keySet();
		Iterator sqlIterator = iter.iterator();
		String resultControlName = null;
		ArrayList<MkSqlJsonData> jsonData = null;
		while(sqlIterator.hasNext()) {
			String controlName = sqlIterator.next().toString();
			jsonData = getControl(controlName);
			
			for(MkSqlJsonData curData : jsonData) {
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
}
