package dataexport.experiment

import bard.db.experiment.Project
import dataexport.cap.experiment.ProjectRestController
import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import org.codehaus.groovy.grails.web.servlet.HttpHeaders
import spock.lang.Specification
import spock.lang.Unroll

import javax.servlet.http.HttpServletResponse

/**
 *
 */
@Unroll
@TestFor(ProjectRestController)
@Mock([Project])
class ProjectRestControllerUnitSpec extends Specification {

    void setup() {
        controller.projectExportService = Mock(ProjectExportService.class)
    }

    void tearDown() {
        // Tear down logic here
    }

    /**
     *
     */
    void "test projects #label"() {
        when: "We send an HTTP GET Request to the projects action"
        request.method = 'GET'
        controller.request.addHeader(HttpHeaders.ACCEPT, mimeType)

        controller.projects()
        then: "We expect a response with the given status code"
        expectedResults == response.status
        where:
        label               | mimeType                                       | expectedResults
        "Expects 400 Error" | "application/vnd.bard.cap+xml;type=dictionary" | HttpServletResponse.SC_BAD_REQUEST
        "Expects 200 OK"    | "application/vnd.bard.cap+xml;type=projects"   | HttpServletResponse.SC_OK
    }
    /**
     *
     */
    void "test  project fail #label #id"() {
        when: "We send an HTTP GET request for a specific project"
        request.method = 'GET'
        controller.request.addHeader(HttpHeaders.ACCEPT, mimeType)
        controller.params.id = id

        controller.project()
        then:
        expectedResults == response.status
        where:
        label                     | id   | mimeType                                    | expectedResults
        "Expects 400 Bad request" | "2"  | "bogus.mime.type"                           | HttpServletResponse.SC_BAD_REQUEST
        "Expects 400 Bad request" | null | "application/vnd.bard.cap+xml;type=project" | HttpServletResponse.SC_BAD_REQUEST
    }
    /**
     *
     */
    void "test  project #label"() {
        when: "We send an HTTP GET request for a specific project"
        request.method = 'GET'
        controller.request.addHeader(HttpHeaders.ACCEPT, mimeType)
        controller.params.id = id

        controller.project()
        then:
        expectedResults == response.status
        where:
        label                        | id  | mimeType                                    | expectedResults
        "Expects 200 OK For Project" | "5" | "application/vnd.bard.cap+xml;type=project" | HttpServletResponse.SC_OK
    }
}
