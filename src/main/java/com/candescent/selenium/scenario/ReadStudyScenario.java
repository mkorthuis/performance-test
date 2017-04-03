package com.candescent.selenium.scenario;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;

import com.candescent.selenium.CaseTime;

public class ReadStudyScenario extends Scenario {

	private static final String LOGIN_URL = "/user/login";
	protected static final Integer DROPDOWN_SLEEP_MILLI = 1000;
	
	//UI Markers
	protected static final String USERNAME = "username";
	protected static final String PASSWORD = "password";
	protected static final String LOGIN_SUBMIT = "input[type=\"submit\"]";
	protected static final String ADDRESS_CLASS = "address";
	protected static final String GROUP_TAB = "//div[@id='main']/div/nav/ul/li[@href='#/worklist/common_reading_pool']";
	protected static final String GROUP_TAB_COUNT = GROUP_TAB + "/span";
	protected static final String WORKLIST_ITEM = "//div[@id='main']/div[2]/worklist-tab[2]/div/worklist/div[2]/div[3]/ul/li[2]";
	protected static final String PRIOR_REPORTS = "//li[@id='priorReports']/a/span";
	protected static final String NEXT_DROPDOWN = "span.ui-select-match-text.pull-left";
	protected static final String WORKLIST_LINK = "//a[@id='worklist']";
	
	//Status Checkpoints
	protected static final String LAUNCH = "Launch";
	protected static final String LOGIN_START = "Login Start";
	protected static final String LOGIN_END = "Login End";
	protected static final String SELECT_WORKSTATION = "Select Workstation";
	protected static final String END_WORKSTATION = "End Workstation";
	protected static final String SELECT_GROUP_START = "Select Group Start";
	protected static final String SELECT_GROUP_END = "Select Group End";
	protected static final String SELECT_GROUP_CASE_START = "Select Group Case Start";
	protected static final String SELECT_CASE_END = "Select Case End";
	protected static final String SELECT_CASE_PRIORS_END = "Select Case Priors End";
	protected static final String START_DICTATE_CASE = "Start Dictate Case";
	protected static final String END_DICTATE_CASE = "End Dictate Case";
	
	protected String url;
	protected String username;
	protected String password;
	
	/**
	 * Login to application
	 */
	protected void login() {
		writeLine(LAUNCH,"",driver.getCurrentUrl());
        driver.get(url + LOGIN_URL);
        driver.findElement(By.id(USERNAME)).clear();
        driver.findElement(By.id(USERNAME)).sendKeys(username);
        driver.findElement(By.id(PASSWORD)).clear();
        driver.findElement(By.id(PASSWORD)).sendKeys(password);
        
        writeLine(LOGIN_START,"",driver.getCurrentUrl());
        driver.findElement(By.cssSelector(LOGIN_SUBMIT)).click();
        waitForPageLoad();
        waitForClassName(ADDRESS_CLASS);
        writeLine(LOGIN_END,"",driver.getCurrentUrl());
        
        writeLine(SELECT_WORKSTATION,"",driver.getCurrentUrl());
        driver.findElementsByClassName(ADDRESS_CLASS).get(0).click();

        waitForPageLoad();
        writeLine(END_WORKSTATION,"",driver.getCurrentUrl());		
	}

	/**
	 * Select a specific group from the worklist
	 * @param time
	 */
	protected void selectGroup(CaseTime time) {
		writeLine(SELECT_GROUP_START,"",driver.getCurrentUrl(), time);
        waitForXPath(GROUP_TAB);

        WebElement we = driver.findElement(By.xpath(GROUP_TAB));
        //If none, already selected.
        if(!we.getCssValue("pointer-events").equals("none")) {
            we.click();
        }
        String count = driver.findElement(By.xpath(GROUP_TAB_COUNT)).getText();
        writeLine(SELECT_GROUP_END, count,driver.getCurrentUrl(), time);		
	}
	
	/**
	 * Select a specific study from the group worklist
	 * @param time
	 * @return
	 */
	protected Boolean launchStudyFromGroup(CaseTime time) {
    	//Make Sure Worklist is there.
    	waitForXPath(WORKLIST_ITEM);
    	WebElement webElement = driver.findElement(By.xpath(WORKLIST_ITEM));
    	if(webElement == null) {
    		return false;
    	}
    	
        writeLine(SELECT_GROUP_CASE_START, "",driver.getCurrentUrl(), time);
    	webElement.click();
        launchStudy(time);
    	return true;
	}
	
	/**
	 * Track the launching of a study
	 * @param time
	 */
	protected void launchStudy(CaseTime time) {
		// Custom wait. 
    	wait.until(new ExpectedCondition<Boolean>() {
	        public Boolean apply(WebDriver wdriver) {
	        	List<WebElement> priors = driver.findElements(By.xpath(PRIOR_REPORTS));
	        	Boolean isPresent =  priors.size() > 0 && StringUtils.isNotBlank(priors.get(0).getText());
	        	return isPresent;
	        }
	    });
    	writeLine(SELECT_CASE_END, "",driver.getCurrentUrl(), time);
    	
    	String value = driver.findElement(By.xpath(PRIOR_REPORTS)).getText();
    	Integer numberOfPriors = Integer.valueOf(value);
    	if(numberOfPriors > 0) {
    	    wait.until(new ExpectedCondition<Boolean>() {
    	        public Boolean apply(WebDriver wdriver) {
    	        	//Waits until the loading bar hides
    	        	WebElement we = driver.findElementsByClassName("comparisons-status").get(0);
    	        	String classValue = we.getAttribute("class");
    	        	Boolean isPresent =  classValue.contains("ng-hide");
    	        	return isPresent;
    	        }
    	    });	
        	System.out.println("Finished!");
    	}
    	writeLine(SELECT_CASE_PRIORS_END, value,driver.getCurrentUrl(), time);
	}
}
