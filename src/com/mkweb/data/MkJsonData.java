package com.mkweb.data;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.mkweb.logger.MkLogger;

public class MkJsonData {
	private JSONObject jsonObject = null;
	private JSONArray jsonArray = null;
	private String data = null;

	private String TAG = "[MkJsonData]";
	private MkLogger mklogger = MkLogger.Me();

	public MkJsonData() {
		data = null;
		jsonObject = null;
		jsonArray = null;
	}

	public MkJsonData(String data) {
		this.data = data;
		jsonObject = null;
		jsonArray = null;
	}

	private boolean isValidDataForJson(String data) {
		try {
			return (JSONObject) new JSONParser().parse(data) != null;
		} catch (ParseException e) {
			mklogger.error(TAG + " Given data is not valid for JSONObject.\n" + data);
		}
		return false;
	}

	public boolean setJsonObject() {
		boolean isDone = false;
		if(this.data == null) {
			mklogger.error(TAG + " No given data.");
			return false;
		}

		if(!isValidDataForJson(data)) 
			return false;
		
		  
		JSONParser parser = new JSONParser();
		Object obj;
		try {
			obj = parser.parse(data);
		} catch (ParseException e) {
			mklogger.error(TAG + " Given data is not valid for JSONObject." + e);
			return false;
		}
		jsonObject = (JSONObject) obj;
		return true;
	}
	public JSONObject getJsonObject() {	return this.jsonObject;	}
	public JSONArray getJsonArray() {	return this.jsonArray;	}
	public String getData() {	return this.data;	}
	public void setData(String data) {	this.data = data;	}
}
