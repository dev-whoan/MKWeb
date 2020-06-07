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
    	
    	ArrayList<PageXmlData> apiPageInfo = null;
    	if(MkRestApiPageConfigs.Me().getControl(mkPage) != null) {
    		apiPageInfo = MkRestApiPageConfigs.Me().getControl(mkPage);
    	}
    	
    	if(apiPageInfo == null) {
    		mklogger.error(TAG + " api page info null");
    		return false; 
    	}
    	
    	if(!apiPageInfo.get(0).isMethodAllowed(rqMethod)) { 
    		mklogger.error(TAG + " The request method is not allowed : " + rqMethod);
			return false;
		}
    	
    	PageXmlData pageInfo = null;
    	for(int i = 0; i < apiPageInfo.size(); i++) {
    		if(apiPageInfo.get(i).getSql()[3].equals(rqMethod)) {
    			pageInfo = apiPageInfo.get(i);
    			break;
    		}
    	}
    	
    	if(pageInfo == null) {
    		mklogger.error(TAG + " No Service is allowed for request method : " + rqMethod);
    		return false;
    	}
    	return true;
    	
    	//get일때는 ? 이후는 모두 요청 파라미터
    	//에 대하여 최초의 .으로 좌측이 파라미터로 요청 SQL ID 확인.
    	//해당 SQL ID가 METHOD를 지원하는가?
    	
    	
    	
    	//GET 방식이 아니라면
    	//그냥 Parameter 자르면 됨.
    	
		
	//	requestParams = cpi.getRequestPageParameterName(request);
	//	requestValues = cpi.getRequestParameterValues(request);
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
    	doTask(request, response);
    }

    protected void doOptions(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
    	doTask(request, response);
    }
    
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
    	doTask(request, response);
    }
    
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
    	doTask(request, response);
    }
    
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doTask(request, response);
	}
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doTask(request, response);
	}

	private void doTask(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
    	String reqApiKey = request.getParameter(MkConfigReader.Me().get("mkweb.restapi.searchkey.exp"));
		
		//페이지 유효성 검사
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
			//에러페이지
			RequestDispatcher dispatcher = this.getServletContext().getRequestDispatcher("/404.jsp");
			dispatcher.forward(request, response);
			return;
		}
		
		if(!isKeyValid(reqApiKey, mkPage)) {
			RequestDispatcher dispatcher = this.getServletContext().getRequestDispatcher("/600.jsp");
			dispatcher.forward(request, response);
			return;
		}
		
		if(!checkMethod(request, "get", mkPage)) {
			RequestDispatcher dispatcher = this.getServletContext().getRequestDispatcher("/600.jsp");
			dispatcher.forward(request, response);
			return;
		}
		
		if(!request.getHeader("Content-Type").toLowerCase().contentEquals("application/json")) {
			response.setStatus(415);
			return;
		}
		//application/json 이 아니면 거절
    }
}
