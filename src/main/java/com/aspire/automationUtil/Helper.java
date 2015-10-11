package com.aspire.automationUtil;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileElement;
import io.appium.java_client.TouchAction;

import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.sikuli.api.ImageTarget;
import org.sikuli.api.ScreenRegion;
import org.sikuli.api.StaticImageScreenRegion;
import org.sikuli.api.Target;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;

public abstract class Helper {


	//private  WebDriverWait driverWait;
	public  static String sharedData = "";
	UnivisionDriverProvider driverProvider;
	int timeoutInSeconds;
	/**
	 * Initialize the webdriver. Must be called before using any helper methods.
	 * *
	 */
	public Helper(UnivisionDriverProvider driverPorvider)
	{
		this.driverProvider = driverPorvider;
	//	driver =mainTest.driver;
		 timeoutInSeconds = 60;
		if (SessionHandler.getRunOnSauce()) {
			timeoutInSeconds = 120;
		}
		//driverWait = new WebDriverWait(driver, timeoutInSeconds);

	}
	/**
	 * Wrap WebElement in MobileElement *
	 */

	 public void scrollTo(String elementSelector) {
		    boolean found = false;
		    int screenHeight = currentDriver().manage().window().getSize().getHeight();
		    int screenWidth = currentDriver().manage().window().getSize().getWidth();
		    int previousValue;

		      while (!found) {
		        currentDriver().swipe((int) (screenWidth * 0.5),(int) (screenHeight * 0.9), (int) (screenWidth * 0.5), 0,2000);

		        try {
		         if (find(elementSelector).isDisplayed()) {
		          found = true;
		          WebElement ele = find(elementSelector);

		          while (ele.getLocation().getY() > screenHeight * 0.20) {
		           previousValue = ele.getLocation().getY();
		           currentDriver().swipe((int) (screenWidth * 0.5),
		             (int) (screenHeight * 0.5),
		             (int) (screenWidth * 0.5), (int) (screenHeight *  0.4), 2000);
		           ele = find(elementSelector);
		           if (previousValue == ele.getLocation().getY()) {
		            break;
		           }

		          }

		         }
		        } catch (Exception e) {
		        }
		       }
		   
		    

		 }
	 public void SwipeRight() {

		  int screenHeight = currentDriver().manage().window().getSize().getHeight();
		  int screenWidth = currentDriver().manage().window().getSize().getWidth();
		  currentDriver().swipe((int) (screenWidth * 0.1),(int) (screenHeight * 0.5), (int) (screenWidth * 0.99),(int) (screenHeight  *0.5) ,1000);  
		 }
		 
		 public void SwipeLeft() {

		  int screenHeight = currentDriver().manage().window().getSize().getHeight();
		  int screenWidth = currentDriver().manage().window().getSize().getWidth();
		  currentDriver().swipe((int) (screenWidth  * 0.8),(int) (screenHeight  * 0.5), 0,(int) (screenHeight * 0.5) ,500);  
		 }
	private  MobileElement w(WebElement element) {
		return  (MobileElement) element;
	}
	 public  void sleepTime(int value) {
		  try {
		   Thread.currentThread();
		   Thread.sleep(value);
		  } catch (Exception e) {
		   e.printStackTrace();
		  }
		 }
	public AppiumDriver currentDriver()
	{
		return driverProvider.getCurrentDriver();
	}
	/**
	 * Wrap WebElement in MobileElement *
	 */
	private  List<MobileElement> w(List<WebElement> elements) {
		List<MobileElement> list = new ArrayList<MobileElement>(elements.size());
		for (WebElement element : elements) {
			list.add(w(element));
		}

		return list;
	}

	/**
	 * Set implicit wait in seconds *
	 */
	public  void setWait(int seconds) {
		driverProvider.getCurrentDriver().manage().timeouts().implicitlyWait(seconds, TimeUnit.SECONDS);
	}

	/**
	 * Return an element by locator *
	 */
	public  MobileElement element(By locator) {
		return w(driverProvider.getCurrentDriver().findElement(locator));
	}

	/**
	 * Return a list of elements by locator *
	 */
	public  List<MobileElement> elements(By locator) {
		return w(driverProvider.getCurrentDriver().findElements(locator));
	}

	/**
	 * Press the back button *
	 */
	public  void back() {
		driverProvider.getCurrentDriver().navigate().back();
	}

	/**
	 * Return a list of elements by tag name *
	 */
	public  List<MobileElement> tags(String tagName) {
		return elements(for_tags(tagName));
	}

	/**
	 * Return a tag name locator *
	 */
	public  By for_tags(String tagName) {
		return By.className(tagName);
	}

	/**
	 * Return a  text element by xpath index *
	 */
	public  MobileElement s_text(int xpathIndex) {
		return element(for_text(xpathIndex));
	}

	/**
	 * Return a  text locator by xpath index *
	 */
	public  By for_text(int xpathIndex) {
		return By.xpath("//android.widget.TextView[" + xpathIndex + "]");
	}

	/**
	 * Return a  text element that contains text *
	 */
	public  MobileElement text(String text) {
		return element(for_text(text));
	}

	/**
	 * Return a  text locator that contains text *
	 */
	public  By for_text(String text) {
		return By.xpath("//android.widget.TextView[contains(@text, '" + text
				+ "')]");
	}

	/**
	 * Return a  text element by exact text *
	 */
	public  MobileElement text_exact(String text) {
		return element(for_text_exact(text));
	}

	/**
	 * Return a  text locator by exact text *
	 */
	public  By for_text_exact(String text) {
		return By.xpath("//android.widget.TextView[@text='" + text + "']");
	}

	public  By for_find(String value) {
		return By.xpath("//*[@content-desc=\"" + value + "\" or @type=\""
				+ value + "\" or @class=\"" + value + "\" or @package=\""
				+ value + "\" or @resource-id=\"" + value + "\" or @text=\""
				+ value + "\"] | //*[contains(translate(@content-desc,\""
				+ value + "\",\"" + value + "\"), \"" + value
				+ "\") or contains(translate(@text,\"" + value + "\",\""
				+ value + "\"), \"" + value + "\") or @resource-id=\"" + value
				+ "\"]");
	}
	
	
	
	public By for_first_attribute(String value) {
		  return By.xpath("(//*[@*=\"" + value + "\"])[1]");
		 }

		 public By for_attribute(String value) {
		  return By.xpath("(//*[@*=\"" + value + "\"])");
		 }

		 public  By for_path(String value) {
			   return By.xpath(value);
			  }
		 
	public  MobileElement find(String value) {
		return element(for_find(value));
	}

	
	public MobileElement for_tag(String value, String tag) {
		  try {
			List<MobileElement> elements = waitAll(for_attribute(value));

			  for (MobileElement mobileElement : elements) {
			   if (mobileElement.getTagName().equals(tag)) {
			    mobileElement.click();
			    return mobileElement;
			   }
			  }
			  return null;
		} catch (Exception e) {
			 return null;
		}
		 }
	
	
	public  WebElement findElement(String value) {
		return driverProvider.getCurrentDriver().findElement(for_find(value));
	}

	public  WebElement findElementByName(String value) {
		return driverProvider.getCurrentDriver().findElement(By.name(value));
	}

	public  void hideKeyboard() {
		driverProvider.getCurrentDriver().hideKeyboard();
		
	}

	/**
	 * Wait 30 seconds for locator to find an element *
	 */
	public  MobileElement wait(By locator) {
		WebDriverWait driverWait= new WebDriverWait(driverProvider.getCurrentDriver(), timeoutInSeconds);
		return w(driverWait.until(ExpectedConditions
				.visibilityOfElementLocated(locator)));
	}

	/**
	 * Wait 60 seconds for locator to find all elements *
	 */
	public  List<MobileElement> waitAll(By locator) {
		WebDriverWait driverWait= new WebDriverWait(driverProvider.getCurrentDriver(), timeoutInSeconds);
		return w(driverWait.until(ExpectedConditions
				.visibilityOfAllElementsLocatedBy(locator)));
	}

	/**
	 * Wait 60 seconds for locator to not find a visible element *
	 */
	public  boolean waitInvisible(By locator) {
		WebDriverWait driverWait= new WebDriverWait(driverProvider.getCurrentDriver(), timeoutInSeconds);
		return driverWait.until(ExpectedConditions
				.invisibilityOfElementLocated(locator));
	}

	/**
	 * Return an element that contains name or text *
	 */
	public  MobileElement scroll_to(String value) {
		return (MobileElement)driverProvider.getCurrentDriver().scrollTo(value);
	}

	/**
	 * Return an element that exactly matches name or text *
	 */
	public  MobileElement scroll_to_exact(String value) {
		return (MobileElement)driverProvider.getCurrentDriver().scrollToExact(value);
	}

	/**
	 * Return a list of elements by locator *
	 */
	public  List<WebElement> webElements(By locator) {
		return driverProvider.getCurrentDriver().findElements(locator);
	}

	public  int findElementsCount(String value) {

		return driverProvider.getCurrentDriver().findElements(for_find(value)).size();
	}

	public  void setSharedData(String value) {
		sharedData = value;
	}

	public  String getSharedData() {
		return sharedData;
	}

public void clickOnImage(String imageName) {
		//project dir  File.seprator imgs imagePath
	
	 String workingDir = System.getProperty("user.dir");
	 String path = File.separator +workingDir+ File.separator +"imgs"+File.separator+imageName;
	
	 
		ScreenRegion reg = getRegion(path);
		TouchAction t = new TouchAction(currentDriver()) ;
		t.tap(reg.getCenter().getX() , reg.getCenter().getY());
		t.perform();

	}

public void checkByImage(String imageName) {
	//project dir  File.seprator imgs imagePath

 String workingDir = System.getProperty("user.dir");
 String path = File.separator +workingDir+ File.separator +"imgs"+File.separator+imageName;

 
	ScreenRegion reg = getRegion(path);
	TouchAction t = new TouchAction(currentDriver()) ;
	t.tap(reg.getCenter().getX() , reg.getCenter().getY());

}
public ScreenRegion getRegion(String targetFileLocation) {
		ScreenRegion region;
		//long p = 1000L; // Adjust to suit timing
		//long lastTime = System.currentTimeMillis() - p;
		//long thisTime = System.currentTimeMillis();
		
		int counter=5;
		
		boolean notDone = true;
		
		if (targetFileLocation == null) 
		{
			System.err.println(" no target file name");
			try 
				{
					Thread.sleep(100);
				} 
			catch (Exception e) {
								}
		return null;
		}
		else if (new File(targetFileLocation).exists() && !new File(targetFileLocation).isDirectory()) 
		{
			do 
				{
					try {
						//lastTime = thisTime;
					//	object.wait(timeoutExpiredMs - System.currentTimeMillis());
						
						region = findRegion(targetFileLocation);
						region.getCenter();
						notDone = false;
						return region;
						} 
					catch (Exception e) {
						System.err.println(" no target image found in shot. retrying...");
										}
					try {
						Thread.sleep(200);
					} catch (Exception e) {
					}
				} while (/*(thisTime - lastTime) >= p &&*/(counter--)>0 && notDone);
		
		} 
		else 
		{
			System.err.println(" file not exits: " + targetFileLocation);
			try {
				Thread.sleep(100);
				} 
			catch (Exception e) {
				}
			return null;
		}
		return null;
	}
	
		public ScreenRegion findRegion(String targetFileLocation) 
		{
			Target target = new ImageTarget(new File(targetFileLocation));
			target.setMinScore(0.9); //0.7 default
		
			ScreenRegion screenRegion = new StaticImageScreenRegion(takeShot()).find(target);
			return screenRegion;
		}
		public static BufferedImage resize(BufferedImage img, int newW, int newH) { 
		    Image tmp = img.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
		    BufferedImage dimg = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_ARGB);
		    Graphics2D g2d = dimg.createGraphics();
		    g2d.drawImage(tmp, 0, 0, null);
		    g2d.dispose();
		    return dimg;
		}  
		public BufferedImage takeShot() 
		{
		    for (int i = 0; i < 5; i++) 
		    {
		        if (currentDriver().manage().window().getSize().getHeight() > currentDriver().manage().window().getSize().getWidth()) 
		        {
		            System.out.println("   takeShot: portrait mode w: " + currentDriver().manage().window().getSize().getWidth()
		                    + ", h: " + currentDriver().manage().window().getSize().getHeight());
		            try 
		            {
		            	Dimension dat = currentDriver().manage().window().getSize();
		            	BufferedImage img =  ImageIO.read(((AppiumDriver) currentDriver()).getScreenshotAs(OutputType.FILE));
		            	img = resize(img,currentDriver().manage().window().getSize().getWidth(),currentDriver().manage().window().getSize().getHeight());
		            	  File outputfile = new File("saved.png");
		                  ImageIO.write(img,"png", outputfile);
		                return img;
		            } 
		            catch (Exception e) {
		            }
		        } 
		        else 
		        {
		            System.out.println("   takeShot: landscape mode w: " + currentDriver().manage().window().getSize().getWidth()
		                    + ", h: " + currentDriver().manage().window().getSize().getHeight());
		            BufferedImage image = null;
		            try 
		            {
		                image = ImageIO.read(((AppiumDriver) currentDriver()).getScreenshotAs(OutputType.FILE));
		            } 
		            catch (Exception e) 
		            {
		                System.err.println("    get screenshot failed.");
		            }
		            if(image!=null) {
		                //System.out.println("    original image height: "+image.getHeight()+", width: "+image.getWidth());
		                int diff = (image.getHeight() - image.getWidth());
		                diff = diff / 2;
		                //System.out.println("   diff: "+diff);

		                AffineTransform tx = new AffineTransform();
		                tx.rotate(Math.PI * 1.5, image.getHeight() / 2, image.getWidth() / 2);//(radian,arbit_X,arbit_Y)
		                AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_BILINEAR);
		                image = op.filter(image, null);//(source,destination)
		                //System.out.println("    rotated image height: "+image.getHeight()+", width: "+image.getWidth());

		                image = image.getSubimage(diff, diff, image.getWidth() - diff, image.getHeight() - diff);
		                //System.out.println("    result image height: "+image.getHeight()+", width: "+image.getWidth());
		                return image;
		            }
		        }
		    }
		    return null;
		}
		public void isAds(){
			try{
				clickOnImage("x.PNG");
			}catch (Exception e) {
            }
			
		}
		
		public void clickByImage(String imageName){
			
				clickOnImage(imageName);
	
		}
		public void iOSAds(){
			try{
				clickByImage("close.png");
			}catch (Exception e) {}
			
		}


}