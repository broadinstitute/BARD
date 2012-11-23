package dataexport.experiment

import bard.db.experiment.Experiment
import dataexport.cap.experiment.ExperimentRestController
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
@TestFor(ExperimentRestController)
@Mock([Experiment])
class ExperimentRestControllerUnitSpec extends Specification {

    void setup() {
        controller.experimentExportService = Mock(ExperimentExportService.class)
    }

    void tearDown() {
        // Tear down logic here
    }

    /**
     *
     */
    void "test experiments #label"() {
        when: "We send an HTTP GET Request to the experiments action"
        request.method = 'GET'
        controller.request.addHeader(HttpHeaders.ACCEPT, mimeType)
        params.start = "0"
        controller.experiments()
        then: "We expect a response with the given status code"
        expectedResults == response.status
        where:
        label                                  | mimeType                                        | expectedResults
        "Status Code 400, incorrect mime type" | "application/vnd.bard.cap+xml;type=dictionary"  | HttpServletResponse.SC_BAD_REQUEST
        "Status Code 200"                      | "application/vnd.bard.cap+xml;type=experiments" | HttpServletResponse.SC_OK
    }
    /**
     *
     */
    void "test  experiment fail #label #id"() {
        when: "We send an HTTP GET request for a specific experiment"
        request.method = 'GET'
        controller.request.addHeader(HttpHeaders.ACCEPT, mimeType)
        controller.experiment(id)
        then:
        expectedResults == response.status
        where:
        label                                  | id   | mimeType                                       | expectedResults
        "Status Code 400, incorrect mime type" | 2    | "bogus.mime.type"                              | HttpServletResponse.SC_BAD_REQUEST
        "Status Code 400, null id "            | null | "application/vnd.bard.cap+xml;type=experiment" | HttpServletResponse.SC_BAD_REQUEST
        "Status Code 200"                      | 5    | "application/vnd.bard.cap+xml;type=experiment" | HttpServletResponse.SC_OK
    }
}
