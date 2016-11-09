package jo.aspire.test;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.remote.DesiredCapabilities;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import jo.aspire.generic.EnvirommentManager;
import jo.aspire.mobile.automationUtil.TargetPlatform;

public class TestMobileDriver {
public static void main(String[] args){
	DesiredCapabilities capabilities;
	capabilities = DesiredCapabilities.android();
	capabilities.setCapability("platformVersion",
			"5.0");
//
//	capabilities.setCapability("platformName",
//			"Android");
	
	
	capabilities.setCapability("deviceName", "192.168.56.101:5555");
	capabilities.setCapability("commandTimeout", "600");
	capabilities.setCapability("commandTimeout", "600");
	capabilities.setCapability("appPackage", "me.scan.android.client"); 
	capabilities.setCapability("appActivity", "me.scan.android.client.ui.ScanActivity"); 
	capabilities.setCapability("appWaitActivity", "me.scan.android.client.ui.ScanActivity"); 
	capabilities.setCapability("appWaitPackage", "me.scan.android.client"); 
	URL serverAddress =  null;
	try {
		serverAddress = new URL("http://127.0.0.1:"
				+"4723" + "/wd/hub");
	} catch (MalformedURLException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	AppiumDriver driver = new AndroidDriver(serverAddress, capabilities);
	try{
	driver.removeApp("com.july.univision");
	}catch(Exception ex){
	}
	try{
	driver.installApp("/Users/khalid/Documents/load/Slideshow/deportes-android.apk");
	}catch(Exception ex){
	}
	driver.manage().timeouts().implicitlyWait(60, TimeUnit.SECONDS);
	driver.findElement(By.id("android:id/button1")).click();
	driver.findElement(By.id("android:id/button1")).click();
	
	driver.quit();
//	driver.findElement(for_find("bata"));
}

}
