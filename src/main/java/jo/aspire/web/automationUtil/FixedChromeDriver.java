package jo.aspire.web.automationUtil;

import java.util.Map;

import org.openqa.selenium.Capabilities;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.Response;
import org.openqa.selenium.remote.UnreachableBrowserException;

public class FixedChromeDriver extends ChromeDriver {
	 private final int retryCount = 2;

	    public FixedChromeDriver() {
	    }
	    public FixedChromeDriver(ChromeOptions options) {
	    	super(options);
	      }
	    public FixedChromeDriver(Capabilities desiredCapabilities) {
	        super(desiredCapabilities);
	    }

	    public FixedChromeDriver(ChromeDriverService service, Capabilities desiredCapabilities) {
	        super(service, desiredCapabilities);
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
