package rest

import grails.plugin.spock.IntegrationSpec
import bardqueryapi.QueryTargetApiService

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
//    void testGetAssaysForAccessionTarget() {
//        given: "An accession number for a target"
//        final String accessionId = "P01112"
//        when: "We make a Query to NCGC's rest API to get a list of assays with that target"
//        final List<String> assays = queryTargetApiService.findAssaysForAccessionTarget(accessionId)
//        then: "We get back a list assay ids"
//        assert assays
//        println assays
//    }
    void "test Get Assays For Accession Target, return Empty list"() {
        given: "An accession number for a target"
        final String accessionId = "XXY"
        when: "We make a Query to NCGC's rest API to get a list of assays with that target"
        final List<String> assays =  queryTargetApiService.findAssaysForAccessionTarget(accessionId)
        then: "An empty list should be returned"
        assert !assays
        println assays
    }
    void testFindProteinByGeneId() {
        given:
        final String geneId = "3265"
        when:
        final def protein = queryTargetApiService.findProteinByGeneId(geneId)
        then:
        assert protein
        println protein
    }

    void testFindProteinByUniprotAccession() {
        given:
        final String accessionId = "P01112"
        when:
        final def protein = queryTargetApiService.findProteinByUniprotAccession(accessionId)
        then:
        assert protein
        println protein
    }
}
