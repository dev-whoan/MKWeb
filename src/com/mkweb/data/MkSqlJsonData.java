package com.mkweb.data;

import java.util.HashMap;

public class MkSqlJsonData extends AbsJsonData {
	private String db = null;
	private boolean allowSingle = false;
	private boolean allowLike = false;
	private String debugLevel = null;
	private String[] rawSQL = null;
	private HashMap<String, Object> tableData = null;

	private String[] parameter = null;

	/* api */
	private boolean isApi = false;
	private String[] condition = null;

	public String getDB() { return this.db; }
	public boolean getAllowSingle() {	return this.allowSingle;	}
	public boolean getAllowLike() {	return this.allowLike;	}
	public String getDebugLevel() {	return this.debugLevel;	}
	public String[] getParameters() {	return this.parameter;	}

	public boolean IsApiSql() {	return this.isApi;	}
	public String[] getCondition() {	return this.condition;	}
	public String[] getRawSql() {	return this.rawSQL;	}
	public HashMap<String, Object> getTableData(){	return this.tableData;	}

	public void setDB(String db) { this.db = db;	}
	public void setAllowSingle(String as) {	this.allowSingle = (as.equals("yes") ? true : false);	}
	public void setAllowLike(String al) {	this.allowLike = (al.equals("yes") ? true : false);	}
	public void setDebugLevel(String dl) {	this.debugLevel = dl;	}
	public void setParameters(String[] parameter){	this.parameter = parameter;	}
	public void setRawSql(String[] rs) {		this.rawSQL = rs;		}
	public void setTableData(HashMap<String, Object> tableData) {	this.tableData = tableData;	}

	/* api */
	public void setApiSQL(boolean ias) {	this.isApi = ias;	}
	public void setCondition(String[] condition) {	this.condition = condition;	}
}