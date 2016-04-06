package jo.aspire.test;

import java.net.MalformedURLException;
import java.net.URL;

import org.openqa.selenium.By;
import org.openqa.selenium.remote.DesiredCapabilities;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.AppiumDriver;
import jo.aspire.generic.EnvirommentManager;
import jo.aspire.mobile.automationUtil.TargetPlatform;

public class TestMobileDriver {
public static void main(String[] args){
	DesiredCapabilities capabilities;
	capabilities = DesiredCapabilities.iphone();
	capabilities.setCapability("platformVersion",
			TargetPlatform.platformVersion);

	capabilities.setCapability("platformName",
			TargetPlatform.platformName);
	capabilities.setCapability("deviceName", TargetPlatform.deviceName);
	capabilities.setCapability("deviceOrientation", "portrait");
	capabilities.setCapability("browserName", "");
	capabilities.setCapability("commandTimeout", "600");
	capabilities.setCapability("commandTimeout", "600");
	capabilities.setCapability("app", "com.gilt.enterprise.ios.nightly"); 
	URL serverAddress =  null;
	try {
		serverAddress = new URL("http://127.0.0.1:"
				+"4723" + "/wd/hub");
	} catch (MalformedURLException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	AppiumDriver driver = new IOSDriver(serverAddress, capabilities);
//	driver.findElement(for_find("bata"));
}

}
