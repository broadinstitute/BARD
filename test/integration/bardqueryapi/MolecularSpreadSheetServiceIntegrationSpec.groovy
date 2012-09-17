package bardqueryapi

import bard.core.rest.RESTCompoundService
import bard.core.rest.RESTExperimentService
import grails.plugin.spock.IntegrationSpec
import org.junit.After
import org.junit.Before
import spock.lang.Unroll

import static junit.framework.Assert.assertNotNull

@Unroll
class MolecularSpreadSheetServiceIntegrationSpec extends IntegrationSpec {

    MolecularSpreadSheetService molecularSpreadSheetService
    MolSpreadSheetData molSpreadSheetData = generateFakeData()
    RESTCompoundService restCompoundService
    RESTExperimentService restExperimentService



    @Before
    void setup() {
        this.restExperimentService = molecularSpreadSheetService.queryServiceWrapper.restExperimentService
        this.restCompoundService = molecularSpreadSheetService.queryServiceWrapper.restCompoundService

    }

    @After
    void tearDown() {

    }

     //TODO: Uncomment once NCGC fix the server issues they are having
//    void "tests cartAssaysToExperiments #label"() {
//        given: "That a list of CartAssay objects have been created"
//        final List<CartAssay> givenCartAssays = cartAssays
//        when: "We call the cartAssaysToExperiments() with the given list of assay carty objects"
//        List<Experiment> experiments = molecularSpreadSheetService.cartAssaysToExperiments(givenCartAssays)
//        then: "We expect experiments for each of the assays to be found"
//        assert experiments
//
//
//        where:
//        label                                | cartAssays
//        "An existing assay with experiments" | [new CartAssay(assayId: new Long(519))]
//    }

//    void "tests extractActivitiesFromExperiment #label"() {
//        given: "That we have created an ETag from a list of CIDs"
//        final Object compoundETag = restCompoundService.newETag("Compound ETags", cids);
//        and: "That we have an Experiment Object"
//        Experiment experiment = restExperimentService.get(experimentId)
//
//        and: "We call the activities method on the restExperimentService"
//        final ServiceIterator<Value> experimentIterator = this.restExperimentService.activities(experiment, compoundETag);
//
//        and: "We extract the first element in the collection"
//        Value experimentValue
//        while (experimentIterator.hasNext()) {
//            experimentValue = experimentIterator.next()
//            break;
//        }
//        when: "We call the extractActivitiesFromExperiment method with the experimentValue"
//        SpreadSheetActivity spreadSheetActivity = molecularSpreadSheetService.extractActivitiesFromExperiment(experimentValue)
//        then: "We a spreadSheetActivity"
//        assert spreadSheetActivity
//        assert spreadSheetActivity.cid
//        assert spreadSheetActivity.eid
//        assert spreadSheetActivity.sid
//        assert spreadSheetActivity.hillCurveValue
//        where:
//        label                                    | cids                                                      | experimentId
//        "An existing experiment witha ctivities" | [new Long(1051569), new Long(2917647), new Long(3494575)] | new Long(519)
//
//    }

//    void "tests findActivitiesForCompounds #label"() {
//        given: "That we have created an ETag from a list of CIDs"
//        final Object etag = restCompoundService.newETag("Compound ETags", cids);
//        and: "That we have an Experiment Object"
//        Experiment experiment = restExperimentService.get(experimentId)
//        when: "We call the findActivitiesForCompounds() method with the experiment and the ETag"
//        final List<SpreadSheetActivity> activities = molecularSpreadSheetService.findActivitiesForCompounds(experiment, etag)
//        then: "We expect experiments for each of the assays to be found"
//        assert activities
//        where:
//        label                                    | cids                                                      | experimentId  | expectedExperimentIds
//        "An existing experiment witha ctivities" | [new Long(1051569), new Long(2917647), new Long(3494575)] | new Long(519) | []
//    }

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
        molSpreadSheetData.rowPointer.put(5342L,0)
        molSpreadSheetData.rowPointer.put(5344L,0)
        molSpreadSheetData
    }
}
