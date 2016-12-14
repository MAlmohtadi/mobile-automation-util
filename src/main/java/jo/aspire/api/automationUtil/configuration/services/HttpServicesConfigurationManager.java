package jo.aspire.api.automationUtil.configuration.services;

import java.net.URL;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;


import jo.aspire.api.automationUtil.configuration.services.HttpServiceConfigurations.HttpServiceConfiguration;
import jo.aspire.api.automationUtil.configuration.services.HttpServiceConfigurations.NameValuePair;
import jo.aspire.api.automationUtil.configuration.services.HttpServiceConfigurations.HttpServicesConfigurationCollection;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.google.gson.Gson;

class HttpServicesConfigurationManager {

	private Hashtable<String,HttpServicesConfigurationCollection> _httpServicesConfigurationCollectionHashtable = new Hashtable<String,HttpServicesConfigurationCollection>();
	private HttpServicesConfigurationCollection _httpServicesConfigurationCollection;

	public HttpServicesConfigurationManager(String servicesConfigFilePath) {
		loadHttpsServicesConfigurationCollection(servicesConfigFilePath);
	}
	public HttpServiceConfiguration getHttpServiceConfiguration(String serviceName) throws Exception {
		HttpServiceConfiguration matchingServiceConfig = null;
		HttpServicesConfigurationCollection httpServicesConfiguration = this.getHttpServicesConfigurationCollection();
		
		for (HttpServiceConfiguration serviceConfig : httpServicesConfiguration.getServices()) {
			if (serviceName.equalsIgnoreCase(serviceConfig.getName())) {
				matchingServiceConfig = serviceConfig;
				break;
			}
		}
		if(matchingServiceConfig == null)//No configuration has been found for this service
		{
			throw new Exception("No configuration found for [Service Name]: [" + serviceName+ "]");
		}
		//load default configuration
		setDefaultConfiguration(matchingServiceConfig, httpServicesConfiguration);
		return matchingServiceConfig;
	}
	protected void loadHttpsServicesConfigurationCollection(String servicesConfigFilePath) {
		HttpServicesConfigurationCollection httpServicesConfigurationCollection = getHttpServicesConfigurationFromHashtable(servicesConfigFilePath);
		if(httpServicesConfigurationCollection != null )
		{
			setHttpServicesConfigurationCollection(httpServicesConfigurationCollection);
		}
		else
		{   
			//load file for the frist time.		
			httpServicesConfigurationCollection = loadServicesConfigJsonFile(servicesConfigFilePath);
			setHttpServicesConfigurationCollection(httpServicesConfigurationCollection);
			//Add service config file to hashtable.
			setHttpServicesConfigurationIntoHashtable(servicesConfigFilePath, httpServicesConfigurationCollection);
		}
	}	
	protected void setDefaultConfiguration(HttpServiceConfiguration matchingServiceConfig, HttpServicesConfigurationCollection httpServicesConfigurationCollection) {
		if(matchingServiceConfig.getHost() == null || matchingServiceConfig.getHost().trim() == "")
			matchingServiceConfig.setHost(httpServicesConfigurationCollection.getDefaultHost());
		if(matchingServiceConfig.getHttpMethod() == null || matchingServiceConfig.getHttpMethod().trim() == "")
			matchingServiceConfig.setHttpMethod(httpServicesConfigurationCollection.getDefaultHttpMethod());
		if(matchingServiceConfig.getConentType() == null ||matchingServiceConfig.getConentType().trim() == "")
			matchingServiceConfig.setConentType(httpServicesConfigurationCollection.getDefaultContentType());
		if(httpServicesConfigurationCollection.getDefaultRequestHeaders() != null)
		{
			List<NameValuePair> defaultRequestHeaders = httpServicesConfigurationCollection.getDefaultRequestHeaders();
			List<NameValuePair> matchingServiceConfigRequestHeaders = matchingServiceConfig.getRequestHeaders();
			if(matchingServiceConfigRequestHeaders == null)
			{
				matchingServiceConfigRequestHeaders = new ArrayList<NameValuePair>();
				matchingServiceConfig.setRequestHeaders(matchingServiceConfigRequestHeaders); 
			}			
			for(NameValuePair defaultRequestHeader : defaultRequestHeaders)
			{
				boolean isExists = isRequestHeaderExists(
						matchingServiceConfigRequestHeaders,
						defaultRequestHeader);
				if(!isExists)
				{
					matchingServiceConfigRequestHeaders.add(defaultRequestHeader);
				}
			}
		}
	}
	private boolean isRequestHeaderExists(
			List<NameValuePair> matchingServiceConfigRequestHeaders,
			NameValuePair defaultRequestHeader) {
		boolean isExists = false;
		for(NameValuePair h : matchingServiceConfigRequestHeaders){
		    if(h.name != null && h.name.toLowerCase().trim().equals(defaultRequestHeader.name.toLowerCase().trim()))
		    {
		    	isExists = true;
		    	break;
		    }
		}
		return isExists;
	}	
	protected void setHttpServicesConfigurationCollection(HttpServicesConfigurationCollection httpServicesConfigurationCollection) {
		 _httpServicesConfigurationCollection = httpServicesConfigurationCollection;
	}
	private HttpServicesConfigurationCollection getHttpServicesConfigurationCollection() {
		return _httpServicesConfigurationCollection;
	}
	protected HttpServicesConfigurationCollection getHttpServicesConfigurationFromHashtable(String key) {
		if(_httpServicesConfigurationCollectionHashtable.containsKey(key)){
			return _httpServicesConfigurationCollectionHashtable.get(key);
		}
		return null;
	}
	protected void setHttpServicesConfigurationIntoHashtable(String key, HttpServicesConfigurationCollection value) {
		_httpServicesConfigurationCollectionHashtable.put(key, value);
	}
	private HttpServicesConfigurationCollection loadServicesConfigJsonFile(String servicesConfigFilePath) {
		HttpServicesConfigurationCollection servicesConfig = null;
		
		URL url = Thread.currentThread().getContextClassLoader().getResource(servicesConfigFilePath);
		Gson gson = new Gson();
		try {
			String fileContent = Resources.toString(url, Charsets.UTF_8);			
			servicesConfig = gson.fromJson(fileContent, HttpServicesConfigurationCollection.class);
			
		}  catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return servicesConfig;
	}	
}