package com.aspire.api.automationUtil;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Connect2DB {

	private String driver, url, SQLQuery;	
	
	public String GetRandomid(String field){
		String result = "";
		
		try {
			Class.forName(driver);
			Connection conn = DriverManager.getConnection(url);
			Statement statement = conn.createStatement();
			String sqlQuery = SQLQuery.replace(Constants.FIEDLS_TOKEN, field);
			ResultSet rs = statement.executeQuery(sqlQuery);
			while (rs.next()) {					
				result =  rs.getString(field).trim(); 				
			}
			conn.close();
		
			return result;
			
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
		
	}


	public Connect2DB() {
		url = EnvirommentManager.getInstance().getProperty(Constants.CONFIG_FILE_CONNETION_STRING);
		driver = EnvirommentManager.getInstance().getProperty(Constants.CONFIG_FILE_DRIVER);
	}
	public Connect2DB(String contentType) {
		url = EnvirommentManager.getInstance().getProperty(Constants.CONFIG_FILE_CONNETION_STRING);
		driver = EnvirommentManager.getInstance().getProperty(Constants.CONFIG_FILE_DRIVER);
		SQLQuery = EnvirommentManager.getInstance().getProperty(contentType);
	}
	private boolean dbConnect(String Query) {
		boolean returnedValue = false;
		try {

			Class.forName(driver);
			Connection conn = DriverManager.getConnection(url);
			Statement statement = conn.createStatement();
			ResultSet rs = statement.executeQuery("Select Headline , Status , Body from Worldnow.dbo.story where storyno = 24746783");

			if (rs.next()) {
				returnedValue = true;
			} else {
				returnedValue = false;
			}
			conn.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return returnedValue;
	} 

	/**
	 * Checks if the query returns data from DB
	 * 
	 * @param Key
	 * @return
	 */
	public boolean CheckDataInDB(String Key) {
		return dbConnect(EnvirommentManager.getInstance().getProperty(Key));
	}
	/**
	 * Get a random ID from DB
	 * 
	 * @param Key
	 * @return
	 */
	public Map<String, String>  GetDateFromQuery(String fields,List<String>  QueriesTable) {
		Map<String, String>  result = new HashMap<String, String>();
		result = GetDBData("SELECT  TOP 1 " + fields + " FROM Worldnow.dbo.story where status like 'L' and affiliateno = 6 ORDER BY NEWID()", QueriesTable); 
		return result;
	}
	
	
	public List<String>  GetListDate(String Query) {
		List<String>  result = new ArrayList <String>();
		result = GetListData(Query); 
		return result;
	}
	
	public String ExtractStoryID(List<String>  data){	
		return data.get(0).toString();
	}
	/**
	 * Returns data form DB in a Json format
	 * 
	 * @param Query
	 * @return Json
	 */
	public Map<String, String> GetDBData(String Query,List<String> Columns ) {
		Map<String, String>  Result = new HashMap<String, String>();
		try {
			Class.forName(driver);
			Connection conn = DriverManager.getConnection(url);
			Statement statement = conn.createStatement();
			ResultSet rs = statement.executeQuery(Query);
			while (rs.next()) {	
				for (int i = 0; i < Columns.size(); i++)
				Result.put(Columns.get(i), rs.getString(Columns.get(i)).trim()); 				
			}
			conn.close();
		
			return Result;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	public List<String> GetListData(String Query) {
		List<String>  Result = new ArrayList<String>();
		try {
			Class.forName(driver);
			Connection conn = DriverManager.getConnection(url);
			Statement statement = conn.createStatement();
			ResultSet rs = statement.executeQuery(Query);
			while (rs.next()) {	
				//for (int i = 0; i < Columns.size(); i++)
				//Result.put(Columns.get(i), rs.getString(Columns.get(i)).trim()); 				
			}
			conn.close();
		
			return Result;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	public String GetDBData(String Query) {
		try {
			String Json = "{";
			Class.forName(driver);
			Connection conn = DriverManager.getConnection(url);
			Statement statement = conn.createStatement();
			ResultSet rs = statement.executeQuery(Query);

			while (rs.next()) {
				for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
					Json += "\""
							+ EnvirommentManager.getInstance().getProperty(
									rs.getMetaData().getColumnName(i)).trim()
							+ "\":\"" + rs.getString(i).trim() + "\",";
				}
				Json = Json.substring(0, Json.length() - 1) + ",";
			}
			conn.close();
			Json = Json.substring(0, Json.length() - 1) + "}";
			return Json;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	public String GetDBValue(String Query) {
		try {
			String Json = "";
			Class.forName(driver);
			Connection conn = DriverManager.getConnection(url);
			Statement statement = conn.createStatement();
			ResultSet rs = statement.executeQuery(Query);

			while (rs.next()) {
				for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
					Json =rs.getString(i).trim();
				}
			}
			conn.close();

			return Json;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
