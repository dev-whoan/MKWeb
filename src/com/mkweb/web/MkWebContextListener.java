package com.mkweb.web;

import java.io.File;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.mkweb.abst.AbsXmlData;
import com.mkweb.config.PageConfigs;
import com.mkweb.config.SQLXmlConfigs;
import com.mkweb.logger.MkLogger;

public class MkWebContextListener implements ServletContextListener {

	@Override
	public void contextDestroyed(ServletContextEvent event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void contextInitialized(ServletContextEvent event) {
		// TODO Auto-generated method stub
		String sqlConfigsUri = event.getServletContext().getInitParameter("MKWeb.SqlConfigs");
		String MkConfigureUri = event.getServletContext().getInitParameter("MKWeb.Configs");
		String pageConfigsUri = event.getServletContext().getInitParameter("MKWeb.PageConfigs");
		
		/*
		 * Setting Mk Default Configure
		 */
		File config_mkweb = new File(new File(event.getServletContext().getRealPath("/")), MkConfigureUri);
		MkLogger.setLogConfig(config_mkweb);
		
		/*
		 * Setting SQL
		 */
		File config_sql = new File(new File(event.getServletContext().getRealPath("/")), sqlConfigsUri);
		SQLXmlConfigs.setSqlConfigs(config_sql);
		
		/*
		 * Setting Pages
		 */
		File config_page_dir = new File(new File(event.getServletContext().getRealPath("/")), pageConfigsUri);
		
		File[] config_pages = config_page_dir.listFiles();
	
		PageConfigs.setPageConfigs(config_pages);
		
	}

}
