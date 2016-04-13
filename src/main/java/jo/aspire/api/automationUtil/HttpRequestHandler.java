package jo.aspire.api.automationUtil;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.text.Document;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpHost;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

/**
 * handler to handle get,post,put, and delete methods all httpclient
 * configurations manage headers, body, authentication, cookies depends on
 * httpcomponent-client 4.5.1 Aspire you must call createNewRequest for each new
 * request
 * 
 */
public class HttpRequestHandler {
	private static PoolingHttpClientConnectionManager cm;
	private static RequestConfig globalConfig;
	private RequestConfig localConfig;
	private static HttpClientBuilder httpclientBuilder;
	private RequestBuilder requestBuilder;
	private HttpContext localContext;
	private URIBuilder requestURI;
	private HttpHost proxy = null;
	

	public Map<String, CloseableHttpResponse> myResponses = new HashMap<String, CloseableHttpResponse>();

	private static HttpRequestHandler instance = null;
	private HttpRequestHandler() {
		// Prevent direct instantiation.
	}
	public static HttpRequestHandler getInstance() {
		if (instance == null) {
			httpclientBuilder = HttpClientBuilder.create();

			globalConfig = RequestConfig.custom().setCookieSpec(CookieSpecs.STANDARD).build();

			cm = new PoolingHttpClientConnectionManager();
			// Increase max total connection to 200
			cm.setMaxTotal(200);
			// Increase default max connection per route to 20
			cm.setDefaultMaxPerRoute(20);
			// Increase max connections for localhost:80 to 50
			// HttpHost localhost = new HttpHost("localhost", 80);
			// cm.setMaxPerRoute(new HttpRoute(localhost), 50);

			httpclientBuilder.setConnectionManager(cm);
			httpclientBuilder.setDefaultRequestConfig(globalConfig);

			instance = new HttpRequestHandler();
		}
		return instance;
	}

	public void createNewRequest(MethodEnum.Method method, String responseUniqueID) {
		requestBuilder = RequestBuilder.create(method.name());
		requestURI = new URIBuilder();
		localContext = new BasicHttpContext();
		localConfig = RequestConfig.custom().setCookieSpec(CookieSpecs.STANDARD).build();

		myResponses.put(responseUniqueID, null);
	}

	public void setRequestUrl(String scheme, String host, String path) throws URISyntaxException {
		requestURI.setScheme(scheme).setHost(host).setPath(path);
	}

	public void setRequestUrl(String url) throws URISyntaxException {
		requestURI = new URIBuilder(url);
	}

	public void setRequestUrl(URI uri) throws URISyntaxException {
		requestURI = new URIBuilder(uri);
	}

	public void setRequestQueryString(String key, String value) {
		requestURI.addParameter(key, value);
	}

	public void setRequestQueryString(List<NameValuePair> queryList) {
		requestURI.addParameters(queryList);
	}

	public void setRequestHeader(Header header) {
		requestBuilder.setHeader(header);
	}
	
	public void setProxy(String proxyAddress, int proxyPort)
	{
		if (proxyAddress == null)
		{
			proxy = null;
		}
		else
		{
			proxy = new HttpHost(proxyAddress, proxyPort, HttpHost.DEFAULT_SCHEME_NAME);
		}
	}

	/**
	 * @param name
	 *            is any variable from class org.apache.http.HttpHeaders
	 */
	public void setRequestHeader(String name, String value) {
		requestBuilder.setHeader(name, value);
	}

	public void setRequestCookies(String name, String value, String domain, String path) {
		BasicClientCookie cookie = new BasicClientCookie(name, value);
		cookie.setDomain(domain);
		cookie.setPath(path);

		this.setRequestCookies(cookie);
	}

	public void setRequestCookies(BasicClientCookie cookie) {
		BasicCookieStore requestCookieStore = new BasicCookieStore();

		requestCookieStore.addCookie(cookie);
		localContext.setAttribute(HttpClientContext.COOKIE_STORE, requestCookieStore);
	}

	public void setRequestBasicAuth(String user, String password) {
		String auth = user + ":" + password;
		byte[] encodedAuth = Base64.encodeBase64(auth.getBytes(Charset.forName("ISO-8859-1")));
		String authHeader = "Basic " + new String(encodedAuth);

		this.setRequestHeader(HttpHeaders.AUTHORIZATION, authHeader);
	}

	public void setSessionHeader(Header header) {
		List<Header> headersList = new ArrayList<Header>();
		headersList.add(header);

		this.setSessionHeader(headersList);
	}

	public void setSessionHeader(String name, String value) {
		List<Header> headersList = new ArrayList<Header>();
		headersList.add(new BasicHeader(name, value));

		this.setSessionHeader(headersList);
	}

	public void setSessionHeader(List<Header> defaultHeaders) {
		httpclientBuilder.setDefaultHeaders(defaultHeaders);
	}

	public void setSessionCookies(String name, String value, String domain, String path) {
		BasicClientCookie cookie = new BasicClientCookie(name, value);
		cookie.setDomain(domain);
		cookie.setPath(path);

		this.setSessionCookies(cookie);
	}

	public void setSessionCookies(BasicClientCookie cookie) {
		BasicCookieStore connCookieStore = new BasicCookieStore();

		connCookieStore.addCookie(cookie);
		httpclientBuilder.setDefaultCookieStore(connCookieStore);
	}

	public void setSessionBasicAuth(String user, String password) {
		String auth = user + ":" + password;
		byte[] encodedAuth = Base64.encodeBase64(auth.getBytes(Charset.forName("ISO-8859-1")));
		String authHeader = "Basic " + new String(encodedAuth);

		this.setSessionHeader(HttpHeaders.AUTHORIZATION, authHeader);
	}

	private void setRequestBody(HttpEntity entity) {
		requestBuilder.setEntity(entity);
	}

	public void setRequestBody(String entity) throws UnsupportedEncodingException {
		StringEntity newEntity = new StringEntity(entity, "utf-8");
		this.setRequestBody(newEntity);
	}

	public void setRequestBody(List<NameValuePair> nvps) throws UnsupportedEncodingException {
		this.setRequestBody(new UrlEncodedFormEntity(nvps));
	}

	public void setRequestBody(JsonObject entity) throws UnsupportedEncodingException {
		String jsonEntity = new Gson().toJson(entity);
		this.setRequestBody(jsonEntity);
	}

	public void setRequestBody(Document entity) throws UnsupportedEncodingException {
		this.setRequestBody(entity.toString());
	}

	public void setRequestBodyByXMLString(String entity) throws UnsupportedEncodingException {
		HttpEntity newEntity = new ByteArrayEntity(entity.getBytes("UTF-8"));
		this.setRequestBody(newEntity);
	}

	public CloseableHttpResponse execute(String responseUniqueID)
			throws URISyntaxException, ClientProtocolException, IOException {

		CloseableHttpClient httpclient;
		if (proxy != null)
		{
			DefaultProxyRoutePlanner routePlanner = new DefaultProxyRoutePlanner(proxy);
			httpclient = httpclientBuilder.setRoutePlanner(routePlanner).build();
			
		}
		else
		{
			httpclient = httpclientBuilder.build();
		}

		HttpUriRequest requestHead = requestBuilder.setUri(requestURI.build()).setConfig(localConfig).build();

		CloseableHttpResponse response = httpclient.execute(requestHead, localContext);

		myResponses.remove(responseUniqueID);
		myResponses.put(responseUniqueID, response);
		
		return response;
	}
}
