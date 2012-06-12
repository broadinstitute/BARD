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
            element: "GET",
            updateElemet: "PATCH"
    ]

    def index() {
        return unsupported()
    }

    def dictionary() {
        try {
            final String mimeType = grailsApplication.config.bard.data.export.dictionary.xml
            response.contentType = mimeType
            //do validations
            if (mimeType == request.getHeader(HttpHeaders.ACCEPT)) {
                final Writer writer = response.writer
                final MarkupBuilder xml = new MarkupBuilder(writer)
                dictionaryExportService.generateDictionary(xml)

                return
            }
            response.status = HttpServletResponse.SC_BAD_REQUEST
            render ""
            return

        } catch (Exception ee) {
            response.status = HttpServletResponse.SC_INTERNAL_SERVER_ERROR
            log.error(ee.message)
            ee.printStackTrace()
            return
        }
    }

    def resultType() {
        try {
            final String mimeType = grailsApplication.config.bard.data.export.dictionary.resultType.xml
            response.contentType = mimeType
            //do validations
            if (mimeType == request.getHeader(HttpHeaders.ACCEPT) && params.id) {
                final BigDecimal resultTypeId = params.id as BigDecimal
                final Writer writer = response.writer
                final MarkupBuilder xml = new MarkupBuilder(writer)
                dictionaryExportService.generateResultType(xml, resultTypeId)
                return
            }
            response.status = HttpServletResponse.SC_BAD_REQUEST
            render ""
            return
        } catch (Exception ee) {
            response.status = HttpServletResponse.SC_INTERNAL_SERVER_ERROR
            log.error(ee.message)
            ee.printStackTrace()
            return
        }
    }

    def stage() {
        try {
            final String mimeType = grailsApplication.config.bard.data.export.dictionary.stage.xml
            response.contentType = mimeType
            //do validations
            if (mimeType == request.getHeader(HttpHeaders.ACCEPT) && params.id) {
                final BigDecimal stageId = params.id as BigDecimal

                final Writer writer = response.writer
                final MarkupBuilder xml = new MarkupBuilder(writer)
                dictionaryExportService.generateStage(xml, stageId)
                return
            }
            response.status = HttpServletResponse.SC_BAD_REQUEST
            render ""
        } catch (Exception ee) {
            response.status = HttpServletResponse.SC_INTERNAL_SERVER_ERROR
            log.error(ee.message)
            ee.printStackTrace()
            return
        }
    }

    def element() {
        try {
            final String mimeType = grailsApplication.config.bard.data.export.dictionary.element.xml
            response.contentType = mimeType
            //do validations
            if (mimeType == request.getHeader(HttpHeaders.ACCEPT) && params.id) {
                final BigDecimal elementId = params.id as BigDecimal
                final Writer writer = response.writer
                final MarkupBuilder xml = new MarkupBuilder(writer)
                dictionaryExportService.generateElementWithElementId(xml, elementId)
                return
            }
            response.status = HttpServletResponse.SC_BAD_REQUEST
            render ""
        } catch (Exception ee) {
            response.status = HttpServletResponse.SC_INTERNAL_SERVER_ERROR
            log.error(ee.message)
            ee.printStackTrace()
            return
        }
    }

    def updateElement() {

    }
}
