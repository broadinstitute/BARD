package bard

import org.apache.commons.lang.exception.ExceptionUtils
import grails.plugins.springsecurity.Secured
import grails.util.Environment
import org.springframework.beans.factory.annotation.Value

@Secured(['permitAll'])
class ErrorsController {
    @Value('${bard.showStackTraceOnErrorPage}')
    boolean showStackTraceOnErrorPage

    def error403 () {

    }

    def error500 () {
        try {
            Exception exception = request.exception

            String errorId = UUID.randomUUID().toString();

            log.error("Error ${errorId}: Uncaught exception", exception)

            render(view: '/error', model: [errorId: errorId, exception: exception, showException: showStackTraceOnErrorPage])
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