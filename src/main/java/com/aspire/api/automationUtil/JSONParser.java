package com.aspire.api.automationUtil;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

import org.json.JSONException;
import org.json.JSONObject;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.WebResource.Builder;
import com.sun.jersey.core.util.MultivaluedMapImpl;

public class JSONParser implements GenericParser {

	private int currentStatus;
	private WebResource webResource;
	private Builder builder;
	private String URL;
	private MultivaluedMap<String,String> methodBody = new MultivaluedMapImpl();
	
	public int getStatus() {
		return currentStatus;
	}

	public void setUrl(String url) {
		URL = url;
	}

	public void createRequest(String url) {
		URL = url;
		if (url.contains(Constants.URL_PARAMETER)) {
			return;
		}
		Client client = Client.create();
		webResource = client.resource(url);

		builder = webResource
				.type("application/json")
				.type(MediaType.APPLICATION_FORM_URLENCODED_TYPE)
				.accept("application/json");
	}

	public void addHeader(String name, String value) {
		if (webResource == null) {
			createRequest(URL);
		}
		builder = builder.header(name, value);
	}
	
	public void setBody(MultivaluedMap<String,String> body) {
		 methodBody = body;
	}

	public JSONObject getResponseAsJsonObject() {
		try {
			ClientResponse response = builder.get(ClientResponse.class);
			return handleResponse(response);
		} catch (Exception e) {
			System.out.println("error: service is not retrieving data");
		}
		return null;
	}

	public JSONObject postResponseAsJsonObject() {
		try {
			ClientResponse response = builder.post(ClientResponse.class, methodBody);
			return handleResponse(response);
		} catch (Exception e) {
			System.out.println("error: service is not returning data");
		}
		return null;
	}
	
	public JSONObject putResponseAsJsonObject() {
		try {
			 
			ClientResponse response = builder.put(ClientResponse.class, methodBody);
			return handleResponse(response);
		} catch (Exception e) {
			System.out.println("error: service is not returning data");
		}
		return null;
	}
	
	public JSONObject deleteResponseAsJsonObject() {
		try {	 
			ClientResponse response = builder.delete(ClientResponse.class, methodBody);
			return handleResponse(response);
		} catch (Exception e) {
			System.out.println("error: service is not returning data");
		}
		return null;
	}

	private JSONObject handleResponse(ClientResponse response) throws JSONException {
		currentStatus = response.getStatus();
		String output = response.getEntity(String.class);
		JSONObject info = new JSONObject(output);
		return info;
	}
}
