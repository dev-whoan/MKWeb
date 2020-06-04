package com.mkweb.restapi;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mkweb.config.MkConfigReader;
import com.mkweb.config.MkPageConfigs;
import com.mkweb.config.MkRestApiPageConfigs;
import com.mkweb.data.MkJsonData;
import com.mkweb.data.PageXmlData;
import com.mkweb.database.MkDbAccessor;
import com.mkweb.logger.MkLogger;
import com.mkweb.security.CheckPageInfo;
import com.mkweb.web.PageInfo;

/**
 * Servlet implementation class MkRestApi
 */

public class MkRestApi extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private MkLogger mklogger = MkLogger.Me();
	private String TAG = "[MkRestApi]";
	private CheckPageInfo cpi = null;
	private String[] methods = {
			"post",
			"get",
			"put",
			"delete",
			"options",
			"head"
	};
    /**
     * @see HttpServlet#HttpServlet()
     */
    public MkRestApi() {
        super();
        cpi = new CheckPageInfo();
        // TODO Auto-generated constructor stub
    }
    
    private boolean checkMethod(HttpServletRequest request, String rqMethod, String mkPage) {
    	/*
    	//Control�� method�� �㿵�ǈd ��Ȯ���� �ϰ� ����Ǹ�
    	PageXmlData apiPageInfo = null;
    	if(MkRestApiPageConfigs.Me().getControl(mkPage) != null) {
    		apiPageInfo = MkRestApiPageConfigs.Me().getControl(mkPage).get(0);
    	}
    	if(apiPageInfo == null) {
    		mklogger.error(TAG + " api page info null");
    		return false;
    	}
    	
    	boolean[] allowMethods = {
    			apiPageInfo.getPost(),
    			apiPageInfo.getGet(),
    	    	apiPageInfo.getPut(),
    	    	apiPageInfo.getDelete(),
    	    	apiPageInfo.getOptions(),
    	    	apiPageInfo.getHead()
    	};
    	
    	int id = -1;
    	for(int i = 0; i < methods.length; i++) {
    		if(methods[i].equals(rqMethod)) {
    			id = i;
    			break;
    		}
    	}
    	
    	if(!allowMethods[id]) {
    		mklogger.error(TAG + " The Method is not allowed : " + rqMethod);
    		return false;
    	}
    	//Control Sql�ܿ��� Ȯ��
    	String[] sqlInfo = apiPageInfo.getSql();
    	//get�϶��� ? ���Ĵ� ��� ��û �Ķ����
    	//�� ���Ͽ� ������ .���� ������ �Ķ���ͷ� ��û SQL ID Ȯ��.
    	//�ش� SQL ID�� METHOD�� �����ϴ°�?
    	
    	
    	
    	//GET ����� �ƴ϶��
    	//�׳� Parameter �ڸ��� ��.
    	
		
		requestParams = cpi.getRequestPageParameterName(request);
		requestValues = cpi.getRequestParameterValues(request);
		*/
    	
    }
    
    private boolean isKeyValid(String key, String mkPage) {
    	if(!MkConfigReader.Me().get("mkweb.restapi.use").equals("yes"))
    		return false;
    	if(!MkRestApiPageConfigs.Me().isApiPageSet())
    		return false;
    		
    	boolean isDone = false;
    	MkRestApiGetKey mra = new MkRestApiGetKey();
		ArrayList<Object> apiKeyList = mra.GetKey();
		
		mklogger.temp(TAG + " REST Api Key has searched : " + key + " Result: " , false);
		
		if(apiKeyList != null) {
			for(int i = 0; i < apiKeyList.size(); i++) {
				HashMap<String, Object> result = new HashMap<String, Object>();
				result = (HashMap<String, Object>) apiKeyList.get(i);
				if(result.get("api_key").equals(key)) {
					mklogger.temp(" key is valid! (user_id : " + result.get("user_id") +")", false);
					mklogger.flush("info");
					isDone = true;
					break;
				}
			}	
		}else {
			mklogger.temp(" Failed to search the key! (No Key List)", false);
			mklogger.flush("warn");
		}
		
		if(!isDone) {
			mklogger.temp(" Failed to search the key! (Key is invalid)", false);
			mklogger.flush("warn");
		}
		
		return isDone;
    }

    protected void doHead(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
    	mklogger.debug(TAG + "HEAD Method");
    }

    protected void doOptions(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
    	mklogger.debug(TAG + "OPTIONS Method");
    }
    
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
    	mklogger.debug(TAG + "PUT Method");
    }
    
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
    	mklogger.debug(TAG + "DEL Method");
    }
    
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String reqApiKey = request.getParameter(MkConfigReader.Me().get("mkweb.restapi.searchkey.exp"));
		
		//������ ��ȿ�� �˻�
		String requestURI = request.getRequestURI();
		String[] reqPage = null;
		String mkPage = null;
		reqPage = requestURI.split("/");
		mkPage = reqPage[reqPage.length - 1];
		
		String[] noUrlPattern = new String[reqPage.length-1];
		for(int i = 1; i < reqPage.length; i++) {
			noUrlPattern[i-1] = reqPage[i];
		}
		
		if(!cpi.isValidApiPageConnection(mkPage, noUrlPattern)) {
			//����������
			RequestDispatcher dispatcher = this.getServletContext().getRequestDispatcher("/404.jsp");
			dispatcher.forward(request, response);
			return;
		}
		
		if(!isKeyValid(reqApiKey, mkPage)) {
			RequestDispatcher dispatcher = this.getServletContext().getRequestDispatcher("/600.jsp");
			dispatcher.forward(request, response);
			return;
		}
		
		PrintWriter out = response.getWriter();

		response.setStatus(200);
		
		response.setHeader("Content-Location", "gimojji");
		response.setHeader("Content-Type", "ko-kr");
		response.setHeader("Retry-After", "5");
		//method �˻� 
		/*
	//	MkDbAccessor DA = new MkDbAccessor();
		
		MkDbAccessor DA = new MkDbAccessor();
		
		if(!cpi.comparePageValueWithRequest(pxData.getData(), requestValues)) {
			mklogger.error(TAG + " Request Value is not authorized. Please check page config.");
			return;
		}
		
		String service = pxData.getServiceName().split("\\.")[1];
		
		String befQuery = cpi.regularQuery(service); 
		String query = cpi.setQuery(befQuery);
		
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
			
			DA.executeDML();
		}
		
		MkJsonData jo = new MkJsonData();
		PrintWriter out = response.getWriter();
		
		out.write("<html><head></head><body>");
		out.write("<br>");
		
		
		
		out.write("</body></html>");
		*/
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String reqApiKey = request.getParameter(MkConfigReader.Me().get("mkweb.restapi.searchkey.exp"));
		
		String requestURI = request.getRequestURI();
		String[] reqPage = null;
		String mkPage = null;
		reqPage = requestURI.split("/");
		mkPage = reqPage[reqPage.length - 1];
		
		String[] noUrlPattern = new String[reqPage.length-1];
		for(int i = 1; i < reqPage.length; i++) {
			noUrlPattern[i-1] = reqPage[i];
		}
		
		if(!isKeyValid(reqApiKey, mkPage)) {
			RequestDispatcher dispatcher = this.getServletContext().getRequestDispatcher("/600.jsp");
			dispatcher.forward(request, response);
			return;
		}
	}

}
