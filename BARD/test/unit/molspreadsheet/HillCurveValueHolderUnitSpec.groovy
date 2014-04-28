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
@TestFor(HillCurveValueHolder)
@Unroll
class HillCurveValueHolderUnitSpec extends Specification{

    SpreadSheetActivityStorage spreadSheetActivityStorage

    void setup() {
         spreadSheetActivityStorage = new  SpreadSheetActivityStorage ()
    }

    void tearDown() {
        // Tear down logic here
    }

    void "Smoke test can we build a HillCurveValueHolder"() {
        when:
        HillCurveValueHolder hillCurveValueHolder = new HillCurveValueHolder()
        assertNotNull(hillCurveValueHolder)

        then:
        assertNotNull hillCurveValueHolder.conc
        assertNotNull hillCurveValueHolder.response
    }


    void "Test constraints for HillCurveValueHolder"() {
        given:
        mockForConstraintsTests(HillCurveValueHolder)

        when:
        HillCurveValueHolder hillCurveValueHolder = new HillCurveValueHolder()
        hillCurveValueHolder.spreadSheetActivityStorage = spreadSheetActivityStorage
        assert hillCurveValueHolder.validate()
        assert !hillCurveValueHolder.hasErrors()

        then:
        assertTrue hillCurveValueHolder.validate()
        def identifier = hillCurveValueHolder.identifier
        hillCurveValueHolder.setIdentifier( null )
        assertFalse hillCurveValueHolder.validate()
        hillCurveValueHolder.setIdentifier ( identifier )
        assertTrue hillCurveValueHolder.validate()

        assertTrue hillCurveValueHolder.validate()
        def subColumnIndex = hillCurveValueHolder.subColumnIndex
        hillCurveValueHolder.setSubColumnIndex( null )
        assertFalse hillCurveValueHolder.validate()
        hillCurveValueHolder.setSubColumnIndex ( subColumnIndex )
        assertTrue hillCurveValueHolder.validate()

        hillCurveValueHolder.setS0(null)
        assertTrue hillCurveValueHolder.validate()

        hillCurveValueHolder.setSlope(null)
        assertTrue hillCurveValueHolder.validate()

        hillCurveValueHolder.setCoef(null)
        assertTrue hillCurveValueHolder.validate()

        hillCurveValueHolder.setConc(null)
        assertTrue hillCurveValueHolder.validate()

        hillCurveValueHolder.setResponse(null)
        assertTrue hillCurveValueHolder.validate()

    }



    void "Test toString method"() {
        when:
        HillCurveValueHolder hillCurveValueHolder = new HillCurveValueHolder()
        hillCurveValueHolder.slope = slope
        hillCurveValueHolder.response = [response]
        hillCurveValueHolder.stringValue = stringValue

        then:
        assertNotNull hillCurveValueHolder
        assert hillCurveValueHolder.toString().trim() ==  returnValue

        where:
        slope       |   response    |   stringValue |   returnValue
        47.89       |   null        |   ""          |   "47.9"
        null        |   47.89       |   ""          |   "47.9"
        47.89       |   47.89       |   ""          |   "47.9"
        null        |   null        |   ""          |   "--"
        47.89       |   null        |   "Inhibitor" |   "Inhibitor"
        null        |   47.89       |   "Inhibitor" |   "Inhibitor"
        47.89       |   47.89       |   "Inhibitor" |   "Inhibitor"
        null        |   null        |   "Inhibitor" |   "Inhibitor"

    }



}
