package bardqueryapi

import grails.test.mixin.TestFor
import org.apache.commons.lang.RandomStringUtils
import spock.lang.Specification
import spock.lang.Unroll

/**
 * See the API for {@link grails.test.mixin.support.GrailsUnitTestMixin} for usage instructions
 */
//@TestMixin(GrailsUnitTestMixin)
@TestFor(BardWebInterfaceController)
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
        Integer compdId = 47

        when:
        CartCompound cartCompound = new CartCompound(smiles: moleculeDefinition, compoundId: compdId)
        // CartCompound cartCompound = new CartCompound( moleculeDefinition,  compoundId)
// WHY NOT? -->       CartCompound cartCompound =   new CartCompound( smiles:moleculeDefinition,  compoundId:compdId)
        assertNotNull(cartCompound)

        then:
        assert cartCompound.smiles == moleculeDefinition
        assert cartCompound.compoundId == compdId
        assertNull cartCompound.shoppingItem
    }

    void "test setSmiles #label"() {
        given:
        final String smiles = RandomStringUtils.randomAlphabetic(smilesLength)
        CartCompound cartCompound = new CartCompound()

        when:
        cartCompound.setSmiles(smiles)

        then:
        assert cartCompound.smileWasTruncated == smilesWasTruncated

        where:
        label                  | smilesLength                                 | smilesWasTruncated
        "Empty Smiles"         | 0                                            | false
        "More than Max Length" | CartCompound.MAXIMUM_SMILES_FIELD_LENGTH + 1 | true
        "Equals Max Length"    | CartCompound.MAXIMUM_SMILES_FIELD_LENGTH     | false
        "Less Than Max Length" | 2                                            | false
    }

    void "test setName #label"() {
        given:
        final String name = RandomStringUtils.randomAlphabetic(nameLength)
        CartCompound cartCompound = new CartCompound()

        when:
        cartCompound.setName(name)

        then:
        assert cartCompound.nameWasTruncated == nameWasTruncated

        where:
        label                  | nameLength                                 | nameWasTruncated
        "Empty Name"           | 0                                          | false
        "More than Max Length" | CartCompound.MAXIMUM_NAME_FIELD_LENGTH + 1 | true
        "Equals Max Length"    | CartCompound.MAXIMUM_NAME_FIELD_LENGTH     | false
        "Less Than Max Length" | 2                                          | false
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
        "Equality"            | new CartCompound(smiles: "CC", name: "Name") | new CartCompound(smiles: "CC", name: "Name") | true


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
        47         | 40001      | "c1ccccc1" | false

    }

    // Note: In this case we are using a setter, and therefore we must NOT mockForConstraintsTests (otherwise
    // the setter will never be hit.
    void "test toString special cases"() {
        given:

        when:
        CartCompound cartCompound = new CartCompound(smiles, name, compoundId)
        cartCompound.setName(name)

        then:
        cartCompound.toString() == properName

        where:
        compoundId | name                   | smiles     | properName
        47         | null                   | "c1ccccc1" | "PubChem CID=47"
        47         | ""                     | "c1ccccc1" | "PubChem CID=47"
        47         | "null"                 | "c1ccccc1" | "PubChem CID=47"
        47         | "dimethyl tryptamine"  | "c1ccccc1" | "dimethyl tryptamine"
        47         | "undifferentiated goo" | "c1ccccc1" | "undifferentiated goo"
    }

    // Note: In this case we are using a setter, and therefore we must NOT mockForConstraintsTests (otherwise
    // the setter will never be hit.
    void "test toString special cases with ellipsis"() {
        given:
        final String name = RandomStringUtils.randomAlphabetic(stringLength)

        when:
        CartCompound cartCompound = new CartCompound(smiles, name, compoundId)
        cartCompound.setName(name)

        then:
        cartCompound.toString().length() == properNameLength

        where:
        compoundId | stringLength | smiles     | properNameLength
        47         | 1000         | "c1ccccc1" | 1000
        47         | 4000         | "c1ccccc1" | 4000
        47         | 4001         | "c1ccccc1" | 4003
    }


}
