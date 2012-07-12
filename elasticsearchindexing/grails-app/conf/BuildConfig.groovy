grails.servlet.version = "2.5" // Change depending on target container compliance (2.5 or 3.0)
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
        test "org.objenesis:objenesis:1.2" // used by spock for Mocking object that lack no args constructor

        test "org.spockframework:spock-core:0.6-groovy-1.8"
        // specify dependencies here under either 'build', 'compile', 'runtime', 'test' or 'provided' scopes eg.
        runtime 'com.github.groovy-wslite:groovy-wslite:0.7.0'
    }

    plugins {
        compile ":spock:0.6"
        compile ":executor:0.3"
        runtime ":hibernate:$grailsVersion"
        compile ":resources:1.1.6"
    }
}
