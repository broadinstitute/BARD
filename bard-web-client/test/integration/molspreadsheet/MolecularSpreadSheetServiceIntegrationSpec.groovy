package molspreadsheet

import bard.core.SearchParams
import bard.core.rest.spring.AssayRestService
import bard.core.rest.spring.CompoundRestService
import bard.core.rest.spring.ExperimentRestService
import bard.core.rest.spring.ProjectRestService
import bard.core.rest.spring.assays.Assay
import com.metasieve.shoppingcart.ShoppingCartService
import grails.plugin.spock.IntegrationSpec
import querycart.CartAssay
import querycart.CartCompound
import querycart.CartProject
import querycart.QueryCartService
import spock.lang.Shared
import spock.lang.Unroll
import bard.core.rest.spring.experiment.*

import static junit.framework.Assert.assertNotNull

@Unroll
class MolecularSpreadSheetServiceIntegrationSpec extends IntegrationSpec {

    MolecularSpreadSheetService molecularSpreadSheetService
    MolSpreadSheetData molSpreadSheetData = generateFakeData()
    CompoundRestService compoundRestService
    AssayRestService assayRestService
    ExperimentRestService experimentRestService
    ProjectRestService projectRestService
    QueryCartService queryCartService
    ShoppingCartService shoppingCartService
    @Shared
    List<Long> TEST_EIDS = [13902, 14980]
    @Shared
    List<Long> TEST_ADIDS = [5155, 5156]
    @Shared
    Long TEST_PID = 1963
    @Shared
    Long TEST_CID = 9795907


    void "test activitiesByEIDs"() {
        given:
        final List<Long> eids = TEST_EIDS
        final SearchParams searchParams = new SearchParams(top: 10, skip: 0)
        when:
        final ExperimentData experimentData = molecularSpreadSheetService.activitiesByEIDs(eids, searchParams)
        then:
        assert experimentData
        assert experimentData.activities

    }

    void "test activitiesByCIDs"() {
        given:
        final List<Long> cids = [223321, 1486788]
        final SearchParams searchParams = new SearchParams(top: 10, skip: 0)
        when:
        final ExperimentData experimentData = molecularSpreadSheetService.activitiesByCIDs(cids, searchParams)
        then:
        assert experimentData
        assert experimentData.activities

    }

    void "test activitiesBySIDs"() {
        given:
        final List<Long> sids = [17410952, 17412946]
        final SearchParams searchParams = new SearchParams(top: 10, skip: 0)
        when:
        final ExperimentData experimentData = molecularSpreadSheetService.activitiesBySIDs(sids, searchParams)
        then:
        assert experimentData
        assert experimentData.activities

    }

    void "test activitiesByADIDs"() {
        given:
        final List<Long> adids = TEST_ADIDS
        final SearchParams searchParams = new SearchParams(top: 10, skip: 0)
        when:
        final ExperimentData experimentData = molecularSpreadSheetService.activitiesByADIDs(adids, searchParams)
        then:
        assert experimentData
        assert experimentData.activities

    }

    void "test retrieveExperimentalData degenerate case"() {
        when: "we have a molecularSpreadSheetService"
        MolSpreadSheetData molSpreadSheetData = molecularSpreadSheetService.retrieveExperimentalDataFromIds([], [], [])

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
        assert molSpreadSheetData.columnPointer.size() == 2
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
        assert dataIsSufficient == queryCartService.weHaveEnoughDataToMakeASpreadsheet()


        where:
        dataIsSufficient | cartAssay             | cartProject             | cartCompound
        false            | null                  | null                    | null
        false            | new CartAssay("A", 1) | null                    | null
        false            | null                  | new CartProject("P", 8) | null
        true             | null                  | null                    | new CartCompound("C", "c", 1, 0, 0)
        true             | null                  | new CartProject("P", 8) | new CartCompound("C", "c", 1, 0, 0)
        true             | new CartAssay("A", 1) | null                    | new CartCompound("C", "c", 1, 0, 0)
        false            | new CartAssay("A", 1) | new CartProject("P", 8) | null
        true             | new CartAssay("A", 1) | new CartProject("P", 8) | new CartCompound("C", "c", 1, 0, 0)
        false            | null                  | null                    | null
    }




    void "test try different cart combos"() {
        given:
        final CartAssay assay1 = new CartAssay("Assay Definition: Identification of inhibitors of RAD54 Measured in Biochemical System Using Plate Reader - 2159-01_Inhibitor_SinglePoint_HTS_Activity", 4332L)
        final CartCompound compound1 = new CartCompound("COC1=CC=C(C=C1)C#CC1=CC=C(C=C1)[C@H]1[C@@H](CO)N2CCCCN(C[C@H]12)C(=O)NC1=CC(F)=CC=C1", "BRD-K70362473-001-01-0", 54667549, 0, 0)
        final CartAssay assay = new CartAssay("Assay Definition: Confirmation Concentration-Response Assay for Inhibitors of Human Muscle isoform 2 Pyruvate Kinase", 364L)
        final CartCompound compound = new CartCompound("CC1=CC=C(O1)C1=C(NC2=CC=C(C)C=C2)N2C(C=CC=C2C)=N1", "HMS1817I15", 4085914L, 0, 0)


        when: "we have a molecularSpreadSheetService"
        queryCartService.addToShoppingCart(assay1)
        queryCartService.addToShoppingCart(compound1)

        queryCartService.addToShoppingCart(assay)
        queryCartService.addToShoppingCart(compound)


        then: "we should be able to generate the core molSpreadSheetData, with valid empty data holders"
        assay1.validate()
        assert !assay1.hasErrors()
        compound1.validate()
        assert !compound1.hasErrors()
        MolSpreadSheetData molSpreadSheetData = null
        if (queryCartService.weHaveEnoughDataToMakeASpreadsheet()) {
            molSpreadSheetData = molecularSpreadSheetService.retrieveExperimentalDataFromIds([compound.externalId], [assay1.externalId, assay.externalId], [])
        }
        assertNotNull molSpreadSheetData

    }





    void "test expanded populateMolSpreadSheetData"() {
        given:
        Long assayId1 = 2199
        Long assayId2 = 730
        when: "we have a molecularSpreadSheetService"
        assertNotNull molecularSpreadSheetService

 //       List<ExperimentSearch> finalExperimentList  = assayRestService.findExperimentsByAssayId(assayId1)
        List<ExperimentSearch> finalExperimentList  = [new ExperimentSearch(),new ExperimentSearch()]
        List<SpreadSheetActivity> spreadSheetActivityList = [new SpreadSheetActivity (cid: 4540 as Long, eid: 47 as Long),
                                                             new SpreadSheetActivity (cid: 777 as Long, eid: 888 as Long)]
        molSpreadSheetData.rowPointer[4540 as Long] = 0
        Map<String, MolSpreadSheetCell> dataMap = [:]

        molecularSpreadSheetService.populateMolSpreadSheetData(molSpreadSheetData,
                finalExperimentList,
                spreadSheetActivityList,
                dataMap)



        then: "we should be able to generate a list of spreadsheet activity elements"
        dataMap.size() != 0
        dataMap["0_4"] != null
        dataMap["0_4"].activity.toString() == "Uninitialized"
        dataMap["0_4"].activity.name() == "Unknown"
        assertNotNull molSpreadSheetData.mssData
    }





    void "test populateMolSpreadSheetData"() {
        given:
        Long assayId1 = 2199
        Long assayId2 = 730
        when: "we have a molecularSpreadSheetService"
        assertNotNull molecularSpreadSheetService

        List<ExperimentSearch> finalExperimentList = assayRestService.findExperimentsByAssayId(assayId1)
        List<ExperimentSearch> experiments = assayRestService.findExperimentsByAssayId(assayId2)
        finalExperimentList.addAll(experiments)
        MolSpreadSheetData molSpreadSheetData = new MolSpreadSheetData()
        100.times {
            molSpreadSheetData.mssHeaders << new MolSpreadSheetColumnHeader()
        }

        String etag = this.compoundRestService.newETag((new Date()).toTimestamp().toString(), [4540 as Long, 4544 as Long, 4549 as Long, 4552 as Long])

        List<SpreadSheetActivity> spreadSheetActivityList2 = [new SpreadSheetActivity (cid: 4540 as Long, eid: 47 as Long) ]
        molSpreadSheetData.rowPointer[4540 as Long] = 0
        molSpreadSheetData.rowPointer[4544 as Long] = 1
        molSpreadSheetData.rowPointer[4549 as Long] = 2
        Map<String, MolSpreadSheetCell> dataMap = [:]

        molecularSpreadSheetService.populateMolSpreadSheetData(molSpreadSheetData,
                finalExperimentList,
                spreadSheetActivityList2,
                dataMap)



        then: "we should be able to generate a list of spreadsheet activity elements"
        assertNotNull molSpreadSheetData.mssData
        assert molSpreadSheetData.mssData.size() == 0
    }






    void "test extractMolSpreadSheetData"() {
        given:
        long assayId1 =TEST_ADIDS.get(0)

        when: "we have a molecularSpreadSheetService"
        List<ExperimentSearch> experiments = assayRestService.findExperimentsByAssayId(assayId1)
        MolSpreadSheetData molSpreadSheetData = new MolSpreadSheetData()
        5.times {
            molSpreadSheetData.mssHeaders << new MolSpreadSheetColumnHeader()
        }
        String etag = this.compoundRestService.newETag((new Date()).toTimestamp().toString(), [6603008 as Long, 6602571 as Long, 6602616 as Long])
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
        long assayId1 = TEST_ADIDS.get(1) //use this specific ADID because it comes back fairly quickly
        when: "we have a molecularSpreadSheetService"
        List<ExperimentSearch> experiments = assayRestService.findExperimentsByAssayId(assayId1)
        MolSpreadSheetData molSpreadSheetData = new MolSpreadSheetData()
        5.times {
            molSpreadSheetData.mssHeaders << new MolSpreadSheetColumnHeader()
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
        List<ExperimentSearch> experiments = assayRestService.findExperimentsByAssayId(assayId1)
        MolSpreadSheetData molSpreadSheetData = new MolSpreadSheetData()
        4.times {
            molSpreadSheetData.mssHeaders << new MolSpreadSheetColumnHeader()
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
        cartProjectList << new CartProject("Summary of Flow Cytometry HTS of Small Molecules that Regulate V-ATPase Proton Transport in Yeast", TEST_PID)
        List<ExperimentSearch> finalExperimentList = molecularSpreadSheetService.projectIdsToExperiments(cartProjectList*.externalId)
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
        cartCompoundList.add(new CartCompound("CC(=O)C1=C(O)C(C)=C(O)C(CC2=C(O)C3=C(OC(C)(C)C=C3)C(C(=O)\\C=C\\C3=CC=CC=C3)=C2O)=C1O", "Rottlerin", 5281847, 0, 0))
        Object eTag = molecularSpreadSheetService.generateETagFromCids(cartCompoundList*.externalId)

        then: "we should be able to build and Etag from them"
        assertNotNull eTag
    }





    void "test cartCompoundsToExperiments"() {
        when: "we have a molecularSpreadSheetService"
        List<CartCompound> cartCompoundList = []
        cartCompoundList.add(new CartCompound("CC(=O)C1=C(O)C(C)=C(O)C(CC2=C(O)C3=C(OC(C)(C)C=C3)C(C(=O)\\C=C\\C3=CC=CC=C3)=C2O)=C1O", "Rottlerin", 5281847, 0, 0))
        //List<Experiment> originalExperimentList =  []
        List<ExperimentSearch> finalExperimentList = molecularSpreadSheetService.compoundIdsToExperiments(cartCompoundList*.externalId)

        then: "we should be able to generate a list of spreadsheet activity elements"
        assertNotNull finalExperimentList
    }



    void "test empty cartCompoundsToExperiments"() {
        when: "we have a molecularSpreadSheetService"
        List<Long> cartCompoundList = []
        List<ExperimentSearch> finalExperimentList = molecularSpreadSheetService.compoundIdsToExperiments(cartCompoundList)

        then: "we should be able to generate a list of spreadsheet activity elements"
        assert finalExperimentList.size() == 0
    }



    void "test cartProjectsToExperiments"() {
        when: "we have a molecularSpreadSheetService"
        List<CartProject> cartProjectList = []
        cartProjectList.add(new CartProject("Summary of Flow Cytometry HTS of Small Molecules that Regulate V-ATPase Proton Transport in Yeast", TEST_PID))
        List<ExperimentSearch> finalExperimentList = molecularSpreadSheetService.projectIdsToExperiments(cartProjectList*.externalId)

        then: "we should be able to generate a list of spreadsheet activity elements"
        assertNotNull finalExperimentList
        assert finalExperimentList.size() > 1
    }












    void "tests cartAssaysToExperiments #label"() {
        given: "That a list of CartAssay objects have been created"
        final List<CartAssay> givenCartAssays = cartAssays
        when: "We call the cartAssaysToExperiments() with the given list of assay carty objects"
        List<ExperimentSearch> experiments = molecularSpreadSheetService.assayIdsToExperiments(null, givenCartAssays*.externalId)
        then: "We expect experiments for each of the assays to be found"
        assert experiments
        where:
        label                                | cartAssays
        "An existing assay with experiments" | [new CartAssay("Test", TEST_ADIDS.get(0))]
    }

    void "tests empty cartAssaysToExperiments"() {
        given: "That a list of CartAssay objects have been created"
        final List<Long> givenCartAssays = []
        when: "We call the cartAssaysToExperiments() with the given list of assay carty objects"
        List<ExperimentSearch> experiments = molecularSpreadSheetService.assayIdsToExperiments(null, givenCartAssays)
        then: "We expect experiments for each of the assays to be found"
        assert experiments.size() == 0
    }



    void "tests empty cartAssaysToExperiments with pre-existing experiment"() {
        given: "That a list of CartAssay objects have been created"
        List<ExperimentSearch> experimentList = []
        ExperimentSearch experiment = new ExperimentSearch()
        experimentList << experiment
        final List<Long> givenCartAssays = []
        when: "We call the cartAssaysToExperiments() with the given list of assay carty objects"
        List<ExperimentSearch> experiments = molecularSpreadSheetService.assayIdsToExperiments(experimentList, givenCartAssays)
        then: "We expect experiments for each of the assays to be found"
        assert experiments.size() == 1
    }



    void "tests extractActivitiesFromExperiment #label"() {
        given: "That we have created an ETag from a list of CIDs"
        final Object compoundETag = compoundRestService.newETag("Compound ETags For Activities", cids);

        and: "We call the activities method on the restExperimentService"
        final ExperimentData experimentData = this.experimentRestService.activities(experimentId, compoundETag);
        final List<Activity> activities = experimentData.activities
        when: "We extract the first element in the collection"
        Activity activity = activities.get(0)
        then:
        assert activity
        assert activity.cid
        assert activity.eid
        assert activity.sid
        assert activity.resultData
        where:
        label                                    | cids                        | experimentId
        "An existing experiment with activities" | [644794L, 645320L, 646386L] | 14743

    }

    void "test retrieve single value"() {
        given: "That we have created"
        Long experimentId = TEST_EIDS.get(0)
        final List<Long> compounds = experimentRestService.compoundsForExperiment(experimentId)
        when: "We call the findAct"

        List<Long> compoundList = compounds.subList(0, 2)
        String etag = compoundRestService.newETag("find an experiment", compoundList);
        ExperimentData experimentData = this.experimentRestService.activities(experimentId, etag);
        assertNotNull experimentData
        assert experimentData.activities
        Activity value = experimentData.activities.get(0)
        then: "We expect experiments for each of the assays to be found"
        assert value
    }

    void "test retrieve multiple values"() {
        given: "That we have identified experiemnt 346"
        final Long experimentId = TEST_EIDS.get(0)
        final List<Long> compounds = experimentRestService.compoundsForExperiment(experimentId)
        when: "We call for the activities"
        List<Long> compoundList = compounds.subList(0, 3)
        String etag = compoundRestService.newETag("find experiment 346 data", compoundList); // etag for 3 compounds
        ExperimentData experimentIterator = this.experimentRestService.activities(experimentId, etag);
        then: "We expect to see non-null activitiy for each compound"
        int countValues = 0
        for (Activity activity : experimentIterator.activities) {
            ResultData resultData = activity.resultData
            CurveFitParameters curveFitParameters = resultData?.priorityElements.get(0)?.concentrationResponseSeries?.curveFitParameters
            if (curveFitParameters) {
                if ((curveFitParameters.s0 != null) &&
                        (curveFitParameters.SInf != null) &&
                        (curveFitParameters.hillCoef != null)) {
                    countValues++;
                }
            }
        }
        assert countValues > 1
    }

    void "test retrieve multiple values from specific expt"() {
        given: "That we have identified"
        final Long experimentId = TEST_EIDS.get(0)

        when: "We call for the activities"
        final List<Long> compounds = experimentRestService.compoundsForExperiment(experimentId)
        List<Long> compoundList = compounds.subList(0, 2)
        String etag = compoundRestService.newETag("find experiment 346 data", compoundList); // etag for 3 compounds
        ExperimentData experimentData = this.experimentRestService.activities(experimentId, etag);
        then: "We expect to see non-null activitiy for each compound"
        int countValues = 0
        for (Activity activity : experimentData.activities) {
            ResultData resultData = activity.resultData
            CurveFitParameters curveFitParameters = resultData?.priorityElements.get(0)?.concentrationResponseSeries?.curveFitParameters
            if (curveFitParameters) {
                if ((curveFitParameters.s0 != null) &&
                        (curveFitParameters.SInf != null) &&
                        (curveFitParameters.hillCoef != null)) {
                    countValues++;
                }
            }
        }
        assert countValues > 1
    }

    // an example of a problem
    void "test indirect accumulation of expts use Assays"() {

        given: "That we casn retrieve the expts for project 274" //////////

        List<Long> cartProjectIdList = new ArrayList<Long>()
        cartProjectIdList.add(TEST_PID)
        List<ExperimentSearch> allExperiments = []
        for (Long projectId : cartProjectIdList) {
            List<Assay> assays = projectRestService.findAssaysByProjectId(projectId)
            for (Assay assay : assays) {
                List<ExperimentSearch> experimentList = assayRestService.findExperimentsByAssayId(assay.id)
                allExperiments.addAll(experimentList)

            }
        }

        when: "We define an etag for a compound used in this project"  /////////////

        List<Long> cartCompoundIdList = new ArrayList<Long>()
        cartCompoundIdList.add(TEST_CID)
        String etag = compoundRestService.newETag((new Date()).toString(), cartCompoundIdList);


        then: "when we step through the value in the expt"    ////////

        int dataCount = 0
        for (ExperimentSearch experiment in allExperiments) {

            ExperimentData experimentIterator = experimentRestService.activities(experiment.bardExptId, etag)
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

//    void "tests findActivitiesForCompounds #label"() {
//        given: "That we have created an ETag from a list of CIDs"
//        final String etag = compoundRestService.newETag("Compound ETags", cids);
//        when: "We call the findActivitiesForCompounds() method with the experiment and the ETag"
//        final List<SpreadSheetActivity> activities = molecularSpreadSheetService.findActivitiesForCompounds(experimentId, etag)
//        then: "We expect experiments for each of the assays to be found"
//        assert activities
//        where:
//        label                                    | cids                                                   | experimentId
//        "An existing experiment with activities" | [new Long(164981), new Long(411519), new Long(483860)] | new Long(1326)
//    }

    void "tests retrieveExperimentalData"() {
        when:
        assertNotNull molSpreadSheetData
        then:
        assertDataForSpreadSheetExist(molSpreadSheetData)
    }


    void assertDataForSpreadSheetExist(MolSpreadSheetData molSpreadSheetData) {
        assert molSpreadSheetData != null
        for (int rowCnt in 0..(molSpreadSheetData.rowCount - 1)) {
            for (int colCnt in 0..(molSpreadSheetData.getColumnCount() - 1)) {
                assertNotNull(molSpreadSheetData.mssData["${rowCnt}_${colCnt}"])
            }

        }
    }


    MolSpreadSheetData generateFakeData() {
        molSpreadSheetData = new MolSpreadSheetData()
        molSpreadSheetData.mssHeaders = [new MolSpreadSheetColumnHeader(columnTitle: ["Chemical Structure"]),
                new MolSpreadSheetColumnHeader(molSpreadSheetColSubHeaderList: [new MolSpreadSheetColSubHeader(columnTitle: 'CID')]),
                new MolSpreadSheetColumnHeader(molSpreadSheetColSubHeaderList: [new MolSpreadSheetColSubHeader(columnTitle: 'DNA polymerase (Q9Y253) ADID : 1 IC50')]),
                new MolSpreadSheetColumnHeader(molSpreadSheetColSubHeaderList: [new MolSpreadSheetColSubHeader(columnTitle: 'Serine-protein kinase (Q13315) ADID : 1 IC50')]),
                new MolSpreadSheetColumnHeader(molSpreadSheetColSubHeaderList: [new MolSpreadSheetColSubHeader(columnTitle: 'Tyrosine-DNA phosphodiesterase 1 (Q9NUW8) ADID: 514789')])]
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
        molSpreadSheetData.columnPointer.put(47L, 0)
        molSpreadSheetData.columnPointer.put(48L, 0)
        molSpreadSheetData
    }
}
