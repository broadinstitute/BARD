/* Copyright (c) 2014, The Broad Institute
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
    * Redistributions of source code must retain the above copyright
      notice, this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright
      notice, this list of conditions and the following disclaimer in the
      documentation and/or other materials provided with the distribution.
    * Neither the name of The Broad Institute nor the
      names of its contributors may be used to endorse or promote products
      derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL The Broad Institute BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
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

import common.ConfigHelper;

/*
 This is the Geb configuration file.
 See: http://www.gebish.org/manual/current/configuration.html
 */

driver = {
    new PhantomJSDriver(getPhantomjsDesiredCapabilities())
}
atCheckWaiting = true
baseUrl = ConfigHelper.config.server.url
reportsDir = "build/geb-reports"
environments {

    chrome {
        // Set the location of the chromedriver executable
        System.setProperty("webdriver.chrome.driver", "test/resources/chromedriver.exe")
        DesiredCapabilities capabilities = new DesiredCapabilities()
        capabilities.setCapability("chrome.switches", Arrays.asList("--ignore-certificate-errors"))
//        capabilities.setCapability("chrome.switches", Arrays.asList("--start-maximized"))
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
        return "/cbplat/bard/dev_tools/phantomjs/phantomjs-1.9.2-linux-x86_64/bin/phantomjs"
    } else {
        throw new RuntimeException("Unsure of OS, so couldn't pick an executable for phantomjs")
    }
}

private void getFirefoxProfile() {
    File profileDir = new File("build/firefoxProfile")
    if (!profileDir.exists()) {
        profileDir.mkdir()
    }
    FirefoxProfile profile = new FirefoxProfile(profileDir)
    profile.setPreference("webdriver.load.strategy", "unstable")
    profile.setAssumeUntrustedCertificateIssuer(true)
}
