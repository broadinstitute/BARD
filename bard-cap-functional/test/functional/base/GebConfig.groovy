/*
 This is the Geb configuration file.
 See: http://www.gebish.org/manual/current/configuration.html
 */


//import org.codehaus.groovy.grails.commons.ConfigurationHolder;
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.firefox.FirefoxDriver
import org.openqa.selenium.firefox.FirefoxProfile
import org.openqa.selenium.htmlunit.HtmlUnitDriver
import org.openqa.selenium.ie.InternetExplorerDriver
import org.openqa.selenium.remote.CapabilityType
import org.openqa.selenium.remote.DesiredCapabilities
//import org.openqa.selenium.safari.SafariDriver
//import org.openqa.selenium.opera.OperaDriver
//import com.opera.core.systems.OperaDriver
//import com.opera.core.systems.OperaProfile
//import com.thoughtworks.selenium.DefaultSelenium
//import org.openqa.selenium.remote.CommandExecutor
//import org.openqa.selenium.SeleneseCommandExecutor

// Use htmlunit as the default
// See: http://code.google.com/p/selenium/wiki/HtmlUnitDriver
driver = {
	def driver = new HtmlUnitDriver()
	driver.javascriptEnabled = true
	driver
}

environments {

	// run as grails -Dgeb.env=chrome test-app
	// See: http://code.google.com/p/selenium/wiki/ChromeDriver
	chrome {
		// Set the location of the chromedriver executable
		System.setProperty("webdriver.chrome.driver", "test/resources/chromedriver.exe")
		// Workaround that causes Chrome not to start up properly
		// see http://code.google.com/p/selenium/issues/detail?id=2681
		DesiredCapabilities capabilities = new DesiredCapabilities()
		capabilities.setCapability("chrome.switches", Arrays.asList("--ignore-certificate-errors"))

		driver = { new ChromeDriver(capabilities) }
	}

	// run as grails -Dgeb.env=firefox test-app
	// See: http://code.google.com/p/selenium/wiki/FirefoxDriver
	firefox {
		File profileDir = new File("target/firefoxProfile")
		if (!profileDir.exists()) { profileDir.mkdir() }
		FirefoxProfile profile = new FirefoxProfile(profileDir)
		profile.setPreference("webdriver.load.strategy", "unstable");
		profile.setAssumeUntrustedCertificateIssuer(true);
		driver = { new FirefoxDriver() }
	}

	// run as grails -Dgeb.env=ie test-app
	ie {
		System.setProperty("webdriver.ie.driver", "test/resources/IEDriverServer.exe")
		DesiredCapabilities capabilities = DesiredCapabilities.internetExplorer();
		capabilities.setCapability(InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS, true)
		capabilities.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true)
		driver = { new InternetExplorerDriver(capabilities) }
	}


	/*safari{
	 //baseURL="http://www.google.co.jp/"
	 //def sel = new DefaultSelenium("localhost", 4444, "*safari", baseURL);
	 //CommandExecutor executor = new SeleneseCommandExecutor(sel);
	 //DesiredCapabilities dc = new DesiredCapabilities();
	 //dc.setBrowserName("safari")
	 //dc.setCapability("platform" ,org.openqa.selenium.Platform.WINDOWS)
	 //dc.setJavascriptEnabled(true);
	 driver = new SafariDriver();
	 }*/


}