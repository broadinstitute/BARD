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

package results

import grails.test.mixin.TestMixin
import grails.test.mixin.support.GrailsUnitTestMixin
import molspreadsheet.MolSpreadSheetCellType
import org.apache.commons.lang.NotImplementedException
import spock.lang.Specification
import spock.lang.Unroll
import bard.core.util.ExperimentalValueTypeUtil

@TestMixin(GrailsUnitTestMixin)
@Unroll
class ExperimentalValueTypeUnitSpec extends Specification {
    void setup() {

    }

    void tearDown() {
        // Tear down logic here
    }

    void "test convert #label"() {


        when: "We call the convert method"
        final ExperimentalValueTypeUtil experimentalValueType = ExperimentalValueType.convert(cellType)

        then: "The resulting experimental value type should much the expected one"
        assert experimentalValueType == expectedExperimentalValueType

        where:
        label                  | cellType                                  | expectedExperimentalValueType
        "Less Than Numeric"    | MolSpreadSheetCellType.lessThanNumeric    | ExperimentalValueTypeUtil.lessThanNumeric
        "Greater Than Numeric" | MolSpreadSheetCellType.greaterThanNumeric | ExperimentalValueTypeUtil.greaterThanNumeric
        "Percentage Numeric"   | MolSpreadSheetCellType.percentageNumeric  | ExperimentalValueTypeUtil.percentageNumeric
        "Numeric"              | MolSpreadSheetCellType.numeric            | ExperimentalValueTypeUtil.numeric
        "Image"                | MolSpreadSheetCellType.image              | ExperimentalValueTypeUtil.image
        "String"               | MolSpreadSheetCellType.string             | ExperimentalValueTypeUtil.string
        "Unknown"              | MolSpreadSheetCellType.unknown            | ExperimentalValueTypeUtil.unknown

    }

    void "test convert throw NotImplementedException"() {


        when: "We call the convert method"
        ExperimentalValueType.convert(cellType)

        then: "The resulting experimental value is completely unexpected, so throw an exception"
        Exception ee = thrown()
        assert ee instanceof NotImplementedException

        where:
        label        | cellType
        "Identifier" | MolSpreadSheetCellType.identifier

    }

    void "test default switch"() {
        when: "We call the convert method for something we don't handle"
        ExperimentalValueType.convert(cellType)

        then: "The code should throw an exception"
        Exception ee = thrown()
        assert ee instanceof NotImplementedException

        where:
        label                 | cellType
        "UnHandled Exception" | MolSpreadSheetCellType.unhandled
    }


}
