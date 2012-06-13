package barddataexport.cap.experiment

import barddataexport.experiment.ExperimentExportService
import barddataexport.experiment.ResultExportService
import groovy.xml.MarkupBuilder
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.codehaus.groovy.grails.web.servlet.HttpHeaders

import javax.servlet.http.HttpServletResponse

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
        throw new RuntimeException("Not Yet Implemented")
//        def mimeType = grailsApplication.config.bard.data.export.data.experiments.xml
//        response.contentType = mimeType
//        //do validations
//        if (mimeType != request.getHeader(HttpHeaders.ACCEPT)) {
//            response.status = HttpServletResponse.SC_BAD_REQUEST
//
//            render ""
//            return
//        }
//        final def writer = response.writer
//        final MarkupBuilder xml = new MarkupBuilder(writer)
//        experimentExportService.generateExperiments(xml)
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
//        def mimeType = grailsApplication.config.bard.data.export.data.experiment.xml
//        response.contentType = mimeType
//        //do validations
//        if (mimeType == request.getHeader(HttpHeaders.ACCEPT) && params.id) {
//            final BigDecimal experimentId = params.id as BigDecimal
//
//            final def writer = response.writer
//            final MarkupBuilder xml = new MarkupBuilder(writer)
//            experimentExportService.generateExperiment(xml, experimentId)
//            return
//        }
//        response.status = HttpServletResponse.SC_BAD_REQUEST
//        render ""
        throw new RuntimeException("Not Yet Implemented")
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
