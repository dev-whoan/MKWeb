package com.mkweb.tag;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import com.mkweb.abst.AbsXmlData;
import com.mkweb.config.SQLXmlConfigs;
import com.mkweb.database.MkDbAccessor;
import com.mkweb.logger.MkLogger;

public class tagSEL extends SimpleTagSupport {
	private String obj;
	private String rst = "get";
	
	//Log 하기
	public void setObj(String obj) {
		this.obj = obj;
	}
	
	public void setRst(String rst) {
		this.rst = rst;
	}
	
	public String getResultId() {
		return this.rst;
	}
	
	public void doTag() throws JspException, IOException{
		MkDbAccessor DA;
		if(this.obj == "list")
		{
			DA = new MkDbAccessor();
			ArrayList<Object> dbResult = new ArrayList<Object>();
			
			AbsXmlData xmlData = new AbsXmlData();
			//아래 "selectUser" 를 능동적으로 page와 걸어줌
			//이 때, 대상값은 rst, obj로 설정
			xmlData.setServiceName("selectUser");
			
			AbsXmlData resultXmlData = SQLXmlConfigs.getControlService(xmlData);
			
			if(resultXmlData == null)
			{
				MkLogger.error("There is no control named : " +xmlData.getServiceName().toString());
				return;
			}
			dbResult = DA.executeSEL(resultXmlData.getData().toString());
			HashMap<String, Object> result = new HashMap<String, Object>();
			
			if(dbResult.size() > 0)
			{
				for(int i = 0; i < dbResult.size(); i++)
				{
					result = (HashMap<String, Object>) dbResult.get(i);
					
					((PageContext)getJspContext()).getRequest().setAttribute(this.rst, result);
					getJspBody().invoke(null);
				}
				((PageContext)getJspContext()).getRequest().removeAttribute(this.rst);
			}else {
				return;
			}
			
			//getJspContext()).getRequest().setAttribute
			
		}else if(this.obj =="map") {
			
		}
	}
}
