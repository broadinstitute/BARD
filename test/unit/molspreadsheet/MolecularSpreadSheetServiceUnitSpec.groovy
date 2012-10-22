package molspreadsheet

import bard.core.adapter.CompoundAdapter
import bard.core.rest.RESTExperimentService
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
import bard.core.rest.RESTCompoundService

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
    RESTCompoundService restCompoundService
    CompoundAdapter compoundAdapter = buildCompoundAdapter(6 as Long, [842121 as Long])
    @Shared Map compoundAdapterMap = [compoundAdapters: [buildCompoundAdapter(6 as Long, [842121 as Long])], facets: null, nHits: 0]


    void setup() {
        compoundAdapter.metaClass.structureSMILES = 'c1ccccc1'
        compoundAdapter.metaClass.pubChemCID = 1 as Long
        compoundAdapter.metaClass.name = 'name'
        this.restExperimentService = Mock(RESTExperimentService)
        this.restCompoundService = Mock(RESTCompoundService)
        this.queryCartService = Mock(QueryCartService)
        this.queryServiceWrapper = Mock(QueryServiceWrapper)
        this.shoppingCartService = Mock(ShoppingCartService)
        this.queryService = Mock(IQueryService)
        service.queryServiceWrapper = queryServiceWrapper

    }

    void tearDown() {
        // Tear down logic here
    }
    /**
     * We tests the non-null case with an integration test
     */
    void "test findExperimentDataById Null Experiment"() {
        given:
        final Long experimentId
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
        final List <Experiment>  experimentList = []

        ServiceIterator<Compound> compoundServiceIterator  = Mock()
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
        restExperimentService.compounds(_) >> {compoundServiceIterator}

        assertNull eTag
    }


    void "test extractActivityValues #label"() {
        given:
        final Experiment experiment = new Experiment()
        final Integer top = 10
        final Integer skip = 0
        ServiceIterator<Value> experimentValueIterator = Mock(ServiceIterator)
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
        ServiceIterator<Value> experimentValueIterator = Mock(ServiceIterator)
        when:
        final Map map = service.extractActivityValuesFromExperimentValueIterator(experimentValueIterator, top, skip)
        then:
        numberOfTimesHasNextIsCalled * experimentValueIterator.hasNext() >> {expectedHasNext}
        numberOfTimesNextIsCalled * experimentValueIterator.next(_) >> {expectedNext}
        numberOfTimesNextIsCalled * experimentValueIterator.count >> {expectedTotalRecords}
        assert map
        assert map.totalNumberOfRecords == expectedTotalRecords
        assert map.activityValues.size() == expectedTotalActivities

        where:
        label                                 | top | skip | expectedTotalActivities | expectedTotalRecords | expectedNext               | expectedHasNext | numberOfTimesNextIsCalled | numberOfTimesHasNextIsCalled
        "hasNext, returns true, skip is zero" | 0   | 0    | 0                       | 0                    | []                         | true            | 1                         | 1
        "hasNext, returns false"              | 0   | 0    | 0                       | 0                    | []                         | false           | 0                         | 1
        "hasNext, returns true, skip is 1"    | 1   | 1    | 1                       | 1                    | [new Value(), new Value()] | true            | 1                         | 1
       // "hasNext, returns true"               | 0   | 0    | 0                       | 0                    | []                         | true            | 0                         | 2

    }

    void "test extractActivitiesFromExperiment when experimentValue has no children"() {
        given: "we have an experiment"
        final Value experimentalValue = new Value()
        final experimentId = 47 as Long

        when: "we want to pull out the active values"
        SpreadSheetActivity spreadSheetActivity = service.extractActivitiesFromExperiment(experimentalValue, experimentId)

        then: "prove that the active values are available"
        assertNotNull spreadSheetActivity
        assert spreadSheetActivity.experimentId == experimentId

    }


    void "test addCurrentActivityToSpreadSheet when experimentValue has no children"() {
        given: "we have an experiment"
        SpreadSheetActivity spreadSheetActivity = new SpreadSheetActivity()
        final Value experimentalValue = new Value()
        experimentalValue.id = identifier
        experimentalValue.metaClass.value = incomingValue

        when: "we want to pull out the active values"
        service.addCurrentActivityToSpreadSheet(spreadSheetActivity, experimentalValue)

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
        final Value experimentalValue = new Value()

        when: "we want to pull out the active values"
        experimentalValue.id = "foo"

        then: "prove that the active values are available"
        shouldFail {service.addCurrentActivityToSpreadSheet(spreadSheetActivity, experimentalValue)}
    }


    def returnRelevantNumber(String identifier, SpreadSheetActivity spreadSheetActivity) {
        def returnValue
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
        returnValue
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
        assert molSpreadSheetData.mssHeaders.size() == 3
        assert molSpreadSheetData.mssHeaders.contains("Struct")
        assert molSpreadSheetData.mssHeaders.contains("CID")
        assert molSpreadSheetData.mssHeaders.contains("UNM Promiscuity Analysis")
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
        assert molSpreadSheetData.mssHeaders.size() == 3
        assert molSpreadSheetData.mssHeaders.contains("Struct")
        assert molSpreadSheetData.mssHeaders.contains("CID")
        assert molSpreadSheetData.mssHeaders.contains("UNM Promiscuity Analysis")
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
        assert molSpreadSheetData.mssHeaders.size() == 6
        assert molSpreadSheetData.mssHeaders.contains("Struct")
        assert molSpreadSheetData.mssHeaders.contains("CID")
        assert molSpreadSheetData.mssHeaders.contains("UNM Promiscuity Analysis")
        assert molSpreadSheetData.mssHeaders.contains("a")
        assert molSpreadSheetData.mssHeaders.contains("b")
        assert molSpreadSheetData.mssHeaders.contains("c")
    }




    void "test populateMolSpreadSheetRowMetadata"() {
        given: "we have an experiment"
        final MolSpreadSheetData molSpreadSheetData = new MolSpreadSheetData()
        molSpreadSheetData.rowPointer = new LinkedHashMap<Long, Integer>()
        molSpreadSheetData.mssData = new LinkedHashMap<String, MolSpreadSheetCell>()
        List<CartCompound> cartCompoundList = []
        cartCompoundList.add(new CartCompound("c1ccccc1", "benzene", 47))

        when: "we want to pull out the active values"
        service.populateMolSpreadSheetRowMetadata(molSpreadSheetData, cartCompoundList)

        then: "prove that the active values are available"
        assertNotNull molSpreadSheetData
        assertNotNull molSpreadSheetData.mssHeaders
        assert molSpreadSheetData.mssHeaders.size() == 0
    }


    void "test populateMolSpreadSheetRowMetadata compoundAdapterMap"() {
        given: "we have an experiment"
        final MolSpreadSheetData molSpreadSheetData = new MolSpreadSheetData()
        molSpreadSheetData.rowPointer = new LinkedHashMap<Long, Integer>()
        molSpreadSheetData.mssData = new LinkedHashMap<String, MolSpreadSheetCell>()

        when: "we want to pull out the active values"
        service.populateMolSpreadSheetRowMetadata(molSpreadSheetData, compoundAdapterMap)

        then: "prove that the active values are available"
        assertNotNull molSpreadSheetData
        assertNotNull molSpreadSheetData.mssHeaders
        assert molSpreadSheetData.mssHeaders.size() == 0
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
