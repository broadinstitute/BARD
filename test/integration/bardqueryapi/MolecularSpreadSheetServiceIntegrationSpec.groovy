package bardqueryapi

import bard.core.Experiment
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

//    void "testExperimentActivity"() {
//        given:
//        Experiment e = restExperimentService.get(519L);
//        when:
//        ServiceIterator<Compound> compounds = restExperimentService.compounds(e)
//        //ServiceIterator<Value> actiter = restExperimentService.activities(e);
//        then:
//        while (compounds.hasNext()) {
//            Compound compound = compounds.next();
//            CompoundAdapter ca = new CompoundAdapter(compound)
//            println ca.getPubChemCID()
//        }
//        //assertEquals("There should be 272 activities for experiment 519", 272, n);
//    }

    void "test  #label"() {
        given: ""

        when: ""

        then: ""

        where:
        label                        | experimentid  | cids
        "Search with a list of CIDs" | new Long(346) | [3232584, 3232585, 3232586]

    }

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
//        label                                | cartAssays                              | expectedExperimentIds
//        "An existing assay with experiments" | [new CartAssay(assayId: new Long(604))] | [604]
//    }

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
        label                                    | cids                                                      | experimentId  | expectedExperimentIds
        "An existing experiment witha ctivities" | [new Long(1051569), new Long(2917647), new Long(3494575)] | new Long(519) | []
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
        molSpreadSheetData
    }

//    /**
//     * Copied from JDO Wrapper tests
//     */
//    void "test retrieving the experimental data for known compounds in an experiment #label"() {
//        given:
//        Object etag = restCompoundService.newETag("My awesome compound collection", cids);
//        when: "We call the getFactest matehod"
//        final Experiment experiment = this.restExperimentService.get(experimentid)
//        Collection<Compound> compounds = this.restCompoundService.get(cids)
//        then: "We expect to get back a list of facets"
//        ServiceIterator<Value> eiter = this.restExperimentService.activities(experiment, etag);
//        while (eiter.hasNext()) {
//            Value v = eiter.next();
//            println v.source
//            Iterator<Value> valueIterator = v.children()
//            // HillCurveValue hillCurveValue
//            // Object payload
//            while (valueIterator.hasNext()) {
//                Value internalValue = valueIterator.next()
//                String identifier = internalValue.id
//                println identifier
//                if (identifier == 'Activity') {
//
//                    println internalValue.getClass().getName()
//                }
//            }
//        }
//        where:
//        label                        | experimentid  | cids
//        "Search with a list of CIDs" | new Long(346) | [3232584, 3232585, 3232586]
//        // "Search with a single of CID" | new Long(346)   |   [3232584]
//        // "Search with another set of CID" | new Long(1326)   |   [11289,5920,442428]
//    }
}
