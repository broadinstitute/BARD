package molspreadsheet

import grails.test.mixin.TestFor
import spock.lang.Unroll
import spock.lang.Specification

/**
 * See the API for {@link grails.test.mixin.support.GrailsUnitTestMixin} for usage instructions
 */
@TestFor(MolSpreadSheetCell)
@Unroll
class MolSpreadSheetCellUnitSpec extends Specification{

    MolSpreadSheetData molSpreadSheetData

    void setup() {
         molSpreadSheetData = new MolSpreadSheetData()
    }

    void tearDown() {
        // Tear down logic here
    }

    void "Test that we can build a Basic molecular spreadsheet cell"() {
        when:
        MolSpreadSheetCell molSpreadSheetCell = new MolSpreadSheetCell()
        assertNotNull(molSpreadSheetCell)

        then:
        assertNotNull molSpreadSheetCell.activity
        assertNotNull molSpreadSheetCell.molSpreadSheetCellType
        assertNotNull molSpreadSheetCell.strInternalValue
        assertNotNull molSpreadSheetCell.intInternalValue
        assertNull molSpreadSheetCell.supplementalInternalValue
        assertNull molSpreadSheetCell.spreadSheetActivityStorage
    }



    void "Test that we can build a few  molecular spreadsheet cells"() {
        when:
        MolSpreadSheetCell molSpreadSheetCell = new MolSpreadSheetCell("2", MolSpreadSheetCellType.string)
        assertNotNull(molSpreadSheetCell)

        then:
        assert molSpreadSheetCell.toString()=="2"
    }




    void "Test copy constructors"() {
        when:
        MolSpreadSheetCell originalMolSpreadSheetCell = new MolSpreadSheetCell("2", MolSpreadSheetCellType.string)
        MolSpreadSheetCell newMolSpreadSheetCell = new MolSpreadSheetCell(originalMolSpreadSheetCell)

        then:
        assert originalMolSpreadSheetCell.activity==newMolSpreadSheetCell.activity
        assert originalMolSpreadSheetCell.molSpreadSheetCellType ==newMolSpreadSheetCell.molSpreadSheetCellType
        assert originalMolSpreadSheetCell.strInternalValue ==newMolSpreadSheetCell.strInternalValue
        assert originalMolSpreadSheetCell.intInternalValue ==newMolSpreadSheetCell.intInternalValue
        assert originalMolSpreadSheetCell.supplementalInternalValue ==newMolSpreadSheetCell.supplementalInternalValue
        assert originalMolSpreadSheetCell.spreadSheetActivityStorage==null
    }


    void "Test copy constructors 2"() {
        when:
        MolSpreadSheetCell originalMolSpreadSheetCell = new MolSpreadSheetCell("2", MolSpreadSheetCellType.string)
        originalMolSpreadSheetCell.spreadSheetActivityStorage = new SpreadSheetActivityStorage()
        MolSpreadSheetCell newMolSpreadSheetCell = new MolSpreadSheetCell(originalMolSpreadSheetCell,0)

        then:
        assert originalMolSpreadSheetCell.activity==newMolSpreadSheetCell.activity
        assert originalMolSpreadSheetCell.molSpreadSheetCellType ==newMolSpreadSheetCell.molSpreadSheetCellType
        assert originalMolSpreadSheetCell.strInternalValue ==newMolSpreadSheetCell.strInternalValue
        assert originalMolSpreadSheetCell.intInternalValue ==newMolSpreadSheetCell.intInternalValue
        assert originalMolSpreadSheetCell.supplementalInternalValue ==newMolSpreadSheetCell.supplementalInternalValue
        assertNotNull originalMolSpreadSheetCell.spreadSheetActivityStorage
    }




    void "Test constraints for molecular spreadsheet data"() {
        given:
        mockForConstraintsTests(MolSpreadSheetCell)

        when:
        MolSpreadSheetCell molSpreadSheetCell = new MolSpreadSheetCell()
        molSpreadSheetCell.molSpreadSheetData = molSpreadSheetData
        assert molSpreadSheetCell.validate()
        assert !molSpreadSheetCell.hasErrors()

        then:
        assertTrue molSpreadSheetCell.validate()
        def activity = molSpreadSheetCell.activity
        molSpreadSheetCell.setActivity( null )
        assertFalse molSpreadSheetCell.validate()
        molSpreadSheetCell.setActivity ( activity )
        assertTrue molSpreadSheetCell.validate()

        assertTrue molSpreadSheetCell.validate()
        def molSpreadSheetCellType = molSpreadSheetCell.molSpreadSheetCellType
        molSpreadSheetCell.setMolSpreadSheetCellType(null)
        assertFalse molSpreadSheetCell.validate()
        molSpreadSheetCell.setMolSpreadSheetCellType(molSpreadSheetCellType)
        assertTrue molSpreadSheetCell.validate()

        assertTrue molSpreadSheetCell.validate()
        def intInternalValue = molSpreadSheetCell.intInternalValue
        molSpreadSheetCell.setIntInternalValue(null)
        assertFalse molSpreadSheetCell.validate()
        molSpreadSheetCell.setIntInternalValue(intInternalValue)
        assertTrue molSpreadSheetCell.validate()

        molSpreadSheetCell.setStrInternalValue(null)
        assertTrue molSpreadSheetCell.validate()

        molSpreadSheetCell.setSupplementalInternalValue(null)
        assertTrue molSpreadSheetCell.validate()

        molSpreadSheetCell.setSpreadSheetActivityStorage(null)
        assertTrue molSpreadSheetCell.validate()
    }


}
