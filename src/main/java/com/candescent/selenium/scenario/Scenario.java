package com.candescent.selenium.scenario;

import java.util.Date;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.Wait;

import com.candescent.selenium.CaseTime;
import com.candescent.selenium.util.Output;

public class Scenario {
	
	protected RemoteWebDriver driver; 
	protected Wait<WebDriver> wait;
	
	/**
	 * Will hold until a page is fully loaded.
	 */
	protected void waitForPageLoad() {

	    wait.until(new ExpectedCondition<Boolean>() {
	        public Boolean apply(WebDriver wdriver) {
	        	try {
	            return ((JavascriptExecutor) driver).executeScript(
	                "return document.readyState"
	            ).equals("complete");
        	} catch(org.openqa.selenium.WebDriverException e) {
        		System.out.println(e);
        	}
			return false;
	        }
	    });
	}
	
	/**
	 * Will hold until is not fully loaded.  Use this to track when a page starts to refresh
	 */
	protected void waitForNotPageLoad() {

	    wait.until(new ExpectedCondition<Boolean>() {
	        public Boolean apply(WebDriver wdriver) {
	        	try {
	            return !((JavascriptExecutor) driver).executeScript(
	                "return document.readyState"
	            ).equals("complete");
	        	} catch(org.openqa.selenium.WebDriverException e) {
	        		System.out.println(e);
	        	}
				return false;
	        }
	    });
	}
	
	/**
	 * Waits until a specific classname is available
	 * @param className
	 */
	protected void waitForClassName(final String className) {
	    wait.until(new ExpectedCondition<Boolean>() {
	        public Boolean apply(WebDriver wdriver) {
	        	Boolean isPresent =  driver.findElementsByClassName(className).size() > 0;
	        	return isPresent;
	        }
	    });	
	}
	
	/**
	 * Waits until a specific xpath is available on the dom.
	 * @param xpath
	 */
	protected void waitForXPath(final String xpath) {
		//Ensuring we only wait if necessary. Probably not necessary.
		if(!(driver.findElements(By.xpath(xpath)).size() > 0)) { 
		    wait.until(new ExpectedCondition<Boolean>() {
		        public Boolean apply(WebDriver wdriver) {
		        	Boolean isPresent =  driver.findElements(By.xpath(xpath)).size() > 0;
		        	return isPresent;
		        }
		    });
		}
	}
	
	/**
	 * Writes action, detail, url and time to standard out and the log.
	 * @param action
	 * @param detail
	 * @param url
	 * @param time
	 */
	protected void writeLine(String action, String detail, String url, CaseTime time) {
		Date date = new Date();
		String[] items = {Long.toString(date.getTime()), action, detail, url };
		Output.getInstance().write(items);
		if(time != null) {
			time.timings.put(action, date.getTime());
		}
	}
	
	/**
	 * Writes action, detail, url to the log.
	 * @param action
	 * @param detail
	 * @param url
	 */
	protected void writeLine(String action, String detail, String url) {
		Date date = new Date();
		String[] items = {Long.toString(date.getTime()), action, detail, url };
		Output.getInstance().write(items);
	}
}
