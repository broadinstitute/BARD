package bardqueryapi

import bard.core.adapter.CompoundAdapter
import bard.core.rest.RESTAssayService
import bard.core.rest.RESTCompoundService
import bard.core.rest.RESTExperimentService
import bard.core.rest.RESTProjectService
import grails.plugin.spock.IntegrationSpec
import molspreadsheet.MolSpreadSheetCell
import molspreadsheet.MolSpreadSheetData
import org.junit.After
import org.junit.Before
import spock.lang.Unroll
import bard.core.*

import static junit.framework.Assert.assertNotNull
import com.metasieve.shoppingcart.ShoppingCartService

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
        this.queryCartService =  molecularSpreadSheetService.queryCartService
        this.shoppingCartService = molecularSpreadSheetService.shoppingCartService
    }

    @After
    void tearDown() {

    }


    void "test retrieveExperimentalData degenerate case"() {
        when:  "we have a molecularSpreadSheetService"
        assertNotNull molecularSpreadSheetService
        MolSpreadSheetData molSpreadSheetData = molecularSpreadSheetService.retrieveExperimentalData()

        then: "we should be able to generate the core molSpreadSheetData, with valid empty data holders"
        assertNotNull molSpreadSheetData
        assertNotNull molSpreadSheetData.mssData
        assertNotNull molSpreadSheetData.rowPointer
        assertNotNull molSpreadSheetData.columnPointer
        assertNotNull molSpreadSheetData.mssHeaders
        assert molSpreadSheetData.mssData.size() == 0
        assert molSpreadSheetData.rowPointer.size() == 0
        assert molSpreadSheetData.columnPointer.size() == 0
        assert molSpreadSheetData.mssHeaders.size() == 3
    }


    void "test weHaveEnoughDataToMakeASpreadsheet()"() {
        when:  "we have a molecularSpreadSheetService"
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
       assert dataIsSufficient == molecularSpreadSheetService.weHaveEnoughDataToMakeASpreadsheet ()


        where:
        dataIsSufficient    | cartAssay                     | cartProject                       | cartCompound
        false               | null                          | null                              | null
        true                | new CartAssay(assayTitle:"A") | null                              | null
        true                | null                          | new CartProject(projectName:"P")  | null
        true                | null                          | null                              | new CartCompound(smiles:"C",name:"c",compoundId:1)
        true                | null                          | new CartProject(projectName:"P")  | new CartCompound(smiles:"C",name:"c",compoundId:1)
        true                | new CartAssay(assayTitle:"A") | null                              | new CartCompound(smiles:"C",name:"c",compoundId:1)
        true                | new CartAssay(assayTitle:"A") | new CartProject(projectName:"P")  | null
        true                | new CartAssay(assayTitle:"A") | new CartProject(projectName:"P")  | new CartCompound(smiles:"C",name:"c",compoundId:1)
        false               | null                          | null                              | null
    }



    void "test findExperimentDataById #label"() {

        when: "We call the findExperimentDataById method with the experimentId #experimentId"
        final Map experimentDataMap = molecularSpreadSheetService.findExperimentDataById(experimentId, top, skip)

        then: "We get back the expected map"
        assert experimentDataMap
        final Long totalActivities = experimentDataMap.total
        final ExperimentValues.ExperimentRole role = experimentDataMap.role
        //println role
        //println totalActivities
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
        Value experimentValue = (Value) collect.iterator().next()
        when: "We call the extractActivitiesFromExperiment method with the experimentValue"
        SpreadSheetActivity spreadSheetActivity = molecularSpreadSheetService.extractActivitiesFromExperiment(experimentValue, experimentId)
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
            HillCurveValue hillCurveValue = spreadSheetActivity.hillCurveValue
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
            HillCurveValue hillCurveValue = spreadSheetActivity.hillCurveValue
            if ((hillCurveValue.s0 != null) &&
                    (hillCurveValue.sinf != null) &&
                    (hillCurveValue.coef != null)) {
                countValues++;
            }
        }
        assert countValues > 1
    }

    // an example of a problem
    void "test indirect accumulation of expts2"() {

        given: "That we casn retrieve the expts for project 274" //////////

//        List<Long> cartProjectIdList = new ArrayList<Long>()
//        cartProjectIdList.add(new Long(274))
        final Project project = queryServiceWrapper.restProjectService.get(new Long(274))
        List<Experiment> allExperiments = []
        //for (Project project : projects) {
        final ServiceIterator<Assay> serviceIterator = queryServiceWrapper.restProjectService.iterator(project, Assay)
        Collection<Assay> assays = serviceIterator.collect()
        for (Assay assay : assays) {
            //println "ASSAY: " + assay.id
            final ServiceIterator<Experiment> experimentIterator = queryServiceWrapper.restAssayService.iterator(assay, Experiment)
            Collection<Experiment> experimentList = experimentIterator.collect()
            allExperiments.addAll(experimentList)

        }
        //}
        List<Long> cartCompoundIdList = new ArrayList<Long>()
        cartCompoundIdList.add(new Long(5281847))
        // Object etag = queryServiceWrapper.restCompoundService.newETag("Test", cartCompoundIdList);
        when: "We define an etag for a compound used in this project"  /////////////
        int dataCount = 0
        for (Experiment experiment in allExperiments) {
            //Experiment newExp = queryServiceWrapper.restExperimentService.get(experiment.id)
            final ServiceIterator<Compound> compoundIterators = restExperimentService.compounds(experiment)
            final Collection<Compound> compoundsThatWereTestedInThisExperiment = compoundIterators.collect()
            for (Compound comp : compoundsThatWereTestedInThisExperiment) {
                CompoundAdapter c = new CompoundAdapter(comp)
                if (cartCompoundIdList.contains(new Long(c.pubChemCID))) {
                    ServiceIterator<Value> experimentIterator = queryServiceWrapper.restExperimentService.activities(experiment)

                    Value experimentValue
                    while (experimentIterator.hasNext()) {
                        experimentValue = experimentIterator.next()
                        dataCount++
                    }
                }
            }


        }



        then: "when we step through the value in the expt"    ////////

        // we expect tyo see some data
        assert dataCount > 0
        //println dataCount
    }

//    // an example of a problem
//    void "test indirect accumulation of expts use Assays"() {
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
//                 final ServiceIterator<Experiment> experimentIterator = restAssayService.iterator(assay, Experiment.class)
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
        for (int rowCnt in 0..(molSpreadSheetData.getRowCount() - 1)) {
            for (int colCnt in 0..(molSpreadSheetData.mssHeaders.size() - 1)) {
                assertNotNull(molSpreadSheetData.mssData["${rowCnt}_${colCnt}"])
            }

        }
    }


    MolSpreadSheetData generateFakeData() {
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
}
