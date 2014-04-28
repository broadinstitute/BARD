/* Copyright (c) 2014, The Broad Institute
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
    * Redistributions of source code must retain the above copyright
      notice, this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright
      notice, this list of conditions and the following disclaimer in the
      documentation and/or other materials provided with the distribution.
    * Neither the name of The Broad Institute nor the
      names of its contributors may be used to endorse or promote products
      derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL The Broad Institute BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

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
        Map deriveListOfExperiments = molSpreadSheetDataBuilder.deriveListOfExperimentsFromIds(cartProjectList,cartAssayList,cartCompoundList,true)
        List<ExperimentSearch> experimentList = deriveListOfExperiments.experimentList
        MolSpreadsheetDerivedMethod molSpreadsheetDerivedMethod = deriveListOfExperiments.molSpreadsheetDerivedMethod
        assertNotNull experimentList
        assert experimentList.size() == 0
        assertNull molSpreadsheetDerivedMethod
        molSpreadSheetDataBuilder.mapExperimentIdsToCapAssayIds.size()==0
    }
}
