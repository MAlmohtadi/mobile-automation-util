package jo.aspire.api.automationUtil.configuration.services;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.Hashtable;
import java.util.List;
import java.util.Map.Entry;

import jo.aspire.api.automationUtil.HttpRequestHandler;
import jo.aspire.api.automationUtil.MethodEnum.Method;
import jo.aspire.api.automationUtil.configuration.services.HttpServiceConfigurations.HttpServiceConfiguration;
import jo.aspire.api.automationUtil.configuration.services.HttpServiceConfigurations.HttpServiceRequestConfigParam;

import jo.aspire.web.automationUtil.StateHelper;
import org.apache.http.Header;
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
	public HttpServiceRequest resolveServiceRequestHeader(String headerName, String placeholderName, String placeholderValue) {
		for(Header header : _httpRequestHandler.getRequestHeaders(headerName))
		{
			if( header.getValue() != null){
			_httpRequestHandler.setRequestHeader(headerName, header.getValue().replace(placeholderName, placeholderValue));
			}
		}
		return this;
	}
	public HttpServiceRequest resolveServiceRequestUrl(String placeholderName, String placeholderValue, boolean encodeUrl) {
		String url = getHttpServiceConfiguration().getUrl();
		if (url != null && url != "") {
			placeholderValue = getEncodedValue(placeholderValue, encodeUrl);
			url = url.replace(placeholderName, placeholderValue);
			getHttpServiceConfiguration().setUrl(url);
			setRequestUrl();
		}
		return this;
	}
	public String getEncodedValue(String placeholderValue, boolean encodeUrl) {
		if(encodeUrl)
		{
			try {
				placeholderValue = URLEncoder.encode(placeholderValue, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return placeholderValue;
	}
	public HttpServiceRequest resolveServiceRequestUrl(String placeholderName, String placeholderValue) {
		 return resolveServiceRequestUrl(placeholderName, placeholderValue, false);
	}
	public HttpServiceRequest addCookie(String name, String value, String domain, String path) {
		getHttpRequestHandler().setSessionCookie(name, value, domain, path); 
		return this;
	}
	public HttpServiceRequest addCookie(Cookie cookie) {
		getHttpRequestHandler().setSessionCookie(cookie);
		return this;
	}
	public HttpServiceRequest setRequestBodyToStepStore(String storeKey) {
		StateHelper.setStepState(storeKey,getServiceRequestBody());
		return this;
	}
	protected HttpServiceRequest initHttpRequest()
	{
		getHttpRequestHandler().createNewRequest(getRequestHttpMethod(), getHttpServiceConfiguration().getMethodName());
		getHttpRequestHandler().setRequestHeader("Content-Type", getHttpServiceConfiguration().getConentType());
	    setRequestUrl();
		initServiceRequestBody(getHttpServiceConfiguration() .getRequestBodyParams());
		initServiceRequestHeaders(getHttpServiceConfiguration() .getRequestHeaders());
		return this;
	}
	void setRequestUrl() {
		try {
			String url = getHttpServiceConfiguration().getHost() + getHttpServiceConfiguration().getUrl();
			getHttpRequestHandler().setRequestUrl(url);
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	protected void initServiceRequestBody(List<HttpServiceRequestConfigParam> httpServiceRequestBodyConfigParam)
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
	protected void initServiceRequestHeaders(List<HttpServiceRequestConfigParam> httpServiceRequestHeaders)
	{			
		if(httpServiceRequestHeaders != null){
			for(HttpServiceRequestConfigParam header : httpServiceRequestHeaders)
			{
				if(header != null){
					_httpRequestHandler.setRequestHeader(header.name, header.value);
				}
			}
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
    private void initServiceRequestBodyFrom(List<HttpServiceRequestConfigParam> httpServiceRequestBodyConfigParam)
    {
			setServiceRequestBody("method=" + getHttpServiceConfiguration().getMethodName());			
			if(httpServiceRequestBodyConfigParam != null){
				String requestBody = getServiceRequestBody();
				for(HttpServiceRequestConfigParam param : httpServiceRequestBodyConfigParam)
				{
					if(param != null){
						requestBody += "&" + param.name + "=" + param.value;
						setServiceRequestBody(requestBody);
					}
				}
			}
			setHttpRequestHandlerRequestBody();
    }    
    private void initServiceRequestBodyJson(List<HttpServiceRequestConfigParam> httpServiceRequestBodyConfigParam)
    {
			if(httpServiceRequestBodyConfigParam != null){
				for(HttpServiceRequestConfigParam param : httpServiceRequestBodyConfigParam)
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