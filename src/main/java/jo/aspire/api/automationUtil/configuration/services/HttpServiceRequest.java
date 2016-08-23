package jo.aspire.api.automationUtil.configuration.services;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.Hashtable;
import java.util.List;
import java.util.Map.Entry;

import jo.aspire.api.automationUtil.HttpRequestHandler;
import jo.aspire.api.automationUtil.MethodEnum.Method;
import jo.aspire.api.automationUtil.configuration.services.HttpServiceConfigurations.HttpServiceConfiguration;
import jo.aspire.api.automationUtil.configuration.services.HttpServiceConfigurations.HttpServiceRequestBodyConfigParam;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.cookie.Cookie;

public class HttpServiceRequest {

	HttpServiceConfiguration _httpServiceConfiguration;
	private HttpRequestHandler _httpRequestHandler;
	private String _requestBody;

	public HttpServiceRequest(HttpRequestHandler httpRequestHandler, HttpServiceConfiguration httpServiceConfiguration)
	{
		setHttpRequestHandler(httpRequestHandler);
		setHttpServiceConfiguration(httpServiceConfiguration);
		initHttpRequest();
	}	
	public HttpServiceRequest resolveServiceRequestBody(Hashtable<String, String> PlaceholderKeyValueHashtable) {
		if (PlaceholderKeyValueHashtable != null) {
			for (Entry<String, String> placeholder : PlaceholderKeyValueHashtable
					.entrySet()) {
				setServiceRequestBody(getServiceRequestBody().replace(
						placeholder.getKey(), placeholder.getValue()));
			}
		}
		setHttpRequestHandlerRequestBody();
		return this;
	}
	public HttpServiceRequest resolveServiceRequestBody(String placeholderName, String placeholderValue) {
		setServiceRequestBody(getServiceRequestBody().replace(placeholderName, placeholderValue));
		setHttpRequestHandlerRequestBody();
		return this;
	}
	public HttpServiceRequest resolveServiceRequestUrl(String placeholderName, String placeholderValue) {
		String url = getHttpServiceConfiguration().getUrl();
		if (url != null && url != "") {
			url = url.replace(placeholderName, placeholderValue);
			getHttpServiceConfiguration().setUrl(url);
			setRequestUrl();
		}
		return this;
	}
	public HttpServiceRequest addCookie(String name, String value, String domain, String path) {
		getHttpRequestHandler().setSessionCookie(name, value, domain, path); 
		return this;
	}
	public HttpServiceRequest addCookie(Cookie cookie) {
		getHttpRequestHandler().setSessionCookie(cookie);
		return this;
	}
	protected HttpServiceRequest initHttpRequest()
	{
		getHttpRequestHandler().createNewRequest(getRequestHttpMethod(), getHttpServiceConfiguration().getMethodName());
		getHttpRequestHandler().setRequestHeader("Content-Type", getHttpServiceConfiguration().getConentType());
	    setRequestUrl();
		initServiceRequestBody(getHttpServiceConfiguration() .getRequestBodyParams());
		return this;
	}
	public void setRequestUrl() {
		try {
			getHttpRequestHandler().setRequestUrl( getHttpServiceConfiguration().getHost() + getHttpServiceConfiguration().getUrl());
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public HttpServiceResponse execute()
	{
		CloseableHttpResponse httpResponse = null;
		try {
			httpResponse = getHttpRequestHandler().executeSessionRequest( getHttpServiceConfiguration().getMethodName());
		} catch (URISyntaxException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new HttpServiceResponse(httpResponse, getHttpServiceConfiguration(), getHttpServiceConfiguration() .getName());
	}
	protected void initServiceRequestBody(List<HttpServiceRequestBodyConfigParam> httpServiceRequestBodyConfigParam)
	{
		if(getHttpServiceConfiguration().getConentType().toLowerCase().contains("application/x-www-form-urlencoded".toLowerCase()))
		{
			initServiceRequestBodyFrom(httpServiceRequestBodyConfigParam);
		}
		else //application/Json
		{
			initServiceRequestBodyJson(httpServiceRequestBodyConfigParam);
		}
	}
	public void executeWithoutResponse() {
		try {
			getHttpRequestHandler().executeSessionRequest(getHttpServiceConfiguration().getMethodName());
		} catch (URISyntaxException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public HttpServiceRequest setRequestHeader(String name, String value) {
		getHttpRequestHandler().setRequestHeader(name, value);
		return this;
	}

	protected void setHttpServiceConfiguration(HttpServiceConfiguration serviceConfig) {
		this._httpServiceConfiguration = serviceConfig;
	}

	protected HttpServiceConfiguration getHttpServiceConfiguration() {
		return this._httpServiceConfiguration;
	}

	protected HttpRequestHandler getHttpRequestHandler() {
		return _httpRequestHandler;
	}

	protected void setHttpRequestHandler(HttpRequestHandler _httpRequestHandler) {
		this._httpRequestHandler = _httpRequestHandler;
	}

	protected void setHttpRequestHandlerRequestBody() {
		try {
			if (getServiceRequestBody() != null) {
				getHttpRequestHandler().setRequestBody(getServiceRequestBody());
			}
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	protected void setServiceRequestBody(String requestBody) {
		_requestBody = requestBody;
	}

	protected String getServiceRequestBody() {
		return _requestBody;
	}

	protected Method getRequestHttpMethod() {
		switch (getHttpServiceConfiguration().getHttpMethod()) {
		case "POST": {
			return Method.POST;
		}
		case "GET": {
			return Method.GET;
		}
		case "DELETE": {
			return Method.DELETE;
		}
		case "PUT": {
			return Method.PUT;
		}
		}
		return Method.POST;
	}
    private void initServiceRequestBodyFrom(List<HttpServiceRequestBodyConfigParam> httpServiceRequestBodyConfigParam)
    {
			setServiceRequestBody("method=" + getHttpServiceConfiguration().getMethodName());			
			if(httpServiceRequestBodyConfigParam != null){
				String requestBody = getServiceRequestBody();
				for(HttpServiceRequestBodyConfigParam param : httpServiceRequestBodyConfigParam)
				{
					if(param != null){
						requestBody += "&" + param.name + "=" + param.value;
						setServiceRequestBody(requestBody);
					}
				}
			}
			setHttpRequestHandlerRequestBody();
    }    
    private void initServiceRequestBodyJson(List<HttpServiceRequestBodyConfigParam> httpServiceRequestBodyConfigParam)
    {
			if(httpServiceRequestBodyConfigParam != null){
				for(HttpServiceRequestBodyConfigParam param : httpServiceRequestBodyConfigParam)
				{
					if(param != null){
						setServiceRequestBody(param.value);
						break;
					}
				}
			}
			setHttpRequestHandlerRequestBody();
    }
}