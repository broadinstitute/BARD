package bardqueryapi

import spock.lang.Specification
import spock.lang.Unroll

import static org.junit.Assert.assertNotNull
import bard.core.Experiment

/**
 * Created with IntelliJ IDEA.
 * User: balexand
 * Date: 10/10/12
 * Time: 9:45 AM
 * To change this template use File | Settings | File Templates.
 */

@Unroll
class MolSpreadSheetDataBuilderUnitSpec  extends Specification {

    MolecularSpreadSheetService molecularSpreadSheetService

    void setup() {
         this.molecularSpreadSheetService = Mock(MolecularSpreadSheetService)
    }

    void tearDown() {
        // Tear down logic here
    }

    void "test holdCartResults, choose one of the data accumulation methods in this Builder"() {
        when:
        List<CartCompound> cartCompoundList  = []
        List<CartAssay> cartAssayList  = []
        List<CartProject> cartProjectList = []
        MolSpreadSheetDataBuilder molSpreadSheetDataBuilder = new MolSpreadSheetDataBuilder()
        assertNotNull  molSpreadSheetDataBuilder

        then: "The expected hashCode is returned"
        molSpreadSheetDataBuilder.holdCartResults(cartCompoundList, cartAssayList, cartProjectList)
        assertNotNull molSpreadSheetDataBuilder.cartCompoundList
        assertNotNull molSpreadSheetDataBuilder.cartAssayList
        assertNotNull molSpreadSheetDataBuilder.cartProjectList
    }



    void "test deriveListOfExperiments in the degenerate case"() {
        when:
        List<CartCompound> cartCompoundList  = []
        List<CartAssay> cartAssayList  = []
        List<CartProject> cartProjectList = []
        MolSpreadSheetDataBuilder molSpreadSheetDataBuilder = new MolSpreadSheetDataBuilder()
        molSpreadSheetDataBuilder.holdCartResults(cartCompoundList, cartAssayList, cartProjectList)

        then: "The expected hashCode is returned"
        List<Experiment> experimentList = molSpreadSheetDataBuilder.deriveListOfExperiments()
        assertNotNull experimentList
        assert experimentList.size() == 0
    }


}
