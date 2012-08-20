package bardqueryapi

import bard.core.adapter.CompoundAdapter
import bard.core.rest.RESTAssayService
import bard.core.rest.RESTCompoundService
import bard.core.rest.RESTExperimentService
import bard.core.rest.RESTProjectService
import elasticsearchplugin.ElasticSearchService
import elasticsearchplugin.QueryExecutorService
import grails.test.mixin.TestFor
import spock.lang.Specification
import wslite.json.JSONObject
import bard.core.*
import spock.lang.Unroll

/**
 * See the API for {@link grails.test.mixin.support.GrailsUnitTestMixin} for usage instructions
 */
@Unroll
@TestFor(QueryService)
class QueryServiceUnitSpec extends Specification {
    QueryServiceWrapper queryServiceWrapper
    QueryExecutorService queryExecutorService
    ElasticSearchService elasticSearchService
    RESTCompoundService restCompoundService
    RESTExperimentService restExperimentService
    RESTProjectService restProjectService
    RESTAssayService restAssayService
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
        queryExecutorService = Mock(QueryExecutorService.class)
        restCompoundService = Mock(RESTCompoundService.class)
        restExperimentService = Mock(RESTExperimentService.class)
        restProjectService = Mock(RESTProjectService.class)
        restAssayService = Mock(RESTAssayService.class)
        elasticSearchService = Mock(ElasticSearchService.class)
        queryServiceWrapper = Mock(QueryServiceWrapper.class)
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
        Compound compound = Mock(Compound.class)
        when: "Client enters a CID and the showCompound method is called"
        CompoundAdapter compoundAdapter = service.showCompound(compoundId)
        then: "The CompoundDocument is called"
        queryServiceWrapper.getRestCompoundService() >> { restCompoundService }
        restCompoundService.get(_) >> {compound}
        assert compoundAdapter
        assert compoundAdapter.compound

        where:
        label                       | compoundId
        "Return a Compound Adapter" | new Integer(872)
    }

    void "test Show Experiment"() {
        given:
        Experiment experiment = Mock(Experiment.class)
        when: "Client enters an experiment ID and the showExperiment method is called"
        Experiment foundExperiment = service.showExperiment(experimentId)
        then: "The Experiment document is displayed"
        queryServiceWrapper.getRestExperimentService() >> { restExperimentService }
        restExperimentService.get(_) >> {experiment}
        assert foundExperiment

        where:
        label                  | experimentId
        "Return an Experiment" | new Integer(872)

    }

    void "test Show Project"() {
        given:
        Project project = Mock(Project.class)
        when: "Client enters a project ID and the showProject method is called"
        Project foundProject = service.showProject(projectId)
        then: "The Project document is displayed"
        queryServiceWrapper.getRestProjectService() >> { restProjectService }
        restProjectService.get(_) >> {project}
        assert foundProject

        where:
        label              | projectId
        "Return a Project" | new Integer(872)

    }

    void "test Show Assay"() {
        given:
        Assay assay = Mock(Assay.class)
        when: "Client enters a assay ID and the showAssay method is called"
        Assay foundAssay = service.showAssay(assayId)
        then: "The Assay document is displayed"
        queryServiceWrapper.getRestAssayService() >> { restAssayService }
        restAssayService.get(_) >> {assay}
        assert foundAssay

        where:
        label              | assayId
        "Return a Project" | new Integer(872)

    }

    void "test Structure Search #label"() {
        given:
        ServiceIterator<Compound> iter = Mock(ServiceIterator.class)
        when:
         service.structureSearch(smiles, structureSearchParamsType)
        then:
        queryServiceWrapper.getRestCompoundService() >> { restCompoundService }
        restCompoundService.structureSearch(_) >> {iter}

        where:
        label                    | structureSearchParamsType                 | smiles
        "Sub structure Search"   | StructureSearchParams.Type.Substructure   | "CC"
        "Exact match Search"     | StructureSearchParams.Type.Exact          | "CC"
        "Similarity Search"      | StructureSearchParams.Type.Similarity     | "CC"
        "Super structure search" | StructureSearchParams.Type.Superstructure | "CC"
    }
}
