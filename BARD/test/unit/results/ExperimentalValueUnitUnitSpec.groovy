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
import molspreadsheet.MolSpreadSheetCellUnit
import org.apache.commons.lang.NotImplementedException
import spock.lang.Specification
import spock.lang.Unroll
import bard.core.util.ExperimentalValueUnitUtil

@TestMixin(GrailsUnitTestMixin)
@Unroll
class ExperimentalValueUnitUnitSpec extends Specification {


    void "test getByDecimalValue #label"() {
        given:

        when: "#label"
        final ExperimentalValueUnitUtil experimentalValueUnit = ExperimentalValueUnitUtil.getByDecimalValue(value)

        then: "Should equal the expected"
        assert experimentalValueUnit == expectedExperimentValueUnit
        where:
        label        | value | expectedExperimentValueUnit
        "Molar"      | 0     | ExperimentalValueUnitUtil.Molar
        "MilliMolar" | -3    | ExperimentalValueUnitUtil.Millimolar
        "Micromolar" | -6    | ExperimentalValueUnitUtil.Micromolar
        "Nanomolar"  | -9    | ExperimentalValueUnitUtil.Nanomolar
        "Picomolar"  | -12   | ExperimentalValueUnitUtil.Picomolar
        "Femtomolar" | -15   | ExperimentalValueUnitUtil.Femtomolar
        "Attamolar"  | -18   | ExperimentalValueUnitUtil.Attamolar
        "Zeptomolar" | -21   | ExperimentalValueUnitUtil.Zeptomolar
        "Yoctomolar" | -24   | ExperimentalValueUnitUtil.Yoctomolar
        "Null"       | 30    | null


    }

    void "test convert #label"() {


        when: "We call the convert method"
        final ExperimentalValueUnitUtil experimentalValueUnit = ExperimentalValueUnit.convert(molSpreadSheetCellUnit)

        then: "The resulting experimental value type should much the expected one"
        assert experimentalValueUnit == expectedExperimentalValueUnit

        where:
        label                  | molSpreadSheetCellUnit            | expectedExperimentalValueUnit
        "Less Than Numeric"    | MolSpreadSheetCellUnit.Molar      | ExperimentalValueUnitUtil.Molar
        "Greater Than Numeric" | MolSpreadSheetCellUnit.Millimolar | ExperimentalValueUnitUtil.Millimolar
        "Percentage Numeric"   | MolSpreadSheetCellUnit.Micromolar | ExperimentalValueUnitUtil.Micromolar
        "Numeric"              | MolSpreadSheetCellUnit.Nanomolar  | ExperimentalValueUnitUtil.Nanomolar
        "Image"                | MolSpreadSheetCellUnit.Picomolar  | ExperimentalValueUnitUtil.Picomolar
        "String"               | MolSpreadSheetCellUnit.Femtomolar | ExperimentalValueUnitUtil.Femtomolar
        "Unknown"              | MolSpreadSheetCellUnit.Attamolar  | ExperimentalValueUnitUtil.Attamolar
        "Unknown"              | MolSpreadSheetCellUnit.Zeptomolar | ExperimentalValueUnitUtil.Zeptomolar
        "Unknown"              | MolSpreadSheetCellUnit.Yoctomolar | ExperimentalValueUnitUtil.Yoctomolar
        "Unknown"              | MolSpreadSheetCellUnit.unknown    | ExperimentalValueUnitUtil.unknown


    }



    void "test getByValue"() {


        when: "We call the convert method"
        final ExperimentalValueUnitUtil experimentalValueUnit = ExperimentalValueUnit.convert(molSpreadSheetCellUnit)

        then: "The resulting experimental value type should much the expected one"
        assert experimentalValueUnit == expectedExperimentalValueUnit

        where:
        label                  | molSpreadSheetCellUnit            | expectedExperimentalValueUnit
        "Less Than Numeric"    | MolSpreadSheetCellUnit.Molar      | ExperimentalValueUnitUtil.Molar
        "Greater Than Numeric" | MolSpreadSheetCellUnit.Millimolar | ExperimentalValueUnitUtil.Millimolar
        "Percentage Numeric"   | MolSpreadSheetCellUnit.Micromolar | ExperimentalValueUnitUtil.Micromolar
        "Numeric"              | MolSpreadSheetCellUnit.Nanomolar  | ExperimentalValueUnitUtil.Nanomolar
        "Image"                | MolSpreadSheetCellUnit.Picomolar  | ExperimentalValueUnitUtil.Picomolar
        "String"               | MolSpreadSheetCellUnit.Femtomolar | ExperimentalValueUnitUtil.Femtomolar
        "Unknown"              | MolSpreadSheetCellUnit.Attamolar  | ExperimentalValueUnitUtil.Attamolar
        "Unknown"              | MolSpreadSheetCellUnit.Zeptomolar | ExperimentalValueUnitUtil.Zeptomolar
        "Unknown"              | MolSpreadSheetCellUnit.Yoctomolar | ExperimentalValueUnitUtil.Yoctomolar
        "Unknown"              | MolSpreadSheetCellUnit.unknown    | ExperimentalValueUnitUtil.unknown


    }





    void "test convert with Exception"() {


        when: "We call the convert method"
        ExperimentalValueUnit.convert(molSpreadSheetCellUnit)

        then: "We expect an error to  be thrown"
        Exception ee = thrown()
        assert ee instanceof NotImplementedException


        where:
        label            | molSpreadSheetCellUnit
        "Unhandled Enum" | MolSpreadSheetCellUnit.unhandled


    }
}

