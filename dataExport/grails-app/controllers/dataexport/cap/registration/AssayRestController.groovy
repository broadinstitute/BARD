package dataexport.cap.registration

import dataexport.registration.AssayExportService
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

class AssayRestController {
    AssayExportService assayExportService
    GrailsApplication grailsApplication
    static allowedMethods = [
            assays: "GET",
            assay: "GET",
            updateAssay: "PATCH",
            assayDocument: "GET"
    ]

    /**
     * Fetch assays with readyForExtraction of ready
     * @return
     */
    def assays() {
        try {
            final String mimeType = grailsApplication.config.bard.data.export.assays.xml
            response.contentType = mimeType
            //do validations
            //mime types must match the expected type
            if (mimeType == request.getHeader(HttpHeaders.ACCEPT)) {
                final Writer writer = response.writer
                //if we run into issues we should consider using StAX or StreamingMarkupBuilder
                final MarkupBuilder markupBuilder = new MarkupBuilder(writer)
                this.assayExportService.generateAssays(markupBuilder)
                return
            }
            response.status = HttpServletResponse.SC_BAD_REQUEST
            render ""
        } catch (Exception ee) {
            response.status = HttpServletResponse.SC_INTERNAL_SERVER_ERROR
            log.error(ee.message,ee)
            //clean out groovy grails boiler plate stuff
            //e message body
            ee.printStackTrace()
        }
    }
    /**
     * Get an assay with the given id
     * @return
     */
    def assayDocument() {
        try {
            final String mimeType = grailsApplication.config.bard.data.export.assay.doc.xml
            response.contentType = mimeType
            //do validations
            if (mimeType == request.getHeader(HttpHeaders.ACCEPT) && params.id) {
                final Writer writer = response.writer
                final MarkupBuilder markupBuilder = new MarkupBuilder(writer)
                this.assayExportService.generateAssayDocument(markupBuilder, new Long(params.id))
                return
            }
            response.status = HttpServletResponse.SC_BAD_REQUEST
            render ""
        }
        catch (NotFoundException notFoundException) {
            log.error(notFoundException.message)
            response.status = HttpServletResponse.SC_NOT_FOUND
            render ""
        }
        catch (Exception ee) {
            response.status = HttpServletResponse.SC_INTERNAL_SERVER_ERROR
            log.error(ee.message)
            ee.printStackTrace()
        }
    }
    /**
     * Get an assay with the given id
     * @return
     */
    def assay() {
        try {
            final String mimeType = grailsApplication.config.bard.data.export.assay.xml
            response.contentType = mimeType
            //do validations
            if (mimeType == request.getHeader(HttpHeaders.ACCEPT) && params.id) {
                final Writer writer = response.writer
                final MarkupBuilder markupBuilder = new MarkupBuilder(writer)
                this.assayExportService.generateAssay(markupBuilder, new Long(params.id))
                return
            }
            response.status = HttpServletResponse.SC_BAD_REQUEST
            render ""
        } catch (NotFoundException notFoundException) {
            log.error(notFoundException.message)
            response.status = HttpServletResponse.SC_NOT_FOUND
            render ""
        }
        catch (Exception ee) {
            response.status = HttpServletResponse.SC_INTERNAL_SERVER_ERROR
            log.error(ee.message)
            ee.printStackTrace()
        }
    }

/**
 * Update the status of the given element
 */
    def updateAssay() {
        throw new RuntimeException("Not Yet Implemented")
    }

}
