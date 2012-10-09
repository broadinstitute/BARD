package molspreadsheet

import bardqueryapi.MolSpreadSheetController
import grails.test.mixin.TestFor
import spock.lang.Specification
import spock.lang.Unroll

/**
 * See the API for {@link grails.test.mixin.support.GrailsUnitTestMixin} for usage instructions
 */
@TestFor(MolSpreadSheetController)
@Unroll
class MolSpreadSheetDataUnitSpec  extends Specification {

    void setup() {
        // Setup logic here
    }

    void tearDown() {
        // Tear down logic here
    }

    void "Smoke test can we build a molecular spreadsheet data"() {
        when:
        MolSpreadSheetData molSpreadSheetData = new MolSpreadSheetData()
        assertNotNull(molSpreadSheetData)

        then:
        assertNotNull molSpreadSheetData.mssData
        assertNotNull molSpreadSheetData.rowPointer
        assertNotNull molSpreadSheetData.rowPointer
    }

    void "Test constraints for molecular spreadsheet data"() {
        given:
        mockForConstraintsTests(MolSpreadSheetData)

        when:
        MolSpreadSheetData molSpreadSheetData = new MolSpreadSheetData()
        molSpreadSheetData.validate()
        molSpreadSheetData.hasErrors() == false

        then:
        def mssData = molSpreadSheetData.mssData
        molSpreadSheetData.setMssData ( null )
        assertFalse molSpreadSheetData.validate()
        molSpreadSheetData.setMssData ( mssData )
        assertTrue molSpreadSheetData.validate()

        def rowPointer = molSpreadSheetData.rowPointer
        molSpreadSheetData.setRowPointer ( null )
        assertFalse molSpreadSheetData.validate()
        molSpreadSheetData.setRowPointer ( rowPointer )
        assertTrue molSpreadSheetData.validate()

        def columnPointer = molSpreadSheetData.columnPointer
        molSpreadSheetData.setColumnPointer ( null )
        assertFalse molSpreadSheetData.validate()
        molSpreadSheetData.setColumnPointer ( columnPointer )
        assertTrue molSpreadSheetData.validate()

        molSpreadSheetData.setMssHeaders ( null )
        assertTrue molSpreadSheetData.validate()

    }
}
