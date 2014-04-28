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

package dataexport.experiment

import bard.db.enums.ReadyForExtraction
import bard.db.experiment.ArchivePathService
import bard.db.experiment.Experiment
import dataexport.cap.experiment.ExperimentRestController
import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import org.codehaus.groovy.grails.web.servlet.HttpHeaders
import spock.lang.IgnoreRest
import spock.lang.Specification
import spock.lang.Unroll

import javax.servlet.http.HttpServletResponse
import dataexport.cap.registration.UpdateStatusHelper
import dataexport.registration.BardHttpResponse

/**
 *
 */
@Unroll
@TestFor(ExperimentRestController)
@Mock([Experiment])
class ExperimentRestControllerUnitSpec extends Specification {

    void setup() {
        controller.metaClass.mixin(UpdateStatusHelper)
        controller.experimentExportService = Mock(ExperimentExportService.class)
    }

    void tearDown() {
        // Tear down logic here
    }
    /**
     *
     */
    void "test results #label"() {
        given:
        Integer experimentId = 2
        controller.archivePathService = Mock(ArchivePathService)
        InputStream inputStream = new ByteArrayInputStream("".getBytes())

        when: "We send an HTTP GET Request to the results action"
        request.method = 'GET'
        controller.request.addHeader(HttpHeaders.ACCEPT, mimeType)
        params.start = "0"
        controller.archivePathService.getEtlExport(_) >> inputStream
        controller.results(experimentId)

        then: "We expect a response with the given status code"
        expectedResults == response.status

        where:
        label                                  | mimeType                                       | expectedResults
        "Status Code 400, incorrect mime type" | "application/vnd.bard.cap+xml;type=dictionary" | HttpServletResponse.SC_BAD_REQUEST
        "Status Code 200"                      | "application/vnd.bard.cap+json;type=results"   | HttpServletResponse.SC_OK
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
    void "test experiments PUT #label"() {
        given:
        Integer experimentId = 2
        when: "We send an HTTP GET Request to the experiments action"
        request.method = 'PUT'
        request.content="Complete"
        request.addHeader(HttpHeaders.IF_MATCH,"0")
        params.start = "0"
        controller.updateExperiment(experimentId)
        then: "We expect a response with the given status code"
        controller.experimentExportService.update(_, _, _) >> {bardHttpResponse}
        expectedResults == response.status
        where:
        label             | bardHttpResponse                            | expectedResults
        "Status Code 500" | new BardHttpResponse(httpResponseCode: 400) | HttpServletResponse.SC_BAD_REQUEST
        "Status Code 200" | new BardHttpResponse(httpResponseCode: 200) | HttpServletResponse.SC_OK
    }

    void "test update ready for extract with value #label"() {
        given:
        Integer experimentId = 2
        when: "We send an HTTP GET Request to the experiments action"
        request.method = 'PUT'
        request.content= label
        request.addHeader(HttpHeaders.IF_MATCH,"0")
        params.start = "0"
        controller.updateExperiment(experimentId)
        then: "We expect a response with the given status code"
        controller.experimentExportService.update(_, _, newValue) >> { new BardHttpResponse(httpResponseCode: 200) }
        HttpServletResponse.SC_OK == response.status

        where:
        label           | newValue
        "Complete"      | ReadyForExtraction.COMPLETE
        "Failed"        | ReadyForExtraction.FAILED
        "Started"       | ReadyForExtraction.STARTED
    }

    /**
     *
     */
    void "test experiment fail #label #id"() {

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
