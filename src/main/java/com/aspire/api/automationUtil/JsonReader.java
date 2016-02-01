package com.aspire.api.automationUtil;

import java.io.FileInputStream;

import org.apache.commons.io.IOUtils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class JsonReader {
	/**
	 * Return value from Json File using a Key to find the matching value
	 * 
	 * @param File
	 * @param Key
	 * @return matching value
	 */
	public static String GetValue(String File, String Key) {
		try {

			String output = null;
			try {
				FileInputStream inputStream = new FileInputStream(System.getProperty("user.dir") + File);
				output = IOUtils.toString(inputStream);
				inputStream.close();
			} catch (Exception e) {
				System.out.println("cannot find file " + File);
				e.printStackTrace();
			}

			JsonParser parser = new JsonParser();
			JsonObject o = (JsonObject) parser.parse(output);

			return o.get(Key).toString().replaceAll("\"", "");

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Retruns the matching key from the provided string
	 * 
	 * @param Value
	 * @param Key
	 * @return matching value
	 */
	public static String GetValueFromString(String Value, String Key) {
		try {

			JsonParser parser = new JsonParser();
			JsonObject o = (JsonObject) parser.parse(Value);
			if(o.get(Key) != null)
				return o.get(Key).toString().replaceAll("\"", "");

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		return null;
	}

	public static String GetValueFromStringWithQuotes(String Value, String Key) {
		try {

			JsonParser parser = new JsonParser();
			JsonObject o = (JsonObject) parser.parse(Value);

			return o.get(Key).toString();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Gets the data form DB in Json format
	 * 
	 * @param Query
	 * @return
	 */
	public static String GenerateJson(String Query) {
		Connect2DB conn = new Connect2DB();
		return conn.GetDBData(EnvirommentManager.getInstance().getProperty(Query));
	}
}
