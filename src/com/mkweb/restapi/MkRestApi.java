package com.mkweb.restapi;

import java.io.BufferedReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;

import com.mkweb.config.MkConfigReader;
import com.mkweb.config.MkRestApiPageConfigs;
import com.mkweb.config.MkRestApiSqlConfigs;
import com.mkweb.data.MkJsonData;
import com.mkweb.data.PageXmlData;
import com.mkweb.data.SqlXmlData;
import com.mkweb.database.MkDbAccessor;
import com.mkweb.logger.MkLogger;
import com.mkweb.security.CheckPageInfo;

/**
 * Servlet implementation class MkRestApi
 */

/*
1. 순서 정렬
2. 전체 조회
3. 응답 정리 ( 코드, 콘텐츠 타입, 실패 사유 등. )
 */

public class MkRestApi extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private MkLogger mklogger = MkLogger.Me();
	private String TAG  = "[MkRestApi]";
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
		request.setAttribute("api-method", "head");
		doTask(request, response);
	}

	protected void doOptions(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		request.setAttribute("api-method", "options");
		doTask(request, response);
	}

	protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		request.setAttribute("api-method", "put");
		doTask(request, response);
	}

	protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		request.setAttribute("api-method", "delete");
		doTask(request, response);
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setAttribute("api-method", "get");
		
		//mkweb.restapi.resource.start
		//mkweb.restapi.resource.end
		//오늘 할 거 : Get방식 주소 묶기 ( disable query string )
		
		doTask(request, response);
	}
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setAttribute("api-method", "post");
		doTask(request, response);
	}

	private void doTask(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		request.setCharacterEncoding("UTF-8");
		if(!MkConfigReader.Me().get("mkweb.restapi.use").equals("yes")) {
			response.sendError(404);
			return;
		}
		final String MKWEB_URL_PATTERN = MkConfigReader.Me().get("mkweb.restapi.urlpattern");
		String reqApiData =  request.getParameter(MkConfigReader.Me().get("mkweb.restapi.request.id"));
		String searchKey = null;
		boolean requireKey = MkConfigReader.Me().get("mkweb.restapi.usekey").contentEquals("yes") ? true : false;
		String reqToJson = null;
		boolean isDataRequestedAsJsonObject = true;
		MkJsonData mkJsonObject = new MkJsonData();
		JSONObject jsonObject = null;
		
		if(reqApiData != null) {
			mkJsonObject.setData(reqApiData);

			if(!mkJsonObject.setJsonObject()) {
				mklogger.error(TAG + " Failed to create JsonObject.");
				isDataRequestedAsJsonObject = false;
			}

			if(!isDataRequestedAsJsonObject) {
				reqToJson = mkJsonObject.stringToJsonString(reqApiData);
				mkJsonObject.setData(reqToJson);
				if(mkJsonObject.setJsonObject()) {
					jsonObject = mkJsonObject.getJsonObject();
				}else {
					mklogger.error(TAG + " Failed to create MkJsonObject. :: " + reqToJson);
				}

			}else {
				jsonObject = mkJsonObject.getJsonObject();
			}
		}

		if(jsonObject == null) {
			StringBuilder stringBuilder = new StringBuilder();

			BufferedReader br = null;
			try {
				InputStream inputStream = request.getInputStream();
				br = new BufferedReader(new InputStreamReader(inputStream));
				char[] charBuffer = new char[128];
				int bytesRead = -1;
				while((bytesRead = br.read(charBuffer)) > 0 ) {
					stringBuilder.append(charBuffer, 0, bytesRead);
				}
			}catch (IOException ex) {
				throw ex;
			}finally {
				if(br != null) {
					try {
						br.close();
					}catch(IOException ex) {
						throw ex;
					}
				}
			}
			boolean stringPass = false;
			if(stringBuilder.length() == 0) {
				stringPass = true;
				/*
				//예외
				RequestDispatcher dispatcher = this.getServletContext().getRequestDispatcher("/600.jsp");
				dispatcher.forward(request, response);
				response.setStatus(401);
				return;
				*/
			}
			if(!stringPass) {
				String rbPostData = stringBuilder.toString();

				String rbpds[] = rbPostData.split("&");

				for(int i = 0; i < rbpds.length; i++) {
					if(rbpds[i].contains(MkConfigReader.Me().get("mkweb.restapi.request.id"))) {
						reqApiData = URLDecoder.decode(rbpds[i].split("=")[1], "UTF-8");
						break;
					}
				}

				mkJsonObject.setData(reqApiData);
				if(!mkJsonObject.setJsonObject()) {
					mklogger.error(TAG + " failed to create JsonObject.");
					isDataRequestedAsJsonObject = false;
				}

				if(!isDataRequestedAsJsonObject) {
					reqToJson = mkJsonObject.stringToJsonString(reqApiData);
					mkJsonObject.setData(reqToJson);
					if(mkJsonObject.setJsonObject()) {
						jsonObject = mkJsonObject.getJsonObject();
					}else {
						mklogger.error(TAG + " Failed to create MkJsonObject. :: " + reqToJson);
						return;
					}
				}else {
					jsonObject = mkJsonObject.getJsonObject();
				}
			}
		}
		
		if(requireKey) {
			if(jsonObject != null) {
				searchKey = jsonObject.get(MkConfigReader.Me().get("mkweb.restapi.searchkey.exp")).toString();
			}else {
				//예외
				response.sendError(401);
				return;
			}
			
			if(searchKey == null) {
				//예외
				response.sendError(401);
				return;
			}
		}
		
		String requestURI = request.getRequestURI();
		Object mAttributes = request.getAttribute("api-method");
		String method = null;
		
		//페이지 유효성 검사
		//String requestURI = request.getRequestURI();
		String[] reqPage = null;
		String mkPage = null;
		reqPage = requestURI.split("/" + MKWEB_URL_PATTERN + "/");
		
		if(reqPage.length < 2) {
			//예외
			response.sendError(401);
			return;
		}
		mkPage = reqPage[1];
		
		if(mkPage.contains("/")) {
			mkPage = mkPage.split("/")[0];
		}
		
		if(mAttributes != null)
			method = mAttributes.toString().toLowerCase();

		if(method == null) {
			//예외
			return;
		}else {
			if(method.contentEquals("get")) {
				requestURI = URLDecoder.decode(requestURI, "UTF-8");
				String[] splits = requestURI.split(mkPage);
				
				if(splits.length != 2) {
					//예외
					response.sendError(401);
					return;
				}
				reqApiData = splits[1];
				String[] reqApiDatas = reqApiData.split("/");
				int size = reqApiDatas.length;
				mklogger.debug(TAG + " size : " + size);
				reqApiData = "{";
				for(int i = 1; i < size; i++) {
					if(i % 2 == 1)	//키
						reqApiData += "\"" + reqApiDatas[i] + "\":";
					else			//밸류
						reqApiData += "\"" + reqApiDatas[i] + "\"";
					
					//마지막이고 홀수면 (URL상 짝수)
					if( i == size - 1 && size % 2 == 0) {
						reqApiData += "\"" + "MK_GET_ALL" + "\"";
					}
					
					if( i < size - 1 && i % 2 == 0 )
						reqApiData += ",";
				}
				reqApiData += "}";
				
				mkJsonObject.setData(reqApiData);

				if(!mkJsonObject.setJsonObject()) {
					//예외
					mklogger.error(TAG + " Failed to create JsonObject.");
					return;
				}
				jsonObject = mkJsonObject.getJsonObject();
			}
		}
		String[] noUrlPattern = new String[reqPage.length-1];
		for(int i = 1; i < reqPage.length; i++) {
			noUrlPattern[i-1] = reqPage[i];
		}
		if(!cpi.isValidApiPageConnection(mkPage, noUrlPattern)) {
			//예외
			response.sendError(401);
			return;
		}
		if(requireKey && !isKeyValid(searchKey, mkPage)) {
			//예외
			response.sendError(401);
			return;
		}
		if(!checkMethod(request, method, mkPage)) {
			//예외
			response.sendError(400);
			return;
		}
		if(!method.contentEquals("get")) {
			if(!request.getHeader("Content-Type").toLowerCase().contains("application/json")) {
				//예외
				response.sendError(415);
				return;
			}
		}
		//리턴은 무조건 json이다.
		//ApiSql에서 Allow_Single 확인
		ArrayList<PageXmlData> apiPageInfo = MkRestApiPageConfigs.Me().getControl(mkPage);

		if(apiPageInfo == null) {
			mklogger.error(TAG + " api page info null");
			return; 
		}
		int target = -1;
		for(int i = 0; i < apiPageInfo.size(); i++) {

			if(apiPageInfo.get(i).getSql()[3].contentEquals(method)) {
				target = i;
				break;
			}
		}
		if(target == -1) {
			//예외
			return;
		}
		if(method.contentEquals("options")) {

		}else if(method.contentEquals("head")) {

		}

		PageXmlData pxData = apiPageInfo.get(target);
		SqlXmlData sqlData = MkRestApiSqlConfigs.Me().getControlService(pxData.getServiceName().split("sql.")[1]);

		if(sqlData == null) {
			//예외
			mklogger.error(TAG + " There is no SQL Data named : " + pxData.getServiceName());
			return;
		}

		ArrayList<String> sqlKey = new ArrayList<>();
		ArrayList<String> sqlColumnData = sqlData.getColumnData();
		Object jsonObjectData = null;
		if(sqlData.getAllowSingle()) {
			//이 안에 있는게 필요한 거
			for(String scData : sqlColumnData) {
				jsonObjectData = jsonObject.get(scData);
				if(jsonObjectData != null)
					sqlKey.add(scData);
			}
			//들어온 파라미터 한 개만
		}else {
			for(String scData : sqlColumnData) {
				jsonObjectData = jsonObject.get(scData);
				if(jsonObjectData == null) {
					//예외
					mklogger.error(TAG + " Allowsingle is not allowed at this page : " + pxData.getControlName());
					response.setStatus(401);
					return;
				}
				sqlKey.add(scData);
			}
		}

		if(!cpi.comparePageValueWithRequest(pxData.getData(), sqlKey, pxData.getPageStaticParams(), true)) {
			//예외
			mklogger.error(TAG + " Request Value is not authorized. Please check page config.");
			return;
		}
		//여기까지는 모든 메서드 중복되는 행위
		//조회? 생성? 삭제?

		ArrayList<MkRestApiResponse> lmrap = null;
		MkRestApiResponse mrap = null;
		Object mro = request.getAttribute("mrap");
		Object mraHash = request.getAttribute("mraHas");
		String Hash = null;
		boolean isDone = false;
		if(mraHash != null) {
			Hash = mraHash.toString();
		}
		JSONObject resultObject = null;

		if(mro != null) {
			lmrap = (ArrayList<MkRestApiResponse>) mro;
			for(MkRestApiResponse ar : lmrap) {
				if(Hash.contentEquals(ar.getHashData())) {
					if(!ar.needUpdate()) {
						mrap = ar;
						resultObject = ar.getData();
						isDone = true;
						break;
					}
				}
			}
		} else {
			lmrap = new ArrayList<>();
		}
		
		if(!isDone) {
			if(method.contentEquals("get")) {
				//조회
				resultObject = doTaskGet(pxData, sqlKey, sqlData, jsonObject, mkPage);
			}else if(method.contentEquals("post")) {
				//삽입, 갱신
				resultObject = doTaskPost(pxData, sqlKey, sqlData, jsonObject, mkPage);
			}else if(method.contentEquals("put")) {
				//조회, 삽입
				resultObject = doTaskPut(pxData, sqlKey, sqlData, jsonObject, mkPage);
			}else if(method.contentEquals("delete")) {
				//삭제
				resultObject = doTaskDelete(pxData, sqlKey, sqlData, jsonObject, mkPage);
			}
			Hash = pxData.getControlName() + sqlData.getServiceName() + method;
			mrap = new MkRestApiResponse(resultObject, lmrap.size()+1, 1, Hash);
		}
		
		mkJsonObject.printObject(resultObject);
		
		if(lmrap.size() == 0)
			lmrap.add(mrap);
		else {
			if(lmrap.size() < 3) {
				lmrap.add(mrap);
			}else {
				lmrap.remove(0);
				lmrap.add(mrap);
			}
		}
		request.setAttribute("mrap", lmrap);
		request.setAttribute("mraHash", Hash);
		//리스폰스(최초 응답, 이전, 다음 응답 기록)
		response.setStatus(200);
		response.setContentType("application/json;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		out.print(resultObject.toString());
		out.flush();
	}

	private JSONObject doTaskGet(PageXmlData pxData, ArrayList<String> sqlKey, SqlXmlData sqlData, JSONObject jsonObject, String mkPage) {
		JSONObject resultObject = null;
		MkDbAccessor DA = new MkDbAccessor();

		String service = pxData.getServiceName().split("\\.")[1];

		String befQuery = cpi.regularQuery(service); 
		String query = null;

		if(sqlData.getAllowLike()) {
			query = cpi.setApiQueryLike(befQuery, sqlKey);
		}else {
			query = cpi.setApiQuery(befQuery, sqlKey);
		}

		if(sqlKey != null) {
			DA.setPreparedStatement(query);

			DA.setRequestValue(sqlKey, jsonObject);

			ArrayList<Object> resultTest = null;

			if(sqlData.getAllowLike()) 
				resultTest = DA.executeSELLike(true);
			else
				resultTest = DA.executeSEL(true);	

			String test = "{\"" + mkPage + "\":[";
			if(resultTest != null) {
				for(int l = 0; l < resultTest.size(); l++) {
					String damn = resultTest.get(l).toString();
					damn = damn.replaceAll("=", ":");
					MkJsonData tttt = new MkJsonData(damn);
					test += damn;
					
					if(l < resultTest.size() - 1)
						test += ", ";
				}
				test += "]}";
				
				MkJsonData tttt = new MkJsonData(test);
				
				if(tttt.setJsonObject()) {
					resultObject = tttt.getJsonObject();
				}
			}else {
				//조회 데이터 없음
			}
		}

		return resultObject;
	}

	private JSONObject doTaskPost(PageXmlData pxData, ArrayList<String> sqlKey, SqlXmlData sqlData, JSONObject jsonObject, String mkPage) {
		JSONObject resultObject = null;
		MkDbAccessor DA = new MkDbAccessor();

		String service = pxData.getServiceName().split("\\.")[1];

		String befQuery = cpi.regularQuery(service); 
		String query = null;

		if(sqlData.getAllowLike()) {
			query = cpi.setApiQueryLike(befQuery, sqlKey);
		}else {
			query = cpi.setApiQuery(befQuery, sqlKey);
		}

		if(sqlKey != null) {
			DA.setPreparedStatement(query);

			DA.setRequestValue(sqlKey, jsonObject);

			ArrayList<Object> resultTest = null;

			if(sqlData.getAllowLike()) 
				resultTest = DA.executeSELLike(true);
			else
				resultTest = DA.executeSEL(true);	


			String test = "{\"" + mkPage + "\":[";
			if(resultTest != null) {
				for(int l = 0; l < resultTest.size(); l++) {
					String damn = resultTest.get(l).toString();
					damn = damn.replaceAll("=", ":");
					MkJsonData tttt = new MkJsonData(damn);
					test += damn;
					
					if(l < resultTest.size() - 1)
						test += ", ";
				}
				test += "]}";
				
				MkJsonData tttt = new MkJsonData(test);
				
				if(tttt.setJsonObject()) {
					resultObject = tttt.getJsonObject();
				}
			}else {
				//조회 데이터 없음
			}
		}

		return resultObject;
	}

	private JSONObject doTaskPut(PageXmlData pxData, ArrayList<String> sqlKey, SqlXmlData sqlData, JSONObject jsonObject, String mkPage) {
		JSONObject resultObject = null;
		MkDbAccessor DA = new MkDbAccessor();

		String service = pxData.getServiceName().split("\\.")[1];

		String befQuery = cpi.regularQuery(service); 
		String query = null;

		if(sqlData.getAllowLike()) {
			query = cpi.setApiQueryLike(befQuery, sqlKey);
		}else {
			query = cpi.setApiQuery(befQuery, sqlKey);
		}

		if(sqlKey != null) {
			DA.setPreparedStatement(query);

			DA.setRequestValue(sqlKey, jsonObject);

			ArrayList<Object> resultTest = null;

			if(sqlData.getAllowLike()) 
				resultTest = DA.executeSELLike(true);
			else
				resultTest = DA.executeSEL(true);	

			String test = "{\"" + mkPage + "\":[";
			if(resultTest != null) {
				for(int l = 0; l < resultTest.size(); l++) {
					String damn = resultTest.get(l).toString();
					damn = damn.replaceAll("=", ":");
					MkJsonData tttt = new MkJsonData(damn);
					test += damn;
					
					if(l < resultTest.size() - 1)
						test += ", ";
				}
				test += "]}";
				MkJsonData tttt = new MkJsonData(test);
				
				if(tttt.setJsonObject()) {
					resultObject = tttt.getJsonObject();
				}
			}else {
				//조회 데이터 없음
			}
		}

		return resultObject;
	}

	private JSONObject doTaskDelete(PageXmlData pxData, ArrayList<String> sqlKey, SqlXmlData sqlData, JSONObject jsonObject, String mkPage) {
		JSONObject resultObject = null;
		MkDbAccessor DA = new MkDbAccessor();

		String service = pxData.getServiceName().split("\\.")[1];

		String befQuery = cpi.regularQuery(service); 
		String query = null;

		if(sqlData.getAllowLike()) {
			query = cpi.setApiQueryLike(befQuery, sqlKey);
		}else {
			query = cpi.setApiQuery(befQuery, sqlKey);
		}

		if(sqlKey != null) {
			DA.setPreparedStatement(query);

			DA.setRequestValue(sqlKey, jsonObject);

			ArrayList<Object> resultTest = null;

			if(sqlData.getAllowLike()) 
				resultTest = DA.executeSELLike(true);
			else
				resultTest = DA.executeSEL(true);	


			String test = "{\"" + mkPage + "\":[";
			if(resultTest != null) {
				for(int l = 0; l < resultTest.size(); l++) {
					String damn = resultTest.get(l).toString();
					damn = damn.replaceAll("=", ":");
					MkJsonData tttt = new MkJsonData(damn);
					test += damn;
					
					if(l < resultTest.size() - 1)
						test += ", ";
				}
				test += "]}";
				
				MkJsonData tttt = new MkJsonData(test);
				
				if(tttt.setJsonObject()) {
					resultObject = tttt.getJsonObject();
				}
			}else {
				//조회 데이터 없음
			}
		}

		return resultObject;
	}
}
