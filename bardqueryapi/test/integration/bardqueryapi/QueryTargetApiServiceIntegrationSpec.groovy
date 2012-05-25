package bardqueryapi

import grails.plugin.spock.IntegrationSpec

/**
 * See the API for {@link grails.test.mixin.support.GrailsUnitTestMixin} for usage instructions
 */

class QueryTargetApiServiceIntegrationSpec extends IntegrationSpec {

    QueryTargetApiService queryTargetApiService

    void setup() {
        // Setup logic here
    }

    void tearDown() {
        // Tear down logic here
    }

    void testFindProteinByGeneId() {
        given:
        final String geneUrl = "/bard/rest/v1/targets/geneid/3265"
        when:
        final def protein = queryTargetApiService.findProteinByGeneId(geneUrl)
        then:
        assert protein
        println protein
    }

    void testFindProteinByUniprotAccession() {
        given:
        final String accessionUrl = "/bard/rest/v1/targets/accession/P01112"
        when:
        final def protein = queryTargetApiService.findProteinByUniprotAccession(accessionUrl)
        then:
        assert protein
        println protein
    }
}
