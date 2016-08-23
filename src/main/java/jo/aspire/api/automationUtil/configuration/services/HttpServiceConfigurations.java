package jo.aspire.api.automationUtil.configuration.services;

import java.util.List;

class HttpServiceConfigurations {

	public class HttpServiceConfiguration {
		private String name;
		private String host;
		private String httpMethod;
		private String contentType;
		private String url;
		private String method;
		private List<HttpServiceRequestBodyConfigParam> requestBodyParams;
		private String valueToCompare;

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

		public List<HttpServiceRequestBodyConfigParam> getRequestBodyParams() {
			return requestBodyParams;
		}

		public String getValueToCompare() {
			return valueToCompare;
		}

		public void setValueToCompare(String valueToCompare) {
			this.valueToCompare = valueToCompare;
		}
	}

	public class HttpServiceRequestBodyConfigParam {

		public String name;
		public String value;
	}
	
	public class HttpServicesConfigurationCollection  {
		
		private String defaultHost;
		private String defaultHttpMethod;
		private String defaultContentType;
		public List<HttpServiceConfiguration> services;
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
	}
}
