package jo.aspire.web.automationUtil.Accessibility;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class custom {
	private static final String lineSeparator = System.getProperty("line.separator");
	private final WebDriver driver;

	//constructor 
	/**
	 * @param driver
	 * @param script The path of google axs_testing library: https://github.com/GoogleChrome/accessibility-developer-tools
	 */
	public custom(final WebDriver driver) {
		this.driver = driver;
	}

	private static String getContents(final URL script) {
		final StringBuilder sb = new StringBuilder();
		BufferedReader reader = null;

		try {
			URLConnection connection = script.openConnection();
			reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String line;

			while ((line = reader.readLine()) != null) {
				sb.append(line);
				sb.append(lineSeparator);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			if (reader != null) {
				try { reader.close(); }
                catch (IOException ignored) {}
			}
		}

		return sb.toString();
	}
	
	/**
	 * use this assert to verify the output assertEquals("*** Begin accessibility audit results ***\nAn accessibility audit found \n*** End accessibility audit results ***", verifyoutput);
	 */
	public String verify(final URL script) {
		return execute(getContents(script));
	}

	private String execute(final String command, final Object... args) {
		this.driver.manage().timeouts().setScriptTimeout(30, TimeUnit.SECONDS);
		Object response = ((JavascriptExecutor) this.driver).executeScript(command, args);
		return (String) response;
	}
	
	public void writeResults(final String name, final Object output) {
		Writer writer = null;

		try {
			writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(name + ".json"), "utf-8"));
			writer.write(output.toString());
		} catch (IOException ignored) {
		} finally {
			try {writer.close();}
            catch (Exception ignored) {}
		}
	}
}

