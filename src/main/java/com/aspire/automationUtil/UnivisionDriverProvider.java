package com.aspire.automationUtil;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Date;
import java.util.Hashtable;
import java.util.concurrent.TimeUnit;


import org.openqa.selenium.remote.DesiredCapabilities;

import com.saucelabs.saucerest.SauceREST;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;

public class UnivisionDriverProvider {
	private Hashtable<String, AppiumDriver> drivers = new Hashtable<String, AppiumDriver>();
	private Date date = new Date();
	public enum platform {
	    ANDROID, IOS 
	}
    public static platform getPlatform(){
    	boolean android = TargetPlatform.platformName.toUpperCase().equals("ANDROID");
    	return android ? platform.ANDROID : platform.IOS;
    }
	public AppiumDriver getCurrentDriver() {
		String threadName = Thread.currentThread().getName();
		if (!drivers.containsKey(threadName)) {
			try {
				SetupDriver(threadName);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			if (drivers.get(threadName) == null) {
				try {
					SetupDriver(threadName);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			try {
				String sessionId = drivers.get(threadName).getSessionId().toString();
				if (sessionId.isEmpty())
				{
					SetupDriver(threadName);
				}
			} catch (Exception e) {
				try {
					SetupDriver(threadName);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}

		}
		return drivers.get(threadName);
	}

	public void SetupDriver(String threadName) throws IOException {
		AppiumDriver driver = null;
		// Setup capabilities
		
        DesiredCapabilities capabilities;
        if (getPlatform() == platform.ANDROID) {
			capabilities = DesiredCapabilities.android();
		}
        else 
        {
			capabilities = DesiredCapabilities.iphone();
			if (!SessionHandler.getRunOnSauce())
			{
//		    	Runtime rt = Runtime.getRuntime();
//		    	
//		    	//Remove and install the app
//			    try {
//			        // First, uninstall the app
//			      String[] deleteCommands = new String[]{"/usr/local/bin/ideviceinstaller","-u","94a58d6b3bc89dd8b317b2bb658f79bffaf71cbb", "-U", "com.phunware.univision.ios"};
//			       Process deleteProcess = rt.exec(deleteCommands);
//			      deleteProcess.waitFor();
//			        String path =System.getProperty("user.dir") + File.separator +  TargetPlatform.appFileName;
//			        // Now install it
//			        String[] installCommands = new String[]{"/usr/local/bin/ideviceinstaller","-u","94a58d6b3bc89dd8b317b2bb658f79bffaf71cbb", "-i", path};
//			        Process installProcess = rt.exec(installCommands);
//			        installProcess.waitFor();

//			    }catch (Exception e){
//			        System.out.println(e.toString());
//			    }
			//capabilities.setCapability("udid", EnvirommentManager.getInstance().getProperty("udid"));
		}
        }
		//capabilities.setCapability("platformVersion",TargetPlatform.platformVersion);
		capabilities.setCapability("platformVersion", TargetPlatform.platformVersion);
		capabilities.setCapability("appiumVersion", EnvirommentManager.getInstance().getProperty("appiumVersion"));
		capabilities.setCapability("platformName", TargetPlatform.platformName);
		capabilities.setCapability("deviceName", TargetPlatform.deviceName);
		capabilities.setCapability("deviceOrientation", "portrait");
		capabilities.setCapability("browserName", "");
		capabilities.setCapability("commandTimeout", "300");
		capabilities.setCapability("maxDuration", "10800");

		// Set job name on Sauce Labs
		capabilities.setCapability("name", System.getProperty("user.name") + " - Deportes(And) - " + date);
		String userDir = System.getProperty("user.dir");

		URL serverAddress;
		String localApp = TargetPlatform.appFileName;

		if (SessionHandler.getRunOnSauce()) {
			String user = SessionHandler.getAuth().getUsername();
			String key = SessionHandler.getAuth().getAccessKey();
			capabilities.setCapability("app", "sauce-storage:" + localApp.trim());
			serverAddress = new URL("http://" + user + ":" + key + "@ondemand.saucelabs.com:80/wd/hub");
			  if (getPlatform() == platform.ANDROID) {
				  driver = new AndroidDriver(serverAddress, capabilities);
				}
		        else {	driver = new IOSDriver(serverAddress, capabilities);}
		

		} else {
			String appPath = Paths.get(TargetPlatform.fileDirectory, localApp).toAbsolutePath().toString();
			capabilities.setCapability("app", appPath);
			serverAddress = new URL("http://127.0.0.1:4723/wd/hub");
			try {
				  if (getPlatform() == platform.ANDROID) {
					  driver = new AndroidDriver(serverAddress, capabilities);
					}
			        else {	driver = new IOSDriver(serverAddress, capabilities);}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
		drivers.put(threadName, driver);
		System.out.println("Driver thread name:-----" + threadName+"---- and session id---"+ driver.getSessionId().toString());
		// Helpers.init(driver, serverAddress);
	}

	public void closeDrivers() {
		for (AppiumDriver driver : drivers.values()) {
			if (driver != null) {
				driver.quit();
				driver = null;
			}
		}
	}
	
	public void closeCurrentDriver() throws IOException
	{
		//get the thread name
		String ThreadName=Thread.currentThread().getName();
		//get the driver name
		AppiumDriver driver=drivers.get(ThreadName);
		if(driver!=null)
		{
			driver.quit();
			driver =null;
			//drivers.put(ThreadName, driver);
		}
		
	}
}
