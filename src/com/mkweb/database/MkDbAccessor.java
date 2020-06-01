package com.mkweb.database;

import java.sql.Connection;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.mkweb.config.MkConfigReader;
import com.mkweb.logger.MkLogger;

public class MkDbAccessor {
	//로그 만들기
	
	private Connection dbCon = null;
	private String psmt = null;
	private MkLogger mklogger = MkLogger.Me();
	private ArrayList<String> reqValue = null;
	private String[] reqValueArr = null;
	private String TAG = "[MkDbAccessor]";
	
	public MkDbAccessor() {
		try {
			dbCon = connectDB();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void setPreparedStatement(String qr) {
		this.psmt = qr;
	}
	
	public void setRequestValue(ArrayList<String> arr) {
		reqValue = new ArrayList<>();
		for(int i = 0; i < arr.size(); i++) 
			reqValue.add(arr.get(i));
	}
	
	public void setRequestValue(String[] arr) {
		reqValueArr = new String[arr.length];
		for(int i = 0; i < reqValueArr.length; i++) 
			reqValueArr[i] = arr[i];
	}
	
	private Connection connectDB() throws SQLException{
		Connection conn = null;
		
		try {
			try {
				Class.forName("com.mysql.cj.jdbc.Driver");
			} catch (ClassNotFoundException e) {
				mklogger.error("(connectDB) ClassNotFoundException: " + e.getMessage());
			}
			
			String url = "jdbc:mysql://" + MkConfigReader.Me().get("mkweb.db.hostname") + ":" + MkConfigReader.Me().get("mkweb.db.port") + "/" + MkConfigReader.Me().get("mkweb.db.database")+ "?" + "characterEncoding=UTF-8&serverTimezone=UTC";
			conn = DriverManager.getConnection(url, MkConfigReader.Me().get("mkweb.db.id"), MkConfigReader.Me().get("mkweb.db.pw"));
		}catch(SQLException e){
			mklogger.error("(connectDB) SQLException : " + e.getMessage());
		}catch(Exception e){ 
			mklogger.error(" " + e.getMessage());
		}
		
		return conn;
	}

	private void queryLog(String query) {
		query = query.trim();
		String queryMsg = "";
		
		String[] queryBuffer = query.split("\n");
		
		for (int i = 0; i < queryBuffer.length; i++) {
			String tempQuery = queryBuffer[i].trim();
			queryMsg += "\n\t\t\t\t\t" + tempQuery;
		}
		
		mklogger.info(queryMsg);
	}
	
	//DML
	public ArrayList<Object> executeSEL(){
		ArrayList<Object> rst = new ArrayList<Object>();
		ResultSet rs = null;
		
		if(dbCon != null)
		{
			if(this.psmt != null)
			{
				try {
					PreparedStatement prestmt;
					prestmt = dbCon.prepareStatement(this.psmt);
					
					if(reqValue != null) {
						for(int i = 0; i < reqValue.size(); i++) {
							prestmt.setString((i+1), reqValue.get(i));
						}
					}else {
						if(reqValueArr != null) {
							for(int i = 0; i < reqValueArr.length; i++)
								prestmt.setString((i+1), reqValueArr[i]);
						}
					}
					rs = prestmt.executeQuery(); 
					
					ResultSetMetaData rsmd; 
					int columnCount;
					String columnNames[];
					if(!rs.next()) {
						return null;
					}else {
						rsmd = rs.getMetaData();
					    columnCount = rsmd.getColumnCount();
					    columnNames = new String[columnCount];
					    for(int i=0; i < columnCount; i++) {
					        columnNames[i] = rsmd.getColumnName(i+1); 
					    }
					}
					HashMap<String, Object> result = null;
					rs.beforeFirst();
					
					while(rs.next()) {
						result = new HashMap<String, Object>();
						for( String name : columnNames )
						{
							result.put(name, rs.getObject(name));
						}
						
						rst.add(result);
					}
					
					if(dbCon != null)
						dbCon.close();
					if(prestmt != null)
						prestmt.close();
					if(rs != null)
						rs.close();
				} catch (SQLException e) {
					mklogger.error( "(executeSEL) psmt = this.dbCon.prepareStatement(" + this.psmt + ") :" + e.getMessage());
				}
			}
		}
		return rst;
	}
	
	public ArrayList<Object> executeSELLike(){
		ArrayList<Object> rst = new ArrayList<Object>();
		ResultSet rs = null;
		
		if(dbCon != null)
		{
			if(this.psmt != null)
			{
				try {
					PreparedStatement prestmt;
					prestmt = dbCon.prepareStatement(this.psmt);
					
					if(reqValue != null) {
						for(int i = 0; i < reqValue.size(); i++) 
							prestmt.setString((i+1), reqValue.get(i));
					}else {
						if(reqValueArr != null) {
							for(int i = 0; i < reqValueArr.length; i++)
								prestmt.setString((i+1), "%" + reqValueArr[i] + "%");
						}
					}
					
					rs = prestmt.executeQuery(); 
					
					ResultSetMetaData rsmd; 
					int columnCount;
					String columnNames[];
					
					if(!rs.next()) {
						return null;
					}else {
						rsmd = rs.getMetaData();
					    columnCount = rsmd.getColumnCount();
					    columnNames = new String[columnCount];
					    for(int i=0; i < columnCount; i++) {
					        columnNames[i] = rsmd.getColumnName(i+1); 
					    }
					}
					HashMap<String, Object> result = null;
					rs.beforeFirst();
					
					while(rs.next()) {
						result = new HashMap<String, Object>();
						for( String name : columnNames )
						{
							result.put(name, rs.getObject(name));
						}
						
						rst.add(result);
					}
					
					if(dbCon != null)
						dbCon.close();
					if(prestmt != null)
						prestmt.close();
					if(rs != null)
						rs.close();
				} catch (SQLException e) {
					mklogger.error( "(executeSELLike) psmt = this.dbCon.prepareStatement(" + this.psmt + ") :" + e.getMessage());
				}
			}
		}
		return rst;
	}
	
	public int executeInsert() {
		int result = 0;
		
		if(dbCon != null)
		{
			if(this.psmt != null)
			{
				try {
					PreparedStatement prestmt;
					prestmt = dbCon.prepareStatement(this.psmt);
					
					if(reqValue != null) {
						for(int i = 0; i < reqValue.size(); i++) {
							prestmt.setString((i+1), reqValue.get(i));
						}
					}else {
						if(reqValueArr != null) {
							for(int i = 0; i < reqValueArr.length; i++)
								prestmt.setString((i+1), reqValueArr[i]);
						}
					}
					
					result = prestmt.executeUpdate();
					
					if(dbCon != null)
						dbCon.close();
					if(prestmt != null)
						prestmt.close();
					
				} catch (SQLException e) {
					mklogger.error( "(executeInsert) psmt = this.dbCon.prepareStatement(" + this.psmt + ") :" + e.getMessage());
				}
			}
		}
		
		return result;
	}
}
