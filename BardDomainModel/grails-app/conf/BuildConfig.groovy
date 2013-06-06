import grails.util.Environment

grails.project.work.dir = "target"
grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"
grails.project.target.level = 1.6



grails.project.dependency.resolution = {
    // inherit Grails' default dependencies
    inherits("global") {
        // uncomment to disable ehcache
        // excludes 'ehcache'
    }
    log "warn" // log level of Ivy resolver, either 'error', 'warn', 'info', 'debug' or 'verbose'

    repositories {
        inherit(false) // don't repositories from plugins
        grailsPlugins()
        grailsHome()
        mavenRepo 'http://bard-repo:8081/artifactory/bard-virtual-repo'
        grailsRepo('http://bard-repo:8081/artifactory/bard-virtual-repo', 'grailsCentral')
    }

    dependencies {
        build 'com.oracle:ojdbc6:11.2.0.2.0'
        test "org.spockframework:spock-grails-support:0.7-groovy-2.0"
        compile 'org.apache.commons:commons-lang3:3.1'
        compile 'org.apache.commons:commons-math3:3.1'
    }

    plugins {
        build(":tomcat:$grailsVersion") { export = false }
        build(":codenarc:0.18.1") { export = false }
        // seems like rest client builder is required by release plugin but not getting included transitively
        // so adding explicitly here
        build(":rest-client-builder:1.0.2") { export = false }
        build(":release:2.2.1") { export = false }
        build(":improx:0.2") { export = false } // Interactive Mode Proxy; useful for IDE integration
        compile(":clover:3.1.10.1") { export = false }
        compile(":console:1.2") { export = false }
        compile(":cbipcrowdauthentication:0.3.4") {
            excludes('spock', 'release', 'google-collections')
        }

        compile(":database-migration:1.3.2") { export = true }
        compile(":spock:0.7") {
            export = false
            exclude("spock-grails-support")
        }
        compile ":spring-security-acl:1.1.1"
        /**
         * including build test data for all environments except production, oracleqa, oracledev
         */
        switch (Environment.current.name) {
            case ('production'):
            case ('oracleqa'):
            case ('oracledev'):
                break
            default:
                compile(":build-test-data:2.0.4") { export = true }
                compile(":fixtures:1.2") {
                    export = true
                    exclude('svn')
                }
                break
        }
    }
}

grails.project.repos.default = "releases"

grails.project.repos.releases.url = "http://bard-repo.broadinstitute.org:8081/artifactory/plugins-release-local"

// create a ~/.grails/settings.groovy file with these
// login to bard-repo:8443, click on your profile and get the unescaped encrypted password from artifactory
// use that encrypted password in the settings.groovy file
//
// grails.project.repos.releases.username = "changeme"
// grails.project.repos.releases.password = "changeme"

grails.project.repos.snapshots.url = "http://bard-repo.broadinstitute.org:8081/artifactory/plugins-snapshot-local"
grails.project.repos.snapshots.username = "changeme"
grails.project.repos.snapshots.password = "changeme"

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
    license.path = "${userHome}/.grails/clover.license"
    directories: ['src/java', 'src/groovy', 'grails-app']
    includes = ['**/*.groovy', '**/*.java']
    excludes = ['test/**.*', '**/*Spec*.*', '**/conf/**', '**/migrations/**']
}
