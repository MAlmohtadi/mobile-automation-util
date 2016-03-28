package jo.aspire.mobile.automationUtil;

import static java.util.Arrays.asList;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.concurrent.TimeUnit;

import jo.aspire.generic.EnvirommentManager;

import org.openqa.selenium.Alert;
import org.openqa.selenium.remote.DesiredCapabilities;



public class DriverProvider {
	private Hashtable<String, AppiumDriver> drivers = new Hashtable<String, AppiumDriver>();
	private Hashtable<String, serverInfo> servers = new Hashtable<String, serverInfo>();
	private Date date = new Date();
	public static Hashtable<String, String> sessions = new Hashtable<String, String>();
	private ArrayList<String> PortsList = new ArrayList<String>();
	private ArrayList<String> udid = new ArrayList<String>();

	public enum platform {
		ANDROID, IOS
	}

	public class serverInfo {
		public int serverPort;
		public String deviceUUID;
	}

	public static String jobName = null;

	public static platform getPlatform() {
		boolean android = TargetPlatform.platformName.toUpperCase().equals(
				"ANDROID");
		return android ? platform.ANDROID : platform.IOS;
	}

	public AppiumDriver getCurrentDriver() {
		String threadName = Thread.currentThread().getName();
		if (PortsList.size() == 0) {
			  if (EnvirommentManager.getInstance().getProperty("UseLocaleEmulators").contains("true")) {
				  String Ports = EnvirommentManager.getInstance().getProperty("Ports");
				  PortsList.addAll(asList(Ports.split(",")));
			  }else{
				  PortsList.add("4723");  
			  }
		}

			
			  if (udid.size() == 0) {
				  String udids = EnvirommentManager.getInstance().getProperty("udid");
				  udid.addAll(asList(udids.split(",")));
			  }
		

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
				String sessionId = drivers.get(threadName).getSessionId()
						.toString();
				sessions.put(threadName, sessionId);
				if (sessionId.isEmpty()) {
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

	@SuppressWarnings("null")
	public void SetupDriver(String threadName) throws IOException {
		serverInfo currentServer = new serverInfo();
		if (servers.get(threadName) != null) {
			currentServer = servers.get(threadName) ;
		} else {

			serverInfo server = new serverInfo();
			synchronized (udid) {
				 if (EnvirommentManager.getInstance().getProperty("UseLocaleEmulators").contains("true")) {
				server.deviceUUID = udid.get(0);
				udid.remove(0);
				 }

			}
			synchronized (PortsList) {
				 if (EnvirommentManager.getInstance().getProperty("UseLocaleEmulators").contains("true")) {
				server.serverPort = Integer.parseInt(PortsList.get(0).trim());
				PortsList.remove(0);
				 }
			}
			servers.put(threadName, server);
			currentServer = server;
			// servers.ad
		}
		AppiumDriver driver = null;
		// Setup capabilities

		DesiredCapabilities capabilities;
		if (getPlatform() == platform.ANDROID) {
			capabilities = DesiredCapabilities.android();
		} else {
			capabilities = DesiredCapabilities.iphone();
			if (!SauceLabeSessionHandler.getRunOnSauce()) {
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
			capabilities.setCapability("nativeInstrumentsLib", true);
			// capabilities.setCapability("autoAcceptAlerts",
			// "$.delay(10000); $.acceptAlert();");
			capabilities.setCapability("waitForAppScript", "$.delay(3000);");

			// capabilities.setCapability("fullReset", "true");
			// capabilities.setCapability("noReset", "true");
			// capabilities.setCapability("appActivity",
			// "com.univision.SplashActivity");
			// capabilities.setCapability("appWaitActivity",
			// "com.univision.SplashActivity");
			// capabilities.setCapability("appPackage", "com.univision");
			// capabilities.setCapability("appWaitPackage", "com.univision");

			// Set job name on Sauce Labs
			capabilities.setCapability("name", System.getProperty("user.name")
					+ " - " + jobName + "(And) - " + date);
		}
		String userDir = System.getProperty("user.dir");

		URL serverAddress;
		String localApp = TargetPlatform.appFileName;

		if (!TargetPlatform.runOnAmazon && SauceLabeSessionHandler.getRunOnSauce()) {
			String user = SauceLabeSessionHandler.getAuth().getUsername();
			String key = SauceLabeSessionHandler.getAuth().getAccessKey();
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

		
				System.out.println(currentServer.serverPort + ":" + currentServer.deviceUUID);
				serverAddress = new URL("http://127.0.0.1:" + currentServer.serverPort
						+ "/wd/hub".toString());
			

				try {
					if (getPlatform() == platform.ANDROID) {
						 if (EnvirommentManager.getInstance().getProperty("UseLocaleEmulators").contains("true")) {
						capabilities.setCapability("udid", currentServer.deviceUUID);
						 }
						driver = new AndroidDriver(serverAddress, capabilities);
					} else {
						driver = new IOSDriver(serverAddress, capabilities);

					}

				} catch (Exception e) {
					e.printStackTrace();
				}
			
		}

		if (getPlatform() == platform.IOS) {
			Alert alert = driver.switchTo().alert();
			boolean autoAcceptAlerts = true;
			int AcceptAlertsCounter = 0;
			int tryCounter = 0;

			while (autoAcceptAlerts) {
				try {
					alert.accept();
					AcceptAlertsCounter++;
					if (AcceptAlertsCounter == 2) {
						autoAcceptAlerts = false;
					}
				} catch (Exception e) {
					tryCounter++;
					if (tryCounter == 10) {
						autoAcceptAlerts = false;
					}
				}
			}
		}

		driver.manage().timeouts().implicitlyWait(180, TimeUnit.SECONDS);
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
