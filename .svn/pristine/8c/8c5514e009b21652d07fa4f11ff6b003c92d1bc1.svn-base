package com.mkweb.dispatcher;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mkweb.config.PageConfigs;
import com.mkweb.data.PageXmlData;
import com.mkweb.logger.MkLogger;

/**
 * Servlet implementation class testDefDispatcher
 */
public class defaultDispatcher extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public defaultDispatcher() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String requestURI = request.getRequestURI();	// /main
		String[] reqPage = requestURI.split("/");
		String mkPage = reqPage[reqPage.length - 1];
		
		if(!isValidPageConnection(mkPage, reqPage, mkPage)) {
			//에러페이지
			RequestDispatcher dispatcher = this.getServletContext().getRequestDispatcher("/404.jsp");
			dispatcher.forward(request, response);
			return;
		}
		
		request.setAttribute("mkPage", mkPage);
		RequestDispatcher dispatcher = this.getServletContext().getRequestDispatcher(requestURI + ".mkw");
		dispatcher.forward(request, response);
	}
	
	private boolean isValidPageConnection(String requestControlName, String[] requestDir, String mkPage) {
		ArrayList<PageXmlData> resultXmlData = PageConfigs.Me().getControl(requestControlName);
		
		if(resultXmlData == null || resultXmlData.size() < 1)
			return false;
		PageXmlData xmlData = resultXmlData.get(0);
		/*
		 * 오직허용: log_dir + page control name
		 * requestDir = URI / 자른거.
		 * mkPage = request page control name
		 */
		String AllowPath = xmlData.getLogicalDir();
		String userLogicalDir = "";
		
		for(int i = 1; i < requestDir.length-1; i++) 
			userLogicalDir += "/" + requestDir[i];
		
		if(userLogicalDir.equals(""))
			userLogicalDir = "/";
		
		String c1 = userLogicalDir + mkPage;
		String c2 = AllowPath + xmlData.getControlName();
		
		if(!c1.equals(c2)){
			return false;
		}
		
		return true;
	}

}
