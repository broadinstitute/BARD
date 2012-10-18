
/*
	This is the Geb configuration file.

	See: http://www.gebish.org/manual/current/configuration.html
*/


import org.openqa.selenium.firefox.FirefoxDriver
import org.openqa.selenium.firefox.FirefoxProfile
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions
import org.openqa.selenium.htmlunit.HtmlUnitDriver
import org.openqa.selenium.remote.DesiredCapabilities

// Use htmlunit as the default
// See: http://code.google.com/p/selenium/wiki/HtmlUnitDriver
driver = {
    def driver = new HtmlUnitDriver()
    driver.javascriptEnabled = true
    driver
}
environments {

    // run as “grails -Dgeb.env=chrome test-app”
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

    // run as “grails -Dgeb.env=firefox test-app”
    // See: http://code.google.com/p/selenium/wiki/FirefoxDriver
    firefox {
        File profileDir = new File("target/firefoxProfile")
        if (!profileDir.exists()) { profileDir.mkdir() }

        FirefoxProfile profile = new FirefoxProfile(profileDir)
        driver = { new FirefoxDriver(profile) }
    }

}