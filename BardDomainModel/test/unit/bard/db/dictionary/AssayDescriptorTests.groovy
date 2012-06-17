package bard.db.dictionary

import grails.test.GrailsUnitTestCase
import grails.test.mixin.support.GrailsUnitTestMixin
import grails.test.mixin.TestMixin

/**
 * See the API for {@link grails.test.mixin.domain.DomainClassUnitTestMixin} for usage instructions
 */
@TestMixin(GrailsUnitTestMixin)
class AssayDescriptorTests extends GrailsUnitTestCase {

    AssayDescriptor assayDescriptor

    protected void setUp() {
        super.setUp()
        assayDescriptor = new AssayDescriptor(parent: null,
                element: new Element(),
                label: 'label',
                description: null,
                abbreviation: null,
                acronym: null,
                synonyms: null,
                externalURL: null,
                unit: null,
                elementStatus: "Pending")

    }

    protected void tearDown() {
        super.tearDown()
    }

    void testValidConstraints() {
        mockForConstraintsTests( AssayDescriptor )

        // example of valid object
        assertTrue "Valid AssayDescriptor", assayDescriptor.validate()
    }

    void testInvalidConstraints1() {
        mockForConstraintsTests( AssayDescriptor )

        assayDescriptor.elementStatus = null
        assertFalse "Missing elementStatus should make AssayDescriptor invalid", assayDescriptor.validate()
        assertEquals "nullable", assayDescriptor.errors.elementStatus
    }


    void testInvalidConstraints2() {
        mockForConstraintsTests( AssayDescriptor )

        assayDescriptor.element = null
        assertFalse "Missing element should make AssayDescriptor invalid", assayDescriptor.validate()
        assertEquals "nullable", assayDescriptor.errors.element
    }


    void testSomething() {
        assertTrue(true)
    }
}
