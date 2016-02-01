package com.aspire.api.automationUtil;

import javax.ws.rs.core.MultivaluedMap;

import org.json.JSONObject;

public interface GenericParser {
	int getStatus();
	void setUrl(String url);
	void createRequest(String url);
	void addHeader(String name, String value);
	void setBody(MultivaluedMap<String,String> formData);
	JSONObject postResponseAsJsonObject();
	JSONObject putResponseAsJsonObject();
	JSONObject deleteResponseAsJsonObject();
	JSONObject getResponseAsJsonObject();
}
