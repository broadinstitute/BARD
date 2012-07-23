package dataexport.cap.util

import dataexport.util.RootService
import groovy.xml.MarkupBuilder
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.codehaus.groovy.grails.web.servlet.HttpHeaders

import javax.servlet.http.HttpServletResponse

/**
 * Please note that the DataExportFilters is applied to all incoming request.
 * ALL incoming request need to have a custom http header named 'APIKEY' and the correct MD5 hash value
 * In addition, the request's remote IP address has to be whitelisted in the commons-config file.
 */

class RootRestController {
    RootService rootService
    GrailsApplication grailsApplication
    static allowedMethods = [
            api: "GET",
    ]

    static final String responseContentTypeEncoding = "UTF-8"

    def index() {
        return response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED)
    }
    def api() {
        try {
            final String mimeType = grailsApplication.config.bard.data.export.bardexport.xml
            //do validations
            if (mimeType == request.getHeader(HttpHeaders.ACCEPT)) {
                final StringWriter markupWriter = new StringWriter()
                final MarkupBuilder markupBuilder = new MarkupBuilder(markupWriter)
                this.rootService.generateRootElement(markupBuilder)
                render (text: markupWriter.toString(), contentType: mimeType, encoding: responseContentTypeEncoding)

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
