package molspreadsheet

import bard.core.HillCurveValue
import bardqueryapi.MolSpreadSheetCellType
import bardqueryapi.MolSpreadSheetController
import bardqueryapi.SpreadSheetActivity
import grails.test.mixin.TestFor
import spock.lang.Specification
import spock.lang.Unroll
import bardqueryapi.ActivityOutcome

/**
 * See the API for {@link grails.test.mixin.support.GrailsUnitTestMixin} for usage instructions
 */
@TestFor(MolSpreadSheetController)
@Unroll
class SpreadSheetActivityStorageUnitSpec   extends Specification {

    MolSpreadSheetCell molSpreadSheetCell

    void setup() {
        molSpreadSheetCell = new MolSpreadSheetCell("0.123",MolSpreadSheetCellType.numeric)
    }

    void tearDown() {
        // Tear down logic here
    }

    void "Smoke test can we build a spreadsheet activity storage data"() {
        when:
        SpreadSheetActivityStorage spreadSheetActivityStorage = new SpreadSheetActivityStorage()

        then:
        assertNotNull(spreadSheetActivityStorage)
    }

    void "Test constraints for molecular spreadsheet data"() {
        given:
        mockForConstraintsTests(SpreadSheetActivityStorage)

        when:
        SpreadSheetActivityStorage spreadSheetActivityStorage = new SpreadSheetActivityStorage()
        spreadSheetActivityStorage.setMolSpreadSheetCell( molSpreadSheetCell )

        then:
        spreadSheetActivityStorage.validate()
        spreadSheetActivityStorage.hasErrors() == false
    }

    /**
     * Demonstrate that we can go through 1000 values without encountering a duplicate hash code
     */

    void "Test hash code and demonstrate that it gives us a nice spread"() {
        when:
        ArrayList<SpreadSheetActivityStorage> activityStorageArrayList = []
        for (i in 1..10) {
            for (j in 1..10) {
                for (k in 1..10) {
                    SpreadSheetActivityStorage spreadSheetActivityStorage = new  SpreadSheetActivityStorage()
                    spreadSheetActivityStorage.eid = i as Long
                    spreadSheetActivityStorage.cid = j as Long
                    spreadSheetActivityStorage.sid = k as Long
                    activityStorageArrayList << spreadSheetActivityStorage
                }
            }
        }
        LinkedHashMap<Integer,Integer>  sheetActivityStorageIntegerLinkedHashMap = []
        for (SpreadSheetActivityStorage spreadSheetActivityStorage in activityStorageArrayList) {
            int hashCode =  spreadSheetActivityStorage.hashCode()
            if (!sheetActivityStorageIntegerLinkedHashMap.containsKey(hashCode)) {
                sheetActivityStorageIntegerLinkedHashMap[hashCode] = 0
            }  else {
                sheetActivityStorageIntegerLinkedHashMap[hashCode] =  sheetActivityStorageIntegerLinkedHashMap[hashCode]+1
            }
        }

        then:
        for (Integer key in sheetActivityStorageIntegerLinkedHashMap.keySet()) {
            assert  sheetActivityStorageIntegerLinkedHashMap[key] <    1
        }
    }




    void "Test constructor"() {
        given:

//        SpreadSheetActivityStorage spreadSheetActivityStorage = new SpreadSheetActivityStorage( )
//        spreadSheetActivityStorage.eid = 4
//        spreadSheetActivityStorage.cid = 5
        final SpreadSheetActivity spreadSheetActivity = new SpreadSheetActivity()
        spreadSheetActivity.sid = 1 as Long
        spreadSheetActivity.activityOutcome = ActivityOutcome.ACTIVE
        spreadSheetActivity.potency = 3 as Double
        final HillCurveValue hillCurveValue = new  HillCurveValue()
        hillCurveValue.id = 1
        hillCurveValue.sinf = 1d
        hillCurveValue.s0  = 1d
        hillCurveValue.slope = 1d
        hillCurveValue.coef = 1d
        hillCurveValue.conc  = [1d]
        hillCurveValue.response = [1d]
        spreadSheetActivity.hillCurveValue = hillCurveValue

        when:
        SpreadSheetActivityStorage spreadSheetActivityStorage = new SpreadSheetActivityStorage( spreadSheetActivity )

        then:
        assertNotNull(spreadSheetActivityStorage)
        assert spreadSheetActivityStorage.sid==1
        assert spreadSheetActivityStorage.activityOutcome==ActivityOutcome.ACTIVE
        assertNotNull( spreadSheetActivityStorage.hillCurveValueS0)
        assertNotNull( spreadSheetActivityStorage.hillCurveValueResponse)
        assertNotNull( spreadSheetActivityStorage.hillCurveValueSlope)
    }




        void "Test equals"() {
        given:
        final SpreadSheetActivityStorage spreadSheetActivityStorage1 = new SpreadSheetActivityStorage(eid: 47l, cid: 47l, sid: 47l )


        when:
        SpreadSheetActivityStorage spreadSheetActivityStorage2 = new SpreadSheetActivityStorage( )
        spreadSheetActivityStorage2.eid = eid
        spreadSheetActivityStorage2.cid = cid
        spreadSheetActivityStorage2.sid = sid

        then:
        spreadSheetActivityStorage2.equals(spreadSheetActivityStorage1) == equality

        where:
        eid         | cid           | sid           | equality
        48 as Long  | 47 as Long    | 47 as Long    | false
        47 as Long  | 48 as Long    | 47 as Long    | false
        47 as Long  | 47 as Long    | 48 as Long    | false
        47 as Long  | 47 as Long    | 47 as Long    | true

    }

}
