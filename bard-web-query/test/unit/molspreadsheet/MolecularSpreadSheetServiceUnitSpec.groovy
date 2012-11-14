package molspreadsheet

import bard.core.adapter.CompoundAdapter
import bard.core.interfaces.SearchResult
import bard.core.rest.RESTAssayService
import bard.core.rest.RESTCompoundService
import bard.core.rest.RESTExperimentService
import bard.core.rest.RESTProjectService
import bardqueryapi.BardWebInterfaceControllerUnitSpec
import bardqueryapi.IQueryService
import bardqueryapi.QueryServiceWrapper
import com.metasieve.shoppingcart.ShoppingCartService
import grails.test.mixin.TestFor
import querycart.CartCompound
import querycart.QueryCartService
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll
import bard.core.*
import bard.core.rest.ActivitySearchResult

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
    QueryServiceWrapper queryServiceWrapper
    ShoppingCartService shoppingCartService
    IQueryService queryService
    RESTExperimentService restExperimentService
    RESTAssayService restAssayService
    RESTCompoundService restCompoundService
    RESTProjectService restProjectService
    CompoundAdapter compoundAdapter = buildCompoundAdapter(6 as Long, [842121 as Long])
    @Shared Map compoundAdapterMap = [compoundAdapters: [buildCompoundAdapter(6 as Long, [842121 as Long])], facets: null, nHits: 0]


    void setup() {
        compoundAdapter.metaClass.structureSMILES = 'c1ccccc1'
        compoundAdapter.metaClass.pubChemCID = 1 as Long
        compoundAdapter.metaClass.name = 'name'
        this.restExperimentService = Mock(RESTExperimentService)
        this.restCompoundService = Mock(RESTCompoundService)
        this.restProjectService = Mock(RESTProjectService)
        this.restAssayService = Mock(RESTAssayService)
        this.queryCartService = Mock(QueryCartService)
        this.queryServiceWrapper = Mock(QueryServiceWrapper)
        this.shoppingCartService = Mock(ShoppingCartService)
        this.queryService = Mock(IQueryService)
        queryServiceWrapper.restAssayService = restAssayService
        service.queryServiceWrapper = queryServiceWrapper
        service.queryService = this.queryService


    }

    void tearDown() {
        // Tear down logic here
    }
    void "test findActivitiesForCompounds #label"(){
        given:
        Experiment experiment = new Experiment()
        Object compoundETag = null
        SearchResult<Value> experimentalResults = new ActivitySearchResult(restExperimentService,experiment)
        //add a null value
        experimentalResults.searchResults.add(null)
        when:
        List<SpreadSheetActivity> spreadSheetActivities = service.findActivitiesForCompounds(experiment,compoundETag)
        then:
        queryServiceWrapper.restExperimentService >> { restExperimentService}
        queryServiceWrapper.restExperimentService.activities(_, _) >> {experimentalResults}
        assert spreadSheetActivities.isEmpty()
    }
    void "test assays To Experiments"() {
        given:
        Collection<Assay> assays = [new Assay(name: "A1")]
        when:
        List<Experiment> experiments = service.assaysToExperiments(assays)
        then:
        queryServiceWrapper.restAssayService >> { restAssayService }
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
        queryServiceWrapper.restExperimentService >> { restExperimentService }
        and:
        restExperimentService.get(_) >> {null}
        and:
        assert resultsMap
        assert resultsMap.experiment == null
        assert resultsMap.total == 0
        assert resultsMap.spreadSheetActivities.isEmpty()
        assert resultsMap.role == null

    }


    void "test null cmp iterator in retrieveImpliedCompoundsEtagFromAssaySpecification"() {
        given:
        final List<Experiment> experimentList = []

        SearchResult<Compound> compoundSearchResult = Mock(SearchResult)
        experimentList << new Experiment()

        when:
        Object eTag = service.retrieveImpliedCompoundsEtagFromAssaySpecification(experimentList)

        then:
        queryServiceWrapper.restExperimentService >> { restExperimentService }
        and:
        queryServiceWrapper.restCompoundService >> { restCompoundService }
        and:
        restCompoundService.newETag(_) >> { new Object() }
        and:
        restExperimentService.compounds(_) >> {compoundSearchResult}

        assertNull eTag
    }


    void "test extractActivityValues #label"() {
        given:
        final Experiment experiment = new Experiment()
        final Integer top = 10
        final Integer skip = 0
        SearchResult<Value> experimentValueIterator = Mock(SearchResult)
        when:
        Map map = service.extractActivityValues(experiment, top, skip)
        then:
        queryServiceWrapper.restExperimentService >> { restExperimentService }
        and:
        restExperimentService.activities(_) >> {experimentValueIterator}
        and:
        assert map
    }

    void "test extractActivityValuesFromExperimentIterator #label, null experimentIterator"() {
        when:
        final Map map = service.extractActivityValuesFromExperimentValueIterator(experimentIterator, top, skip)
        then:
        assert map
        assert map.totalNumberOfRecords == expectedNumberOfRecords
        assert map.activityValues.size() == expectedNumberOfRecords

        where:
        label           | top | skip | expectedNumberOfRecords | experimentIterator
        "Null iterator" | 10  | 0    | 0                       | null
    }

    void "test extractActivityValuesFromExperimentIterator #label"() {
        given:
        SearchResult<Value> experimentValueIterator = Mock(SearchResult)
        when:
        final Map map = service.extractActivityValuesFromExperimentValueIterator(experimentValueIterator, top, skip)
        then:
        numberOfTimesNextIsCalled * experimentValueIterator.next(_, _) >> {expectedNext}
        numberOfTimesNextIsCalled * experimentValueIterator.count >> {expectedTotalRecords}
        assert map
        assert map.totalNumberOfRecords == expectedTotalRecords
        assert map.activityValues.size() == expectedTotalActivities

        where:
        label                | top | skip | numberOfTimesNextIsCalled | expectedNext                                                           | expectedTotalRecords | expectedTotalActivities
        "Top and skip = 0"   | 0   | 0    | 1                         | []                                                                     | 0                    | 0
        "Top=1 and skip = 1" | 1   | 1    | 1                         | [new Value(new DataSource("name")), new Value(new DataSource("name"))] | 1                    | 2
    }


    void "test addCurrentActivityToSpreadSheet when experimentValue has no children"() {
        given: "we have an experiment"
        SpreadSheetActivity spreadSheetActivity = new SpreadSheetActivity()
        List<String> columnNames = []
        final Value experimentalValue = new Value(new DataSource("name"))
        experimentalValue.id = identifier
        experimentalValue.metaClass.value = incomingValue

        when: "we want to pull out the active values"
        service.addCurrentActivityToSpreadSheet(columnNames, spreadSheetActivity, experimentalValue)

        then: "prove that the active values are available"
        assert returnRelevantNumber(identifier, spreadSheetActivity) == returnValue

        where:
        incomingValue | identifier | returnValue
        47 as Double  | "potency"  | 47 as Double
        47 as Long    | "outcome"  | null
        47 as Long    | "eid"      | 47 as Long
        47 as Long    | "cid"      | 47 as Long
        47 as Long    | "sid"      | 47 as Long
    }




    void "test error value of addCurrentActivityToSpreadSheet "() {
        given: "we have an experiment"
        SpreadSheetActivity spreadSheetActivity = new SpreadSheetActivity()
        List<String> columnNames = []
        final Value experimentalValue = new Value(new DataSource("name"))

        when: "we want to pull out the active values"
        experimentalValue.id = "foo"

        then: "prove that the active values are available"
        shouldFail {service.addCurrentActivityToSpreadSheet(columnNames, spreadSheetActivity, experimentalValue)}
    }

    def returnRelevantNumber(String identifier, SpreadSheetActivity spreadSheetActivity) {
        def returnValue = null
        switch (identifier) {
            case "potency":
                returnValue = (Double) spreadSheetActivity.potency
                break
            case "outcome":
                returnValue = spreadSheetActivity.activityOutcome
                break
            case "eid":
                returnValue = spreadSheetActivity.eid
                break
            case "cid":
                returnValue = spreadSheetActivity.cid
                break
            case "sid":
                returnValue = spreadSheetActivity.sid
                break
            default:
                assert false, "Unexpected Identifier: ${identifier} is unknown"
        }
        return returnValue
    }




    void "test populateMolSpreadSheetColumnMetadata when experiment list is empty"() {
        given: "we have an experiment"
        final MolSpreadSheetData molSpreadSheetData = new MolSpreadSheetData()
        final List<Experiment> experimentList = []

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
        final List<Experiment> experimentList = []

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
        final List<Experiment> experimentList = []
        experimentList << new bard.core.Experiment("a")
        experimentList << new bard.core.Experiment("b")
        experimentList << new bard.core.Experiment("c")

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
        List<Experiment> experimentResult = service.cartAssaysToExperiments(null, null)

        then:
        assert experimentResult == []

    }

    void "test cartCompoundsToExperiments with null input"() {

        when:
        List<Experiment> experimentResult = service.cartCompoundsToExperiments(null)

        then:
        assert experimentResult == []

    }

    void "test cartProjectsToExperiments with null input"() {
        given:
        queryServiceWrapper.restProjectService >> { restProjectService }

        when:
        List<Experiment> experimentResult = service.cartProjectsToExperiments(null)

        then:
        assert experimentResult == []

    }

    CompoundAdapter buildCompoundAdapter(final Long cid, final List<Long> sids) {
        final Compound compound = new Compound()
        final DataSource source = new DataSource("stuff", "v1")
        compound.setId(cid);
        for (Long sid : sids) {
            compound.add(new LongValue(source, Compound.PubChemSIDValue, sid));
        }
        // redundant
        compound.add(new LongValue(source, Compound.PubChemCIDValue, cid));
        return new CompoundAdapter(compound)
    }


}
