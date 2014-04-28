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

import org.apache.commons.io.FileUtils
import org.openqa.selenium.By
import org.openqa.selenium.Dimension
import org.openqa.selenium.OutputType
import org.openqa.selenium.TakesScreenshot
import org.openqa.selenium.WebDriver
import org.openqa.selenium.firefox.FirefoxDriver
import org.openqa.selenium.phantomjs.PhantomJSDriver
import org.openqa.selenium.remote.DesiredCapabilities
import org.openqa.selenium.remote.RemoteWebDriver
import spock.lang.Specification

/**
 * Created with IntelliJ IDEA.
 * User: ddurkin
 * Date: 10/23/13
 * Time: 4:24 PM
 * To change this template use File | Settings | File Templates.
 */
class SearchPageSpec extends Specification {


    private static final String FIREFOX = 'firefox'
    private static final String PHANTOMJS = 'phantomjs'

    File dstDir

    private static WebDriver getDriver(String browserName) {
        WebDriver driver
        switch (browserName) {
            case FIREFOX:
                driver = new FirefoxDriver(getDesiredCapabilities());
                break
            case PHANTOMJS:
                driver = new PhantomJSDriver(getDesiredCapabilities());
                break
            default:
                throw new RuntimeException("need driver name")
        }
        driver.manage().window().setSize(new Dimension(1024, 768))
        driver
    }

    private static WebDriver getFirfoxDriver() {
        return
    }

    private static DesiredCapabilities getDesiredCapabilities() {
        DesiredCapabilities desiredCapabilities = new DesiredCapabilities()
        desiredCapabilities.setCapability("takesScreenshot", true);
        desiredCapabilities
    }

    void setup() {
        dstDir = new File("build/screenshots")
        dstDir.mkdirs()
    }

    void "test foo"() {
        given:
        WebDriver driver = getDriver(PHANTOMJS);

        when:
        // Load page
        driver.get("http://youruseragent.info/what-is-my-user-agent");
        // Read values from page
        String myUA = driver.findElement(By.cssSelector("#ua-string span")).getText();
        String myIP = driver.findElement(By.cssSelector("#ip-address span")).getText();

        // Print
        println("My User-Agent: " + myUA);
        println("My IP: " + myIP);

        then:
        myUA
        myIP


    }

    void "test bar"() {
        given:
        WebDriver driver = getDriver(PHANTOMJS);

        when:
        // Load page
        driver.get("http://www.google.com");
        // Read values from page
        File file = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE)
        FileUtils.copyFile(file, new File(dstDir, "{driver.getTitle()}.png"))

        then:
        driver.getTitle()


    }

    void "test bard"() {
        given:
        WebDriver driver = getDriver(FIREFOX);

        when:
        // Load page
        driver.get("http://bard-qa.broadinstitute.org");
        // Read values from page
        File srcFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE)
        FileUtils.copyFile(srcFile, new File(dstDir, "{driver.getTitle()}.png"))

        then:
        driver.getTitle()
    }


}
