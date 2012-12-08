package molspreadsheet

import spock.lang.Specification

//import static org.junit.Assert.assertNotNull

/**
 * Created with IntelliJ IDEA.
 * User: balexand
 * Date: 10/10/12
 * Time: 2:24 PM
 * To change this template use File | Settings | File Templates.
 */
class MolSpreadSheetDataBuilderDirectorUnitSpec extends Specification {

    void setup() {
    }



    void "test deriveListOfExperimentsFromIds - Throws Exception"() {
        given:
        final List<Long> pids = [2]
        final List<Long> adids = []
        final List<Long> cids = []
        MolSpreadSheetDataBuilder molSpreadSheetDataBuilder = new MolSpreadSheetDataBuilder()
        MolecularSpreadSheetService molecularSpreadSheetService = Mock(MolecularSpreadSheetService)
        molSpreadSheetDataBuilder.molecularSpreadSheetService = molecularSpreadSheetService
        when:
        Map map = molSpreadSheetDataBuilder.deriveListOfExperimentsFromIds(pids, cids, adids)
        then:
        1 * molecularSpreadSheetService.projectIdsToExperiments(pids) >> {new RuntimeException()}
        assert map
        assert !map.experimentList
        assert !map.molSpreadsheetDerivedMethod
    }

    void "test set mol spreadsheet data builder"() {
        when:
        MolSpreadSheetDataBuilder molSpreadSheetDataBuilder = new MolSpreadSheetDataBuilder()
        MolSpreadSheetData molSpreadSheetData = new MolSpreadSheetData()
        molSpreadSheetDataBuilder.molSpreadSheetData = molSpreadSheetData
        MolSpreadSheetDataBuilderDirector molSpreadSheetDataBuilderDirector = new MolSpreadSheetDataBuilderDirector()
        molSpreadSheetDataBuilderDirector.setMolSpreadSheetDataBuilder(molSpreadSheetDataBuilder)

        then:
        assert molSpreadSheetDataBuilderDirector.molSpreadSheetData
        assert molSpreadSheetDataBuilderDirector.molSpreadSheetData == molSpreadSheetDataBuilder.molSpreadSheetData

    }


}
