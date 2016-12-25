package jo.aspire.api.automationUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import jo.aspire.generic.EnvirommentManager;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
public class DBhelper {

	/**
	   * @param MySQL url = jdbc:mysql://hostname/ databaseName
	   * @param ORACLE url = jdbc:oracle:thin:@hostname:port Number:databaseName	  
	   */
	
	private String driver;
	private String url;
	private String dbUserName;
	private String dbPassword;

	public DBhelper(String connString, String driver, String userName,  String password) {
		url = connString;
		this.driver = driver;
		dbUserName = userName;
		dbPassword = password;
	}
	/**
	   * This method which makes use to get sql query result in two dimensional array (table) .
	   * @param query
	   * @return list.
	   */
	public List<String[]> getData(String query ) {
		
		List<String[]> table = new ArrayList<>();

		Connection dbConnection = null;
		try {
			dbConnection = getDBConnection();
			Statement statement = dbConnection.createStatement();
			ResultSet rs = statement.executeQuery(query);
			int columnCount = rs.getMetaData().getColumnCount();
			String[] row = new String[columnCount];	
			while (rs.next()) {	
				for (int i = 0; i < columnCount; i++){
			        row[i] =  rs.getString(i+1);
			        }
					table.add( row );
			    }
		} catch (Exception e) {
			System.err.println("Error while executing query:[" + query + "] exception:" + e);
			e.printStackTrace();
			try {
				dbConnection.close();
			} catch (SQLException e1) {
				e1.printStackTrace();
				System.err.println("Error while closing database connection!\n");
			}
		} finally {
			if (dbConnection != null) {
				try {
					dbConnection.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return table;
	}
	/**
	   * This method which makes use to get sql query result in list (one column).

	   * @param query
	   * @return list String.
	   */
	public List<String> getListData(String query) {
		List<String>  result = new ArrayList<String>();
		Connection dbConnection = null;
		try {
			dbConnection = getDBConnection();
			Statement statement = dbConnection.createStatement();
			ResultSet rs = statement.executeQuery(query);
			int columnCount = rs.getMetaData().getColumnCount();
			while (rs.next()) {	
				for (int i = 0; i <columnCount; i++)
				result.add( rs.getString(i + 1) );
			}
		} catch (Exception e) {
			System.err.println("Error while executing query:[" + query + "] exception:" + e);
			e.printStackTrace();
			try {
				dbConnection.close();
			} catch (SQLException e1) {
				e1.printStackTrace();
				System.err.println("Error while closing database connection!\n");
			}
		} finally {
			if (dbConnection != null) {
				try {
					dbConnection.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return result;
	}

	/**
	 * This method used to get query result in json array.
	 * @param query
	 * @return
	 */
	public JSONArray getListDataAsJson(String query) {
		Connection dbConnection = null;
		JSONArray recordObjectArray = new JSONArray();
		try {
			dbConnection = getDBConnection();
			Statement stmt = dbConnection.createStatement();
			System.out.print(query);
			ResultSet result = stmt.executeQuery(query);

			while(result.next())
			{
				JSONObject recordObject = new JSONObject();
				ResultSetMetaData rsmd = result.getMetaData();
				int columnCount = rsmd.getColumnCount();
				for (int i = 1; i < columnCount + 1; i++) {
					String colName = rsmd.getColumnName(i);
					String colValue = result.getString(i);
					recordObject.put(colName, colValue);
				}
				// add the first record to array
				recordObjectArray.put(recordObject);
			}

		} catch (Exception e) {
			System.err.println("Error while executing query:[" + query + "] exception:" + e);
			e.printStackTrace();
			try {
				dbConnection.close();
			} catch (SQLException e1) {
				e1.printStackTrace();
				System.err.println("Error while closing database connection!\n");
			}
		} finally {
			if (dbConnection != null) {
				try {
					dbConnection.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return recordObjectArray;
	}
	/**
	   * This method which makes use to get sql query result in Object(one Value).

	   * @param query
	   * @return Object.
	   */
	public Object getValue(String query) {
		Object result = null;
		Connection dbConnection = null;
		try {

			dbConnection = getDBConnection();
			Statement stmt;
			stmt = dbConnection.createStatement();
			ResultSet rows = stmt.executeQuery(query);
			rows.next();

			result = rows.getObject(1);
		} catch (SQLException e) {
			System.err.println("Error while executing scalar query:[" + query + "] exception:" + e);
			e.printStackTrace();
			try {
				dbConnection.close();
			} catch (SQLException e1) {
				e1.printStackTrace();
				System.err.println("Error while closing database connection!\n");
			}
		} finally {
			if (dbConnection != null) {
				try {
					dbConnection.close();
				} catch (SQLException e) {
					e.printStackTrace();
					System.err.println("Error while closing database connection!\n");
				}
			}
		}
		return result;
	}

	/**
	 * This method used to execute non-queries such as: delete, insert, update.
	 * @param query
	 * @return
	 */
	public int executeNonQuery(String query) {
		Connection dbConnection = null;
		int result = 0;
		try {

			dbConnection = getDBConnection();
			Statement stmt = dbConnection.createStatement();
			System.out.print(query);
			result = stmt.executeUpdate(query);

			System.out.println(result + " Rows Updated.");

		} catch (Exception e) {
			System.err.println("Error while executing non query:[" + query + "] exception:" + e );
			e.printStackTrace();
			try {
				dbConnection.close();
			} catch (SQLException e1) {
				e1.printStackTrace();
				System.err.println("Error while closing database connection!\n");
			}
		} finally {
			if (dbConnection != null) {
				try {
					dbConnection.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return result;
	}


	private Connection getDBConnection() {

		Connection dbConnection = null;
		try {
			Class.forName(driver);
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		}
		try {
			dbConnection = DriverManager.getConnection(url, dbUserName, dbPassword);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			System.err.println("Error while closing database connection!\n" + e);
		}
		return dbConnection;
	}
}
