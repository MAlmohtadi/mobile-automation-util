package jo.aspire.mobile.automationUtil;

import static java.util.Arrays.asList;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

import org.openqa.selenium.remote.DesiredCapabilities;

import com.aspire.automationReport.IDriverProvider;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;

public class DriverProvider implements IDriverProvider {
	@SuppressWarnings("rawtypes")
	private Hashtable<String, AppiumDriver> drivers = new Hashtable<String, AppiumDriver>();
	private Hashtable<String, serverInfo> servers = new Hashtable<String, serverInfo>();
	private Date date = new Date();
	public static String ResetApp = "null";
	public static String autoAcceptAlerts = "null";

	public static String deviceType;

	public static Hashtable<String, String> sessions = new Hashtable<String, String>();
	public static Hashtable<String, String> sauceConnectTunnelsId = new Hashtable<String, String>();
	public static ArrayList<String> sauceTunnelsIdList = new ArrayList<>();
	public static ArrayList<String> appiumPortsList = new ArrayList<>();
	public static ArrayList<String> udid = new ArrayList<>();

	// public static ThreadLocal<JsonObject> driverInfo = new
	// ThreadLocal<JsonObject>();
	public static JsonObject driverInfo = new JsonObject();

	public static void setDriverToRun(JsonObject di) {
		DriverProvider.driverInfo = di;
	}

	public enum platform {
		ANDROID, IOS
	}

	public class serverInfo {
		public int serverPort;
		public String deviceUUID;
	}

	public static String jobName = null;

	public static platform getPlatform() {
		String platformName = getDriverInfo().get("platformName").getAsString();
		boolean isAndroid = platformName.toUpperCase().equals("ANDROID");
		return isAndroid ? platform.ANDROID : platform.IOS;
	}

	public static JsonObject getDriverInfo() {
		return driverInfo;
	}

	public static void initializePortsAndUUIDs() {
		initializePorts();
		initializeUdids();
	}

	public static void initializePorts() {
		Integer initialPort = 4733;
		int threads = 0;
		try {
			threads = Integer.parseInt(getDriverInfo().get("threads").getAsString());
		} catch (Exception ex) {
			threads = 1;
		}
		for (int i = 0; i < threads; i++) {
			appiumPortsList.add(initialPort.toString());
			initialPort += 10;
		}
	}

	public static void initializeUdids() {

		String udids = getDriverInfo().get("udid").getAsString();
		if (udids.contains(",")) {
			udid.addAll(asList(udids.split(",")));
		} else {
			udid.add(udids);
		}

	}

	public static void initializeSauceConnectTunnelsId() {
		int threads = 0;
		try {
			threads = Integer.parseInt(getDriverInfo().get("threads").getAsString());
		} catch (Exception ex) {
			threads = 1;
		}
		for (int i = 0; i < threads; i++) {
			sauceTunnelsIdList.add(("my-tun" + (1 + i)).toString());
		}
	}

	@SuppressWarnings("rawtypes")
	public AppiumDriver getCurrentDriver() {
		String threadName = Thread.currentThread().getName();
		if (!drivers.containsKey(threadName) || (drivers.get(threadName) == null)) {
			try {
				SetupDriver(threadName);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		try {
			String sessionId = drivers.get(threadName).getSessionId().toString();
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
			String sessionId = drivers.get(threadName).getSessionId().toString();
			sessions.put(threadName, sessionId);
		}
		return drivers.get(threadName);
	}

	public serverInfo getCurrentServerInfo(String threadName) {
		serverInfo currentServer = new serverInfo();
		if (servers.get(threadName) != null) {
			currentServer = servers.get(threadName);
		} else {
			serverInfo server = new serverInfo();
			boolean isRemote = Boolean.parseBoolean(getDriverInfo().get("isRemote").getAsString());

			if (!isRemote) {
				if (getPlatform() == platform.ANDROID) {
					synchronized (udid) {
						server.deviceUUID = udid.get(0);
						udid.remove(0);
					}
				}
				synchronized (appiumPortsList) {
					server.serverPort = Integer.parseInt(appiumPortsList.get(0).trim());
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
		serverInfo currentServer = getCurrentServerInfo(threadName);
		AppiumDriver driver = null;

		DesiredCapabilities capabilities;
		if (getPlatform() == platform.ANDROID) {
			capabilities = DesiredCapabilities.android();
		} else {
			capabilities = DesiredCapabilities.iphone();
		}

		// set capabilities
		JsonObject capabilitiesObj = getDriverInfo().get("capabilities").getAsJsonObject();
		Set<Map.Entry<String, JsonElement>> capabilitiesSet = capabilitiesObj.entrySet();

		for (Map.Entry<String, JsonElement> entry : capabilitiesSet) {
			capabilities.setCapability(entry.getKey(), entry.getValue());
		}

		boolean analytics = Boolean.parseBoolean(getDriverInfo().get("isAnalytics").getAsString());

		// if analytics and run on saucelab
		boolean runOnSauceLab = false;
		boolean runOnAmazon = false;

		if (Boolean.parseBoolean(getDriverInfo().get("isRemote").getAsString())) {
			runOnSauceLab = getDriverInfo().get("remote").getAsJsonObject().get("type").getAsString().toLowerCase()
					.contains("sauce");
			runOnAmazon = getDriverInfo().get("remote").getAsJsonObject().get("type").getAsString().toLowerCase()
					.contains("amazon");
		}

		if (!runOnAmazon) {
			ResetApp = getDriverInfo().get("autoAcceptAlerts").getAsString();
			// if analytics and run on saucelab
			if (analytics && runOnSauceLab) {
				String tunnelID = null;

				if (sauceConnectTunnelsId.get(Thread.currentThread().getName()) != null) {
					tunnelID = sauceConnectTunnelsId.get(Thread.currentThread().getName());
				} else {

					if (sauceTunnelsIdList.isEmpty()) {
						initializeSauceConnectTunnelsId();
					}

					sauceConnectTunnelsId.put(Thread.currentThread().getName(), sauceTunnelsIdList.get(0));
					tunnelID = sauceTunnelsIdList.get(0);
					sauceTunnelsIdList.remove(0);
				}

				capabilities.setCapability("tunnelIdentifier", tunnelID);
				System.out.println("tunnelIdentifier: " + tunnelID);
			}

			if (ResetApp.equals("true")) {
				capabilities.setCapability("fullReset", true);
				capabilities.setCapability("noReset", false);
			} else if (ResetApp.equals("false")) {
				capabilities.setCapability("fullReset", false);
				capabilities.setCapability("noReset", true);
			}

			// Set job name on Sauce Labs
			capabilities.setCapability("name", System.getProperty("user.name") + " - " + jobName + "(And) - " + date);
		}
		String userDir = System.getProperty("user.dir");
		URL serverAddress;
		String localApp = getDriverInfo().get("appFileName").getAsString();
		String remoteUrl = getDriverInfo().get("remote").getAsJsonObject().get("remoteUrl").getAsString();

		if (!runOnAmazon && runOnSauceLab) {

			// capabilities.setCapability("app", "sauce-storage:" +
			// localApp.trim());

			serverAddress = new URL(remoteUrl);
			if (getPlatform() == platform.ANDROID) {
				driver = new AndroidDriver(serverAddress, capabilities);
			} else {
				driver = new IOSDriver(serverAddress, capabilities);
			}

		} else {
			String appPath = Paths.get(getDriverInfo().get("fileDirectory").getAsString(), localApp).toAbsolutePath()
					.toString();
			if (!runOnAmazon) {
				capabilities.setCapability("app", appPath);
			}

			// System.out.println(currentServer.serverPort + ":"+
			// currentServer.deviceUUID);
			if (!runOnAmazon) {
				serverAddress = new URL("http://127.0.0.1:" + currentServer.serverPort + "/wd/hub");
			} else {
				serverAddress = new URL("http://127.0.0.1:4723" + "/wd/hub");
			}

			if (getPlatform() == platform.ANDROID) {
				if (!runOnAmazon && !runOnSauceLab) {
					capabilities.setCapability("udid", currentServer.deviceUUID);
				}
				driver = AndroidDriver(serverAddress, capabilities);
			} else {

				driver = IOSDriver(serverAddress, capabilities);
				// driver.manage().timeouts().implicitlyWait(180,TimeUnit.SECONDS);

			}

		}

		drivers.put(threadName, driver);
		System.out.println("Driver thread name:-----" + threadName + "---- and session id---"
				+ driver.getSessionId().toString() + ", UUID:" + currentServer.deviceUUID);
		// Helpers.init(driver, serverAddress);
	}

	@SuppressWarnings("rawtypes")
	public AppiumDriver AndroidDriver(URL serverAddress, DesiredCapabilities capabilities) {
		return new AndroidDriver(serverAddress, capabilities);
	}

	@SuppressWarnings("rawtypes")
	public AppiumDriver IOSDriver(URL serverAddress, DesiredCapabilities capabilities) {
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
		try {
			String ThreadName = Thread.currentThread().getName();
			// get the driver name
			AppiumDriver driver = drivers.get(ThreadName);
			if (driver != null) {
				// driver.resetApp();
				Boolean closeDriver = Boolean.parseBoolean(getDriverInfo().get("closeDriver").getAsString());

				if (closeDriver) {
					try {
						driver.quit();
						System.out.println("closing: " + driver.getRemoteAddress().getPort() + ":"
								+ driver.getCapabilities().getCapability("udid"));

					} catch (Exception ex) {
						ex.printStackTrace();
					}
					drivers.put(ThreadName, driver);
				} else {
					driver.resetApp();
					System.out.println("reset: " + driver.getRemoteAddress().getPort() + ":"
							+ driver.getCapabilities().getCapability("udid"));

				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	@Override
	public boolean isDriverInitialized() {
		String threadName = Thread.currentThread().getName();
		return drivers.get(threadName) != null;

	}
}
