package molspreadsheet

import bardqueryapi.MolSpreadSheetController
import grails.test.mixin.TestFor
import spock.lang.Specification
import spock.lang.Unroll
import bardqueryapi.MolSpreadSheetCellType

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


    void "Test displayValue method"() {
        when:
        MolSpreadSheetData molSpreadSheetData = new MolSpreadSheetData()
        assertNotNull(molSpreadSheetData)

        molSpreadSheetData.mssData["0_0"] = new MolSpreadSheetCell(incoming,MolSpreadSheetCellType.identifier)
        molSpreadSheetData.mssData["0_1"] = new MolSpreadSheetCell(incoming,MolSpreadSheetCellType.image)
        molSpreadSheetData.mssData["1_0"] = new MolSpreadSheetCell(incoming,MolSpreadSheetCellType.numeric)
        molSpreadSheetData.mssData["1_1"] = new MolSpreadSheetCell(incoming,MolSpreadSheetCellType.string)


        then:
        assert molSpreadSheetData.displayValue (row, column)["value"]== returnValue

        where:
        row     |   column  |   incoming    |   returnValue
        0       |   0       |   "123"       |   "123"
        0       |   1       |   "123"       |   null
        1       |   0       |   "123"       |   "123"
        1       |   1       |   "123"       |   "123"



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
