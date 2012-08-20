package bardqueryapi

import bard.QueryServiceWrapper
import bard.core.Compound
import bard.core.MolecularValue
import bard.core.Value
import bard.core.rest.RESTCompoundService
import elasticsearchplugin.ElasticSearchService
import elasticsearchplugin.QueryExecutorService
import grails.test.mixin.TestFor
import spock.lang.Specification
import wslite.json.JSONObject

/**
 * See the API for {@link grails.test.mixin.support.GrailsUnitTestMixin} for usage instructions
 */
@TestFor(QueryService)
class QueryServiceUnitSpec extends Specification {
    QueryServiceWrapper queryServiceWrapper
    QueryExecutorService queryExecutorService
    ElasticSearchService elasticSearchService
    RESTCompoundService restCompoundService
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

    void setup() {
        queryExecutorService = Mock()
        restCompoundService = Mock()
        elasticSearchService = Mock()
        queryServiceWrapper = Mock()
        service.queryExecutorService = queryExecutorService
        service.elasticSearchService = elasticSearchService
        service.queryServiceWrapper = queryServiceWrapper
        service.elasticSearchRootURL = 'httpMock://'
        service.bardAssayViewUrl = 'httpMock://'
        service.ncgcSearchBaseUrl = 'httpMock://'
    }

    void tearDown() {
        // Tear down logic here
    }

    /**
     */
    void "test autoComplete #label"() {

        when:
        final List<String> response = service.autoComplete(term)

        then:
        elasticSearchService.searchQueryStringQuery(_, _) >> { jsonResponse }

        assert response == expectedResponse

        where:
        label                       | term  | jsonResponse                        | expectedResponse
        "Partial match of a String" | "Bro" | new JSONObject(AUTO_COMPLETE_NAMES) | ["Broad Institute MLPCN Platelet Activation"]
        "Empty String"              | ""    | new JSONObject()                    | []
    }

    /**
     */
    void "test Get CIDs By Structure #label"() {

        when:
        List<String> response = service.getCIDsByStructure(smiles, structureType)

        then:
        queryExecutorService.executeGetRequestJSON(_, _) >> {assayJson}

        assert response == expectedResponse

        where:
        label                  | structureType                     | smiles | assayJson      | expectedResponse
        "Sub structure Search" | StructureSearchType.SUB_STRUCTURE | "CC"   | ["223", "224"] | ["223", "224"]
        "Exact match Search"   | StructureSearchType.EXACT_MATCH   | "C"    | ["225", "226"] | ["225", "226"]
        "Similarity Search"    | StructureSearchType.SIMILARITY    | "CCC"  | ["111"]        | ["111"]

    }
    /**
     */
    void "test handleAutoComplete #label"() {

        when:
        final List<String> response = service.handleAutoComplete(term)

        then:
        elasticSearchService.searchQueryStringQuery(_, _) >> { jsonResponse }
        assert response == expectedResponse

        where:
        label                       | term  | jsonResponse                        | expectedResponse
        "Partial match of a String" | "Bro" | new JSONObject(AUTO_COMPLETE_NAMES) | ["Broad Institute MLPCN Platelet Activation"]
        "Empty String"              | ""    | new JSONObject()                    | []
    }

    /**
     */
    void "test Search #label"() {

        when:
        def response = service.search(userInput)

        then:
        queryExecutorService.executeGetRequestJSON(_, _) >> {assayJson}
        elasticSearchService.elasticSearchQuery(_) >> {[:]}
        assert response == expectedResponse

        where:
        label                  | userInput                                             | assayJson      | expectedResponse
        "Sub structure Search" | "${StructureSearchType.SUB_STRUCTURE.description}:CC" | ["223", "224"] | [totalCompounds: 0, assays: [], compounds: [], compoundHeaderInfo: null, experiments: [], projects: []]
        "Exact match Search"   | "${StructureSearchType.EXACT_MATCH.description}:CC"   | ["223", "224"] | [totalCompounds: 0, assays: [], compounds: [], compoundHeaderInfo: null, experiments: [], projects: []]
        "Similarity Search"    | "${StructureSearchType.SIMILARITY.description}:CC"    | ["223", "224"] | [totalCompounds: 0, assays: [], compounds: [], compoundHeaderInfo: null, experiments: [], projects: []]
        "Regular Search"       | "Stuff"                                               | ["Stuff"]      | [totalCompounds: 0, assays: [], compounds: [], compoundHeaderInfo: null, experiments: [], projects: []]
        "Empty Search"         | ""                                                    | []             | [totalCompounds: 0, assays: [], compounds: [], compoundHeaderInfo: null, experiments: [], projects: []]
    }
    /**
     */
    void "test pre Process Search #label"() {

        when:
        String response = service.preprocessSearch(searchString)

        then:
        queryExecutorService.executeGetRequestJSON(_, _) >> {assayJson}
        assert response == expectedCompoundList

        where:
        label                  | searchString                                          | assayJson      | expectedCompoundList
        "Sub structure Search" | "${StructureSearchType.SUB_STRUCTURE.description}:CC" | ["223", "224"] | "223 224"
        "Exact match Search"   | "${StructureSearchType.EXACT_MATCH.description}:CC"   | ["223", "224"] | "223 224"
        "Similarity Search"    | "${StructureSearchType.SIMILARITY.description}:CC"    | ["223", "224"] | "223 224"
        "Regular Search"       | "Stuff"                                               | ["Stuff"]      | "Stuff"
        "Empty Search"         | ""                                                    | []             | ""
    }
    /**
     */
    void "test Show Compound #label"() {

        given:
        Compound compound = Mock()
        MolecularValue compoundValue = Mock()
        Value compoundProbeValue = Mock()
        when: "Client enters a CID and the showCompound method is called"
        final JSONObject response = service.showCompound(compoundId)
        then: "The elastic search service get CompoundDocument is called"
        queryServiceWrapper.getRestCompoundService() >> { restCompoundService }
        restCompoundService.get(_) >> {compound}
        1 * compound.getValues(_) >> {[]}
        1 * compound.getValue(Compound.MolecularData) >> {compoundValue}
        1 * compound.getValue(Compound.ProbeID) >> {compoundProbeValue}
        assert response == expectedCompoundList

        where:
        label                         | compoundId       | expectedCompoundList
        "Return an JSON Object"       | new Integer(872) | [probeId: "null", sids: [], cid: 872] as JSONObject
     }
}
