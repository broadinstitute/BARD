package dataexport.cap.cap

import dataexport.cap.CapExportService
import org.codehaus.groovy.grails.commons.GrailsApplication

/**
 * Please note that the DataExportFilters is applied to all incoming request.
 * ALL incoming request need to have a custom http header named 'APIKEY' and the correct MD5 hash value
 * In addition, the request's remote IP address has to be whitelisted in the commons-config file.
 */

class CapRestController {
    CapExportService capExportService
    GrailsApplication grailsApplication
    static allowedMethods = [
            project: "GET",
            updateProject: "PATCH",
            assay: "GET",
            updateAssay: "PATCH",
            projects: "GET",
            cap: "GET"
    ]

    def index() {
        return unsupported()
    }

    def projects() {
//        def mimeType = grailsApplication.config.bard.data.export.cap.projects.xml
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
//        capExportService.generateNewProjects(xml)
        throw new RuntimeException("Not Yet Implemented")
    }

    def assayDocument() {
//        def mimeType = grailsApplication.config.bard.data.export.cap.projects.xml
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
//        capExportService.generateNewProjects(xml)
        //TODO NYI
        throw new RuntimeException("Not yet Implemented")
        //return null
    }

    def cap() {
//        def mimeType = grailsApplication.config.bard.data.export.cap.xml
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
//        capExportService.generateCap(xml)
        throw new RuntimeException("Not Yet Implemented")
    }

    def assay() {
//        def mimeType = grailsApplication.config.bard.data.export.cap.assay.xml
//        response.contentType = mimeType
//        //do validations
//        if (mimeType == request.getHeader(HttpHeaders.ACCEPT) && params.id) {
//            final BigDecimal assayId = params.id as BigDecimal
//
//            final def writer = response.writer
//            final MarkupBuilder xml = new MarkupBuilder(writer)
//            capExportService.generateAssay(xml, assayId)
//            return
//        }
//        response.status = HttpServletResponse.SC_BAD_REQUEST
//        render ""
        throw new RuntimeException("Not Yet Implemented")
    }

    def project() {
//        def mimeType = grailsApplication.config.bard.data.export.cap.project.xml
//        response.contentType = mimeType
//        //do validations
//        if (mimeType == request.getHeader(HttpHeaders.ACCEPT) && params.id) {
//            final BigDecimal projectId = params.id as BigDecimal
//            final def writer = response.writer
//            final MarkupBuilder xml = new MarkupBuilder(writer)
//            capExportService.generateProject(xml, projectId)
//            return
//        }
//        response.status = HttpServletResponse.SC_BAD_REQUEST
//        render ""
        throw new RuntimeException("Not Yet Implemented")
    }

    def updateAssay() {
        throw new RuntimeException("Not Yet Implemented")
    }

    def updateProject() {

    }
}
