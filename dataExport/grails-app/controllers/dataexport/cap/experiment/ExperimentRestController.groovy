package dataexport.cap.experiment

import dataexport.experiment.ExperimentExportService
import dataexport.experiment.ResultExportService
import exceptions.NotFoundException
import groovy.xml.MarkupBuilder
import groovy.xml.StaxBuilder
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.codehaus.groovy.grails.web.servlet.HttpHeaders

import javax.servlet.http.HttpServletResponse
import javax.xml.stream.XMLOutputFactory

/**
 * Please note that the DataExportFilters is applied to all incoming request.
 * ALL incoming request need to have a custom http header named 'APIKEY' and the correct MD5 hash value
 * In addition, the request's remote IP address has to be whitelisted in the commons-config file.
 */

class ExperimentRestController {
    ExperimentExportService experimentExportService
    ResultExportService resultExportService
    GrailsApplication grailsApplication
    static allowedMethods = [
            experiment: "GET",
            updateExperiment: "PATCH",
            result: "GET",
            updateResult: "PATCH",
            experiments: "GET",
            results: "GET"
    ]

    def index() {
        return response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED)
    }

    def experiments() {

        try {
            final String mimeType = grailsApplication.config.bard.data.export.experiments.xml
            response.contentType = mimeType
            //mime types must match the expected type
            if (mimeType == request.getHeader(HttpHeaders.ACCEPT)) {
                final int offset = params.offset ? new Integer(params.offset) : 0

                final XMLOutputFactory factory = XMLOutputFactory.newInstance()
                final StringWriter markupWriter = new StringWriter()
                final StaxBuilder staxBuilder = new StaxBuilder(factory.createXMLStreamWriter(markupWriter))

                final boolean hasMoreExperiments = this.experimentExportService.generateExperiments(staxBuilder, offset)
                if (hasMoreExperiments) {
                    //we set the header to 206
                    response.status = HttpServletResponse.SC_PARTIAL_CONTENT
                } else {
                    response.status = HttpServletResponse.SC_OK
                }
                response.contentLength = markupWriter.toString().length()
                response.contentType = mimeType
                render markupWriter.toString()
                //now set the writer
                return
            }
            response.status = HttpServletResponse.SC_BAD_REQUEST
            render ""
        } catch (Exception ee) {
            response.status = HttpServletResponse.SC_INTERNAL_SERVER_ERROR
            log.error(ee)
            render ""
        }
    }
    /**
     * Render XML for each result object of the given Experiment
     * If the number of experiments > than the max per page
     * then use paging by setting the SC_PARTIAL_CONTENT header in the response
     * @param id - The Id of this experiment
     * @return
     */
    def results(Integer id) {
        try {
            final String mimeType = grailsApplication.config.bard.data.export.results.xml
            //mime types must match the expected type
            if (mimeType == request.getHeader(HttpHeaders.ACCEPT) && id) {

                final int offset = params.offset ? new Integer(params.offset) : 0

                //we use the stax builder here
                final XMLOutputFactory factory = XMLOutputFactory.newInstance()
                final StringWriter markupWriter = new StringWriter()
                final StaxBuilder staxBuilder = new StaxBuilder(factory.createXMLStreamWriter(markupWriter))
                final boolean hasMoreResults = this.resultExportService.generateResults(staxBuilder, id, offset)
                if (hasMoreResults) {
                    //we set the header to 206
                    response.status = HttpServletResponse.SC_PARTIAL_CONTENT
                } else {
                    response.status = HttpServletResponse.SC_OK
                }
                response.contentLength = markupWriter.toString().length()
                response.contentType = mimeType
                render markupWriter.toString()
                //now set the writer
                return
            }
            response.status = HttpServletResponse.SC_BAD_REQUEST
            render ""
        } catch (Exception ee) {
            response.status = HttpServletResponse.SC_INTERNAL_SERVER_ERROR
            log.error(ee)
            render ""
        }
    }

    def experiment(Integer id) {
        try {
            final String mimeType = grailsApplication.config.bard.data.export.experiment.xml
            response.contentType = mimeType
            //do validations
            if (mimeType == request.getHeader(HttpHeaders.ACCEPT) && id) {
                final StringWriter markupWriter = new StringWriter()
                final MarkupBuilder markupBuilder = new MarkupBuilder(markupWriter)
                final Long eTag = this.experimentExportService.generateExperiment(markupBuilder, id)
                response.addHeader(HttpHeaders.ETAG, eTag.toString())
                response.contentLength = markupWriter.toString().length()
                render markupWriter.toString()
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
            render ""
        }

    }

    def result(Integer id) {
        try {
            final String mimeType = grailsApplication.config.bard.data.export.result.xml
            response.contentType = mimeType
            //do validations
            if (mimeType == request.getHeader(HttpHeaders.ACCEPT) && id) {
                final StringWriter markupWriter = new StringWriter()
                final MarkupBuilder markupBuilder = new MarkupBuilder(markupWriter)
                final Long eTag = this.resultExportService.generateResult(markupBuilder, id)
                response.addHeader(HttpHeaders.ETAG, eTag.toString())
                response.contentLength = markupWriter.toString().length()
                render markupWriter.toString()
                return
            }
            response.status = HttpServletResponse.SC_BAD_REQUEST
            render ""
        } catch (NotFoundException notFoundException) {
            log.error(notFoundException)
            response.status = HttpServletResponse.SC_NOT_FOUND
            render ""
        }
        catch (Exception ee) {
            response.status = HttpServletResponse.SC_INTERNAL_SERVER_ERROR
            log.error(ee.message)
            render ""
        }
    }

    def updateResult() {
        response.status = HttpServletResponse.SC_NOT_IMPLEMENTED
        render ""
    }

    def updateExperiment() {
        response.status = HttpServletResponse.SC_NOT_IMPLEMENTED
        render ""
    }
}
