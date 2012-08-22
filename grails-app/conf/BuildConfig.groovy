grails.servlet.version = "2.5" // Change depending on target container compliance (2.5 or 3.0)
//grails.project.class.dir = "target/classes"
//grails.project.test.class.dir = "target/test-classes"
//grails.project.test.reports.dir = "target/test-reports"
grails.project.work.dir = "target"
grails.project.target.level = 1.6
grails.project.source.level = 1.6
//grails.project.war.file = "target/${appName}-${appVersion}.war"

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
        test "org.spockframework:spock-core:0.6-groovy-1.8"
        runtime 'com.github.groovy-wslite:groovy-wslite:0.7.0'
        test "org.objenesis:objenesis:1.2" // used by spock for Mocking objects that have no args constructor

        test('org.codehaus.groovy.modules.http-builder:http-builder:0.5.2') {
            excludes "commons-logging", "xml-apis", "groovy"
        }

        compile 'org.apache.commons:commons-lang3:3.1'
        compile 'ChemAxon:ChemAxonJChemBase:5.10'
        compile 'org.apache.httpcomponents:httpcomponents-core:4.1.1'
        compile 'org.apache.httpcomponents:httpclient:4.1.1'
        compile 'org.codehaus.jackson:jackson-core-asl:1.9.2'
        compile 'org.codehaus.jackson:jackson-mapper-asl:1.9.2'
        compile 'ChemAxon:ChemAxonJChemBase:5.10'
    }

    plugins {
        runtime ":hibernate:$grailsVersion"
        runtime ":jquery:1.7.1"
        compile ":jquery-ui:1.8.15"
        runtime ":resources:1.1.6"
        compile ":spock:0.6"
        compile ":functional-spock:0.6"
        build ":codenarc:0.15"
        compile ":elastic-search:0.4.5-SNAPSHOT"
        compile ":twitter-bootstrap:2.0.2.25"

        build ":tomcat:$grailsVersion"

        compile ":remote-control:1.2"
    }
}

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