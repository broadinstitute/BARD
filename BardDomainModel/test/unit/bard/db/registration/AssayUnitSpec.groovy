package bard.db.registration



import grails.test.mixin.*
import org.junit.*
import spock.lang.Specification
import spock.lang.Unroll
import bard.db.dictionary.ElementHierarchy
import bard.db.dictionary.TestUtils
import static bard.db.dictionary.TestUtils.assertFieldValidationExpectations
import grails.buildtestdata.mixin.Build

/**
 * See the API for {@link grails.test.mixin.domain.DomainClassUnitTestMixin} for usage instructions
 */
@Build(Assay)
@Unroll
class AssayUnitSpec extends Specification {
    Assay domainInstance

    void setup() {
        domainInstance = Assay.buildWithoutSave(assayVersion: '2')
    }


    void "test assayStatus constraints #desc assayStatus: '#valueUnderTest'"() {
        final String field = 'assayStatus'

        when:
        domainInstance[(field)] = valueUnderTest
        domainInstance.validate()

        then:
        assertFieldValidationExpectations(domainInstance, field, valid, errorCode)

        where:
        desc               | valueUnderTest | valid | errorCode
        'null not valid'   | null           | false | 'nullable'
        'blank not valid'  | ''             | false | 'blank'
        'blank not valid'  | '   '          | false | 'blank'
        'value not inList' | 'Foo'          | false | 'not.inList'
        'valid value'      | 'Pending'      | true  | null
        'valid value'      | 'Active'       | true  | null
        'valid value'      | 'Superseded'   | true  | null
        'valid value'      | 'Retired'      | true  | null
    }

    void "test readyForExtraction constraints #desc readyForExtraction: '#valueUnderTest'"() {

        final String field = 'readyForExtraction'

        when:
        domainInstance[(field)] = valueUnderTest
        domainInstance.validate()

        then:
        assertFieldValidationExpectations(domainInstance, field, valid, errorCode)

        where:
        desc               | valueUnderTest | valid | errorCode
        'null not valid'   | null           | false | 'nullable'
        'blank not valid'  | ''             | false | 'blank'
        'blank not valid'  | '   '          | false | 'blank'
        'value not inList' | 'Foo'          | false | 'not.inList'
        'valid value'      | 'Ready'        | true  | null
        'valid value'      | 'Started'      | true  | null
        'valid value'      | 'Complete'     | true  | null
    }
}
