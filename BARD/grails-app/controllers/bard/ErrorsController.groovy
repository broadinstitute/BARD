package bard

import bard.util.ErrorService
import clover.org.apache.commons.lang.exception.ExceptionUtils
import grails.plugins.springsecurity.Secured
import grails.util.Environment
import org.codehaus.groovy.grails.commons.GrailsApplication

@Secured(['permitAll'])
class ErrorsController {
    def error403 () {

    }

    def error500 () {
        try {
            Exception exception = request.exception

            String errorId = UUID.randomUUID().toString();

            log.error("Error ${errorId}: Uncaught exception", exception)

            boolean showException = Environment.getCurrent().name != Environment.PRODUCTION

            render(view: '/error', model: [errorId: errorId, exception: exception, showException: showException])
        } catch(Exception ex) {
            log.error("Exception writing error page", ex)
            def trace = ExceptionUtils.getFullStackTrace(ex)
            println("exception writing error page: ${trace}")
        }
    }

    def simulateError() {
        throw new RuntimeException("Threw sample exception")
    }
}