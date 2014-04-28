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

import bardqueryapi.ActivityOutcome
import grails.test.mixin.TestMixin
import grails.test.mixin.support.GrailsUnitTestMixin
import spock.lang.Specification
import spock.lang.Unroll

/**
 * Created with IntelliJ IDEA.
 * User: balexand
 * Date: 1/16/13
 * Time: 1:26 PM
 * To change this template use File | Settings | File Templates.
 */
@TestMixin(GrailsUnitTestMixin)
@Unroll
class MolSpreadSheetCellActivityOutcomeUnitSpec  extends Specification{
    void setup() {

    }

    void tearDown() {
        // Tear down logic here
    }

    void "test convert #label"() {


        when: "Call all the conversion routines for MolSpreadSheetCellActivityOutcome #label"
        final  MolSpreadSheetCellActivityOutcome molSpreadSheetCellActivityOutcome = MolSpreadSheetCellActivityOutcome.newMolSpreadSheetCellActivityOutcome(activityOutcome)

        then: "The resulting experimental value type should much the expected one"
        assert molSpreadSheetCellActivityOutcome == expectedMolSpreadSheetCellActivityOutcome

        where:
        label                   | activityOutcome                | expectedMolSpreadSheetCellActivityOutcome
        "Active"                | ActivityOutcome.ACTIVE         | MolSpreadSheetCellActivityOutcome.Active
        "Inactive"              | ActivityOutcome.INACTIVE       | MolSpreadSheetCellActivityOutcome.Inactive
        "Inconclusive"          | ActivityOutcome.INCONCLUSIVE   | MolSpreadSheetCellActivityOutcome.Inconclusive
        "Probe"                 | ActivityOutcome.PROBE          | MolSpreadSheetCellActivityOutcome.Probe
        "Unspecified"           | ActivityOutcome.UNSPECIFIED    | MolSpreadSheetCellActivityOutcome.Unspecified
        "uninitialized"         | null                           | MolSpreadSheetCellActivityOutcome.Unknown

    }

    void "MolSpreadSheetCellActivityOutcome smoke test"() {


        when: "We call the convert method"
        MolSpreadSheetCellActivityOutcome molSpreadSheetCellActivityOutcome = MolSpreadSheetCellActivityOutcome.Active

        then: "The resulting experimental value is completely unexpected, so throw an exception"
        assertNotNull molSpreadSheetCellActivityOutcome.color
        molSpreadSheetCellActivityOutcome.toString() == "Active"
    }
}
