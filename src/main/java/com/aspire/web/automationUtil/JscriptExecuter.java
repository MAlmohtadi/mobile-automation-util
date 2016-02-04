package aspire.common;

import java.util.concurrent.TimeUnit;

import org.jbehave.web.selenium.FluentWebDriverPage;
import org.jbehave.web.selenium.WebDriverProvider;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.FluentWait;

import com.google.common.base.Predicate;

/**
 * 
 * This class aims to execute JavaScript with timer to avoid asynchronous issue.
 *
 */
public class JscriptExecuter extends FluentWebDriverPage {

	public JscriptExecuter(WebDriverProvider webDriverProvider) {
		super(webDriverProvider);
	}

	//

	/**
	 * This method is used to perform a javascript executer with timer to avoid
	 * async issue. This method takes two parameter, we should call this method
	 * if we have return type. e.g.return $(\"td:contains('Current
	 * Status:')+td\").text().
	 * 
	 * @param strSelector
	 *            :This is the selector we need to execute.
	 * @param stateHelperName
	 *            : as this method returns string, the value will be saved in a
	 *            statehelper, this param represents the key name.
	 * @return String.
	 */
	public String executeScript(final String strSelector,
			final String stateHelperName, final boolean isPositiveSelector) {

		new FluentWait<JavascriptExecutor>(
				(JavascriptExecutor) getDriverProvider().get()).withTimeout(10,
				TimeUnit.SECONDS).until(new Predicate<JavascriptExecutor>() {
			public boolean apply(JavascriptExecutor executor) {

				return checkMethodToExecute(executor, strSelector,
						stateHelperName, isPositiveSelector);

			}
		});

		String StateHelperValue = StateHelper.getStepState(stateHelperName)
				.toString();
		String returnValue = StateHelperValue == "NoDataFound" ? ""
				: StateHelperValue;
		return returnValue;
	}

	/**
	 * This method is used to perform a javascript executer with timer to avoid
	 * async issue. This method will be used for the events that not return
	 * value, e.g. .Blur(), .click()
	 * 
	 * @param strSelector
	 *            :This is the selector we need to execute.
	 */
	public void executeScript(final String strSelector) {

		new FluentWait<JavascriptExecutor>(
				(JavascriptExecutor) getDriverProvider().get()).withTimeout(10,
				TimeUnit.SECONDS).until(new Predicate<JavascriptExecutor>() {
			public boolean apply(JavascriptExecutor executor) {

				return checkMethodToExecute(executor, strSelector, "", false);

			}
		});
	}

	/**
	 * This method is used to perform a javascript executer with timer to avoid
	 * async issue. this method will be used for the events that does not return
	 * value, but it takes the webelement as prameter as well,
	 * e.g.arguments[0].click();
	 * 
	 * @param strSelector
	 *            : This is the selector we need to execute.
	 * @param element
	 *            : This is the element we send when we have multiple elements.
	 *            e.g WebElement element = elements.getWebElement();
	 */
	public void executeScript(final String strSelector, final WebElement element) {

		new FluentWait<JavascriptExecutor>(
				(JavascriptExecutor) getDriverProvider().get()).withTimeout(10,
				TimeUnit.SECONDS).until(new Predicate<JavascriptExecutor>() {
			public boolean apply(JavascriptExecutor executor) {

				return checkMethodToExecute(executor, strSelector, element);

			}
		});
	}

	/**
	 * This private method used as a map to know which method should we call,
	 * both String executeScript(final String strSelector,final String
	 * stateHelperName) and Void executeScript(strSelector) is calling this
	 * method.
	 * 
	 * @param executor
	 *            : This is the javascript excutor that we need to use in apply
	 *            method.
	 * @param strSelector
	 *            :This is the selector we need to execute.
	 * @param stateHelperName
	 *            : as this method returns string, the value will be saved in a
	 *            statehelper, this param represents the key name.
	 * @return
	 */
	private boolean checkMethodToExecute(JavascriptExecutor executor,
			String strSelector, String stateHelperName,
			boolean isPositiveSelector) {
		boolean executionResult = false;
		try {
			switch (stateHelperName) {
			// Empty stateHelperName then this is a trigger with no return type.
			case "":
				Trigger(executor, strSelector);
				executionResult = true;
				break;
			default:
				executionResult = executeScript(executor, strSelector,
						stateHelperName, isPositiveSelector);
				break;
			}

		} catch (Exception e) {

			Logger.log(e.getMessage());
			throw (e);
		}

		return executionResult;
	}

	/**
	 * Overloaded method for checkMethodToExecute, this method with only trigger
	 * with Webelement parameter.
	 * 
	 * @param executor
	 *            : This is the javascript excutor that we need to use in apply
	 *            method.
	 * @param strSelector
	 *            :This is the selector we need to execute.
	 * @param element
	 *            : This is the element we send when we have multiple elements.
	 *            e.g WebElement element = elements.getWebElement();
	 * @return
	 */
	private boolean checkMethodToExecute(JavascriptExecutor executor,
			String strSelector, WebElement element) {
		boolean executionResult = false;
		try {
			Trigger(executor, strSelector, element);
			executionResult = true;
		} catch (Exception ex) {
			Logger.log(ex.getMessage());
			throw (ex);
		}

		return executionResult;
	}

	/**
	 * This mwthod will execute the script, and then add the value to
	 * statehelper.
	 * 
	 * @param executor
	 *            : This is the javascript excutor that we need to use in apply
	 *            method.
	 * @param strSelector
	 *            : his is the selector we need to execute.
	 * @param stateHelperName
	 *            : as this method returns string, the value will be saved in a
	 *            statehelper, this param represents the key name.
	 * @return String
	 */
	private boolean executeScript(JavascriptExecutor executor,
			String strSelector, String stateHelperName,boolean isPositiveSelector) {
		String executionResult = "";
		try {
			executionResult = executor.executeScript(strSelector).toString();
			if (executionResult != null && !executionResult.isEmpty()) {
				StateHelper.setStepState(stateHelperName, executionResult);
			}
		} catch (Exception e) {
			Logger.log(e.getMessage());
			throw (e);
		}

		return getReturnValue(isPositiveSelector, executionResult,
				stateHelperName);
	}

	private boolean getReturnValue(boolean isPositiveSelector,
			String executionResult, String stateHelperName) {
		boolean returnValue = false;
		if (isPositiveSelector) {
			returnValue = executionResult.length() > 0;
		} else {
			returnValue = executionResult.length() == 0;
			StateHelper.setStepState(stateHelperName, "NoDataFound");
		}
		return returnValue;
	}

	/**
	 * This method will trigger the script, e.g. .click.
	 * 
	 * @param executor
	 *            :This is the javascript excutor that we need to use in apply
	 *            method.
	 * @param strSelector
	 *            :This is the selector we need to execute.
	 */
	private void Trigger(JavascriptExecutor executor, String strSelector) {

		executor.executeScript(strSelector);

	}

	/**
	 * This method will trigger the script with additional parameter Webelement,
	 * e.g. arg[0].click.
	 * 
	 * @param executor
	 *            : This is the javascript excutor that we need to use in apply
	 *            method.
	 * @param strSelector
	 *            :This is the selector we need to execute.
	 * @param element
	 *            : This is the element we send when we have multiple elements.
	 *            e.g WebElement element = elements.getWebElement();
	 */
	private void Trigger(JavascriptExecutor executor, String strSelector,
			WebElement element) {

		executor.executeScript(strSelector, element);

	}
}
