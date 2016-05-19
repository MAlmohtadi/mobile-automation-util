package jo.aspire.mobile.automationUtil;

import static java.util.Arrays.asList;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.concurrent.TimeUnit;

import jo.aspire.generic.EnvirommentManager;

import org.openqa.selenium.Alert;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class DriverProvider {
	private Hashtable<String, AppiumDriver> drivers = new Hashtable<String, AppiumDriver>();
	private Hashtable<String, serverInfo> servers = new Hashtable<String, serverInfo>();
	private Date date = new Date();
	public static String autoAcceptAlerts = "false";
	public static Hashtable<String, String> sessions = new Hashtable<String, String>();
	public static ArrayList<String> appiumPortsList = new ArrayList<>();
	public static ArrayList<String> udid = new ArrayList<>();
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

	public static void initializePortsAndUUIDs(){
		initializePorts();
		initializeUdids();
	}
	public static void initializePorts() {
//		if (appiumPortsList == null) {
//			appiumPortsList = new ArrayList<String>();
//			if (EnvirommentManager.getInstance()
//					.getProperty("UseLocaleEmulators").contains("true")) {
//				String Ports = EnvirommentManager.getInstance().getProperty(
//						"Ports");
//				appiumPortsList.addAll(asList(Ports.split(",")));
//			} else {
//				appiumPortsList.add("4723");
//			}
//		}
		Integer initialPort = 4733;
		int threads=0;
		try{
		threads= Integer.parseInt(EnvirommentManager.getInstance()
				.getProperty("threads"));
		}catch(Exception ex){
			threads =1;
		}
		for (int i =0; i< threads; i++){
			appiumPortsList.add(initialPort.toString());
			initialPort +=10;
		}
	}

	public static  void initializeUdids() {
	
			String udids = EnvirommentManager.getInstance().getProperty("udid");
			if (udids.contains(",")) {
				udid.addAll(asList(udids.split(",")));
			} else {
				udid.add(udids);
			}

	}
	@SuppressWarnings("rawtypes")
	public AppiumDriver getCurrentDriver() {
		String threadName = Thread.currentThread().getName();
		if (!drivers.containsKey(threadName)
				|| (drivers.get(threadName) == null)) {
			try {
				SetupDriver(threadName);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		try {
			String sessionId = drivers.get(threadName).getSessionId()
					.toString();
			if (sessionId.isEmpty()) {

				SetupDriver(threadName);
			}
			sessions.put(threadName, sessionId);
		} catch (Exception e) {
			try {
				SetupDriver(threadName);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			String sessionId = drivers.get(threadName).getSessionId()
					.toString();
			sessions.put(threadName, sessionId);
		}
		return drivers.get(threadName);
	}

public serverInfo getCurrentServerInfo(String threadName)
{
	serverInfo currentServer = new serverInfo();
	if (servers.get(threadName) != null) {
		currentServer = servers.get(threadName);
	} else {
		serverInfo server = new serverInfo();
		
		if (!EnvirommentManager.getInstance().getProperty("UseSauceLabs")
				.contains("true")) {
			if(getPlatform() == platform.ANDROID){
				synchronized (udid) {
					server.deviceUUID = udid.get(0);
					udid.remove(0);
				}
			}
			synchronized (appiumPortsList) {
				server.serverPort = Integer.parseInt(appiumPortsList.get(0)
						.trim());
				appiumPortsList.remove(0);
			}
		}
		servers.put(threadName, server);
		currentServer = server;
		// servers.ad
	}
	return currentServer;
}
	@SuppressWarnings({ "rawtypes", "unused" })
	public void SetupDriver(String threadName) throws IOException {
		serverInfo currentServer =getCurrentServerInfo(threadName);
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
			capabilities.setCapability("commandTimeout", "900");
			capabilities.setCapability("maxDuration", "10800");
			capabilities.setCapability("nativeInstrumentsLib", true);
			capabilities.setCapability("waitForAppScript", "$.delay(3000);$.acceptAlert()");
			capabilities.setCapability("noReset", true);
						
			try {
				autoAcceptAlerts = EnvirommentManager.getInstance().getProperty("autoAcceptAlerts");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if (autoAcceptAlerts.equals("true")) {
			//	capabilities.setCapability("autoAcceptAlerts", true);
//				capabilities.setCapability("notificationsAuthorized", true);
//				capabilities.setCapability("locationServicesAuthorized", false);
			}
			
			// Set job name on Sauce Labs
			capabilities.setCapability("name", System.getProperty("user.name")
					+ " - " + jobName + "(And) - " + date);
		}
		String userDir = System.getProperty("user.dir");
		URL serverAddress;
		String localApp = TargetPlatform.appFileName;
		if (!TargetPlatform.runOnAmazon
				&& SauceLabeSessionHandler.getRunOnSauce()) {
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

			System.out.println(currentServer.serverPort + ":"
					+ currentServer.deviceUUID);
			if(!TargetPlatform.runOnAmazon){
			serverAddress = new URL("http://127.0.0.1:"
					+ currentServer.serverPort + "/wd/hub");
			}else{
				serverAddress = new URL("http://127.0.0.1:4723" + "/wd/hub");
			}

				if (getPlatform() == platform.ANDROID) {
					if (!TargetPlatform.runOnAmazon
							&& !SauceLabeSessionHandler.getRunOnSauce()) {
						capabilities.setCapability("udid",
								currentServer.deviceUUID);
					}
					driver = AndroidDriver(serverAddress, capabilities);
				} else {

					driver = IOSDriver(serverAddress, capabilities);
					driver.manage().timeouts().implicitlyWait(180,TimeUnit.SECONDS);

				}

		}

				
		if (getPlatform() == platform.IOS) {
			Alert alert = driver.switchTo().alert();
			boolean autoAcceptAlerts = true;
			int AcceptAlertsCounter = 0;
			int tryCounter = 0;

			while (autoAcceptAlerts) {
				try {
					Thread.sleep(2000);
					alert.accept();
					AcceptAlertsCounter++;
					if (AcceptAlertsCounter == 1) {
						autoAcceptAlerts = false;
					}
				} catch (Exception e) {
					tryCounter++;
					if (tryCounter == 5) {
						autoAcceptAlerts = false;
					}
				}
			}
		}
		
		
		
		
//		if (EnvirommentManager.getInstance().getProperty("UseLocaleEmulators")
//				.contains("true")) {
//			driver.manage().timeouts().implicitlyWait(45, TimeUnit.SECONDS);
//		} else {
//			driver.manage().timeouts().implicitlyWait(180, TimeUnit.SECONDS);
//		}
		drivers.put(threadName, driver);
		System.out.println("Driver thread name:-----" + threadName
				+ "---- and session id---" + driver.getSessionId().toString() + ", UUID:"+currentServer.deviceUUID );
		// Helpers.init(driver, serverAddress);
	}

	@SuppressWarnings("rawtypes")
	public  AppiumDriver AndroidDriver(URL serverAddress,
			DesiredCapabilities capabilities) {
		return new AndroidDriver(serverAddress, capabilities);
	}

	@SuppressWarnings("rawtypes")
	public  AppiumDriver IOSDriver(URL serverAddress,
			DesiredCapabilities capabilities) {
		return new IOSDriver(serverAddress, capabilities);
	}

	@SuppressWarnings("rawtypes")
	public void closeDrivers() {
		for (AppiumDriver driver : drivers.values()) {
			if (driver != null) {
				driver.quit();
				driver = null;
			}
		}
	}

	@SuppressWarnings("rawtypes")
	public void closeCurrentDriver() throws IOException {
		// get the thread name
		try{
		 String ThreadName = Thread.currentThread().getName();
			// get the driver name
			AppiumDriver driver = drivers.get(ThreadName);
			if (driver != null) {
				// driver.resetApp();
				

				if (EnvirommentManager.getInstance().getProperty("closeDriver")
						.contains("true")) {
//					if (!EnvirommentManager.getInstance()
//							.getProperty("UseSauceLabs").contains("true")) {
//						synchronized (udid) {
//							udid.add(driver.getCapabilities().getCapability(
//									"udid")
//									+ "");
//						}
//
//						synchronized (appiumPortsList) {
//							appiumPortsList.add(driver.getRemoteAddress().getPort()
//									+ "");
//						}
//					}
					try{
					driver.quit();
					System.out.println("closing: "+driver.getRemoteAddress().getPort() + ":"
							+ driver.getCapabilities().getCapability("udid"));
					
					}catch(Exception ex){
						ex.printStackTrace();
					}
					drivers.put(ThreadName, driver);
				}else{
					driver.resetApp();
					System.out.println("reset: "+driver.getRemoteAddress().getPort() + ":"
							+ driver.getCapabilities().getCapability("udid"));
//					try {
//						Thread.currentThread().sleep(5000);
//					} catch (InterruptedException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
				}
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}

	}
}
