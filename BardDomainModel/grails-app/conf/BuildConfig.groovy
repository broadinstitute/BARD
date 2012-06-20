grails.project.work.dir = "target"
grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"
grails.project.target.level = 1.6
//grails.project.war.file = "target/${appName}-${appVersion}.war"


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
        mavenRepo 'http://bard-repo:8081/artifactory/bard-virtual-repo'
        grailsRepo('http://bard-repo:8081/artifactory/bard-virtual-repo', 'grailsCentral')
    }
    dependencies {
        // specify dependencies here under either 'build', 'compile', 'runtime', 'test' or 'provided' scopes eg.

        // runtime 'mysql:mysql-connector-java:5.1.5'
        build 'com.oracle:ojdbc6:11.2.0.2.0'
    }

    plugins {
        build(":tomcat:$grailsVersion"){
            export = false
        }
        // seems like rest client builder is required by release plugin but not getting included transitively
        // so adding explicitly here
        build(":rest-client-builder:1.0.2"){
            export = false
        }
        build(":release:2.0.2") {
            export = false
        }
        build ":database-migration:1.1"
    }
}

grails.project.repos.default = "releases"

grails.project.repos.releases.url = "http://bard-repo.broadinstitute.org:8081/artifactory/plugins-release-local"
// TODO externalize these
grails.project.repos.releases.username = "changeme"
// TODO document artifactory encrypted password howto
grails.project.repos.releases.password = "changeme"

grails.project.repos.snapshots.url = "http://bard-repo.broadinstitute.org:8081/artifactory/plugins-snapshot-local"
grails.project.repos.snapshots.username = "changeme"
grails.project.repos.snapshots.password = "changeme"

