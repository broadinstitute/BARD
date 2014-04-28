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

import bard.util.BardCacheUtilsService
import bard.validation.ext.ExternalOntologyFactory
import bard.validation.extext.PersonCreator
import org.apache.log4j.Appender
import org.apache.log4j.Logger
import org.apache.log4j.filter.DenyAllFilter
import org.apache.log4j.filter.ExpressionFilter
import org.apache.log4j.filter.StringMatchFilter
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.codehaus.groovy.grails.plugins.springsecurity.SecurityFilterPosition
import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils

class BootStrap {
    BardCacheUtilsService bardCacheUtilsService
    ExternalOntologyFactory externalOntologyFactory
    GrailsApplication grailsApplication

    def init = { servletContext ->
        if (grailsApplication.config.grails.plugin.databasemigration.updateOnStart) {
            // if we are starting solely for the purpose of updating the schema, don't bother
            // populating caches
            return
        }
        //add extra log4j filters
        addLog4jFilters()

        SpringSecurityUtils.clientRegisterFilter('personaAuthenticationFilter', SecurityFilterPosition.SECURITY_CONTEXT_FILTER.order + 10)
        bardCacheUtilsService.refreshDueToNonDictionaryEntry()


    }

    def destroy = {
    }
    /**
     *
     * Allows us to exclude some errors from being sent by email
     * Allows logging of some specific exceptions to specific files
     */
    void addLog4jFilters() {
        Enumeration appenders = ((Logger) Logger.getLogger(BootStrap.class)).getRootLogger().getAllAppenders()
        while (appenders.hasMoreElements()) {
            Appender appender = (Appender) appenders.nextElement();
            if (appender.name == "mySQLAppender") {
                handleMySQLErrorFileLogging(appender)
            } else if (appender.name == "accessDeniedAppender") {
                handleAccessDeniedFileLogging(appender)
            } else {
                final List<ExpressionFilter> excludeFilters = getSmtpExcludeFilters()
                excludeFilters.each { excludeFilter ->
                    appender.addFilter(excludeFilter)
                }
            }

        }
    }

    //Handle the BoneCP MySQL errors
    void handleMySQLErrorFileLogging(Appender appender) {

        ExpressionFilter boneCPFilter = new ExpressionFilter()
        boneCPFilter.expression =
            "THREAD ~= 'BoneCP-keep-alive-scheduler'"
        boneCPFilter.acceptOnMatch = true
        boneCPFilter.activateOptions()
        appender.addFilter(boneCPFilter)

        //We use the following filter to deny everything but the one we defined above
        DenyAllFilter denyAllFilter = new DenyAllFilter()
        appender.addFilter(denyAllFilter)
    }
    //Call this method so that we log this message to a specific file
    //when access denied errors occur or when a security exception occurs
    void handleAccessDeniedFileLogging(Appender appender) {

        ExpressionFilter accessDeniedFilter = new ExpressionFilter()
        accessDeniedFilter.expression = "(EXCEPTION ~= org.springframework.security.access.AccessDeniedException) || (EXCEPTION ~= org.springframework.security.authentication.AuthenticationServiceException)"
        accessDeniedFilter.acceptOnMatch = true
        accessDeniedFilter.activateOptions()
        appender.addFilter(accessDeniedFilter)

        //We use the following filter to deny everything but the one we defined above
        DenyAllFilter denyAllFilter = new DenyAllFilter()
        appender.addFilter(denyAllFilter)

    }
    //add these filters so that we do not send email when they occur
    List<ExpressionFilter> getSmtpExcludeFilters() {
        List<ExpressionFilter> excludeFilters = []

        //authentication service exception
        ExpressionFilter authenticationExpressionFilter = new ExpressionFilter()
        authenticationExpressionFilter.expression = "EXCEPTION ~= org.springframework.security.authentication.AuthenticationServiceException"
        authenticationExpressionFilter.acceptOnMatch = false
        authenticationExpressionFilter.activateOptions()
        excludeFilters.add(authenticationExpressionFilter)

        //exclude access denied errors
        ExpressionFilter accessDeniedFilter = new ExpressionFilter()
        accessDeniedFilter.expression = "EXCEPTION ~= org.springframework.security.access.AccessDeniedException"
        accessDeniedFilter.acceptOnMatch = false
        accessDeniedFilter.activateOptions()
        excludeFilters.add(accessDeniedFilter)

        ExpressionFilter boneCPFilter = new ExpressionFilter()
        boneCPFilter.expression =
            "THREAD ~= 'BoneCP-keep-alive-scheduler'"
        boneCPFilter.acceptOnMatch = true
        boneCPFilter.activateOptions()
        excludeFilters.add(boneCPFilter)
        return excludeFilters
    }

}
