package dataexport.cap.registration

import dataexport.registration.ExternalReferenceExportService
import exceptions.NotFoundException
import groovy.xml.MarkupBuilder
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.codehaus.groovy.grails.web.servlet.HttpHeaders

import javax.servlet.http.HttpServletResponse

/**
 * Please note that the DataExportFilters is applied to all incoming request.
 * ALL incoming request need to have a custom http header named 'APIKEY' and the correct MD5 hash value
 */

class ExternalReferenceRestController {
    ExternalReferenceExportService externalReferenceExportService
    GrailsApplication grailsApplication
    static allowedMethods = [
            externalReferences: 'GET',
            externalReference: 'GET',
            externalSystems: 'GET',
            externalSystem: 'GET'
    ]

    static final String responseContentTypeEncoding = "UTF-8"

    def index() {
        return response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED)
    }
    /**
     * Fetch external systems
     * @return
     */
    def externalSystems() {
        try {
            final String mimeType = grailsApplication.config.bard.data.export.externalsystems.xml
            //mime types must match the expected type
            if (mimeType == request.getHeader(HttpHeaders.ACCEPT)) {
                final StringWriter markupWriter = new StringWriter()
                final MarkupBuilder markupBuilder = new MarkupBuilder(markupWriter)
                this.externalReferenceExportService.generateExternalSystems(markupBuilder)
                render(text: markupWriter.toString(), contentType: mimeType, encoding: responseContentTypeEncoding)

                return
            }
            response.status = HttpServletResponse.SC_BAD_REQUEST
            render ""
        } catch (Exception ee) {
            response.status = HttpServletResponse.SC_INTERNAL_SERVER_ERROR
            log.error(ee)
        }
    }
    /**
     * Fetch external references
     * @return
     */
    def externalReferences() {
        try {
            final String mimeType = grailsApplication.config.bard.data.export.externalreferences.xml
            //mime types must match the expected type
            if (mimeType == request.getHeader(HttpHeaders.ACCEPT)) {
                final StringWriter markupWriter = new StringWriter()
                final MarkupBuilder markupBuilder = new MarkupBuilder(markupWriter)
                this.externalReferenceExportService.generateExternalReferences(markupBuilder)
                render(text: markupWriter.toString(), contentType: mimeType, encoding: responseContentTypeEncoding)

                return
            }
            response.status = HttpServletResponse.SC_BAD_REQUEST
            render ""
        } catch (Exception ee) {
            response.status = HttpServletResponse.SC_INTERNAL_SERVER_ERROR
            log.error(ee)
        }
    }
     /**
     * Get an system with the given id
     * @return
     */
    def externalReference(Integer id) {
        try {
            final String mimeType = grailsApplication.config.bard.data.export.externalreference.xml
            //do validations
            if (mimeType == request.getHeader(HttpHeaders.ACCEPT) && id) {
                final StringWriter markupWriter = new StringWriter()
                final MarkupBuilder markupBuilder = new MarkupBuilder(markupWriter)
                final Long eTag = this.externalReferenceExportService.generateExternalReference(markupBuilder, new Long(id))
                response.addHeader(HttpHeaders.ETAG, eTag.toString())
                render(text: markupWriter.toString(), contentType: mimeType, encoding: responseContentTypeEncoding)
                return
            }
            response.status = HttpServletResponse.SC_BAD_REQUEST
            render ""
        }
        catch (NotFoundException notFoundException) {
            log.error(notFoundException)
            response.status = HttpServletResponse.SC_NOT_FOUND
            render ""
        }
        catch (Exception ee) {
            response.status = HttpServletResponse.SC_INTERNAL_SERVER_ERROR
            log.error(ee)
        }
    }

    /**
     * Get an external system with the given id
     * @return
     */
    def externalSystem(Integer id) {
        try {
            final String mimeType = grailsApplication.config.bard.data.export.externalsystem.xml
            //do validations
            if (mimeType == request.getHeader(HttpHeaders.ACCEPT) && id) {
                final StringWriter markupWriter = new StringWriter()
                final MarkupBuilder markupBuilder = new MarkupBuilder(markupWriter)
                final Long eTag = this.externalReferenceExportService.generateExternalSystem(markupBuilder, new Long(id))
                response.addHeader(HttpHeaders.ETAG, eTag.toString())
                render(text: markupWriter.toString(), contentType: mimeType, encoding: responseContentTypeEncoding)
                return
            }
            response.status = HttpServletResponse.SC_BAD_REQUEST
            render ""
        }
        catch (NotFoundException notFoundException) {
            log.error(notFoundException)
            response.status = HttpServletResponse.SC_NOT_FOUND
            render ""
        }
        catch (Exception ee) {
            response.status = HttpServletResponse.SC_INTERNAL_SERVER_ERROR
            log.error(ee)
        }
    }
}

