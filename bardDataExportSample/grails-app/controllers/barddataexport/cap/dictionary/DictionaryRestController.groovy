package barddataexport.cap.dictionary

import barddataexport.dictionary.DictionaryExportService
import groovy.xml.MarkupBuilder
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.codehaus.groovy.grails.web.servlet.HttpHeaders

import javax.servlet.http.HttpServletResponse

class DictionaryRestController {
    DictionaryExportService dictionaryExportService
    GrailsApplication grailsApplication
    static allowedMethods = [
            dictionary: "GET",
            resultType: "GET",
            stage: "GET",
            element: "GET"
    ]

    def index() {
        return unsupported()
    }

    def dictionary() {
        def mimeType = grailsApplication.config.bard.data.export.dictionary.xml
        response.contentType = mimeType
        //do validations
        if (mimeType != request.getHeader(HttpHeaders.ACCEPT)) {
            response.status = HttpServletResponse.SC_BAD_REQUEST

            render ""
            return
        }
        final def writer = response.writer
        final MarkupBuilder xml = new MarkupBuilder(writer)
        dictionaryExportService.generateDictionary(xml)
    }

    def resultType() {
        def mimeType = grailsApplication.config.bard.data.export.dictionary.resultType.xml
        response.contentType = mimeType
        //do validations
        if (mimeType == request.getHeader(HttpHeaders.ACCEPT) && params.resultTypeId) {
            final BigDecimal resultTypeId = params.resultTypeId as BigDecimal
            final def writer = response.writer
            final MarkupBuilder xml = new MarkupBuilder(writer)
            dictionaryExportService.generateResultType(xml, resultTypeId)
            return
        }
        response.status = HttpServletResponse.SC_BAD_REQUEST
        render ""
    }

    def stage() {
        def mimeType = grailsApplication.config.bard.data.export.dictionary.stage.xml
        response.contentType = mimeType
        //do validations
        if (mimeType == request.getHeader(HttpHeaders.ACCEPT) && params.stageId) {
            final BigDecimal stageId = params.stageId as BigDecimal

            final def writer = response.writer
            final MarkupBuilder xml = new MarkupBuilder(writer)
            dictionaryExportService.generateStage(xml, stageId)
            return
        }
        response.status = HttpServletResponse.SC_BAD_REQUEST
        render ""
    }

    def element() {
        def mimeType = grailsApplication.config.bard.data.export.dictionary.element.xml
        response.contentType = mimeType
        //do validations
        if (mimeType == request.getHeader(HttpHeaders.ACCEPT) && params.elementId) {
            final BigDecimal elementId = params.elementId as BigDecimal
            final def writer = response.writer
            final MarkupBuilder xml = new MarkupBuilder(writer)
            dictionaryExportService.generateElement(xml, elementId)
            return
        }
        response.status = HttpServletResponse.SC_BAD_REQUEST
        render ""
    }
}
