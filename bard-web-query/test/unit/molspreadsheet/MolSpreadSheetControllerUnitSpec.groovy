package molspreadsheet

import grails.test.mixin.TestFor
import grails.test.mixin.TestMixin
import grails.test.mixin.support.GrailsUnitTestMixin
import spock.lang.Specification
import spock.lang.Unroll
import querycart.CartCompoundService
import querycart.CartProjectService
import querycart.QueryCartService
import querycart.CartCompound
import querycart.CartProject
import querycart.QueryItem
import grails.test.mixin.Mock

/**
 * See the API for {@link grails.test.mixin.support.GrailsUnitTestMixin} for usage instructions
 */
@TestMixin(GrailsUnitTestMixin)
@TestFor(MolSpreadSheetController)
@Mock([QueryItem])
@Unroll
class MolSpreadSheetControllerUnitSpec extends Specification {
    MolecularSpreadSheetService molecularSpreadSheetService
    QueryCartService queryCartService
    CartCompoundService cartCompoundService
    CartProjectService cartProjectService

    void setup() {
        this.molecularSpreadSheetService = Mock(MolecularSpreadSheetService)
        controller.molecularSpreadSheetService = this.molecularSpreadSheetService
        this.queryCartService = Mock(QueryCartService)
        this.cartCompoundService = Mock(CartCompoundService)
        controller.cartCompoundService = this.cartCompoundService
        this.cartProjectService = Mock(CartProjectService)
        controller.cartProjectService = this.cartProjectService
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


    void "test showExperimentDetails() #label"() {
        when:
        controller.showExperimentDetails(pid, cid)

        then:
        (1..3) * molecularSpreadSheetService.getQueryCartService() >> {this.queryCartService}
        1 * queryCartService.emptyShoppingCart() >> {}
        (0..2) * queryCartService.addToShoppingCart(_) >> {}
        1 * cartCompoundService.createCartCompoundFromCID(cid) >> {cartCompound}
        1 * cartProjectService.createCartProjectFromPID(pid) >> {cartProject}

        assert response.status == expectedResponseStatus
        assert view == expectedView
        assert response.status == expectedResponseStatus

        where:
        label                                | pid | cid | cartCompound       | cartProject       | expectedView                           | expectedResponseStatus
        'valid cartCompound and cartProject' | 1   | 2   | new CartCompound() | new CartProject() | '/molSpreadSheet/molecularSpreadSheet' | 200
        'valid cartProject only'             | 1   | 2   | null               | new CartProject() | null                                   | 417
        'valid cartCompound only'            | 1   | 2   | new CartCompound() | null              | null                                   | 417
    }
}