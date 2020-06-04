package com.mkweb.dispatcher;

import java.io.IOException;
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
import com.mkweb.data.PageXmlData;
import com.mkweb.logger.MkLogger;
import com.mkweb.restapi.MkRestApiGetKey;

/**
 * Servlet implementation class testMkDispatcher
 */
@WebServlet(
	name="MkWebDispatcher",
	loadOnStartup=1,
	urlPatterns="*.mkw"
)
public class MkDispatcher extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private String TAG = "[MkDispatcher]";
	private MkLogger mklogger = MkLogger.Me();
    /**
     * @see HttpServlet#HttpServlet()
     */
    public MkDispatcher() {
        super();
        // TODO Auto-generated constructor stub
    }
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String requestURI = request.getRequestURI();
		Object o = request.getAttribute("mkPage");
		if(o == null) {
			mklogger.error("Request URI is invalid. ( Unauthorzied connection [" + requestURI + "] )");
			dispatch(request, response, "/600.html");
			return;
		}
		String mkPage = request.getAttribute("mkPage").toString();
		ArrayList<PageXmlData> resultXmlData = MkPageConfigs.Me().getControl(mkPage);
		
		if(resultXmlData == null || resultXmlData.size() < 1) {
			mklogger.error("requested URI : " + requestURI);
			dispatch(request, response, "/600.jsp");
			return;
		}
		
		String targetURI = PageXmlData.getAbsPath() + resultXmlData.get(0).getDir() + "/" + resultXmlData.get(0).getPageName();
		request.setAttribute("mkPage", mkPage);
		dispatch(request, response, targetURI);
		
		mklogger.info(TAG + "Page Called");
		MkPageConfigs.Me().printPageInfo(resultXmlData.get(0), "no-sql");
	}
	
	private void dispatch(HttpServletRequest request, HttpServletResponse response, String URI) throws ServletException, IOException {
		RequestDispatcher dispatcher = this.getServletContext().getRequestDispatcher(URI);
		dispatcher.forward(request, response);
	}

}
