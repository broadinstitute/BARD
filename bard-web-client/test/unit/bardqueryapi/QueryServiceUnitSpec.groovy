package bardqueryapi

import bard.core.SearchParams
import bard.core.adapter.AssayAdapter
import bard.core.adapter.CompoundAdapter
import bard.core.adapter.ProjectAdapter
import bard.core.rest.spring.assays.Assay
import bard.core.rest.spring.assays.AssayResult
import bard.core.rest.spring.assays.ExpandedAssay
import bard.core.rest.spring.assays.ExpandedAssayResult
import bard.core.rest.spring.experiment.Activity
import bard.core.rest.spring.experiment.ExperimentSearch
import bard.core.rest.spring.experiment.ExperimentSearchResult
import bard.core.rest.spring.project.Project
import bard.core.rest.spring.project.ProjectExpanded
import bard.core.rest.spring.project.ProjectResult
import bard.core.rest.spring.util.StructureSearchParams
import bard.core.util.FilterTypes
import grails.test.mixin.TestFor
import org.apache.commons.lang.time.StopWatch
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll
import bard.core.rest.spring.*
import bard.core.rest.spring.compounds.*
import com.fasterxml.jackson.databind.ObjectMapper

/**
 * See the API for {@link grails.test.mixin.support.GrailsUnitTestMixin} for usage instructions
 */
@Unroll
@TestFor(QueryService)
class QueryServiceUnitSpec extends Specification {
    QueryHelperService queryHelperService
    AssayRestService assayRestService
    CompoundRestService compoundRestService
    ProjectRestService projectRestService
    ExperimentRestService experimentRestService
    SubstanceRestService substanceRestService
    @Shared ExpandedAssayResult expandedAssayResult = new ExpandedAssayResult()
    @Shared CompoundResult compoundResult = new CompoundResult()
    @Shared ExpandedAssay expandedAssay1 = new ExpandedAssay()
    @Shared ExpandedAssay expandedAssay2 = new ExpandedAssay()
    @Shared ProjectResult projectResult = new ProjectResult()
    @Shared Assay assay1 = new Assay(bardAssayId: 1, name: "A1")
    @Shared Assay assay2 = new Assay(bardAssayId: 2, name: "A2")
    @Shared Compound compound1 = new Compound(name: "C1")
    @Shared Compound compound2 = new Compound(name: "C2")
    @Shared ProjectExpanded project1 = new ProjectExpanded(name: "P1")
    @Shared Project project2 = new Project(bardProjectId: 2, name: "P2")
    @Shared Project project3 = new Project(bardProjectId: 3, name: "P3")
    @Shared Map compoundAdapterMap1 = [compoundAdapters: [], facets: null, nHits: 0, eTag: null]
    @Shared Map compoundAdapterMap2 = [compoundAdapters: [], facets: [], nHits: 0, eTag: null]
    @Shared Map assayAdapterMap1 = [assayAdapters: [new AssayAdapter(assay1)], facets: [], nHits: 0]
    @Shared Map assayAdapterMap2 = [assayAdapters: [], facets: null, nHits: 0]
    @Shared Map projectAdapterMap1 = [projectAdapters: [new ProjectAdapter(project1)], facets: null, nHits: 0]
    @Shared Map projectAdapterMap2 = [projectAdapters: [], facets: [], nHits: 0]
    @Shared CompoundSummary compoundSummary = new CompoundSummary(ntest: 2,
            testedExptdata: [new Activity(bardExptId: 1, eid: 1, bardAssayId: 1, bardProjId: [2]),
                    new Activity(bardExptId: 4, eid: 4, bardAssayId: 2, bardProjId: [3])],
            testedAssays: [assay1, assay2],
            nhit: 1,
            hitAssays: [assay1],
            hitExptdata: [new Activity(bardExptId: 1, eid: 1, bardAssayId: 1, bardProjId: [2])])

    @Shared CompoundSummary compoundSummary2
    @Shared ObjectMapper objectMapper = new ObjectMapper()
    final String COMPOUND_SUMMARY = """
{
   "testedExptdata" :
      [
         {
            "exptDataId" : "1.855944",
            "eid" : 0,
            "cid" : 443939,
            "sid" : 855944,
            "bardExptId" : 1,
            "bardAssayId" : 1,
            "capExptId" : 1,
            "capAssayId" : 1,
            "capProjId" :
               [
                  118
               ],
            "bardProjId" :
               [
                  18
               ],
            "resultJson" : "{\\"responseClass\\":\\"SP\\",\\"bardExptId\\":1,\\"capExptId\\":1,\\"bardAssayId\\":1,\\"capAssayId\\":1,\\"sid\\":855944,\\"cid\\":443939,\\"potency\\":null,\\"priorityElements\\":[{\\"displayName\\":\\"percent inhibition\\",\\"dictElemId\\":998,\\"value\\":\\"60.0\\"}],\\"rootElements\\":[{\\"displayName\\":\\"PubChem outcome\\",\\"dictElemId\\":896,\\"value\\":\\"Active\\",\\"childElements\\":[{\\"displayName\\":\\"PubChem activity score\\",\\"dictElemId\\":898,\\"value\\":\\"60.0\\"}]}],\\"projects\\":[{\\"bardProjId\\":18,\\"capProjId\\":118}]}",
            "runset" : "default",
            "outcome" : 2,
            "score" : 60,
            "classification" : null,
            "potency" : null,
            "readouts" : null,
            "resourcePath" : "/exptdata/225.855944"
         },
         {
            "exptDataId" : "2.855944",
            "eid" : 0,
            "cid" : 443939,
            "sid" : 855944,
            "bardExptId" : 2,
            "bardAssayId" : 2,
            "capExptId" : 2,
            "capAssayId" : 2,
            "capProjId" :
               [
                  75
               ],
            "bardProjId" :
               [
                  6
               ],
            "resultJson" : "{\\"responseClass\\":\\"CR_SER\\",\\"bardExptId\\":2,\\"capExptId\\":2,\\"bardAssayId\\":2,\\"capAssayId\\":2,\\"sid\\":855944,\\"cid\\":443939,\\"potency\\":7.0795,\\"priorityElements\\":[{\\"displayName\\":\\"AC50\\",\\"dictElemId\\":959,\\"value\\":\\"7.0795\\",\\"concResponseSeries\\":{\\"responseUnit\\":\\"percent activity\\",\\"testConcUnit\\":\\"uM\\",\\"crSeriesDictId\\":986,\\"concRespParams\\":{\\"s0\\":-2.5499,\\"sInf\\":-180.521,\\"hillCoef\\":4.9549,\\"logEc50\\":-5.15},\\"concRespPoints\\":[{\\"testConc\\":0.00368173,\\"value\\":\\"-0.3369\\"},{\\"testConc\\":0.0195624,\\"value\\":\\"4.6837\\"},{\\"testConc\\":0.10299,\\"value\\":\\"1.3235\\"},{\\"testConc\\":0.452287,\\"value\\":\\"-16.7802\\"},{\\"testConc\\":2.37064,\\"value\\":\\"-2.8465\\"},{\\"testConc\\":12.5394,\\"value\\":\\"-165.309\\"},{\\"testConc\\":67.5346,\\"value\\":\\"-1.5224\\"}]},\\"childElements\\":[{\\"displayName\\":\\"R-squared\\",\\"dictElemId\\":980,\\"value\\":\\"0.9877\\"},{\\"displayName\\":\\"QC analyst\\",\\"dictElemId\\":1476,\\"value\\":\\"QC'd by \\\\\\"Sigma Chemical Company\\\\\\"\\"},{\\"displayName\\":\\"comment\\",\\"dictElemId\\":1329,\\"value\\":\\"-2.1\\"},{\\"displayName\\":\\"curve-fit specification\\",\\"dictElemId\\":590,\\"value\\":\\"Partial curve; high efficacy\\"}]},{\\"displayName\\":\\"efficacy\\",\\"dictElemId\\":983,\\"value\\":\\"177.971\\"}],\\"rootElements\\":[{\\"displayName\\":\\"PubChem outcome\\",\\"dictElemId\\":896,\\"value\\":\\"Active\\",\\"childElements\\":[{\\"displayName\\":\\"PubChem activity score\\",\\"dictElemId\\":898,\\"value\\":\\"46.0\\"}]},{\\"displayName\\":\\"activity type\\",\\"dictElemId\\":1708,\\"value\\":\\"Inhibitor\\"}],\\"projects\\":[{\\"bardProjId\\":6,\\"capProjId\\":75}]}",
            "runset" : "default",
            "outcome" : 2,
            "score" : 46,
            "classification" : null,
            "potency" : 7.0795,
            "readouts" : null,
            "resourcePath" : "/exptdata/77.855944"
         }
      ],
   "testedAssays" :
      [
         {
            "bardAssayId" : 1,
            "capAssayId" : 1,
            "category" : 0,
            "summary" : 0,
            "assays" : 0,
            "classification" : 0,
            "name" : "Multiplexed dose response screen for small molecule regulators of Bcl-2 family protein interactions, specifically Bim-Bfl-1.",
            "source" : null,
            "grantNo" : null,
            "title" : "NP_004040 bcl-2-related protein A1 isoform 1 [Homo sapiens]; single protein format; protein-protein interaction assay; using measured value et al",
            "designedBy" : "NMMLSC",
            "deposited" : null,
            "updated" : "2013-04-30",
            "assayType" : "Regular",
            "assayStatus" : "Draft",
            "documents" :
               [
               ],
            "targets" :
               [
               ],
            "experiments" :
               [
                  223,
                  224,
                  225
               ],
            "projects" :
               [
                  18
               ],
            "minimumAnnotations" :
               {
                  "assay footprint" : "384-well plate",
                  "detection method type" : "fluorescence intensity",
                  "detection instrument name" : "HyperCyt high-throughput flow-cytometry system",
                  "assay format" : "single protein format",
                  "assay type" : "protein-protein interaction assay"
               },
            "score" : 1.0,
            "resourcePath" : "/assays/145"
         },
         {
            "bardAssayId" : 2,
            "capAssayId" : 2,
            "category" : 0,
            "summary" : 0,
            "assays" : 0,
            "classification" : 0,
            "name" : "Multiplexed dose response screen for small molecule regulators of Bcl-2 family protein interactions, specifically Bim-Bcl-W.",
            "source" : null,
            "grantNo" : null,
            "title" : "AAB09055 Bcl-w [Homo sapiens]; single protein format; protein-protein interaction assay; using measured value et al",
            "designedBy" : "NMMLSC",
            "deposited" : null,
            "updated" : "2013-05-07",
            "assayType" : "Regular",
            "assayStatus" : "Draft",
            "documents" :
               [
               ],
            "targets" :
               [
               ],
            "experiments" :
               [
                  232,
                  233
               ],
            "projects" :
               [
                  17
               ],
            "minimumAnnotations" :
               {
                  "assay footprint" : "384-well plate",
                  "detection method type" : "fluorescence intensity",
                  "detection instrument name" : "HyperCyt high-throughput flow-cytometry system",
                  "assay format" : "single protein format",
                  "assay type" : "protein-protein interaction assay"
               },
            "score" : 1.0,
            "resourcePath" : "/assays/149"
         }
      ],
   "nhit" : 1,
   "hitAssays" :
      [
         {
            "bardAssayId" : 1,
            "capAssayId" : 1,
            "category" : 0,
            "summary" : 0,
            "assays" : 0,
            "classification" : 0,
            "name" : "Multiplexed dose response screen for small molecule regulators of Bcl-2 family protein interactions, specifically Bim-Bfl-1.",
            "source" : null,
            "grantNo" : null,
            "title" : "NP_004040 bcl-2-related protein A1 isoform 1 [Homo sapiens]; single protein format; protein-protein interaction assay; using measured value et al",
            "designedBy" : "NMMLSC",
            "deposited" : null,
            "updated" : "2013-04-30",
            "assayType" : "Regular",
            "assayStatus" : "Draft",
            "documents" :
               [
               ],
            "targets" :
               [
               ],
            "experiments" :
               [
                  223,
                  224,
                  225
               ],
            "projects" :
               [
                  18
               ],
            "minimumAnnotations" :
               {
                  "assay footprint" : "384-well plate",
                  "detection method type" : "fluorescence intensity",
                  "detection instrument name" : "HyperCyt high-throughput flow-cytometry system",
                  "assay format" : "single protein format",
                  "assay type" : "protein-protein interaction assay"
               },
            "score" : 1.0,
            "resourcePath" : "/assays/145"
         }
      ],
   "ntest" : 2,
   "hitExptdata" :
      [
         {
            "exptDataId" : "1",
            "eid" : 0,
            "cid" : 443939,
            "sid" : 855944,
            "bardExptId" : 1,
            "bardAssayId" : 1,
            "capExptId" : 1,
            "capAssayId" : 1,
            "capProjId" :
               [
                  118
               ],
            "bardProjId" :
               [
                  18
               ],
            "resultJson" : "{\\"responseClass\\":\\"SP\\",\\"bardExptId\\":225,\\"capExptId\\":3527,\\"bardAssayId\\":145,\\"capAssayId\\":3325,\\"sid\\":855944,\\"cid\\":443939,\\"potency\\":null,\\"priorityElements\\":[{\\"displayName\\":\\"percent inhibition\\",\\"dictElemId\\":998,\\"value\\":\\"60.0\\"}],\\"rootElements\\":[{\\"displayName\\":\\"PubChem outcome\\",\\"dictElemId\\":896,\\"value\\":\\"Active\\",\\"childElements\\":[{\\"displayName\\":\\"PubChem activity score\\",\\"dictElemId\\":898,\\"value\\":\\"60.0\\"}]}],\\"projects\\":[{\\"bardProjId\\":18,\\"capProjId\\":118}]}",
            "runset" : "default",
            "outcome" : 2,
            "score" : 60,
            "classification" : null,
            "potency" : null,
            "readouts" : null,
            "resourcePath" : "/exptdata/225.855944"
         }
      ]
}
    """

    void setup() {
        compoundRestService = Mock(CompoundRestService)
        projectRestService = Mock(ProjectRestService)
        assayRestService = Mock(AssayRestService)
        experimentRestService = Mock(ExperimentRestService)
        substanceRestService = Mock(SubstanceRestService)
        queryHelperService = Mock(QueryHelperService)

        service.queryHelperService = queryHelperService
        service.assayRestService = assayRestService
        service.compoundRestService = compoundRestService
        service.projectRestService = projectRestService
        service.substanceRestService = substanceRestService
        service.experimentRestService = experimentRestService
        this.compoundSummary2= objectMapper.readValue(COMPOUND_SUMMARY, CompoundSummary.class)
    }

    void "test getSummaryForCompound"() {
        given:
        Long cid = new Long(222)
        when:
        CompoundSummary compoundSummary = service.getSummaryForCompound(cid)
        then:
        1 * compoundRestService.getSummaryForCompound(cid) >> {new CompoundSummary(nhit: 2)}
        assert compoundSummary

    }
    /**
     * We tests the non-null case with an integration test
     */
    void "test findExperimentDataById Null Experiment #label"() {
        given:
        final Long experimentId = 2L
        final Integer top = 10
        final Integer skip = 0
        when:
        Map resultsMap = service.findExperimentDataById(experimentId, top, skip, [bard.core.util.FilterTypes.TESTED])
        then:
        experimentRestService.getExperimentById(_) >> {null}
        and:
        assert resultsMap
        assert resultsMap.experiment == null
        assert resultsMap.total == 0
        assert resultsMap.activities.isEmpty()
        assert resultsMap.role == null
        where:
        label                 | normalizeYAxis
        "Normalized Y Axis"   | NormalizeAxis.Y_NORM_AXIS
        "DeNormalized Y Axis" | NormalizeAxis.Y_DENORM_AXIS
    }

    void "test findSubstancesByCid #label"() {
        when:
        List<Long> sids = service.findSubstancesByCid(cid)
        then:
        substanceRestService.findSubstancesByCid(cid) >> {foundSIDS}
        assert sids.size() == expectedSIDS.size()
        assert sids.size() == foundSIDS.size()


        where:
        label             | cid  | expectedSIDS | foundSIDS
        "CID has SIDS"    | 2722 | [2, 3]       | ["/substances/2", "/substances/3"]
        "CID has no SIDS" | 222  | []           | []
        "CID is null"     | null | []           | []


    }

    /**
     * {@link QueryService#showCompound(Long)}
     *
     */
    void "test Show Compound #label"() {

        when: "Client enters a CID and the showCompound method is called"
        CompoundAdapter compoundAdapter = service.showCompound(compoundId)
        then: "The CompoundDocument is called"
        this.compoundRestService.getCompoundById(_) >> {compound}
        if (compound) {
            assert compoundAdapter
            assert compoundAdapter.compound
        } else {
            assert !compoundAdapter
        }

        where:
        label                       | compoundId | compound
        "Return a Compound Adapter" | 872        | compound1
        "Unknown Compound"          | 872        | null
        "Null CompoundId"           | null       | null
    }
    /**
     * {@link QueryService#showProject(Long)}
     *
     */
    void "test Show Project - No Experiments associated to Project"() {

        given:
        List<Assay> assays = Mock(List)

        when:
        Map foundProjectAdapterMap = service.showProject(projectId)
        then:
        projectRestService.getProjectById(_) >> {project}
        projectRestService.findExperimentsByProjectId(_) >> {[]}
        projectRestService.findAssaysByProjectId(_) >> {assays}
        assert foundProjectAdapterMap
        ProjectAdapter foundProjectAdapter = foundProjectAdapterMap.projectAdapter
        assert foundProjectAdapter
        assert foundProjectAdapter.project
        assert !foundProjectAdapterMap.experiments

        where:
        label                                   | projectId | project
        "Project has no Experiment association" | 872       | project1

    }
    /**
     * {@link QueryService#showProject(Long)}
     *
     */
    void "test Show Project #label"() {

        given:
        final ExperimentSearch experiment = new ExperimentSearch()
        experiment.classification = 0
        List<ExperimentSearch> experiments = [experiment]
        List<Assay> assays = Mock(List)

        when: "Client enters a project ID and the showProject method is called"
        Map foundProjectAdapterMap = service.showProject(projectId)
        then: "The ProjectSearchResult document is displayed"
        projectRestService.getProjectById(_) >> {project}
        projectRestService.findExperimentsByProjectId(_) >> {experiments}
        projectRestService.findAssaysByProjectId(_) >> {assays}

        if (project) {
            assert foundProjectAdapterMap
            ProjectAdapter foundProjectAdapter = foundProjectAdapterMap.projectAdapter
            assert foundProjectAdapter
            assert foundProjectAdapter.project
        } else {
            assert !foundProjectAdapterMap
        }

        where:
        label                                  | projectId | project
        "Return a ProjectSearchResult Adapter" | 872       | project1
        "Unknown ProjectSearchResult"          | 872       | null
        "Null projectId"                       | null      | null
    }
    /**
     * {@link QueryService#showAssay(Long)}
     *
     */
    void "test Show Assay #label"() {
        given:
        ExpandedAssay expandedAssay = new ExpandedAssay()
        expandedAssay.assayId = assayId
        when: "Client enters a assay ID and the showAssay method is called"
        Map foundAssayMap = service.showAssay(assayId)
        then: "The Assay document is displayed"
        numberOfExceptedCalls * assayRestService.getAssayById(_) >> {expandedAssay}
        numberOfExceptedCalls * assayRestService.findAnnotations(_)
        assert foundAssayMap
        assert foundAssayMap.assayAdapter

        where:
        label                     | assayId | assay  | numberOfExceptedCalls
        "Return an Assay Adapter" | 872     | assay1 | 1
        "Unknown Assay"           | 872     | null   | 1
    }
    /**
     * {@link QueryService#showAssay(Long)}
     *
     */
    void "test Show Assay - Null AssayId"() {
        given:
        ExpandedAssay expandedAssay = new ExpandedAssay()
        when: "Client enters a assay ID and the showAssay method is called"
        Map foundAssayMap = service.showAssay(assayId)
        then: "The Assay document is displayed"
        0 * assayRestService.getAssayById(_) >> {expandedAssay}
        assert !foundAssayMap

        where:
        label           | assayId | assay
        "Null assay Id" | null    | null
    }

    void "test findCompoundsByCIDs - Non existing ids"() {
        when:
        Map responseMap = service.findCompoundsByCIDs([2, 3])
        then:
        1 * compoundRestService.searchCompoundsByIds(_) >> {null}
        and:
        assert responseMap
        assert responseMap.nHits == 0
        assert !responseMap.facets
        assert responseMap.compoundAdapters.size() == 0
    }
    /**
     * {@link QueryService#findCompoundsByCIDs(List, List)}
     *
     */
    void "test findCompoundsByCIDs #label"() {
        given:
        compoundResult.compounds = compound
        when:
        Map responseMap = service.findCompoundsByCIDs(cids)
        then:
        expectedNumberOfCalls * compoundRestService.searchCompoundsByIds(_) >> {compoundResult}
        and:
        assert responseMap
        assert responseMap.nHits == expectedNumberOfHits
        assert !responseMap.facets
        assert responseMap.compoundAdapters.size() == expectedNumberOfHits
        where:
        label                    | cids                           | compound               | expectedNumberOfCalls | expectedNumberOfHits
        "Multiple Compound Ids"  | [new Long(872), new Long(111)] | [compound1, compound2] | 1                     | 2
        "Unknown Compound Id"    | [new Long(802)]                | null                   | 1                     | 0
        "Single Compound Id"     | [new Long(872)]                | [compound1]            | 1                     | 1
        "Empty Compound Id list" | []                             | null                   | 0                     | 0

    }
    /**
     * {@link QueryService#findAssaysByADIDs(List, List)}
     *
     */
    void "test findAssaysByADIDs #label"() {
        given:
        expandedAssayResult.assays = assay
        when:
        Map responseMap = service.findAssaysByADIDs(assayIds)
        then:
        queryHelperService.assaysToAdapters(_, _) >> {assayAdapters}
        expectedNumberOfCalls * assayRestService.searchAssaysByIds(_) >> {expandedAssayResult}
        and:
        assert responseMap
        assert responseMap.nHits == expectedNumberOfHits
        assert !responseMap.facets
        assert responseMap.assayAdapters.size() == expectedNumberOfHits
        where:
        label                 | assayIds                       | assay                            | expectedNumberOfCalls | expectedNumberOfHits | assayAdapters
        "Multiple Assay Ids"  | [new Long(872), new Long(111)] | [expandedAssay1, expandedAssay2] | 1                     | 2                    | [new AssayAdapter(expandedAssay1), new AssayAdapter(expandedAssay2)]
        "Unknown Assay Id"    | [new Long(802)]                | null                             | 1                     | 0                    | null
        "Single Assay Id"     | [new Long(872)]                | [expandedAssay1]                 | 1                     | 1                    | [new AssayAdapter(expandedAssay1)]
        "Empty Assay Id list" | []                             | null                             | 0                     | 0                    | null

    }
    /**
     * {@link QueryService#findAssaysByADIDs(List, List)}
     *
     */
    void "test findAssaysByADIDs - Non existing ids"() {
        when:
        Map responseMap = service.findAssaysByADIDs([2, 3])
        then:
        1 * assayRestService.searchAssaysByIds(_) >> {null}
        and:
        assert responseMap
        assert responseMap.nHits == 0
        assert !responseMap.facets
        assert responseMap.assayAdapters.size() == 0
    }

    /**
     * {@link QueryService#findProjectsByPIDs(List, List)}
     *
     */
    void "test findProjectsByPIDs - Non existing ids"() {
        when:
        Map responseMap = service.findProjectsByPIDs([2, 3])
        then:
        1 * projectRestService.searchProjectsByIds(_) >> {null}
        and:
        assert responseMap
        assert responseMap.nHits == 0
        assert !responseMap.facets
        assert responseMap.projectAdapters.size() == 0
    }
    /**
     * {@link QueryService#findProjectsByPIDs(List, List)}
     *
     */
    void "test findProjectsByPIDs #label"() {
        given:
        projectResult.projects = project
        when:
        Map responseMap = service.findProjectsByPIDs(projectIds)
        then:
        queryHelperService.projectsToAdapters(_) >> {projectAdapters}
        expectedNumberOfCalls * projectRestService.searchProjectsByIds(_) >> {projectResult}
        and:
        assert responseMap
        assert responseMap.nHits == expectedNumberOfHits
        assert !responseMap.facets
        assert responseMap.projectAdapters.size() == expectedNumberOfHits
        where:
        label                               | projectIds                     | project              | expectedNumberOfCalls | expectedNumberOfHits | projectAdapters
        "Multiple ProjectSearchResult Ids"  | [new Long(872), new Long(111)] | [project1, project2] | 1                     | 2                    | [new ProjectAdapter(project1), new ProjectAdapter(project2)]
        "Unknown ProjectSearchResult Id"    | [new Long(802)]                | null                 | 1                     | 0                    | null
        "Single ProjectSearchResult Id"     | [new Long(872)]                | [project1]           | 1                     | 1                    | [new ProjectAdapter(project2)]
        "Empty ProjectSearchResult Id list" | []                             | null                 | 0                     | 0                    | null

    }

    void "test showProbeList"() {
        when:
        Map map = service.showProbeList()
        then:
        compoundRestService.findCompoundsByETag(_) >> {new CompoundResult(compounds: [new Compound()])}
        queryHelperService.compoundsToAdapters(_) >> {[new CompoundAdapter()]}
        assert map

    }
    /**
     * {@link QueryService#structureSearch(String, bard.core.rest.spring.util.StructureSearchParams.Type)}
     *
     */
    void "test Structure Search No Filters #label"() {
        given:
        final CompoundResult expandedCompoundResult = new CompoundResult(compounds: [new Compound(smiles: smiles)])
        when:
        service.structureSearch(smiles, structureSearchParamsType, [], 90, 10, 0, 10)
        then:
        compoundRestService.structureSearch(_) >> {expandedCompoundResult}

        where:
        label                    | structureSearchParamsType                 | smiles
        "Sub structure Search"   | StructureSearchParams.Type.Substructure   | "CC"
        "Exact match Search"     | StructureSearchParams.Type.Exact          | "CC"
        "Similarity Search"      | StructureSearchParams.Type.Similarity     | "CC"
        "Super structure search" | StructureSearchParams.Type.Superstructure | "CC"
    }
    /**
     * {@link QueryService#structureSearch(String, StructureSearchParams.Type)}
     *
     */
    void "test Structure Search Empty Smiles"() {
        given:
        CompoundResult compoundResult = Mock(CompoundResult)

        when:
        final Map searchResults = service.structureSearch("", StructureSearchParams.Type.Substructure)
        then:
        _ * compoundRestService.structureSearch(_, _) >> {compoundResult}
        assert searchResults.nHits == -1
        assert searchResults.compoundAdapters.isEmpty()
        assert searchResults.facets.isEmpty()
    }
    /**
     * {@link bardqueryapi.IQueryService#structureSearch}
     *
     */
    void "test Structure Search #label"() {
        given:
        CompoundResult compoundResult = Mock(CompoundResult)
        List<SearchFilter> searchFilters = [new SearchFilter(filterName: "a", filterValue: "b")]

        List<String[]> filters = [["a", "b"] as String[]]
        when:
        service.structureSearch(smiles, structureSearchParamsType, searchFilters)
        then:
        queryHelperService.convertSearchFiltersToFilters(_) >> {filters}
        compoundRestService.structureSearch(_) >> {compoundResult}

        where:
        label                    | structureSearchParamsType                 | smiles
        "Sub structure Search"   | StructureSearchParams.Type.Substructure   | "CC"
        "Exact match Search"     | StructureSearchParams.Type.Exact          | "CC"
        "Similarity Search"      | StructureSearchParams.Type.Similarity     | "CC"
        "Super structure search" | StructureSearchParams.Type.Superstructure | "CC"
    }

    /**
     * {@link QueryService#findCompoundsByTextSearch(String, Integer, Integer, List)}
     *
     */
    void "test Find Compounds By Text Search #label"() {
        given:
        StopWatch sw = Mock(StopWatch)
        final CompoundResult compoundResult = Mock(CompoundResult)
        when:
        Map map = service.findCompoundsByTextSearch(searchString, 10, 0, [])
        then:
        queryHelperService.startStopWatch() >> {sw}
        queryHelperService.stopStopWatch(_, _) >> {}
        compoundRestService.findCompoundsByFreeTextSearch(_) >> {compoundResult}
        queryHelperService.stripCustomStringFromSearchString(_) >> {"stuff"}
        queryHelperService.constructSearchParams(_, _, _, _) >> { new SearchParams(searchString)}
        queryHelperService.compoundsToAdapters(_) >> {[new CompoundAdapter(compound1)]}
        assert map == foundMap
        where:
        searchString         | foundMap
        "Some Search String" | compoundAdapterMap1
        ""                   | compoundAdapterMap2

    }

    /**
     * {@link QueryService#searchCompoundsByCids}
     *
     */
    void "test Find Compounds by CIds #label"() {
        given:
        final CompoundResult compoundResult = Mock(CompoundResult)
        when:
        Map map = service.searchCompoundsByCids(cids, 10, 0, [])
        then:

        compoundRestService.searchCompoundsByCids(_, _) >> {compoundResult}
        queryHelperService.constructSearchParams(_, _, _, _) >> { new SearchParams("")}
        queryHelperService.compoundsToAdapters(_) >> {compoundAdapter}
        assert map.compoundAdapters.size() == foundMap.compoundAdapters.size()
        where:
        label                | cids      | foundMap            | compoundAdapter
        "With Cap Ids"       | [1, 2, 3] | compoundAdapterMap1 | [new CompoundAdapter(compound1)]
        "With empty Cap IDs" | []        | compoundAdapterMap2 | null

    }
    /**
     * {@link QueryService#findCompoundsByTextSearch(String)}
     *
     */
    void "test Find Compounds By Text Search with defaults #searchString"() {
        given:
        StopWatch sw = Mock(StopWatch)
        final CompoundResult compoundResult = Mock(CompoundResult)
        when:
        Map map = service.findCompoundsByTextSearch(searchString)
        then:
        queryHelperService.startStopWatch() >> {sw}
        queryHelperService.stopStopWatch(_, _) >> {}
        compoundRestService.findCompoundsByFreeTextSearch(_) >> {compoundResult}
        queryHelperService.stripCustomStringFromSearchString(_) >> {"stuff"}
        queryHelperService.constructSearchParams(_, _, _, _) >> { new SearchParams(searchString)}
        queryHelperService.compoundsToAdapters(_) >> {[new CompoundAdapter(compound1)]}
        assert map == foundMap
        where:
        searchString         | foundMap
        "Some Search String" | compoundAdapterMap1
        ""                   | compoundAdapterMap2

    }
    /**
     * {@link QueryService#findAssaysByTextSearch(String)}
     *
     */
    void "test Find Assays By Text Search with defaults #searchString"() {
        given:
        StopWatch sw = Mock(StopWatch)
        AssayResult assayResult = new AssayResult()
        when:
        Map map = service.findAssaysByTextSearch(searchString)
        then:
        queryHelperService.startStopWatch() >> {sw}
        queryHelperService.stopStopWatch(_, _) >> {}
        assayRestService.findAssaysByFreeTextSearch(_) >> {assayResult}
        queryHelperService.stripCustomStringFromSearchString(_) >> {"stuff"}
        queryHelperService.constructSearchParams(_, _, _, _) >> { new SearchParams(searchString)}
        queryHelperService.assaysToAdapters(_) >> {assayAdapter}
        assert map.assayAdapters.size() == foundMap.assayAdapters.size()
        where:
        searchString         | foundMap         | assayAdapter
        "Some Search String" | assayAdapterMap1 | [new AssayAdapter(assay1)]
        ""                   | assayAdapterMap2 | null

    }
    /**
     * {@link QueryService#findAssaysByCapIds}
     *
     */
    void "test Find Assays By Cap Ids #label"() {
        given:
        AssayResult assayResult = Mock(AssayResult)
        when:
        Map map = service.findAssaysByCapIds(capIds, 10, 0, [])
        then:

        assayRestService.searchAssaysByCapIds(_, _) >> {assayResult}
        queryHelperService.constructSearchParams(_, _, _, _) >> { new SearchParams("")}
        queryHelperService.assaysToAdapters(_) >> {[assayAdapter]}
        assert map.assayAdapters.size() == foundMap.assayAdapters.size()
        where:
        label                | capIds    | foundMap         | assayAdapter
        "With Cap Ids"       | [1, 2, 3] | assayAdapterMap1 | [new AssayAdapter(assay1)]
        "With empty Cap IDs" | []        | assayAdapterMap2 | null

    }
    /**
     * {@link QueryService#findAssaysByTextSearch(String, Integer, Integer, List)}
     *
     */
    void "test Find Assays By Text Search #searchString"() {
        given:
        StopWatch sw = Mock(StopWatch)
        AssayResult assayResult = Mock(AssayResult)
        when:
        Map map = service.findAssaysByTextSearch(searchString, 10, 0, [])
        then:
        queryHelperService.startStopWatch() >> {sw}
        queryHelperService.stopStopWatch(_, _) >> {}
        assayRestService.findAssaysByFreeTextSearch(_) >> {assayResult}
        queryHelperService.stripCustomStringFromSearchString(_) >> {"stuff"}
        queryHelperService.constructSearchParams(_, _, _, _) >> { new SearchParams(searchString)}
        queryHelperService.assaysToAdapters(_) >> {[assayAdapter]}
        assert map.assayAdapters.size() == foundMap.assayAdapters.size()
        where:
        searchString         | foundMap         | assayAdapter
        "Some Search String" | assayAdapterMap1 | [new AssayAdapter(assay1)]
        ""                   | assayAdapterMap2 | null

    }
    /**
     * {@link QueryService#findProjectsByTextSearch(String, Integer, Integer, List)}
     *
     */
    void "test Find Projects By Text Search"() {
        given:
        StopWatch sw = Mock(StopWatch)
        ProjectResult projectResult = Mock(ProjectResult)
        when:
        Map map = service.findProjectsByTextSearch(searchString, 10, 0, [])
        then:
        _ * queryHelperService.startStopWatch() >> {sw}
        _ * queryHelperService.stopStopWatch(_, _) >> {}
        _ * projectRestService.findProjectsByFreeTextSearch(_) >> {projectResult}
        _ * queryHelperService.stripCustomStringFromSearchString(_) >> {"stuff"}
        _ * queryHelperService.constructSearchParams(_, _, _, _) >> { new SearchParams(searchString)}
        _ * queryHelperService.projectsToAdapters(_) >> {projectAdapter}
        assert map.projectAdapters.size() == foundMap.projectAdapters.size()
        where:
        searchString         | foundMap           | projectAdapter
        "Some Search String" | projectAdapterMap1 | [new ProjectAdapter(project1)]
        ""                   | projectAdapterMap2 | null
    }
    /**
     * {@link QueryService#findProjectsByCapIds}
     *
     */
    void "test Find Projects by CAP IDs #label"() {
        given:
        ProjectResult projectResult = Mock(ProjectResult)
        when:
        Map map = service.findProjectsByCapIds(capIds, 10, 0, [])
        then:
        _ * projectRestService.searchProjectsByCapIds(_, _) >> {projectResult}
        _ * queryHelperService.constructSearchParams(_, _, _, _) >> { new SearchParams("")}
        _ * queryHelperService.projectsToAdapters(_) >> {projectAdapter}
        assert map.projectAdapters.size() == foundMap.projectAdapters.size()
        where:
        label               | capIds    | foundMap           | projectAdapter
        "With Cap IDs"      | [123, 34] | projectAdapterMap1 | [new ProjectAdapter(project1)]
        "With Empty CapIds" | []        | projectAdapterMap2 | null
    }
    /**
     * {@link QueryService#findProjectsByTextSearch(String)}
     *
     */
    void "test Find Projects By Text Search with defaults"() {
        given:
        StopWatch sw = Mock(StopWatch)
        ProjectResult projectResult = Mock(ProjectResult)
        when:
        Map map = service.findProjectsByTextSearch(searchString)
        then:
        _ * queryHelperService.startStopWatch() >> {sw}
        _ * queryHelperService.stopStopWatch(_, _) >> {}
        _ * projectRestService.findProjectsByFreeTextSearch(_) >> {projectResult}
        _ * queryHelperService.stripCustomStringFromSearchString(_) >> {"stuff"}
        _ * queryHelperService.constructSearchParams(_, _, _, _) >> { new SearchParams(searchString)}
        _ * queryHelperService.projectsToAdapters(_) >> {projectAdapter}
        assert map.projectAdapters.size() == foundMap.projectAdapters.size()
        where:
        searchString         | foundMap           | projectAdapter
        "Some Search String" | projectAdapterMap1 | [new ProjectAdapter(project1)]
        ""                   | projectAdapterMap2 | null
    }
    /**
     * {@link QueryService#autoComplete(String)}
     *
     */
    void "test auto Complete"() {
        given:
        Map map = [a: "b"]
        when:
        List list = service.autoComplete("Some Search String")
        then:
        assayRestService.suggest(_) >> {map}
        queryHelperService.autoComplete(_, _) >> {[map]}
        assert list
    }
    /**
     * {@link QueryService#findFiltersInSearchBox(List, String)}
     */
    void "test find Filters in search box"() {
        when:
        List<SearchFilter> searchFilters = []
        service.findFiltersInSearchBox(searchFilters, "gobp_term:DNA Repair")
        then:
        queryHelperService.findFiltersInSearchBox(_, _) >> {}
    }

    void "test findPromiscuityForCID #label"() {
        when:
        final Map promiscuityMap = service.findPromiscuityForCID(cid)
        then:
        compoundRestService.findPromiscuityForCompound(_) >> {promiscuity}

        assert promiscuityMap.status == expectedStatus
        assert promiscuityMap.message == expectedMessage
        assert promiscuityMap.promiscuityScore?.cid == promiscuity?.cid
        where:
        label                              | cid   | promiscuity                | expectedStatus | expectedMessage
        "Returns a Promiscuity Score"      | 1234  | new Promiscuity(cid: 1234) | 200            | "Success"
        "Returns a null Promiscuity Score" | 23435 | null                       | 404            | "Error getting Promiscuity Score for 23435"


    }

    void "test findPromiscuityScoreForCID #label"() {
        when:
        final Map promiscuityScoreMap = service.findPromiscuityScoreForCID(cid)
        then:
        compoundRestService.findPromiscuityScoreForCompound(_) >> {promiscuityScore}

        assert promiscuityScoreMap.status == expectedStatus
        assert promiscuityScoreMap.message == expectedMessage
        assert promiscuityScoreMap.promiscuityScore?.cid == promiscuityScore?.cid
        where:
        label                              | cid   | promiscuityScore                | expectedStatus | expectedMessage
        "Returns a Promiscuity Score"      | 1234  | new PromiscuityScore(cid: 1234) | 200            | "Success"
        "Returns a null Promiscuity Score" | 23435 | null                            | 404            | "Error getting Promiscuity Score for 23435"


    }

    void "test createCompoundBioActivitySummaryDataTable #label"() {
        when:
        final TableModel tableModel = service.createCompoundBioActivitySummaryDataTable(cid, groupBy, filterTypes, [], new SearchParams(top: 10, skip: 0))

        then:
        this.compoundRestService.getSummaryForCompound(cid) >> {this.compoundSummary2}
        this.experimentRestService.searchExperimentsByIds(_) >> {new ExperimentSearchResult(experiments: [new ExperimentSearch(bardExptId: 1L), new ExperimentSearch(bardExptId: 2L)])}
        this.projectRestService.searchProjectsByIds(_) >> {new ProjectResult(projects: [new Project(bardProjectId: 1L), new Project(bardProjectId: 2L)])}
        this.queryHelperService.projectsToAdapters(_) >>> [[new ProjectAdapter(project2)], [new ProjectAdapter(project3)]]
        this.compoundRestService.getSummaryForCompound(_) >> {this.compoundSummary2}

        assert tableModel.columnHeaders.size() == 2
        assert tableModel.data.size() == expectedTableModelDataSize
        assert tableModel.data.first().first().class == expectedResourceType

        where:
        label                            | cid  | groupBy              | filterTypes          | expectedTableModelDataSize | expectedResourceType
        "group-by assay, tested"         | 1234 | GroupByTypes.ASSAY   | [FilterTypes.TESTED] | 2                          | AssayValue
        "group-by assay, actives-only"   | 1234 | GroupByTypes.ASSAY   | []                   | 1                          | AssayValue
        "group-by project, tested"       | 1234 | GroupByTypes.PROJECT | [FilterTypes.TESTED] | 2                          | ProjectValue
        "group-by project, actives-pnly" | 1234 | GroupByTypes.PROJECT | []                   | 1                          | ProjectValue
    }
}
