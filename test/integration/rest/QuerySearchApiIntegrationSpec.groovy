package rest

import bardqueryapi.QueryAssayApiService
import grails.plugin.spock.IntegrationSpec
import wslite.json.JSONObject

import static junit.framework.Assert.assertNotNull

class QuerySearchApiIntegrationSpec  extends IntegrationSpec {

    QueryAssayApiService queryAssayApiService

    void setup() {
        // Setup logic here
    }

    void tearDown() {
        // Tear down logic here
    }

    void testSearchForAssays() {
        given:
        final String searchSpecification = "/search/assays?q=dna"
        when:
        final JSONObject assayResults = queryAssayApiService.performSearchGivenAString(searchSpecification)
        then:
        assert assertCoreElementsPresent(assayResults)
    }

    void testSearchForCompounds() {
        given:
        final String searchSpecification = "/search/compounds?q=dna"
        when:
        final JSONObject compoundsResults = queryAssayApiService.performSearchGivenAString(searchSpecification)
        then:
        assert assertCoreElementsPresent(compoundsResults)
    }

    void testSearchForProjects() {
        given:
        final String searchSpecification = "/search/projects?q=dna"
        when:
        final JSONObject projectResults = queryAssayApiService.performSearchGivenAString(searchSpecification)
        then:
        assert assertCoreElementsPresent(projectResults)
    }

    def assertCoreElementsPresent(JSONObject jSONObject) {
        assertNotNull jSONObject
        assert jSONObject.eTag!=0
        assert jSONObject.docs.size()!=0
        assertNotNull jSONObject.link
        return true
    }
}



