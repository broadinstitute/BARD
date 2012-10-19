package querycart

import bardqueryapi.BardWebInterfaceController
import grails.test.mixin.TestFor
import org.apache.commons.lang.RandomStringUtils
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

    void "test constructor with integer"() {
        given:
        int projectId = 2
        final String projectName = "Project title"
        when:
        CartProject cartProject = new CartProject(projectName, projectId)

        then:
        assert cartProject.projectName == 'Project title'
        assert cartProject.projectId
    }

    void "test toString #label"() {
        given:
        CartProject cartProject = new CartProject()
        cartProject.setProjectName(projectName)
        when:
        final String projectAsString = cartProject.toString()
        then:
        assert projectAsString == expectedTitle
        where:
        label                         | projectName  | expectedTitle
        "Empty Project Name"          | ""           | ""
        "Null String as Project Name" | ""           | ""
        "With Project Name"           | "Some Title" | "Some Title"
        "Null Project Name"           | null         | ""

    }

    void "Test equals #label"() {
        when:
        final boolean equals = cartProject.equals(otherCartProject)

        then:
        equals == equality
        where:
        label               | cartProject                       | otherCartProject                  | equality
        "Other is null"     | new CartProject()                 | null                              | false
        "Different classes" | new CartProject()                 | 20                                | false
        "Equality"          | new CartProject("Some Title", 24) | new CartProject("Some Title", 24) | true


    }

    void "Test hashCode #label"() {
        when:
        final int code = cartProject.hashCode()

        then:
        assert code
        where:
        label               | cartProject
        "Other is null"     | new CartProject()
        "Different classes" | new CartProject()
        "Equality"          | new CartProject("Some Title", 22)


    }

    // Note: In this case we are using a setter, and therefore we must NOT mockForConstraintsTests (otherwise
    // the setter will never be hit.
    void "test adding ellipses when the project name is too long"() {
        given:
        final String projectName = RandomStringUtils.randomAlphabetic(stringLength)
        CartProject cartProject = new CartProject(projectName, projectId)

        when:
        cartProject.setProjectName(projectName)

        then:
        cartProject.toString().length() == properStringLength

        where:
        projectId | stringLength | properStringLength
        47        | 4001         | 4003
        47        | 80000        | 4003
        47        | 25           | 25
        2         | 0            | 0
    }

    void "test shopping cart project element"() {
        when:
        CartProject cartProject = new CartProject("my project name", 20)
        assertNotNull(cartProject)

        then:
        assert cartProject.projectName == 'my project name'
        assertNull cartProject.shoppingItem
    }




    void "test constraints on CartProject #label"() {
        setup:
        mockForConstraintsTests(CartProject)

        when:
        CartProject cartProject = new CartProject(projectName, 20)
        cartProject.validate()

        then:
        cartProject.hasErrors() == !valid

        where:
        label                      | projectName    | valid
        "Project Name is null"     | ""             | false
        "Project Name is not null" | "Some Project" | true
    }


}
