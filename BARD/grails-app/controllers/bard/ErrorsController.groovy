package bard

import bard.core.exceptions.RestApiException
import org.apache.commons.lang.exception.ExceptionUtils
import grails.plugins.springsecurity.Secured
import grails.util.Environment
import org.springframework.beans.factory.annotation.Value

import javax.servlet.http.HttpServletResponse

@Secured(['permitAll'])
class ErrorsController {
    @Value('${bard.showStackTraceOnErrorPage}')
    boolean showStackTraceOnErrorPage

    def error403 () {
        if (request.getHeader("X-Requested-With") == "XMLHttpRequest") {
            render(status: HttpServletResponse.SC_FORBIDDEN, text: "We're sorry, but you are not authorized to perform the requested operation.", contentType: 'text/plain', template: null)
        }
    }

    // returns true if bard.core.exceptions.RestApiException is one of the causes of the exception chain
    private def isApiException(Exception ex) {
//        List causes = ex.getThrowableList(ex);
//        println "causes: ${causes}";
//        for(cause in causes) {
//            if(RestApiException.isAssignableFrom(cause)) {
//                return true;
//            }
//        }

        return ExceptionUtils.indexOfType(ex, RestApiException) >= 0;
    }

    def error500 () {
        try {
            Exception exception = request.exception

            String errorId = UUID.randomUUID().toString();

            log.error("Error ${errorId}: Uncaught exception ${System.identityHashCode(exception)}", exception)

            boolean wasAjaxRequest = request.getHeader("X-Requested-With") == "XMLHttpRequest";
            boolean wasApiException = exception != null && isApiException(exception)

            if (wasAjaxRequest) {
                if(wasApiException) {
                    render(status: HttpServletResponse.SC_INTERNAL_SERVER_ERROR, text: "An error occurred communicating with warehouse (error: ${errorId})", contentType: 'text/plain', template: null)
                } else {
                    render(status: HttpServletResponse.SC_INTERNAL_SERVER_ERROR, text: "Error ${errorId}", contentType: 'text/plain', template: null)
                }
            } else {
                if(wasApiException) {
                    render(view: '/layouts/warehouseUnavailable', model: [message: "Got an error communicating with the warehouse"])
                } else {
                    render(view: '/error', model: [errorId: errorId, exception: exception, showException: (showStackTraceOnErrorPage && exception != null)])
                }
            }
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
