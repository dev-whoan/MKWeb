package com.mkweb.config;

import com.mkweb.data.MkDeviceData;
import com.mkweb.data.MkPageJsonData;
import com.mkweb.entity.MkPageConfigCan;
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
import java.util.*;

public class MkViewConfig extends MkPageConfigCan {
    private HashMap<String, ArrayList<MkPageJsonData>> page_configs = new HashMap<String, ArrayList<MkPageJsonData>>();
    private HashMap<String, ArrayList<MkPageJsonData>> apiPage_configs = new HashMap<>();
    private HashMap<String, String> allowURI = new HashMap<String, String>();
    private File[] normalDefaultFiles = null;
    private File[] apiDefaultFiles = null;

    private static MkViewConfig vc = null;
    private long[] lastModified;
    private static final String TAG = "[MkViewConfig]";
    private static final MkLogger mklogger = new MkLogger(TAG);

    public static MkViewConfig Me() {
        if(vc == null)
            vc = new MkViewConfig();
        return vc;
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
            "obj",
            "method"
    };

    @Override
    public void setPageConfigs(File[] pageConfigs, String pageType) {
        if(pageType.toLowerCase().contentEquals("page")) {
            page_configs.clear();
            normalDefaultFiles = pageConfigs;
        }
        else if(pageType.toLowerCase().contentEquals("api page")) {
            apiPage_configs.clear();
            apiDefaultFiles = pageConfigs;
        }

        ArrayList<MkPageJsonData> pageJsonData = null;
        lastModified = new long[pageConfigs.length];
        allowURI = new HashMap<String, String>();
        int lmi = 0;
        for(File defaultFile : pageConfigs) {
            if (defaultFile.isDirectory())
                continue;

            lastModified[lmi++] = defaultFile.lastModified();
            mklogger.info("=*=*=*=*=*=*=* MkWeb " + pageType + " Configs Start*=*=*=*=*=*=*=*=");
            mklogger.info("File: " + defaultFile.getAbsolutePath());
            mklogger.info("=            " + defaultFile.getName() + "              =");
            if (defaultFile == null || !defaultFile.exists()) {
                mklogger.error("Config file is not exists or null");
                return;
            }

            try (FileReader reader = new FileReader(defaultFile)) {
                pageJsonData = new ArrayList<MkPageJsonData>();
                JSONParser jsonParser = new JSONParser();
                JSONObject jsonObject = (JSONObject) jsonParser.parse(reader);
                JSONObject pageObject = (JSONObject) jsonObject.get("Controller");

                String controlName = pageObject.get("name").toString();
                String lastURI = pageObject.get("last_uri").toString();

                String apiPage = pageObject.get("api").toString();
                boolean isApiPage = apiPage.toLowerCase().contentEquals("yes");

                String pageDebugLevel = pageObject.get("debug").toString();
                String authorize = (!isApiPage ? pageObject.get("auth").toString() : "no");
                mklogger.debug("\n\n\nauthorize: " + authorize);

                JSONArray serviceArray = (JSONArray) pageObject.get("services");

                JSONObject pageDevice = (JSONObject) pageObject.get("device");
                ArrayList<MkDeviceData> deviceConfig = new ArrayList<>();

                if (!isApiPage) {
                    deviceConfig = getNormalPageDeviceInfo(pageDevice, lastURI, defaultFile.getName(), controlName);
                } else {
                    deviceConfig = getApiPageDeviceInfo(pageDevice, defaultFile.getName());
                }
                if(deviceConfig == null){
                    return;
                }


                if(serviceArray.size() > 0){
                    for(int i = 0; i < serviceArray.size(); i++) {
                        JSONObject serviceObject = (JSONObject) serviceArray.get(i);
                        boolean isPageStatic = false;
                        String serviceId = null;
                        String serviceType = null;
                        JSONObject serviceKinds = null;
                        String serviceParameter = null;
                        String serviceObjectType = null;
                        String serviceMethod = null;
                        String[] page_value = null;
                        try {
                            isPageStatic = serviceObject.get("page_static").toString().contentEquals("true");
                            serviceParameter = serviceObject.get("parameter_name").toString();
                            serviceObjectType = serviceObject.get("obj").toString();
                            serviceMethod = serviceObject.get("method").toString();

                            serviceKinds = (JSONObject) serviceObject.get("type");

                            serviceType = serviceKinds.get("kind").toString();
                            serviceId = serviceKinds.get("id").toString();
                        } catch (NullPointerException npe) {
                            mklogger.error("[Controller: " + controlName + "] Some service of the page doesn't have attributes. Please check the page config.");
                            return;
                        }

                        MkJsonData mkJsonData = new MkJsonData(serviceObject.get("value").toString());
                        JSONObject tempValues = null;

                        if(mkJsonData.setJsonObject()) {
                            tempValues = mkJsonData.getJsonObject();
                        }
                        if(tempValues.size() == 0) {
                            mklogger.error("[Controller: " + controlName + " | Service ID: " + serviceId+ "] Service doesn't have any value. Service must have at least one value. If the service does not include any value, please create blank one.");
                            mklogger.debug("{\"1\":\"\"}");
                            continue;
                        }
                        page_value = new String[tempValues.size()];

                        for(int j = 0; j < tempValues.size(); j++) {
                            page_value[j] = tempValues.get("" + (j+1)).toString();
                        }

                        if(isApiPage){
                            /*	 Add Index Page	*/
                            if(controlName.contentEquals("/")) {
                                mklogger.temp("[" + controlName + "] RESTful API view last_uri property must have value.", false);
                                mklogger.temp("The settings for this view controller is terminated.", false);
                                mklogger.flush("error");
                                return;
                            }
                        } else {
                            if(controlName.contentEquals("/"))
                                controlName = "";
                        }

                        MkPageJsonData curData = setPageJsonData(isPageStatic,
                                controlName,
                                lastURI,
                                serviceId,
                                serviceType,
                                pageDebugLevel,
                                deviceConfig,
                                serviceObjectType,
                                serviceMethod,
                                serviceParameter,
                                page_value,
                                isApiPage,
                                authorize);

                        printPageInfo(mklogger, curData, "info");
                        pageJsonData.add(curData);
                        if(pageType.toLowerCase().contentEquals("page")){
                            page_configs.put(controlName, pageJsonData);
                        } else if(pageType.toLowerCase().contentEquals("api page")) {
                            apiPage_configs.put(controlName, pageJsonData);
                        }
                    }
                } else if(serviceArray.size() == 0 && !isApiPage){
                    String[] page_value = new String[1];
                    page_value[0] = "";

                    /*	 Add Index Page	*/
                    if(controlName.contentEquals("/"))
                        controlName = "";
                    MkPageJsonData curData = setPageJsonData(false,
                            controlName,
                            lastURI,
                            "No Service",
                            "No Service",
                            pageDebugLevel,
                            deviceConfig,
                            "No Service",
                            "No Service",
                            null,
                            page_value,
                            false,
                            "no");

                    printPageInfo(mklogger, curData, "info");
                    pageJsonData.add(curData);
                    page_configs.put(controlName, pageJsonData);
                }
            }catch (FileNotFoundException e) {
                mklogger.error("defaultFile.getName()FileNOtFoundException: " + e.getMessage());
                e.printStackTrace();
            } catch (IOException e) {
                mklogger.error("IOException: " + e.getMessage());
                e.printStackTrace();
            } catch (ParseException e) {
                mklogger.error("ParseException : " + e.getMessage());
                e.printStackTrace();
            }
            mklogger.info("=*=*=*=*=*=*=* MkWeb " + pageType + " Configs  Done*=*=*=*=*=*=*=*=");
        }
    }

    private ArrayList<MkDeviceData> getNormalPageDeviceInfo(JSONObject pageDevice, String lastURI, String currentFileName, String controlName){
        Iterator<String> deviceConfigIter = pageDevice.keySet().iterator();
        ArrayList<MkDeviceData> deviceConfig = new ArrayList<>();
        while(deviceConfigIter.hasNext()) {
            String deviceControlName = deviceConfigIter.next();

            Object dO = pageDevice.get(deviceControlName);
            if(dO != null) {
                JSONObject deviceObject = (JSONObject) dO;

                MkDeviceData tempDevice = new MkDeviceData();
                HashMap<String, String[]> tempDeviceInfo = new HashMap<>();

                tempDevice.setControlName(deviceControlName);
                Iterator<String> iterator = deviceObject.keySet().iterator();
                while(iterator.hasNext()) {
                    String iNext = iterator.next();
                    tempDevice.setServiceName(iNext);
                    JSONObject deviceService = (JSONObject) deviceObject.get(iNext);

                    String[] tempDeviceServiceInfo = new String[device_service_filter.length];
                    for(int di = 0; di < device_service_filter.length; di++) {
                        String deviceURI = deviceService.get(device_service_filter[di]).toString();
                        tempDeviceServiceInfo[di] = deviceURI;
                        tempDeviceInfo.put(iNext, tempDeviceServiceInfo);
                        if(di == 2) {
                            if(!deviceURI.contentEquals("") && deviceURI != null) {
                                if(!lastURI.contentEquals("") && lastURI != null) {
                                    if(  deviceURI.charAt(deviceURI.length()-1) != '/' ) {
                                        deviceURI += "/";
                                    }
                                }
                            }

                            if(deviceURI.contentEquals("") || deviceURI == null) {
                                if(!lastURI.contentEquals("") && lastURI != null) {
                                    if(  lastURI.charAt(lastURI.length()-1) != '/' ) {
                                        deviceURI = "/";
                                    }
                                }
                            }

                            mklogger.debug("allow uri: " + (deviceURI + lastURI));
                            allowURI.put((deviceURI + lastURI), controlName);
                        }
                    }

                    tempDevice.setDeviceInfo(tempDeviceInfo);
                }

                if(tempDeviceInfo.get("default") == null) {
                    mklogger.temp("[" + currentFileName +"] Every view controller's device tag must include at least one platform that includes default service. (Device Tag : " + deviceControlName +")", false);
                    mklogger.temp("The settings for this view controller is terminated.", false);
                    mklogger.flush("error");
                    return null;
                }
                deviceConfig.add(tempDevice);
            }
        }

        return deviceConfig;
    }

    private ArrayList<MkDeviceData> getApiPageDeviceInfo(JSONObject pageDevice, String currentFileName){
        Iterator<String> deviceConfigIter = pageDevice.keySet().iterator();
        ArrayList<MkDeviceData> deviceConfig = new ArrayList<>();

        while(deviceConfigIter.hasNext()) {
            String deviceControlName = deviceConfigIter.next();

            Object dO = pageDevice.get(deviceControlName);
            if(dO != null) {
                JSONObject deviceObject = (JSONObject) dO;

                MkDeviceData tempDevice = new MkDeviceData();
                HashMap<String, String[]> tempDeviceInfo = new HashMap<>();

                tempDevice.setControlName(deviceControlName);	// desktop, android, ios

                Iterator<String> iterator = deviceObject.keySet().iterator();
                while(iterator.hasNext()) {
                    String iNext = iterator.next();
                    JSONObject deviceService = (JSONObject) deviceObject.get(iNext);

                    String[] tempDeviceServiceInfo = new String[device_service_filter.length];
                    for(int di = 0; di < device_service_filter.length; di++) {
                        tempDeviceServiceInfo[di] = deviceService.get(device_service_filter[di]).toString();
                        tempDeviceInfo.put(iNext, tempDeviceServiceInfo);
                    }

                    tempDevice.setDeviceInfo(tempDeviceInfo);
                }

                if(tempDeviceInfo.get("default") == null) {
                    mklogger.temp("[" + currentFileName +"] Every view controller's device tag must include at least one platform that includes default service. (Device Tag : " + deviceControlName +")", false);
                    mklogger.temp("The settings for this view controller is terminated.", false);
                    mklogger.flush("error");
                    return null;
                }
                deviceConfig.add(tempDevice);
            }
        }

        return deviceConfig;
    }

    private String getURIControl(String requestURI) {

		Iterator<String> iter = allowURI.keySet().iterator();
		mklogger.temp(TAG + "requested: " + requestURI + " and my allwed pages", false);
		while(iter.hasNext()) {
			String key = iter.next();
			mklogger.temp(key + ":" + allowURI.get(key), false);
		}
		mklogger.flush("debug");

		if(requestURI.contentEquals("/"))
		    requestURI = "";

        return allowURI.get(requestURI);
    }

    @Override
    public ArrayList<MkPageJsonData> getNormalControl(String requestURI) {
        String mkPage = getURIControl(requestURI);
        for(int i = 0; i < normalDefaultFiles.length; i++)
        {
            if(lastModified[i] != normalDefaultFiles[i].lastModified()){
                normalDefaultFiles[i].lastModified();
                setPageConfigs(normalDefaultFiles, "Page");
                mklogger.info("==============Reload Page Config files==============");
                mklogger.info("========Caused by  : different modified time========");
                mklogger.info("File: " + normalDefaultFiles[i].getName() + "|" + normalDefaultFiles[i].lastModified());
                mklogger.info("==============Reload Page Config files==============");
                break;
            }
        }

        if(mkPage == null) {
            mklogger.error(" : Input String data is null");
            return null;
        }

        if(page_configs.get(mkPage) == null)
        {
            mklogger.error(" : The control is unknown. [called control name: " + mkPage + ", uri:" + requestURI + "]");
            return null;
        }
        return page_configs.get(mkPage);
    }

    @Override
    public ArrayList<MkPageJsonData> getApiControl(String k) {
        String apiControlName = k;
        for(int i = 0; i < apiDefaultFiles.length; i++)
        {
            if(lastModified[i] != apiDefaultFiles[i].lastModified()){
                setPageConfigs(apiDefaultFiles, "API Page");
                mklogger.info("==============Reload API Page Config files==============");
                mklogger.info("========Caused by  : different modified time========");
                mklogger.info("==============Reload API Page Config files==============");
                break;
            }
        }
        mklogger.debug("k: " + k);
        if(k == null) {
            mklogger.error(" : Input String data is null");
            return null;
        }

        if(apiPage_configs.get(apiControlName) == null)
        {
            mklogger.error(" : The control is unknown. [called control name: " + k + "]");
            return null;
        }
        return apiPage_configs.get(apiControlName);
    }

    @Override
    protected MkPageJsonData setPageJsonData(boolean pageStatic, String controlName, String pageLastURI, String serviceName, String serviceType, String debugLevel, ArrayList<MkDeviceData> device, String objectType, String method, String PRM_NAME, String[] VAL_INFO, boolean isApi, String auth) {
        MkPageJsonData result = new MkPageJsonData();

        result.setPageStatic(pageStatic);
        result.setControlName(controlName);
        result.setLastURI(pageLastURI);
        result.setDebug(debugLevel);

        result.setDevice(device);

        result.setServiceName(serviceName);
        result.setServiceType(serviceType);

        result.setObjectType(objectType);
        result.setMethod(method);
        result.setParameter(PRM_NAME);
        result.setData(VAL_INFO);
        result.setAPI(isApi);

        result.setAuth(auth);

        LinkedHashMap<String, Boolean> PAGE_VALUE = null;
        PAGE_VALUE = pageValueToHashMap(VAL_INFO);
        result.setPageValue(PAGE_VALUE);
        return result;
    }
}

