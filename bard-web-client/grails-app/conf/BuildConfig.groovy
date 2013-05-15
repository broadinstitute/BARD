grails.servlet.version = "2.5" // Change depending on target container compliance (2.5 or 3.0)
//grails.project.class.dir = "target/classes"
//grails.project.test.class.dir = "target/test-classes"
//grails.project.test.reports.dir = "target/test-reports"
grails.project.work.dir = "target"
grails.project.target.level = 1.6
grails.project.source.level = 1.6
//grails.project.war.file = "target/${appName}-${appVersion}.war"

def gebVersion = "0.9.0-RC-1"
def seleniumVersion = "2.31.0"


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
        // build scope

        // compile scope
        compile('cbip:cbip_encoding:0.1') {
            excludes "junit"
        }
        compile "com.oracle:ojdbc6:11.2.0.2.0"
        compile 'org.apache.commons:commons-lang3:3.1'
        compile 'ChemAxon:ChemAxonJChemBase:5.10'
        compile 'jfree:jfreechart:1.0.13'
        compile('org.apache.httpcomponents:httpclient:4.1.2') {
            excludes "commons-codec", "commons-logging"
        }
        compile 'com.thoughtworks.xstream:xstream:1.4.2'
        compile "org.codehaus.groovy.modules.remote:remote-transport-http:0.5", {
            excludes "servlet-api"
        }

        // runtime scope
        // runtime 'mysql:mysql-connector-java:5.1.16'
        runtime('org.codehaus.groovy.modules.http-builder:http-builder:0.5.2') {
            excludes "commons-logging", "xml-apis", "groovy", "httpclient", "httpcore", "nekohtml"
        }

        // test scope
        test "org.spockframework:spock-grails-support:0.7-groovy-2.0"
        test "org.objenesis:objenesis:1.2" // used by spock for Mocking objects that have no args constructor
        test "org.gebish:geb-spock:$gebVersion"
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

        // provided scope
        provided('net.sourceforge.nekohtml:nekohtml:1.9.15') {
            exclude "xml-api"
        }
        provided 'org.apache.httpcomponents:httpcomponents-core:4.1.3'
    }

    plugins {
        //build scope
        build ":tomcat:$grailsVersion"
 //       build ":codenarc:0.18.1" {
//            excludes "groovy-all"
 //       }

        // compile scope
        compile ":jquery-ui:1.8.15"
        compile ":export:1.5"
        // runtime ":resources:1.1.6"
        compile ":resources:1.2.RC2"
        // compile ":functional-spock:0.6"
        compile ":twitter-bootstrap:2.3.0"
        compile(":cbipcrowdauthentication:0.3.2"){
            excludes('spock', 'release')
        }
        compile ":clover:3.1.10.1"
        compile ":spring-mobile:0.4"
        compile ":google-analytics:2.0"
        compile ":mail:1.0.1"
        compile ":greenmail:1.3.3"
        compile ":cache:1.0.1"

        // runtime scope
        runtime ":hibernate:$grailsVersion"
        runtime ":jquery:1.7.1"

        // test scope
        test(":spock:0.7") {
            exclude "spock-grails-support"
        }
        test "org.grails.plugins:geb:$gebVersion"
        test ":remote-control:1.4"
    }
}

// making the domain plugin an in-place plugin

grails.plugin.location.'shopping-cart:0.8.2' = "../shopping-cart-0.8.2"

grails.plugin.location.'bard-rest-api-wrapper' = "../bard-rest-api-wrapper"
grails.plugin.location.'functional-spock'="../functional-spock"
//grails.plugin.location.'remote-control-1.4'="../remote-control-1.4"

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
