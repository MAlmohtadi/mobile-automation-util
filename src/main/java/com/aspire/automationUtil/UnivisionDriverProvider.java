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
	public static Hashtable<String, String> sessions = new Hashtable<String, String>();
	public enum platform {
		ANDROID, IOS
	}

	public static platform getPlatform() {
		boolean android = TargetPlatform.platformName.toUpperCase().equals(
				"ANDROID");
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
				sessions.put(threadName, sessionId);
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
		} else {
			capabilities = DesiredCapabilities.iphone();
			if (!SessionHandler.getRunOnSauce()) {
			}
		}
		if (!TargetPlatform.runOnAmazon) {
			capabilities.setCapability("platformVersion",
					TargetPlatform.platformVersion);
			capabilities.setCapability("appiumVersion", EnvirommentManager
					.getInstance().getProperty("appiumVersion"));
			capabilities.setCapability("platformName",
					TargetPlatform.platformName);
			capabilities.setCapability("deviceName", TargetPlatform.deviceName);
			capabilities.setCapability("deviceOrientation", "portrait");
			capabilities.setCapability("browserName", "");
			capabilities.setCapability("commandTimeout", "600");
			capabilities.setCapability("maxDuration", "10800");

			// Set job name on Sauce Labs
			capabilities.setCapability("name", System.getProperty("user.name")
					+ " - Deportes(And) - " + date);
		}
		String userDir = System.getProperty("user.dir");

		URL serverAddress;
		String localApp = TargetPlatform.appFileName;

		if (!TargetPlatform.runOnAmazon && SessionHandler.getRunOnSauce()) {
			String user = SessionHandler.getAuth().getUsername();
			String key = SessionHandler.getAuth().getAccessKey();
			capabilities.setCapability("app",
					"sauce-storage:" + localApp.trim());
			serverAddress = new URL("http://" + user + ":" + key
					+ "@ondemand.saucelabs.com:80/wd/hub");
			if (getPlatform() == platform.ANDROID) {
				driver = new AndroidDriver(serverAddress, capabilities);
			} else {
				driver = new IOSDriver(serverAddress, capabilities);
			}

		} else {
			String appPath = Paths.get(TargetPlatform.fileDirectory, localApp)
					.toAbsolutePath().toString();
			if (!TargetPlatform.runOnAmazon) {
				capabilities.setCapability("app", appPath);
			}
			serverAddress = new URL("http://127.0.0.1:4723/wd/hub");
			try {
				if (getPlatform() == platform.ANDROID) {
					driver = new AndroidDriver(serverAddress, capabilities);
				} else {
					driver = new IOSDriver(serverAddress, capabilities);
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
		drivers.put(threadName, driver);
		System.out.println("Driver thread name:-----" + threadName
				+ "---- and session id---" + driver.getSessionId().toString());
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

	public void closeCurrentDriver() throws IOException {
		// get the thread name
		String ThreadName = Thread.currentThread().getName();
		// get the driver name
		AppiumDriver driver = drivers.get(ThreadName);
		if (driver != null) {
			driver.quit();
			driver = null;
			// drivers.put(ThreadName, driver);
		}

	}
}
