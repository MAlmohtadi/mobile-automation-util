package jo.aspire.generic;

import static org.mockserver.integration.ClientAndProxy.startClientAndProxy;
import static org.mockserver.integration.ClientAndServer.startClientAndServer;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.mockserver.integration.ClientAndProxy;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.HttpRequest;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;

public class MockServerProxy {
	public static ClientAndProxy proxy;
	public static ClientAndServer mockServer;

	/**
	 * This method is used to start MockServer and MockProxy. ClientServer well
	 * start on local host with port 1080, And ClientProxy well start with port
	 * 1090.
	 * 
	 * @exception IOException
	 *                Address already in used.
	 */
	public void startProxy() {
		mockServer = startClientAndServer(1080);
		proxy = startClientAndProxy(1090);
	}

	/**
	 * This method is used to start MockServer and MockProxy. ClientServer well
	 * start on local host with port 1080, And ClientProxy well start with port
	 * 1090.
	 * 
	 * @param clientPort
	 *            to start ClientProxy with specific port
	 * @exception IOException
	 *                Address already in used.
	 */
	public void startProxy(Integer clientPort) {
		mockServer = startClientAndServer(1080);
		proxy = startClientAndProxy(clientPort);
	}

	/**
	 * This method is used to start MockServer and MockProxy. ClientServer well
	 * start on local host with port 1080, And ClientProxy well start with port
	 * 1090.
	 * 
	 * @param serverPort
	 *            to start ClientServer with specific port
	 * @param clientPort
	 *            to start ClientProxy with specific port
	 * @exception IOException
	 *                Address already in used.
	 */
	public void startProxy(Integer serverPort, Integer clientPort) {
		mockServer = startClientAndServer(serverPort);
		proxy = startClientAndProxy(clientPort);
	}

	/**
	 * This method is used to start MockServer and MockProxy. Client Server well
	 * start on local host with port 1080, And ClientProxy well start with port
	 * 1090.
	 * 
	 * @param serverPort
	 *            to start Client Server with specific port
	 * @param clientPort
	 *            to start ClientProxy with specific port
	 * @param remoteIp
	 *            to start ClientProxy on specific remote IP
	 * @param remotePort
	 *            to start ClientProxy on specific remote port
	 * @exception IOException
	 *                Address already in used.
	 */
	public void startProxy(Integer serverPort, Integer clientPort, String remoteIp, Integer remotePort) {
		mockServer = startClientAndServer(serverPort);
		proxy = new ClientAndProxy(clientPort, remoteIp, remotePort);
	}

	/**
	 * This method is used to stop MockServer and MockProxy.
	 */
	public void stopProxy() {
		proxy.stop();
		mockServer.stop();
	}

	/**
	 * This method is used to clear MockServer and MockProxy.
	 */
	public void clearProxy() {
		proxy.clear(HttpRequest.request());

	}

	/**
	 * This method is used to get all request form MockProxy that contains
	 * specific text by convert every request to URL request then verify if the
	 * URL contains the text.
	 * 
	 * @param textToSearchForIt
	 *            type String.
	 * @return method will return List<JSONObject> that contains
	 *         textToSearchForIt.
	 */
	public List<JSONObject> getRequestContainsText(String textToSearchForIt) {
		JSONArray getAllRequest = new JSONArray(proxy.retrieveAsJSON(HttpRequest.request()));
		JSONObject request = null;
		JSONArray queryParams = new JSONArray();
		String param = "";
		Object json;
		Object tempJson;
		String url = "";
		List<JSONObject> requestsMatches = new ArrayList<>();
		for (int i = 0; i < getAllRequest.length(); i++) {
			request = new JSONObject(getAllRequest.get(i).toString());
			json = Configuration.defaultConfiguration().jsonProvider().parse(request.toString());
			url = JsonPath.read(json, "$.headers[?(@.name =~ /.*Host/i)].values[0]").toString();
			url = url.replace("[", "").replace("]", "").replace("\"", "");
			if (request.toString().contains("path")) {
				url = url + JsonPath.read(json, "$.path").toString();
			}
			if (request.toString().contains("queryStringParameters")) {
				queryParams = new JSONArray(JsonPath.read(json, "$.queryStringParameters").toString());
				for (int j = 0; j < queryParams.length(); j++) {
					// get parameters with values then add them to URL
					if (j == 0) {
						url = url + "?";
					} else {
						url = url + "&";
					}
					tempJson = Configuration.defaultConfiguration().jsonProvider().parse(queryParams.get(j).toString());
					param = JsonPath.read(tempJson, "$.name").toString();
					url = url + param + "=";
					if (tempJson.toString().contains("values")) {
						param = JsonPath.read(tempJson, "$.values[0]").toString();
						url = url + param;
					}

				}
			}

			if (url.trim().contains(textToSearchForIt.trim())) {
				requestsMatches.add(request);
			}
		}

		return requestsMatches;
	}

	/**
	 * This method is used to get specific request with index form MockProxy
	 * that contains specific text by convert every request to URL request then
	 * verify if the URL contains the text then return JSONObject.
	 * 
	 * @param textToSearchForIt
	 *            type String.
	 * @param index
	 *            type Integer , we used it to retrieve request as JSONObject
	 *            with specific index form all request that contains the text.
	 * 
	 * @return method will return JSONObject that contains textToSearchForIt or
	 *         null if all request not contains the text.
	 */
	public JSONObject getRequestWithIndex(String textToSearchForIt, int index) {
		List<JSONObject> requests = getRequestContainsText(textToSearchForIt);
		if ((requests.size() > 0) && (requests.size() > index)) {
			return requests.get(index);
		}
		return null;
	}

	/**
	 * This method is used to verify if the request that have parameter that
	 * equal parameterToCheckIt and will return boolean condition.
	 * 
	 * @param request
	 *            type JSONObject.
	 * @param parameterToCheckIt
	 *            type String.
	 * 
	 * @return method will return true if the request have one of parameters
	 *         equal parameterToCheckIt , and return false if request not have
	 *         at least one parameter equal parameterToCheckIt
	 */
	public boolean verifyRequestContainsParameter(JSONObject request, String parameterToCheckIt) {
		if (!request.toString().contains("queryStringParameters")) {
			return false;
		} else {
			Object json = Configuration.defaultConfiguration().jsonProvider().parse(request.toString());
			JSONArray parametersInsideRequest = new JSONArray(
					JsonPath.read(json, "$.queryStringParameters").toString());

			for (int i = 0; i < parametersInsideRequest.length(); i++) {
				json = Configuration.defaultConfiguration().jsonProvider()
						.parse(parametersInsideRequest.get(i).toString());
				if (JsonPath.read(json, "$.name").toString().equals(parameterToCheckIt)) {
					return true;
				}

			}
		}
		return false;
	}

	/**
	 * This method is used to verify if the request that have parameter that
	 * equal parameter and the value for this parameter equal value then will
	 * return boolean condition.
	 * 
	 * @param request
	 *            type JSONObject.
	 * @param parameter
	 *            type String.
	 * @param value
	 *            type String.
	 * 
	 * @return method will return true if the request have one of parameters
	 *         equal parameter and it's value equal value , and return false if
	 *         request not have at least one parameter equal parameter or value
	 *         of parameter not equal value
	 */
	public boolean verifyRequestContainsParamWithValue(JSONObject request, String parameter, String value) {
		if (!request.toString().contains("queryStringParameters")) {
			return false;
		} else {
			Object json = Configuration.defaultConfiguration().jsonProvider().parse(request.toString());
			JSONArray parametersInsideRequest = new JSONArray(
					JsonPath.read(json, "$.queryStringParameters").toString());

			for (int i = 0; i < parametersInsideRequest.length(); i++) {
				json = Configuration.defaultConfiguration().jsonProvider()
						.parse(parametersInsideRequest.get(i).toString());
				if (JsonPath.read(json, "$.name").toString().equals(parameter)
						&& JsonPath.read(json, "$.values[0]").toString().equals(value)) {
					return true;
				}

			}
		}
		return false;

	}

}
