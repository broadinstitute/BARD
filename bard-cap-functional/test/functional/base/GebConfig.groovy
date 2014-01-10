/*
 This is the Geb configuration file.
 See: http://www.gebish.org/manual/current/configuration.html
 */

import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.firefox.FirefoxDriver
import org.openqa.selenium.firefox.FirefoxProfile
import org.openqa.selenium.htmlunit.HtmlUnitDriver
import org.openqa.selenium.ie.InternetExplorerDriver
import org.openqa.selenium.remote.CapabilityType
import org.openqa.selenium.remote.DesiredCapabilities

driver = {
	def d = new HtmlUnitDriver()
	d.javascriptEnabled = true
	d
}
baseUrl = System.properties.getProperty('server.url') ?: "http://localhost:8080/BARD/"
environments {

	chrome {
		// Set the location of the chromedriver executable
		System.setProperty("webdriver.chrome.driver", "test/resources/chromedriver.exe")
		DesiredCapabilities capabilities = new DesiredCapabilities()
		capabilities.setCapability("chrome.switches", Arrays.asList("--ignore-certificate-errors"))
		capabilities.setCapability("chrome.switches", Arrays.asList("--start-maximized"))
		driver = { new ChromeDriver(capabilities) }
	}

	firefox {
		File profileDir = new File("target/firefoxProfile")
		if (!profileDir.exists()) { profileDir.mkdir() }
		FirefoxProfile profile = new FirefoxProfile(profileDir)
		profile.setPreference("webdriver.load.strategy", "unstable");
		profile.setAssumeUntrustedCertificateIssuer(true);
		driver = { new FirefoxDriver() }
	}

	ie {
		System.setProperty("webdriver.ie.driver", "test/resources/IEDriverServer.exe")
		DesiredCapabilities capabilities = DesiredCapabilities.internetExplorer();
		capabilities.setCapability(InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS, true)
		capabilities.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true)
		driver = { new InternetExplorerDriver(capabilities) }
	}

}