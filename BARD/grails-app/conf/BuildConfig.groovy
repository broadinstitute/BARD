grails.servlet.version = "2.5" // Change depending on target container compliance (2.5 or 3.0)
//grails.project.class.dir = "target/classes"
//grails.project.test.class.dir = "target/test-classes"
//grails.project.test.reports.dir = "target/test-reports"
grails.project.work.dir = "target"
grails.project.target.level = 1.6
grails.project.source.level = 1.6
grails.server.port.http = 8081
//grails.project.war.file = "target/${appName}-${appVersion}.war"

def seleniumVersion = "2.32.0"

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
        compile "org.apache.httpcomponents:httpclient:4.2.3"

        compile "bard:external-validation:20130430"

        // runtime scope
        runtime 'com.github.groovy-wslite:groovy-wslite:0.7.0'
        runtime 'commons-net:commons-net:3.2'
        runtime 'net.sf.opencsv:opencsv:2.3'
        runtime 'com.fasterxml.jackson.core:jackson-annotations:2.1.2'
        runtime 'com.fasterxml.jackson.core:jackson-core:2.1.2'
        runtime 'com.fasterxml.jackson.core:jackson-databind:2.1.2'
        runtime 'com.fasterxml.jackson.dataformat:jackson-dataformat-xml:2.1.2'
        runtime 'com.fasterxml.jackson.module:jackson-module-jaxb-annotations:2.1.2'

        // this dependency seems a little ridiculous but some page renders seem to fail and adding this
        // was advised in http://stackoverflow.com/questions/12627147/grails-rendering-plugin-gives-`java-lang-classnotfoundexception-when-deployed
        runtime 'org.springframework:spring-test:3.1.2.RELEASE'

        // test scope
        test "org.spockframework:spock-grails-support:0.7-groovy-2.0"
        test "org.objenesis:objenesis:1.3" // used by spock for Mocking object that lack no args constructor

        test "org.gebish:geb-spock:0.9.0-RC-1"
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
        build(":codenarc:0.18.1") {
            excludes "groovy-all"
        }
        compile ":hibernate:$grailsVersion"
        build ":improx:0.2" // Interactive Mode Proxy; useful for IDE integration
        build ":tomcat:$grailsVersion"

        // compile scope
        compile ":ajaxflow:0.2.4"
        compile(":cbipcrowdauthentication:0.3.1") {
            excludes('spock', 'release', 'google-collections')
        }
        compile ":clover:3.1.6"
        compile ":console:1.2"
        compile ":jquery-validation-ui:1.4.2"
        compile ":twitter-bootstrap:2.2.2"
        compile(':webflow:2.0.0') {
            exclude 'grails-webflow'
        }

        // runtime scope
        runtime ":jquery:1.7.1"
        runtime ":jquery-ui:1.8.15"
        runtime ":resources:1.1.6"

        // test scope
        test ":geb:0.7.2"
        test ":remote-control:1.4"
        test(":spock:0.7") {
            exclude "spock-grails-support"
        }
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
