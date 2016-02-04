package jo.aspire.mobile.automationUtil;

import jo.aspire.helper.EnvirommentManager;

import com.saucelabs.common.SauceOnDemandAuthentication;
import com.saucelabs.common.SauceOnDemandSessionIdProvider;
import com.saucelabs.junit.SauceOnDemandTestWatcher;
import org.apache.commons.logging.LogFactory;
import org.junit.Rule;



public class SessionHandler implements SauceOnDemandSessionIdProvider {
	DriverProvider driverProvider;
	public SessionHandler(DriverProvider driverProvider){
		this.driverProvider = driverProvider;
	}
	
	private static boolean runOnSauce;
    //or to run on device :

	public static boolean getRunOnSauce(){
		return runOnSauce;
	}
	public static SauceOnDemandAuthentication getAuth(){
		return auth;
	}
    /** Authenticate to Sauce with environment variables SAUCE_USER_NAME and SAUCE_API_KEY **/
    private static SauceOnDemandAuthentication auth;
    // or to run on device    
    
    static {
        // Disable annoying cookie warnings.
        // WARNING: Invalid cookie header
        LogFactory.getFactory().setAttribute("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.NoOpLog");
        
        try {
			runOnSauce = Boolean.parseBoolean(EnvirommentManager.getInstance()
					.getProperty("UseSauceLabs"));
			auth = new SauceOnDemandAuthentication(EnvirommentManager
					.getInstance().getProperty("saucelabsUsername"),
					EnvirommentManager.getInstance().getProperty("saucelabsAccessKey"));
		} catch (Exception e) {
		 e.printStackTrace();
		}
    }



    /** Report pass/fail to Sauce Labs **/
    // false to silence Sauce connect messages.
    public @Rule
    SauceOnDemandTestWatcher reportToSauce = new SauceOnDemandTestWatcher(this, auth, false);
   
    
    /** If we're not on Sauce then return null otherwise SauceOnDemandTestWatcher will error. **/
    public String getSessionId() {
        return runOnSauce ? driverProvider.getCurrentDriver().getSessionId().toString() : null;
    }
}