package com.candescent.selenium;

public class Arguments {

	public Arguments() { 
		
	}
	
	public enum Browser { 
		CHROME("chrome"), 
		IE("ie");
		
		private String name;
		
		Browser(String name) {
			this.name = name;
		}
		
		public static Browser find(String name) {
			for (Browser browser : Browser.values()) {
		      if (browser.name.equalsIgnoreCase(name)) {
		        return browser;
		      }
		    }
		    return null;
		}
	
	};
	public enum Scenario { 
		WORKLIST("worklist"), 
		NEXT("next");
		
		private String name;
		
		Scenario(String name) {
			this.name = name;
		}
		
		public static Scenario find(String name) {
			for (Scenario scenario : Scenario.values()) {
		      if (scenario.name.equalsIgnoreCase(name)) {
		        return scenario;
		      }
		    }
		    return null;
		}
	};
	
	
	public enum Environment {
		PRODUCTION("prod", Constants.PROD_BASE_URL), 
		TEST("test", Constants.TEST_BASE_URL);
		
		private String name;
		private String url;
		
		Environment(String name, String url) {
			this.name = name;
			this.url = url;
		}
		
		public String getUrl() {
			return url;
		}
		
		public static Environment find(String name) {
			for (Environment env : Environment.values()) {
		      if (env.name.equalsIgnoreCase(name)) {
		        return env;
		      }
		    }
		    return null;
		}
	};
	
	private Boolean valid;
	private String username;
	private String password; 
	private Boolean nextWait = Boolean.FALSE;
	private Environment environment;
	private Browser browser;
	private Scenario scenario;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Boolean getNextWait() {
		return nextWait;
	}

	public void setNextWait(Boolean nextWait) {
		this.nextWait = nextWait;
	}

	public Environment getEnvironment() {
		return environment;
	}

	public void setEnvironment(Environment environment) {
		this.environment = environment;
	}

	public Browser getBrowser() {
		return browser;
	}

	public void setBrowser(Browser browser) {
		this.browser = browser;
	}

	public Scenario getScenario() {
		return scenario;
	}

	public void setScenario(Scenario scenario) {
		this.scenario = scenario;
	}

	public Boolean isValid() {
		return valid;
	}

	public void setValid(Boolean valid) {
		this.valid = valid;
	}
	
}
