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
