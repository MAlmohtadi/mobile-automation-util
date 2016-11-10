package jo.aspire.api.automationUtil.configuration.services;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import jo.aspire.api.automationUtil.HttpRequestHandler;
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

	private ThreadLocal<CloseableHttpResponse> _httpResponse;
	private ThreadLocal<String> _httpResponseResultAsString;
	private ThreadLocal<String> _serviceName;
	private ThreadLocal<HttpServiceConfiguration> _httpServiceConfiguration;
	private ThreadLocal<JsonElement> _jsonResult;
	private ThreadLocal<Document> _xmlResult;
	public HttpServiceResponse(final CloseableHttpResponse httpResponse, final HttpServiceConfiguration httpServiceConfiguration, final String serviceName) {
		setHttpServiceConfiguration(httpServiceConfiguration);
		setServiceName(serviceName);
		setHttpResponse(httpResponse);
		handleAndSetHttpResponseResult();
	}
	public HttpServiceResponse getResponseResult() {
		return this;
	}
	public HttpServiceResponse setResultAsStringToStoryStore(String sotreKey) {
		StateHelper.setStoryState(getServiceName() + sotreKey, getResultAsString());
		return this;
	}
	public String getResultAsStringFromStoryStore(String sotreKey) {
		return StateHelper.getStoryState(getServiceName() + sotreKey).toString();
	}
	public HttpServiceResponse setResultAsStringToStepStore(String sotreKey) {
		StateHelper.setStepState(getServiceName() + sotreKey, getResultAsString());
		return this;
	}
	public String getResultAsStringFromStepStore(String sotreKey) {
		return StateHelper.getStepState(getServiceName() + sotreKey).toString();
	}
	public String getResultAsString() {
		return _httpResponseResultAsString.get();
	}
	protected HttpServiceResponse setResultAsString(final String httpResponseResultAsString)
	{
		_httpResponseResultAsString = new ThreadLocal<String>() {
		@Override public String initialValue() {
			return httpResponseResultAsString;
		}
	};
		return this;
	}	
	public String getValueToCompare() {
		return  getHttpServiceConfiguration().getValueToCompare();
	}
	private void setHttpResponse(final CloseableHttpResponse httpResponse) {
		_httpResponse = new ThreadLocal<CloseableHttpResponse>() {
			@Override public CloseableHttpResponse initialValue() {
				return httpResponse;
			}
		};
	}
	private CloseableHttpResponse getHttpResponse() {
		return _httpResponse.get();
	}
	private void setServiceName(final String serviceName) {
		_serviceName = new ThreadLocal<String>() {
			@Override public String initialValue() {
				return serviceName;
			}
		};
	}
	private String getServiceName() {
		return _serviceName.get();
	}
	private void setHttpServiceConfiguration(final HttpServiceConfiguration httpServiceConfiguration) {
		_httpServiceConfiguration = new ThreadLocal<HttpServiceConfiguration>() {
			@Override public HttpServiceConfiguration initialValue() {
				return httpServiceConfiguration;
			}
		};
	}
	private HttpServiceConfiguration getHttpServiceConfiguration() {
		return _httpServiceConfiguration.get();
	}
	private void handleAndSetHttpResponseResult()
	{
		String responseContentType = getHttpResponse().getEntity().getContentType().getValue();
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
			json = parser.asJson(getHttpResponse());
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
			document = parser.asXML(getHttpResponse());
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
		return _jsonResult.get();
	}
	private void setResultAsJson(final JsonElement jsonResult) {
		this._jsonResult = new ThreadLocal<JsonElement>() {
			@Override public JsonElement initialValue() {
				return jsonResult;
			}
		};;
	}
	public Document getResultAsXml() {
		return _xmlResult.get();
	}
	private void setResultAsXml(final Document xmlResult) {
		this._xmlResult =  new ThreadLocal<Document>() {
			@Override public Document initialValue() {
				return xmlResult;
			}
		};
	}
}