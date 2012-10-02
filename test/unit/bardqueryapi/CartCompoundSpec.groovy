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
class CartCompoundSpec extends Specification {

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
