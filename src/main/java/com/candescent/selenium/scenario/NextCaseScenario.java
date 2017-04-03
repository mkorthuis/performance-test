package com.candescent.selenium.scenario;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.Wait;

import com.candescent.selenium.CaseTime;
import com.candescent.selenium.util.Output;

public class NextCaseScenario extends ReadStudyScenario {

	private Boolean nextWait;

	private static final Integer NEXT_WAIT_SLEEP_MILLI = 5000;
	private static final String NEXT_CASE_SELECTION = "#ui-select-choices-row-4-2 > span.ui-select-choices-row-inner > span.ng-binding.ng-scope";
	
	//Status checkpoints
	private static final String START_WAIT = "Start Wait";
	private static final String END_WAIT = "End Wait";
	
	public NextCaseScenario(RemoteWebDriver driver, Wait<WebDriver> wait, String url, String username, String password,
			Boolean nextWait) {
		this.driver = driver;
		this.wait = wait;
		this.url = url;
		this.username = username;
		this.password = password;
		this.nextWait = nextWait;
	}

	public List<CaseTime> execute()  throws Throwable {
		login();
		selectGroup(null);
		launchStudyFromGroup(null);
		updateSelectionGroup();
		
		selectGroup(null);
		launchStudyFromGroup(null);
		
        List<CaseTime> caseTimes = new ArrayList<CaseTime>();
        
        try {
        	while(true) {
        		CaseTime time = new CaseTime();
        		dictateStudy(time);
        		launchStudy(time);
        		if(nextWait) {
        			writeLine(START_WAIT,"", driver.getCurrentUrl(), time);
		        	Thread.sleep(NEXT_WAIT_SLEEP_MILLI);
		        	writeLine(END_WAIT,"",driver.getCurrentUrl(), time);
        		}
        		caseTimes.add(time);
        	}
        } catch(org.openqa.selenium.TimeoutException te) {
        	
        }
        return caseTimes;

	}
	
	public void calculateCaseTimes(List<CaseTime> caseTimes) {
		List<Long> nextCaseTimes = new ArrayList<Long>();
		long nextCaseSum = 0;
		for(CaseTime caseTime : caseTimes) {
			long nextCaseTime = 0;
			nextCaseTime += (caseTime.timings.get(END_DICTATE_CASE) - caseTime.timings.get(START_DICTATE_CASE));
			nextCaseTime += (caseTime.timings.get(SELECT_CASE_END) - caseTime.timings.get(END_DICTATE_CASE));
			nextCaseTime += (caseTime.timings.get(SELECT_CASE_PRIORS_END) - caseTime.timings.get(SELECT_CASE_END));
			nextCaseSum += nextCaseTime;
			nextCaseTimes.add(nextCaseTime);
		}
		
		String[] itemsAvg = { "Next Case Average (ms)", Long.toString(nextCaseSum/caseTimes.size()) };
		Output.getInstance().write(itemsAvg);
		
		Collections.sort(nextCaseTimes);
		
		String[] itemsNinty = { "Next Case 90% (ms)", Long.toString(nextCaseTimes.get((int)(caseTimes.size() *.9)-1)) };
		Output.getInstance().write(itemsNinty);
	}
	
	/**
	 * Select next case from group or personal in drop down.
	 * @throws Throwable
	 */
	private void updateSelectionGroup() throws Throwable {
	    driver.findElement(By.cssSelector(NEXT_DROPDOWN)).click();
	    Thread.sleep(DROPDOWN_SLEEP_MILLI);
	    driver.findElement(By.cssSelector(NEXT_CASE_SELECTION)).click();
		
        Thread.sleep(DROPDOWN_SLEEP_MILLI);
        WebElement we = driver.findElement(By.xpath(WORKLIST_LINK));
        JavascriptExecutor executor = (JavascriptExecutor) driver;
        executor.executeScript("arguments[0].click();", we);

        waitForPageLoad();
	}
	
	/**
	 * Click the green checkmark
	 * @param time
	 */
	private void dictateStudy(CaseTime time) {
    	writeLine(START_DICTATE_CASE, "",driver.getCurrentUrl(), time);

    	JavascriptExecutor executor = (JavascriptExecutor) driver;
    	executor.executeScript("arguments[0].click();", driver.findElement(By.id("case_mark_dictated")));
		
		waitForNotPageLoad();
		waitForPageLoad();
    	writeLine(END_DICTATE_CASE, "",driver.getCurrentUrl(), time);

	}

}
