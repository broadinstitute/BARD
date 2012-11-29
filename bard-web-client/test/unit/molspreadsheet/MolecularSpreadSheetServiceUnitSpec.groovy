package molspreadsheet

import bard.core.adapter.CompoundAdapter
import bard.core.rest.spring.assays.Assay
import bard.core.rest.spring.compounds.Compound
import bard.core.rest.spring.experiment.Activity
import bard.core.rest.spring.experiment.ExperimentData
import bard.core.rest.spring.experiment.ExperimentSearch
import bardqueryapi.BardWebInterfaceControllerUnitSpec
import bardqueryapi.IQueryService
import com.metasieve.shoppingcart.ShoppingCartService
import grails.test.mixin.TestFor
import querycart.CartCompound
import querycart.QueryCartService
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll
import bard.core.rest.spring.*

/**
 * Created with IntelliJ IDEA.
 * User: balexand
 * Date: 10/9/12
 * Time: 12:17 PM
 * To change this template use File | Settings | File Templates.
 */

@Unroll
@TestFor(MolecularSpreadSheetService)
@Mixin(BardWebInterfaceControllerUnitSpec)
class MolecularSpreadSheetServiceUnitSpec extends Specification {

    QueryCartService queryCartService
    RestCombinedService restCombinedService
    ShoppingCartService shoppingCartService
    IQueryService queryService
    ExperimentRestService experimentRestService
    AssayRestService assayRestService
    CompoundRestService compoundRestService
    ProjectRestService projectRestService
    CompoundAdapter compoundAdapter = buildCompoundAdapter(6L)
    @Shared Map compoundAdapterMap = [compoundAdapters: [buildCompoundAdapter(6L)], facets: null, nHits: 0]


    void setup() {
        compoundAdapter.metaClass.structureSMILES = 'c1ccccc1'
        compoundAdapter.metaClass.pubChemCID = 1 as Long
        compoundAdapter.metaClass.name = 'name'
        this.experimentRestService = Mock(ExperimentRestService)
        this.compoundRestService = Mock(CompoundRestService)
        this.projectRestService = Mock(ProjectRestService)
        this.assayRestService = Mock(AssayRestService)
        this.queryCartService = Mock(QueryCartService)
        this.shoppingCartService = Mock(ShoppingCartService)
        this.queryService = Mock(IQueryService)
        this.restCombinedService = Mock(RestCombinedService)
        service.assayRestService = assayRestService
        service.compoundRestService = compoundRestService
        service.experimentRestService = experimentRestService
        service.projectRestService = projectRestService
        service.queryService = this.queryService
        service.restCombinedService = this.restCombinedService


    }

    void tearDown() {
        // Tear down logic here
    }





    void "test prepareForExport #label"() {
        given:
        MolSpreadSheetData molSpreadSheetData = new MolSpreadSheetData()

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
        MolSpreadSheetData molSpreadSheetData = new MolSpreadSheetData()

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




    void "test findActivitiesForCompounds #label"() {
        given:
        Long experimentId = 2
        String compoundETag = null
        ExperimentData experimentData = new ExperimentData(activities: [new Activity(potency: 2.0), new Activity(potency: 3.0)])
        //add a null value
        experimentData.activities.add(null)
        when:
        List<SpreadSheetActivity> spreadSheetActivities = service.findActivitiesForCompounds(experimentId, compoundETag)
        then:
        experimentRestService.activities(_, _) >> {experimentData}
        assert !spreadSheetActivities.isEmpty()
    }

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
        restCombinedService.compounds(_) >> {[123, 456]}

        assertNull eTag
    }

    void "test extractActivityValuesFromExperimentData skip >= totalNumberOfRecords"() {
        given:
        int top = 10
        int skip = 2
        ExperimentData experimentData = new ExperimentData(activities: [new Activity(potency: 2.0), new Activity(potency: 3.0)])
        when:
        Map map = service.extractActivityValuesFromExperimentData(experimentData, top, skip)
        then:
        assert !map.activityValues
        assert map.totalNumberOfRecords == 2

    }

    void "test extractActivityValuesFromExperimentData top >= totalNumberOfRecords"() {
        given:
        int top = 2
        int skip = 1
        ExperimentData experimentData = new ExperimentData(activities: [new Activity(potency: 2.0), new Activity(potency: 3.0)])
        when:
        Map map = service.extractActivityValuesFromExperimentData(experimentData, top, skip)
        then:
        assert map.activityValues.size() == 2
        assert map.totalNumberOfRecords == 2

    }

    void "test extractActivityValuesFromExperimentData skip + top > totalNumberOfRecords"() {
        given:
        int top = 2
        int skip = 2
        ExperimentData experimentData = new ExperimentData(activities: [new Activity(potency: 2.0), new Activity(potency: 3.0), new Activity(potency: 3.0)])
        when:
        Map map = service.extractActivityValuesFromExperimentData(experimentData, top, skip)
        then:
        assert map.activityValues.size() == 1
        assert map.totalNumberOfRecords == 3

    }


    void "test extractActivityValuesFromExperimentData skip + top == totalNumberOfRecords"() {
        given:
        int top = 1
        int skip = 2
        ExperimentData experimentData = new ExperimentData(activities: [new Activity(potency: 2.0), new Activity(potency: 3.0), new Activity(potency: 3.0)])
        when:
        Map map = service.extractActivityValuesFromExperimentData(experimentData, top, skip)
        then:
        assert map.activityValues.size() == 1
        assert map.totalNumberOfRecords == 3

    }


    void "test extractActivityValuesFromExperimentData"() {
        given:
        int top = 3
        int skip = 0
        ExperimentData experimentData = new ExperimentData(activities: [new Activity(potency: 2.0), new Activity(potency: 3.0), new Activity(potency: 3.0)])
        when:
        Map map = service.extractActivityValuesFromExperimentData(experimentData, top, skip)
        then:
        assert map.activityValues.size() == 3
        assert map.totalNumberOfRecords == 3

    }


    void "test extractActivityValuesFromExperimentData default skip and top"() {
        given:
        ExperimentData experimentData = new ExperimentData(activities: [new Activity(potency: 2.0), new Activity(potency: 3.0), new Activity(potency: 3.0)])
        when:
        Map map = service.extractActivityValuesFromExperimentData(experimentData)
        then:
        assert map.activityValues.size() == 3
        assert map.totalNumberOfRecords == 3

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
        List<CartCompound> cartCompoundList = []
        cartCompoundList.add(new CartCompound("c1ccccc1", "benzene", 47))
        Map<String, MolSpreadSheetCell> dataMap = [:]

        when: "we want to pull out the active values"
        service.populateMolSpreadSheetRowMetadata(molSpreadSheetData, cartCompoundList, dataMap)

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

    void "test cartAssaysToExperiments with null inputs"() {

        when:
        List<ExperimentSearch> experimentResult = service.cartAssaysToExperiments(null, null)

        then:
        assert experimentResult == []

    }

    void "test cartCompoundsToExperiments with null input"() {

        when:
        List<ExperimentSearch> experimentResult = service.cartCompoundsToExperiments(null)

        then:
        assert experimentResult == []

    }

    void "test cartProjectsToExperiments with null input"() {
        when:
        List<ExperimentSearch> experimentResult = service.cartProjectsToExperiments(null)

        then:
        assert experimentResult == []

    }

    CompoundAdapter buildCompoundAdapter(final Long cid) {
        final Compound compound = new Compound()
        compound.setCid(cid.intValue());
        return new CompoundAdapter(compound)
    }


}
