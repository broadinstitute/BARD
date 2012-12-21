grails.servlet.version = "2.5" // Change depending on target container compliance (2.5 or 3.0)
//grails.project.class.dir = "target/classes"
//grails.project.test.class.dir = "target/test-classes"
//grails.project.test.reports.dir = "target/test-reports"
grails.project.work.dir = "target"
grails.project.target.level = 1.6
grails.project.source.level = 1.6
//grails.project.war.file = "target/${appName}-${appVersion}.war"
def gebVersion = "0.7.2"
def seleniumVersion = "2.25.0"
grails.project.dependency.resolution = {
    // inherit Grails' default dependencies
    inherits("global") {
        // uncomment to disable ehcache
        // excludes 'ehcache'
    }

    log "error" // log level of Ivy resolver, either 'error', 'warn', 'info', 'debug' or 'verbose'
    checksums true // Whether to verify checksums on resolve

    repositories {
        grailsPlugins()
        grailsHome()
        mavenRepo 'http://bard-repo:8081/artifactory/bard-virtual-repo'
        grailsRepo('http://bard-repo:8081/artifactory/bard-virtual-repo', 'grailsCentral')
    }
    dependencies {

        // specify dependencies here under either 'build', 'compile', 'runtime', 'test' or 'provided' scopes eg.

        // runtime 'mysql:mysql-connector-java:5.1.16'
        test("org.spockframework:spock-core:0.6-groovy-1.8") {
            exclude "groovy-all"
        }
        test "org.objenesis:objenesis:1.2" // used by spock for Mocking objects that have no args constructor

        runtime('org.codehaus.groovy.modules.http-builder:http-builder:0.5.2') {
            excludes "commons-logging", "xml-apis", "groovy", "httpclient", "httpcore", "nekohtml"
        }
        test "org.codehaus.geb:geb-spock:$gebVersion"
        test("org.seleniumhq.selenium:selenium-htmlunit-driver:$seleniumVersion") {
            excludes "xml-apis", "commons-io"
        }
        test("org.seleniumhq.selenium:selenium-chrome-driver:$seleniumVersion") {
            exclude "xml-apis"
        }
        test("org.seleniumhq.selenium:selenium-firefox-driver:$seleniumVersion") {
            excludes "xml-apis", "commons-io"
        }
        test("org.seleniumhq.selenium:selenium-remote-driver:$seleniumVersion") {
            excludes "xml-apis"
        }
        provided('net.sourceforge.nekohtml:nekohtml:1.9.15') {
            exclude "xml-api"
        }
        compile 'org.apache.commons:commons-lang3:3.1'
        provided 'org.apache.httpcomponents:httpcomponents-core:4.1.3'
        compile('org.apache.httpcomponents:httpclient:4.1.2') {
            excludes "commons-codec", "commons-logging"
        }
        compile 'ChemAxon:ChemAxonJChemBase:5.10'
        compile 'jfree:jfreechart:1.0.13'
        compile 'com.thoughtworks.xstream:xstream:1.4.2'
    }

    plugins {
        runtime ":hibernate:$grailsVersion"
        runtime ":jquery:1.7.1"
        compile ":jquery-ui:1.8.15"
        //  compile ":export:1.5"
        runtime ":resources:1.1.6"
        compile ":functional-spock:0.6"
        compile ":twitter-bootstrap:2.1.0"
        compile ":shopping-cart:0.8.2"
        compile ":cbipcrowdauthentication:0.3.0"
        build ":tomcat:$grailsVersion"
        test ":spock:0.6"
        test ":codenarc:0.15"
        test ":geb:$gebVersion"
        test ":remote-control:1.2"
        compile ":clover:3.1.6"
        compile ":spring-mobile:0.4"
        compile ":google-analytics:2.0"
        compile ":mail:1.0.1"
        compile ":greenmail:1.3.3"
    }
}

// making the domain plugin an in-place plugin
grails.plugin.location.'bard-rest-api-wrapper' = "../bard-rest-api-wrapper"

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
