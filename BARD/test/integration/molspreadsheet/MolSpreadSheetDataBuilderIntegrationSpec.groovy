package molspreadsheet

import bard.core.rest.spring.experiment.ExperimentSearch
import com.metasieve.shoppingcart.ShoppingCartService
import grails.plugin.spock.IntegrationSpec
import org.junit.After
import org.junit.Before
import querycart.CartAssay
import querycart.CartCompound
import querycart.CartProject
import querycart.QueryCartService
import spock.lang.Unroll

@Unroll
class MolSpreadSheetDataBuilderIntegrationSpec extends IntegrationSpec {

    MolecularSpreadSheetService molecularSpreadSheetService
    MolSpreadSheetData molSpreadSheetData = generateFakeData()
    MolSpreadSheetDataBuilder molSpreadSheetDataBuilder
    QueryCartService queryCartService
    ShoppingCartService shoppingCartService


    @Before
    void setup() {
        molSpreadSheetDataBuilder = new MolSpreadSheetDataBuilder(this.molecularSpreadSheetService)
    }

    @After
    void tearDown() {

    }

    void "test different combinations through deriveListOfExperiments #label"() {
        given:
        List<Long> pids = []
        List<Long> adids = []
        List<Long> cids = []
        if (cartAssay) {
            adids << cartAssay.externalId
        }
        if (cartCompound) {
            cids << cartCompound.externalId
        }
        if (cartProject) {
            pids << cartProject.externalId
        }
        Map<Long, Long> mapExperimentIdsToCapAssayIds = [:]

        when: "we have a molecularSpreadSheetService"
        Map deriveListOfExperiments = molSpreadSheetDataBuilder.deriveListOfExperimentsFromIds(pids, adids, cids,mapExperimentIdsToCapAssayIds)

        then: "we should be able to generate the core molSpreadSheetData, with valid empty data holders"
        List<ExperimentSearch> experimentList = deriveListOfExperiments.experimentList
        MolSpreadsheetDerivedMethod molSpreadsheetDerivedMethod = deriveListOfExperiments.molSpreadsheetDerivedMethod
        assert experimentList != null



        where:
        label                         | dataIsSufficient | cartAssay             | cartProject             | cartCompound                        | expectedMolSpreadsheetDerivedMethod
        "All null"                    | false            | null                  | null                    | null                                | null
        "Assay"                       | true             | new CartAssay("A", 1, 1) | null                    | null                                | MolSpreadsheetDerivedMethod.NoCompounds_Assays_NoProjects
        "Project"                     | true             | null                  | new CartProject("P", 8, 8) | null                                | MolSpreadsheetDerivedMethod.NoCompounds_NoAssays_Projects
        "Compound"                    | true             | null                  | null                    | new CartCompound("C", "c", 1, 0, 0) | null
        "Project and Compound"        | true             | null                  | new CartProject("P", 8, 8) | new CartCompound("C", "c", 1, 0, 0) | MolSpreadsheetDerivedMethod.NoCompounds_NoAssays_Projects
        "Assay and Compound"          | true             | new CartAssay("A", 1, 1) | null                    | new CartCompound("C", "c", 1, 0, 0) | MolSpreadsheetDerivedMethod.NoCompounds_Assays_NoProjects
        "Assay and Project"           | true             | new CartAssay("A", 1, 1) | new CartProject("P", 8, 8) | null                                | MolSpreadsheetDerivedMethod.NoCompounds_NoAssays_Projects
        "Assay, Project and Compound" | true             | new CartAssay("A", 1, 1) | new CartProject("P", 8, 8) | new CartCompound("C", "c", 1, 0, 0) | MolSpreadsheetDerivedMethod.NoCompounds_NoAssays_Projects
        "None"                        | false            | null                  | null                    | null                                | null
    }

//
//
//
//
//
//
//    void "test extractMolSpreadSheetData"() {
//        when: "we have a molecularSpreadSheetService"
//        assertNotNull molecularSpreadSheetService
//        Assay assay = restCompoundService.get(519 as Long)
//        final ServiceIterator<Experiment> serviceIterator = restCompoundService.iterator(assay, Experiment)
//        Collection<Experiment> experimentList = serviceIterator.collect()
//        MolSpreadSheetData molSpreadSheetData = new MolSpreadSheetData()
//        List<Long> compounds = []
//        //compounds << 364 as Long
//        List<SpreadSheetActivity> spreadSheetActivityList = molecularSpreadSheetService.extractMolSpreadSheetData(molSpreadSheetData,
//                experimentList,
//                compounds)
//
//        then: "we should be able to generate a list of spreadsheet activity elements"
//        assertNotNull spreadSheetActivityList
//        spreadSheetActivityList.size() > 0
//        spreadSheetActivityList.each {  SpreadSheetActivity spreadSheetActivity ->
//            assertNotNull spreadSheetActivity.eid
//            assertNotNull spreadSheetActivity.cid
//            assertNotNull spreadSheetActivity.sid
//        }
//    }
//
//
//
//
//    void "test cartCompoundsToExperiments"() {
//        when: "we have a molecularSpreadSheetService"
//        assertNotNull molecularSpreadSheetService
//        List<CartCompound> cartCompoundList = []
//        cartCompoundList.add(new CartCompound(smiles: "CC(=O)C1=C(O)C(C)=C(O)C(CC2=C(O)C3=C(OC(C)(C)C=C3)C(C(=O)\\C=C\\C3=CC=CC=C3)=C2O)=C1O", name: "Rottlerin", compoundId: 5281847))
//        //List<Experiment> originalExperimentList =  []
//        List<Experiment> finalExperimentList = molecularSpreadSheetService.cartCompoundsToExperiments(cartCompoundList)
//
//        then: "we should be able to generate a list of spreadsheet activity elements"
//        assertNotNull finalExperimentList
//    }
//
//
//    void "test cartProjectsToExperiments"() {
//        when: "we have a molecularSpreadSheetService"
//        assertNotNull molecularSpreadSheetService
//        List<CartProject> cartProjectList = []
//        cartProjectList.add(new CartProject("Summary of Flow Cytometry HTS of Small Molecules that Regulate V-ATPase Proton Transport in Yeast", 364 as Long))
//        List<Experiment> finalExperimentList = molecularSpreadSheetService.cartProjectsToExperiments(cartProjectList)
//
//        then: "we should be able to generate a list of spreadsheet activity elements"
//        assertNotNull finalExperimentList
//        assert finalExperimentList.size() > 1
//    }
//
//
//
//
//    void "test findExperimentDataById #label"() {
//
//        when: "We call the findExperimentDataById method with the experimentId #experimentId"
//        final Map experimentDataMap = molecularSpreadSheetService.findExperimentDataById(experimentId, top, skip)
//
//        then: "We get back the expected map"
//        assert experimentDataMap
//        final Long totalActivities = experimentDataMap.total
////        /final ExperimentValues.ExperimentRole role = experimentDataMap.role
//        //println role
//        //println totalActivities
//        assert totalActivities
//        final List<SpreadSheetActivity> activities = experimentDataMap.spreadSheetActivities
//        assert activities
//        assert activities.size() == 10
//        for (SpreadSheetActivity spreadSheetActivity : activities) {
//            assert spreadSheetActivity
//            assert spreadSheetActivity.cid
//            assert spreadSheetActivity.eid
//            assert spreadSheetActivity.sid
//            assert spreadSheetActivity.hillCurveValue
//        }
//        where:
//        label                                              | experimentId   | top | skip
//        "An existing experiment with activities - skip 0"  | new Long(1326) | 10  | 0
//        "An existing experiment with activities - skip 10" | new Long(1326) | 10  | 10
//    }
//
//    void "tests cartAssaysToExperiments #label"() {
//        given: "That a list of CartAssay objects have been created"
//        final List<CartAssay> givenCartAssays = cartAssays
//        when: "We call the cartAssaysToExperiments() with the given list of assay carty objects"
//        List<Experiment> experiments = molecularSpreadSheetService.cartAssaysToExperiments(null, givenCartAssays)
//        then: "We expect experiments for each of the assays to be found"
//        assert experiments
//        where:
//        label                                | cartAssays
//        "An existing assay with experiments" | [new CartAssay(assayId: new Long(519))]
//    }
//
//    void "tests extractActivitiesFromExperiment #label"() {
//        given: "That we have created an ETag from a list of CIDs"
//        final Object compoundETag = restCompoundService.newETag("Compound ETags For Activities", cids);
//        and: "That we have an Experiment Object"
//        Experiment experiment = restExperimentService.get(experimentId)
//
//        and: "We call the activities method on the restExperimentService"
//        final ServiceIterator<Value> experimentIterator = this.restExperimentService.activities(experiment, compoundETag);
//        Collection collect = experimentIterator.collect()
//        and: "We extract the first element in the collection"
//        Value experimentValue = (Value) collect.iterator().next()
//        when: "We call the extractActivitiesFromExperiment method with the experimentValue"
//        SpreadSheetActivity spreadSheetActivity = molecularSpreadSheetService.extractActivitiesFromExperiment(experimentValue, experimentId)
//        then: "We a spreadSheetActivity"
//        assert spreadSheetActivity
//        assert spreadSheetActivity.cid
//        assert spreadSheetActivity.eid
//        assert spreadSheetActivity.sid
//        assert spreadSheetActivity.hillCurveValue
//        where:
//        label                                    | cids                                                   | experimentId
//        "An existing experiment with activities" | [new Long(164981), new Long(411519), new Long(483860)] | new Long(1326)
//
//    }
//
////    void "test assay to experiment mapping"(){
////        given:
////        Long assayId = 1326L
////        Assay assayOriginatingFromScalarId =  queryServiceWrapper.getRestAssayService().get( assayId )
////        and:
////        List<Long> assayListWithOneId = [1326L]
////        Collection<Assay> assaysFromListWithOneId = queryServiceWrapper.getRestAssayService().get( assayListWithOneId)
////        assert assaysFromListWithOneId.size()==1
////        Assay assayOriginatingFromIdsInAList = assaysFromListWithOneId[0]
////        when:
////        Collection<Experiment> experiments1 = assayOriginatingFromIdsInAList.getExperiments()
////        Collection<Experiment> experiments2 = assayOriginatingFromScalarId.getExperiments()
////        println experiments2.getClass().name
////            then:
////        if ((experiments1.size()==1)   && (experiments2.size()==1)) {
////            assert experiments1.iterator().next() ==  experiments2.iterator().next()
////        }
////        else
////            assert true
////    }
//
//    void "test retrieve single value"() {
//        given: "That we have created"
//        Experiment experiment = restExperimentService.get(new Long(883))
//        final ServiceIterator<Compound> compoundServiceIterator = restExperimentService.compounds(experiment)
//        when: "We call the findAct"
//        assert experiment
//        List<Compound> compoundList = compoundServiceIterator.next(2)
//        Object etag = restCompoundService.newETag("find an experiment", compoundList*.id);
//        ServiceIterator<Value> eiter = this.restExperimentService.activities(experiment, etag);
//        assertNotNull eiter
//        eiter.hasNext()
//        Value value = eiter.next()
//        //final List<SpreadSheetActivity> activities = experiment.
//        then: "We expect experiments for each of the assays to be found"
//        assert value
//    }
//
//    void "test retrieve multiple values"() {
//        given: "That we have identified experiemnt 346"
//        final Long experimentId = new Long(346)
//        Experiment experiment = restExperimentService.get(experimentId)
//        final ServiceIterator<Compound> compoundServiceIterator = restExperimentService.compounds(experiment)
//        when: "We call for the activities"
//        assert experiment
//        List<Compound> compoundList = compoundServiceIterator.next(3)
//        Object etag = restCompoundService.newETag("find experiment 346 data", compoundList*.id); // etag for 3 compounds
//        ServiceIterator<Value> experimentIterator = this.restExperimentService.activities(experiment, etag);
//        then: "We expect to see non-null activitiy for each compound"
//        int countValues = 0
//        while (experimentIterator.hasNext()) {
//            Value experimentValue = experimentIterator.next()
//            SpreadSheetActivity spreadSheetActivity = molecularSpreadSheetService.extractActivitiesFromExperiment(experimentValue, experimentId)
//            HillCurveValue hillCurveValue = spreadSheetActivity.hillCurveValue
//            if ((hillCurveValue.s0 != null) &&
//                    (hillCurveValue.sinf != null) &&
//                    (hillCurveValue.coef != null)) {
//                countValues++;
//            }
//        }
//        assert countValues > 1
//    }
//
//    void "test retrieve multiple values from specific expt"() {
//        given: "That we have identified project 274"
//        final Long experimentId = new Long(1140)
//        Experiment experiment = restExperimentService.get(experimentId)
//        final ServiceIterator<Compound> compoundServiceIterator = restExperimentService.compounds(experiment)
//        when: "We call for the activities"
//        assert experiment
//        List<Compound> compoundList = compoundServiceIterator.next(1)
//        Object etag = restCompoundService.newETag("find experiment 346 data", compoundList*.id); // etag for 3 compounds
//        ServiceIterator<Value> experimentIterator = this.restExperimentService.activities(experiment, etag);
//        then: "We expect to see non-null activitiy for each compound"
//        int countValues = 0
//        while (experimentIterator.hasNext()) {
//            Value experimentValue = experimentIterator.next()
//            SpreadSheetActivity spreadSheetActivity = molecularSpreadSheetService.extractActivitiesFromExperiment(experimentValue, experimentId)
//            HillCurveValue hillCurveValue = spreadSheetActivity.hillCurveValue
//            if ((hillCurveValue.s0 != null) &&
//                    (hillCurveValue.sinf != null) &&
//                    (hillCurveValue.coef != null)) {
//                countValues++;
//            }
//        }
//        assert countValues > 1
//    }
//
//    // an example of a problem
//    void "test indirect accumulation of expts2"() {
//
//        given: "That we casn retrieve the expts for project 274"
//        final ProjectSearchResult project = queryServiceWrapper.restProjectService.get(new Long(274))
//        List<Experiment> allExperiments = []
//        //for (ProjectSearchResult project : projects) {
//        final ServiceIterator<Assay> serviceIterator = queryServiceWrapper.restProjectService.iterator(project, Assay)
//        Collection<Assay> assays = serviceIterator.collect()
//        for (Assay assay : assays) {
//            //println "ASSAY: " + assay.id
//            final ServiceIterator<Experiment> experimentIterator = queryServiceWrapper.restCompoundService.iterator(assay, Experiment)
//            Collection<Experiment> experimentList = experimentIterator.collect()
//            allExperiments.addAll(experimentList)
//
//        }
//        //}
//        List<Long> cartCompoundIdList = []
//        cartCompoundIdList.add(new Long(5281847))
//        // Object etag = queryServiceWrapper.restCompoundService.newETag("Test", cartCompoundIdList);
//        when: "We define an etag for a compound used in this project"  /////////////
//        int dataCount = 0
//        for (Experiment experiment in allExperiments) {
//            //Experiment newExp = queryServiceWrapper.restExperimentService.get(experiment.id)
//            final ServiceIterator<Compound> compoundIterators = restExperimentService.compounds(experiment)
//            final Collection<Compound> compoundsThatWereTestedInThisExperiment = compoundIterators.collect()
//            for (Compound comp : compoundsThatWereTestedInThisExperiment) {
//                CompoundAdapter c = new CompoundAdapter(comp)
//                if (cartCompoundIdList.contains(new Long(c.pubChemCID))) {
//                    ServiceIterator<Value> experimentIterator = queryServiceWrapper.restExperimentService.activities(experiment)
//
//                    // Value experimentValue
//                    while (experimentIterator.hasNext()) {
//                        experimentIterator.next()
//                        dataCount++
//                    }
//                }
//            }
//
//
//        }
//
//
//
//        then: "when we step through the value in the expt"    ////////
//
//        // we expect tyo see some data
//        assert dataCount > 0
//        //println dataCount
//    }
//
//
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
//        label                                    | cids                                                   | experimentId
//        "An existing experiment with activities" | [new Long(164981), new Long(411519), new Long(483860)] | new Long(1326)
//    }
//
//    void "tests retrieveExperimentalData"() {
//        when:
//        assertNotNull molSpreadSheetData
//        then:
//        assertDataForSpreadSheetExist(molSpreadSheetData)
//    }
//
//
//    void assertDataForSpreadSheetExist(MolSpreadSheetData molSpreadSheetData) {
//        assert molSpreadSheetData != null
//        for (int rowCnt in 0..(molSpreadSheetData.rowCount - 1)) {
//            for (int colCnt in 0..(molSpreadSheetData.mssHeaders.size() - 1)) {
//                assertNotNull(molSpreadSheetData.mssData["${rowCnt}_${colCnt}"])
//            }
//
//        }
//    }
//
//
    MolSpreadSheetData generateFakeData() {
        molSpreadSheetData = new MolSpreadSheetData()
        molSpreadSheetData.mssHeaders = [new MolSpreadSheetColumnHeader (columnTitle: ["Chemical Structure"]) ,
                new MolSpreadSheetColumnHeader (molSpreadSheetColSubHeaderList:[new MolSpreadSheetColSubHeader(columnTitle:'CID')]) ,
                new MolSpreadSheetColumnHeader (molSpreadSheetColSubHeaderList:[new MolSpreadSheetColSubHeader(columnTitle:'DNA polymerase (Q9Y253) ADID : 1 IC50')]) ,
                new MolSpreadSheetColumnHeader (molSpreadSheetColSubHeaderList:[new MolSpreadSheetColSubHeader(columnTitle:'Serine-protein kinase (Q13315) ADID : 1 IC50')]) ,
                new MolSpreadSheetColumnHeader (molSpreadSheetColSubHeaderList:[new MolSpreadSheetColSubHeader(columnTitle:'Tyrosine-DNA phosphodiesterase 1 (Q9NUW8) ADID: 514789')]) ]
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
}
