/*
 This is the Geb configuration file.
 See: http://www.gebish.org/manual/current/configuration.html
 */



import org.apache.commons.lang3.StringUtils
import org.apache.commons.lang3.SystemUtils
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.firefox.FirefoxDriver
import org.openqa.selenium.firefox.FirefoxProfile
import org.openqa.selenium.ie.InternetExplorerDriver
import org.openqa.selenium.phantomjs.PhantomJSDriver
import org.openqa.selenium.phantomjs.PhantomJSDriverService
import org.openqa.selenium.remote.CapabilityType
import org.openqa.selenium.remote.DesiredCapabilities
import org.openqa.selenium.remote.RemoteWebDriver

driver = {
    new PhantomJSDriver(getPhantomjsDesiredCapabilities())
}
atCheckWaiting = true
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
        getFirefoxProfile()
        driver = { new FirefoxDriver() }
    }

    ie {
        System.setProperty("webdriver.ie.driver", "test/resources/IEDriverServer.exe")
        DesiredCapabilities capabilities = DesiredCapabilities.internetExplorer();
        capabilities.setCapability(InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS, true)
        capabilities.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true)
        driver = { new InternetExplorerDriver(capabilities) }
    }
    remote {
        driver = { new RemoteWebDriver(getPhantomjsDesiredCapabilities()) }
    }
    phantomjs {
        driver = {new PhantomJSDriver(getPhantomjsDesiredCapabilities())}
    }

}

private DesiredCapabilities getPhantomjsDesiredCapabilities() {
    final List<String> cliArgs = ["--web-security=false", "--ssl-protocol=any", "--ignore-ssl-errors=true", "--webdriver-loglevel=INFO"]
    final DesiredCapabilities desiredCapabilities = DesiredCapabilities.phantomjs()
    desiredCapabilities.setJavascriptEnabled(true)
    desiredCapabilities.setCapability("takesScreenshot", true)
    desiredCapabilities.setCapability(PhantomJSDriverService.PHANTOMJS_CLI_ARGS, cliArgs)
    desiredCapabilities.setCapability(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY, getPhantomJsExecutablePath())
    desiredCapabilities
}

public static String getPhantomJsExecutablePath() {
    if (StringUtils.isNotBlank(System.getProperty("PHANTOM_JS_EXECUTABLE_PATH"))) {
        return System.getProperty("PHANTOM_JS_EXECUTABLE_PATH");
    } else if (SystemUtils.IS_OS_MAC_OSX) {
        return "/usr/local/bin/phantomjs"   // assuming brew was used to install
    } else if (SystemUtils.IS_OS_LINUX) {
        return "/cbplat/bard/dev_tools/phantomjs/phantomjs-1.9.2-linux-x86_64/bin"
    } else {
        throw new RuntimeException("Unsure of OS, so couldn't pick an executable for phantomjs")
    }
}

private void getFirefoxProfile() {
    File profileDir = new File("target/firefoxProfile")
    if (!profileDir.exists()) {
        profileDir.mkdir()
    }
    FirefoxProfile profile = new FirefoxProfile(profileDir)
    profile.setPreference("webdriver.load.strategy", "unstable")
    profile.setAssumeUntrustedCertificateIssuer(true)
}