package bard.db.dictionary


import grails.test.GrailsUnitTestCase
import grails.test.mixin.support.GrailsUnitTestMixin
import grails.test.mixin.TestMixin

/**
 * Created with IntelliJ IDEA.
 * User: balexand
 * Date: 6/16/12
 * Time: 2:44 PM
 * To change this template use File | Settings | File Templates.
 */
@TestMixin(GrailsUnitTestMixin)
class BiologyDescriptorTests extends GrailsUnitTestCase {

    BiologyDescriptor biologyDescriptor

    protected void setUp() {
        super.setUp()
        biologyDescriptor = new BiologyDescriptor(parent: null,
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
        mockForConstraintsTests( BiologyDescriptor )

        assertTrue "Valid AssayDescriptor", biologyDescriptor.validate()
    }

    void testInvalidConstraints1() {
        mockForConstraintsTests( BiologyDescriptor )

        biologyDescriptor.elementStatus = null
        assertFalse "Missing elementStatus should make biologyDescriptor invalid", biologyDescriptor.validate()
        assertEquals "nullable", biologyDescriptor.errors.elementStatus
    }
    void testInvalidConstraints3() {
        mockForConstraintsTests( BiologyDescriptor )

        biologyDescriptor.abbreviation = "ddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddd"
        assertFalse "Missing elementStatus should make biologyDescriptor invalid", biologyDescriptor.validate()
        assertEquals "maxSize", biologyDescriptor.errors.abbreviation
    }

    void testInvalidConstraints2() {
        mockForConstraintsTests( BiologyDescriptor )

        biologyDescriptor.element = null
        assertFalse "Missing element should make biologyDescriptor invalid", biologyDescriptor.validate()
        assertEquals "nullable", biologyDescriptor.errors.element
    }

}
