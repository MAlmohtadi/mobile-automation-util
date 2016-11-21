package jo.aspire.api.automationUtil.configuration.services;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import jo.aspire.api.automationUtil.configuration.services.HttpServiceConfigurations.HttpServiceConfiguration;
import jo.aspire.generic.Parsers;
import jo.aspire.web.automationUtil.StateHelper;

import org.apache.http.ParseException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.google.gson.JsonElement;

public class HttpServiceResponse {

	private ThreadLocal<CloseableHttpResponse> _httpResponse = new ThreadLocal();
	private ThreadLocal<String> _httpResponseResultAsString = new ThreadLocal();
	private ThreadLocal<String> _serviceName = new ThreadLocal();
	private ThreadLocal<HttpServiceConfiguration> _httpServiceConfiguration = new ThreadLocal();

	public HttpServiceResponse(final CloseableHttpResponse httpResponse, final HttpServiceConfiguration httpServiceConfiguration, final String serviceName) {
		setHttpServiceConfiguration(httpServiceConfiguration);
		setServiceName(serviceName);
		setHttpResponse(httpResponse);
		setResultAsString(parseResultAsString());
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
	public CloseableHttpResponse getHttpResponse() {
		return _httpResponse.get();
	}
	public JsonElement getResultAsJson() {
		Parsers parser = new Parsers();
		JsonElement json = null;
		try {
			json = parser.asJson(getResultAsString());
		} catch (ParseException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return json;
	}
	public Document getResultAsXml() {
		Parsers parser = new Parsers();
		Document document = null;
		try {
			document = parser.asXML(getResultAsString());
		} catch (ParseException | IOException | ParserConfigurationException
				| SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return document;
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
	private String parseResultAsString() {
		Parsers parser = new Parsers();
		String value = null;
		try {
			value = parser.asString(getHttpResponse());
		} catch (ParseException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return value;
	}
}