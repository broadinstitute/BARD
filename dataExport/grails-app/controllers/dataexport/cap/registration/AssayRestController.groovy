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

    static final String responseContentTypeEncoding = "UTF-8"

    def index() {
        return response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED)
    }
    /**
     * Fetch assays with readyForExtraction of ready
     * @return
     */
    def assays() {
        try {
            final String mimeType = grailsApplication.config.bard.data.export.assays.xml
            //do validations
            //mime types must match the expected type
            if (mimeType == request.getHeader(HttpHeaders.ACCEPT)) {
                final StringWriter markupWriter = new StringWriter()
                final MarkupBuilder markupBuilder = new MarkupBuilder(markupWriter)
                this.assayExportService.generateAssays(markupBuilder)
                response.contentLength = markupWriter.toString().length()
                render (text: markupWriter.toString(), contentType: mimeType, encoding: responseContentTypeEncoding)

                return
            }
            response.status = HttpServletResponse.SC_BAD_REQUEST
            render ""
        } catch (Exception ee) {
            response.status = HttpServletResponse.SC_INTERNAL_SERVER_ERROR
            log.error(ee.message, ee)
            //clean out groovy grails boiler plate stuff
            //e message body
            ee.printStackTrace()
        }
    }
    /**
     * Get an assay with the given id
     * @return
     */
    def assayDocument(Integer id) {
        try {
            final String mimeType = grailsApplication.config.bard.data.export.assay.doc.xml
            //do validations
            if (mimeType == request.getHeader(HttpHeaders.ACCEPT) && id) {
                final StringWriter markupWriter = new StringWriter()
                final MarkupBuilder markupBuilder = new MarkupBuilder(markupWriter)
                final Long eTag = this.assayExportService.generateAssayDocument(markupBuilder, new Long(id))
                response.addHeader(HttpHeaders.ETAG, eTag.toString())
                response.contentLength = markupWriter.toString().length()
                render (text: markupWriter.toString(), contentType: mimeType, encoding: responseContentTypeEncoding)
                //add content length
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
    def assay(Integer id) {
        try {
            final String mimeType = grailsApplication.config.bard.data.export.assay.xml
            //do validations
            if (mimeType == request.getHeader(HttpHeaders.ACCEPT) && id) {
                final StringWriter markupWriter = new StringWriter()
                final MarkupBuilder markupBuilder = new MarkupBuilder(markupWriter)
                Long eTag = this.assayExportService.generateAssay(markupBuilder, new Long(id))
                response.addHeader(HttpHeaders.ETAG, eTag.toString())
                response.contentLength = markupWriter.toString().length()
                render (text: markupWriter.toString(), contentType: mimeType, encoding: responseContentTypeEncoding)
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
        response.status = HttpServletResponse.SC_NOT_IMPLEMENTED
    }

}
