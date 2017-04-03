package com.candescent.selenium;

import java.util.List;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.candescent.selenium.Arguments.Browser;
import com.candescent.selenium.Arguments.Environment;
import com.candescent.selenium.Arguments.Scenario;
import com.candescent.selenium.scenario.NextCaseScenario;
import com.candescent.selenium.scenario.WorklistScenario;
import com.candescent.selenium.util.Output;

public class Main {

	public static void main(String[] args) {
		System.out.println("Starting Application");
		
		//Pull arguments from command line.
		Arguments arguments = handleArguments(args);
		if(!arguments.isValid()) {
			return;
		}
		
		//Set default environment parameters for Selenium
		setDefaultProperties();
		
		//Define Selenium Driver
		RemoteWebDriver driver = defineDriver(arguments);
		Wait<WebDriver> wait = new WebDriverWait(driver, 15,50);
		
		//Run the proper test.
		try { 
			runScenario(driver, wait, arguments);
		} catch(Throwable t) {
			System.out.println(t);
		} finally {
			driver.quit();
			Output.getInstance().close();
		}
	}

	private static Arguments handleArguments(String[] args) {
		Arguments arguments = new Arguments();
		
		if(args.length < 4 || args.length > 6) {
			System.out.println("Invalid Arguments.  Need type of scenario (next, worklist), environment (test, prod), username, password, and browser.  Optional next case sleep (true/false)");
    		arguments.setValid(Boolean.FALSE);
			return arguments;
		}
		
		Scenario scenario = Arguments.Scenario.find(args[0]);
		if(scenario == null) {
			System.out.println("Invalid Arguments.  Scenario must be either next or worklist");
			arguments.setValid(Boolean.FALSE);
			return arguments;
		}
		
		arguments.setScenario(scenario);
		
		Environment env = Arguments.Environment.find(args[1]);
		if(env == null) {
			System.out.println("Invalid Arguments.  Environment must be either prod or test");
			arguments.setValid(Boolean.FALSE);
			return arguments;
		}
		arguments.setEnvironment(env);
		
		arguments.setUsername(args[2]);
		arguments.setPassword(args[3]);
		
		Browser browser = Arguments.Browser.find(args[4]);
		if(browser == null) {
			System.out.println("Invalid Arguments.  Browser must be either chrome or ie");
			arguments.setValid(Boolean.FALSE);
			return arguments;
		}
		arguments.setBrowser(browser);
		
		if(args.length > 5 && args[5].equals("true")) {
			arguments.setNextWait(Boolean.TRUE);
		}
		
		arguments.setValid(Boolean.TRUE);
		return arguments;
	}
	
	private static void setDefaultProperties() {
        System.setProperty("webdriver.ie.driver.extractpath", Constants.WEBDRIVER_IE_DRIVER_EXTRACTPATH);
        System.setProperty("webdriver.ie.driver", Constants.WEBDRIVER_IE_DRIVER);
        System.setProperty("webdriver.ie.logfile", Constants.WEBDRIVER_IE_LOGFILE);
        
        System.setProperty("webdriver.chrome.driver", Constants.WEBDRIVER_CHROME_DRIVER);		
	}
	
	private static RemoteWebDriver defineDriver(Arguments arguments) {
        RemoteWebDriver driver;
        switch(arguments.getBrowser()) {
	        case CHROME:
	    		ChromeOptions options = new ChromeOptions();
	    		options.setBinary(Constants.CHROME_LOCATION);
	    		driver = new ChromeDriver(options);
	        	break;
	        case IE:
	        default:
	            DesiredCapabilities ieCaps = DesiredCapabilities.internetExplorer();
	            ieCaps.setCapability(InternetExplorerDriver.INITIAL_BROWSER_URL, arguments.getEnvironment().getUrl());
	            driver = new InternetExplorerDriver(ieCaps);  
	        	break;
        }
        return driver;
	}
	

	private static void runScenario(RemoteWebDriver driver, Wait<WebDriver> wait, Arguments arguments) throws Throwable {
		switch(arguments.getScenario()) {
			case WORKLIST: 
				WorklistScenario wt = new WorklistScenario(
						driver, 
						wait,
						arguments.getEnvironment().getUrl(),
						arguments.getUsername(),
						arguments.getPassword()
						);
				List<CaseTime> wtTimes = wt.execute();
				wt.calculateCaseTimes(wtTimes);
				break;
			case NEXT:
			default:
				NextCaseScenario nct = new NextCaseScenario(
						driver,
						wait,
						arguments.getEnvironment().getUrl(),
						arguments.getUsername(),
						arguments.getPassword(),
						arguments.getNextWait());
				List<CaseTime> nctTimes = nct.execute();
				nct.calculateCaseTimes(nctTimes);
				break;
		}
	}
}
