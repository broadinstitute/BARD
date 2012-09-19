grails.servlet.version = "2.5" // Change depending on target container compliance (2.5 or 3.0)
//grails.project.class.dir = "target/classes"
//grails.project.test.class.dir = "target/test-classes"
//grails.project.test.reports.dir = "target/test-reports"
grails.project.work.dir = "target"
grails.project.target.level = 1.6
grails.project.source.level = 1.6
grails.server.port.http = 8081
//grails.project.war.file = "target/${appName}-${appVersion}.war"

grails.project.dependency.resolution = {
    // inherit Grails' default dependencies
    inherits("global") {
        // uncomment to disable ehcache
        // excludes 'ehcache'
    }
    log "warn" // log level of Ivy resolver, either 'error', 'warn', 'info', 'debug' or 'verbose'
    checksums true // Whether to verify checksums on resolve

    repositories {
        inherits true // Whether to inherit repository definitions from plugins
        grailsPlugins()
        grailsHome()
        mavenRepo "http://bard-repo.broadinstitute.org:8081/artifactory/bard-virtual-repo"
        grailsRepo("http://bard-repo.broadinstitute.org:8081/artifactory/bard-virtual-repo", "grailsCentral")
    }
    dependencies {
        // build scope

        // compile scope

        // runtime scope
        runtime 'mysql:mysql-connector-java:5.1.16'
        runtime 'com.github.groovy-wslite:groovy-wslite:0.7.0'
        compile('cbip:cbip_encoding:0.1') {
            excludes "junit"
        }

        // test scope
        test "org.spockframework:spock-core:0.6-groovy-1.8"
        test "org.objenesis:objenesis:1.2" // used by spock for Mocking object that lack no args constructor

        // provided  scope
    }

    plugins {
        // build scope
        build ":tomcat:$grailsVersion"
        build ":codenarc:0.15"
        // compile scope
        compile ":grails-ui:1.2.3"
        compile ":yui:2.8.2.1"
        compile ":twitter-bootstrap:2.1.0"
        compile ":elastic-search:0.4.5-SNAPSHOT"
        compile ":spring-security-core:1.2.7.3"
        compile(":build-test-data:2.0.3")

        // runtime scope
        runtime ":hibernate:$grailsVersion"
        runtime ":jquery:1.7.1"
        runtime ":jquery-ui:1.8.15"
        runtime ":resources:1.1.6"
        // test scope
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
