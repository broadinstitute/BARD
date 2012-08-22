package bardqueryapi

import bard.core.StructureSearchParams
import grails.plugin.remotecontrol.RemoteControl
import groovyx.net.http.HttpResponseDecorator
import groovyx.net.http.RESTClient
import spock.lang.Specification
import spock.lang.Unroll

import javax.servlet.http.HttpServletResponse

import static groovyx.net.http.ContentType.JSON

@Unroll
class BardWebInterfaceFunctionalSpec extends Specification {
    RemoteControl remote = new RemoteControl()
    String baseUrl = remote { ctx.grailsApplication.config.grails.serverURL }


    def "Test Search querying: #label"() {
        given: "there is a service end point to get the root elements"
        String requestUrl = generateRequestUrl([searchString], searchAction)
        RESTClient http = new RESTClient(requestUrl)

        when: 'We send an HTTP GET request for the search action'
        HttpResponseDecorator serverResponse = (HttpResponseDecorator) http.get(requestContentType: JSON)

        then: 'We expect a JSON representation of the root elements'
        assert serverResponse.statusLine.statusCode == HttpServletResponse.SC_OK
        println serverResponse.data.toString()
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
        println serverResponse.data.toString()
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
