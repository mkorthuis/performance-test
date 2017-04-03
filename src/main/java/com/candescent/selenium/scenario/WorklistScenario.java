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

public class WorklistScenario extends ReadStudyScenario {

	private static final String WORKLIST_LINK = "#ui-select-choices-row-4-0 > span.ui-select-choices-row-inner > span.ng-binding.ng-scope";
	private static final String SEND_TO_GROUP_BUTTON = "//a[@id='sendToGroup']";
	
	
	public WorklistScenario(RemoteWebDriver driver, Wait<WebDriver> wait, String url, String username, String password) {
		this.driver = driver;
		this.wait = wait;
		this.url = url;
		this.username = username;
		this.password = password;
	}

	public List<CaseTime> execute() throws Throwable {
		
        List<CaseTime> caseTimes = new ArrayList<CaseTime>();
		
		login();
		selectGroup(null);
		launchStudyFromGroup(null);
		updateSelectionWorklist();
		try {
			while(true) {
	        	CaseTime time = new CaseTime();
				selectGroup(time);
				Boolean launched = launchStudyFromGroup(time);
				if(!launched) {
					break;
				}
				dictateStudy(time);
				caseTimes.add(time);
			}
		} catch(Throwable e) {
			e.printStackTrace();
		}
		
		return caseTimes;
	}
	
	public void calculateCaseTimes(List<CaseTime> caseTimes) {
		List<Long> worklistTimes = new ArrayList<Long>();
		long worklistSum = 0;
		for(CaseTime caseTime : caseTimes) {
			long worklistTime = 0;
			worklistTime += (caseTime.timings.get(END_DICTATE_CASE) - caseTime.timings.get(START_DICTATE_CASE));
			worklistTime += (caseTime.timings.get(SELECT_GROUP_CASE_START) - caseTime.timings.get(SELECT_GROUP_START));
			worklistSum += worklistTime;
			worklistTimes.add(worklistTime);
		}
		
		List<Long> encounterTimes = new ArrayList<Long>();
		long encounterSum = 0;
		for(CaseTime caseTime : caseTimes) {
			long encounterTime = 0;
			encounterTime += (caseTime.timings.get(SELECT_CASE_END) - caseTime.timings.get(SELECT_GROUP_CASE_START));
			encounterTime += (caseTime.timings.get(SELECT_CASE_PRIORS_END) - caseTime.timings.get(SELECT_CASE_END));
			encounterSum += encounterTime;
			encounterTimes.add(encounterTime);
		}
		
		String[] wrkAvg = { "Worklist Average (ms)", Long.toString(worklistSum/caseTimes.size()) };
		Output.getInstance().write(wrkAvg);
		
		String[] encounterAvg = { "Encounter Average (ms)", Long.toString(encounterSum/caseTimes.size()) };
		Output.getInstance().write(encounterAvg);
		
		Collections.sort(worklistTimes);
		Collections.sort(encounterTimes);
		
		String[] wrkNinety = { "Worklist 90% (ms)", Long.toString(worklistTimes.get((int)(caseTimes.size() *.9)-1)) };
		Output.getInstance().write(wrkNinety);

		String[] encounterNinety = { "Encounter 90% (ms)", Long.toString(encounterTimes.get((int)(caseTimes.size() *.9)-1)) };
		Output.getInstance().write(encounterNinety);
	}
	
	/**
	 * Updates next case selection to be worklist
	 * @throws Throwable
	 */
	private void updateSelectionWorklist() throws Throwable {
	    driver.findElement(By.cssSelector(NEXT_DROPDOWN)).click();
	    Thread.sleep(1000);
	    driver.findElement(By.cssSelector(WORKLIST_LINK)).click();
		
        Thread.sleep(1000);
        WebElement we = driver.findElement(By.xpath(SEND_TO_GROUP_BUTTON));
        JavascriptExecutor executor = (JavascriptExecutor) driver;
        executor.executeScript("arguments[0].click();", we);
		waitForPageLoad();		
	}

	/**
	 * Clicks the green checkmark
	 * @param time
	 */
	private void dictateStudy(CaseTime time) {
    	writeLine(START_DICTATE_CASE, "",driver.getCurrentUrl(), time);
    	JavascriptExecutor executor = (JavascriptExecutor) driver;
    	executor.executeScript("arguments[0].click();", driver.findElement(By.id("case_mark_dictated")));

		waitForPageLoad();
    	writeLine(END_DICTATE_CASE, "",driver.getCurrentUrl(), time);		
	}
	
	

}
