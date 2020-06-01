package com.mkweb.web;

import java.io.IOException;

import java.util.ArrayList;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mkweb.config.MkConfigReader;
import com.mkweb.config.PageConfigs;
import com.mkweb.data.PageXmlData;
import com.mkweb.database.MkDbAccessor;
import com.mkweb.logger.MkLogger;
import com.mkweb.security.CheckPageInfo;

/**
 * Servlet implementation class MkReceiveFormData
 */

public class MkReceiveFormData extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private MkLogger mklogger = MkLogger.Me();
	private String TAG = "[MkReceiveFormData]";
    private PageInfo pi = null;
    private PageXmlData pxData = null;
    private String requestParams = null;
    private ArrayList<String> requestValues = null;
    private CheckPageInfo cpi = null;
    public MkReceiveFormData() {
        super();
        cpi = new CheckPageInfo();
    }
	
	private PageInfo getPageControl(String url) {
		String[] requestUriList = url.split("/");
		String mkPage = requestUriList[requestUriList.length - 1];
		
		if(mkPage.equals(MkConfigReader.Me().get("mkweb.web.hostname"))) {
			mkPage = "";
		}
		
		return new PageInfo(mkPage);
	}
    
    private boolean checkMethod(HttpServletRequest request, String rqMethod, String rqPageURL) {

    	if(!pi.getPageSqlInfo().get(0)[3].toString().toLowerCase().equals(rqMethod))
    		return false;
    	
		String hostCheck = rqPageURL.split("://")[1];
		String host = MkConfigReader.Me().get("mkweb.web.hostname");
		
    	if(host == null) {
    		mklogger.error(TAG + " Hostname is not set. You must set hostname on configs/MkWeb.conf");
    		return false;
    	}
    	host = host + "/";
    	String requestURI = rqPageURL.split(MkConfigReader.Me().get("mkweb.web.hostname"))[1];
		String[] reqPage = null;
		String mkPage = null;

		mklogger.debug(rqPageURL);
		mklogger.debug(host);
	
		if(!hostCheck.equals(host))
		{
			reqPage = requestURI.split("/");
			mkPage = reqPage[reqPage.length - 1];
		}else {
			reqPage = null;
			mkPage = "";
		}

		if(!cpi.isValidPageConnection(mkPage, reqPage)) {
			mklogger.error(TAG + " checkMethod: Invalid Page Connection. ");
			return false;
		}
    	
		if(pi == null || !pi.isSet()) {
			mklogger.error(TAG + " PageInfo is not set!");
			return false;
		}
		
		requestParams = cpi.getRequestPageParameterName(request);
		requestValues = cpi.getRequestParameterValues(request);
		
		ArrayList<PageXmlData> pal = PageConfigs.Me().getControl(mkPage);
		for(PageXmlData px : pal) {
			if(px.getParameter().equals(requestParams)) {
				pxData = px;
				break;
			}
		}
		
    	return (pxData != null ? true : false);
    }
    
    private void doTask(HttpServletRequest request, HttpServletResponse response) {
    	MkDbAccessor DA = new MkDbAccessor();
		
		mklogger.debug(pxData.getData());
	
		if(!cpi.comparePageValueWithRequest(pxData.getData(), requestValues)) {
			mklogger.error(TAG + " Request Value is not authorized. Please check page config.");
			return;
		}
		
		String service = pxData.getServiceName().split("\\.")[1];
		
		String befQuery = cpi.regularQuery(service); 
		String query = cpi.setQuery(befQuery);
		
		mklogger.debug(befQuery);
		mklogger.debug(query);
		
		if(requestValues != null) {
			DA.setPreparedStatement(query);
			
			String[] reqs = new String[requestValues.size()];
			String tempValue = "";
			for(int i = 0; i < reqs.length; i++) {
				tempValue = request.getParameter(requestParams + "." + requestValues.get(i));
				reqs[i] = tempValue;
			}
			tempValue = null;
			DA.setRequestValue(reqs);
			reqs = null;
			
			DA.executeInsert();
		}
    }
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String refURL = request.getHeader("Referer");
		pi = getPageControl(refURL);
		if(!checkMethod(request, "get", request.getHeader("Referer"))) {
			mklogger.error("»ç¶Ç¹ä");
			RequestDispatcher dispatcher = this.getServletContext().getRequestDispatcher("/500.jsp");
			dispatcher.forward(request, response);
			return;
		}
		
		doTask(request, response);
	}
    
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String refURL = request.getHeader("Referer");
		pi = getPageControl(refURL);
		if(!checkMethod(request, "post", request.getHeader("Referer"))) {
			
			return;
		}
		doTask(request, response);
	}
}
