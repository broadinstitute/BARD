package molspreadsheet

import grails.test.mixin.TestFor
import spock.lang.Unroll
import spock.lang.Specification

/**
 * See the API for {@link grails.test.mixin.support.GrailsUnitTestMixin} for usage instructions
 */
@Unroll
@TestFor(RetainSpreadsheetService)
class RetainSpreadsheetServiceSpec  extends Specification{

    void setup() {
        // Setup logic here
    }

    //  Given that the service has no methods and only one data object is not too terribly much to test
    void testMolSpreadSheetData() {
        given:
        final MolSpreadSheetData molSpreadSheetData = new MolSpreadSheetData()

        when:
        int i=1
        assertNull service.molSpreadSheetData
        service.molSpreadSheetData = molSpreadSheetData

        then:
        assert i==1
        assertNotNull service.molSpreadSheetData
        service.setMolSpreadSheetData(null)
        assertNull service.molSpreadSheetData
    }
}
