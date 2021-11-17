package com.mkweb.dispatcher;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mkweb.auths.MkAuthToken;
import com.mkweb.config.MkViewConfig;
import com.mkweb.data.MkPageJsonData;
import com.mkweb.logger.MkLogger;
import com.mkweb.utils.ConnectionChecker;
import com.mkweb.config.MkConfigReader;

/**
 * Servlet implementation class testDefDispatcher
 */
@WebServlet(
	name="DefaultDispatcher",
	loadOnStartup=1,
	urlPatterns="/"
)
public class defaultDispatcher extends HttpServlet {
	private static final long serialVersionUID = 1L;
    
    private static final String TAG = "[defaultDispatcher]";
    private static final MkLogger mklogger = new MkLogger(TAG);
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
		String ipAddress = request.getHeader("X-FORWARDED-FOR");  
		if (ipAddress == null) {  
		    ipAddress = request.getRemoteAddr();  
		}

		String requestURI = request.getRequestURI();	// /main
		String mkPage = null;
		
		String hostcheck = request.getRequestURL().toString().split("://")[1];
		String host = MkConfigReader.Me().get("mkweb.web.hostname") + "/";

		if(!hostcheck.equals(host))
		{
			mkPage = requestURI;
		}else {
			mkPage = "";
		}

		if(!ConnectionChecker.isValidPageConnection(mkPage)) {
			response.sendError(404);
			return;
		}

		MkPageJsonData pages = MkViewConfig.Me().getNormalControl(mkPage).get(0);

		if(!pages.IsApiPage()){
			request.setAttribute("mkPage", mkPage);
			request.setAttribute("client-host", ipAddress);
			RequestDispatcher dispatcher = this.getServletContext().getRequestDispatcher(requestURI + ".mkw");
			dispatcher.forward(request, response);
		} else {
			mklogger.debug("cookie: " + request.getCookies().length);
			MkAuthToken.printCookies(request.getCookies());
			mklogger.info("API Page Called by " + ipAddress);
			MkViewConfig.Me().printPageInfo(mklogger, pages, "no-sql");
		}
	}
}
