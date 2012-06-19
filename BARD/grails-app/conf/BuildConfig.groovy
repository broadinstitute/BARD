grails.servlet.version = "2.5" // Change depending on target container compliance (2.5 or 3.0)
//grails.project.class.dir = "target/classes"
//grails.project.test.class.dir = "target/test-classes"
//grails.project.test.reports.dir = "target/test-reports"
grails.project.work.dir = "target"
grails.project.target.level = 1.6
grails.project.source.level = 1.6
grails.server.port.http=8081
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
        mavenRepo  "http://bard-repo.broadinstitute.org:8081/artifactory/bard-virtual-repo"
        grailsRepo("http://bard-repo.broadinstitute.org:8081/artifactory/bard-virtual-repo", "grailsCentral")
    }
    dependencies {
        // build scope

        // compile scope

        // runtime scope
         runtime 'mysql:mysql-connector-java:5.1.16'

        // test scope

        // provided  scope
    }

    plugins {
        // build scope
        build ":tomcat:$grailsVersion"

        // compile scope
        compile ":database-migration:1.1"
        compile ":db-reverse-engineer:0.4"
        compile ":extjs4:4.1.0-RC.0"
        compile ":grails-ui:1.2.3"
        compile ":json-rest-api:1.0.11"
        compile ":webflow:2.0.0"
        compile ":yui:2.8.2.1"

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
