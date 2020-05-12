package automation.mobile;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import enums.Mobile;
import enums.TestStatus;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileElement;
import io.appium.java_client.TouchAction;
import io.appium.java_client.android.AndroidTouchAction;
import io.appium.java_client.ios.IOSTouchAction;
import io.appium.java_client.touch.LongPressOptions;
import io.appium.java_client.touch.TapOptions;
import io.appium.java_client.touch.offset.ElementOption;

public class AppiumMobileAutomation implements MobileAutomation {
	
	AppiumDriver<MobileElement> driver;
	TouchAction<?> touchAction;
	Logger log;
	WebDriverWait wait;
	Select select;
	Alert alert;
	Actions action;
	JavascriptExecutor javascriptExecutor;
	ArrayList<String> tabs;
	File applicationFile;
	long implicitWaitDuration;
	long explicitWaitDuration;
	boolean testNgEnabled;
	
	private AppiumMobileDriver appiumMobileDriver;
	private Mobile mobile;
	private String platformVersion;
	private String deviceName;
	
	public AppiumMobileAutomation(Mobile mobile, URL appiumServerUrl, String platformVersion, String deviceName, File applicationFile) {
		this.log = LogManager.getLogger(this.getClass());
		this.log.debug("Initializing AppiumWebAutomation Class.");
		this.appiumMobileDriver = new AppiumMobileDriver(appiumServerUrl);
		this.implicitWaitDuration = 20;
		this.testNgEnabled = false;
		this.mobile = mobile;
		this.platformVersion = platformVersion;
		this.deviceName = deviceName;
		this.applicationFile = applicationFile;
		this.log.debug("Successfully initialized AppiumWebAutomation Class.");
	}
	
	public AppiumMobileAutomation(Mobile mobile, URL appiumServerUrl, String platformVersion, String deviceName, File applicationFile, boolean testNgEnabled) {
		this.log = LogManager.getLogger(this.getClass());
		this.log.debug("Initializing AppiumWebAutomation Class.");
		this.appiumMobileDriver = new AppiumMobileDriver(appiumServerUrl);
		this.implicitWaitDuration = 20;
		this.testNgEnabled = testNgEnabled;
		this.mobile = mobile;
		this.platformVersion = platformVersion;
		this.deviceName = deviceName;
		this.applicationFile = applicationFile;
		this.log.debug("Successfully initialized AppiumWebAutomation Class.");
	}
	
	@Override
	public void openApplication() {
		this.log.info("I open application.");
		switch(this.mobile) {
		case iOS:
			this.driver = this.appiumMobileDriver.getIOSDriver(this.platformVersion, this.deviceName, this.applicationFile);
			this.touchAction = new TouchAction<IOSTouchAction>(this.driver);
			break;
		case Android:
			this.driver = this.appiumMobileDriver.getAndroidDriver(this.platformVersion, this.deviceName, this.applicationFile);
			this.touchAction = new TouchAction<AndroidTouchAction>(this.driver);
			break;
		default:
			this.log.fatal("Encountered unsupported Mobile Platform while initializing AppiumDriver. Check defined Mobile Platform.");
			System.exit(1);
		}
		this.driver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);
	}
	
	MobileElement getElement(Object locator) {
		MobileElement element = null;
		element = this.driver.findElement((By)locator);
		return element;
	}
	
	List<MobileElement> getElements(Object locator) {
		List<MobileElement> elements;
		elements = this.driver.findElements((By)locator);
		return elements;
	}

	@Override
	public void tap(Object locator) {
		this.log.info("I tap on Mobile Element: \"" + locator.toString() + "\".");
		TapOptions tapOptions = new TapOptions();
		MobileElement element = this.driver.findElement((By)locator);
		this.touchAction.tap(tapOptions.withElement(ElementOption.element(element))).perform();
	}
	
	@Override
	public void tapFromTableBasedOnText(Object objectToCheckText, String textToCheck, Object objectToTap) {
		this.log.info("I tap a Mobile Element based on Text: \"" + textToCheck + "\".");
		List<MobileElement> elementToCheckText = this.getElements((By)objectToCheckText);
		List<MobileElement> elementToTap = this.getElements((By)objectToTap);
		int size = elementToTap.size();
		boolean flgTextFound = false;
		for(int i = 0; i < size; i++) {
			String text = elementToCheckText.get(i).getText().trim();
			if (text.equals(textToCheck)) {
				TapOptions tapOptions = new TapOptions();
				this.touchAction.tap(tapOptions.withElement(ElementOption.element(elementToTap.get(i)))).perform();
				flgTextFound = true;
				break;
			}
		}
		if (!flgTextFound) {
			this.log.error("The text \"" + textToCheck + "\" is not found from Table.");
		}
	}
	
	@Override
	public void longPress(Object locator) {
		this.log.info("I long press on Mobile Element: \"" + locator.toString() + "\".");
		LongPressOptions longPressOptions = new LongPressOptions();
		MobileElement element = this.driver.findElement((By)locator);
		this.touchAction.longPress(longPressOptions.withElement(ElementOption.element(element))).perform();
	}

	@Override
	public void type(Object locator, String inputText) {
		this.log.info("I type \"" + inputText + "\" at Mobile Element: \"" + locator.toString() + "\".");
		MobileElement element = this.driver.findElement((By)locator);
		element.sendKeys(inputText);
	}
	
	@Override
	public void typeFromTableBasedOnText(Object objectToCheckText, String textToCheck, Object objectToFill, String inputText) {
		this.log.info("I type at Web Element based on Text: \"" + textToCheck + "\".");
		List<MobileElement> elementToCheckText = this.getElements((By)objectToCheckText);
		List<MobileElement> elementToFill = this.getElements((By)objectToFill);
		int size = elementToFill.size();
		boolean flgTextFound = false;
		for(int i = 0; i < size; i++) {
			String text = elementToCheckText.get(i).getText().trim();
			if (text.equals(textToCheck)) {
				elementToFill.get(i).sendKeys(inputText);
				flgTextFound = true;
				break;
			}
		}
		if (!flgTextFound) {
			this.log.error("The text \"" + textToCheck + "\" is not found from Table.");
		}
	}

	@Override
	public void clear(Object locator) {
		this.log.info("I clear Mobile Element: \"" + locator.toString() + "\".");
		MobileElement element = this.driver.findElement((By)locator);
		element.clear();
	}

	@Override
	public String getText(Object locator) {
		this.log.info("I get text from Mobile Element: \"" + locator.toString() + "\".");
		MobileElement element = this.getElement(locator);
		String text = null;
		text = element.getText().trim();
		if (text.length() == 0) {
			this.log.debug("Mobile Element: \"" + locator.toString() + "\" has no text.");
		}
		return text;
	}

	@Override
	public String getTextFromTableBasedOnText(Object objectToCheckText, String textToCheck, Object objectToGetTextFrom) {
		this.log.info("I get text from a Mobile Element based on Text: \"" + textToCheck + "\".");
		List<MobileElement> elementToCheckText = this.getElements((By)objectToCheckText);
		List<MobileElement> elementToClick = this.getElements((By)objectToGetTextFrom);
		int size = elementToClick.size();
		boolean flgTextFound = false;
		String retrievedText = null;
		for(int i = 0; i < size; i++) {
			String text = elementToCheckText.get(i).getText().trim();
			if (text.equals(textToCheck)) {
				retrievedText = elementToClick.get(i).getText().trim();
				flgTextFound = true;
				break;
			}
		}
		if (!flgTextFound) {
			this.log.error("The text \"" + textToCheck + "\" is not found from Table.");
		}
		return retrievedText;
	}

	@Override
	public String getAttributeValue(Object locator, String attribute) {
		this.log.info("I get attribute value from Web Element: \"" + locator.toString() + "\".");
		String text = null;
		WebElement element = this.getElement(locator);
		text = element.getAttribute(attribute);
		if (text.length() == 0) {
			this.log.debug("The Attribute: " + attribute + " of Web Element: \"" + locator.toString() + "\".");
		}
		return text;
	}

	@Override
	public String getValue(Object locator) {
		this.log.info("I get value from Mobile Element: \"" + locator.toString() + "\".");
		String text = null;
		WebElement element = this.getElement(locator);
		text = element.getAttribute("value");
		if (text.length() == 0) {
			this.log.debug("The Text Box/Area Web Element: \"" + locator.toString() + "\" has no value.");
		}
		return text;
	}

	@Override
	public void wait(int duration) {
		this.log.info("I wait for " + duration + ".");
		try {
			Thread.sleep(duration * 1000);
		} catch (IllegalArgumentException e) {
			this.log.error("Encountered IllegalArgumentException while waiting for " + duration + ".");
		} catch (InterruptedException e) {
			this.log.error("Encountered InterruptedException while waiting for " + duration + ".");
		}
	}
	
	/*#######################################################*/
	/*                     VERIFICATIONS                     */
	/*#######################################################*/

	@Override
	public TestStatus verifyTappable(Object locator) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TestStatus verifyValue(Object locator, String expectedValue) {
		this.log.info("I verify \"" + expectedValue + "\" is displayed at Mobile Element: \"" + locator.toString() + "\".");
		String actualValue = this.getValue((By)locator);
		boolean isValueEqual = actualValue.equals(expectedValue);
		TestStatus status = TestStatus.FAILED;
		if(isValueEqual) {
			status = TestStatus.PASSED;
			this.log.info("I see value: \"" + expectedValue + "\" from Mobile Element: \"" + locator.toString() + "\".");
		} else {
			status = TestStatus.FAILED;
			this.log.error("I don't see value: \"" + expectedValue + "\" from Mobile Element: \"" + locator.toString() + "\". Actual value is \"" + actualValue + "\".");
			this.fail();
		}
		return status;	
	}

	@Override
	public TestStatus verifyAttributeValue(Object locator, String attribute, String expectedValue) {
		this.log.info("I verify \"" + expectedValue + "\" is displayed for attribute: \"" + attribute + "\" at Mobile Element: \"" + locator.toString() + "\".");
		String actualValue = this.getAttributeValue((By)locator, attribute);
		boolean isValueEqual = actualValue.equals(expectedValue);
		TestStatus status = TestStatus.FAILED;
		if(isValueEqual) {
			status = TestStatus.PASSED;
			this.log.info("I see value: \"" + expectedValue + "\" for attribute: \"" + attribute + "\" at Mobile Element: \"" + locator.toString() + "\".");
		} else {
			status = TestStatus.FAILED;
			this.log.error("I don't see value: \"" + expectedValue + "\" for attribute: \"" + attribute + "\" at Mobile Element: \"" + locator.toString() + "\". Actual value is \"" + actualValue + "\".");
			this.fail();
		}
		return status;	
	}

	@Override
	public TestStatus verifyText(Object locator, String expectedValue) {
		this.log.info("Verifying \"" + expectedValue + "\" Text Value is displayed.");
		String actualText = this.getText((By)locator);
		boolean isValueEqual = actualText.equals(expectedValue);
		TestStatus status = TestStatus.FAILED;
		if(isValueEqual) {
			status = TestStatus.PASSED;
			this.log.info("I see text: \"" + expectedValue + "\" at Mobile Element: \"" + locator.toString() + "\".");
		} else {
			status = TestStatus.FAILED;
			this.log.error("I don't see text: \"" + expectedValue + "\" at Mobile Element: \"" + locator.toString() + "\". Actual value is \"" + actualText + "\".");
			this.fail();
		}
		return status;
	}

	@Override
	public TestStatus verifyDisplayed(Object locator) {
		this.log.info("I verify Mobile Element: \"" + locator.toString() + "\" is displayed.");
		List<MobileElement> elements = this.getElements(locator);
		TestStatus status = TestStatus.FAILED;
		if (elements.size() > 0) {
			status = TestStatus.PASSED;
			this.log.info("I verified Mobile Element: \"" + locator.toString() + "\" is displayed.");
		} else {
			status = TestStatus.FAILED;
			this.log.error("I verified Mobile Element: \"" + locator.toString() + "\" is not displayed.");
			this.fail();
		}
		return status;
	}

	@Override
	public TestStatus verifyNotDisplayed(Object locator) {
		this.log.info("I verify Mobile Element: \"" + locator.toString() + "\" is not displayed.");
		List<MobileElement> elements = this.getElements(locator);
		TestStatus status = TestStatus.FAILED;
		if (elements.size() == 0) {
			status = TestStatus.PASSED;
			this.log.info("I verified Mobile Element: \"" + locator.toString() + "\" is not displayed.");
		} else {
			status = TestStatus.FAILED;
			this.log.error("I verified Mobile Element: \"" + locator.toString() + "\" is displayed.");
			this.fail();
		}
		return status;
	}

	@Override
	public TestStatus verifyEnabled(Object locator) {
		this.log.info("Verifying Mobile Element \"" + locator.toString() + "\" is enabled.");
		MobileElement element = this.getElement(locator);
		boolean isEnabled = element.isEnabled();
		TestStatus status = TestStatus.FAILED;
		if (isEnabled) {
			status = TestStatus.PASSED;
			this.log.info("I verified Mobile Element: \"" + locator.toString() + "\" is enabled.");
		} else {
			status = TestStatus.FAILED;
			this.log.error("I verified Mobile Element: \"" + locator.toString() + "\" is not enabled.");
			this.fail();
		}
		return status;
	}

	@Override
	public TestStatus verifyDisabled(Object locator) {
		this.log.info("Verifying Mobile Element \"" + locator.toString() + "\" is disabled.");
		MobileElement element = this.getElement(locator);
		boolean isEnabled = element.isEnabled();
		TestStatus status = TestStatus.FAILED;
		if (!isEnabled) {
			status = TestStatus.PASSED;
			this.log.info("I verified Mobile Element: \"" + locator.toString() + "\" is disabled.");
		} else {
			status = TestStatus.FAILED;
			this.log.error("I verified Mobile Element: \"" + locator.toString() + "\" is not disabled.");
			this.fail();
		}
		return status;
	}
	
	void fail() {
		if (this.testNgEnabled) {
			Assert.fail();
		}
	}

}
