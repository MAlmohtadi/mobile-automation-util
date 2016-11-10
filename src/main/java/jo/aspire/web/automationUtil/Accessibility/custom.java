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

import org.json.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class custom {
	private static final String lineSeparator = System.getProperty("line.separator");
	private final WebDriver driver;
	private final URL script;

	//constructor 
	/**
	 * @param driver
	 * @param script The path of google axs_testing library: https://github.com/GoogleChrome/accessibility-developer-tools
	 */
	public custom(final WebDriver driver, final URL script) {
		this.driver = driver;
		this.script = script;

		this.inject(this.driver, this.script);
	}
	
	//inject library file 
	public static void inject(final WebDriver driver, final URL scriptUrl) {
		final String script = getContents(scriptUrl);
		final ArrayList<WebElement> parents = new ArrayList<WebElement>();

		//injectIntoFrames(driver, script, parents);

		JavascriptExecutor js = (JavascriptExecutor) driver;
		driver.switchTo().defaultContent();
		js.executeScript(script);
	}
	private static void injectIntoFrames(final WebDriver driver, final String script, final ArrayList<WebElement> parents) {
		final JavascriptExecutor js = (JavascriptExecutor) driver;
		final List<WebElement> frames = driver.findElements(By.tagName("iframe"));

		for (WebElement frame : frames) {
			driver.switchTo().defaultContent();

			if (parents != null) {
				for (WebElement parent : parents) {
					driver.switchTo().frame(parent);
				}
			}

			driver.switchTo().frame(frame);
			js.executeScript(script);

			ArrayList<WebElement> localParents = (ArrayList<WebElement>) parents.clone();
			localParents.add(frame);

			injectIntoFrames(driver, script, localParents);
		}
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

	//analyze and get json result 
//	public String verifyWithJsonOutput() {
//		String results = String.format("axs.Audit.run;");
//		return execute(results);
//	}
	
	/**
	 * use this assert to verify the output assertEquals("*** Begin accessibility audit results ***\nAn accessibility audit found \n*** End accessibility audit results ***", verifyoutput);
	 */
	public ArrayList verify() {
		String results = String.format("return window.aspire.audit();");
		return execute(results);
	}

	private ArrayList execute(final String command, final Object... args) {
		this.driver.manage().timeouts().setScriptTimeout(30, TimeUnit.SECONDS);
		Object response = ((JavascriptExecutor) this.driver).executeScript(command, args);
		return (ArrayList) response;
	}
	
	public void writeResults(final String name, final Object output) {
		Writer writer = null;

		try {
			writer = new BufferedWriter(
					new OutputStreamWriter(
					new FileOutputStream(name + ".json"), "utf-8"));

			writer.write(output.toString());
		} catch (IOException ignored) {
		} finally {
			try {writer.close();}
            catch (Exception ignored) {}
		}
	}
}

