package com.aspire.web.automationUtil;

import org.jbehave.web.selenium.PropertyWebDriverProvider;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;

public class AspirePropertyWebDriverProvider extends PropertyWebDriverProvider {

	@Override
	protected FirefoxDriver createFirefoxDriver() {

		FirefoxProfile profile = new FirefoxProfile();
		profile.setPreference("dom.max_chrome_script_run_time", 0);
		profile.setPreference("dom.max_script_run_time", 0);
		return new FirefoxDriver(profile);
	}
}