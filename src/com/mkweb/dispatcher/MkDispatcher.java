package com.mkweb.dispatcher;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mkweb.abst.PageXmlData;
import com.mkweb.config.PageConfigs;
import com.mkweb.logger.MkLogger;

/**
 * Servlet implementation class testMkDispatcher
 */
public class MkDispatcher extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
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
			dispatch600(request, response);
			return;
		}
		
		String mkPage = request.getAttribute("mkPage").toString();
		
		ArrayList<PageXmlData> resultXmlData = PageConfigs.getControl(mkPage);
		
		if(resultXmlData == null || resultXmlData.size() < 1) {
			dispatch600(request, response);
			return;
		}
		
		String targetURI = resultXmlData.get(0).getDir() + "/" + resultXmlData.get(0).getPageName();
		
		MkLogger.debug(targetURI);
		
		RequestDispatcher dispatcher = this.getServletContext().getRequestDispatcher(targetURI);
		dispatcher.forward(request, response);
	}
	
	private void dispatch600(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		RequestDispatcher dispatcher = this.getServletContext().getRequestDispatcher("/600.jsp");
		dispatcher.forward(request, response);
	}

}
