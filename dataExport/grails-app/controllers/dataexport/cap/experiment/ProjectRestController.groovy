package dataexport.cap.experiment

import dataexport.experiment.ProjectExportService
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

class ProjectRestController {
    ProjectExportService projectExportService
    GrailsApplication grailsApplication
    static allowedMethods = [
            project: "GET",
            updateProject: "PATCH",
            projects: "GET"
    ]

    def projects() {
        try {
            final String mimeType = grailsApplication.config.bard.data.export.projects.xml
            response.contentType = mimeType
            //do validations
            //mime types must match the expected type
            if (mimeType == request.getHeader(HttpHeaders.ACCEPT)) {
                final Writer writer = response.writer
                final MarkupBuilder markupBuilder = new MarkupBuilder(writer)
                this.projectExportService.generateProjects(markupBuilder)
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

    def project() {
        try {
            final String mimeType = grailsApplication.config.bard.data.export.project.xml
            response.contentType = mimeType
            //do validations
            if (mimeType == request.getHeader(HttpHeaders.ACCEPT) && params.id) {
                final Writer writer = response.writer
                final MarkupBuilder markupBuilder = new MarkupBuilder(writer)
                this.projectExportService.generateProject(markupBuilder, new Long(params.id))
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
    def updateProject() {
        throw new RuntimeException("Not Yet Implemented")
    }
}
