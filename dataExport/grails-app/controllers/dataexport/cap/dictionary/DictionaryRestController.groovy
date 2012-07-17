package dataexport.cap.dictionary

import dataexport.dictionary.DictionaryExportService
import exceptions.NotFoundException
import groovy.xml.MarkupBuilder
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.codehaus.groovy.grails.web.servlet.HttpHeaders

import javax.servlet.http.HttpServletResponse

/**
 * Please note that the DataExportFilters is applied to all incoming request.
 * ALL incoming request need to have a custom http header named 'APIKEY' and the correct MD5 hash value
 * In addition, the request's remote IP address has to be whitelisted in the commons-config file.
 */

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
        return response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED)
    }
    /**
     * Fetch the entire dictionary
     * @return
     */
    def dictionary() {
        try {
            final String mimeType = grailsApplication.config.bard.data.export.dictionary.xml
            response.contentType = mimeType
            //do validations
            //mime types must match the expected type
            if (mimeType == request.getHeader(HttpHeaders.ACCEPT)) {
                final Writer writer = response.writer
                final MarkupBuilder xml = new MarkupBuilder(writer)
                dictionaryExportService.generateDictionary(xml)

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
    /**
     * Get a result type given an element id
     * @return
     */
    def resultType() {
        try {
            final String mimeType = grailsApplication.config.bard.data.export.dictionary.resultType.xml
            response.contentType = mimeType
            //do validations
            if (mimeType == request.getHeader(HttpHeaders.ACCEPT) && params?.id) {
                final Writer writer = response.writer
                final MarkupBuilder xml = new MarkupBuilder(writer)
                dictionaryExportService.generateResultType(xml, new Long(params.id))
                return
            }
            response.status = HttpServletResponse.SC_BAD_REQUEST
            render ""
        }
        catch (NotFoundException notFoundException) {
            log.error(notFoundException.message)
            response.status = HttpServletResponse.SC_NOT_FOUND
            render ""
        } catch (Exception ee) {
            response.status = HttpServletResponse.SC_INTERNAL_SERVER_ERROR
            log.error(ee.message)
            ee.printStackTrace()
        }
    }
    /**
     * Get the stage with the given an element id
     * @return
     */
    def stage() {
        try {
            final String mimeType = grailsApplication.config.bard.data.export.dictionary.stage.xml
            response.contentType = mimeType
            //do validations
            if (mimeType == request.getHeader(HttpHeaders.ACCEPT) && params.id) {
                final Writer writer = response.writer
                final MarkupBuilder xml = new MarkupBuilder(writer)

                dictionaryExportService.generateStage(xml, new Long(params.id))
                return
            }
            response.status = HttpServletResponse.SC_BAD_REQUEST
            render ""
        }
        catch (NotFoundException notFoundException) {
            log.error(notFoundException.message)
            response.status = HttpServletResponse.SC_NOT_FOUND
            render ""
        } catch (Exception ee) {
            response.status = HttpServletResponse.SC_INTERNAL_SERVER_ERROR
            log.error(ee.message)
            ee.printStackTrace()
        }
    }
/**
 * Get the element with the given id
 * @return
 */
    def element() {
        try {
            final String mimeType = grailsApplication.config.bard.data.export.dictionary.element.xml
            response.contentType = mimeType
            //do validations
            if (mimeType == request.getHeader(HttpHeaders.ACCEPT) && params.id) {
                final Writer writer = response.writer
                final MarkupBuilder xml = new MarkupBuilder(writer)
                dictionaryExportService.generateElement(xml, new Long(params.id))
                return
            }
            response.status = HttpServletResponse.SC_BAD_REQUEST
            render ""
        } catch (NotFoundException notFoundException) {
            log.error(notFoundException.message)
            response.status = HttpServletResponse.SC_NOT_FOUND
            render ""
        } catch (Exception ee) {
            response.status = HttpServletResponse.SC_INTERNAL_SERVER_ERROR
            log.error(ee.message)
            ee.printStackTrace()
        }
    }
/**
 * Update the status of the given element
 */
    def updateElement() {
        response.status = HttpServletResponse.SC_NOT_IMPLEMENTED
        render ""
    }

}
