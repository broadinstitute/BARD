grails.servlet.version = "2.5" // Change depending on target container compliance (2.5 or 3.0)
grails.project.work.dir = "target"
grails.project.target.level = 1.6
grails.project.source.level = 1.6
grails.server.port.http = 8081
//grails.project.war.file = "target/${appName}-${appVersion}.war"

def gebVersion = "0.9.0-RC-1"
def seleniumVersion = "2.31.0"


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

        //TODO: Without adding this repos the push-event plugin won't work. Needs further investigations
        mavenRepo "https://oss.sonatype.org/content/repositories/snapshots/"
    }
    dependencies {
        // specify dependencies here under either 'build', 'compile', 'runtime', 'test' or 'provided' scopes eg.
        // build scope

        // compile scope
        compile('cbip:cbip_encoding:0.1') {
            excludes "junit"
        }
        compile "org.grails:grails-webflow:$grailsVersion"
        compile "org.apache.httpcomponents:httpclient:4.2.3"

        compile "bard:external-validation:20130717"

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
        // build scope
        build(":codenarc:0.18.1") {
            excludes "groovy-all"
        }

        build ":improx:0.2" // Interactive Mode Proxy; useful for IDE integration
        build ":tomcat:$grailsVersion"

        // compile scope
        compile ":hibernate:$grailsVersion"
        compile ":jquery-ui:1.8.15"
        compile ":export:1.5"
        compile ":resources:1.2.RC2"
        compile ":twitter-bootstrap:2.3.0"
        compile(":cbipcrowdauthentication:0.3.4") {
            excludes('spock', 'release', 'google-collections')
        }
        compile ":clover:3.1.10.1"
        compile ":spring-mobile:0.4"
        compile ":console:1.2"
        compile ":jquery-validation-ui:1.4.2"
        compile ":twitter-bootstrap:2.2.2"
        compile(':webflow:2.0.0') {
            exclude 'grails-webflow'
        }
        compile ":spring-security-acl:1.1.1"
        compile ":remote-control:1.4"
        compile ":google-analytics:2.0"
        compile ":mail:1.0.1"
        compile ":greenmail:1.3.3"
        compile ":cache:1.0.1"
        compile ':events-push:1.0.M7'
        compile ":famfamfam:1.0.1"
        compile ":spring-security-ui:0.2"

        compile ":jesque:0.6.2"
        compile ":jesque-web:0.4.0"

        // runtime scope
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
grails.plugin.location.'bard-domain-model' = "../BardDomainModel"
grails.plugin.location.'crowd-user-registration' = "../crowdUserRegistration"


grails.plugin.location.'shopping-cart:0.8.2' = "../shopping-cart-0.8.2"

grails.plugin.location.'bard-rest-api-wrapper' = "../bard-rest-api-wrapper"
grails.plugin.location.'functional-spock' = "../functional-spock"
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
