package molspreadsheet

import bard.core.adapter.CompoundAdapter
import bard.core.rest.spring.CompoundRestService
import bard.core.rest.spring.compounds.Compound
import bard.core.rest.spring.compounds.CompoundResult
import bard.core.rest.spring.experiment.ExperimentSearch
import bardqueryapi.QueryHelperService
import bardqueryapi.QueryService
import grails.test.mixin.TestMixin
import grails.test.mixin.support.GrailsUnitTestMixin
import querycart.CartAssay
import querycart.CartCompound
import querycart.CartProject
import spock.lang.Specification
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
    CompoundRestService compoundRestService
    QueryService queryService
    QueryHelperService queryHelperService

    void setup() {
        this.molecularSpreadSheetService = Mock(MolecularSpreadSheetService)
        this.compoundRestService = Mock(CompoundRestService)
        this.queryService = Mock(QueryService)
        this.queryHelperService = Mock(QueryHelperService)
        this.queryService.queryHelperService = this.queryHelperService
        this.molecularSpreadSheetService.queryService = this.queryService
        this.molecularSpreadSheetService.compoundRestService = this.compoundRestService
    }

    void tearDown() {
        // Tear down logic here
    }

    void "test populateMolSpreadSheet Non_Empty Compound Cart"() {
        given:
        Compound compound = new Compound(smiles: "C", cid: 200, numActiveAssay: 0, numAssay: 0)
        MolSpreadSheetDataBuilder molSpreadSheetDataBuilder = new MolSpreadSheetDataBuilder()
        molSpreadSheetDataBuilder.molecularSpreadSheetService = this.molecularSpreadSheetService
        // molSpreadSheetDataBuilder.cartCompoundList = [new CartCompound("C", "C", 200, 0, 0)]
        when:
        molSpreadSheetDataBuilder.populateMolSpreadSheetWithCids([], MolSpreadsheetDerivedMethod.Compounds_NoAssays_NoProjects, [200])
        then:
        1 * molecularSpreadSheetService.generateETagFromCids(_) >> {"etag"}
        2 * molecularSpreadSheetService.compoundRestService >> {this.compoundRestService}
        1 * molecularSpreadSheetService.compoundRestService.searchCompoundsByIds(_) >> { new CompoundResult(compounds: [compound])}
        3 * molecularSpreadSheetService.queryService >> {this.queryService}
        2 * molecularSpreadSheetService.queryService.queryHelperService >> {this.queryHelperService}
        0 * molecularSpreadSheetService.queryService.queryHelperService.compoundsToAdapters(_) >> {[new CompoundAdapter(compound)]}
        1 * molecularSpreadSheetService.populateMolSpreadSheetRowMetadataFromCompoundAdapters(_, _, _) >> {}
        1 * molecularSpreadSheetService.extractMolSpreadSheetData(_, _, _) >> {[new SpreadSheetActivity()]}

        1 * molecularSpreadSheetService.populateMolSpreadSheetColumnMetadata(_, _) >> {}

        molecularSpreadSheetService.populateMolSpreadSheetData(_, _, _, _) >> {}
        molecularSpreadSheetService.fillInTheMissingCellsAndConvertToExpandedMatrix(_, _) >> {}
        molecularSpreadSheetService.prepareMapOfColumnsToAssay(_) >> {}
    }


    void "test deriveListOfExperiments in the degenerate case"() {
        when:
        List<Long> cartCompoundList = []
        List<Long> cartAssayList = []
        List<Long> cartProjectList = []
        MolSpreadSheetDataBuilder molSpreadSheetDataBuilder = new MolSpreadSheetDataBuilder()

        then: "The expected hashCode is returned"
        Map deriveListOfExperiments = molSpreadSheetDataBuilder.deriveListOfExperimentsFromIds(cartProjectList,cartAssayList,cartCompoundList)
        List<ExperimentSearch> experimentList = deriveListOfExperiments.experimentList
        MolSpreadsheetDerivedMethod molSpreadsheetDerivedMethod = deriveListOfExperiments.molSpreadsheetDerivedMethod
        assertNotNull experimentList
        assert experimentList.size() == 0
        assertNull molSpreadsheetDerivedMethod
    }
}
