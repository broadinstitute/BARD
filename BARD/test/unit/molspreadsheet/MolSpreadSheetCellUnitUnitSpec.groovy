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

import grails.test.mixin.TestMixin
import grails.test.mixin.support.GrailsUnitTestMixin
import spock.lang.Specification
import spock.lang.Unroll

/**
 * Created with IntelliJ IDEA.
 * User: balexand
 * Date: 10/12/12
 * Time: 8:40 AM
 * To change this template use File | Settings | File Templates.
 */
@TestMixin(GrailsUnitTestMixin)
@Unroll
class MolSpreadSheetCellUnitUnitSpec extends Specification {
    void setup() {

    }

    void tearDown() {
        // Tear down logic here
    }


    void "test getByDecimalValue #label"() {
        given:

        when: "#label"
        MolSpreadSheetCellUnit molSpreadSheetCellUnit = expectedMolSpreadSheetUnit

        then: "Should equal the expected"
        assert molSpreadSheetCellUnit.toString() == value

        where:
        label        | value | expectedMolSpreadSheetUnit
        "Molar"      | "M"   | MolSpreadSheetCellUnit.Molar
        "MilliMolar" | "mM"  | MolSpreadSheetCellUnit.Millimolar
        "Micromolar" | "uM"  | MolSpreadSheetCellUnit.Micromolar
        "Nanomolar"  | "nM"  | MolSpreadSheetCellUnit.Nanomolar
        "Picomolar"  | "pM"  | MolSpreadSheetCellUnit.Picomolar
        "Femtomolar" | "fM"  | MolSpreadSheetCellUnit.Femtomolar
        "Attamolar"  | "aM"  | MolSpreadSheetCellUnit.Attamolar
        "Zeptomolar" | "zM"  | MolSpreadSheetCellUnit.Zeptomolar
        "Yoctomolar" | "yM"  | MolSpreadSheetCellUnit.Yoctomolar
        "Null"       | "U"   | MolSpreadSheetCellUnit.unknown
    }


    void "test MolSpreadSheetCell constructor, two parameters, no units"() {
        when:
        MolSpreadSheetCell molSpreadSheetCell = new MolSpreadSheetCell(inputString, molSpreadSheetCellType)
        assertNotNull(molSpreadSheetCell)

        then:
        assert molSpreadSheetCell.toString() == resultingString
        assert molSpreadSheetCell.activity == activity

        where:
        molSpreadSheetCellType                    | inputString | resultingString | activity
        MolSpreadSheetCellType.identifier         | "3"         | "3"             | MolSpreadSheetCellActivityOutcome.Unknown
    }


    void "test MolSpreadSheetCell constructor, two parameters, no units, error condition"() {
        when:
        MolSpreadSheetCell molSpreadSheetCell = new MolSpreadSheetCell("NaN", MolSpreadSheetCellType.identifier)
        assertNotNull(molSpreadSheetCell)

        then:
        assert molSpreadSheetCell.toString() == "0"
    }


    void "test MolSpreadSheetCell constructor, three parameters, no units, unknown type error condition"() {
        when:
        MolSpreadSheetCell molSpreadSheetCell = new MolSpreadSheetCell("NaN", MolSpreadSheetCellType.unknown, MolSpreadSheetCellUnit.Micromolar)
        assertNotNull(molSpreadSheetCell)

        then:
        assert molSpreadSheetCell.molSpreadSheetCellType == MolSpreadSheetCellType.unknown
    }


    void "test MolSpreadSheetCell constructor, three parameters, no units, string error condition"() {
        when:
        MolSpreadSheetCell molSpreadSheetCell = new MolSpreadSheetCell("0", MolSpreadSheetCellType.string, MolSpreadSheetCellUnit.Micromolar)
        assertNotNull(molSpreadSheetCell)

        then:
        assert molSpreadSheetCell.toString() == "null"
    }

    void "test MolSpreadSheetCell constructor, three parameters: String value1, String value2, MolSpreadSheetCellType molSpreadSheetCellType: #label"() {
        when:
        MolSpreadSheetCell molSpreadSheetCell = new MolSpreadSheetCell("0", "1", molSpreadSheetCellType)

        then:
        assertNotNull(molSpreadSheetCell)
        assert molSpreadSheetCell.strInternalValue == expectedStrInternalValue


        where:
        label                           | molSpreadSheetCellType        | expectedStrInternalValue
        'MolSpreadSheetCellType.string' | MolSpreadSheetCellType.string | "null"
        'MolSpreadSheetCellType.image'  | MolSpreadSheetCellType.image  | "0"
    }

    void "test overriden toString(): #label"() {
        when:
        MolSpreadSheetCell molSpreadSheetCell = new MolSpreadSheetCell()
        molSpreadSheetCell.activity = MolSpreadSheetCellActivityOutcome.Unknown
        molSpreadSheetCell.molSpreadSheetCellType = molSpreadSheetCellType
        molSpreadSheetCell.strInternalValue = 'something'
        molSpreadSheetCell.intInternalValue = 2

        then:
        assertNotNull(molSpreadSheetCell)
        assert molSpreadSheetCell.toString() == expectedToString


        where:
        label                                       | molSpreadSheetCellType                    | expectedToString
        'MolSpreadSheetCellType.identifier'         | MolSpreadSheetCellType.identifier         | "2"
        'MolSpreadSheetCellType.image'              | MolSpreadSheetCellType.image              | null
    }
}
