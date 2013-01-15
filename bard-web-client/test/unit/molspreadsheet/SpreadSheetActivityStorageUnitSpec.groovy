package molspreadsheet

import bard.core.HillCurveValue
import bard.core.rest.spring.experiment.ConcentrationResponseSeries
import bard.core.rest.spring.experiment.PriorityElement
import bardqueryapi.ActivityOutcome
import grails.test.mixin.TestFor
import spock.lang.Specification
import spock.lang.Unroll
import bard.core.rest.spring.experiment.CurveFitParameters
import bard.core.rest.spring.experiment.ConcentrationResponsePoint

/**
 * See the API for {@link grails.test.mixin.support.GrailsUnitTestMixin} for usage instructions
 */
@TestFor(MolSpreadSheetController)
@Unroll
class SpreadSheetActivityStorageUnitSpec extends Specification {

    MolSpreadSheetCell molSpreadSheetCell
    HillCurveValueHolder hillCurveValueHolder

    void setup() {
        molSpreadSheetCell = new MolSpreadSheetCell("0.123", MolSpreadSheetCellType.numeric)
        hillCurveValueHolder = new HillCurveValueHolder()
    }

    void tearDown() {
        // Tear down logic here
    }

    void "Smoke test can we build a spreadsheet activity storage data"() {
        when:
        SpreadSheetActivityStorage spreadSheetActivityStorage = new SpreadSheetActivityStorage()
//        spreadSheetActivityStorage.hillCurveValueHolderList <<  hillCurveValueHolder

        then:
        assertNotNull(spreadSheetActivityStorage)
    }

    void "Test constraints for molecular spreadsheet data"() {
        given:
        mockForConstraintsTests(SpreadSheetActivityStorage)

        when:
        SpreadSheetActivityStorage spreadSheetActivityStorage = new SpreadSheetActivityStorage()
        spreadSheetActivityStorage.setMolSpreadSheetCell(molSpreadSheetCell)

        then:
        spreadSheetActivityStorage.validate()
        !spreadSheetActivityStorage.hasErrors()
    }

    void "test hashCode all branches"() {
        given:
        SpreadSheetActivityStorage spreadSheetActivityStorage =
            new SpreadSheetActivityStorage(version: 2, id: 2,
                    molSpreadSheetCell: new MolSpreadSheetCell())
        when:
        final int hashCode = spreadSheetActivityStorage.hashCode()
        then:
        assert hashCode
    }
    /**
     * Demonstrate that we can go through 1000 values without encountering a duplicate hash code
     */
    void "Test hash code and demonstrate that it gives us a nice spread"() {
        when:
        List<SpreadSheetActivityStorage> activityStorageArrayList = []
        for (i in 1..10) {
            for (j in 1..10) {
                for (k in 1..10) {
                    SpreadSheetActivityStorage spreadSheetActivityStorage = new SpreadSheetActivityStorage()
                    spreadSheetActivityStorage.eid = i as Long
                    spreadSheetActivityStorage.cid = j as Long
                    spreadSheetActivityStorage.sid = k as Long
                    activityStorageArrayList << spreadSheetActivityStorage
                }
            }
        }
        Map<Integer, Integer> sheetActivityStorageIntegerLinkedHashMap = [:]
        for (SpreadSheetActivityStorage spreadSheetActivityStorage in activityStorageArrayList) {
            int hashCode = spreadSheetActivityStorage.hashCode()
            if (sheetActivityStorageIntegerLinkedHashMap.containsKey(hashCode)) {
                sheetActivityStorageIntegerLinkedHashMap[hashCode] = sheetActivityStorageIntegerLinkedHashMap[hashCode] + 1
            } else {
                sheetActivityStorageIntegerLinkedHashMap[hashCode] = 0

            }
        }

        then:
        for (Integer key in sheetActivityStorageIntegerLinkedHashMap.keySet()) {
            assert sheetActivityStorageIntegerLinkedHashMap[key] < 1
        }
    }

    void "Test constructor No HillCurve"() {
        given:
        final SpreadSheetActivity spreadSheetActivity = new SpreadSheetActivity()
        spreadSheetActivity.sid = 1 as Long
        spreadSheetActivity.activityOutcome = ActivityOutcome.ACTIVE
        spreadSheetActivity.potency = 3 as Double

        when:
        SpreadSheetActivityStorage spreadSheetActivityStorage = new SpreadSheetActivityStorage(spreadSheetActivity)

        then:
        assertNotNull(spreadSheetActivityStorage)
        assert spreadSheetActivityStorage.sid == 1
        assert spreadSheetActivityStorage.activityOutcome == ActivityOutcome.ACTIVE
//        assertNull(spreadSheetActivityStorage.hillCurveValueS0)
//        assertNull(spreadSheetActivityStorage.hillCurveValueResponse)
//        assertNull(spreadSheetActivityStorage.hillCurveValueSlope)
    }


    void "Test SpreadSheetActivityStorage constructor"() {
        given:
        final SpreadSheetActivity spreadSheetActivity = new SpreadSheetActivity()
        spreadSheetActivity.sid = 1 as Long
        spreadSheetActivity.activityOutcome = ActivityOutcome.ACTIVE
        spreadSheetActivity.potency = 3 as Double
        final HillCurveValue hillCurveValue = new HillCurveValue()
        hillCurveValue.id = 1
        hillCurveValue.sinf = 1d
        hillCurveValue.s0 = 1d
        hillCurveValue.slope = 1d
        hillCurveValue.coef = 1d
        hillCurveValue.conc = [1d]
        hillCurveValue.response = [1d]

        when:
        SpreadSheetActivityStorage spreadSheetActivityStorage = new SpreadSheetActivityStorage(spreadSheetActivity)

        then:
        assertNotNull(spreadSheetActivityStorage)
        assert spreadSheetActivityStorage.sid == 1
        assert spreadSheetActivityStorage.activityOutcome == ActivityOutcome.ACTIVE
    }


    void "Test  MolSpreadSheetCell constructor in case of multiple identical column names"() {
        given:
        final SpreadSheetActivity spreadSheetActivity = new SpreadSheetActivity()
        spreadSheetActivity.sid = 1 as Long
        spreadSheetActivity.activityOutcome = ActivityOutcome.ACTIVE
        spreadSheetActivity.potency = 3 as Double
        PriorityElement priorityElement = new PriorityElement(displayName: "testName", value: 0.47d)
        spreadSheetActivity.priorityElementList = [priorityElement,priorityElement]

        when:
        MolSpreadSheetCell molSpreadSheetCell =  new  MolSpreadSheetCell(spreadSheetActivity)

        then:
        assertNotNull(molSpreadSheetCell)
        molSpreadSheetCell.spreadSheetActivityStorage.hillCurveValueHolderList.size()==2
    }


    void "Test  MolSpreadSheetCell constructor in case of no value"() {
        given:
        final SpreadSheetActivity spreadSheetActivity = new SpreadSheetActivity()
        spreadSheetActivity.sid = 1 as Long
        spreadSheetActivity.activityOutcome = ActivityOutcome.ACTIVE
        spreadSheetActivity.potency = 3 as Double
        PriorityElement priorityElement = new PriorityElement(displayName: "testName")
        spreadSheetActivity.priorityElementList = [priorityElement]

        when:
        MolSpreadSheetCell molSpreadSheetCell =  new  MolSpreadSheetCell(spreadSheetActivity)

        then:
        assertNotNull(molSpreadSheetCell)
        molSpreadSheetCell.spreadSheetActivityStorage.hillCurveValueHolderList.size()==0
    }


    void "Test  MolSpreadSheetCell constructor in case of no displayName"() {
        given:
        final SpreadSheetActivity spreadSheetActivity = new SpreadSheetActivity()
        spreadSheetActivity.sid = 1 as Long
        spreadSheetActivity.activityOutcome = ActivityOutcome.ACTIVE
        spreadSheetActivity.potency = 3 as Double
        PriorityElement priorityElement = new PriorityElement(value: 0.47d)
        priorityElement.responseUnit='nM'
        spreadSheetActivity.priorityElementList = [priorityElement]

        when:
        MolSpreadSheetCell molSpreadSheetCell =  new  MolSpreadSheetCell(spreadSheetActivity)

        then:
        assertNotNull(molSpreadSheetCell)
        molSpreadSheetCell.spreadSheetActivityStorage.hillCurveValueHolderList.size()==1
        molSpreadSheetCell.spreadSheetActivityStorage.hillCurveValueHolderList[0].identifier=='nM'
    }
    void "Test  MolSpreadSheetCell constructor in case of no displayName and no response unit"() {
        given:
        final SpreadSheetActivity spreadSheetActivity = new SpreadSheetActivity()
        spreadSheetActivity.sid = 1 as Long
        spreadSheetActivity.activityOutcome = ActivityOutcome.ACTIVE
        spreadSheetActivity.potency = 3 as Double
        PriorityElement priorityElement = new PriorityElement(value: 0.47d)
        spreadSheetActivity.priorityElementList = [priorityElement]

        when:
        MolSpreadSheetCell molSpreadSheetCell =  new  MolSpreadSheetCell(spreadSheetActivity)

        then:
        assertNotNull(molSpreadSheetCell)
        molSpreadSheetCell.spreadSheetActivityStorage.hillCurveValueHolderList.size()==1
        molSpreadSheetCell.spreadSheetActivityStorage.hillCurveValueHolderList[0].identifier==' '
    }



    void "Test  MolSpreadSheetCell constructor with curve parms"() {
        given:
        final SpreadSheetActivity spreadSheetActivity = new SpreadSheetActivity()
        spreadSheetActivity.sid = 1 as Long
        spreadSheetActivity.activityOutcome = ActivityOutcome.ACTIVE
        spreadSheetActivity.potency = 3 as Double
        PriorityElement priorityElement = new PriorityElement(value: 0.47d,displayName: "colname")
        ConcentrationResponseSeries concentrationResponseSeries = new ConcentrationResponseSeries()
        concentrationResponseSeries.concentrationResponsePoints = []
        concentrationResponseSeries.concentrationResponsePoints  << new ConcentrationResponsePoint(testConcentration: 0.1d, value: 0.1d)
        concentrationResponseSeries.concentrationResponsePoints  << new ConcentrationResponsePoint(testConcentration: 0.2d, value: 0.2d)
        CurveFitParameters curveFitParameters = new CurveFitParameters(s0: 1.0d,
                sInf: 1.0d,
                hillCoef: 1.0d)
        concentrationResponseSeries.curveFitParameters =  curveFitParameters
        priorityElement.concentrationResponseSeries = concentrationResponseSeries
        spreadSheetActivity.priorityElementList = [priorityElement]

        when:
        MolSpreadSheetCell molSpreadSheetCell =  new  MolSpreadSheetCell(spreadSheetActivity)

        then:
        assertNotNull(molSpreadSheetCell)
        molSpreadSheetCell.spreadSheetActivityStorage.hillCurveValueHolderList.size()==1
        molSpreadSheetCell.spreadSheetActivityStorage.hillCurveValueHolderList[0].identifier=='colname'
        molSpreadSheetCell.spreadSheetActivityStorage.hillCurveValueHolderList[0].s0==1.0d
        molSpreadSheetCell.spreadSheetActivityStorage.hillCurveValueHolderList[0].conc.size()==2
        molSpreadSheetCell.spreadSheetActivityStorage.hillCurveValueHolderList[0].response.size()==2
    }




    void "Test  MolSpreadSheetCell constructor in case of one column names"() {
        given:
        final SpreadSheetActivity spreadSheetActivity = new SpreadSheetActivity()
        spreadSheetActivity.sid = 1 as Long
        spreadSheetActivity.activityOutcome = ActivityOutcome.ACTIVE
        spreadSheetActivity.potency = 3 as Double
        PriorityElement priorityElement = new PriorityElement(displayName: "testName", value: 0.47d)
        spreadSheetActivity.priorityElementList = [priorityElement]

        when:
        MolSpreadSheetCell molSpreadSheetCell =  new  MolSpreadSheetCell(spreadSheetActivity)

        then:
        assertNotNull(molSpreadSheetCell)
        molSpreadSheetCell.spreadSheetActivityStorage.hillCurveValueHolderList.size()==1
    }



    void "Test  MolSpreadSheetCell constructor with qualifiers"() {
        given:
        final SpreadSheetActivity spreadSheetActivity = new SpreadSheetActivity()
        spreadSheetActivity.sid = 1 as Long
        spreadSheetActivity.activityOutcome = ActivityOutcome.ACTIVE
        spreadSheetActivity.potency = 3 as Double
        PriorityElement priorityElement = new PriorityElement(displayName: "testName", value: 0.47d)
        spreadSheetActivity.priorityElementList = [priorityElement]


        when:
        priorityElement.qualifier = qualifier
        MolSpreadSheetCell molSpreadSheetCell =  new  MolSpreadSheetCell(spreadSheetActivity)

        then:
        assertNotNull(molSpreadSheetCell)
        molSpreadSheetCell.molSpreadSheetCellType ==  TypeOfMolSpreadSheetCell

        where:
        qualifier       | TypeOfMolSpreadSheetCell
        ">"             | MolSpreadSheetCellType.greaterThanNumeric
        "<"             | MolSpreadSheetCellType.lessThanNumeric
        "="             | MolSpreadSheetCellType.numeric
    }




    void "Test equals"() {
        given:
        final SpreadSheetActivityStorage spreadSheetActivityStorage1 = new SpreadSheetActivityStorage(eid: 47l, cid: 47l, sid: 47l)


        when:
        SpreadSheetActivityStorage spreadSheetActivityStorage2 = new SpreadSheetActivityStorage()
        spreadSheetActivityStorage2.eid = eid
        spreadSheetActivityStorage2.cid = cid
        spreadSheetActivityStorage2.sid = sid

        then:
        spreadSheetActivityStorage2.equals(spreadSheetActivityStorage1) == equality

        where:
        eid        | cid        | sid        | equality
        48 as Long | 47 as Long | 47 as Long | false
        47 as Long | 48 as Long | 47 as Long | false
        47 as Long | 47 as Long | 48 as Long | false
        47 as Long | 47 as Long | 47 as Long | true

    }

    void "Test extended equals #label"() {
        when:
        final boolean equals = spreadSheetActivityStorage.equals(otherSpreadSheetActivityStorage)

        then:
        equals == equality
        where:
        label               | spreadSheetActivityStorage       | otherSpreadSheetActivityStorage | equality
        "Other is null"     | new SpreadSheetActivityStorage() | null                            | false
        "Different classes" | new SpreadSheetActivityStorage() | 20                              | false

    }

}
