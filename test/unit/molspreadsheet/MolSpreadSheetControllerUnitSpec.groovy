package molspreadsheet

import grails.test.mixin.TestFor
import grails.test.mixin.TestMixin
import grails.test.mixin.support.GrailsUnitTestMixin
import spock.lang.Specification
import spock.lang.Unroll
import molspreadsheet.MolSpreadSheetData
import molspreadsheet.MolSpreadSheetController
import molspreadsheet.MolecularSpreadSheetService

/**
 * See the API for {@link grails.test.mixin.support.GrailsUnitTestMixin} for usage instructions
 */
@TestMixin(GrailsUnitTestMixin)
@TestFor(MolSpreadSheetController)
@Unroll
class MolSpreadSheetControllerUnitSpec extends Specification  {
    MolecularSpreadSheetService molecularSpreadSheetService


    void setup() {
        this.molecularSpreadSheetService = Mock(MolecularSpreadSheetService)
        controller.molecularSpreadSheetService = this.molecularSpreadSheetService
    }

    void tearDown() {
        // Tear down logic here
    }

    void "test Index"() {

        when:
        controller.index()

        then:
        assert response.status == 200
    }






    void "test molecularSpreadSheet no data"() {
        when:
        controller.molecularSpreadSheet()

        then:
        assert response.status == 200
        assert response.contentAsString.contains("Cannot display molecular spreadsheet without at least one assay")
        assert true
    }




    void "test molecularSpreadSheet with data"() {
        when:
        controller.molecularSpreadSheet()

        then:
        molecularSpreadSheetService.weHaveEnoughDataToMakeASpreadsheet() >> {true}
        molecularSpreadSheetService.retrieveExperimentalData() >> {new MolSpreadSheetData()}
        assert response.contentAsString.contains("molecular spreadsheet")
        assert response.status == 200
        assert true
    }




    void "test list, which we will one day use when sorting"() {
        when:
        controller.list()

        then:
        assert response.status == 302
    }




}

