grails.project.work.dir = "target"
grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"

grails.project.dependency.resolution = {
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
        compile 'com.fasterxml.jackson.core:jackson-annotations:2.1.2'
        compile 'com.fasterxml.jackson.core:jackson-core:2.1.2'
        compile 'com.fasterxml.jackson.core:jackson-databind:2.1.2'
        compile 'com.fasterxml.jackson.dataformat:jackson-dataformat-xml:2.1.2'
        compile 'com.fasterxml.jackson.module:jackson-module-jaxb-annotations:2.1.2'
        compile 'com.github.groovy-wslite:groovy-wslite:0.7.2'

    }

    plugins {
        runtime ":hibernate:$grailsVersion"
        runtime ":jquery:1.8.3"
        runtime ":resources:1.1.6"
        compile ":jquery-ui:1.8.15"
        compile(":cbipcrowdauthentication:0.3.4") {
            excludes('spock', 'release', 'google-collections')
        }
        compile ':cache:1.0.1'

        build(":tomcat:$grailsVersion",
                ":release:2.2.0",
                ":rest-client-builder:1.0.3") {
            export = false
        }
    }  // runtime 'mysql:mysql-connector-java:5.1.21'

}
