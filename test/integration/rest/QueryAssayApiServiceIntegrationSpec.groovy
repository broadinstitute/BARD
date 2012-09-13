package rest

import grails.plugin.spock.IntegrationSpec
import wslite.json.JSONArray
import wslite.json.JSONObject
import bardqueryapi.QueryAssayApiService
import bardqueryapi.QueryExecutorInternalService

/**
 * See the API for {@link grails.test.mixin.support.GrailsUnitTestMixin} for usage instructions
 */

class QueryAssayApiServiceIntegrationSpec extends IntegrationSpec {

    QueryExecutorInternalService queryExecutorInternalService
    def grailsApplication

    void setup() {
        // Setup logic here
    }

    void tearDown() {
        // Tear down logic here
    }




    void testAssaySummary() {
        //http://bard.nih.gov/api/v1/search/assays?q="dna repair"&top=10
        given:
        def params = [query: [q: '"dna repair"', top: 10, expand: false, include_entities: true]]
        //final String assaySearchUrl = "${grailsApplication.config.ncgc.server.root.url}/search/assays?q=\"dna repair\"&top=10"
        final String assaySearchUrl = "${grailsApplication.config.ncgc.server.root.url}/search/assays"
        when:
        def assays = queryExecutorInternalService.executeGetJSON(assaySearchUrl, params)

        then:
        assert assays
        final JSONArray docs = assays.docs
        final List documentList = docs.subList(0, docs.length())
        documentList.each {document ->
            println document.assay_id.toString()
            println document.name.toString()
            println document.highlight.toString()

            println ""
            println ""
        }
        final String nextLink = assays.link
        final JSONObject metaData = assays.metaData
        final int numberOFHits = metaData.nhit
        final JSONArray facets = metaData.facets
        println assays.getClass().getName()

    }
}
