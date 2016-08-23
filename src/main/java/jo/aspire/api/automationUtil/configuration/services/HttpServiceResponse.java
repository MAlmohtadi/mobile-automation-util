package jo.aspire.api.automationUtil.configuration.services;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import jo.aspire.api.automationUtil.configuration.services.HttpServiceConfigurations.HttpServiceConfiguration;
import jo.aspire.generic.Parsers;
import jo.aspire.web.automationUtil.StateHelper;

import org.apache.http.ParseException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.w3c.dom.Document;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;
import org.xml.sax.SAXException;

import com.google.gson.JsonElement;

public class HttpServiceResponse {

	private CloseableHttpResponse _httpResponse;
	private String _httpResponseResultAsString;
	private String _serviceName;
	private HttpServiceConfiguration _httpServiceConfiguration;
	private JsonElement _jsonResult;
	private Document _xmlResult;
	public HttpServiceResponse(CloseableHttpResponse httpResponse, HttpServiceConfiguration httpServiceConfiguration, String serviceName) {
		_httpServiceConfiguration = httpServiceConfiguration;
		_serviceName = serviceName;
		_httpResponse = httpResponse;	    
	    handleAndSetHttpResponseResult();
	}
	public HttpServiceResponse getResponseResult() {
		return this;
	}
	public HttpServiceResponse setResultAsStringToStoryStore(String sotreKey) {
		StateHelper.setStoryState(_serviceName + sotreKey, getResultAsString());
		return this;
	}
	public String getResultAsStringFromStoryStore(String sotreKey) {
		return StateHelper.getStoryState(_serviceName + sotreKey).toString();
	}
	public HttpServiceResponse setResultAsStringToStepStore(String sotreKey) {
		StateHelper.setStepState(_serviceName + sotreKey, getResultAsString());
		return this;
	}
	public String getResultAsStringFromStepStore(String sotreKey) {
		return StateHelper.getStepState(_serviceName + sotreKey).toString();
	}
	public String getResultAsString() {
		return _httpResponseResultAsString;
	}
	protected HttpServiceResponse setResultAsString(String httpResponseResultAsString)
	{
		_httpResponseResultAsString = httpResponseResultAsString;
		return this;
	}	
	public String getValueToCompare() {
		return  _httpServiceConfiguration.getValueToCompare();
	}	
	private void handleAndSetHttpResponseResult()
	{
		String responseContentType = _httpResponse.getEntity().getContentType().getValue();
		if(responseContentType.contains("application/json"))
		{
			setResultAsJson(getAsJson());
			setResultAsString(getResultAsJson() != null ? getResultAsJson().toString() : "{}");
		}
		else if(responseContentType.contains("text/xml"))
		{
			setResultAsXml(getAsXml());		 
			setResultAsString(getStringXmlFromDocument(getResultAsXml()));
		}
	}
	private JsonElement getAsJson() {
		Parsers parser = new Parsers();		
        JsonElement json = null;
		try {
			json = parser.asJson(_httpResponse);
		} catch (ParseException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return json;
	}
	private Document getAsXml() {
		Parsers parser = new Parsers();
		Document document = null;
		try {
			document = parser.asXML(_httpResponse);
		} catch (ParseException | IOException | ParserConfigurationException
				| SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return document;
	}
	private String getStringXmlFromDocument(Document document) {
		DOMImplementationLS domImplementation = (DOMImplementationLS) document
				.getImplementation();
		LSSerializer lsSerializer = domImplementation.createLSSerializer();
		return lsSerializer.writeToString(document);
	}
	public JsonElement getResultAsJson() {
		return _jsonResult;
	}
	private void setResultAsJson(JsonElement _jsonResult) {
		this._jsonResult = _jsonResult;
	}
	public Document getResultAsXml() {
		return _xmlResult;
	}
	private void setResultAsXml(Document _xmlResult) {
		this._xmlResult = _xmlResult;
	}
}