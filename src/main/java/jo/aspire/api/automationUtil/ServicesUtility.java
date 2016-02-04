package jo.aspire.api.automationUtil;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

public class ServicesUtility {

	public String Param;

	/**
	 * Call service with the input values in the request
	 * 
	 * @param url
	 * @param input
	 * @return service response
	 */
	public String CallService(String url, String input, String Parameter) {
		try {

			Client client = Client.create();

			WebResource webResource = client.resource(url);

			ClientResponse response = webResource.type("application/json")
					.post(ClientResponse.class, input);

			if (response.getStatus() != 200) {
				throw new RuntimeException("Failed : HTTP error code : "
						+ response.getStatus());
			}

			String output = response.getEntity(String.class);
			if (Parameter != null) {
				String[] params = Parameter.split(",");
				if (params.length > 1) {
					String TempOutput = output;
					for (String param : params) {
						TempOutput = JsonReader.GetValueFromString(TempOutput,
								param);
					}
					Param = TempOutput;
				} else {

					Param = JsonReader.GetValueFromStringWithQuotes(output, Parameter);
				}
			}

			if (JsonReader.GetValueFromString(output, "Success")
					.equalsIgnoreCase("true")) {
				return JsonReader.GetValueFromString(output, "Success");
			} else {
				return JsonReader.GetValueFromString(output, "Errors");
			}

		} catch (Exception e) {
			System.out.println("error: service is not returning data");
			return e.getMessage();

		}
	}
	public String CallService(String url, String input, String result,String optional) {
		try {
			Client client = Client.create();
			WebResource webResource = client.resource(url);
			ClientResponse response = webResource.type("application/x-www-form-urlencoded").post(ClientResponse.class,"{}");			
			//if (response.getStatus() != 200) {
				String success = "";
			String output = response.getEntity(String.class);	
			String serviceResult = JsonReader.GetValueFromString(output, input);
			//Param = JsonReader.GetValueFromStringWithQuotes(output, Parameter);	
			if(optional != null )
				if (optional.toLowerCase().equals("more") ){
					if(Integer.parseInt(serviceResult) > Integer.parseInt(result))
						return "Success";
				}else if (optional.toLowerCase().equals("less")) {
					if(Integer.parseInt(serviceResult) < Integer.parseInt(result))
						return "Success";
				}else if (optional.toLowerCase().equals("equal")) {
					if(Integer.parseInt(serviceResult) == Integer.parseInt(result))
						return "Success";
				}					
								
			if(JsonReader.GetValueFromString(output, input).equals(result))
				return "Success";
			return success;

		} catch (Exception e) {
			System.out.println("error: service is not returning data");
			return e.getMessage();

		}
	}
}
