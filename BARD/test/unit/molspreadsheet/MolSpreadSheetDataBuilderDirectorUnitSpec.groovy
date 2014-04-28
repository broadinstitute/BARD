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
