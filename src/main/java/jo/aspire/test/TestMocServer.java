package jo.aspire.test;

import org.mockserver.model.HttpRequest;

import jo.aspire.generic.MockServerProxy;
import org.json.JSONArray;
import org.json.JSONObject;

public class TestMocServer {

	public static void main(String args[]){
		MockServerProxy test = new MockServerProxy();
		test.startProxy();
		
		try {
			Thread.sleep(60000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		test.clearProxy();
//		test.getRequestContainsText("/controltag");
		JSONObject json=test.getRequestWithIndex("auid=538486050",0);
		test.verifyRequestContainsParameter(json,"auid");
		test.verifyRequestContainsParamWithValue(json,"auid","538486050");
		
		test.stopProxy();
	}
}
