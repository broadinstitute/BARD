package molspreadsheet

import bardqueryapi.MolSpreadSheetCellType
import bardqueryapi.MolSpreadSheetCellUnit
import grails.test.mixin.TestMixin
import grails.test.mixin.support.GrailsUnitTestMixin
import results.ExperimentalValue
import results.ExperimentalValueUnit
import spock.lang.Specification
import spock.lang.Unroll
import results.ExperimentalValueType

/**
 * See the API for {@link grails.test.mixin.support.GrailsUnitTestMixin} for usage instructions
 */
//@TestFor(MolSpreadSheetController)
@TestMixin(GrailsUnitTestMixin)
@Unroll
class MolSpreadSheetCellUnitSpec extends  Specification {

    void setup() {

    }

    void tearDown() {
        // Tear down logic here
    }

    void "test MolSpreadSheetCell to see if the object can be instantiated"() {
        when:
        MolSpreadSheetCell molSpreadSheetCell = new MolSpreadSheetCell("0.123",MolSpreadSheetCellType.numeric)
        assertNotNull(molSpreadSheetCell)

        then:
        assert molSpreadSheetCell.activity == true

    }




}
