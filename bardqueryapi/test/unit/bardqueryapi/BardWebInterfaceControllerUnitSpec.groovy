package bardqueryapi


import elasticsearchplugin.ESAssay
import elasticsearchplugin.ESXCompound
import grails.test.mixin.TestFor
import spock.lang.Specification
import wslite.json.JSONArray
import wslite.json.JSONObject

/**
 * See the API for {@link grails.test.mixin.support.GrailsUnitTestMixin} for usage instructions
 */
@TestFor(BardWebInterfaceController)
class BardWebInterfaceControllerUnitSpec extends Specification {

    QueryService queryService

    final static String compoundDocumentJson = '''{"probeId":"null","sids":[4243156,24368917],"smiles":"C-C","cid":"3237916"}'''
//    final static String AUTO_COMPLETE_NAMES = '''
//{
//    "hits": {
//        "hits": [
//            {
//                "fields": {
//                    "name": "Broad Institute MLPCN Platelet Activation"
//                }
//            }
//        ]
//    }
//  }
// '''

    final static ESAssay esAssay = new ESAssay(
            _index: 'index',
            _type: 'type',
            _id: 'id',
            assayNumber: 'assayNumber',
            assayName: 'assayName'
    )

//    final static ESCompound esCompound = new ESCompound(
//            _index: 'index',
//            _type: 'type',
//            _id: 'id',
//            cid: '1234567890'
//    )

    final static ESXCompound esxCompound = new ESXCompound(
            _index: 'index',
            _type: 'type',
            _id: 'id',
            cid: '1234567890'
    )

//    final static String STRUCTURE_SEARCH_RESPONSE_JSON = '''
//    ["/bard/rest/v1/compounds/6796",
//    "/bard/rest/v1/compounds/9189",
//    "/bard/rest/v1/compounds/7047"]
//    '''

    void setup() {
        queryService = Mock(QueryService)
        controller.queryService = this.queryService
    }

    void tearDown() {
        // Tear down logic here
    }

    void "test showCompound #label"() {
        when:
        request.method = 'GET'
        controller.showCompound(cid)
        then:
        queryService.showCompound(_) >> { compoundJson }

        "/bardWebInterface/showCompound" == view
        872 == model.compoundId
        def compoundJson1 = model.compoundJson
        println compoundJson1
        //compoundJson1.probeId == null
        //println compoundJson1.sids


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
        queryService.search(searchTerm) >> { resultJson }

        assert "/bardWebInterface/homePage" == view
        assert model.totalCompounds == expectedTotalCompounds
        assert model.assays.size == expectedAssays
        assert model.compounds.size == expectedCompounds
        assert model.experiments == []
        assert model.projects == []

        where:
        label                                | searchTerm | resultJson                                                                                                          | expectedTotalCompounds | expectedAssays | expectedCompounds
        "nothing was found"                  | '644'      | [totalCompounds: 0, assays: [], compounds: [], experiments: [], projects: []]                                       | 0                      | 0              | 0
        "An Assay and a compound were found" | '644'      | [totalCompounds: 1, assays: [esAssay], compounds: ['CC'], xcompounds: [esxCompound], experiments: [], projects: []] | 1                      | 1              | 1
    }


    void "test handle autocomplete #label"() {
        when:
        request.method = 'GET'
        controller.params.term = searchString
        controller.autoCompleteAssayNames()
        then:
        queryService.autoComplete(_) >> { expectedList }
        println controller.response.json
        println expectedList.toString()
        controller.response.json.toString() == new JSONArray(expectedList.toString()).toString()

        where:
        label                | searchString | expectedList
        "Return two strings" | "Bro"        | ["Broad Institute MLPCN Platelet Activation"]

    }

//    void "test handle getCIDsByStructureFromNCGC #label"() {
//        when:
//        controller.grailsApplication.config.ncgc.server.structureSearch.root.url = 'http://mockedUrl'
//        final List<String> responseList = controller.getCIDsByStructure(smiles, searchType)
//
//
//        then:
//        queryExecutorService.executeGetRequestJSON(expectedRequestUrl, [connectTimeout: 5000, readTimeout: 10000]) >> { expectedJSONResponse }
//        responseList == expectedList
//
//        where:
//        label                  | smiles | searchType                               | expectedJSONResponse                          | expectedList             | expectedRequestUrl
//        "Sub-structure searcg" | "C-C"  | StructureSearchType.SUB_STRUCTURE.name() | new JSONArray(STRUCTURE_SEARCH_RESPONSE_JSON) | ["6796", "9189", "7047"] | 'http://mockedUrl?filter=C-C[structure]&type=sub'
//        "Exact-match search"   | "C-C"  | StructureSearchType.EXACT_MATCH.name()   | new JSONArray(STRUCTURE_SEARCH_RESPONSE_JSON) | ["6796", "9189", "7047"] | 'http://mockedUrl?filter=C-C[structure]&type=exact'
//        "Similarity search"    | "C-C"  | StructureSearchType.SIMILARITY.name()    | new JSONArray(STRUCTURE_SEARCH_RESPONSE_JSON) | ["6796", "9189", "7047"] | 'http://mockedUrl?filter=C-C[structure]&type=sim&cutoff=0.9'
//    }
}
