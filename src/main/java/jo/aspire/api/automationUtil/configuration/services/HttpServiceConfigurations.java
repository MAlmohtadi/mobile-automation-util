package jo.aspire.api.automationUtil.configuration.services;

import java.util.List;

public class HttpServiceConfigurations {

	public class HttpServiceConfiguration {
		private String name;
		private String host;
		private String httpMethod;
		private String contentType;
		private String url;
		private String method;
		private List<HttpServiceRequestConfigParam> requestBodyParams;
		private List<HttpServiceRequestConfigParam> requestHeaders;
		private String valueToCompare;
		private String jsonFilePath;
		private List<JSONCheckRule> jsonCheckRules;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getHost() {
			return host;
		}

		public void setHost(String host) {
			this.host = host;
		}

		public String getHttpMethod() {
			return httpMethod;
		}

		public void setHttpMethod(String httpMethod) {
			this.httpMethod = httpMethod;
		}

		public String getConentType() {
			return contentType;
		}

		public void setConentType(String contentType) {
			this.contentType = contentType;
		}

		public String getUrl() {
			return url;
		}

		public void setUrl(String url) {
			this.url = url;
		}

		public String getMethodName() {

			return method;
		}

		public void setMethodName(String methodName) {
			this.method = methodName;
		}

		public String getJSONFilePath()
		{
			return jsonFilePath;
		}
		public List<JSONCheckRule> getJSONCheckRules()
		{
			return jsonCheckRules;
		}

		public List<HttpServiceRequestConfigParam> getRequestBodyParams() {
			return requestBodyParams;
		}

		public List<HttpServiceRequestConfigParam> getRequestHeaders() {
			return requestHeaders;
		}
		
		void setRequestHeaders(List<HttpServiceRequestConfigParam> headers) {
			 requestHeaders = headers;
		}
		public String getValueToCompare() {
			return valueToCompare;
		}

		public void setValueToCompare(String valueToCompare) {
			this.valueToCompare = valueToCompare;
		}
	}

	public class HttpServiceRequestConfigParam {

		public String name;
		public String value;
	}

	public class JSONCheckRule {
		public String node;
		public String check;
	}
	
	public class HttpServicesConfigurationCollection  {
		
		private String defaultHost;
		private String defaultHttpMethod;
		private String defaultContentType;
		public List<HttpServiceConfiguration> services;
		private List<HttpServiceRequestConfigParam> defaultRequestHeaders;
		
		public String getDefaultHost() {
			if(defaultHost == null || defaultHost.trim() == "")
			{
				try {
					throw new Exception("Host cannot be empty, there is no value has been set for this configuration.");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					e.getCause();
				}
			}
			return defaultHost;
		}
		public void setDefaultHost(String defaultHost) {
			this.defaultHost = defaultHost;
		}
		public String getDefaultHttpMethod() {
			if(defaultHttpMethod == null || defaultHttpMethod.trim() == "")
			{
				try {
					throw new Exception("Http method cannot be empty, there is no value has been set for this configuration.");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					e.getCause();
				}
			}
			return defaultHttpMethod;
		}
		public void setDefaultHttpMethod(String defaultHttpMethod) {
			this.defaultHttpMethod = defaultHttpMethod;
		}
		public String getDefaultContentType() {
			if(defaultContentType == null || defaultContentType.trim() == "")
			{
				try {
					throw new Exception("Content type cannot be empty, there is no value has been set for this configuration.");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					e.getCause();
				}
			}
			return defaultContentType;
		}
		public void setDefaultContentType(String defaultContentType) {
			this.defaultContentType = defaultContentType;
		}
		public List<HttpServiceRequestConfigParam> getDefaultRequestHeaders() {
			return defaultRequestHeaders;
		}
		public void setDefaultRequestHeaders(List<HttpServiceRequestConfigParam> defaultRequestHeaders) {
			this.defaultRequestHeaders = defaultRequestHeaders;
		}
	}
}
