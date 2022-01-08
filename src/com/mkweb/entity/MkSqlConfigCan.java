package com.mkweb.entity;

import java.io.File;
import java.util.HashMap;

import com.mkweb.data.MkSqlJsonData;

public abstract class MkSqlConfigCan extends MkSqlJsonData{
	public abstract Object getControl(String controlName, boolean isApi);
	public abstract Object getControlByServiceName(String serviceName, boolean isApi);
	public abstract void setSqlConfigs(File[] sqlConfigs, String typeName);
	public abstract void printSqlInfo(MkSqlJsonData jsonData, String type, boolean isApi);

	protected abstract String[] createSQL(String[] befQuery, HashMap<String, Object> tableData, boolean isApi);
}
