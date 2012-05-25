package bardqueryapi

import grails.plugin.spock.IntegrationSpec

/**
 * See the API for {@link grails.test.mixin.support.GrailsUnitTestMixin} for usage instructions
 */

class QueryCompoundApiServiceIntegrationSpec extends IntegrationSpec {

    QueryCompoundApiService queryCompoundApiService

    void setup() {
       // Setup logic here
    }

    void tearDown() {
       // Tear down logic here
    }


    void testFindCompoundByCID() {
        given:
        String cidUrl = "/bard/rest/v1/compounds/1356407"

        when:
        final def compound = queryCompoundApiService.findCompoundByCID(cidUrl,null)
        then:
        assert compound
        println compound

    }
    void testFindCompoundByCIDSDFFormat() {
        given:
        String cidUrl = "/bard/rest/v1/compounds/1356407"

        when:
        final def compound = queryCompoundApiService.findCompoundByCID(cidUrl,[Accept:"chemical/x-mdl-sdfile"])
        then:
        assert compound
        println compound
    }
    void testFindCompoundByCIDDaylightSmilesFormat() {
        given:
        String cidUrl = "/bard/rest/v1/compounds/1356407"

        when:
        final def compound = queryCompoundApiService.findCompoundByCID(cidUrl,[Accept:"chemical/x-daylight-smiles"])
        then:
        assert compound
        println compound
    }
    void testFindCompoundBySID() {
        given:
        String cidUrl = "/bard/rest/v1/compounds/sid/92764147"

        when:
        final def compound = queryCompoundApiService.findCompoundByCID(cidUrl,null)
        then:
        assert compound
        println compound

    }
    void testFindCompoundBySIDSDFFormat() {
        given:
        String cidUrl = "/bard/rest/v1/compounds/sid/92764147"

        when:
        final def compound = queryCompoundApiService.findCompoundByCID(cidUrl,[Accept:"chemical/x-mdl-sdfile"])
        then:
        assert compound
        println compound
    }
    void testFindCompoundBySIDDaylightSmilesFormat() {
        given:
        String cidUrl = "/bard/rest/v1/compounds/sid/92764147"

        when:
        final def compound = queryCompoundApiService.findCompoundByCID(cidUrl,[Accept:"chemical/x-daylight-smiles"])
        then:
        assert compound
        println compound
    }
}
