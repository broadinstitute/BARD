package querycart

import grails.test.mixin.TestFor
import org.apache.commons.lang.RandomStringUtils
import spock.lang.Specification
import spock.lang.Unroll
import org.apache.commons.lang.StringUtils

/**
 * See the API for {@link grails.test.mixin.support.GrailsUnitTestMixin} for usage instructions
 */
@TestFor(CartProject)
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
        assert cartProject.name == 'Project title'
        assert cartProject.externalId == projectId
    }

    void "test toString #label"() {
        given:
        CartProject cartProject = new CartProject(projectName, 5)

        when:
        String projectAsString = cartProject.toString()

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
        int code1 = cartProject1.hashCode()
        int code2 = cartProject2.hashCode()

        then:
        assert (code1 == code2) == expectation

        where:
        label                   | cartProject1                      | cartProject2                      | expectation
        "Empty classes"         | new CartProject()                 | new CartProject()                 | true
        "Diff ids, diff names"  | new CartProject("Test 1", 5)      | new CartProject("Test 2", 3)      | false
        "Same ids, diff names"  | new CartProject("Test 1", 3)      | new CartProject("Test 2", 3)      | true
        "Diff ids, same names"  | new CartProject("Some Title", 22) | new CartProject("Some Title", 25) | false
        "Same ids, same names"  | new CartProject("Some Title", 22) | new CartProject("Some Title", 22) | true

    }

    void "test adding ellipses when the project name is too long"() {
        given:
        mockForConstraintsTests(CartProject)
        final String name = RandomStringUtils.randomAlphabetic(stringLength)
        String truncatedName = StringUtils.abbreviate(name, CartCompound.MAXIMUM_NAME_FIELD_LENGTH)

        CartProject cartProject = new CartProject(name, projectId)

        when:
        cartProject.validate()

        then:
        assert cartProject.name == truncatedName

        where:
        projectId | stringLength
        47        | 4001
        47        | 80000
        47        | 25
        2         | 0
    }

    void "test shopping cart project element"() {
        when:
        CartProject cartProject = new CartProject("my project name", 20)
        assertNotNull(cartProject)

        then:
        assert cartProject.name == 'my project name'
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
