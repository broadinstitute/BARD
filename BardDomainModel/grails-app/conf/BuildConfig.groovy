grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"
grails.project.target.level = 1.6
//grails.project.war.file = "target/${appName}-${appVersion}.war"

//grails.project.repos.barddomain.url =  "http://cbip-repo:8081/artifactory/repo"

grails.project.dependency.resolution = {
    // inherit Grails' default dependencies
    inherits("global") {
        // uncomment to disable ehcache
        // excludes 'ehcache'
    }
    log "warn" // log level of Ivy resolver, either 'error', 'warn', 'info', 'debug' or 'verbose'
    repositories {
        grailsPlugins()
        grailsHome()
        mavenRepo 'http://cbip-repo:8081/artifactory/repo'
        grailsRepo 'http://cbip-repo:8081/artifactory/repo'
        grailsCentral()
    }
    dependencies {
        // specify dependencies here under either 'build', 'compile', 'runtime', 'test' or 'provided' scopes eg.

        // runtime 'mysql:mysql-connector-java:5.1.5'
        compile 'com.oracle:ojdbc6:11.2.0.2.0'
    }

    plugins {
        build(":tomcat:$grailsVersion",
              ":release:2.0.0") {
            export = false
        }
        build ":database-migration:1.0"
    }
}

grails.project.repos.default = "releases"

grails.project.repos.releases.url = "http://cbip-repo:8081/artifactory/plugins-release-local"
grails.project.repos.releases.username = "bcbdev"
grails.project.repos.releases.password = "ch3mh3ad"

grails.project.repos.snapshots.url = "http://cbip-repo:8081/artifactory/plugins-snapshot-local"
grails.project.repos.snapshots.username = "bcbdev"
grails.project.repos.snapshots.password = "ch3mh3ad"

