package automation.web;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

public class SeleniumWait {
	
	private Logger log;
	private WebDriverWait wait;
	private Alert alert;
	private boolean testNgEnabled;
	
	public SeleniumWait(WebDriverWait wait) {
		this.log = LogManager.getLogger(this.getClass());
		this.log.debug("Initializing SeleniumWaits Class.");
		this.wait = wait;
		this.testNgEnabled = false;
		this.log.debug("Successfully initialized SeleniumWaits Class.");
	}
	
	public SeleniumWait(WebDriverWait wait, boolean testNgEnabled) {
		this.log = LogManager.getLogger(this.getClass());
		this.log.debug("Initializing SeleniumWaits Class.");
		this.wait = wait;
		this.testNgEnabled = testNgEnabled;
		this.log.debug("Successfully initialized SeleniumWaits Class.");
	}
	
	/**
	 * Waits for Page URL Value to be the same as expected value.
	 * 
	 * @param expectedUrl Expected Page URL to compare into
	 */

	public final boolean waitForUrlToBe(String expectedUrl) {
		this.log.trace("Waiting for Page URL to match expected value.");
		boolean isUrlEqual = false;
		try {
			isUrlEqual = this.wait.until(ExpectedConditions.urlMatches(expectedUrl));
			this.log.trace("Page URL had matched the expected value!");
		} catch (TimeoutException e) {
			this.log.error("Wait time for Page URL to match the expected URL Value has expired!");
			this.fail();
		} catch (Exception e) {
			this.log.error("Encountered Exception while waiting for Page URL to match expected value!");
			e.printStackTrace();
			this.fail();
		}
		return isUrlEqual;
	}
	
	/**
	 * Waits for Page Title Value to be the same as expected value.
	 * 
	 * @param expectedTitle Expected Page Title to compare into
	 */

	public final boolean waitForTitleToBe(String expectedTitle) {
		this.log.trace("Waiting for Page Title to match expected value.");
		boolean isTitleEqual = false;
		try {
			isTitleEqual = this.wait.until(ExpectedConditions.titleIs(expectedTitle));
			this.log.trace("Page Title had matched the expected value!");
		} catch (TimeoutException e) {
			this.log.error("Wait time for Page Title to match the expected URL Value has expired!");
			this.fail();
		} catch (Exception e) {
			this.log.error("Encountered Exception while waiting for Page Title to match expected value!");
			e.printStackTrace();
			this.fail();
		}
		return isTitleEqual;
	}
	
	/**
	 * Waits for WebElement to be visible at the Web Page.
	 * 
	 * @param locator Object used to locate element to wait for.
	 */

	public final WebElement waitForObjectToBeVisible(Object locator) {
		this.log.trace("Waiting for Web Element to be visible.");
		WebElement element = null;
		try {
			element = this.wait.until(ExpectedConditions.visibilityOfElementLocated((By)locator));
			this.log.trace("Web Element had become visible!");
		} catch (TimeoutException e) {
			this.log.error("Wait time for Web Element to be visible has expired!");
			this.fail();
		} catch (Exception e) {
			this.log.error("Encountered Exception while waiting for element to be visible!");
			e.printStackTrace();
			this.fail();
		}
		return element;
	}
	
	/**
	 * Waits for WebElements to be visible at the Web Page.
	 * 
	 * @param locator Object used to locate elements to wait for.
	 */
	
	public final List<WebElement> waitForObjectsToBeVisible(Object locator) {
		this.log.trace("Waiting for Web Element to be visible.");
		List<WebElement> elements = null;
		try {
			elements = this.wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy((By)locator));
			this.log.trace("Web Element had become visible!");
		} catch (TimeoutException e) {
			this.log.error("Wait time for Web Element to be visible has expired!");
			this.fail();
		} catch (Exception e) {
			this.log.error("Encountered Exception while waiting for element to be visible!");
			e.printStackTrace();
			this.fail();
		}
		return elements;
	}

	/**
	 * Waits for WebElement to be clickable at the Web Page.
	 * 
	 * @param locator Object used to locate element to wait for.
	 */

	public WebElement waitForObjectToBeClickable(Object locator) {
		this.log.trace("Waiting for Web Element to be clickable.");
		WebElement element = null;
		try {
			element = this.wait.until(ExpectedConditions.elementToBeClickable((By)locator));
			this.log.trace("Web Element had become clickable!");
		} catch (TimeoutException e) {
			this.log.error("Wait time for Web Element to be clickable has expired!");
			this.fail();
		} catch (Exception e) {
			this.log.error("Encountered Exception while waiting for element to be clickable!");
			this.fail();
		}
		return element;
	}

	/**
	 * Waits for WebElement to be invisible at the Web Page.
	 * 
	 * @param locator Object used to locate element to wait for.
	 */

	public final boolean waitForObjectToBeInvisible(Object locator) {
		this.log.trace("Waiting for Web Element to be invisible.");
		boolean isVisible = true;
		try {
			isVisible = this.wait.until(ExpectedConditions.invisibilityOfElementLocated((By)locator));
			this.log.trace("Web Element had become invisible!");
		} catch (TimeoutException e) {
			this.log.error("Wait time for Web Element to be invisible has expired!");
			this.fail();
		} catch (Exception e) {
			this.log.error("Encountered Exception while waiting for element to be invisible!");
			this.fail();
		}
		return isVisible;
	}

	/**
	 * Waits for an element's selection state to be the expected selection state.
	 * 
	 * @param locator        Target Object for checking of selection state.
	 * @param selectionState expected selection state.
	 */

	public final boolean waitForObjectSelectionStateToBe(Object locator, boolean selectionState) {
		this.log.trace("Waiting for Web Element Selection State is " + selectionState + "!");
		boolean status = false;
		try {
			status = this.wait.until(ExpectedConditions.elementSelectionStateToBe((By)locator, selectionState));
			this.log.trace("Web Element Selection State is " + selectionState + "!");
		} catch (TimeoutException e) {
			this.log.error("Wait time for Web Element Selection State to be " + selectionState + " has expired!");
			this.fail();
		} catch (Exception e) {
			this.log.error("Encountered Exception while waiting for Web Element Selection State to be " + selectionState + " has expired!");
			this.fail();
		}
		return status;
	}

	/**
	 * Waits for a Javascript Alert to be present on the WebPage.
	 */

	public final Alert waitForAlertToBePresent() {
		try {
			this.alert = this.wait.until(ExpectedConditions.alertIsPresent());
		} catch (TimeoutException e) {
			this.log.error("Wait time for Alert to be displayed has expired!");
			this.fail();
		} catch (Exception e) {
			this.log.error("Encountered Exception while getting Alert Message!");
			this.fail();
		}
		return this.alert;
	}
	
	private void fail() {
		if (this.testNgEnabled) {
			Assert.fail();
		}
	}

}