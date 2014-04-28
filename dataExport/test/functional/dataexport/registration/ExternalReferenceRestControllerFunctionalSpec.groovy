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

//import org.springframework.context.ApplicationContext


import common.tests.XmlTestAssertions
import common.tests.XmlTestSamples
import grails.converters.XML
import grails.plugin.remotecontrol.RemoteControl
import groovyx.net.http.RESTClient
import org.custommonkey.xmlunit.XMLAssert
import spock.lang.Specification
import spock.lang.Unroll

import javax.servlet.http.HttpServletResponse

import static groovyx.net.http.Method.GET
import spock.lang.Shared

/**
 * Created with IntelliJ IDEA.
 * User: jasiedu
 * Date: 4/15/12
 * Time: 8:37 PM
 * To change this template use File | Settings | File Templates.
 */
@Unroll
class ExternalReferenceRestControllerFunctionalSpec extends Specification {
    RemoteControl remote = new RemoteControl()
    @Shared final String externalReferencesUrl = remote { ctx.grailsApplication.config.grails.serverURL } + "/api/externalReferences"
    @Shared final String externalSystemsUrl = remote { ctx.grailsApplication.config.grails.serverURL } + "/api/externalSystems"
    @Shared final String externalReferenceUrl = remote { ctx.grailsApplication.config.grails.serverURL } + "/api/externalReference"
    @Shared final String externalSystemUrl = remote { ctx.grailsApplication.config.grails.serverURL } + "/api/externalSystem"

    @Shared String externalReferencesAcceptContentType = remote { ctx.grailsApplication.config.bard.data.export.externalreferences.xml }
    @Shared String externalSystemsAcceptContentType = remote { ctx.grailsApplication.config.bard.data.export.externalsystems.xml }
    @Shared String externalReferenceAcceptContentType = remote { ctx.grailsApplication.config.bard.data.export.externalreference.xml }
    @Shared String externalSystemAcceptContentType = remote { ctx.grailsApplication.config.bard.data.export.externalsystem.xml }

    final String apiKeyHeader = remote { ctx.grailsApplication.config.dataexport.externalapplication.apiKey.header }
    final String apiKeyHashed = remote { ctx.grailsApplication.config.dataexport.externalapplication.apiKey.hashed }

    def 'test GET external references success'() {
        given: "there is a service end point to get the the list of external references"
        RESTClient http = new RESTClient(externalReferencesUrl)
        when: 'We send an HTTP GET request for the list of external references'
        def serverResponse = http.request(GET, XML) {
            headers.'Accept' = externalReferencesAcceptContentType
            headers."${apiKeyHeader}" = apiKeyHashed
        }
        then: 'We expect an XML representation of the external references'
        assert serverResponse.statusLine.statusCode == HttpServletResponse.SC_OK
        final String responseData = serverResponse.data.readLines().join()
        XmlTestAssertions.assertResults(XmlTestSamples.ASSAYS_FROM_SERVER, responseData)
    }

    def 'test GET, fail with wrong Accept Header #label'() {
        given:
        final RESTClient http = new RESTClient(url)

        when: 'We send an HTTP GET request with the wrong mime type'
        def serverResponse = http.request(GET, XML) {
            headers.'Accept' = "some bogus"
            headers."${apiKeyHeader}" = apiKeyHashed
            response.failure = { resp ->
                resp
            }
        }
        then: 'We expect a status code of 400 (Bad Request)'
        assert serverResponse.statusLine.statusCode == HttpServletResponse.SC_BAD_REQUEST
        where:
        label                                      | url
        "External References With wrong mime type" | externalReferencesUrl
        "External Systems With wrong mime type"    | externalSystemsUrl
        "External System With wrong mime type"     | externalSystemUrl
        "External Reference With wrong mime type"  | externalReferenceUrl
    }

    def 'test GET  fail with 404 #label'() {
        given:
        RESTClient http = new RESTClient(url)

        when: 'We send an HTTP GET request with the wrong mime type'
        def serverResponse = http.request(GET, XML) {
            headers.Accept = acceptType
            headers."${apiKeyHeader}" = apiKeyHashed
            response.failure = { resp ->
                resp
            }
        }
        then: 'We expect a status code of 404 (Not Found)'
        assert serverResponse.statusLine.statusCode == HttpServletResponse.SC_NOT_FOUND
        where:
        label                                      | url                   | acceptType
        "External References With wrong mime type" | externalReferencesUrl | externalReferencesAcceptContentType
        "External Systems With wrong mime type"    | externalSystemsUrl    | externalSystemsAcceptContentType
        "External System With wrong mime type"     | externalSystemUrl     | externalReferenceAcceptContentType
        "External Reference With wrong mime type"  | externalReferenceUrl  | externalSystemAcceptContentType
    }



    def 'test GET External Reference Success'() {
        given: "there is a service endpoint to get an assay with id 1"
        RESTClient http = new RESTClient("${externalReferenceUrl}/1")

        when: 'We send an HTTP GET request for that assay with the appropriate mime type'
        def serverResponse = http.request(GET, XML) {
            headers.Accept = externalReferenceAcceptContentType
            headers."${apiKeyHeader}" = apiKeyHashed
        }
        then: 'We expect an XML representation of that assay'
        assert serverResponse.statusLine.statusCode == HttpServletResponse.SC_OK
        assert serverResponse.getFirstHeader('ETag')
        assert serverResponse.getFirstHeader('ETag').name == 'ETag'
        assert serverResponse.getFirstHeader('ETag').value == '0'
        final String responseData = serverResponse.data.readLines().join()
        XMLAssert.assertXpathEvaluatesTo("1", "count(//assayContexts)", responseData)
        XMLAssert.assertXpathEvaluatesTo("1", "count(//assayContext)", responseData)
        XMLAssert.assertXpathEvaluatesTo("1", "count(//measures)", responseData)
        XMLAssert.assertXpathEvaluatesTo("1", "count(//measure)", responseData)
        XMLAssert.assertXpathEvaluatesTo("2", "count(//assayContextItems)", responseData)
        XMLAssert.assertXpathEvaluatesTo("6", "count(//assayContextItem)", responseData)
        XMLAssert.assertXpathEvaluatesTo("2", "count(//assayDocument)", responseData)
        XMLAssert.assertXpathEvaluatesTo("17", "count(//link)", responseData)
    }
}
