package com.aspire.api.automationUtil;

import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class XMLReader {
	/**
	 * Return value from XML File using a Key to find the matching value
	 * 
	 * @param File
	 * @param Key
	 * @return matching value
	 */
	public static String XMLGetValue(String File, String Key) {
		try {


			try {				
				DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
				InputSource src = new InputSource();
				src.setCharacterStream(new StringReader(File));

				Document doc = builder.parse(src);
				String Value = doc.getElementsByTagName(Key).item(0).getTextContent();

				return Value;
				
			} catch (Exception e) {
				System.out.println("cannot find file " + File);
				e.printStackTrace();
			}				
			return null;

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
	public static String GetXMLValueFromString(String Value, String Key) {
		try {

			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			InputSource src = new InputSource();
			src.setCharacterStream(new StringReader(Value));

			Document doc = builder.parse(src);
			String value = doc.getElementsByTagName(Key).item(0).getTextContent();

			return value;

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	public static String GetXMLValueFromStringWithQuotes(String Value, String Key) {
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
	 * Gets the data form DB in XML format
	 * 
	 * @param Query
	 * @return
	 */
	public static String GenerateXML(String Query) {
		Connect2DB conn = new Connect2DB();
		return conn.GetDBData(EnvirommentManager.getInstance().getProperty(Query));
	}
}
