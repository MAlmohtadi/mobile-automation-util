package  jo.aspire.generic;

import org.jbehave.core.annotations.AfterStories;
import org.jbehave.core.annotations.AfterStory;
import org.jbehave.core.annotations.BeforeStory;
import org.jbehave.web.selenium.PerStoryWebDriverSteps;
import org.jbehave.web.selenium.WebDriverProvider;
import org.openqa.selenium.WebDriver;

import jo.aspire.web.automationUtil.DriverProvider;

public class CustomPerStoryWebDriverSteps extends PerStoryWebDriverSteps {

	public CustomPerStoryWebDriverSteps(WebDriverProvider driverProvider) {
		super(driverProvider);
	}

	@BeforeStory
	public void beforeStory() throws Exception {
		try {
			if (driverProvider.get() != null) {
				driverProvider.end();

			}
		} catch (Exception ex) {

		}
		driverProvider.initialize();
	}

	@AfterStory
	public void afterStory() throws Exception {
		
	}
	
	@AfterStories
	public void afterStories(){
		 for( long key :DriverProvider.allThreads.keySet()){
		 try{
		 WebDriver driver = DriverProvider.allThreads.get(key);
		 if(driver!=null){
		 driver.quit();
		 }else{
			 System.out.println("############## Driver is null");
		 }
		 }catch(Exception ex)
		 {
			 ex.printStackTrace();
		 }
	 }

	}
}
