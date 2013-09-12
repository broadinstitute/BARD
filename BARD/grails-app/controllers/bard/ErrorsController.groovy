package bard

import bard.util.ErrorService
import grails.plugins.springsecurity.Secured
import grails.util.Environment
import org.codehaus.groovy.grails.commons.GrailsApplication

@Secured(['permitAll'])
class ErrorsController {
    def error403 = {}

    def error500 = {
        Exception exception = request.exception

        String errorId = UUID.randomUUID().toString();

        log.error("Error ${errorId}: Uncaught exception", errorId)

        boolean showException = Environment.getCurrent().name != Environment.PRODUCTION

        render(view: '/error', model: [errorId: errorId, exception: exception, showException: showException])
    }
}