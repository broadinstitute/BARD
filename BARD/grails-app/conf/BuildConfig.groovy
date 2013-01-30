grails.servlet.version = "2.5" // Change depending on target container compliance (2.5 or 3.0)
//grails.project.class.dir = "target/classes"
//grails.project.test.class.dir = "target/test-classes"
//grails.project.test.reports.dir = "target/test-reports"
grails.project.work.dir = "target"
grails.project.target.level = 1.6
grails.project.source.level = 1.6
grails.server.port.http = 8081
//grails.project.war.file = "target/${appName}-${appVersion}.war"

def gebVersion = "0.7.2"
def seleniumVersion = "2.25.0"

grails.project.dependency.resolution = {
    // inherit Grails' default dependencies
    inherits("global") {
        // uncomment to disable ehcache
        // excludes 'ehcache'
    }
    log "warn" // log level of Ivy resolver, either 'error', 'warn', 'info', 'debug' or 'verbose'
    checksums true // Whether to verify checksums on resolve

    repositories {
        inherit(false) // don't repositories from plugins
        grailsPlugins()
        grailsHome()
        mavenRepo "http://bard-repo.broadinstitute.org:8081/artifactory/bard-virtual-repo"
        grailsRepo("http://bard-repo.broadinstitute.org:8081/artifactory/bard-virtual-repo", "grailsCentral")
    }
    dependencies {
        // build scope

        // compile scope
        compile('cbip:cbip_encoding:0.1') {
            excludes "junit"
        }
        compile "org.grails:grails-webflow:$grailsVersion"

        // runtime scope
        runtime 'com.github.groovy-wslite:groovy-wslite:0.7.0'

        // test scope
        test "org.spockframework:spock-core:0.6-groovy-1.8"
        test "org.objenesis:objenesis:1.2" // used by spock for Mocking object that lack no args constructor
        test "org.codehaus.geb:geb-spock:$gebVersion"
        test("org.seleniumhq.selenium:selenium-htmlunit-driver:$seleniumVersion") {
            excludes "xml-apis"
        }
        test("org.seleniumhq.selenium:selenium-chrome-driver:$seleniumVersion") {
            exclude "xml-apis"
        }
        test("org.seleniumhq.selenium:selenium-firefox-driver:$seleniumVersion") {
            excludes "xml-apis"
        }
        test("org.seleniumhq.selenium:selenium-remote-driver:$seleniumVersion") {
            excludes "xml-apis"
        }

        // provided  scope
    }

    plugins {
        // build scope
        build ":codenarc:0.15"
        compile ":hibernate:$grailsVersion"
        build ":tomcat:$grailsVersion"

        // compile scope
        compile ":ajaxflow:0.2.4"
        compile ":cbipcrowdauthentication:0.3.0"
        compile ":clover:3.1.6"
        compile ":console:1.2"
        compile ":jquery-validation-ui:1.4"
        compile ":twitter-bootstrap:2.2.2"
        compile(':webflow:2.0.0') {
            exclude 'grails-webflow'
        }

        // runtime scope
        runtime ":jquery:1.7.1"
        runtime ":jquery-ui:1.8.15"
        runtime ":resources:1.1.6"

        // test scope
        test ":geb:$gebVersion"
        test ":remote-control:1.2"
        test ":spock:0.6"
        // provided  scope
    }
}

// making the domain plugin an in-place plugin
grails.plugin.location.'bard-domain-model' = "../BardDomainModel"

codenarc.ruleSetFiles = "file:grails-app/conf/BardCodeNarcRuleSet.groovy"
codenarc.reports = {
    html('html') {
        outputFile = 'target/codenarc-reports/html/BARD-CodeNarc-Report.html'
        title = 'BARD CodeNarc Report'
    }
}
codenarc {
    exclusions = ['**/grails-app/migrations/*']
}

clover {
    //initstring = "bardwebclover.db"
    directories: ['src/java', 'src/groovy', 'grails-app']
    includes = ['**/*.groovy', '**/*.java']
    excludes = ['**/bardwebquery/**.*', '**/*Spec*.*', '**/mockServices/**.*', '**/conf/**', '**/GridController.*', '**/mockServices/**.*']
}
// used for tomcat when running grails run-war
grails.tomcat.jvmArgs = ["-server", "-XX:MaxPermSize=256m", "-Xmx768m"]
