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
        grailsCentral()
        // uncomment the below to enable remote dependency resolution
        // from public Maven repositories
        //mavenCentral()
        //mavenLocal()
        //mavenRepo "http://snapshots.repository.codehaus.org"
        //mavenRepo "http://repository.codehaus.org"
        //mavenRepo "http://download.java.net/maven/2/"
        //mavenRepo "http://repository.jboss.com/maven2/"
    }
    dependencies {
        // specify dependencies here under either 'build', 'compile', 'runtime', 'test' or 'provided' scopes eg.

        // runtime 'mysql:mysql-connector-java:5.1.5'
    }

    plugins {
        build(":tomcat:$grailsVersion",
              ":release:1.0.0") {
            export = false
        }
        build(":release:2.0.2") {
            export = false
        }

    }
    grails.project.repos.default = "releases"

    grails.project.repos.releases.url = "http://bard-repo.broadinstitute.org:8081/artifactory/plugins-release-local"
    grails.project.repos.snapshots.url = "http://bard-repo.broadinstitute.org:8081/artifactory/plugins-snapshot-local"
    //NOTE: TODO This is something you must do to publish to artifcatory
    // create a ~/.grails/settings.groovy file with these
// login to bard-repo:8443, click on your profile and get the unescaped encrypted password from artifactory
// use that encrypted password in the settings.groovy file
//
// grails.project.repos.releases.username = "changeme"
// grails.project.repos.releases.password = "changeme"


}
