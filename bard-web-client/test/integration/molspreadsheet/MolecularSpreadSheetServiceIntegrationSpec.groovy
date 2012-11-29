package molspreadsheet

import bard.core.HillCurveValue
import bard.core.rest.spring.CompoundRestService
import bard.core.rest.spring.ExperimentRestService
import bard.core.rest.spring.RestCombinedService
import bard.core.rest.spring.assays.Assay
import bard.core.rest.spring.experiment.Activity
import bard.core.rest.spring.experiment.ExperimentData
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

import static junit.framework.Assert.assertNotNull

@Unroll
class MolecularSpreadSheetServiceIntegrationSpec extends IntegrationSpec {

    MolecularSpreadSheetService molecularSpreadSheetService
    MolSpreadSheetData molSpreadSheetData = generateFakeData()
    CompoundRestService compoundRestService
    ExperimentRestService experimentRestService
    RestCombinedService restCombinedService
    QueryCartService queryCartService
    ShoppingCartService shoppingCartService



    @Before
    void setup() {
        this.queryCartService = molecularSpreadSheetService.queryCartService
        this.shoppingCartService = molecularSpreadSheetService.shoppingCartService
    }

    @After
    void tearDown() {

    }



    void "test retrieveExperimentalData degenerate case"() {
        when: "we have a molecularSpreadSheetService"
        assertNotNull molecularSpreadSheetService
        MolSpreadSheetData molSpreadSheetData = molecularSpreadSheetService.retrieveExperimentalData()

        then: "we should be able to generate the core molSpreadSheetData, with valid empty data holders"
        assertNotNull molSpreadSheetData
        assertNotNull molSpreadSheetData.mssData
        assertNotNull molSpreadSheetData.rowPointer
        assertNotNull molSpreadSheetData.columnPointer
        assertNotNull molSpreadSheetData.mssHeaders
        assert molSpreadSheetData.mssData.size() == 8
        assert molSpreadSheetData.rowPointer.size() == 0
        assert molSpreadSheetData.columnPointer.size() == 0
        assert molSpreadSheetData.mssHeaders.size() == 4
    }

//  This test should work, but there are some recent changes to the code that I think may need to be reversed
//  I will leave this stub here for now, with the intention of later modification.
//
//    void "test prepareForExport ( molSpreadSheetData )"() {
//        MolSpreadSheetDataBuilder molSpreadSheetDataBuilder = new MolSpreadSheetDataBuilder()
//        MolSpreadSheetDataBuilderDirector molSpreadSheetDataBuilderDirector = new MolSpreadSheetDataBuilderDirector()
//
//        when: "we have a molecularSpreadSheetService"
//        assertNotNull molecularSpreadSheetService
//        assertNotNull molecularSpreadSheetService
//        assertNotNull queryCartService
//        assertNotNull molSpreadSheetDataBuilder
//        List<CartCompound> cartCompoundList = []
//        List<CartAssay> cartAssayList = []
//        List<CartProject> cartProjectList = []
//
//
//        then: "we should be able to generate the core molSpreadSheetData, with valid empty data holders"
//
//        cartAssayList << new CartAssay("assay",2199)
//        cartCompoundList << new CartCompound("C(=O)C1=C(NCCCC2=CC=CC=C2)C=CC(=C1)[N+]([O-])=O","mol1",4549L)
//        cartCompoundList << new CartCompound("COC1=CC(=CC(OC)=C1OC)C(CCCN(C)CCC1=CC(OC)=C(OC)C=C1)(C#N)C(C)C","mol2",1234L)
//
//
//
//        molSpreadSheetDataBuilderDirector.setMolSpreadSheetDataBuilder(molSpreadSheetDataBuilder)
//        molSpreadSheetDataBuilderDirector.constructMolSpreadSheetData(cartCompoundList,
//                cartAssayList,
//                cartProjectList)
//
//          molSpreadSheetDataBuilderDirector.molSpreadSheetData
//
//        LinkedHashMap<String, Object> dataReadyForExporting  =  molecularSpreadSheetService.prepareForExport molSpreadSheetData
//
//
//        then: "we should be able to generate a list of spreadsheet activity elements"
//        assertNotNull dataReadyForExporting
//    }
//


    void "test fillInTheMissingCellsAndConvertToExpandedMatrix"() {
        when: "we have a molecularSpreadSheetService"
        Map<String, MolSpreadSheetCell> dataMap = [:]
        dataMap["0_3"] = new MolSpreadSheetCell()
        MolSpreadSheetCell flawedMolSpreadSheetCell = new MolSpreadSheetCell()
        flawedMolSpreadSheetCell.spreadSheetActivityStorage = new SpreadSheetActivityStorage()
        dataMap["0_4"] = flawedMolSpreadSheetCell
        molecularSpreadSheetService.fillInTheMissingCellsAndConvertToExpandedMatrix(molSpreadSheetData, dataMap)

        then: "we should be able to generate the core molSpreadSheetData, with valid empty data holders"
        assertNotNull molSpreadSheetData
        assertNotNull molSpreadSheetData.mssData
        assertNotNull molSpreadSheetData.rowPointer
        assertNotNull molSpreadSheetData.columnPointer
        assertNotNull molSpreadSheetData.mssHeaders
        assert molSpreadSheetData.mssData.size() == 10
        assert molSpreadSheetData.rowPointer.size() == 2
        assert molSpreadSheetData.columnPointer.size() == 0
        assert molSpreadSheetData.mssHeaders.size() == 5
    }




    void "test weHaveEnoughDataToMakeASpreadsheet()"() {
        given:
        shoppingCartService.emptyShoppingCart()

        when: "we have a molecularSpreadSheetService"
        assertNotNull molecularSpreadSheetService
        assertNotNull queryCartService


        then: "we should be able to generate the core molSpreadSheetData, with valid empty data holders"
        if (cartAssay) {
            shoppingCartService.addToShoppingCart(cartAssay)
        }
        if (cartCompound) {
            shoppingCartService.addToShoppingCart(cartCompound)
        }
        if (cartProject) {
            shoppingCartService.addToShoppingCart(cartProject)
        }
        assert dataIsSufficient == molecularSpreadSheetService.weHaveEnoughDataToMakeASpreadsheet()


        where:
        dataIsSufficient | cartAssay             | cartProject             | cartCompound
        false            | null                  | null                    | null
        true             | new CartAssay("A", 1) | null                    | null
        true             | null                  | new CartProject("P", 8) | null
        true             | null                  | null                    | new CartCompound("C", "c", 1)
        true             | null                  | new CartProject("P", 8) | new CartCompound("C", "c", 1)
        true             | new CartAssay("A", 1) | null                    | new CartCompound("C", "c", 1)
        true             | new CartAssay("A", 1) | new CartProject("P", 8) | null
        true             | new CartAssay("A", 1) | new CartProject("P", 8) | new CartCompound("C", "c", 1)
        false            | null                  | null                    | null
    }




    void "test try different cart combos"() {
        when: "we have a molecularSpreadSheetService"
        assertNotNull molecularSpreadSheetService
        assertNotNull queryCartService


        then: "we should be able to generate the core molSpreadSheetData, with valid empty data holders"
        final CartAssay assay1 = new CartAssay("Assay Definition: Identification of inhibitors of RAD54 Measured in Biochemical System Using Plate Reader - 2159-01_Inhibitor_SinglePoint_HTS_Activity", 4332L)
        assay1.validate()
        assert !assay1.hasErrors()
        queryCartService.addToShoppingCart(assay1)
        final CartCompound compound1 = new CartCompound("COC1=CC=C(C=C1)C#CC1=CC=C(C=C1)[C@H]1[C@@H](CO)N2CCCCN(C[C@H]12)C(=O)NC1=CC(F)=CC=C1", "BRD-K70362473-001-01-0", 54667549)
        compound1.validate()
        assert !compound1.hasErrors()
        queryCartService.addToShoppingCart(compound1)

        queryCartService.addToShoppingCart(new CartAssay("Assay Definition: Confirmation Concentration-Response Assay for Inhibitors of Human Muscle isoform 2 Pyruvate Kinase", 364L))
        queryCartService.addToShoppingCart(new CartCompound("CC1=CC=C(O1)C1=C(NC2=CC=C(C)C=C2)N2C(C=CC=C2C)=N1", "HMS1817I15", 4085914L))

        MolSpreadSheetData molSpreadSheetData = null
        if (molecularSpreadSheetService.weHaveEnoughDataToMakeASpreadsheet()) {
            molSpreadSheetData = molecularSpreadSheetService.retrieveExperimentalData()
        }
        assertNotNull molSpreadSheetData

    }





    void "test populateMolSpreadSheetData"() {
        given:
        Long assayId1 = 2199
        Long assayId2 = 730
        when: "we have a molecularSpreadSheetService"
        assertNotNull molecularSpreadSheetService

        List<ExperimentSearch> finalExperimentList = restCombinedService.findExperimentsByAssayId(assayId1)
        List<ExperimentSearch> experiments = restCombinedService.findExperimentsByAssayId(assayId2)
        finalExperimentList.addAll(experiments)
        MolSpreadSheetData molSpreadSheetData = new MolSpreadSheetData()
        100.times {
            molSpreadSheetData.mssHeaders << []
        }

        String etag = this.compoundRestService.newETag((new Date()).toTimestamp().toString(), [4540 as Long, 4544 as Long, 4549 as Long, 4552 as Long])

        List<SpreadSheetActivity> spreadSheetActivityList = molecularSpreadSheetService.extractMolSpreadSheetData(molSpreadSheetData,
                finalExperimentList,
                etag)
        molSpreadSheetData.rowPointer[4540 as Long] = 0
        molSpreadSheetData.rowPointer[4544 as Long] = 1
        molSpreadSheetData.rowPointer[4549 as Long] = 2
        Map<String, MolSpreadSheetCell> dataMap = [:]

        molecularSpreadSheetService.populateMolSpreadSheetData(molSpreadSheetData,
                finalExperimentList,
                spreadSheetActivityList,
                dataMap)



        then: "we should be able to generate a list of spreadsheet activity elements"
        assertNotNull molSpreadSheetData.mssData
        assert molSpreadSheetData.mssData.size() == 0
    }



    void "test extractMolSpreadSheetData"() {
        given:
        long assayId1 = 519

        when: "we have a molecularSpreadSheetService"
        List<ExperimentSearch> experiments = restCombinedService.findExperimentsByAssayId(assayId1)
        MolSpreadSheetData molSpreadSheetData = new MolSpreadSheetData()
        5.times {
            molSpreadSheetData.mssHeaders << []
        }
        String etag = this.compoundRestService.newETag((new Date()).toTimestamp().toString(), [1074927 as Long, 1074929 as Long, 1077518 as Long])
        List<SpreadSheetActivity> spreadSheetActivityList = molecularSpreadSheetService.extractMolSpreadSheetData(molSpreadSheetData,
                experiments,
                etag)

        then: "we should be able to generate a list of spreadsheet activity elements"
        assertNotNull spreadSheetActivityList
        spreadSheetActivityList.size() > 0
        spreadSheetActivityList.each {  SpreadSheetActivity spreadSheetActivity ->
            assertNotNull spreadSheetActivity.eid
            assertNotNull spreadSheetActivity.cid
            assertNotNull spreadSheetActivity.sid
        }
    }



    void "test extractMolSpreadSheetData with no compounds selected"() {
        given:
        long assayId1 = 346
        when: "we have a molecularSpreadSheetService"
        List<ExperimentSearch> experiments = restCombinedService.findExperimentsByAssayId(assayId1)
        MolSpreadSheetData molSpreadSheetData = new MolSpreadSheetData()
        5.times {
            molSpreadSheetData.mssHeaders << []
        }
        //List<Long> compounds = []
        //compounds << 364 as Long
        List<SpreadSheetActivity> spreadSheetActivityList = molecularSpreadSheetService.extractMolSpreadSheetData(molSpreadSheetData,
                experiments)

        then: "we should be able to generate a list of spreadsheet activity elements"
        assertNotNull spreadSheetActivityList
        spreadSheetActivityList.size() > 0
        spreadSheetActivityList.each {  SpreadSheetActivity spreadSheetActivity ->
            assertNotNull spreadSheetActivity.eid
            assertNotNull spreadSheetActivity.cid
            assertNotNull spreadSheetActivity.sid
        }
    }




    void "test extractMolSpreadSheetData with compounds"() {
        given:
        long assayId1 = 346

        when: "we have a molecularSpreadSheetService"
        List<ExperimentSearch> experiments = restCombinedService.findExperimentsByAssayId(assayId1)
        MolSpreadSheetData molSpreadSheetData = new MolSpreadSheetData()
        4.times {
            molSpreadSheetData.mssHeaders << []
        }
        String etag = this.compoundRestService.newETag((new Date()).toTimestamp().toString(), [364 as Long])
        List<SpreadSheetActivity> spreadSheetActivityList = molecularSpreadSheetService.extractMolSpreadSheetData(molSpreadSheetData,
                experiments,
                etag)

        then: "we should be able to generate a list of spreadsheet activity elements"
        assertNotNull spreadSheetActivityList
        assert spreadSheetActivityList.size() == 0
    }




    void "test that we can create an ETag from a list of experiments"() {
        when: "we have list of cart compounds"
        List<CartProject> cartProjectList = []
        cartProjectList << new CartProject("Summary of Flow Cytometry HTS of Small Molecules that Regulate V-ATPase Proton Transport in Yeast", 364)
        List<ExperimentSearch> finalExperimentList = molecularSpreadSheetService.cartProjectsToExperiments(cartProjectList)
        String eTag = molecularSpreadSheetService.retrieveImpliedCompoundsEtagFromAssaySpecification(finalExperimentList)

        then: "we should be able to build and Etag from them"
        assertNotNull eTag
    }


    void "test an empty ETag from a list of null experiments"() {
        when: "we have list of cart compounds"
        List<ExperimentSearch> experimentList = []
        experimentList << new ExperimentSearch()
        String eTag = molecularSpreadSheetService.retrieveImpliedCompoundsEtagFromAssaySpecification(experimentList)

        then: "we should be able to build an Etag from them"
        assertNotNull eTag
    }




    void "test that we can create an ETag from a list of  current compounds"() {
        when: "we have list of cart compounds"
        List<CartCompound> cartCompoundList = []
        cartCompoundList.add(new CartCompound("CC(=O)C1=C(O)C(C)=C(O)C(CC2=C(O)C3=C(OC(C)(C)C=C3)C(C(=O)\\C=C\\C3=CC=CC=C3)=C2O)=C1O", "Rottlerin", 5281847))
        Object eTag = molecularSpreadSheetService.generateETagFromCartCompounds(cartCompoundList)

        then: "we should be able to build and Etag from them"
        assertNotNull eTag
    }





    void "test cartCompoundsToExperiments"() {
        when: "we have a molecularSpreadSheetService"
        List<CartCompound> cartCompoundList = []
        cartCompoundList.add(new CartCompound("CC(=O)C1=C(O)C(C)=C(O)C(CC2=C(O)C3=C(OC(C)(C)C=C3)C(C(=O)\\C=C\\C3=CC=CC=C3)=C2O)=C1O", "Rottlerin", 5281847))
        //List<Experiment> originalExperimentList =  []
        List<ExperimentSearch> finalExperimentList = molecularSpreadSheetService.cartCompoundsToExperiments(cartCompoundList)

        then: "we should be able to generate a list of spreadsheet activity elements"
        assertNotNull finalExperimentList
    }



    void "test empty cartCompoundsToExperiments"() {
        when: "we have a molecularSpreadSheetService"
        List<CartCompound> cartCompoundList = []
        List<ExperimentSearch> finalExperimentList = molecularSpreadSheetService.cartCompoundsToExperiments(cartCompoundList)

        then: "we should be able to generate a list of spreadsheet activity elements"
        assert finalExperimentList.size() == 0
    }



    void "test cartProjectsToExperiments"() {
        when: "we have a molecularSpreadSheetService"
        List<CartProject> cartProjectList = []
        cartProjectList.add(new CartProject("Summary of Flow Cytometry HTS of Small Molecules that Regulate V-ATPase Proton Transport in Yeast", 364))
        List<ExperimentSearch> finalExperimentList = molecularSpreadSheetService.cartProjectsToExperiments(cartProjectList)

        then: "we should be able to generate a list of spreadsheet activity elements"
        assertNotNull finalExperimentList
        assert finalExperimentList.size() > 1
    }




    void "test findExperimentDataById #label"() {

        when: "We call the findExperimentDataById method with the experimentId #experimentId"
        final Map experimentDataMap = molecularSpreadSheetService.findExperimentDataById(experimentId, top, skip)

        then: "We get back the expected map"
        assert experimentDataMap
        final Long totalActivities = experimentDataMap.total
        assert totalActivities
        final List<SpreadSheetActivity> activities = experimentDataMap.spreadSheetActivities
        assert activities
        assert activities.size() == 10
        for (SpreadSheetActivity spreadSheetActivity : activities) {
            assert spreadSheetActivity
            assert spreadSheetActivity.cid
            assert spreadSheetActivity.eid
            assert spreadSheetActivity.sid
            assert spreadSheetActivity.hillCurveValueList
        }
        where:
        label                                              | experimentId   | top | skip
        "An existing experiment with activities - skip 0"  | new Long(1326) | 10  | 0
        "An existing experiment with activities - skip 10" | new Long(1326) | 10  | 10
    }


    void "test convertSpreadSheetActivityToCompoundInformation"() {

        when: "We call the findExperimentDataById method with the experimentId #experimentId"
        final Map experimentDataMap = molecularSpreadSheetService.findExperimentDataById(experimentId, top, skip)

        then: "We get back the expected map"
        assert experimentDataMap
        final Long totalActivities = experimentDataMap.total
        assert totalActivities
        final List<SpreadSheetActivity> activities = experimentDataMap.spreadSheetActivities
        def returnMap = molecularSpreadSheetService.convertSpreadSheetActivityToCompoundInformation(activities)
        assertNotNull returnMap
        assertNotNull returnMap."compoundAdapters"
        assertNotNull returnMap."facets"
        assertNotNull returnMap."nHits"

        where:
        label                                              | experimentId   | top | skip
        "An existing experiment with activities - skip 0"  | new Long(1326) | 10  | 0
        "An existing experiment with activities - skip 10" | new Long(1326) | 10  | 10
    }







    void "tests cartAssaysToExperiments #label"() {
        given: "That a list of CartAssay objects have been created"
        final List<CartAssay> givenCartAssays = cartAssays
        when: "We call the cartAssaysToExperiments() with the given list of assay carty objects"
        List<ExperimentSearch> experiments = molecularSpreadSheetService.cartAssaysToExperiments(null, givenCartAssays)
        then: "We expect experiments for each of the assays to be found"
        assert experiments
        where:
        label                                | cartAssays
        "An existing assay with experiments" | [new CartAssay("Test", 519)]
    }

    void "tests empty cartAssaysToExperiments"() {
        given: "That a list of CartAssay objects have been created"
        final List<CartAssay> givenCartAssays = []
        when: "We call the cartAssaysToExperiments() with the given list of assay carty objects"
        List<ExperimentSearch> experiments = molecularSpreadSheetService.cartAssaysToExperiments(null, givenCartAssays)
        then: "We expect experiments for each of the assays to be found"
        assert experiments.size() == 0
    }



    void "tests empty cartAssaysToExperiments with pre-existing experiment"() {
        given: "That a list of CartAssay objects have been created"
        List<ExperimentSearch> experimentList = []
        ExperimentSearch experiment = new ExperimentSearch()
        experimentList << experiment
        final List<CartAssay> givenCartAssays = []
        when: "We call the cartAssaysToExperiments() with the given list of assay carty objects"
        List<ExperimentSearch> experiments = molecularSpreadSheetService.cartAssaysToExperiments(experimentList, givenCartAssays)
        then: "We expect experiments for each of the assays to be found"
        assert experiments.size() == 1
    }



    void "tests extractActivitiesFromExperiment #label"() {
        given: "That we have created an ETag from a list of CIDs"
        final Object compoundETag = compoundRestService.newETag("Compound ETags For Activities", cids);

        and: "We call the activities method on the restExperimentService"
        final ExperimentData experimentData = this.experimentRestService.activities(experimentId, compoundETag);
        final List<Activity> searchResults = experimentData.activities
        and: "We extract the first element in the collection"
        Activity experimentValue = searchResults.get(0)
        when: "We call the extractActivitiesFromExperiment method with the experimentValue"
        SpreadSheetActivity spreadSheetActivity = molecularSpreadSheetService.extractActivitiesFromExperiment(experimentValue)
        then: "We a spreadSheetActivity"
        assert spreadSheetActivity
        assert spreadSheetActivity.cid
        assert spreadSheetActivity.eid
        assert spreadSheetActivity.sid
        assert spreadSheetActivity.hillCurveValueList[0]
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
        Long experimentId = new Long(883)
        final List<Long> compoundIterator = restCombinedService.compounds(experimentId)
        when: "We call the findAct"

        List<Long> compoundList = compoundIterator.subList(0, 2)
        String etag = compoundRestService.newETag("find an experiment", compoundList);
        ExperimentData eiter = this.experimentRestService.activities(experimentId, etag);
        assertNotNull eiter
        assert eiter.activities
        Activity value = eiter.activities.get(0)
        //final List<SpreadSheetActivity> activities = experiment.
        then: "We expect experiments for each of the assays to be found"
        assert value
    }

    void "test retrieve multiple values"() {
        given: "That we have identified experiemnt 346"
        final Long experimentId = new Long(346)
        final List<Long> compoundIterator = restCombinedService.compounds(experimentId)
        when: "We call for the activities"
        List<Long> compoundList = compoundIterator.subList(0, 3)
        String etag = compoundRestService.newETag("find experiment 346 data", compoundList); // etag for 3 compounds
        ExperimentData experimentIterator = this.experimentRestService.activities(experimentId, etag);
        then: "We expect to see non-null activitiy for each compound"
        int countValues = 0
        for (Activity experimentValue : experimentIterator.activities) {
            SpreadSheetActivity spreadSheetActivity = molecularSpreadSheetService.extractActivitiesFromExperiment(experimentValue)
            HillCurveValue hillCurveValue = spreadSheetActivity.hillCurveValueList[0]
            if ((hillCurveValue.s0 != null) &&
                    (hillCurveValue.sinf != null) &&
                    (hillCurveValue.coef != null)) {
                countValues++;
            }
        }
        assert countValues > 1
    }

    void "test retrieve multiple values from specific expt"() {
        given: "That we have identified project 274"
        final Long experimentId = new Long(1140)

        when: "We call for the activities"
        final List<Long> compoundIterator = restCombinedService.compounds(experimentId)
        List<Long> compoundList = compoundIterator.subList(0, 2)
        String etag = compoundRestService.newETag("find experiment 346 data", compoundList); // etag for 3 compounds
        ExperimentData experimentIterator = this.experimentRestService.activities(experimentId, etag);
        then: "We expect to see non-null activitiy for each compound"
        int countValues = 0
        for (Activity experimentValue : experimentIterator.activities) {
            SpreadSheetActivity spreadSheetActivity = molecularSpreadSheetService.extractActivitiesFromExperiment(experimentValue)
            HillCurveValue hillCurveValue = spreadSheetActivity.hillCurveValueList[0]
            if ((hillCurveValue.s0 != null) &&
                    (hillCurveValue.sinf != null) &&
                    (hillCurveValue.coef != null)) {
                countValues++;
            }
        }
        assert countValues > 1
    }

    // an example of a problem
    void "test indirect accumulation of expts use Assays"() {

        given: "That we casn retrieve the expts for project 274" //////////

        List<Long> cartProjectIdList = new ArrayList<Long>()
        cartProjectIdList.add(new Long(274))
        List<ExperimentSearch> allExperiments = []
        for (Long projectId : cartProjectIdList) {
            List<Assay> assays = restCombinedService.findAssaysByProjectId(projectId)
            for (Assay assay : assays) {
                List<ExperimentSearch> experimentList = restCombinedService.findExperimentsByAssayId(assay.id)
                allExperiments.addAll(experimentList)

            }
        }

        when: "We define an etag for a compound used in this project"  /////////////

        List<Long> cartCompoundIdList = new ArrayList<Long>()
        cartCompoundIdList.add(new Long(5281847))
        String etag = compoundRestService.newETag((new Date()).toString(), cartCompoundIdList);


        then: "when we step through the value in the expt"    ////////

        int dataCount = 0
        for (ExperimentSearch experiment in allExperiments) {

            ExperimentData experimentIterator = experimentRestService.activities(experiment.id, etag)
            for (Activity experimentValue : experimentIterator.activities) {
                dataCount++
            }

        }

        // we expect to see some data
        assert dataCount > 0
    }

    // an example of a problem
//    void "test indirect accumulation of expts"() {
//
//        given: "That we casn retrieve the expts for project 274" //////////
//
//        List<Long> cartProjectIdList = new ArrayList<Long>()
//        cartProjectIdList.add(new Long(274))
//        final Collection<ProjectSearchResult> projects = restProjectService.get(cartProjectIdList)
//        List<Experiment> allExperiments = []
//        for (ProjectSearchResult project : projects) {
//            final ServiceIterator<Assay> serviceIterator = restProjectService.iterator(project, Assay.class)
//            Collection<Assay> assays = serviceIterator.collect()
//            for (Assay assay : assays) {
//                //println "ASSAY: " + assay.id
//                final ServiceIterator<Experiment> experimentIterator = restCompoundService.iterator(assay, Experiment.class)
//                Collection<Experiment> experimentList = experimentIterator.collect()
//                allExperiments.addAll(experimentList)
//
//            }
//        }
//
//        when: "We define an etag for a compound used in this project"  /////////////
//
//        List<Long> cartCompoundIdList = new ArrayList<Long>()
//        cartCompoundIdList.add(new Long(5281847))
//        Object etag = restCompoundService.newETag((new Date()).toString(), cartCompoundIdList);
//
//
//        then: "when we step through the value in the expt"    ////////
//
//        int dataCount = 0
//        for (Experiment experiment in allExperiments) {
//
//            ServiceIterator<Value> experimentIterator = restExperimentService.activities(experiment, etag)
//            Value experimentValue
//            while (experimentIterator.hasNext()) {
//                experimentValue = experimentIterator.next()
//                dataCount++
//            }
//
//        }
//
//        // we expect tyo see some data
//        assert dataCount > 0
//    }


    void "tests findActivitiesForCompounds #label"() {
        given: "That we have created an ETag from a list of CIDs"
        final String etag = compoundRestService.newETag("Compound ETags", cids);
        when: "We call the findActivitiesForCompounds() method with the experiment and the ETag"
        final List<SpreadSheetActivity> activities = molecularSpreadSheetService.findActivitiesForCompounds(experimentId, etag)
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
        for (int rowCnt in 0..(molSpreadSheetData.rowCount - 1)) {
            for (int colCnt in 0..(molSpreadSheetData.mssHeaders.flatten().size() - 1)) {
                assertNotNull(molSpreadSheetData.mssData["${rowCnt}_${colCnt}"])
            }

        }
    }


    MolSpreadSheetData generateFakeData() {
        molSpreadSheetData = new MolSpreadSheetData()
        molSpreadSheetData.mssHeaders = [["Chemical Structure"],
                ["CID"],
                ["DNA polymerase (Q9Y253) ADID : 1 IC50"],
                ["Serine-protein kinase (Q13315) ADID : 1 IC50"],
                ["Tyrosine-DNA phosphodiesterase 1 (Q9NUW8) ADID: 514789"]]
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
