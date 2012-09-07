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

    def "Test Search By IDs: #label"() {
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
        label                   | searchString                | searchAction
        "Find Projects By Id"   | "1772,%20805,%201074"           | "searchProjectsByIDs"
        "Find Assays By Id"     | "600,644,666"             | "searchAssaysByIDs"
        "Find Compounds By CID" | "3235555,3235556,3235557" | "searchCompoundsByIDs"
    }

    def "Test Search with Filters : #label"() {
        given: "there is a service end point to get the root elements"
        String requestUrl = generateRequestUrl([searchString, filterName, filterValue, formName], searchAction);
        RESTClient http = new RESTClient(requestUrl)

        when: 'We send an HTTP GET request for the search action'
        HttpResponseDecorator serverResponse = (HttpResponseDecorator) http.get(requestContentType: JSON)

        then: 'We expect a JSON representation of the root elements'
        assert serverResponse.statusLine.statusCode == HttpServletResponse.SC_OK
        println serverResponse.data.toString()
        assert serverResponse.data.size() > 0
        assert !serverResponse.data.any { it.toString().contains("errorCode") }
        where:
        label            | searchString | searchAction   | filterName                        | filterValue                                   | formName
        "Find Projects"  | "dna+repair" | "applyFilters" | "filters[0].filterName=num_expt"  | "filters[0].filterValue=6"                    | "formName=" + FacetFormType.ProjectFacetForm.toString()
        "Find Assays"    | "dna+repair" | "applyFilters" | "filters[0].filterName=gobp_term" | "filters[0].filterValue=response%20to%20UV-C" | "formName=" + FacetFormType.AssayFacetForm.toString()
        "Find Compounds" | "dna+repair" | "applyFilters" | "filters[0].filterName=tpsa"      | "filters[0].filterValue=55.1"                 | "formName=" + FacetFormType.CompoundFacetForm.toString()
//        "Super structure search" | "${StructureSearchParams.Type.Superstructure}:O=S(*C)(Cc1ccc2ncc(CCNC)c2c1)=O"           | "searchStructures"
//        "Similarity Search"      | "${StructureSearchParams.Type.Similarity}:CN(C)CCC1=CNC2=C1C=C(CS(=O)(=O)N3CCCC3)C=C2"   | "searchStructures"
//        "Exact match Search"     | "${StructureSearchParams.Type.Exact}:CN(C)CCC1=CNC2=C1C=C(CS(=O)(=O)N3CCCC3)C=C2"        | "searchStructures"
//        "Sub structure Search"   | "${StructureSearchParams.Type.Substructure}:CN(C)CCC1=CNC2=C1C=C(CS(=O)(=O)N3CCCC3)C=C2" | "searchStructures"

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
