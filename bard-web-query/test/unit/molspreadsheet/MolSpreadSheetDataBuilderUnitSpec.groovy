package molspreadsheet

import bard.core.Experiment
import querycart.CartAssay
import querycart.CartCompound
import querycart.CartProject
import grails.test.mixin.support.GrailsUnitTestMixin
import spock.lang.Specification
import grails.test.mixin.TestMixin
import spock.lang.Unroll

/**
 * Created with IntelliJ IDEA.
 * User: balexand
 * Date: 10/10/12
 * Time: 9:45 AM
 * To change this template use File | Settings | File Templates.
 */

@TestMixin(GrailsUnitTestMixin)
@Unroll
class MolSpreadSheetDataBuilderUnitSpec extends Specification {

    MolecularSpreadSheetService molecularSpreadSheetService

    void setup() {
        this.molecularSpreadSheetService = Mock(MolecularSpreadSheetService)
    }

    void tearDown() {
        // Tear down logic here
    }

    void "test populateMolSpreadSheet Non_Empty Compound Cart"() {
        given:
        MolSpreadSheetDataBuilder molSpreadSheetDataBuilder = new MolSpreadSheetDataBuilder()
        molSpreadSheetDataBuilder.molecularSpreadSheetService = this.molecularSpreadSheetService
        molSpreadSheetDataBuilder.cartCompoundList = [new CartCompound("C","C",200)]
        when:
        molSpreadSheetDataBuilder.populateMolSpreadSheet([])
        then:
        1 * molecularSpreadSheetService.populateMolSpreadSheetColumnMetadata(_, _) >> {}
        molecularSpreadSheetService.extractMolSpreadSheetData(_, _, _) >> {[]}


    }

    void "test holdCartResults, choose one of the data accumulation methods in this Builder"() {
        when:
        List<CartCompound> cartCompoundList = []
        List<CartAssay> cartAssayList = []
        List<CartProject> cartProjectList = []
        MolSpreadSheetDataBuilder molSpreadSheetDataBuilder = new MolSpreadSheetDataBuilder()
        assertNotNull molSpreadSheetDataBuilder

        then: "The expected hashCode is returned"
        molSpreadSheetDataBuilder.holdCartResults(cartCompoundList, cartAssayList, cartProjectList)
        assertNotNull molSpreadSheetDataBuilder.cartCompoundList
        assertNotNull molSpreadSheetDataBuilder.cartAssayList
        assertNotNull molSpreadSheetDataBuilder.cartProjectList
    }



    void "test deriveListOfExperiments in the degenerate case"() {
        when:
        List<CartCompound> cartCompoundList = []
        List<CartAssay> cartAssayList = []
        List<CartProject> cartProjectList = []
        MolSpreadSheetDataBuilder molSpreadSheetDataBuilder = new MolSpreadSheetDataBuilder()
        molSpreadSheetDataBuilder.holdCartResults(cartCompoundList, cartAssayList, cartProjectList)

        then: "The expected hashCode is returned"
        List<Experiment> experimentList = molSpreadSheetDataBuilder.deriveListOfExperiments()
        assertNotNull experimentList
        assert experimentList.size() == 0
    }

//    void "test populateMolSpreadSheet #label"() {
//        when:
//        MolSpreadSheetDataBuilder molSpreadSheetDataBuilder = new MolSpreadSheetDataBuilder()
//        molSpreadSheetDataBuilder.molecularSpreadSheetService = this.molecularSpreadSheetService
//        molSpreadSheetDataBuilder.cartCompoundList = cartCompoundList
//        molSpreadSheetDataBuilder.populateMolSpreadSheet([])
//
//        then:
//        1 * molecularSpreadSheetService.extractMolSpreadSheetData(_, _, _) >> {[]}
//        numOfInvocations * molecularSpreadSheetService.convertSpreadSheetActivityToCompoundInformation(_) >> {[:] as Map}
//        //molecularSpreadSheetService.populateMolSpreadSheetColumnMetadata(_, _) >> {null}
//        1 * molecularSpreadSheetService.populateMolSpreadSheetRowMetadata(_, _) >> {null}
//
//        where:
//        label                       | cartCompoundList                         | numOfInvocations
//        'compound-list is empty'    | []                                       | 1
//        'one compound in the list'  | [new CartCompound()]                     | 0
//        'two compounds in the list' | [new CartCompound(), new CartCompound()] | 0
//    }
}
