package barddataexport.cap.util

import barddataexport.util.RootService
import groovy.xml.MarkupBuilder
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.codehaus.groovy.grails.web.servlet.HttpHeaders

import javax.servlet.http.HttpServletResponse

class RootRestController {
    RootService rootService
    GrailsApplication grailsApplication
    static allowedMethods = [
            api: "GET",
    ]

    def index() {
        return response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED)
    }
    def api() {
        try {
            final String mimeType = grailsApplication.config.bard.data.export.bardexport.xml
            response.contentType = mimeType
            //do validations
            if (mimeType == request.getHeader(HttpHeaders.ACCEPT)) {
                final def writer = response.writer
                final MarkupBuilder xml = new MarkupBuilder(writer)
                rootService.generateRootElement(xml)
                return
            }
            response.status = HttpServletResponse.SC_BAD_REQUEST
            render ""

        } catch (Exception ee) {
            response.status = HttpServletResponse.SC_INTERNAL_SERVER_ERROR
            log.error(ee.message)
            ee.printStackTrace()
        }
    }
}
