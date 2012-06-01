package barddataexport.cap.util

import barddataexport.experiment.ExperimentExportService
import barddataexport.experiment.ResultExportService
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.codehaus.groovy.grails.web.servlet.HttpHeaders
import javax.servlet.http.HttpServletResponse
import groovy.xml.MarkupBuilder
import barddataexport.util.RootService

class RootRestController {
    RootService rootService
    GrailsApplication grailsApplication
    static allowedMethods = [
            api: "GET",
    ]

    def index() {
        return unsupported()
    }

    def api() {
        def mimeType = grailsApplication.config.bard.data.export.bardexport.xml
        response.contentType = mimeType
        //do validations
        if (mimeType != request.getHeader(HttpHeaders.ACCEPT)) {
            response.status = HttpServletResponse.SC_BAD_REQUEST
            render ""
            return
        }
        final def writer = response.writer
        final MarkupBuilder xml = new MarkupBuilder(writer)
        rootService.generateRootElement(xml)
    }
}
