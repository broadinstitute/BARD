package dataexport.experiment

import bard.db.project.Project
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
    void "test  project #label #id"() {
        when: "We send an HTTP GET request for a specific project"
        request.method = 'GET'
        controller.request.addHeader(HttpHeaders.ACCEPT, mimeType)
        //controller.params.id = id

        controller.project(id)
        then:
        expectedResults == response.status
        where:
        label                                  | id   | mimeType                                    | expectedResults
        "Status Code 400, incorrect mime type" | 2    | "bogus.mime.type"                           | HttpServletResponse.SC_BAD_REQUEST
        "Status Code 400, null id"             | null | "application/vnd.bard.cap+xml;type=project" | HttpServletResponse.SC_BAD_REQUEST
        "Status Code 200"                      | 5    | "application/vnd.bard.cap+xml;type=project" | HttpServletResponse.SC_OK
    }


    /**
     *
     */
    void "test  projectDocument #label #id"() {
        when:
        request.method = 'GET'
        controller.request.addHeader(HttpHeaders.ACCEPT, mimeType)
        //controller.params.id = id

        controller.projectDocument(id)
        then:
        expectedResults == response.status
        where:
        label                                 | id   | mimeType                                       | expectedResults
        "Status Code 400,incorrect mime type" | 2    | "bogus.mime.type"                              | HttpServletResponse.SC_BAD_REQUEST
        "Status Code 400, null id"            | null | "application/vnd.bard.cap+xml;type=projectDoc" | HttpServletResponse.SC_BAD_REQUEST
        "Status Code 200"                     | 5    | "application/vnd.bard.cap+xml;type=projectDoc" | HttpServletResponse.SC_OK

    }
}
