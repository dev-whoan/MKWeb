package com.mkweb.web;

import java.io.File;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.mkweb.config.MkConfigReader;
import com.mkweb.config.PageConfigs;
import com.mkweb.config.SQLXmlConfigs;
import com.mkweb.data.AbsXmlData;
import com.mkweb.logger.MkLogger;

public class MkWebContextListener implements ServletContextListener {

	@Override
	public void contextDestroyed(ServletContextEvent event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void contextInitialized(ServletContextEvent event) {
		// TODO Auto-generated method stub
		String mkwebProperties = event.getServletContext().getInitParameter("MKWeb.Properties");
		String sqlConfigsUri = event.getServletContext().getInitParameter("MKWeb.SqlConfigs");
		String MkLoggerUri = event.getServletContext().getInitParameter("MKWeb.LoggerConfigs");
		String pageConfigsUri = event.getServletContext().getInitParameter("MKWeb.PageConfigs");
		
		/*
		 * Setting Mk Logger Configure
		 */
		File mkweb_logger_config = new File(new File(event.getServletContext().getRealPath("/")), MkLoggerUri);
		MkLogger ml = MkLogger.Me();
		ml.setLogConfig(mkweb_logger_config);
		
		/*
		 * Read Mk Configs
		 */
		File mkweb_properties = new File(new File(event.getServletContext().getRealPath("/")), mkwebProperties);
		MkConfigReader mcr = MkConfigReader.Me();
		mcr.setMkConfig(mkweb_properties);
		//		MkConfigReader.setMkConfig(mkweb_properties);
		
		/*
		 * Setting SQL
		 */
		File mkweb_sql_config = new File(new File(event.getServletContext().getRealPath("/")), sqlConfigsUri);
		SQLXmlConfigs sxc = SQLXmlConfigs.Me();
		sxc.setSqlConfigs(mkweb_sql_config);
		
		/*
		 * Setting Pages
		 */
		File mkweb_page_config = new File(new File(event.getServletContext().getRealPath("/")), pageConfigsUri);
		File[] config_pages = mkweb_page_config.listFiles();
		
		PageConfigs pc = PageConfigs.Me();
		pc.setPageConfigs(config_pages);
	}
}
