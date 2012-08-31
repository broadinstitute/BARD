package bardqueryapi

import grails.test.mixin.TestFor
import spock.lang.Specification

/**
 * See the API for {@link grails.test.mixin.support.GrailsUnitTestMixin} for usage instructions
 */
//@TestMixin(GrailsUnitTestMixin)
@TestFor(BardWebInterfaceController)
class CartCompoundSpec  extends Specification  {

    void setup() {
        // Setup logic here
    }

    void tearDown() {
        // Tear down logic here
    }

    void "test shopping cart compound element"() {
        given:
             String moleculeDefinition =  "c1ccccc1"
             def compdId = 47

        when:
        CartCompound cartCompound = new CartCompound( moleculeDefinition)
        cartCompound.compoundId= compdId
        // CartCompound cartCompound = new CartCompound( moleculeDefinition,  compoundId)
// WHY NOT? -->       CartCompound cartCompound =   new CartCompound( smiles:moleculeDefinition,  compoundId:compdId)
        assertNotNull(cartCompound)

        then:
            assert cartCompound.smiles==moleculeDefinition
            assert cartCompound.compoundId==compdId
            assertNull cartCompound.shoppingItem
    }



    void "test constraints on compound object"() {
        setup:
        mockForConstraintsTests(CartCompound)

        when:
        CartCompound cartCompound = new CartCompound(smiles,compoundId)
        cartCompound.validate()

        then:
        cartCompound.hasErrors() == !valid

        where:
        compoundId  |   smiles          |   valid
        47          |   null            |   false
        47          |   ""              |   true
        47          |   "Some assay"    |   true
        0           |   null            |   false
        0           |   ""              |   false
        0           |   "Some assay"    |   false
    }


}
