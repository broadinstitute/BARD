package molspreadsheet

import bard.core.rest.RESTAssayService
import bard.core.rest.RESTCompoundService
import bard.core.rest.RESTExperimentService
import bard.core.rest.RESTProjectService
import bardqueryapi.QueryServiceWrapper
import com.metasieve.shoppingcart.ShoppingCartService
import grails.plugin.spock.IntegrationSpec
import org.junit.After
import org.junit.Before
import querycart.CartAssay
import querycart.CartCompound
import querycart.CartProject
import querycart.QueryCartService
import spock.lang.Unroll
import bard.core.*

import static junit.framework.Assert.assertNotNull

@Unroll
class MolecularSpreadSheetServiceIntegrationSpec extends IntegrationSpec {

    MolecularSpreadSheetService molecularSpreadSheetService
    MolSpreadSheetData molSpreadSheetData = generateFakeData()
    RESTCompoundService restCompoundService
    RESTExperimentService restExperimentService
    RESTProjectService restProjectService
    RESTAssayService restAssayService
    QueryServiceWrapper queryServiceWrapper
    QueryCartService queryCartService
    ShoppingCartService shoppingCartService


    @Before
    void setup() {
        this.restExperimentService = molecularSpreadSheetService.queryServiceWrapper.restExperimentService
        this.restCompoundService = molecularSpreadSheetService.queryServiceWrapper.restCompoundService
        this.restProjectService = molecularSpreadSheetService.queryServiceWrapper.restProjectService
        this.restAssayService = molecularSpreadSheetService.queryServiceWrapper.restAssayService
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




    void "test fillInTheMissingCellsAndConvertToExpandedMatrix"() {
        when: "we have a molecularSpreadSheetService"
        Map<String,MolSpreadSheetCell> dataMap = [:]
        dataMap["0_3"]  =new MolSpreadSheetCell()
        MolSpreadSheetCell flawedMolSpreadSheetCell =new MolSpreadSheetCell()
        flawedMolSpreadSheetCell.spreadSheetActivityStorage=new SpreadSheetActivityStorage()
        dataMap["0_4"]  =flawedMolSpreadSheetCell
        molecularSpreadSheetService.fillInTheMissingCellsAndConvertToExpandedMatrix(molSpreadSheetData,dataMap)

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

        queryCartService.addToShoppingCart(new CartAssay("Assay Definition: Confirmation Concentration-Response Assay for Inhibitors of Human Muscle isoform 2 Pyruvate Kinase",364L ))
        queryCartService.addToShoppingCart(new CartCompound("CC1=CC=C(O1)C1=C(NC2=CC=C(C)C=C2)N2C(C=CC=C2C)=N1", "HMS1817I15", 4085914L))

        MolSpreadSheetData molSpreadSheetData
        if (molecularSpreadSheetService.weHaveEnoughDataToMakeASpreadsheet()) {
            molSpreadSheetData = molecularSpreadSheetService.retrieveExperimentalData()
        }
        assertNotNull molSpreadSheetData

    }





    void "test populateMolSpreadSheetData"() {
        when: "we have a molecularSpreadSheetService"
        assertNotNull molecularSpreadSheetService
//        List<CartProject> cartProjectList = []
//        cartProjectList << new CartProject("Summary of Flow Cytometry HTS of Small Molecules that Regulate V-ATPase Proton Transport in Yeast", 364 as Long)
//        List<Experiment> finalExperimentList = molecularSpreadSheetService.cartProjectsToExperiments(cartProjectList)

        Assay assay = restAssayService.get(2199 as Long)
        final ServiceIterator<Experiment> serviceIterator = restAssayService.iterator(assay, Experiment)
        Collection<Experiment> finalExperimentList = serviceIterator.collect()
        assay = restAssayService.get(730 as Long)
        serviceIterator = restAssayService.iterator(assay, Experiment)
        finalExperimentList.addAll(serviceIterator.collect())
        MolSpreadSheetData molSpreadSheetData = new MolSpreadSheetData()
        100.times{
            molSpreadSheetData.mssHeaders << []
        }

        Object etag = this.restCompoundService.newETag((new Date()).toTimestamp().toString(), [4540 as Long,4544 as Long,4549 as Long,4552 as Long])

        List<SpreadSheetActivity> spreadSheetActivityList = molecularSpreadSheetService.extractMolSpreadSheetData(molSpreadSheetData,
                finalExperimentList,
                etag)
        molSpreadSheetData.rowPointer[4540 as Long]  =0
        molSpreadSheetData.rowPointer[4544 as Long]  =1
        molSpreadSheetData.rowPointer[4549 as Long]  =2
        Map<String,MolSpreadSheetCell> dataMap = [:]

        molecularSpreadSheetService.populateMolSpreadSheetData(molSpreadSheetData,
                finalExperimentList,
                spreadSheetActivityList,
                dataMap)



        then: "we should be able to generate a list of spreadsheet activity elements"
        assertNotNull molSpreadSheetData.mssData
        assert molSpreadSheetData.mssData.size()==0
    }



    void "test extractMolSpreadSheetData"() {
        when: "we have a molecularSpreadSheetService"
        assertNotNull molecularSpreadSheetService
        Assay assay = restAssayService.get(519 as Long)
        final ServiceIterator<Experiment> serviceIterator = restAssayService.iterator(assay, Experiment)
        Collection<Experiment> experimentList = serviceIterator.collect()
        MolSpreadSheetData molSpreadSheetData = new MolSpreadSheetData()
        5.times{
            molSpreadSheetData.mssHeaders << []
        }
        List<Long> compounds = []
        //compounds << 364 as Long
        Object etag = this.restCompoundService.newETag((new Date()).toTimestamp().toString(), [1074927 as Long,1074929 as Long,1077518 as Long])
        List<SpreadSheetActivity> spreadSheetActivityList = molecularSpreadSheetService.extractMolSpreadSheetData(molSpreadSheetData,
                experimentList,
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
        when: "we have a molecularSpreadSheetService"
        assertNotNull molecularSpreadSheetService
        Assay assay = restAssayService.get(346 as Long)
        final ServiceIterator<Experiment> serviceIterator = restAssayService.iterator(assay, Experiment)
        Collection<Experiment> experimentList = serviceIterator.collect()
        MolSpreadSheetData molSpreadSheetData = new MolSpreadSheetData()
        5.times{
            molSpreadSheetData.mssHeaders << []
        }
        List<Long> compounds = []
        //compounds << 364 as Long
        List<SpreadSheetActivity> spreadSheetActivityList = molecularSpreadSheetService.extractMolSpreadSheetData(molSpreadSheetData,
                experimentList)

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
        when: "we have a molecularSpreadSheetService"
        assertNotNull molecularSpreadSheetService
        Assay assay = restAssayService.get(519 as Long)
        final ServiceIterator<Experiment> serviceIterator = restAssayService.iterator(assay, Experiment)
        Collection<Experiment> experimentList = serviceIterator.collect()
        MolSpreadSheetData molSpreadSheetData = new MolSpreadSheetData()
        4.times{
            molSpreadSheetData.mssHeaders << []
        }
        Object etag = this.restCompoundService.newETag((new Date()).toTimestamp().toString(), [364 as Long])
        List<SpreadSheetActivity> spreadSheetActivityList = molecularSpreadSheetService.extractMolSpreadSheetData(molSpreadSheetData,
                experimentList,
                etag)

        then: "we should be able to generate a list of spreadsheet activity elements"
        assertNotNull spreadSheetActivityList
        assert spreadSheetActivityList.size() == 0
     }




    void "test that we can create an ETag from a list of experiments"() {
        when: "we have list of cart compounds"


        assertNotNull molecularSpreadSheetService
        List<CartProject> cartProjectList = []
        cartProjectList << new CartProject("Summary of Flow Cytometry HTS of Small Molecules that Regulate V-ATPase Proton Transport in Yeast", 364)
        List<Experiment> finalExperimentList = molecularSpreadSheetService.cartProjectsToExperiments(cartProjectList)
        Object eTag = molecularSpreadSheetService.retrieveImpliedCompoundsEtagFromAssaySpecification (finalExperimentList)

        then: "we should be able to build and Etag from them"
        assertNotNull eTag
    }


    void "test an empty ETag from a list of null experiments"() {
        when: "we have list of cart compounds"
        assertNotNull molecularSpreadSheetService
        List<Experiment> experimentList = []
        experimentList << new Experiment()
        Object eTag = molecularSpreadSheetService.retrieveImpliedCompoundsEtagFromAssaySpecification (experimentList)

        then: "we should be able to build and Etag from them"
        assertNotNull eTag
    }




    void "test that we can create an ETag from a list of  current compounds"() {
        when: "we have list of cart compounds"
        assertNotNull molecularSpreadSheetService
        List<CartCompound> cartCompoundList = []
        cartCompoundList.add(new CartCompound("CC(=O)C1=C(O)C(C)=C(O)C(CC2=C(O)C3=C(OC(C)(C)C=C3)C(C(=O)\\C=C\\C3=CC=CC=C3)=C2O)=C1O", "Rottlerin", 5281847))
        Object eTag = molecularSpreadSheetService.generateETagFromCartCompounds(cartCompoundList)

        then: "we should be able to build and Etag from them"
        assertNotNull eTag
    }





    void "test cartCompoundsToExperiments"() {
        when: "we have a molecularSpreadSheetService"
        assertNotNull molecularSpreadSheetService
        List<CartCompound> cartCompoundList = []
        cartCompoundList.add(new CartCompound("CC(=O)C1=C(O)C(C)=C(O)C(CC2=C(O)C3=C(OC(C)(C)C=C3)C(C(=O)\\C=C\\C3=CC=CC=C3)=C2O)=C1O", "Rottlerin", 5281847))
        //List<Experiment> originalExperimentList =  []
        List<Experiment> finalExperimentList = molecularSpreadSheetService.cartCompoundsToExperiments(cartCompoundList)

        then: "we should be able to generate a list of spreadsheet activity elements"
        assertNotNull finalExperimentList
    }



    void "test empty cartCompoundsToExperiments"() {
        when: "we have a molecularSpreadSheetService"
        assertNotNull molecularSpreadSheetService
        List<CartCompound> cartCompoundList = []
        List<Experiment> finalExperimentList = molecularSpreadSheetService.cartCompoundsToExperiments(cartCompoundList)

        then: "we should be able to generate a list of spreadsheet activity elements"
        assert finalExperimentList.size()==0
    }



    void "test cartProjectsToExperiments"() {
        when: "we have a molecularSpreadSheetService"
        assertNotNull molecularSpreadSheetService
        List<CartProject> cartProjectList = []
        cartProjectList.add(new CartProject("Summary of Flow Cytometry HTS of Small Molecules that Regulate V-ATPase Proton Transport in Yeast", 364))
        List<Experiment> finalExperimentList = molecularSpreadSheetService.cartProjectsToExperiments(cartProjectList)

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
        List<Experiment> experiments = molecularSpreadSheetService.cartAssaysToExperiments(null, givenCartAssays)
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
        List<Experiment> experiments = molecularSpreadSheetService.cartAssaysToExperiments(null, givenCartAssays)
        then: "We expect experiments for each of the assays to be found"
        assert experiments.size()==0
    }



    void "tests empty cartAssaysToExperiments with pre-existing experiment"() {
        given: "That a list of CartAssay objects have been created"
        List<Experiment> experimentList = []
        Experiment experiment = new Experiment ()
        experimentList << experiment
        final List<CartAssay> givenCartAssays = []
        when: "We call the cartAssaysToExperiments() with the given list of assay carty objects"
        List<Experiment> experiments = molecularSpreadSheetService.cartAssaysToExperiments(experimentList, givenCartAssays)
        then: "We expect experiments for each of the assays to be found"
        assert experiments.size()==1
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
        Value experimentValue = (Value) collect.iterator().next()
        when: "We call the extractActivitiesFromExperiment method with the experimentValue"
        SpreadSheetActivity spreadSheetActivity = molecularSpreadSheetService.extractActivitiesFromExperiment(experimentValue, experimentId)
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
        //final List<SpreadSheetActivity> activities = experiment.
        then: "We expect experiments for each of the assays to be found"
        assert value
    }

    void "test retrieve multiple values"() {
        given: "That we have identified experiemnt 346"
        final Long experimentId = new Long(346)
        Experiment experiment = restExperimentService.get(experimentId)
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
            SpreadSheetActivity spreadSheetActivity = molecularSpreadSheetService.extractActivitiesFromExperiment(experimentValue, experimentId)
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
        Experiment experiment = restExperimentService.get(experimentId)
        final ServiceIterator<Compound> compoundServiceIterator = restExperimentService.compounds(experiment)
        when: "We call for the activities"
        assert experiment
        List<Compound> compoundList = compoundServiceIterator.next(1)
        Object etag = restCompoundService.newETag("find experiment 346 data", compoundList*.id); // etag for 3 compounds
        ServiceIterator<Value> experimentIterator = this.restExperimentService.activities(experiment, etag);
        then: "We expect to see non-null activitiy for each compound"
        int countValues = 0
        while (experimentIterator.hasNext()) {
            Value experimentValue = experimentIterator.next()
            SpreadSheetActivity spreadSheetActivity = molecularSpreadSheetService.extractActivitiesFromExperiment(experimentValue, experimentId)
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
        final Collection<Project> projects = restProjectService.get(cartProjectIdList)
        List<Experiment> allExperiments = []
        for (Project project : projects) {
            final ServiceIterator<Assay> serviceIterator = restProjectService.iterator(project, Assay.class)
            Collection<Assay> assays = serviceIterator.collect()
            for (Assay assay : assays) {
                 final ServiceIterator<Experiment> experimentIterator = restAssayService.iterator(assay, Experiment.class)
                Collection<Experiment> experimentList = experimentIterator.collect()
                allExperiments.addAll(experimentList)

            }
        }

        when: "We define an etag for a compound used in this project"  /////////////

        List<Long> cartCompoundIdList = new ArrayList<Long>()
        cartCompoundIdList.add(new Long(5281847))
        Object etag = restCompoundService.newETag((new Date()).toString(), cartCompoundIdList);


        then: "when we step through the value in the expt"    ////////

        int dataCount = 0
        for (Experiment experiment in allExperiments) {

            ServiceIterator<Value> experimentIterator = restExperimentService.activities(experiment, etag)
            Value experimentValue
            while (experimentIterator.hasNext()) {
                experimentValue = experimentIterator.next()
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
//        final Collection<Project> projects = restProjectService.get(cartProjectIdList)
//        List<Experiment> allExperiments = []
//        for (Project project : projects) {
//            final ServiceIterator<Assay> serviceIterator = restProjectService.iterator(project, Assay.class)
//            Collection<Assay> assays = serviceIterator.collect()
//            for (Assay assay : assays) {
//                //println "ASSAY: " + assay.id
//                final ServiceIterator<Experiment> experimentIterator = restAssayService.iterator(assay, Experiment.class)
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
