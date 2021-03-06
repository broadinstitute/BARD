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

package bardqueryapi

import bard.core.rest.spring.util.StructureSearchParams
import grails.converters.JSON
import grails.plugin.remotecontrol.RemoteControl
import groovyx.net.http.HttpResponseDecorator
import groovyx.net.http.HttpResponseException
import groovyx.net.http.RESTClient
import spock.lang.Specification
import spock.lang.Unroll

import javax.servlet.http.HttpServletResponse

@Unroll
class BardWebInterfaceFunctionalSpec extends Specification {
    RemoteControl remote = new RemoteControl()
    String baseUrl = remote { ctx.grailsApplication.config.grails.serverURL }


    def "test promiscuity action #label"() {
        given: "That we have a valid cid #cid"
        String requestUrl = "${baseUrl}/bardWebInterface/promiscuity?cid=${cid}"
        RESTClient http = new RESTClient(requestUrl)

        when: 'We send an HTTP GET request to the promiscuity action with cid #cid'
        HttpResponseDecorator serverResponse = (HttpResponseDecorator) http.get(requestContentType: JSON)

        then: 'We expect a JSON representation of the promiscuity scores'
        assert serverResponse.statusLine.statusCode == HttpServletResponse.SC_OK
        assert serverResponse.data.size() > 0
        assert !serverResponse.data.any { it.toString().contains("errorCode") }

        where:
        label       | cid
        "CID 38911" | 38911
        "CID 2722"  | 2722

    }

    def "test promiscuity action with exception #label"() {
        given:
        String requestUrl = "${baseUrl}/bardWebInterface/promiscuity?cid=${cid}"
        RESTClient http = new RESTClient(requestUrl)

        when: 'We send an HTTP GET request for the search action'
        (HttpResponseDecorator) http.get(requestContentType: JSON)

        then: 'We expect an error'
        def e = thrown(HttpResponseException)
        e.statusCode == statusCode

        where:
        label                 | cid  | statusCode
        "Empty CID String"    | null | HttpServletResponse.SC_BAD_REQUEST
        "CID- Does not exist" | -1   | HttpServletResponse.SC_NOT_FOUND
    }

    def "Test Search querying: #label"() {
        given: "there is a service end point to get the root elements"
        String requestUrl = generateRequestUrl([searchString], searchAction)
        RESTClient http = new RESTClient(requestUrl)

        when: 'We send an HTTP GET request for the search action'
        HttpResponseDecorator serverResponse = (HttpResponseDecorator) http.get(requestContentType: JSON)

        then: 'We expect a JSON representation of the root elements'
        assert serverResponse.statusLine.statusCode == HttpServletResponse.SC_OK
        assert serverResponse.data.size() > 0
        assert !serverResponse.data.any { it.toString().contains("errorCode") }

        where:
        label                    | searchString                                                                             | searchAction
        "Find Projects"          | "dna+repair"                                                                             | "searchProjects"
        "Find Assays"            | "dna+repair"                                                                             | "searchAssays"
        "Find Compounds"         | "dna+repair"                                                                             | "searchCompounds"
        "Super structure search" | "${StructureSearchParams.Type.Superstructure}:O=S(*C)(Cc1ccc2ncc(CCNC)c2c1)=O"           | "searchStructures"
        "Similarity Search"      | "${StructureSearchParams.Type.Similarity}:CN(C)CCC1=CNC2=C1C=C(CS(=O)(=O)N3CCCC3)C=C2"   | "searchStructures"
        "Exact match Search"     | "${StructureSearchParams.Type.Exact}:CN(C)CCC1=CNC2=C1C=C(CS(=O)(=O)N3CCCC3)C=C2"        | "searchStructures"
        "Sub structure Search"   | "${StructureSearchParams.Type.Substructure}:CN(C)CCC1=CNC2=C1C=C(CS(=O)(=O)N3CCCC3)C=C2" | "searchStructures"

    }


    def "Test Search By IDs: #label"() {
        given: "there is a service end point to get the root elements"
        String requestUrl = generateRequestUrl([searchString], searchAction)
        RESTClient http = new RESTClient(requestUrl)

        when: 'We send an HTTP GET request for the search action'
        HttpResponseDecorator serverResponse = (HttpResponseDecorator) http.get(requestContentType: JSON)

        then: 'We expect a JSON representation of the root elements'
        assert serverResponse.statusLine.statusCode == HttpServletResponse.SC_OK
        assert serverResponse.data.size() > 0
        assert !serverResponse.data.any { it.toString().contains("errorCode") }

        where:
        label                   | searchString               | searchAction
        "Find Projects By Id"   | "1581,%201563,%2017481772" | "searchProjectsByIDs"
        "Find Assays By Id"     | "5155,5158,5157"         | "searchAssaysByIDs"
        "Find Compounds By CID" | "3235555,3235556,3235557"  | "searchCompoundsByIDs"
    }

    def "Test Search with Filters : #label"() {
        given: "there is a service end point to get the root elements"
        String requestUrl = generateRequestUrl([searchString, filterName, filterValue, formName], searchAction);
        RESTClient http = new RESTClient(requestUrl)

        when: 'We send an HTTP GET request for the search action'
        HttpResponseDecorator serverResponse = (HttpResponseDecorator) http.get(requestContentType: JSON)

        then: 'We expect a JSON representation of the root elements'
        assert serverResponse.statusLine.statusCode == HttpServletResponse.SC_OK
        assert serverResponse.data.size() > 0
        assert !serverResponse.data.any { it.toString().contains("errorCode") }
        where:
        label            | searchString | searchAction      | filterName                        | filterValue                                   | formName
        "Find Projects"  | "dna+repair" | "searchProjects"  | "filters[0].filterName=num_expt"  | "filters[0].filterValue=6"                    | "formName=" + FacetFormType.ProjectFacetForm.toString()
        "Find Assays"    | "dna+repair" | "searchAssays"    | "filters[0].filterName=gobp_term" | "filters[0].filterValue=response%20to%20UV-C" | "formName=" + FacetFormType.AssayFacetForm.toString()
        "Find Compounds" | "dna+repair" | "searchCompounds" | "filters[0].filterName=tpsa"      | "filters[0].filterValue=55.1"                 | "formName=" + FacetFormType.CompoundFacetForm.toString()
    }

    def "Test Auto Complete querying"() {
        given: "there is a service end point to do autocomplete on Assay names"

        StringBuilder sb = new StringBuilder()
        sb.append(baseUrl)
        sb.append("/bardWebInterface/autoCompleteAssayNames?term=Bro*")

        final String requestUrl = sb.toString()
        RESTClient http = new RESTClient(requestUrl)

        when: 'We send an HTTP GET request for the auto complete action'
        HttpResponseDecorator serverResponse = (HttpResponseDecorator) http.get(requestContentType: JSON)

        then: 'We expect a JSON representation of the assay names'
        assert serverResponse.statusLine.statusCode == HttpServletResponse.SC_OK
        assert serverResponse.data.size() > 0
        assert !serverResponse.data.any { it.toString().contains("errorCode") }
    }

    /*
     * Chain together the test parameters to create a URL for the request
     */

    private String generateRequestUrl(final List<String> paramList, final String searchAction) {
        String allParams = paramList?.join("&")

        StringBuilder sb = new StringBuilder()

        sb.append(baseUrl)
        sb.append("/bardWebInterface/")
        sb.append("${searchAction}")
        sb.append("?searchString=${allParams}")
        return sb.toString()
    }
}
