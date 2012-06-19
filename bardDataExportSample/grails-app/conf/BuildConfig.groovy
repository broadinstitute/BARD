grails.servlet.version = "2.5" // Change depending on target container compliance (2.5 or 3.0)
//grails.project.class.dir = "target/classes"
//grails.project.test.class.dir = "target/test-classes"
//grails.project.test.reports.dir = "target/test-reports"
grails.project.work.dir = "target"
grails.project.target.level = 1.6
grails.project.source.level = 1.6
//grails.project.war.file = "target/${appName}-${appVersion}.war"

//we exclude the following from the war file
grails.war.resources = { stagingDir ->
    delete(file:"${stagingDir}/WEB-INF/classes/common/tests/XmlTestSamples.class")
    delete(file:"${stagingDir}/WEB-INF/classes/common/tests/XmlTestAssertions.class")
    delete(dir:"${stagingDir}/WEB-INF/classes/common/tests")
}
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
        grailsCentral()
        mavenRepo "http://cbip-repo:8081/artifactory/repo"
        grailsRepo "http://cbip-repo:8081/artifactory/svn.codehaus.org_grails-plugins"
        grailsRepo "http://cbip-repo:8081/artifactory/svn.codehaus.org_grails_trunk_grails-plugins"
    }
    dependencies {
        compile 'com.oracle:ojdbc6:11.2.0.2.0'
        // specify dependencies here under either 'build', 'compile', 'runtime', 'test' or 'provided' scopes eg.
        test "org.spockframework:spock-core:0.6-groovy-1.8"
        test('org.codehaus.groovy.modules.http-builder:http-builder:0.5.2'){
            exclude "groovy"
        }
        test "org.objenesis:objenesis:1.2" // used by spock for Mocking objects that have no args constructor
        // runtime 'mysql:mysql-connector-java:5.1.16'
        test 'xmlunit:xmlunit:1.3'
    }

    plugins {
        runtime ":hibernate:$grailsVersion"
        runtime ":jquery:1.7.1"
        runtime ":resources:1.1.6"
        runtime ":bard-domain-model:0.1.2"
        compile ":functional-spock:0.6"


        // Uncomment these (or add new ones) to enable additional resources capabilities
        //runtime ":zipped-resources:1.0"
        //runtime ":cached-resources:1.0"
        //runtime ":yui-minify-resources:0.1.4"

        build ":tomcat:$grailsVersion"
        compile ":spock:0.6"
        compile ":remote-control:1.2"
    }
}
