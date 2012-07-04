package bard.db.registration



import grails.test.mixin.*
import org.junit.*
import spock.lang.Specification
import spock.lang.Unroll
import bard.db.dictionary.ElementHierarchy
import bard.db.dictionary.TestUtils
import static bard.db.dictionary.TestUtils.assertFieldValidationExpectations

/**
 * See the API for {@link grails.test.mixin.domain.DomainClassUnitTestMixin} for usage instructions
 */
@TestFor(Assay)
@Unroll
class AssayUnitSpec extends Specification {

    Assay assay

    @Before
    public void setup() {
        mockForConstraintsTests(Assay)
        assay = new Assay(assayName: "Test", assayVersion: "2", assayStatus: "Active", readyForExtraction: "Complete")
    }

    void testValidConstraints() {

        expect:
        assertTrue "Assay is valid", assay.validate()
    }

    void testNoAssayName() {
        assay.setAssayName(null)

        expect:
        assertFalse "Assay should not be valid with no name", assay.validate()
        assertEquals "nullable", assay.errors["assayName"]

    }

    void "test assayStatus constraints #desc assayStatus: '#assayStatus'"() {

        given:
        mockForConstraintsTests(Assay)

        when:

        assay.assayStatus = assayStatus
        assay.validate()

        then:
        assertFieldValidationExpectations(assay, 'assayStatus', valid, errorCode)

        where:
        desc               | assayStatus  | valid | errorCode
        'null not valid'   | null         | false | 'nullable'
        'blank not valid'  | ''           | false | 'blank'
        'blank not valid'  | '   '        | false | 'blank'
        'value not inList' | 'Foo'        | false | 'inList'
        'valid value'      | 'Pending'    | true  | null
        'valid value'      | 'Active'     | true  | null
        'valid value'      | 'Superseded' | true  | null
        'valid value'      | 'Retired'    | true  | null
    }

    void "test readyForExtraction constraints #desc readyForExtraction: '#valueUnderTest'"() {

        given:
        mockForConstraintsTests(Assay)
        String field = 'readyForExtraction'

        when:
        assay[(field)] = valueUnderTest
        assay.validate()

        then:
        assertFieldValidationExpectations(assay, field, valid, errorCode)

        where:
        desc               | valueUnderTest | valid | errorCode
        'null not valid'   | null           | false | 'nullable'
        'blank not valid'  | ''             | false | 'blank'
        'blank not valid'  | '   '          | false | 'blank'
        'value not inList' | 'Foo'          | false | 'inList'
        'valid value'      | 'Ready'        | true  | null
        'valid value'      | 'Started'      | true  | null
        'valid value'      | 'Complete'     | true  | null
    }


}
