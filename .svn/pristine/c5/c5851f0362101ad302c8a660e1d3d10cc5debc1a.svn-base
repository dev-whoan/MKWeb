package com.mkweb.tag;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import com.mkweb.database.MkDbAccessor;

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
		System.out.println("커스텀 태그 호출~!");
		MkDbAccessor DA;
		if(this.obj == "list")
		{
			DA = new MkDbAccessor();
			ArrayList<Object> dbResult = new ArrayList<Object>();
			dbResult = DA.executeSEL();
			HashMap<String, Object> result = new HashMap<String, Object>();
			
			System.out.println("사이즈 : " + dbResult.size() + "\nrst:" + this.rst );
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
