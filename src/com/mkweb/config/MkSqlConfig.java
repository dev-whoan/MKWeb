package com.mkweb.config;

import com.mkweb.data.MkSqlJsonData;
import com.mkweb.entity.MkSqlConfigCan;
import com.mkweb.logger.MkLogger;
import com.mkweb.utils.MkJsonData;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class MkSqlConfig extends MkSqlConfigCan {
    private HashMap<String, ArrayList<MkSqlJsonData>> sql_configs = new HashMap<String, ArrayList<MkSqlJsonData>>();
    private HashMap<String, ArrayList<MkSqlJsonData>> apiSql_configs = new HashMap<>(); //분리!!
    private File[] normalDefaultFiles = null;
    private File[] apiDefaultFiles = null;
    private static MkSqlConfig msc = null;
    private long[] lastModified = null;
    private long[] lastApiModified = null;

    private static final String TAG = "[MkSqlConfig]";
    private static final MkLogger mklogger = new MkLogger(TAG);

    public static MkSqlConfig Me() {
        if(msc == null)
            msc = new MkSqlConfig();
        return msc;
    }

    @Override
    public void setSqlConfigs(File[] sqlConfigs, String typeName) {
        if(typeName.contentEquals("SQL")) {
            sql_configs.clear();
            normalDefaultFiles = sqlConfigs;
            lastModified = new long[sqlConfigs.length];
        }
        else if(typeName.contentEquals("API SQL")){
            apiSql_configs.clear();
            apiDefaultFiles = sqlConfigs;
            lastApiModified = new long[sqlConfigs.length];
        }

        ArrayList<MkSqlJsonData> sqlJsonData = null;
        int lmi = 0;
        for(File defaultFile : sqlConfigs) {
            if (defaultFile.isDirectory())
                continue;

            if(typeName.contentEquals("SQL"))
                lastModified[lmi++] = defaultFile.lastModified();
            else if(typeName.contentEquals("API SQL"))
                lastApiModified[lmi++] = defaultFile.lastModified();

            mklogger.info("=*=*=*=*=*=*=* MkWeb " + typeName + "  Configs Start*=*=*=*=*=*=*=*=");
            mklogger.info("File: " + defaultFile.getAbsolutePath());
            mklogger.info("=            " + defaultFile.getName() + "              =");
            if (defaultFile == null || !defaultFile.exists()) {
                mklogger.error("Config file is not exists or null");
                return;
            }

            try (FileReader reader = new FileReader(defaultFile)) {
                sqlJsonData = new ArrayList<MkSqlJsonData>();
                JSONParser jsonParser = new JSONParser();
                JSONObject jsonObject = (JSONObject) jsonParser.parse(reader);
                JSONObject sqlObject = (JSONObject) jsonObject.get("Controller");

                String sqlName = sqlObject.get("name").toString();
                String sqlDebugLevel = sqlObject.get("debug").toString();

                String sqlDB = sqlObject.get("db").toString();
                String sqlAPI = sqlObject.get("api").toString();
                String[] serviceConditions = null;
                String[] sqlParameters = null;
                boolean isApiSql = sqlAPI.toLowerCase().contentEquals("yes");
                /* API 영역 */
                String sqlTable = null; //sqlObject.get("table").toString();

                if (isApiSql) {
                    sqlTable = sqlObject.get("table").toString();

                    Object sqlParamObject = sqlObject.get("parameter");
                    if (sqlParamObject != null) {
                        JSONObject sqlParameterObject = (JSONObject) sqlParamObject;
                        sqlParameters = new String[sqlParameterObject.size()];
                        for (int j = 0; j < sqlParameterObject.size(); j++) {
                            sqlParameters[j] = sqlParameterObject.get("" + (j + 1)).toString();
                        }

                        if (sqlParameters.length == 1 && sqlParameters[0].contentEquals("*")) {
                            sqlParameters = null;
                        }
                    }


                    MkJsonData mkJsonData = new MkJsonData(sqlObject.get("condition").toString());
                    JSONObject tempValues = null;

                    if (mkJsonData.setJsonObject()) {
                        tempValues = mkJsonData.getJsonObject();
                    }
                    if (tempValues.size() == 0) {
                        mklogger.error("[Controller: " + controlName + " | Service ID: " + serviceName + "] Service doesn't have any value. Service must have at least one value. If the service does not include any value, please create blank one.");
                        mklogger.debug("{\"1\":\"\"}");
                        continue;
                    }

                    serviceConditions = new String[tempValues.size()];

                    for (int j = 0; j < tempValues.size(); j++) {
                        serviceConditions[j] = tempValues.get("" + (j + 1)).toString();
                    }
                }
                /* API 영역 이까지 함 */
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
                        serviceQuery = (isApiSql ? new String[serviceQueryData.size() + 1] : new String[serviceQueryData.size()]);

                        if (serviceQuery.length != 5) {
                            mklogger.error("[Controller: " + sqlName + " | service: " + serviceId + "] The format of query is not valid. Please check your page configs.");
                            continue;
                        }

                        serviceQuery[0] = serviceQueryData.get("crud").toString();
                        //	serviceQuery[2] = serviceQueryData.get("table").toString();
                        serviceQuery[3] = serviceQueryData.get("where").toString();    // [4]
                        if (isApiSql) {
                            tableData = new HashMap<>();
                            tableData.put("from", sqlTable);
                        }
                        else {
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
                        }

                        MkJsonData serviceColumn = new MkJsonData(serviceQueryData.get("column").toString());
                        if (!serviceColumn.setJsonObject()) {
                            mklogger.debug("Failed to set MkJsonObject service name : " + serviceId + "(column)");
                            return;
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
                            return;
                        }
                        JSONObject jsonDatas = serviceData.getJsonObject();
                        serviceDatas = "";
                        for (int k = 0; k < jsonDatas.size(); k++) {
                            serviceDatas += "@" + jsonDatas.get("" + (k + 1)).toString() + "@";

                            if (k < jsonDatas.size() - 1)
                                serviceDatas += ",";
                        }
                        serviceQuery[1] = serviceColumns;
                        serviceQuery[2] = serviceDatas;
                    } catch (NullPointerException npe) {
                        mklogger.error("[Controller: " + sqlName + "(" + serviceId + ")] The service SQL doesn't have attributes. Please check the SQL config.");
                        continue;
                    }

                    MkSqlJsonData sqlData = new MkSqlJsonData();

                    if (!isApiSql) {
                        /* normal에만 존재 */
                        Object join = tableData.get("join");

                        if (join != null) {
                            JSONObject joinObject = (JSONObject) join;
                            String[] catchme = {"type", "joinfrom", "on"};
                            for (int cm = 0; cm < catchme.length; cm++) {
                                try {
                                    tableData.put(catchme[cm], joinObject.get(catchme[cm]).toString());
                                } catch (NullPointerException e) {
                                    mklogger.error("[Controller: " + sqlName + "(" + serviceId + ")] You must to set \"" + catchme[cm] + "\" in \"table\" to use join. This controller will not be registered.");
                                    return;
                                }
                            }
                        }
                        /* normal에만 존재 */
                    }
                    String[] finalQuery = createSQL(serviceQuery, tableData, isApiSql);

                    sqlData.setRawSql(serviceQuery);
                    sqlData.setControlName(sqlName);
                    sqlData.setServiceType(serviceQuery[0]);
                    //ID = 0, DB = 1
                    sqlData.setServiceName(serviceId);
                    sqlData.setTableData(tableData);
                    sqlData.setDB(sqlDB);
                    sqlData.setData(finalQuery);
                    sqlData.setDebugLevel(sqlDebugLevel);
                    sqlData.setAuth(isApiSql ? (serviceAuth ? "yes" : "no") : "no");
                    sqlData.setApiSQL((sqlAPI.toLowerCase().contentEquals("yes")));
                    if(isApiSql) {
                        sqlData.setCondition(serviceConditions);
                        sqlData.setParameters(sqlParameters);
                    }
                    sqlJsonData.add(sqlData);

                    printSqlInfo(sqlData, "info", isApiSql);
                }
                if(typeName.contentEquals("SQL"))
                    sql_configs.put(sqlName, sqlJsonData);
                else if(typeName.contentEquals("API SQL"))
                    apiSql_configs.put(sqlName, sqlJsonData);
            } catch(ParseException | IOException e){
                mklogger.error("Error occured while setting Controller: " + e.getMessage());
                e.printStackTrace();
            }
            mklogger.info("=*=*=*=*=*=*=* MkWeb " + typeName + "  Configs  Done*=*=*=*=*=*=*=*=");
        }
    }

    @Override
    public void printSqlInfo(MkSqlJsonData jsonData, String type, boolean isApi){
        if(isApi)
            printApiSqlInfo(jsonData, type);
        else
            printNormalSqlInfo(jsonData, type);
    }

    @Override
    protected String[] createSQL(String[] befQuery, HashMap<String, Object> tableData, boolean isApi) {
        {
            String[] result = new String[1];

            String rawFrom = tableData.get("from").toString();
            String dataFrom = rawFrom;

            boolean doJoin = (tableData.get("join") != null);
            String joinType = "";
            String joinFrom = "";
            String joinOn = "";
            if(doJoin) {
                joinType = tableData.get("type").toString();
                joinFrom = tableData.get("joinfrom").toString();
                joinOn = tableData.get("on").toString();

                dataFrom = rawFrom + " " + joinType + " " + joinFrom + " ON " + joinOn;
            }
            mklogger.debug("befQuery 0 : " + befQuery[0].toLowerCase());

            switch(befQuery[0].toLowerCase()) {
                case "select": {
                    if (!isApi) {
                        if (befQuery[3].length() > 0)
                            result[0] = "SELECT " + befQuery[1] + " FROM " + dataFrom + " WHERE " + befQuery[3] + ";";
                        else
                            result[0] = "SELECT " + befQuery[1] + " FROM " + dataFrom + ";";
                    } else {
                        result[0] = "SELECT " + befQuery[1] + " FROM " + dataFrom + " WHERE " + befQuery[3] + ";";
                    }

                    break;
                }
                case "insert": {
                    result[0] = "INSERT INTO " + dataFrom + "(" + befQuery[1] + ") VALUE (" + befQuery[2] + ");";
                    break;
                }
                case "update": {
                    String[] tempColumns = befQuery[1].split(",");
                    String[] tempDatas = befQuery[2].split(",");
                    String tempField = "";
                    if (tempColumns.length != tempDatas.length) {
                        mklogger.error("(func createQuery) UPDATE Query is not valid. Columns count and data count is not same.");
                        return null;
                    }

                    for (int i = 0; i < tempColumns.length; i++) {
                        tempField += tempColumns[i] + "=" + tempDatas[i];

                        if (i < tempColumns.length - 1)
                            tempField += ", ";
                    }
                    result[0] = "UPDATE " + dataFrom + " SET " + tempField + " WHERE " + befQuery[3] + ";";
                    break;
                }
                case "delete": {
                    result[0] = "DELETE FROM " + dataFrom + " WHERE " + befQuery[3] + ";";
                    break;
                }
            }
            return result;
        }
    }

    private void printNormalSqlInfo(MkSqlJsonData jsonData, String type) {
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

    private void printApiSqlInfo(MkSqlJsonData jsonData, String type) {
        String conditions = "";
        int conditionLength = (jsonData.getCondition() != null ? jsonData.getCondition().length : -1);

        for(int i = 0; i < conditionLength; i++) {
            conditions += jsonData.getCondition()[i];

            if(i < conditionLength -1)
                conditions += ", ";
        }
        String tempMsg = "\n===========================SQL Control  :  " + jsonData.getControlName() + "============================="
                + "\n|SQL ID:\t" + jsonData.getServiceName() + "\t\t API:\t" + jsonData.IsApiSql()
                + "\n|SQL DB:\t" + jsonData.getDB()
                + "\n|SQL Debug:\t" + jsonData.getDebugLevel()
                + "\n|sql Query:\t" + jsonData.getData()[0].trim()
                + "\n|conditions:\t" + conditions
                + "\n============================================================================";

        mklogger.temp(tempMsg, false);
        mklogger.flush(type);
    }

    private void updateConfigs(boolean isApi){
        String typeName = (isApi ? "API SQL" : "SQL");
        File[] defaultFiles = (isApi ? apiDefaultFiles : normalDefaultFiles);
        long[] cLM = (isApi ? lastApiModified : lastModified);
        for(int i = 0; i < defaultFiles.length; i++)
        {
            if(cLM[i] != defaultFiles[i].lastModified()){
                setSqlConfigs(defaultFiles, typeName);
                mklogger.info("==============Reload SQL Config files==============");
                mklogger.info("========Caused by : different modified time========");
                mklogger.info("==============Reload SQL Config files==============");
                break;
            }
        }
    }

    public MkSqlJsonData getServiceInfoByServiceName(String serviceName){
        ArrayList<MkSqlJsonData> control = getControlByServiceName(serviceName, false);
        for(MkSqlJsonData service : control){
            if(service.getServiceName().contentEquals(serviceName)){
                return service;
            }
        }
        return null;
    }

    @Override
    public ArrayList<MkSqlJsonData> getControl(String controlName, boolean isApi) {
        updateConfigs(isApi);
        return (isApi ? apiSql_configs.get(controlName) : sql_configs.get(controlName));
    }

    @Override
    public ArrayList<MkSqlJsonData> getControlByServiceName(String serviceName, boolean isApi) {
        updateConfigs(isApi);

        mklogger.debug("apiSql_configs.size() : " + apiSql_configs.size());
        mklogger.debug("apiSql_configs.keyset() : " + apiSql_configs.keySet().toString());

        Iterator<String> sqlIterator = (isApi ? apiSql_configs.keySet().iterator() : sql_configs.keySet().iterator());
        String resultControlName = null;
        ArrayList<MkSqlJsonData> jsonData = null;
        mklogger.debug("isIter has Next : " + sqlIterator.hasNext());
        while(sqlIterator.hasNext()) {
            String controlName = sqlIterator.next();
            mklogger.debug("controlName: " + controlName);
            jsonData = getControl(controlName, isApi);

            for(MkSqlJsonData curData : jsonData) {
                mklogger.debug("\tserviceName: " + curData.getServiceName());
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
