package bard.util

import bard.db.registration.Assay
import grails.buildtestdata.DomainInstanceBuilder
import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils

class ScaffoldService {
    def grailsApplication;

    Map builderCache = [:]

    public static void testMethod() {
        println("hello")
    }

    def build(String className, Map properties = [:]) {
        SpringSecurityUtils.reauthenticate('integrationTestUser', null)
        DomainInstanceBuilder builder = builderCache[className]

        if (builder == null) {
            def c = grailsApplication.getArtefactInfo("Domain").getGrailsClass(className)
            if(c == null) {
                throw new RuntimeException("could not find class ${className}")
            }

            builder = new DomainInstanceBuilder(c)
            builderCache[className] = builder
        }

        return builder.build(properties)
    }
}
