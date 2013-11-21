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



    void "test deriveListOfExperimentsFromIdswith pids"() {
        given:
        final List<Long> pids = [2]
        final List<Long> adids = []
        final List<Long> cids = []
        MolSpreadSheetDataBuilder molSpreadSheetDataBuilder = new MolSpreadSheetDataBuilder()
        MolecularSpreadSheetService molecularSpreadSheetService = Mock(MolecularSpreadSheetService)
        molSpreadSheetDataBuilder.molecularSpreadSheetService = molecularSpreadSheetService
        when:
        Map map = molSpreadSheetDataBuilder.deriveListOfExperimentsFromIds(pids, cids, adids, true)
        then:
        assert map
        assert !map.experimentList
        assert map.molSpreadsheetDerivedMethod.toString()=="NoCompounds_NoAssays_Projects"
    }







    void "test deriveListOfExperimentsFromIds with aids"() {
        given:
        final List<Long> pids = []
        final List<Long> adids = [2]
        final List<Long> cids = []
        MolSpreadSheetDataBuilder molSpreadSheetDataBuilder = new MolSpreadSheetDataBuilder()
        MolecularSpreadSheetService molecularSpreadSheetService = Mock(MolecularSpreadSheetService)
        molSpreadSheetDataBuilder.molecularSpreadSheetService = molecularSpreadSheetService
        when:
        Map map = molSpreadSheetDataBuilder.deriveListOfExperimentsFromIds(pids, adids,cids, true)
        then:
        assert map
        assert !map.experimentList
        assert map.molSpreadsheetDerivedMethod.toString()=="NoCompounds_Assays_NoProjects"

    }






    void "test deriveListOfExperimentsFromIds with cids"() {
        given:
        final List<Long> pids = []
        final List<Long> adids = []
        final List<Long> cids = [2]
        Map<Long, String> mapCapAssayIdsToAssayNames = [:]
        MolSpreadSheetDataBuilder molSpreadSheetDataBuilder = new MolSpreadSheetDataBuilder()
        MolecularSpreadSheetService molecularSpreadSheetService = Mock(MolecularSpreadSheetService)
        molSpreadSheetDataBuilder.molecularSpreadSheetService = molecularSpreadSheetService
        when:
        Map map = molSpreadSheetDataBuilder.deriveListOfExperimentsFromIds(pids, adids, cids, true)
        then:
        1 * molecularSpreadSheetService.compoundIdsToExperiments(cids,molSpreadSheetDataBuilder.mapExperimentIdsToCapAssayIds,mapCapAssayIdsToAssayNames, true) >> {new RuntimeException()}
        assert map
        assert !map.experimentList
        assert !map.molSpreadsheetDerivedMethod
        molSpreadSheetDataBuilder.mapExperimentIdsToCapAssayIds.size()==0
        molSpreadSheetDataBuilder.mapCapAssayIdsToAssayNames.size()==0

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
