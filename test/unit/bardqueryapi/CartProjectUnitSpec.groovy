package bardqueryapi

import grails.test.mixin.TestFor
import spock.lang.Specification
import spock.lang.Unroll

/**
 * See the API for {@link grails.test.mixin.support.GrailsUnitTestMixin} for usage instructions
 */
@TestFor(BardWebInterfaceController)
@Unroll
class CartProjectUnitSpec extends Specification {

    void setup() {
        // Setup logic here
    }

    void tearDown() {
        // Tear down logic here
    }

    void "test shopping cart project element"() {
        when:
        CartProject cartProject = new CartProject(projectName: "my project name")
        assertNotNull(cartProject)

        then:
        assert cartProject.projectName == 'my project name'
        assertNull cartProject.shoppingItem
    }




    void "test constraints on CartProject object"() {
        setup:
        mockForConstraintsTests(CartProject)

        when:
        CartProject cartProject = new CartProject(projectName: projectName)
        cartProject.validate()

        then:
        cartProject.hasErrors() == !valid

        where:
        projectName    | valid
        ""             | false
        "Some Project" | true
    }


}
