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
	@SuppressWarnings("rawtypes")
	private Hashtable<String, AppiumDriver> drivers = new Hashtable<String, AppiumDriver>();
	private Hashtable<String, serverInfo> servers = new Hashtable<String, serverInfo>();
	private Date date = new Date();
	public static String ResetApp = "null";
	public static String autoAcceptAlerts = "null";
	
	public static String deviceType ;
	
	public static Hashtable<String, String> sessions = new Hashtable<String, String>();
	public static Hashtable<String, String> sauceConnectTunnelsId = new Hashtable<String, String>();
	public static ArrayList<String> sauceTunnelsIdList = new ArrayList<>();
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
	
	
	public static void initializeSauceConnectTunnelsId() {
		int threads=0;
		try{
		threads= Integer.parseInt(EnvirommentManager.getInstance()
				.getProperty("threads"));
		}catch(Exception ex){
			threads =1;
		}
		for (int i =0; i< threads; i++){
			sauceTunnelsIdList.add(("my-tun" + (1 + i)).toString());
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
			try {
				capabilities.setCapability("commandTimeout", EnvirommentManager.getInstance().getProperty("commandTimeout"));
			} catch (Exception e1) {
				capabilities.setCapability("commandTimeout", "600");
			}
			capabilities.setCapability("maxDuration", "10800");
			capabilities.setCapability("nativeInstrumentsLib", true);
			
			
			try {
				ResetApp = EnvirommentManager.getInstance().getProperty("autoAcceptAlerts");
			} catch (Exception e) {
			}
			
			if(autoAcceptAlerts.equals("true")){
					capabilities.setCapability("notificationsAuthorized", true);
					capabilities.setCapability("locationServicesAuthorized", true);
				//capabilities.setCapability("autoAcceptAlerts", true);
			}else{
				capabilities.setCapability("waitForAppScript", "$.delay(5000);$.acceptAlert()");
			}
			
		
			
			try {
				capabilities.setCapability("appPackage", EnvirommentManager.getInstance().getProperty("appPackage"));
			} catch (Exception e) {
			
			}
			
			try {
				capabilities.setCapability("appActivity", EnvirommentManager.getInstance().getProperty("appActivity"));
			} catch (Exception e) {
				
			}
			
			if (getPlatform() == platform.ANDROID) {
				try {
					deviceType = EnvirommentManager.getInstance().getProperty("deviceType");
				} catch (Exception e) {
					deviceType = "phone";
				}
				
				capabilities.setCapability("deviceType",deviceType);
			}
			
			boolean analytics;
			try{
				analytics = Boolean.parseBoolean(EnvirommentManager.getInstance()
						.getProperty("analytics"));
				}catch(Exception ex){
					analytics = false;
				}
			
			if(analytics && SauceLabeSessionHandler.getRunOnSauce()){
				String tunnelID = null;
				
				if(sauceConnectTunnelsId.get(Thread.currentThread().getName()) != null)
				{
					tunnelID = sauceConnectTunnelsId.get(Thread.currentThread().getName());
				}
				else{
					
					if(sauceTunnelsIdList.isEmpty()){
						initializeSauceConnectTunnelsId();
						
					}
					
					sauceConnectTunnelsId.put(Thread.currentThread().getName(),sauceTunnelsIdList.get(0));
					tunnelID = sauceTunnelsIdList.get(0);
					sauceTunnelsIdList.remove(0);
				}
				
				capabilities.setCapability("tunnelIdentifier", tunnelID);
				System.out.println("tunnelIdentifier: " + tunnelID);
			}
			
			
			
			
			
			try {
				ResetApp = EnvirommentManager.getInstance().getProperty("ResetApp");
			} catch (Exception e) {
			}
			
			if (ResetApp.equals("true")){
				capabilities.setCapability("fullReset",true);
				capabilities.setCapability("noReset", false);
			}else if (ResetApp.equals("false")){
				capabilities.setCapability("fullReset",false);
				capabilities.setCapability("noReset", true);	
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

			// System.out.println(currentServer.serverPort + ":"+ currentServer.deviceUUID);
			
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
				//	driver.manage().timeouts().implicitlyWait(180,TimeUnit.SECONDS);

				}

		}

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

				}
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}

	}
}
