package jo.aspire.api.automationUtil;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;

public class ConfigMapper {
	/**
	 * Gets the service input from the config file for service input
	 * @param Key
	 * @return matching key value
	 */
	public static String GetServiceInput(String Key)
	{
		return GetValue("/inputs/input[@id='%s']", Constants.SERVICE_INPUT_FILE, Key);
	}
	
	/**
	 * Gets the value from the config file
	 * @param Key
	 * @return matching key value
	 */
	public static String GetConfigValue(String Key) {
		return GetValue("/configurations/add[@key='%s']/@value", Constants.CONFIG_FILE, Key);
	}

	/**
	 * Gets the mapping Json value for column
	 * @param Column
	 * @return matching column value
	 */
	public static String GetJsonByColumn(String Column) {
		return GetValue("/columns/column[@id='%s']",
				Constants.COLUMN_MAPPER_FILE, Column);
	}

	private static String GetValue(String Expression, String File, String Key) {
		String expression = String.format(Expression, Key);
		XPath xPath = XPathFactory.newInstance().newXPath();
		// read a string value
		try {
			return xPath.compile(expression).evaluate(GetXMLDocument(File));
		} catch (XPathExpressionException e) {
			e.printStackTrace();
			return null;
		}
	}

	private static Document GetXMLDocument(String File) {
		try {
			File fXmlFile = new File(System.getProperty("user.dir") + File);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder dBuilder;

			dBuilder = dbFactory.newDocumentBuilder();
			return dBuilder.parse(fXmlFile);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

	}
}
