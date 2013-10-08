package molspreadsheet

import bard.core.rest.spring.experiment.PriorityElement
import bard.core.rest.spring.experiment.ConcentrationResponseSeries
import grails.test.mixin.TestFor
import spock.lang.Specification
import spock.lang.Unroll
import bard.core.rest.spring.experiment.ConcentrationResponsePoint

/**
 * See the API for {@link grails.test.mixin.support.GrailsUnitTestMixin} for usage instructions
 */
@TestFor(MolSpreadSheetCell)
@Unroll
class MolSpreadSheetCellUnitSpec extends Specification {

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

        then:
        assertNotNull molSpreadSheetCell.activity
        assertNotNull molSpreadSheetCell.molSpreadSheetCellType
        assertNotNull molSpreadSheetCell.strInternalValue
        assertNotNull molSpreadSheetCell.intInternalValue
        assertNull molSpreadSheetCell.supplementalInternalValue
        assertNull molSpreadSheetCell.spreadSheetActivityStorage
    }

    void "Test that we can build a Basic molecular spreadsheet cell with image type"() {
        given:
        String value1 = null
        String value2 = null
        MolSpreadSheetCellType molSpreadSheetCellType = MolSpreadSheetCellType.image
        SpreadSheetActivityStorage spreadSheetActivityStorage = null
        when:
        MolSpreadSheetCell molSpreadSheetCell = new MolSpreadSheetCell(value1, value2, molSpreadSheetCellType, spreadSheetActivityStorage)

        then:
        assert molSpreadSheetCell.strInternalValue == ""
        assert molSpreadSheetCell.supplementalInternalValue == ""
    }





    void "Test  MolSpreadSheetCell ctor"() {
        given:
        SpreadSheetActivity spreadSheetActivity = new  SpreadSheetActivity()
        PriorityElement priorityElement1 = new  PriorityElement()
        priorityElement1.value = null
        PriorityElement priorityElement2 = new  PriorityElement()
        priorityElement2.value = "unexpected nonnumeric argument"
        PriorityElement priorityElement3 = new  PriorityElement()
        priorityElement3.value = "12.34"
        PriorityElement priorityElement4 = new  PriorityElement()
        priorityElement4.concentrationResponseSeries = new ConcentrationResponseSeries()
        priorityElement4.concentrationResponseSeries.concentrationResponsePoints = [new ConcentrationResponsePoint(value: "0.47", testConcentration: 47d)]
        priorityElement4.value = "43.21"
        spreadSheetActivity.priorityElementList = [priorityElement1,priorityElement2,priorityElement3,priorityElement4]
        when:
        MolSpreadSheetCell molSpreadSheetCell = new MolSpreadSheetCell(spreadSheetActivity)
        println  molSpreadSheetCell.toString()
        then:
        molSpreadSheetCell.spreadSheetActivityStorage
        molSpreadSheetCell.spreadSheetActivityStorage.hillCurveValueHolderList.size() == 4
        molSpreadSheetCell.spreadSheetActivityStorage.hillCurveValueHolderList[0].toString().trim()=="--"
        molSpreadSheetCell.spreadSheetActivityStorage.hillCurveValueHolderList[1].toString().trim()=="--"
        molSpreadSheetCell.spreadSheetActivityStorage.hillCurveValueHolderList[2].toString().trim()=="12.3"
        molSpreadSheetCell.spreadSheetActivityStorage.hillCurveValueHolderList[3].toString().trim()=="43.2"
        molSpreadSheetCell.spreadSheetActivityStorage.dictionaryLabel == ''
    }





    void "Test that we can build a few  molecular spreadsheet cells"() {
        when:
        MolSpreadSheetCell molSpreadSheetCell = new MolSpreadSheetCell("2", MolSpreadSheetCellType.string)

        then:
        assert molSpreadSheetCell.toString() == "2"
    }




    void "Test copy constructors"() {
        when:
        MolSpreadSheetCell originalMolSpreadSheetCell = new MolSpreadSheetCell("2", MolSpreadSheetCellType.string)
        MolSpreadSheetCell newMolSpreadSheetCell = new MolSpreadSheetCell(originalMolSpreadSheetCell)

        then:
        assert originalMolSpreadSheetCell.activity == newMolSpreadSheetCell.activity
        assert originalMolSpreadSheetCell.molSpreadSheetCellType == newMolSpreadSheetCell.molSpreadSheetCellType
        assert originalMolSpreadSheetCell.strInternalValue == newMolSpreadSheetCell.strInternalValue
        assert originalMolSpreadSheetCell.intInternalValue == newMolSpreadSheetCell.intInternalValue
        assert originalMolSpreadSheetCell.supplementalInternalValue == newMolSpreadSheetCell.supplementalInternalValue
        assert originalMolSpreadSheetCell.spreadSheetActivityStorage == null
    }


    void "Test copy constructors 2"() {
        when:
        MolSpreadSheetCell originalMolSpreadSheetCell = new MolSpreadSheetCell("2", MolSpreadSheetCellType.string)
        originalMolSpreadSheetCell.spreadSheetActivityStorage = new SpreadSheetActivityStorage()
        MolSpreadSheetCell newMolSpreadSheetCell = new MolSpreadSheetCell(originalMolSpreadSheetCell, 0)

        then:
        assert originalMolSpreadSheetCell.activity == newMolSpreadSheetCell.activity
        assert originalMolSpreadSheetCell.molSpreadSheetCellType == newMolSpreadSheetCell.molSpreadSheetCellType
        assert originalMolSpreadSheetCell.strInternalValue == newMolSpreadSheetCell.strInternalValue
        assert originalMolSpreadSheetCell.intInternalValue == newMolSpreadSheetCell.intInternalValue
        assert originalMolSpreadSheetCell.supplementalInternalValue == newMolSpreadSheetCell.supplementalInternalValue
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
        molSpreadSheetCell.setActivity(null)
        assertFalse molSpreadSheetCell.validate()
        molSpreadSheetCell.setActivity(activity)
        assertTrue molSpreadSheetCell.validate()

        assertTrue molSpreadSheetCell.validate()
        def molSpreadSheetCellType = molSpreadSheetCell.molSpreadSheetCellType

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




    void "Test molSpreadSheetCellListFactory"() {
        SpreadSheetActivity spreadSheetActivity = new  SpreadSheetActivity()
        PriorityElement priorityElement1 = new  PriorityElement()
        priorityElement1.value = null
        PriorityElement priorityElement2 = new  PriorityElement()
        priorityElement2.value = "unexpected nonnumeric argument"
        PriorityElement priorityElement3 = new  PriorityElement()
        priorityElement3.value = "12.34"
        PriorityElement priorityElement4 = new  PriorityElement()
        priorityElement4.concentrationResponseSeries = new ConcentrationResponseSeries()
        priorityElement4.concentrationResponseSeries.concentrationResponsePoints = [new ConcentrationResponsePoint(value: "0.47", testConcentration: 47d)]
        priorityElement4.value = "43.21"
        spreadSheetActivity.priorityElementList = [priorityElement1,priorityElement2,priorityElement3,priorityElement4]
        when:
        List <MolSpreadSheetCell> molSpreadSheetCellList = MolSpreadSheetCell.molSpreadSheetCellListFactory(spreadSheetActivity)

        then:
        println molSpreadSheetCellList
       assertNotNull molSpreadSheetCellList

    }

    }