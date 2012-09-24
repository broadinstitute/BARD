package bardqueryapi

import bard.core.rest.RESTCompoundService
import bard.core.rest.RESTExperimentService
import grails.plugin.spock.IntegrationSpec
import molspreadsheet.MolSpreadSheetCell
import molspreadsheet.MolSpreadSheetData
import org.junit.After
import org.junit.Before
import spock.lang.Unroll
import bard.core.*

import static junit.framework.Assert.assertNotNull
import spock.lang.Timeout

@Unroll
class MolecularSpreadSheetServiceIntegrationSpec extends IntegrationSpec {

    MolecularSpreadSheetService molecularSpreadSheetService
    MolSpreadSheetData molSpreadSheetData = generateFakeData()
    RESTCompoundService restCompoundService
    RESTExperimentService restExperimentService
    QueryServiceWrapper queryServiceWrapper



    @Before
    void setup() {
        this.restExperimentService = molecularSpreadSheetService.queryServiceWrapper.restExperimentService
        this.restCompoundService = molecularSpreadSheetService.queryServiceWrapper.restCompoundService

    }

    @After
    void tearDown() {

    }

    void "test findExperimentDataById #label"() {

        when: "We call the findExperimentDataById method with the experimentId #experimentId"
        final Map<Long, List<SpreadSheetActivity>> experimentDataMap = molecularSpreadSheetService.findExperimentDataById(experimentId, top, skip)

        then: "We get back the expected map"
        assert experimentDataMap
        final Long totalActivities = experimentDataMap.total
        final ExperimentValues.ExperimentRole role = experimentDataMap.role
        println role
        println totalActivities
        assert totalActivities
        final List<SpreadSheetActivity> activities = experimentDataMap.spreadSheetActivities
        assert activities
        assert activities.size() == 10
        for (SpreadSheetActivity spreadSheetActivity : activities) {
            assert spreadSheetActivity
            assert spreadSheetActivity.cid
            assert spreadSheetActivity.eid
            assert spreadSheetActivity.sid
            assert spreadSheetActivity.hillCurveValue
        }
        where:
        label                                              | experimentId   | top | skip
        "An existing experiment with activities - skip 0"  | new Long(1326) | 10  | 0
        "An existing experiment with activities - skip 10" | new Long(1326) | 10  | 10
    }

    void "tests cartAssaysToExperiments #label"() {
        given: "That a list of CartAssay objects have been created"
        final List<CartAssay> givenCartAssays = cartAssays
        when: "We call the cartAssaysToExperiments() with the given list of assay carty objects"
        List<Experiment> experiments = molecularSpreadSheetService.cartAssaysToExperiments(null, givenCartAssays)
        then: "We expect experiments for each of the assays to be found"
        assert experiments
        where:
        label                                | cartAssays
        "An existing assay with experiments" | [new CartAssay(assayId: new Long(519))]
    }

    void "tests extractActivitiesFromExperiment #label"() {
        given: "That we have created an ETag from a list of CIDs"
        final Object compoundETag = restCompoundService.newETag("Compound ETags For Activities", cids);
        and: "That we have an Experiment Object"
        Experiment experiment = restExperimentService.get(experimentId)

        and: "We call the activities method on the restExperimentService"
        final ServiceIterator<Value> experimentIterator = this.restExperimentService.activities(experiment, compoundETag);
        Collection collect = experimentIterator.collect()
        and: "We extract the first element in the collection"
        Value experimentValue = collect.iterator().next()
        when: "We call the extractActivitiesFromExperiment method with the experimentValue"
        SpreadSheetActivity spreadSheetActivity = molecularSpreadSheetService.extractActivitiesFromExperiment(experimentValue)
        then: "We a spreadSheetActivity"
        assert spreadSheetActivity
        assert spreadSheetActivity.cid
        assert spreadSheetActivity.eid
        assert spreadSheetActivity.sid
        assert spreadSheetActivity.hillCurveValue
        where:
        label                                    | cids                                                   | experimentId
        "An existing experiment with activities" | [new Long(164981), new Long(411519), new Long(483860)] | new Long(1326)

    }

//    void "test assay to experiment mapping"(){
//        given:
//        Long assayId = 1326L
//        Assay assayOriginatingFromScalarId =  queryServiceWrapper.getRestAssayService().get( assayId )
//        and:
//        List<Long> assayListWithOneId = [1326L]
//        Collection<Assay> assaysFromListWithOneId = queryServiceWrapper.getRestAssayService().get( assayListWithOneId)
//        assert assaysFromListWithOneId.size()==1
//        Assay assayOriginatingFromIdsInAList = assaysFromListWithOneId[0]
//        when:
//        Collection<Experiment> experiments1 = assayOriginatingFromIdsInAList.getExperiments()
//        Collection<Experiment> experiments2 = assayOriginatingFromScalarId.getExperiments()
//        println experiments2.getClass().name
//            then:
//        if ((experiments1.size()==1)   && (experiments2.size()==1)) {
//            assert experiments1.iterator().next() ==  experiments2.iterator().next()
//        }
//        else
//            assert true
//    }

    void "test retrieve single value"() {
        given: "That we have created"
        Experiment experiment = restExperimentService.get(new Long(883))
        final ServiceIterator<Compound> compoundServiceIterator = restExperimentService.compounds(experiment)
        when: "We call the findAct"
        assert experiment
        List<Compound> compoundList = compoundServiceIterator.next(2)
        Object etag = restCompoundService.newETag("find an experiment", compoundList*.id);
        ServiceIterator<Value> eiter = this.restExperimentService.activities(experiment, etag);
        assertNotNull eiter
        eiter.hasNext()
        Value value = eiter.next()
        assert value
        //final List<SpreadSheetActivity> activities = experiment.
        then: "We expect experiments for each of the assays to be found"
        assert true
    }

    void "test retrieve multiple values"() {
        given: "That we have identified experiemnt 346"
        Experiment experiment = restExperimentService.get(new Long(346))
        final ServiceIterator<Compound> compoundServiceIterator = restExperimentService.compounds(experiment)
        when: "We call for the activities"
        assert experiment
        List<Compound> compoundList = compoundServiceIterator.next(3)
        Object etag = restCompoundService.newETag("find experiment 346 data", compoundList*.id); // etag for 3 compounds
        ServiceIterator<Value> experimentIterator = this.restExperimentService.activities(experiment, etag);
        then: "We expect to see non-null activitiy for each compound"
        int countValues = 0
        while (experimentIterator.hasNext()) {
            Value experimentValue = experimentIterator.next()
            SpreadSheetActivity spreadSheetActivity = molecularSpreadSheetService.extractActivitiesFromExperiment(experimentValue)
            HillCurveValue hillCurveValue = spreadSheetActivity.hillCurveValue
            if ((hillCurveValue.s0 != null) &&
                    (hillCurveValue.sinf != null) &&
                    (hillCurveValue.coef != null))
                countValues++;
        }
        assert countValues > 1
    }

    void "tests findActivitiesForCompounds #label"() {
        given: "That we have created an ETag from a list of CIDs"
        final Object etag = restCompoundService.newETag("Compound ETags", cids);
        and: "That we have an Experiment Object"
        Experiment experiment = restExperimentService.get(experimentId)
        when: "We call the findActivitiesForCompounds() method with the experiment and the ETag"
        final List<SpreadSheetActivity> activities = molecularSpreadSheetService.findActivitiesForCompounds(experiment, etag)
        then: "We expect experiments for each of the assays to be found"
        assert activities
        where:
        label                                    | cids                                                   | experimentId
        "An existing experiment with activities" | [new Long(164981), new Long(411519), new Long(483860)] | new Long(1326)
    }

    void "tests retrieveExperimentalData"() {
        when:
        assertNotNull molSpreadSheetData
        then:
        assertDataForSpreadSheetExist(molSpreadSheetData)
    }


    void assertDataForSpreadSheetExist(MolSpreadSheetData molSpreadSheetData) {
        assert molSpreadSheetData != null
        for (int rowCnt in 0..(molSpreadSheetData.getRowCount() - 1)) {
            for (int colCnt in 0..(molSpreadSheetData.mssHeaders.size() - 1)) {
                String key = "${rowCnt}_${colCnt}"
                assertNotNull(molSpreadSheetData.mssData["${rowCnt}_${colCnt}"])
            }

        }
    }


    MolSpreadSheetData generateFakeData() {
        MolSpreadSheetData molSpreadSheetData = new MolSpreadSheetData()
        molSpreadSheetData = new MolSpreadSheetData()
        molSpreadSheetData.mssHeaders = ["Chemical Structure",
                "CID",
                "DNA polymerase (Q9Y253) ADID : 1 IC50",
                "Serine-protein kinase (Q13315) ADID : 1 IC50",
                "Tyrosine-DNA phosphodiesterase 1 (Q9NUW8) ADID: 514789"]
        molSpreadSheetData.mssData.put("0_0", new MolSpreadSheetCell("1", MolSpreadSheetCellType.string))
        molSpreadSheetData.mssData.put("0_1", new MolSpreadSheetCell("3888711", MolSpreadSheetCellType.identifier))
        molSpreadSheetData.mssData.put("0_2", new MolSpreadSheetCell("3888711", MolSpreadSheetCellType.greaterThanNumeric))
        molSpreadSheetData.mssData.put("0_3", new MolSpreadSheetCell("3888711", MolSpreadSheetCellType.percentageNumeric))
        molSpreadSheetData.mssData.put("0_4", new MolSpreadSheetCell("3888711", MolSpreadSheetCellType.lessThanNumeric))
        molSpreadSheetData.mssData.put("1_0", new MolSpreadSheetCell("1", MolSpreadSheetCellType.string))
        molSpreadSheetData.mssData.put("1_1", new MolSpreadSheetCell("3888711", MolSpreadSheetCellType.identifier))
        molSpreadSheetData.mssData.put("1_2", new MolSpreadSheetCell("3888711", MolSpreadSheetCellType.greaterThanNumeric))
        molSpreadSheetData.mssData.put("1_3", new MolSpreadSheetCell("3888711", MolSpreadSheetCellType.percentageNumeric))
        molSpreadSheetData.mssData.put("1_4", new MolSpreadSheetCell("3888711", MolSpreadSheetCellType.lessThanNumeric))
        molSpreadSheetData.rowPointer.put(5342L, 0)
        molSpreadSheetData.rowPointer.put(5344L, 0)
        molSpreadSheetData
    }

    @Timeout(10)
    void "tests getExperimentActivities #label"() {
        given: "Experiment ID"
        final Experiment experiment = restExperimentService.get(experimentId)

        when: "We call the restExperimentService.activities method with the experiment"
        final ServiceIterator<Value> experimentIterator = restExperimentService.activities(experiment);
        List<Value> activityValues = []
        if (experimentIterator.hasNext()) {
            activityValues = experimentIterator.next(10)
        }

        then: "We expect activities for each of the experiments to be found"
        assert activityValues

        where:
        label                                    | experimentId
        "An existing experiment with activities" | new Long(346)
    }

}
