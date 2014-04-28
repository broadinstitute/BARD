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
import org.apache.commons.lang.StringUtils
import spock.lang.Specification
import spock.lang.Unroll

/**
 * See the API for {@link grails.test.mixin.support.GrailsUnitTestMixin} for usage instructions
 */
//@TestMixin(GrailsUnitTestMixin)
@TestFor(CartCompound)
@Unroll
class CartCompoundUnitSpec extends Specification {

    void setup() {
        // Setup logic here
    }

    void tearDown() {
        // Tear down logic here
    }

    void "test shopping cart compound element"() {
        given:
        String moleculeDefinition = "c1ccccc1"
        String name = "test"
        Integer compdId = 47

        when:
        CartCompound cartCompound = new CartCompound(moleculeDefinition, name, compdId)
        assertNotNull(cartCompound)

        then:
        assert cartCompound.smiles == moleculeDefinition
        assert cartCompound.externalId == compdId
        assertNull cartCompound.shoppingItem
    }

    void "test truncate smiles #label"() {
        given:
        mockForConstraintsTests(CartCompound)
        String smiles = null
        if (smilesLength) {
            smiles = RandomStringUtils.randomAlphabetic(smilesLength)
        }
        final String truncatedSmiles = StringUtils.abbreviate(smiles, CartCompound.MAXIMUM_SMILES_FIELD_LENGTH)

        when:
        CartCompound cartCompound = new CartCompound(smiles, "test", 3)
        cartCompound.validate()

        then:
        assert cartCompound.smiles == truncatedSmiles

        where:
        label                  | smilesLength
        "Null Smiles"          | null
        "Empty Smiles"         | 0
        "More than Max Length" | CartCompound.MAXIMUM_SMILES_FIELD_LENGTH + 1
        "Equals Max Length"    | CartCompound.MAXIMUM_SMILES_FIELD_LENGTH
        "Less Than Max Length" | 2
    }

    void "test setName #label"() {
        given:
        mockForConstraintsTests(CartCompound)
        String name = RandomStringUtils.randomAlphabetic(nameLength)
        String truncatedName = StringUtils.abbreviate(name, CartCompound.MAXIMUM_NAME_FIELD_LENGTH)
        Long compoundId = 3

        when:
        CartCompound cartCompound = new CartCompound("CC", name, compoundId)
        cartCompound.validate()

        then:
        assert cartCompound.name == truncatedName

        where:
        label                  | nameLength
        "Empty Name"           | 0
        "More than Max Length" | CartCompound.MAXIMUM_NAME_FIELD_LENGTH + 1
        "Equals Max Length"    | CartCompound.MAXIMUM_NAME_FIELD_LENGTH
        "Less Than Max Length" | 2
    }

    void "Test equals #label"() {
        when:
        final boolean equals = cartCompound.equals(otherCartCompound)

        then:
        equals == equality
        where:
        label               | cartCompound                                 | otherCartCompound                            | equality
        "Other is null"     | new CartCompound()                           | null                                         | false
        "Different classes" | new CartCompound()                           | 20                                           | false
        "Equality"          | new CartCompound("CC", "Name", 3)            | new CartCompound("CC", "Name", 3)            | true


    }

    void "test hashCode"() {
        given:
        CartCompound cartCompound = new CartCompound()
        when:
        final int hashCode = cartCompound.hashCode()
        then:
        assert hashCode
    }

    void "test constraints on compound object"() {
        given:
        mockForConstraintsTests(CartCompound)
        final String name = RandomStringUtils.randomAlphabetic(nameLength)
        when:
        CartCompound cartCompound = new CartCompound(smiles, name, compoundId)
        cartCompound.validate()

        then:
        cartCompound.hasErrors() == !valid

        where:
        compoundId | nameLength | smiles     | valid
        47         | 0          | "c1ccccc1" | false
        47         | 0          | "c1ccccc1" | false
        47         | 10         | "c1ccccc1" | true
        0          | 0          | "c1ccccc1" | false
        0          | 0          | "c1ccccc1" | false
        0          | 10         | "c1ccccc1" | false
        47         | 4000       | "c1ccccc1" | true
        47         | 40001      | "c1ccccc1" | true

    }

    void "test toString special cases #label"() {
        given:
        mockForConstraintsTests(CartCompound)
        Long compoundId = 47

        when:
        CartCompound cartCompound = new CartCompound(smiles, name, compoundId)
        cartCompound.validate()

        then:
        cartCompound.toString() == properName

        where:
        label              | name                   | smiles     | properName
        "null name"        | null                   | "c1ccccc1" | "PubChem CID=47"
        "blank name"       | ""                     | "c1ccccc1" | "PubChem CID=47"
        "null string name" | "null"                 | "c1ccccc1" | "PubChem CID=47"
        "good name 1"      | "dimethyl tryptamine"  | "c1ccccc1" | "dimethyl tryptamine"
        "good name 2"      | "undifferentiated goo" | "c1ccccc1" | "undifferentiated goo"
    }

    void "test toString special cases with ellipsis"() {
        given:
        mockForConstraintsTests(CartCompound)
        String name = RandomStringUtils.randomAlphabetic(stringLength)
        String truncatedName = StringUtils.abbreviate(name, CartCompound.MAXIMUM_NAME_FIELD_LENGTH)

        when:
        CartCompound cartCompound = new CartCompound(smiles, name, compoundId)
        cartCompound.validate()

        then:
        cartCompound.toString() == truncatedName

        where:
        compoundId | stringLength | smiles
        47         | 1000         | "c1ccccc1"
        47         | 4000         | "c1ccccc1"
        47         | 4001         | "c1ccccc1"
    }
}
