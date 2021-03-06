/* Copyright (c) 2014, The Broad Institute
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
    * Redistributions of source code must retain the above copyright
      notice, this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright
      notice, this list of conditions and the following disclaimer in the
      documentation and/or other materials provided with the distribution.
    * Neither the name of The Broad Institute nor the
      names of its contributors may be used to endorse or promote products
      derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL The Broad Institute BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package dataexport.cap.registration

import bard.db.enums.EnumNotFoundException
import bard.db.enums.ReadyForExtraction
import dataexport.registration.AssayExportService
import dataexport.registration.BardHttpResponse
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
@Mixin(UpdateStatusHelper)
class AssayRestController {
    AssayExportService assayExportService
    GrailsApplication grailsApplication
    static allowedMethods = [
            assays: 'GET',
            assay: 'GET',
            updateAssay: 'PUT',
            assayDocument: 'GET'
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
                render(text: markupWriter.toString(), contentType: mimeType, encoding: responseContentTypeEncoding)

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
                render(text: markupWriter.toString(), contentType: mimeType, encoding: responseContentTypeEncoding)
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
                render(text: markupWriter.toString(), contentType: mimeType, encoding: responseContentTypeEncoding)
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
 * Update the status of the given assay
 */
    def updateAssay(final Long id) {
        updateDomainObject(this.assayExportService, id)
    }

}
/**
 * We would use this helper class as Mixin for
 * the RestController
 */
class UpdateStatusHelper {
    public BardHttpResponse updateDomainObject(final def service, final Long id) {

        try {
            final String requestBody = request?.reader?.text?.trim()
            final ReadyForExtraction readyForExtraction = ReadyForExtraction.byId(requestBody)
            if (!ReadyForExtraction.isAllowed(readyForExtraction)) {
                response.status = HttpServletResponse.SC_BAD_REQUEST
            } else {
                final String ifMatchHeader = request.getHeader(HttpHeaders.IF_MATCH)
                if (ifMatchHeader && id) {
                    final BardHttpResponse bardHttpResponse = service.update(new Long(id), new Long(ifMatchHeader),
                            ReadyForExtraction.byId(requestBody))
                    response.status = bardHttpResponse.httpResponseCode
                    response.addHeader(HttpHeaders.ETAG, bardHttpResponse.ETag.toString())
                    render ""
                    return
                }
            }
            response.status = HttpServletResponse.SC_BAD_REQUEST

        } catch (EnumNotFoundException enumNotFoundException) {
            response.status = HttpServletResponse.SC_BAD_REQUEST
        }
        catch (NotFoundException notFoundException) {
            log.error(notFoundException.message)
            response.status = HttpServletResponse.SC_NOT_FOUND
        } catch (Exception ee) {
            response.status = HttpServletResponse.SC_INTERNAL_SERVER_ERROR
            log.error(ee.message)
            ee.printStackTrace()
        }
        render ""
        return
    }

}

