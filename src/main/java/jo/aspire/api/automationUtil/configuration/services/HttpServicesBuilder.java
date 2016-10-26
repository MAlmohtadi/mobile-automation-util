package jo.aspire.api.automationUtil.configuration.services;

import jo.aspire.api.automationUtil.HttpRequestHandler;
import jo.aspire.api.automationUtil.configuration.services.HttpServiceConfigurations.HttpServiceConfiguration;
import jo.aspire.web.automationUtil.StateHelper;

public class HttpServicesBuilder {

	private ThreadLocal<HttpServicesConfigurationManager> _httpServicesConfigurationManager = null;
	private ThreadLocal<HttpRequestHandler> _httpRequestHandler = new ThreadLocal<HttpRequestHandler>() {
		@Override public HttpRequestHandler initialValue() {
			return HttpRequestHandler.getNewInstance();
		}
	};
	private StateHelper _stateHelper;	
	public HttpServicesBuilder(String servicesConfiguratinFilePath) throws Exception{
		try {
			// setHttpRequestHandler();
			 setHttpServicesConfigurationManager(servicesConfiguratinFilePath);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public HttpServiceRequest build(String serviceName) {
		HttpServiceRequest serviceRequest = null;
		try {
			HttpServiceConfiguration httpServiceConfiguration = getHttpServicesConfigurationManager().getHttpServiceConfiguration(serviceName);
			serviceRequest = new HttpServiceRequest(getHttpRequestHandler(), httpServiceConfiguration);
			//throw exception in case service object is null
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return serviceRequest;
	}
	public StateHelper getStore() {
		return _stateHelper;
	}
	protected HttpRequestHandler getHttpRequestHandler() {
		return _httpRequestHandler.get();
	}
	protected HttpServicesConfigurationManager getHttpServicesConfigurationManager() {
		return _httpServicesConfigurationManager.get();
	}
	protected void setHttpServicesConfigurationManager(final String servicesConfiguratinFilePath) throws Exception {

		if (_httpServicesConfigurationManager == null || _httpServicesConfigurationManager.get() == null) {
			if (servicesConfiguratinFilePath == null || servicesConfiguratinFilePath.trim() == "") {
				throw new Exception("Invalid configuration file (null reference), http services configuration object has not been initiated.");
			}
			try {
				_httpServicesConfigurationManager = new ThreadLocal<HttpServicesConfigurationManager>() {
					@Override public HttpServicesConfigurationManager initialValue() {
						return new HttpServicesConfigurationManager(servicesConfiguratinFilePath);
					}
				};
			} catch (Exception e) {
				throw new Exception(
						"Error while loading configuration file:["
								+ servicesConfiguratinFilePath
								+ "] configuration file content is not valid json format, Exception: ["
								+ e + "]");
			}
		}
	}
}