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

package querycart

import grails.test.mixin.TestFor
import org.apache.commons.lang.RandomStringUtils
import spock.lang.Specification
import spock.lang.Unroll

/**
 * See the API for {@link grails.test.mixin.support.GrailsUnitTestMixin} for usage instructions
 */
//@TestMixin(GrailsUnitTestMixin)
@TestFor(CartAssay)
@Unroll
class CartAssayUnitSpec extends Specification {

    void setup() {
        // Setup logic here
    }

    void tearDown() {
        // Tear down logic here
    }

    void "test constructor with integer"() {
        given:
        int assayId = 2
        when:
        CartAssay cartAssay = new CartAssay("Assay title", assayId-1, assayId)

        then:
        assert cartAssay.name == 'Assay title'
        assert cartAssay.externalId == assayId
    }

    void "test toString #label"() {

        when:
        final String assayAsString = cartAssay.toString()
        then:
        assert assayAsString == expectedTitle
        where:
        label                  | cartAssay                             | expectedTitle
        "Empty Title"          | new CartAssay()                       | ""
        "Null String as title" | new CartAssay("null", 99, 100)        | ""
        "With title"           | new CartAssay("Some Title", 109, 110) | "Some Title"
    }

    void "Test equals #label"() {
        when:
        final boolean equals = cartAssay.equals(otherCartAssay)

        then:
        equals == equality
        where:
        label               | cartAssay                             | otherCartAssay                        | equality
        "Other is null"     | new CartAssay()                       | null                                  | false
        "Different classes" | new CartAssay()                       | 20                                    | false
        "Equality"          | new CartAssay("Some Title", 119, 120) | new CartAssay("Some Title", 119, 120) | true


    }

    void "Test hashCode #label"() {
        when:
        final int code = cartAssay.hashCode()

        then:
        assert code
        where:
        label               | cartAssay
        "Other is null"     | new CartAssay()
        "Different classes" | new CartAssay()
        "Equality"          | new CartAssay("Some Title", 49, 50)


    }

    void "test shopping cart assay element"() {
        when:
        CartAssay cartAssay = new CartAssay("Assay title", 49, 50)
        assertNotNull(cartAssay)

        then:
        assert cartAssay.name == 'Assay title'
        assertNull cartAssay.shoppingItem
    }

    /**
     * constraint test.
     */
    void "test constraints on CartAssay object with title length = #stringLength"() {
        given:
        mockForConstraintsTests(CartAssay)

        final String assayTitle = RandomStringUtils.randomAlphabetic(stringLength)

        when:
        CartAssay cartAssay = new CartAssay(assayTitle, 46, 47 as Long)
        cartAssay.validate()

        then:
        cartAssay.hasErrors() == !valid

        where:
        stringLength | valid
        0            | false
        20           | true
        4000         | true
        40001        | true
    }

    void "test adding ellipses when the assay title is too long"() {
        given:
        mockForConstraintsTests(CartAssay)
        final String assayTitle = RandomStringUtils.randomAlphabetic(stringLength)

        when:
        CartAssay cartAssay = new CartAssay(assayTitle, assayId-1, assayId)
        cartAssay.validate()

        then:
        cartAssay.toString().length() == properStringLength

        where:
        assayId | stringLength | properStringLength
        47      | 4001         | 4000
        47      | 80000        | 4000
        47      | 25           | 25
        2       | 0            | 0
    }


}
