package bardqueryapi

import elasticsearchplugin.ESAssay
import elasticsearchplugin.ESCompound
import elasticsearchplugin.ESXCompound
import elasticsearchplugin.ElasticSearchService
import grails.test.mixin.TestFor
import spock.lang.Specification
import wslite.json.JSONObject

/**
 * See the API for {@link grails.test.mixin.support.GrailsUnitTestMixin} for usage instructions
 */
@TestFor(BardWebInterfaceController)
class BardWebInterfaceControllerUnitSpec extends Specification {

    ElasticSearchService elasticSearchService

    final static String compoundDocumentJson = '''{
        "_index": "compounds",
        "_type": "compound",
        "_id": "3237916",
        "_score": "1",
        "_source": {
            "resourcePath": "/bard/rest/v1/compounds/3237916",
            "probeId": "null",
            "sids": [
                4243156,
                24368917
            ],
            "smiles": "C-C",
            "cid": 3237916,
            "url": "null"
        }
    }'''
    final static String AUTO_COMPLETE_NAMES = '''
{
    "hits": {
        "hits": [
            {
                "fields": {
                    "name": "Broad Institute MLPCN Platelet Activation"
                }
            }
        ]
    }
  }
 '''

    final static ESAssay esAssay = new ESAssay(
            _index: 'index',
            _type: 'type',
            _id: 'id',
            assayNumber: 'assayNumber',
            assayName: 'assayName'
    )

    final static ESCompound esCompound = new ESCompound(
            _index: 'index',
            _type: 'type',
            _id: 'id',
            cid: '1234567890'
    )

    final static ESXCompound esxCompound = new ESXCompound(
            _index: 'index',
            _type: 'type',
            _id: 'id',
            cid: '1234567890'
    )

    void setup() {
        elasticSearchService = Mock(ElasticSearchService)
        controller.elasticSearchService = this.elasticSearchService
        controller.metaClass.mixin(AutoCompleteHelper)
    }

    void tearDown() {
        // Tear down logic here
    }

    void "test showCompound #label"() {
        when:
        request.method = 'GET'
        controller.showCompound(cid)
        then:
        elasticSearchService.getCompoundDocument(_) >> { compoundJson }

        "/bardWebInterface/showCompound" == view
        expectedCompoundJson == model.compoundJson.toString()
        872 == model.compoundId

        where:
        label                                | cid              | compoundJson                         | expectedCompoundJson
        "compound not found - error message" | new Integer(872) | new JSONObject()                     | '{}'
        "Return a compound"                  | new Integer(872) | new JSONObject(compoundDocumentJson) | '{"probeId":"null","sids":[4243156,24368917],"smiles":"C-C","cid":"3237916"}'
    }

    void "test search #label"() {
        when:
        request.method = 'GET'
        controller.params.searchString = searchTerm
        controller.search()

        then:
//        elasticSearchService.search(searchTerm) >> { resultJson }
        elasticSearchService.elasticSearchQuery(searchTerm) >> { resultJson }

        assert "/bardWebInterface/homePage" == view
        assert model.totalCompounds == expectedTotalCompounds
        assert model.assays.size == expectedAssays
        assert model.compounds.size == expectedCompounds
        assert model.experiments == []
        assert model.projects == []

        where:
        label                                | searchTerm | resultJson                                   | expectedTotalCompounds | expectedAssays | expectedCompounds
        "nothing was found"                  | '644'      | [assays: [], compounds: []]                  | 0                      | 0              | 0
        "An Assay and a compound were found" | '644'      | [assays: [esAssay], xcompounds: [esxCompound]]| 1                      | 1              | 1
    }

    void "test autocomplete #label"() {
        when:
        request.method = 'GET'
        controller.params.searchString = searchString
        controller.autoCompleteAssayNames()


        then:
        elasticSearchService.searchQueryStringQuery(_, _) >> { expectedJSONResponse }
        assertEquals expectedRender, controller.response.json.toString()

        where:
        label                 | searchString | expectedJSONResponse                | expectedRender
        "Return two strings"  | "Bro"        | new JSONObject(AUTO_COMPLETE_NAMES) | '["Broad Institute MLPCN Platelet Activation"]'
        "Return Empty String" | "644"        | []                                  | '[""]'

    }

    void "test handle autocomplete #label"() {
        when:
        request.method = 'GET'
        controller.params.term = searchString
        final List<String> responseList = controller.handleAutoComplete(this.elasticSearchService, "http://localhost")


        then:
        elasticSearchService.searchQueryStringQuery(_, _) >> { expectedJSONResponse }
        responseList == expectedList

        where:
        label                 | searchString | expectedJSONResponse                | expectedList
        "Return two strings"  | "Bro"        | new JSONObject(AUTO_COMPLETE_NAMES) | ["Broad Institute MLPCN Platelet Activation"]
        "Return Empty String" | "644"        | []                                  | []

    }
}
