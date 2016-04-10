package jo.aspire.api.automationUtil;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.formula.functions.Columns;

import jo.aspire.generic.EnvirommentManager;

public class DBhelper {

	/**
	   * @param MySQL url = jdbc:mysql://hostname/ databaseName
	   * @param ORACLE url = jdbc:oracle:thin:@hostname:port Number:databaseName	  
	   */
	
	private String driver, url, SQLQuery , DBUserName , DBPassWord;	
	
	
	  
	public DBhelper(String ConfigFileConnectionString,String ConfigFileDriver) {
		url = EnvirommentManager.getInstance().getProperty(ConfigFileConnectionString);
		driver = EnvirommentManager.getInstance().getProperty(ConfigFileDriver);
	}
	public DBhelper(String contentType,String ConfigFileConnectionString,String ConfigFileDriver) {
		url = EnvirommentManager.getInstance().getProperty(ConfigFileConnectionString);
		driver = EnvirommentManager.getInstance().getProperty(ConfigFileDriver);
		SQLQuery = EnvirommentManager.getInstance().getProperty(contentType);
	}
	public DBhelper(String query,String connString,String drvr , String UserName , String Password) {
		url = connString;
		driver = drvr;
		SQLQuery = query;
		DBUserName = UserName; 
		DBPassWord = Password;
	}
	
	 /**
	   * This is the main method which makes use to Verify if the Data Base connected .
	   * @param ConfigFileConnectionString
	   * @param ConfigFileDriver
	   * @param DataBase UserName
	   * @param DataBase Password
	   * @param Query
	   * @return boolean.
	   */
	private boolean VerifyDBConnect(String Query) {
		boolean returnedValue = false;
		try {

			Class.forName(driver);
			Connection conn = DriverManager.getConnection(url , DBUserName , DBPassWord);
			Statement statement = conn.createStatement();
			ResultSet rs = statement.executeQuery(Query);

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
	   * This method which makes use to store sql query result in two dimensional array (table) .
	   * @param ConfigFileConnectionString
	   * @param ConfigFileDriver
	   * @param DataBase UserName
	   * @param DataBase Password
	   * @param Query
	   * @return list.
	   */
	public List<String[]> GetDBData(String Query ) {
		
		List<String[]> table = new ArrayList<>();
		 
		try {
			Class.forName(driver);
			Connection conn = DriverManager.getConnection(url ,DBUserName , DBPassWord);
			Statement statement = conn.createStatement();
			ResultSet rs = statement.executeQuery(Query);
			int columnCount = rs.getMetaData().getColumnCount();
			String[] row = new String[columnCount];	
			while (rs.next()) {	
				for (int i = 0; i < columnCount; i++){
				//Result.put(Columns.get(i), rs.getString(Columns.get(i)).trim());

			        row[i] =  rs.getObject(i+1).toString();
			        }
					table.add( row );
			    }
			    
			
			conn.close();
		
			return table;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	
	/**
	   * This method which makes use to store sql query result in list (one column).
	   * @param ConfigFileConnectionString
	   * @param ConfigFileDriver
	   * @param DataBase UserName
	   * @param DataBase Password
	   * @param Query
	   * @return list.
	   */
	public List<String> GetListData(String Query) {
		List<String>  Result = new ArrayList<String>();
		try {
			Class.forName(driver);
			Connection conn = DriverManager.getConnection(url , DBUserName , DBPassWord);
			Statement statement = conn.createStatement();
			ResultSet rs = statement.executeQuery(Query);
			int columnCount = rs.getMetaData().getColumnCount();
			while (rs.next()) {	
				for (int i = 0; i <columnCount; i++)
				Result.add( rs.getString(i + 1) ); 				
			}
			conn.close();
		
			return Result;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	/**
	   * This method which makes use to store sql query result in String(one Value) .
	   * @param ConfigFileConnectionString
	   * @param ConfigFileDriver
	   * @param DataBase UserName
	   * @param DataBase Password
	   * @param Query
	   * @return list.
	   */
	public String GetDBValue(String Query) {
		try {
			String Json = "";
			Class.forName(driver).newInstance();
			Connection conn = DriverManager.getConnection(url , DBUserName , DBPassWord);
			
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
