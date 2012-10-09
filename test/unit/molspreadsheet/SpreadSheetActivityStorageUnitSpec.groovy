package molspreadsheet

import bardqueryapi.MolSpreadSheetController
import grails.test.mixin.TestFor
import spock.lang.Unroll
import spock.lang.Specification
import bardqueryapi.MolSpreadSheetCellType

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


    void "Test equals and hash code"() {
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
