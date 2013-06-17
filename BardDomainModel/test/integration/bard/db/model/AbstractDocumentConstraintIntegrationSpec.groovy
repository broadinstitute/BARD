package bard.db.model

import bard.db.BardIntegrationSpec
import bard.db.enums.DocumentType
import org.junit.Before
import spock.lang.Unroll

import static bard.db.model.AbstractDocument.DOCUMENT_NAME_MAX_SIZE
import static bard.db.model.AbstractDocument.MODIFIED_BY_MAX_SIZE
import static test.TestUtils.assertFieldValidationExpectations
import static test.TestUtils.createString

/**
 * Created with IntelliJ IDEA.
 * User: ddurkin
 * Date: 11/8/12
 * Time: 5:04 PM
 * To change this template use File | Settings | File Templates.
 */
@Unroll
abstract class AbstractDocumentConstraintIntegrationSpec extends BardIntegrationSpec {

    def domainInstance

    @Before
    abstract void doSetup()

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
        desc          | valueUnderTest                          | valid | errorCode
        'valid value' | DocumentType.DOCUMENT_TYPE_DESCRIPTION  | true  | null
        'valid value' | DocumentType.DOCUMENT_TYPE_PROTOCOL     | true  | null
        'valid value' | DocumentType.DOCUMENT_TYPE_COMMENTS     | true  | null
        'valid value' | DocumentType.DOCUMENT_TYPE_PUBLICATION  | true  | null
        'valid value' | DocumentType.DOCUMENT_TYPE_EXTERNAL_URL | true  | null
        'valid value' | DocumentType.DOCUMENT_TYPE_OTHER        | true  | null
    }

    void "test documentContent constraints #desc documentContent: "() {

        final String field = 'documentContent'

        when:
        domainInstance.documentType = documentType
        domainInstance[(field)] = valueUnderTest
        domainInstance.validate()

        then:
        assertFieldValidationExpectations(domainInstance, field, valid, errorCode)

        where:
        desc                                      | documentType                            | valueUnderTest         | valid | errorCode
        'blank not valid'                         | DocumentType.DOCUMENT_TYPE_DESCRIPTION  | ''                     | false | 'blank'
        'blank not valid'                         | DocumentType.DOCUMENT_TYPE_DESCRIPTION  | '   '                  | false | 'blank'
        'url expected for publication not valid'  | DocumentType.DOCUMENT_TYPE_PUBLICATION  | 'foo'                  | false | 'document.invalid.url.message'
        'url expected for external url not valid' | DocumentType.DOCUMENT_TYPE_EXTERNAL_URL | 'foo'                  | false | 'document.invalid.url.message'

        'url expected for publication valid'      | DocumentType.DOCUMENT_TYPE_PUBLICATION  | 'http://foo.bar'       | true  | null
        'url expected for external url valid'     | DocumentType.DOCUMENT_TYPE_EXTERNAL_URL | 'http://foo.bar'       | true  | null
        'null valid'                              | DocumentType.DOCUMENT_TYPE_DESCRIPTION  | null                   | true  | null
        'greater than varchar limit'              | DocumentType.DOCUMENT_TYPE_DESCRIPTION  | createString(4000 + 1) | true  | null

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
        desc             | valueUnderTest | valid | errorCode
        'null not valid' | null           | false | 'nullable'
        'date valid'     | new Date()     | true  | null
    }
}
