package jo.aspire.web.automationUtil;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Driver;
import java.util.HashMap;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.jbehave.web.selenium.DelegatingWebDriverProvider;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.LocalFileDetector;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.safari.SafariDriver;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.remote.MobileCapabilityType;
import jo.aspire.generic.EnvirommentManager;

public class DriverProvider extends DelegatingWebDriverProvider {
	private RemoteWebDriver driver;
	private static String browser;
	public static HashMap<Long, WebDriver> allThreads = new HashMap<Long, WebDriver>();

	@Override
	public void initialize() {

		boolean runOnSource = Boolean.parseBoolean(EnvirommentManager.getInstance().getProperty("useSouceLabs"));

		boolean runOnStack = false;
		try {
			runOnStack = Boolean.parseBoolean(EnvirommentManager.getInstance().getProperty("useBrowserStack"));
		} catch (Exception ex) {
		}

		if (runOnSource) {
			DesiredCapabilities capabilities = new DesiredCapabilities();
			if (PlatformInformation.browserName != null && !PlatformInformation.browserName.isEmpty()) {
				capabilities.setCapability("browserName", PlatformInformation.browserName);
			}
			if (PlatformInformation.browserVersion != null && !PlatformInformation.browserVersion.isEmpty()) {
				capabilities.setCapability("version", PlatformInformation.browserVersion);
			}
			capabilities.setCapability("platform", PlatformInformation.platformName);
			if (PlatformInformation.screenResolution != null && !PlatformInformation.screenResolution.isEmpty()) {
				capabilities.setCapability("screenResolution", PlatformInformation.screenResolution);
			}
			if (PlatformInformation.deviceName != null && !PlatformInformation.deviceName.isEmpty()) {
				capabilities.setCapability("deviceName", PlatformInformation.deviceName);
			}
			if (PlatformInformation.deviceOrientation != null && !PlatformInformation.deviceOrientation.isEmpty()) {
				capabilities.setCapability("deviceOrientation", PlatformInformation.deviceOrientation);
			}

			try {
				this.driver = new RemoteWebDriver(
						new URL("http://" + EnvirommentManager.getInstance().getProperty("username") + ":"
								+ EnvirommentManager.getInstance().getProperty("accessKey")
								+ "@ondemand.saucelabs.com:80/wd/hub"),
						capabilities);



				this.driver.setFileDetector(new LocalFileDetector());

				allThreads.put(Thread.currentThread().getId(), this.driver );

			}

			catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			delegate.set((WebDriver) driver);
		} else if (runOnStack) {
			String stackUsername = EnvirommentManager.getInstance().getProperty("StackUsername");
			String stackKey = EnvirommentManager.getInstance().getProperty("StackKey");
			String stackUrl = "https://" + stackUsername + ":" + stackKey + "@hub-cloud.browserstack.com/wd/hub";
			DesiredCapabilities caps = new DesiredCapabilities();
			if (PlatformInformation.browserName != null && !PlatformInformation.browserName.isEmpty()) {
				caps.setCapability("browser", PlatformInformation.browserName);
			}
			if (PlatformInformation.browserVersion != null && !PlatformInformation.browserVersion.isEmpty()) {
				caps.setCapability("browser_version", PlatformInformation.browserVersion);
			}
			caps.setCapability("os", PlatformInformation.platformName);
			caps.setCapability("os_version", PlatformInformation.platformVersion);
			if (PlatformInformation.screenResolution != null && !PlatformInformation.screenResolution.isEmpty()) {
				caps.setCapability("resolution", PlatformInformation.screenResolution);
			}
			if (PlatformInformation.projectName != null && !PlatformInformation.projectName.isEmpty()) {
				caps.setCapability("project", PlatformInformation.projectName);
			}
			caps.setCapability("browserstack.debug", "true");
			try {
				this.driver = new RemoteWebDriver(new URL(stackUrl), caps);
				allThreads.put(Thread.currentThread().getId(), this.driver);
			} catch (Exception ex) {
			}
		} else {
			browser = PlatformInformation.browserName;
			WebDriver webDriver = createDriver();
			allThreads.put(Thread.currentThread().getId(), webDriver);
			delegate.set(webDriver);
		}

		// hash map for thread per driver

	}

	private WebDriver createDriver() {
		if (PlatformInformation.isMobile) {
			return createAndroidBrowser();
		} else {
			if (browser.equals("Phantom")) {
				return createPhantomDrive();
				// } else if (browser.equals("htmlUnit")) {
				// return createHtmlUnitDriver();
			} else if (browser.equals("chrome")) {
				return createChromeDriver();
			} else if (browser.equals("ie32")) {
				return createInternetExplorerDriver32();
			} else if (browser.equals("ie64")) {
				return createInternetExplorerDriver64();
			} else if (browser.equals("safari")) {
				return createSafariDriver();
			} else if (browser.equals("remote")) {
				return createRemoteDriver();
			} else if(browser.equals("edge")) {
				return createEdgeDriver();
			} else {
				return createFirefoxDriver();
			}

		}
	}
protected WebDriver createEdgeDriver(){
	System.setProperty("webdriver.edge.driver",
			System.getProperty("user.dir") + "/webdrivers/edge/MicrosoftWebDriver.exe");
	EdgeDriver driver = new EdgeDriver();

	return driver;
}
	protected WebDriver createAndroidBrowser() {
		DesiredCapabilities caps = new DesiredCapabilities();
		caps.setCapability(MobileCapabilityType.BROWSER_NAME, browser);
		caps.setCapability(MobileCapabilityType.PLATFORM_VERSION, PlatformInformation.browserVersion);
		caps.setCapability(MobileCapabilityType.PLATFORM_NAME, PlatformInformation.browserName);
		caps.setCapability(MobileCapabilityType.DEVICE_NAME, PlatformInformation.deviceName);
		URL serverAddress = null;
		try {
			serverAddress = new URL("http://127.0.0.1:" + "4723" + "/wd/hub");
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		AndroidDriver driver = new AndroidDriver(serverAddress, caps);
		return driver;
	}

	protected RemoteWebDriver createRemoteDriver() {
		DesiredCapabilities cap = null;
		if (PlatformInformation.remoteBrowserName.equals("chrome")) {
			cap = DesiredCapabilities.chrome();
		} else {
			cap = DesiredCapabilities.firefox();
		}
		try {
			return new RemoteWebDriver(new URL(PlatformInformation.remoteUrl), cap);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	protected ChromeDriver createChromeDriver() {
		if (OSValidator.isMac()) {

			System.setProperty("webdriver.chrome.driver",
					System.getProperty("user.dir") + "/webdrivers/chrome/mac/chromedriver");
		} else if (OSValidator.isWindows()) {

			System.setProperty("webdriver.chrome.driver",
					System.getProperty("user.dir") + "/webdrivers/chrome/win/chromedriver.exe");
		} else if (OSValidator.isUnix()) {

			System.setProperty("webdriver.chrome.driver",
					System.getProperty("user.dir") + "/webdrivers/chrome/linux/chromedriver");
		}
		ChromeOptions options = new ChromeOptions();
		options.addArguments("start-maximized");
		HashMap<String, Object> chromePrefs = new HashMap<String, Object>();

		chromePrefs.put("profile.default_content_settings.popups", 0);
		chromePrefs.put("profile.default_content_setting_values.notifications", 2);
		chromePrefs.put("download.default_directory", System.getProperty("user.dir") + File.separator + "Temp");
		options.setExperimentalOption("prefs", chromePrefs);
		DesiredCapabilities cap = DesiredCapabilities.chrome();
		if (PlatformInformation.isProxy) {
			addProxyCapabilities(cap, PlatformInformation.proxyHost, PlatformInformation.proxyPort);
		}
		// options.addArguments("user-data-dir="+System.getProperty("user.dir")
		// + File.separator + "SeleniumChromeDriveProfile");
		cap.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
		options.addArguments("disable-popup-blocking");
		cap.setCapability(ChromeOptions.CAPABILITY, options);
		options.addArguments("test-type");
		options.addArguments("--disable-extensions");

		return new FixedChromeDriver(cap);
	}

	protected PhantomJSDriver createPhantomDrive() {
		
		DesiredCapabilities capabilities = DesiredCapabilities.phantomjs();
		if (OSValidator.isMac()) {
			capabilities.setCapability(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY,
					System.getProperty("user.dir") + "/webdrivers/phantom/mac/phantomjs");
		} else if (OSValidator.isWindows()) {

			capabilities.setCapability(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY,
					System.getProperty("user.dir") + "/webdrivers/phantom/win/phantomjs.exe");
		} else if (OSValidator.isUnix()) {
			capabilities.setCapability(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY,
					System.getProperty("user.dir") + "/webdrivers/phantom/linux/phantomjs");
		}
		String[] phantomArgs = new String[] { "--webdriver-loglevel=NONE" };
		capabilities.setCapability(PhantomJSDriverService.PHANTOMJS_CLI_ARGS, phantomArgs);
		Logger.getLogger(PhantomJSDriverService.class.getName()).setLevel(Level.OFF);
		Logger.getLogger(PhantomJSDriverService.class.getName()).setLevel(Level.OFF);
		capabilities.setCapability("JavascriptEnabled", true);
		capabilities.setCapability("cssSelectorsEnabled", true);
		capabilities.setCapability("applicationCacheEnabled", false);
		capabilities.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
		capabilities.setCapability(CapabilityType.SUPPORTS_ALERTS, true);
		capabilities.setCapability(CapabilityType.SUPPORTS_JAVASCRIPT, true);
		return new PhantomJSDriver(capabilities);
	}

	protected FirefoxDriver createFirefoxDriver() {
		FirefoxProfile firefoxProfile = new FirefoxProfile();
		// FirefoxProfile firefoxProfile = profile.getProfile("default");
		String path = System.getProperty("user.dir") + File.separator + "Temp";
		firefoxProfile.setPreference("browser.download.folderList", 2);
		firefoxProfile.setPreference("browser.download.manager.showWhenStarting", false);
		firefoxProfile.setPreference("browser.download.dir", path);
		firefoxProfile.setPreference("browser.helperApps.neverAsk.saveToDisk",
				"application/vnd.ms-excel,application/vnd.openxmlformats-officedocument.spreadsheetml.sheet, text/comma-separated-values, text/csv, application/csv, application/excel, application/vnd.msexcel, text/anytext , application/x-xpinstall,application/x-zip,application/x-zip-compressed,application/octet-stream,application/zip,application/pdf,application/msword,text/plain,application/octet,text/html, application/x-csv, text/x-csv , application/msexcel,binary/octet-stream , text/html ,application/xhtml+xml,application/xml,application/json");
		if (PlatformInformation.isProxy) {
			firefoxProfile.setPreference("network.proxy.type", 1);
			firefoxProfile.setPreference("network.proxy.http", PlatformInformation.proxyHost);
			firefoxProfile.setPreference("network.proxy.http_port", PlatformInformation.proxyPort);
			firefoxProfile.setPreference("network.proxy.ssl", PlatformInformation.proxyHost);
			firefoxProfile.setPreference("network.proxy.ssl_port", PlatformInformation.proxyPort);
		}
		return new FixedFirefoxDriver(firefoxProfile);
	}

	protected InternetExplorerDriver createInternetExplorerDriver32() {
		System.setProperty("webdriver.ie.driver",
				System.getProperty("user.dir") + "/webdrivers/ie32/IEDriverServer.exe");
		return new InternetExplorerDriver();
	}

	protected InternetExplorerDriver createInternetExplorerDriver64() {
		System.setProperty("webdriver.ie.driver",
				System.getProperty("user.dir") + "/webdrivers/ie64/IEDriverServer.exe");

		return new InternetExplorerDriver();
	}

	protected SafariDriver createSafariDriver() {
		return new SafariDriver();
	}

	private DesiredCapabilities addProxyCapabilities(DesiredCapabilities capabilities, String zapProxyHost,
			int zapProxyPort) {
		Proxy proxy = new Proxy();
		proxy.setHttpProxy(zapProxyHost + ":" + zapProxyPort);
		proxy.setSslProxy(zapProxyHost + ":" + zapProxyPort);
		capabilities.setCapability("proxy", proxy);
		return capabilities;
	}

}
