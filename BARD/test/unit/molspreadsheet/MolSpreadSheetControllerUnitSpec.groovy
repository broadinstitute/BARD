package molspreadsheet
import bard.core.adapter.CompoundAdapter
import bard.core.rest.spring.compounds.Compound
import bard.core.rest.spring.util.StructureSearchParams
import bardqueryapi.BardUtilitiesService
import bardqueryapi.IQueryService
import bardqueryapi.InetAddressUtil
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

import javax.servlet.http.HttpServletResponse
/**
 * See the API for {@link grails.test.mixin.support.GrailsUnitTestMixin} for usage instructions
 */
@TestMixin(GrailsUnitTestMixin)
@TestFor(MolSpreadSheetController)
@Mock([QueryItem])
@Unroll
class MolSpreadSheetControllerUnitSpec extends Specification {
    MolecularSpreadSheetService molecularSpreadSheetService
    BardUtilitiesService bardUtilitiesService
    ExportService exportService
    QueryCartService queryCartService
    IQueryService queryService
    RetainSpreadsheetService retainSpreadsheetService = new RetainSpreadsheetService()
    @Shared MolSpreadSheetData molSpreadSheetData = new MolSpreadSheetData()



    void setup() {
        controller.metaClass.mixin([InetAddressUtil])

        controller.retainSpreadsheetService = retainSpreadsheetService
        bardUtilitiesService = Mock(BardUtilitiesService)
        controller.bardUtilitiesService = bardUtilitiesService
        this.molecularSpreadSheetService = Mock(MolecularSpreadSheetService)
        controller.molecularSpreadSheetService = this.molecularSpreadSheetService
        this.exportService = Mock(ExportService)
        this.queryCartService = Mock(QueryCartService)
        this.queryService = Mock(IQueryService)
        controller.queryCartService = this.queryCartService
        controller.exportService = this.exportService
        controller.queryService = this.queryService
        molSpreadSheetData.molSpreadsheetDerivedMethod = MolSpreadsheetDerivedMethod.Compounds_NoAssays_NoProjects
    }

    void "test index transpose and norefresh parameters"() {
        given:
        params.norefresh = norefresh
        params.transpose = transpose

        when:
        controller.index()

        then:
        assert response.status == 200

        where:
        label                       | norefresh | transpose
        "no refresh your transpose" | null      | null
        "refresh only"              | "true"    | null
        "transpose only"            | null      | "true"
        "refresh and transpose"     | null      | "true"

    }





    void "test molecularSpreadSheet transpose and norefresh parameters"() {
        given:
        params.norefresh = norefresh
        params.transpose = transpose
        params.ChangeNorm = ChangeNorm
        controller.retainSpreadsheetService.molSpreadSheetData = molSpreadSheetData

        when:
        controller.molecularSpreadSheet()

        then:
        assert response.status == 200

        where:
        label                       | norefresh | transpose | molSpreadSheetData        |  ChangeNorm
        "no refresh your transpose" | null      | null      | null                      |  null
        "refresh only"              | "true"    | null      | null                      |  null
        "transpose only"            | null      | "true"    | null                      |  ""
        "refresh and transpose"     | "true"    | "true"    | null                      |  ""
        "no refresh your transpose" | null      | null      | new MolSpreadSheetData()  |  null
        "refresh only"              | "true"    | null      | new MolSpreadSheetData()  |  null
        "transpose only"            | null      | "true"    | new MolSpreadSheetData()  |  ""
        "refresh and transpose"     | "true"    | "true"    | new MolSpreadSheetData()  |  ""
        "no refresh your transpose" | null      | null      | null                      |  "0"
        "refresh only"              | "true"    | null      | null                      |  "0"
        "transpose only"            | null      | "true"    | null                      |  "9388"
        "refresh and transpose"     | "true"    | "true"    | null                      |  "9388"
        "no refresh your transpose" | null      | null      | new MolSpreadSheetData()  |  "0"
        "refresh only"              | "true"    | null      | new MolSpreadSheetData()  |  "0"
        "transpose only"            | null      | "true"    | new MolSpreadSheetData()  |  "9388"
        "refresh and transpose"     | "true"    | "true"    | new MolSpreadSheetData()  |  "9388"
    }

//    void "test no flash"() {
//        given:
//        controller.flash = null
//
//        when:
//        controller.molecularSpreadSheet()
//
//        then:
//        assert response.status == 200
//    }


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
        assert response.contentAsString.contains("Cannot display molecular spreadsheet without at least one compound")
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

        molecularSpreadSheetService.retrieveExperimentalDataFromIds(exptCids, exptAdids, exptPids, activeCompoundsOnly) >> {new MolSpreadSheetData()}
        assert response.contentAsString.contains("molecular spreadsheet")
        assert response.status == 200
        where:
        label       | cid | adid | pid  | activeCompoundsOnly | exptCids | exptAdids | exptPids
        "With CID"  | "25" | ""   | ""   | true | [25] | [] | []
        "With PID"  | ""  | ""   | "25"  | true | [] | [] | [25]
        "With ADID" | ""  | "25"  | ""   | true | [] | [25] | []
        "With CID"  | "25" | ""   | ""   | false | [25] | [] | []
        "With PID"  | ""  | ""   | "25"  | false | [] | [] | [25]
        "With ADID" | ""  | "25"  | ""   | false | [] | [25] | []
        "With null cid"  | null | "35"   | ""   | true | [] | [35] | []
        "With CID list"  | ["25","35"] | ""   | ""   | true | [25,35] | [] | []
        "With CID list and project"  | ["25","35"] | ""   | "45"   | true | [25,35] | [] | [45]
    }


    void "test molecularSpreadSheet with data"() {
        when:
        controller.molecularSpreadSheet()

        then:
        queryCartService.weHaveEnoughDataToMakeASpreadsheet() >> {true}
        molecularSpreadSheetService.retrieveExperimentalDataFromIds(_, _, _, _) >> {new MolSpreadSheetData()}
        assert response.contentAsString.contains("molecular spreadsheet")
        assert response.status == 200
    }

    void "test molecularSpreadSheet with data - With Exception"() {
        when:
        controller.molecularSpreadSheet()

        then:
        molecularSpreadSheetService.retrieveExperimentalDataFromIds(_, _, _, _) >> {new Exception()}
        assert response.status == 400
        assert flash.message == "Could not generate SpreadSheet for current Query Cart Contents"
    }

    void "test molecularSpreadSheet with data and flash.message"() {
        when:
        controller.molecularSpreadSheet()

        then:
        queryCartService.weHaveEnoughDataToMakeASpreadsheet() >> {true}
        molecularSpreadSheetService.retrieveExperimentalDataFromIds(_, _, _, _) >> {molSpreadSheetData}
        assert response.contentAsString.contains("molecular spreadsheet")
        assert response.status == 200
    }



    void "test molecularSpreadSheet with export"() {
        given:
        params.format = 'csv'
        LinkedHashMap<String, Object> fakeMap = ['data': null, 'fields': null, 'labels': null]

        when:
        controller.molecularSpreadSheet()

        then:
        queryCartService.weHaveEnoughDataToMakeASpreadsheet() >> {true}
        molecularSpreadSheetService.retrieveExperimentalDataFromIds(_, _, _,_) >> {molSpreadSheetData}
        molecularSpreadSheetService.prepareForExport(_) >> {fakeMap}
        exportService.export(_, _, _, _, _, _, _) >> {}
        assert response.status == 200
    }

    void "test probeSarTable for #label"() {

        when:
        controller.probeSarTable(pid, cid, transpose, threshold)

        then:
        1 * queryService.structureSearch(cid, StructureSearchParams.Type.Similarity, threshold, [], _ as Integer, _ as Integer, _ as Integer) >> {
            Map results = [:]
            def compoundList = searchResults.collect {
                new CompoundAdapter(new Compound(cid:it))
            }
            results.compoundAdapters = compoundList
            return results
        }
        response.status == HttpServletResponse.SC_OK
        view == '/molSpreadSheet/molecularSpreadSheet'
        model.cid == exptCids
        model.pid == pid
        model.transpose == transpose

        where:
        label                 | pid | cid | transpose | threshold | searchResults | exptCids
        "valid ids"           | 100 | 555 | false     | 0.9       | [500,555,600] | [555,500,600]
        "transpose true"      | 100 | 555 | true      | 0.9       | [500,555,600] | [555,500,600]
        "showOnlyActive true" | 100 | 555 | false     | 0.9       | [500,555,600] | [555,500,600]
        "transpose and active"| 100 | 555 | true      | 0.9       | [500,555,600] | [555,500,600]
        "null pid"            | null| 555 | false     | 0.9       | [500,555,600] | [555,500,600]
    }

    void "test probeSarTable errors for #label"() {

        when:
        controller.probeSarTable(pid, cid, transpose, threshold)

        then:
        0 * _._
        response.status == HttpServletResponse.SC_BAD_REQUEST
        view == null

        where:
        label                 | pid | cid | transpose | threshold | searchResults | exptCids
        "null cid"            | 100 | null| false     | 0.9       | [] | []
        "null pid and cid"    | null| null| false     | 0.9       | [] | []
    }

    void "test list, which we will one day use when sorting"() {
        when:
        controller.list()

        then:
        assert response.status == 302
    }


    void "test showExperimentDetails() #label"() {
        when:
        controller.showExperimentDetails(pid, cid, transpose,true)

        then:

        assert response.status == expectedResponseStatus
        assert view == expectedView
        assert response.status == expectedResponseStatus

        where:
        label               | pid | cid | transpose | expectedView                           | expectedResponseStatus
        'valid cid and pid' | 1   | 2   | true      | '/molSpreadSheet/molecularSpreadSheet' | 200
    }
}