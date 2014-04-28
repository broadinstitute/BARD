/* Copyright (c) 2014, The Broad Institute
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
    * Redistributions of source code must retain the above copyright
      notice, this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright
      notice, this list of conditions and the following disclaimer in the
      documentation and/or other materials provided with the distribution.
    * Neither the name of The Broad Institute nor the
      names of its contributors may be used to endorse or promote products
      derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL The Broad Institute BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

grails.project.work.dir = "target"
grails.project.target.level = 1.6
grails.project.source.level = 1.6


grails.project.dependency.resolution = {
    // inherit Grails' default dependencies
    inherits("global") {
        // uncomment to disable ehcache
        // excludes 'ehcache'
    }
    //log "warn" // log level of Ivy resolver, either 'error', 'warn', 'info', 'debug' or 'verbose'
    repositories {
        inherits false // Whether to inherit repository definitions from plugins
        grailsPlugins()
        grailsHome()
        mavenRepo 'http://bard-repo:8081/artifactory/bard-virtual-repo'
        grailsRepo('http://bard-repo:8081/artifactory/bard-virtual-repo', 'grailsCentral')
    }
    dependencies {
        compile 'org.apache.httpcomponents:httpmime:4.1.1'
        compile 'joda-time:joda-time:1.6'
        provided('net.sourceforge.nekohtml:nekohtml:1.9.15') {
            exclude "xml-api"
        }
        compile 'org.apache.commons:commons-lang3:3.1'
        compile 'com.fasterxml.jackson.core:jackson-annotations:2.1.2'
        compile 'com.fasterxml.jackson.core:jackson-core:2.1.2'
        compile 'com.fasterxml.jackson.core:jackson-databind:2.1.2'
        compile 'com.fasterxml.jackson.dataformat:jackson-dataformat-xml:2.1.2'
        compile 'com.fasterxml.jackson.module:jackson-module-jaxb-annotations:2.1.2'
        compile 'org.codehaus.woodstox:stax2-api:3.1.1'
        compile 'org.codehaus.woodstox:woodstox-core-asl:4.1.2'

        // specify dependencies here under either 'build', 'compile', 'runtime', 'test' or 'provided' scopes eg.
        compile "org.spockframework:spock-grails-support:0.7-groovy-2.0"

        test "org.objenesis:objenesis:1.2" // used by spock for Mocking objects that have no args constructor
    }

    plugins {
        build ":improx:0.2" // Interactive Mode Proxy; useful for IDE integration
        build(":tomcat:$grailsVersion",
                ":release:2.0.3",
                ":rest-client-builder:1.0.2") {
            export = false
        }
        test(":spock:0.7") {
            exclude "spock-grails-support"
        }
        test ":codenarc:0.18.1"
        compile ":clover:3.1.10.1"
        compile ":cache:1.0.1"
    }
}
clover {
    directories: ['src/java', 'src/groovy', 'grails-app']
    includes = ['**/*.groovy', '**/*.java']
    excludes = ['**/RESTTestHelper.*', '**/*Spec*.*', '**/conf/**', '**/JSONNodeTestHelper.java']
}
codenarc.ruleSetFiles = "file:grails-app/conf/BardRestApiWrapperCodeNarcRuleSet.groovy"
codenarc.reports = {
    html('html') {
        outputFile = 'target/codenarc-reports/html/BARD-CodeNarc-Report.html'
        title = 'BARD CodeNarc Report'
    }
}
codenarc {
    exclusions = ['**/grails-app/migrations/*']
}
