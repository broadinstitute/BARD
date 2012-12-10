package molspreadsheet

import bard.core.adapter.CompoundAdapter
import bard.core.rest.spring.AssayRestService
import bard.core.rest.spring.CompoundRestService
import bard.core.rest.spring.ExperimentRestService
import bard.core.rest.spring.ProjectRestService
import bard.core.rest.spring.assays.Assay
import bard.core.rest.spring.compounds.Compound
import bard.core.rest.spring.experiment.ExperimentSearch
import bard.core.rest.spring.experiment.ExperimentSearchResult
import bard.core.rest.spring.project.Project
import bard.core.rest.spring.project.ProjectResult
import bardqueryapi.IQueryService
import com.metasieve.shoppingcart.ShoppingCartService
import grails.test.mixin.TestFor
import querycart.QueryCartService
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

/**
 * Created with IntelliJ IDEA.
 * User: balexand
 * Date: 10/9/12
 * Time: 12:17 PM
 * To change this template use File | Settings | File Templates.
 */

@Unroll
@TestFor(MolecularSpreadSheetService)
class MolecularSpreadSheetServiceUnitSpec extends Specification {

    QueryCartService queryCartService
    ShoppingCartService shoppingCartService
    IQueryService queryService
    ExperimentRestService experimentRestService
    AssayRestService assayRestService
    CompoundRestService compoundRestService
    ProjectRestService projectRestService
    CompoundAdapter compoundAdapter = buildCompoundAdapter(6L)
    @Shared Map compoundAdapterMap = [compoundAdapters: [buildCompoundAdapter(6L)], facets: null, nHits: 0]


    void setup() {
        this.experimentRestService = Mock(ExperimentRestService)
        this.compoundRestService = Mock(CompoundRestService)
        this.projectRestService = Mock(ProjectRestService)
        this.assayRestService = Mock(AssayRestService)
        this.queryCartService = Mock(QueryCartService)
        this.shoppingCartService = Mock(ShoppingCartService)
        this.queryService = Mock(IQueryService)
        service.assayRestService = assayRestService
        service.compoundRestService = compoundRestService
        service.experimentRestService = experimentRestService
        service.projectRestService = projectRestService
        service.queryService = this.queryService
    }

    void "test prepareForExport #label"() {
        given:
        final MolSpreadSheetData molSpreadSheetData = new MolSpreadSheetData()

        when:
        molSpreadSheetData.mssHeaders << ['a']
        molSpreadSheetData.mssHeaders << ['b']
        molSpreadSheetData.mssHeaders << ['c']
        molSpreadSheetData.mssHeaders << ['e', 'f', 'g']
        MolSpreadSheetCell molSpreadSheetCell0 = new MolSpreadSheetCell("2", MolSpreadSheetCellType.image)
        molSpreadSheetCell0.spreadSheetActivityStorage = new SpreadSheetActivityStorage()
        MolSpreadSheetCell molSpreadSheetCell1 = new MolSpreadSheetCell("2", MolSpreadSheetCellType.string)
        molSpreadSheetCell1.spreadSheetActivityStorage = new SpreadSheetActivityStorage()
        MolSpreadSheetCell molSpreadSheetCell2 = new MolSpreadSheetCell("2.1", MolSpreadSheetCellType.numeric)
        molSpreadSheetCell2.spreadSheetActivityStorage = new SpreadSheetActivityStorage()
        molSpreadSheetData.mssData = [:]
        molSpreadSheetData.mssData.put("1_0", molSpreadSheetCell0)
        molSpreadSheetData.mssData.put("1_1", molSpreadSheetCell1)
        molSpreadSheetData.mssData.put("1_3", molSpreadSheetCell1)
        molSpreadSheetData.mssData.put("1_4", molSpreadSheetCell1)
        molSpreadSheetData.mssData.put("1_5", molSpreadSheetCell2)
        molSpreadSheetData.rowPointer[0L] = 0
        molSpreadSheetData.rowPointer[1L] = 1
        LinkedHashMap<String, Object> dataForExporting = service.prepareForExport(molSpreadSheetData)

        then:
        assertNotNull dataForExporting
        assertNotNull dataForExporting."labels"
        assert (dataForExporting["labels"]).size() == 6
        assertNotNull dataForExporting."fields"
        assert (dataForExporting["fields"]).size() == 5
        assertNotNull dataForExporting."data"
        assert (dataForExporting["data"]).size() == 2

    }

    void "test prepareForExport degenerate"() {
        given:
        final MolSpreadSheetData molSpreadSheetData = new MolSpreadSheetData()

        when:
        molSpreadSheetData.mssHeaders << ['a']
        molSpreadSheetData.mssHeaders << ['b']
        molSpreadSheetData.mssHeaders << ['c']
        molSpreadSheetData.mssData = [:]
        molSpreadSheetData.rowPointer[0L] = 0
        LinkedHashMap<String, Object> dataForExporting = service.prepareForExport(molSpreadSheetData)

        then:
        assertNotNull dataForExporting
        assertNotNull dataForExporting."labels"
        assert (dataForExporting["labels"]).size() == 3
        assertNotNull dataForExporting."fields"
        assert (dataForExporting["fields"]).size() == 2
        assertNotNull dataForExporting."data"
        assert (dataForExporting["data"]).size() == 1

    }

//    void "test findActivitiesForCompounds"() {
//        given:
//        Long experimentId = 2
//        String compoundETag = null
//        ExperimentData experimentData = new ExperimentData(activities: [new Activity(potency: 2.0), new Activity(potency: 3.0)])
//        //add a null value
//        experimentData.activities.add(null)
//        when:
//        List<SpreadSheetActivity> spreadSheetActivities = service.findActivitiesForCompounds(experimentId, compoundETag)
//        then:
//        experimentRestService.activities(_, _) >> {experimentData}
//        assert !spreadSheetActivities.isEmpty()
//    }
//    void "test findActivitiesForCompounds with skip and top"() {
//        given:
//        Long experimentId = 2
//        int skip = 0
//        int top = 2
//        String compoundETag = null
//        ExperimentData experimentData = new ExperimentData(activities: [new Activity(potency: 2.0), new Activity(potency: 3.0)])
//        //add a null value
//        experimentData.activities.add(null)
//        when:
//        List<SpreadSheetActivity> spreadSheetActivities = service.findActivitiesForCompounds(experimentId, compoundETag,top,skip)
//        then:
//        experimentRestService.activities(_, _,_,_) >> {experimentData}
//        assert !spreadSheetActivities.isEmpty()
//    }
    void "test assays To Experiments"() {
        given:
        Collection<Assay> assays = [new Assay(assayId: 2)]
        when:
        List<ExperimentSearch> experiments = service.assaysToExperiments(assays)
        then:
        assert experiments.isEmpty()
    }




    void "test prepareMapOfColumnsToAssay "() {
        when:
        final MolSpreadSheetData molSpreadSheetData = new MolSpreadSheetData()
        molSpreadSheetData.mssHeaders << ['a']
        molSpreadSheetData.mssHeaders << ['b']
        molSpreadSheetData.mssHeaders << ['c']
        molSpreadSheetData.mssHeaders << ['d']
        molSpreadSheetData.mssHeaders << ['e', 'f', 'g']
        service.prepareMapOfColumnsToAssay(molSpreadSheetData)
        molSpreadSheetData.experimentNameList << 'a'

        then: "we want to pull out the active values"
        assertNotNull molSpreadSheetData
        assert molSpreadSheetData.mapColumnsToAssay.size() == 7
    }

    /**
     * We tests the non-null case with an integration test
     */
    void "test findExperimentDataById Null Experiment"() {
        given:
        final Long experimentId = 2L
        final Integer top = 10
        final Integer skip = 0
        when:
        Map resultsMap = service.findExperimentDataById(experimentId, top, skip)
        then:
        experimentRestService.getExperimentById(_) >> {null}
        and:
        assert resultsMap
        assert resultsMap.experiment == null
        assert resultsMap.total == 0
        assert resultsMap.spreadSheetActivities.isEmpty()
        assert resultsMap.role == null

    }


    void "test null cmp iterator in retrieveImpliedCompoundsEtagFromAssaySpecification"() {
        given:
        final List<ExperimentSearch> experimentList = []
        experimentList << new ExperimentSearch()

        when:
        String eTag = service.retrieveImpliedCompoundsEtagFromAssaySpecification(experimentList)

        then:
        compoundRestService.newETag(_, _) >> { null }
        and:
        experimentRestService.compoundsForExperiment(_) >> {[123, 456]}

        assertNull eTag
    }

    void "test addFixedColumnData"() {
        given:
        final LinkedHashMap<String, String> mapForThisRow = [:]
        final MolSpreadSheetData molSpreadSheetData = new MolSpreadSheetData()
        final int rowCnt = 0
        when:
        service.addFixedColumnData(mapForThisRow, molSpreadSheetData, rowCnt)
        then:
        assert mapForThisRow.size() == 3
        assert mapForThisRow["molstruct"] == "Unknown smiles"
        assert mapForThisRow["cid"] == "-"
        assert mapForThisRow["c3"] == "-"


    }

    void "test assaysToExperiments with an empty list of assays"() {
        when:
        List<ExperimentSearch> experimentSearches = service.assaysToExperiments([])
        then:
        0 * assayRestService.findExperimentsByAssayId(_) >> {[new ExperimentSearch()]}
        assert !experimentSearches
    }

    void "test projectIdsToExperiments"() {
        given:
        final List<Long> pids = [2, 4]
        when:
        final List<ExperimentSearch> experimentSearches = service.projectIdsToExperiments(pids)
        then:
        projectRestService.searchProjectsByIds(_) >> {projectResult}
        expectedNumberOfMethodCalls * experimentRestService.searchExperimentsByIds(_) >> { new ExperimentSearchResult(experiments: [new ExperimentSearch()])}
        assert experimentSearches.size() == expectedSize
        where:
        label                              | projectResult                                         | expectedSize | expectedNumberOfMethodCalls
        "Return list of experiments"       | new ProjectResult(projects: [new Project(eids: [1])]) | 1            | 1
        "Return empty list of experiments" | null                                                  | 0            | 0
    }

    void "test projectsToExperiments #label"() {
        when:
        List<ExperimentSearch> experiments = service.projectsToExperiments(projectResult)
        then:
        expectedNumberOfMethodCalls * experimentRestService.searchExperimentsByIds(_) >> { new ExperimentSearchResult(experiments: [new ExperimentSearch()])}
        assert experiments.size() == expectedSize
        where:
        label                              | projectResult                                         | expectedSize | expectedNumberOfMethodCalls
        "With ExperimentSearchResult"      | new ProjectResult(projects: [new Project(eids: [1])]) | 1            | 1
        "With Null ExperimentSearchResult" | null                                                  | 0            | 0


    }

    void "test projectToExperiment #label"() {
        given:
        final List<Long> eids = [2, 4]
        final List<ExperimentSearch> allExperiments = []
        when:
        service.projectToExperiment(eids, allExperiments)
        then:
        experimentRestService.searchExperimentsByIds(_) >> {experimentSearchResult}
        allExperiments.size() == expectedSize
        where:
        label                              | experimentSearchResult                                            | expectedSize
        "With ExperimentSearchResult"      | new ExperimentSearchResult(experiments: [new ExperimentSearch()]) | 1
        "With Null ExperimentSearchResult" | null                                                              | 0
    }


    void "test populateMolSpreadSheetColumnMetadata when experiment list is empty"() {
        given: "we have an experiment"
        final MolSpreadSheetData molSpreadSheetData = new MolSpreadSheetData()
        final List<ExperimentSearch> experimentList = []

        when: "we want to pull out the active values"
        service.populateMolSpreadSheetColumnMetadata(molSpreadSheetData, experimentList)

        then: "prove that the active values are available"
        assertNotNull molSpreadSheetData
        assertNotNull molSpreadSheetData.mssHeaders

        assert molSpreadSheetData.mssHeaders.flatten().size() == 4
        assert molSpreadSheetData.mssHeaders.flatten().contains("Struct")
        assert molSpreadSheetData.mssHeaders.flatten().contains("CID")
        assert molSpreadSheetData.mssHeaders.flatten().contains("UNM Promiscuity Analysis")
        assert molSpreadSheetData.mssHeaders.flatten().contains("Active vs Tested across all Assay Definitions")
    }


    void "test populateMolSpreadSheetColumnMetadata when experiment list is empty and mssheader is null"() {
        given: "we have an experiment"
        final MolSpreadSheetData molSpreadSheetData = new MolSpreadSheetData()
        final List<ExperimentSearch> experimentList = []

        when: "we want to pull out the active values"
        service.populateMolSpreadSheetColumnMetadata(molSpreadSheetData, experimentList)

        then: "prove that the active values are available"
        assertNotNull molSpreadSheetData
        assertNotNull molSpreadSheetData.mssHeaders
        assert molSpreadSheetData.mssHeaders.flatten().size() == 4
        assert molSpreadSheetData.mssHeaders.flatten().contains("Struct")
        assert molSpreadSheetData.mssHeaders.flatten().contains("CID")
        assert molSpreadSheetData.mssHeaders.flatten().contains("UNM Promiscuity Analysis")
        assert molSpreadSheetData.mssHeaders.flatten().contains("Active vs Tested across all Assay Definitions")
    }


    void "test populateMolSpreadSheetColumnMetadata when experiment list is not empty"() {
        given: "we have an experiment"
        final MolSpreadSheetData molSpreadSheetData = new MolSpreadSheetData()
        final List<ExperimentSearch> experimentList = []
        experimentList << new ExperimentSearch(name: "a")
        experimentList << new ExperimentSearch(name: "b")
        experimentList << new ExperimentSearch(name: "c")

        when: "we want to pull out the active values"
        service.populateMolSpreadSheetColumnMetadata(molSpreadSheetData, experimentList)

        then: "prove that the active values are available"
        assertNotNull molSpreadSheetData
        assertNotNull molSpreadSheetData.mssHeaders

        assert molSpreadSheetData.mssHeaders.size() == 7
        assert molSpreadSheetData.mssHeaders.flatten().contains("Struct")
        assert molSpreadSheetData.mssHeaders.flatten().contains("CID")
        assert molSpreadSheetData.mssHeaders.flatten().contains("UNM Promiscuity Analysis")
    }




    void "test populateMolSpreadSheetRowMetadata"() {
        given: "we have an experiment"
        final MolSpreadSheetData molSpreadSheetData = new MolSpreadSheetData()
        molSpreadSheetData.rowPointer = new LinkedHashMap<Long, Integer>()
        molSpreadSheetData.mssData = new LinkedHashMap<String, MolSpreadSheetCell>()
        //  List<CartCompound> cartCompoundList = []
        //cartCompoundList.add(new CartCompound("c1ccccc1", "benzene", 47, 0, 0))
        Map<String, MolSpreadSheetCell> dataMap = [:]

        // final MolSpreadSheetData molSpreadSheetData, final Map compoundAdapterMap, Map<String, MolSpreadSheetCell> dataMap
        when: "we want to pull out the active values"
        service.populateMolSpreadSheetRowMetadata(molSpreadSheetData, [:], dataMap)

        then: "prove that the active values are available"
        assertNotNull molSpreadSheetData
        assertNotNull molSpreadSheetData.mssHeaders
        assert molSpreadSheetData.mssHeaders.flatten().size() == 0
    }


    void "test populateMolSpreadSheetRowMetadata compoundAdapterMap"() {
        given: "we have an experiment"
        final MolSpreadSheetData molSpreadSheetData = new MolSpreadSheetData()
        molSpreadSheetData.rowPointer = new LinkedHashMap<Long, Integer>()
        molSpreadSheetData.mssData = new LinkedHashMap<String, MolSpreadSheetCell>()
        Map<String, MolSpreadSheetCell> dataMap = [:]

        when: "we want to pull out the active values"
        service.populateMolSpreadSheetRowMetadata(molSpreadSheetData, compoundAdapterMap, dataMap)

        then: "prove that the active values are available"
        assertNotNull molSpreadSheetData
        assertNotNull molSpreadSheetData.mssHeaders
        assert molSpreadSheetData.mssHeaders.flatten().size() == 0
    }






    CompoundAdapter buildCompoundAdapter(final Long cid) {
        final Compound compound = new Compound()

        compound.smiles = 'c1ccccc1'
        compound.cid = cid
        compound.name = 'name'
        compound.numActiveAssay = new Integer("1")
        compound.numAssay = new Integer("5")
        return new CompoundAdapter(compound)
    }


}
