package jo.aspire.generic;

import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class XMLHelper {
	
	public static String getValueFromXml(String xmlAsString, String xPath)
	{
		Document document = getXmlDocument(xmlAsString);		
		return getXmlValueByXpath(document, xPath);
	}
	public static String getXmlValueByXpath(Document document, String xPath) {
		XPathExpression xp = null;
		try {
			xp = XPathFactory.newInstance().newXPath().compile(xPath);
		} catch (XPathExpressionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String value = null;
		try {
			value = xp.evaluate(document);
		} catch (XPathExpressionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 
		return value;
	}
	public static Document getXmlDocument(String xmlAsString) {
		DocumentBuilder b = null;
		try {
			b = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		Document document = null;
		try { 
			document = b.parse(new InputSource(new StringReader(xmlAsString)));
		} catch (SAXException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return document;
	}
	/**
	 * Get count of all element under root element.
	 * @param xmlAsString XML as string to get count of elements from
	 * @return count of elements under root element.
	 */
	public static int getCountOfElements(String xmlAsString)
	{
		int count = 0;
		Document document = getXmlDocument(xmlAsString);
		Element root = document.getDocumentElement();
		if(root != null) 
		{
			count = root.getChildNodes().getLength();
		}
		return count;	
	}
	/**
	 * Get count of specific element
	 * @param xmlAsString XML as string to get count of elements from
	 * @param elementName Element name to get count of elements that matches with
	 * @return count of elements which matches element name
	 */
	public static int getCountOfElement(String xmlAsString, String elementName)
	{
		Document document = getXmlDocument(xmlAsString);
		return document.getElementsByTagName(elementName).getLength();
	}
	/**
	 * Check if element is exist.
	 * @param xmlAsString XML as string
	 * @param elementName Element name which is being checked
	 * @return true if element exists on XML, otherwise false
	 */
	public static boolean isElementExists(String xmlAsString, String elementName)
	{
		Document document = getXmlDocument(xmlAsString);
		return document.getElementsByTagName(elementName).getLength() != 0;
	}
	/**
	 * Check if part of XML is exists in another XML
	 * @param xmlAsString XML as string that is being checked
	 * @param xmlToCheckAsString XML as string that needs to be exists in
	 * @return true if XML is exists, otherwise false
	 */
	public static boolean isXmlExists(String xmlAsString, String xmlToCheckAsString)
	{
		return false;
	}
	/**
	 * Check if an element's value is matching with a specific value
	 * @param xmlAsString XML as string that contains element's value
	 * @param elementXpath XPath of Element which its value is being checked 
	 * @param valueToMatch The value that needs to be checked
	 * @return true if element's value is exactly matching valueToMatch, otherwise false
	 */
	public static boolean isElementValueMatches(String xmlAsString, String elementXpath, String valueToMatch)
	{
		Document document = getXmlDocument(xmlAsString);		
		String elementValue =  getXmlValueByXpath(document,elementXpath);
		
		return elementValue == valueToMatch;
	}
}
