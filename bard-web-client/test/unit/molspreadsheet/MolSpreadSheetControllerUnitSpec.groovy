package molspreadsheet

import de.andreasschmitt.export.ExportService
import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import grails.test.mixin.TestMixin
import grails.test.mixin.support.GrailsUnitTestMixin
import querycart.QueryCartService
import querycart.QueryItem
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

/**
 * See the API for {@link grails.test.mixin.support.GrailsUnitTestMixin} for usage instructions
 */
@TestMixin(GrailsUnitTestMixin)
@TestFor(MolSpreadSheetController)
@Mock([QueryItem])
@Unroll
class MolSpreadSheetControllerUnitSpec extends Specification {
    MolecularSpreadSheetService molecularSpreadSheetService
    ExportService exportService
    QueryCartService queryCartService
    @Shared MolSpreadSheetData molSpreadSheetData = new MolSpreadSheetData()



    void setup() {
        this.molecularSpreadSheetService = Mock(MolecularSpreadSheetService)
        controller.molecularSpreadSheetService = this.molecularSpreadSheetService
        this.exportService = Mock(ExportService)
        this.queryCartService = Mock(QueryCartService)
        controller.queryCartService = this.queryCartService
        controller.exportService = this.exportService
        molSpreadSheetData.molSpreadsheetDerivedMethod = MolSpreadsheetDerivedMethod.Compounds_NoAssays_NoProjects
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

    void "test molecularSpreadSheet with ids"() {
        given:
        params.cid = cid
        params.pid = pid
        params.adid = adid
        when:
        controller.molecularSpreadSheet()

        then:

        molecularSpreadSheetService.retrieveExperimentalDataFromIds(_, _, _) >> {new MolSpreadSheetData()}
        assert response.contentAsString.contains("molecular spreadsheet")
        assert response.status == 200
        where:
        label       | cid | adid | pid
        "With CID"  | "2" | ""   | ""
        "With PID"  | ""  | ""   | "2"
        "With ADID" | ""  | "2"  | ""
    }


    void "test molecularSpreadSheet with data"() {
        when:
        controller.molecularSpreadSheet()

        then:
        queryCartService.weHaveEnoughDataToMakeASpreadsheet() >> {true}
        molecularSpreadSheetService.retrieveExperimentalDataFromIds(_, _, _) >> {new MolSpreadSheetData()}
        assert response.contentAsString.contains("molecular spreadsheet")
        assert response.status == 200
    }
    void "test molecularSpreadSheet with data - With Exception"() {
        when:
        controller.molecularSpreadSheet()

        then:
        molecularSpreadSheetService.retrieveExperimentalDataFromIds(_, _, _) >> {new Exception()}
        assert response.status == 400
        assert flash.message == "Could not generate SpreadSheet for current Query Cart Contents"
    }

    void "test molecularSpreadSheet with data and flash.message"() {
        when:
        controller.molecularSpreadSheet()

        then:
        queryCartService.weHaveEnoughDataToMakeASpreadsheet() >> {true}
        molecularSpreadSheetService.retrieveExperimentalDataFromIds(_, _, _) >> {molSpreadSheetData}
        assert response.contentAsString.contains("molecular spreadsheet")
        assert response.status == 200
        assert flash.message == "Please note: Only active compounds are shown in the Molecular Spreadsheet"
    }



    void "test molecularSpreadSheet with export"() {
        given:
        params.format = 'csv'
        LinkedHashMap<String, Object> fakeMap = ['data': null, 'fields': null, 'labels': null]

        when:
        controller.molecularSpreadSheet()

        then:
        queryCartService.weHaveEnoughDataToMakeASpreadsheet() >> {true}
        molecularSpreadSheetService.retrieveExperimentalDataFromIds(_, _, _) >> {molSpreadSheetData}
        molecularSpreadSheetService.prepareForExport(_) >> {fakeMap}
        exportService.export(_, _, _, _, _, _, _) >> {}
        assert response.status == 200
    }



    void "test list, which we will one day use when sorting"() {
        when:
        controller.list()

        then:
        assert response.status == 302
    }


    void "test showExperimentDetails() #label"() {
        when:
        controller.showExperimentDetails(pid, cid)

        then:

        assert response.status == expectedResponseStatus
        assert view == expectedView
        assert response.status == expectedResponseStatus

        where:
        label               | pid | cid | expectedView                           | expectedResponseStatus
        'valid cid and pid' | 1   | 2   | '/molSpreadSheet/molecularSpreadSheet' | 200
    }
}