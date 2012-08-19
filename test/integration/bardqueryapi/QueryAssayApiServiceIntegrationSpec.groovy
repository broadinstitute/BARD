package bardqueryapi

import elasticsearchplugin.QueryExecutorService
import grails.plugin.spock.IntegrationSpec
import wslite.json.JSONArray
import wslite.json.JSONObject

/**
 * See the API for {@link grails.test.mixin.support.GrailsUnitTestMixin} for usage instructions
 */

class QueryAssayApiServiceIntegrationSpec extends IntegrationSpec {

    QueryAssayApiService queryAssayApiService
    QueryExecutorService queryExecutorService
    def grailsApplication

    void setup() {
        // Setup logic here
    }

    void tearDown() {
        // Tear down logic here
    }

    void testFindAssayByAid() {
        given:
        final String aidUrl = "/assays/1772"
        when:
        final def assay = queryAssayApiService.findAssayByAid(aidUrl)
        then:
        assert assay
        //println assay
    }

    void testFindCompoundsByAssay() {
        given:
        final String aidUrl = "/assays/1772"
        when:
        final def compounds = queryAssayApiService.findCompoundsByAssay(aidUrl, null)
        then:
        assert compounds
        compounds.each {compound ->
            //  println compound
        }
    }

    void testFindProteinTargetsByAssay() {
        given:
        final String aidUrl = "/assays/1772"
        when:
        final def proteinTargetsByAssay = queryAssayApiService.findProteinTargetsByAssay(aidUrl)
        then:
        assert proteinTargetsByAssay
        proteinTargetsByAssay.each {target ->
            //println target
        }
    }

    void testAssays() {
        given:
        final String publicationUrl = "${grailsApplication.config.ncgc.server.root.url}/assays"
        when:
        def assays = queryExecutorService.executeGetRequestJSON(publicationUrl)

        then:
        assert assays
        int counter = 0
        assays.each {assay ->
            ++counter
            //println counter + ":" + assay
        }
    }

    void testAssaySummary() {
        //http://bard.nih.gov/api/v1/search/assays?q="dna repair"&top=10
        given:
        def params = [query: [q: '"dna repair"', top: 10, expand: false, include_entities: true]]
        //final String assaySearchUrl = "${grailsApplication.config.ncgc.server.root.url}/search/assays?q=\"dna repair\"&top=10"
        final String assaySearchUrl = "${grailsApplication.config.ncgc.server.root.url}/search/assays"
        when:
        def assays = queryExecutorService.executeGetRequestJSON(assaySearchUrl, params)

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

//    void testFindAssayByAid() {
//        given:
//        String aid = 493159
//        when:
//        final Response assays = queryAssayApiService.findAssayByAid(aid)
//        final json = assays.json
//        then:
//        assert json
//        println "Aid: ${json.aid}"
//        println "Category : ${json.category}"
//        println "Type: ${json.type}"
//        println "Summary: ${json.summary}"
//        println "Assays: ${json.assays}"
//        println "Classification: ${json.classification}"
//        println "Samples: ${json.samples}"
//        println "Name: ${json.name}"
//        println "Description: ${json.description}"
//        println "Source: ${json.source}"
//        println "Grant #: ${json.grantNo}"
//        println "Deposited: ${json.deposited}"
//        println "Updated: ${json.updated}"
//        println "Targets: ${json.targets}"
//        println "Data : ${json.data}"
//        println "Publications :"
//        json.publications.each{ publication ->
//            println "\t ${publication.resourcePath}"
//        }
//        assays.json.each{ assay ->
//            println assay
//        }
//
//
//    }
}
