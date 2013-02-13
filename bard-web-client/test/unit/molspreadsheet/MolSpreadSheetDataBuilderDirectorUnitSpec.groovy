package molspreadsheet

import spock.lang.Specification
import bard.core.rest.spring.experiment.ExperimentSearch

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
        Map<Long, Long> mapExperimentIdsToCapAssayIds = [:]
        MolSpreadSheetDataBuilder molSpreadSheetDataBuilder = new MolSpreadSheetDataBuilder()
        MolecularSpreadSheetService molecularSpreadSheetService = Mock(MolecularSpreadSheetService)
        molSpreadSheetDataBuilder.molecularSpreadSheetService = molecularSpreadSheetService
        when:
        Map map = molSpreadSheetDataBuilder.deriveListOfExperimentsFromIds(pids, cids, adids, mapExperimentIdsToCapAssayIds)
        then:
        1 * molecularSpreadSheetService.projectIdsToExperiments(pids,mapExperimentIdsToCapAssayIds) >> {new RuntimeException()}
        assert map
        assert !map.experimentList
        assert !map.molSpreadsheetDerivedMethod
        mapExperimentIdsToCapAssayIds.size()==0
    }







    void "test deriveListOfExperimentsFromIds with aids"() {
        given:
        final List<Long> pids = []
        final List<Long> adids = [2]
        final List<Long> cids = []
        List<ExperimentSearch> experimentList = []
        Map<Long, Long> mapExperimentIdsToCapAssayIds = [:]
        MolSpreadSheetDataBuilder molSpreadSheetDataBuilder = new MolSpreadSheetDataBuilder()
        MolecularSpreadSheetService molecularSpreadSheetService = Mock(MolecularSpreadSheetService)
        molSpreadSheetDataBuilder.molecularSpreadSheetService = molecularSpreadSheetService
        when:
        Map map = molSpreadSheetDataBuilder.deriveListOfExperimentsFromIds(pids, adids,cids, mapExperimentIdsToCapAssayIds)
        then:
        1 * molecularSpreadSheetService.assayIdsToExperiments(experimentList,adids,mapExperimentIdsToCapAssayIds) >> {new RuntimeException()}
        assert map
        assert !map.experimentList
        assert !map.molSpreadsheetDerivedMethod
        mapExperimentIdsToCapAssayIds.size()==0
    }






    void "test deriveListOfExperimentsFromIds with cids"() {
        given:
        final List<Long> pids = []
        final List<Long> adids = []
        final List<Long> cids = [2]
        Map<Long, Long> mapExperimentIdsToCapAssayIds = [:]
        MolSpreadSheetDataBuilder molSpreadSheetDataBuilder = new MolSpreadSheetDataBuilder()
        MolecularSpreadSheetService molecularSpreadSheetService = Mock(MolecularSpreadSheetService)
        molSpreadSheetDataBuilder.molecularSpreadSheetService = molecularSpreadSheetService
        when:
        Map map = molSpreadSheetDataBuilder.deriveListOfExperimentsFromIds(pids, adids, cids, mapExperimentIdsToCapAssayIds)
        then:
        1 * molecularSpreadSheetService.compoundIdsToExperiments(cids,mapExperimentIdsToCapAssayIds) >> {new RuntimeException()}
        assert map
        assert !map.experimentList
        assert !map.molSpreadsheetDerivedMethod
        mapExperimentIdsToCapAssayIds.size()==0
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
