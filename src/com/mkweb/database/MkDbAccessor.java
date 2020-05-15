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

public class MkDbAccessor {
	private String TAG = "[MkDbAccessor]";
	
	private Connection dbCon = null;
	private PreparedStatement psmt = null;
	
	String stmt = "";
	
	public MkDbAccessor() {
		try {
			dbCon = connectDB();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private Connection connectDB() throws SQLException{
		Connection conn = null;
		
		try {
			try {
				Class.forName("com.mysql.cj.jdbc.Driver");
			} catch (ClassNotFoundException e) {
				System.out.println(TAG + "(line: 35) ClassNotFoundException: " + e.getMessage());
			}
			
			String url = "jdbc:mysql://eugenes.iptime.org:3306/mkweb?characterEncoding=UTF-8&serverTimezone=UTC";
			conn = DriverManager.getConnection(url, "mkweb", "mkweb");
		}catch(SQLException e){
			System.out.println(TAG + "(line: 41) SQLException : " + e.getMessage());
		}catch(Exception e){
			System.out.println(TAG + " " + e.getMessage());
		}
		
		return conn;
	}
	private void setPreparedStatement(int type, ArrayList<String> args) {
		String stmt = "null";
		
		this.stmt = stmt;
	}
	//DML
	public ArrayList<Object> executeSEL() {
		ArrayList<Object> rst = new ArrayList<Object>();
		ResultSet rs = null;
		if(dbCon != null)
		{
			if(this.stmt != null)
			{
				try {
					psmt = dbCon.prepareStatement("SELECT * FROM Test");
					rs = psmt.executeQuery(); 
					
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
					        System.out.println(columnNames[i]);
					    }
					}
					HashMap<String, Object> result = null;
					rs.beforeFirst();
					
					while(rs.next()) {
						result = new HashMap<String, Object>();
						for( String name : columnNames )
						{
							result.put(name, rs.getObject(name));
							System.out.println(rs.getObject(name) + "name: " + name);
						}
						
						rst.add(result);
					}
				} catch (SQLException e) {
					System.out.println(TAG + "(line: 96~105) psmt = this.dbCon.prepareStatement(" + this.stmt + ") :" + e.getMessage());
				}finally {
					try {
						if(dbCon != null)
							dbCon.close();
						if(psmt != null)
							psmt.close();
						if(rs != null)
							rs.close();
						
						return rst;
					}catch(Exception e)
					{
						System.out.println(TAG + "(line: 107~110): " + e.getMessage());
					}
				}
			}else {
				System.out.println(TAG + "(line:93) NullPointerException: stmt is null object");
				return null;
			}
			
		}else {
			System.out.println(TAG + "(line:91) NullPointerException: dbCon is null object");
			return null;
		}
		return rst;
	}
}
