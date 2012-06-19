package bard.db.util

import grails.test.mixin.TestFor
import org.junit.Before

/**
 * See the API for {@link grails.test.mixin.domain.DomainClassUnitTestMixin} for usage instructions
 */
@TestFor(Qualifier)
class QualifierTests {

    Qualifier qualifier

    @Before
    public void setUp() {
        qualifier = new Qualifier()
        qualifier.setQualifier('>')
        qualifier.setDescription("greater than")
    }

    void testValidConstraints() {

        mockForConstraintsTests(Qualifier)

        assertTrue "Qualifier is valid", qualifier.validate()
    }

    void testNoQualifierSet() {

        mockForConstraintsTests(Qualifier)

        qualifier.setQualifier(null)

        assertFalse "Missing qualifier id should make Qualifier not valid", qualifier.validate()
        assertEquals "nullable", qualifier.errors["qualifier"]

    }
}
