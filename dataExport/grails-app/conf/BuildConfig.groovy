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
        inherit(false) // don't repositories from plugins
        grailsPlugins()
        grailsHome()
        mavenRepo 'http://bard-repo:8081/artifactory/bard-virtual-repo'
        grailsRepo('http://bard-repo:8081/artifactory/bard-virtual-repo', 'grailsCentral')
    }
    dependencies {
        compile('cbip:cbip_encoding:0.1') {
            excludes "junit"
        }
        compile 'com.oracle:ojdbc6:11.2.0.2.0'
        // specify dependencies here under either 'build', 'compile', 'runtime', 'test' or 'provided' scopes eg.
        test "org.spockframework:spock-grails-support:0.7-groovy-2.0"

        // used by spock for Mocking objects that have no args constructor
        //test "org.objenesis:objenesis:1.3" 

        test('org.codehaus.groovy.modules.http-builder:http-builder:0.5.2'){
            exclude "groovy"
        }
        test 'xmlunit:xmlunit:1.3'
    }

    plugins {
        build ":tomcat:$grailsVersion"
        build ":codenarc:0.18.1" {
            exclude "groovy-all"
        }
        build ":improx:0.2" // Interactive Mode Proxy; useful for IDE integration

        compile ":clover:3.1.6"
        test(":spock:0.7") {
            exclude "spock-grails-support"
        }
        compile ":remote-control:1.4"

        runtime ":hibernate:$grailsVersion"
        runtime ":resources:1.1.6"
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
clover {
    //initstring = "bardwebclover.db"
    directories: ['src/java', 'src/groovy', 'grails-app']
    includes = ['**/*.groovy', '**/*.java']
    excludes = ['**/*Spec*.*', '**/conf/**']
}
