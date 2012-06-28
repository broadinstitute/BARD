package dataexport.cap.experiment

import dataexport.experiment.ExperimentExportService
import dataexport.experiment.ResultExportService
import groovy.xml.MarkupBuilder
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.codehaus.groovy.grails.web.servlet.HttpHeaders

import javax.servlet.http.HttpServletResponse
import exceptions.NotFoundException

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
        return unsupported()
    }

    def experiments() {

        try {
            final String mimeType = grailsApplication.config.bard.data.export.experiments.xml
            response.contentType = mimeType
            //mime types must match the expected type
            if (mimeType == request.getHeader(HttpHeaders.ACCEPT)) {
                final Writer writer = response.writer
                final MarkupBuilder markupBuilder = new MarkupBuilder(writer)
                int start = 0
                if (params.start){
                    start = new Integer(params.start)
                }
                this.experimentExportService.generateExperiments(markupBuilder,start)
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

    def results() {
        throw new RuntimeException("Not Yet Implemented")
//        //TODO: Set response header for partial requests
//        def mimeType = grailsApplication.config.bard.data.export.data.results.xml
//        response.contentType = mimeType
//        //do validations
//        if (mimeType == request.getHeader(HttpHeaders.ACCEPT) && params.id) {
//            final def writer = response.writer
//            final MarkupBuilder xml = new MarkupBuilder(writer)
//            final BigDecimal experimentId = params.id as BigDecimal
//            resultExportService.generateResults(xml, experimentId)
//            return
//        }
//        response.status = HttpServletResponse.SC_BAD_REQUEST
//        render ""
    }

    def experiment() {
        try {
            final String mimeType = grailsApplication.config.bard.data.export.experiment.xml
            response.contentType = mimeType
            //do validations
            if (mimeType == request.getHeader(HttpHeaders.ACCEPT) && params.id) {
                final Writer writer = response.writer
                final MarkupBuilder markupBuilder = new MarkupBuilder(writer)
                this.experimentExportService.generateExperiment(markupBuilder, new Long(params.id))
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

    def result() {
//        def mimeType = grailsApplication.config.bard.data.export.data.result.xml
//        response.contentType = mimeType
//        //do validations
//        if (mimeType == request.getHeader(HttpHeaders.ACCEPT) && params.id) {
//            final BigDecimal resultId = params.id as BigDecimal
//            final def writer = response.writer
//            final MarkupBuilder xml = new MarkupBuilder(writer)
//            resultExportService.generateResult(xml, resultId)
//            return
//
//        }
//        response.status = HttpServletResponse.SC_BAD_REQUEST
//
//        render ""
        throw new RuntimeException("Not Yet Implemented")
    }

    def updateResult() {
        throw new RuntimeException("Not Yet Implemented")
    }

    def updateExperiment() {

    }
}
