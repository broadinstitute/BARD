package bard.db.registration

import grails.plugin.spock.IntegrationSpec
import org.junit.Before
import spock.lang.Unroll

import static bard.db.registration.AbstractDocument.*
import static test.TestUtils.assertFieldValidationExpectations
import static test.TestUtils.createString

/**
 * Created with IntelliJ IDEA.
 * User: ddurkin
 * Date: 7/10/12
 * Time: 9:47 PM
 * To change this template use File | Settings | File Templates.
 */
@Unroll
class AssayDocumentConstraintIntegrationSpec extends IntegrationSpec {

    AssayDocument domainInstance

    @Before
    void doSetup() {
        domainInstance = AssayDocument.buildWithoutSave()
    }

    void "test documentName constraints #desc documentName: "() {

        final String field = 'documentName'

        when:
        domainInstance[(field)] = valueUnderTest
        domainInstance.validate()

        then:
        assertFieldValidationExpectations(domainInstance, field, valid, errorCode)

        where:
        desc               | valueUnderTest                           | valid | errorCode
        'null not valid'   | null                                     | false | 'nullable'
        'blank not valid'  | ''                                       | false | 'blank'
        'blank not valid'  | '   '                                    | false | 'blank'

        'too long'         | createString(DOCUMENT_NAME_MAX_SIZE + 1) | false | 'maxSize.exceeded'
        'exactly at limit' | createString(DOCUMENT_NAME_MAX_SIZE)     | true  | null
    }

    void "test documentType constraints #desc documentType: '#valueUnderTest'"() {

        final String field = 'documentType'

        when:
        domainInstance[(field)] = valueUnderTest
        domainInstance.validate()

        then:
        assertFieldValidationExpectations(domainInstance, field, valid, errorCode)

        where:
        desc               | valueUnderTest                           | valid | errorCode
        'null not valid'   | null                                     | false | 'nullable'
        'blank not valid'  | ''                                       | false | 'blank'
        'blank not valid'  | '   '                                    | false | 'blank'

        'too long'         | createString(DOCUMENT_TYPE_MAX_SIZE + 1) | false | 'maxSize.exceeded'

        'value not inList' | 'Foo'                                    | false | 'not.inList'
        'valid value'      | 'Description'                            | true  | null
        'valid value'      | 'Protocol'                               | true  | null
        'valid value'      | 'Comments'                               | true  | null
        'valid value'      | 'Paper'                                  | true  | null
        'valid value'      | 'External URL'                           | true  | null
        'valid value'      | 'Other'                                  | true  | null
        //        'exactly at limit' | createString(DOCUMENT_TYPE_MAX_SIZE)     | true  | null
    }

    void "test documentContent constraints #desc documentContent: "() {

        final String field = 'documentContent'

        when:
        domainInstance[(field)] = valueUnderTest
        domainInstance.validate()

        then:
        assertFieldValidationExpectations(domainInstance, field, valid, errorCode)

        where:
        desc                         | valueUnderTest         | valid | errorCode
        'blank not valid'            | ''                     | false | 'blank'
        'blank not valid'            | '   '                  | false | 'blank'

        'null valid'                 | null                   | true  | null
        'greater than varchar limit' | createString(4000 + 1) | true  | null

    }

    void "test modifiedBy constraints #desc modifiedBy: '#valueUnderTest'"() {

        final String field = 'modifiedBy'

        when:
        domainInstance[(field)] = valueUnderTest
        domainInstance.validate()

        then:
        assertFieldValidationExpectations(domainInstance, field, valid, errorCode)

        where:
        desc               | valueUnderTest                         | valid | errorCode
        'too long'         | createString(MODIFIED_BY_MAX_SIZE + 1) | false | 'maxSize.exceeded'
        'blank valid'      | ''                                     | false | 'blank'
        'blank valid'      | '  '                                   | false | 'blank'

        'exactly at limit' | createString(MODIFIED_BY_MAX_SIZE)     | true  | null
        'null valid'       | null                                   | true  | null
    }

    void "test dateCreated constraints #desc dateCreated: '#valueUnderTest'"() {
        final String field = 'dateCreated'

        when:
        domainInstance[(field)] = valueUnderTest
        domainInstance.validate()

        then:
        assertFieldValidationExpectations(domainInstance, field, valid, errorCode)

        where:
        desc             | valueUnderTest | valid | errorCode
        'null not valid' | null           | false | 'nullable'
        'date valid'     | new Date()     | true  | null
    }

    void "test lastUpdated constraints #desc lastUpdated: '#valueUnderTest'"() {
        final String field = 'lastUpdated'

        when:
        domainInstance[(field)] = valueUnderTest
        domainInstance.validate()

        then:
        assertFieldValidationExpectations(domainInstance, field, valid, errorCode)

        where:
        desc         | valueUnderTest | valid | errorCode
        'null valid' | null           | true  | null
        'date valid' | new Date()     | true  | null
    }

}
