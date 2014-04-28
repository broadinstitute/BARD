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
        Exception exception
        try {
            exception = request.exception

            String errorId = UUID.randomUUID().toString();

            if(exception == null) {
                log.error("Error ${errorId}: Requested URI ${request.requestURI} -> Forwarded URI ${request.forwardURI}")
            } else {
                log.error("Error ${errorId}: Requested URI ${request.requestURI}, Uncaught exception", exception)
            }

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
            if(exception != null)
                log.error("Original exception which resulted in error rendering error page", exception)
        }
    }

    def simulateError() {
        throw new RuntimeException("Threw sample exception")
    }
}
