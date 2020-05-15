package com.mkweb.logger;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MkLogger {
	private String msg = null;
	private String path = "/testhdev/webapps/log/logger.log";
	private int option = -1;
	
	private String TAG = "[MkLogger]";
	private String dfm = null;
	private String DEFAULT_TAG = "";
	public int OPT_FLUSH = 1001;
	public int OPT_NO_FLUSH = 1002;
	
	public MkLogger(String msg, int option) {
		Date d = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		DEFAULT_TAG = "[MkLogger-ServerName]("+dateFormat.format(d)+")]";
		
		if(option == OPT_FLUSH) {
			flush();
		}
		
		this.msg = DEFAULT_TAG + msg;
	}
	
	public MkLogger() {
		Date d = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		DEFAULT_TAG = "[MkLogger-ServerName]("+dateFormat.format(d)+")]";
		
		this.msg = DEFAULT_TAG;
	}
	
	public void in(String msg, int option) {
		if(option == OPT_FLUSH) {
			flush();
			this.msg = DEFAULT_TAG;
		}
		this.msg += msg;
	}
	
	public void flush() { Log(); }
	public void clear() { this.msg = ""; }
	
	private void Log(){
		
		File file = new File(path);
		try {
			BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file, true));
			bufferedWriter.write(msg);
			bufferedWriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println(TAG + " function Logging() : ");
			e.printStackTrace();
		}
		
	}
}
