package jo.aspire.api.automationUtil.configuration.services;

import jo.aspire.api.automationUtil.HttpRequestHandler;
import jo.aspire.api.automationUtil.configuration.services.HttpServiceConfigurations.HttpServiceConfiguration;
import jo.aspire.web.automationUtil.StateHelper;

public class HttpServicesBuilder {

	private HttpServicesConfigurationManager _httpServicesConfigurationManager;
	private HttpRequestHandler _httpRequestHandler;
	private StateHelper _stateHelper;
	public HttpServicesBuilder(String servicesConfiguratinFilePath) throws Exception{
		try {
			 setHttpRequestHandler();
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
		return _httpRequestHandler;
	}
	protected void setHttpRequestHandler() {
		if (_httpRequestHandler == null) {
			_httpRequestHandler = HttpRequestHandler.getInstance();
		}
	}
	protected HttpServicesConfigurationManager getHttpServicesConfigurationManager() {
		return _httpServicesConfigurationManager;
	}
	protected void setHttpServicesConfigurationManager(String servicesConfiguratinFilePath) throws Exception {		
		if (_httpServicesConfigurationManager == null) {
			if (servicesConfiguratinFilePath == null || servicesConfiguratinFilePath.trim() == "") {
				throw new Exception("Invalid configuration file (null reference), http services configuration object has not been initiated.");
			}
			try {
				_httpServicesConfigurationManager = new HttpServicesConfigurationManager(servicesConfiguratinFilePath);
			} catch (Exception e) {
				throw new Exception(
						"Error while loading configuration file:["
								+ servicesConfiguratinFilePath
								+ "] configuration file content is valid json format, Exception: ["
								+ e + "]");
			}
		}
	}
}