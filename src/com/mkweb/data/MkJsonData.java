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

	private JSONObject isValidDataForJson(String data) {
		try {
			mklogger.debug(TAG + data);

			JSONObject jo = new JSONObject();
			JSONParser parser = new JSONParser();
			Object obj = parser.parse(data);
			jo = (JSONObject) obj;
			
			return jo;
		} catch (ParseException e) {
			mklogger.error(TAG + " ParseException:: " + e + " Given data is not valid for JSONObject.\n" + data);
		} catch(NullPointerException e2) {
			mklogger.error(TAG + " NullPoitnerException:: " + e2 + "\nGiven data is not valid for JSONObject.\n" + data);
		}
		return null;
	}

	public boolean setJsonObject() {
		if(this.data == null) {
			mklogger.error(TAG + " No given data.");
			return false;
		}

		if((jsonObject = isValidDataForJson(this.data)) == null)
			return false;

		return true;
	}
	public JSONObject getJsonObject() {	return this.jsonObject;	}
	public JSONArray getJsonArray() {	return this.jsonArray;	}
	public String getData() {	return this.data;	}
	public void setData(String data) {	this.data = data;	}
}
