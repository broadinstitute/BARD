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

import grails.test.mixin.TestFor
import spock.lang.Unroll
import spock.lang.Specification

/**
 * See the API for {@link grails.test.mixin.support.GrailsUnitTestMixin} for usage instructions
 */
@TestFor(SpreadSheetActivityStorage)
@Unroll
class SpreadSheetActivityStorageSpec  extends Specification{

    MolSpreadSheetCell molSpreadSheetCell

    void setup() {
         molSpreadSheetCell = new MolSpreadSheetCell()
    }

    void tearDown() {
        // Tear down logic here
    }

    void "Test that we can build a Basic molecular spreadsheet cell"() {
        when:
        SpreadSheetActivityStorage spreadSheetActivityStorage = new SpreadSheetActivityStorage()
        assertNotNull(spreadSheetActivityStorage)

        then:
        assertNotNull spreadSheetActivityStorage.hillCurveValueHolderList
        assertNotNull spreadSheetActivityStorage.columnNames
    }


    void "Test molecular spreadsheet cell constructor"() {
        given:
        SpreadSheetActivityStorage originalSpreadSheetActivityStorage = new SpreadSheetActivityStorage()
        originalSpreadSheetActivityStorage.eid=41L
        originalSpreadSheetActivityStorage.cid=42L
        originalSpreadSheetActivityStorage.sid=43L
        originalSpreadSheetActivityStorage.hillCurveValueHolderList << new HillCurveValueHolder(slope: 1, identifier:"a",subColumnIndex: 1)
        originalSpreadSheetActivityStorage.hillCurveValueHolderList << new HillCurveValueHolder(slope: 1, identifier:"b",subColumnIndex: 2)


        when:
        SpreadSheetActivityStorage copySpreadSheetActivityStorage = new SpreadSheetActivityStorage(originalSpreadSheetActivityStorage,hillCurveValueIndex)
        assertNotNull(copySpreadSheetActivityStorage)

        then:
        assert originalSpreadSheetActivityStorage.eid ==  copySpreadSheetActivityStorage.eid
        assert originalSpreadSheetActivityStorage.cid ==  copySpreadSheetActivityStorage.cid
        assert originalSpreadSheetActivityStorage.sid ==  copySpreadSheetActivityStorage.sid
        assert copySpreadSheetActivityStorage.hillCurveValueHolderList[0]?.subColumnIndex ==  columnIndex

        where:
        hillCurveValueIndex     |   identifier  |   columnIndex
        0                       |  "a"          |   1
        1                       |  "b"          |   2
        2                       |  null         |   null
    }



    void "Test constraints for molecular spreadsheet data"() {
        given:
        mockForConstraintsTests(SpreadSheetActivityStorage)

        when:
        SpreadSheetActivityStorage spreadSheetActivityStorage = new SpreadSheetActivityStorage()
        spreadSheetActivityStorage.molSpreadSheetCell = molSpreadSheetCell
        spreadSheetActivityStorage.validate()
        spreadSheetActivityStorage.hasErrors()

        then:
        spreadSheetActivityStorage.setEid(null)
        assertTrue spreadSheetActivityStorage.validate()

        spreadSheetActivityStorage.setCid(null)
        assertTrue spreadSheetActivityStorage.validate()

        spreadSheetActivityStorage.setSid(null)
        assertTrue spreadSheetActivityStorage.validate()

        spreadSheetActivityStorage.setActivityOutcome(null)
        assertTrue spreadSheetActivityStorage.validate()

        spreadSheetActivityStorage.setPotency(null)
        assertTrue spreadSheetActivityStorage.validate()

    }



}
