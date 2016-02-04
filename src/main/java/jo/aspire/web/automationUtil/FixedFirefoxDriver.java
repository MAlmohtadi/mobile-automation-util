package jo.aspire.web.automationUtil;

import java.util.Map;

import org.openqa.selenium.Capabilities;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.remote.Response;
import org.openqa.selenium.remote.UnreachableBrowserException;

public class FixedFirefoxDriver extends FirefoxDriver {
	 private final int retryCount = 2;

	    public FixedFirefoxDriver() {
	    }

	    public FixedFirefoxDriver(FirefoxProfile profile) {
	         super(profile);   
	    }  
	    
	    public FixedFirefoxDriver(Capabilities desiredCapabilities) {
	        super(desiredCapabilities);
	    }

	   

	    @Override
	    protected Response execute(String driverCommand, Map<String, ?> parameters) {
	        int retryAttempt = 0;

	        while (true) {
	            try {

	                return super.execute(driverCommand, parameters);

	            } catch (UnreachableBrowserException e) {
	                retryAttempt++;
	                if (retryAttempt > retryCount) {
	                    throw e;
	                }
	            }
	        }
	    }
}
