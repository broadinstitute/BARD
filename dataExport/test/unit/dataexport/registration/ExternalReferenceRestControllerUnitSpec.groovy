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

package dataexport.registration

import bard.db.registration.ExternalReference
import bard.db.registration.ExternalSystem
import dataexport.cap.registration.ExternalReferenceRestController
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
@TestFor(ExternalReferenceRestController)
@Mock([ExternalReference, ExternalSystem])
class ExternalReferenceRestControllerUnitSpec extends Specification {

    void setup() {
        controller.externalReferenceExportService = Mock(ExternalReferenceExportService.class)
    }

    void tearDown() {
        // Tear down logic here
    }

    /**
     *
     */
    void "test externalReferences #label"() {
        when: "We send an HTTP GET Request to the externalReferences action"
        request.method = 'GET'
        controller.request.addHeader(HttpHeaders.ACCEPT, mimeType)

        controller.externalReferences()
        then: "We expect a response with the given status code"
        expectedResults == response.status
        where:
        label                              | mimeType                                               | expectedResults
        "Statuc Code 400, wrong mime type" | "application/vnd.bard.cap+xml;type=dictionary"         | HttpServletResponse.SC_BAD_REQUEST
        "Status Code 200"                  | "application/vnd.bard.cap+xml;type=externalReferences" | HttpServletResponse.SC_OK
    }

    /**
     *
     */
    void "test externalSystems #label"() {
        when: "We send an HTTP GET Request to the externalSystems action"
        request.method = 'GET'
        controller.request.addHeader(HttpHeaders.ACCEPT, mimeType)

        controller.externalSystems()
        then: "We expect a response with the given status code"
        expectedResults == response.status
        where:
        label                                  | mimeType                                            | expectedResults
        "Status Code 400, incorrect mime type" | "application/vnd.bard.cap+xml;type=dictionary"      | HttpServletResponse.SC_BAD_REQUEST
        "Status Code 200"                      | "application/vnd.bard.cap+xml;type=externalSystems" | HttpServletResponse.SC_OK
    }
    /**
     *
     */
    void "test  external reference #label #id"() {
        when: "We send an HTTP GET request for a specific externalReference"
        request.method = 'GET'
        controller.request.addHeader(HttpHeaders.ACCEPT, mimeType)
        controller.externalReference(id)
        then:
        expectedResults == response.status
        where:
        label                                  | id   | mimeType                                              | expectedResults
        "Status Code 400, incorrect mime type" | 2    | "bogus.mime.type"                                     | HttpServletResponse.SC_BAD_REQUEST
        "Status Code 400, null entity Id"      | null | "application/vnd.bard.cap+xml;type=externalReference" | HttpServletResponse.SC_BAD_REQUEST
        "Status Code 200"                      | 5    | "application/vnd.bard.cap+xml;type=externalReference" | HttpServletResponse.SC_OK
    }

    /**
     *
     */
    void "test  external system fail #label #id"() {
        when: "We send an HTTP GET request for a specific externalSystem"
        request.method = 'GET'
        controller.request.addHeader(HttpHeaders.ACCEPT, mimeType)
        controller.externalSystem(id)
        then:
        expectedResults == response.status
        where:
        label                                  | id   | mimeType                                           | expectedResults
        "Status Code 400, incorrect mime type" | 2    | "bogus.mime.type"                                  | HttpServletResponse.SC_BAD_REQUEST
        "Status Code 400, null entity Id"      | null | "application/vnd.bard.cap+xml;type=externalSystem" | HttpServletResponse.SC_BAD_REQUEST
        "Status Code 200"                      | 5    | "application/vnd.bard.cap+xml;type=externalSystem" | HttpServletResponse.SC_OK
    }
}
