package bard.db.registration

import spock.lang.Specification
import grails.test.mixin.TestFor
import spock.lang.Unroll
import elasticsearchplugin.ElasticSearchService
import elasticsearchplugin.ESAssay
import elasticsearchplugin.ESCompound
import org.codehaus.groovy.grails.web.json.JSONObject
import grails.converters.JSON

/**
 * See the API for {@link grails.test.mixin.support.GrailsUnitTestMixin} for usage instructions
 */
@TestFor(ESAssayController)
@Unroll
class ESAssayControllerUnitSpec extends Specification {

    ElasticSearchService elasticSearchService

    final static String assayDocumentJson = '''
    {"_index" : "assays",
	"_type" : "assay",
	"_id" : "644",
	"_version" : 2,
	"exists" : true,
	"_source" : {
		"summary" : 0,
		"protocol" : "protocol",
		"deposited" : null,
		"aid" : 644,
		"type" : 2,
		"assays" : 1,
		"resourcePath" : "/bard/rest/v1/assays/644",
		"grantNo" : null,
		"category" : 1,
		"publications" : [{
				"resourcePath" : "/bard/rest/v1/documents/12102637",
				"title" : "Kinetic mechanism for human Rho-Kinase II (ROCK-II).",
				"pubmedId" : 12102637,
				"abs" : "abstract",
				"doi" : "null"
			}
		],
		"source" : "The Scripps Research Institute Molecular Screening Center",
		"updated" : null,
		"description" : "description",
		"classification" : 0,
		"name" : "Dose-response biochemical assay of inhibitors of Rho kinase 2 (Rock2) ",
		"entityTag" : "tag",
		"targets" : [{
				"resourcePath" : "/bard/rest/v1/targets/accession/O75116",
				"taxId" : 9606,
				"status" : "Reviewed",
				"geneId" : 9475,
				"description" : null,
				"name" : "Rho-associated protein kinase 2",
				"acc" : "O75116"
			}
		],
		"comments" : "comments"
	    }
    }'''

    void setup() {
        elasticSearchService = Mock(ElasticSearchService)
        controller.elasticSearchService = this.elasticSearchService
    }

    void tearDown() {
        // Tear down logic here
    }

    void "test showCompound #label"() {
        when:
        request.method = 'GET'
        controller.params.id = assayId
        def response = controller.show()

        then:
        elasticSearchService.getAssayDocument(assayId) >> { assayJson }

        assert expectedResponseAid == response?.assayInstance?.aid
        assert expectedFlashMessage == model?.flash?.message

        where:
        label                             | assayId          | assayJson                     | expectedFlashMessage | expectedResponseAid
        "Assay not found - error message" | new Integer(644) | [:] as JSONObject             | null                 | null
        "Return an assay"                 | new Integer(644) | JSON.parse(assayDocumentJson) | null                 | 644 as Integer

    }
}
