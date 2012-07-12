package bard.db.registration


import spock.lang.Specification
import spock.lang.Unroll

import static bard.db.dictionary.TestUtils.assertFieldValidationExpectations
import static bard.db.dictionary.TestUtils.createString
import static bard.db.registration.Assay.ASSAY_NAME_MAX_SIZE
import static bard.db.registration.Assay.ASSAY_VERSION_MAX_SIZE
import static bard.db.registration.Assay.DESIGNED_BY_MAX_SIZE
import static bard.db.registration.Assay.MODIFIED_BY_MAX_SIZE
import grails.buildtestdata.mixin.Build
import org.apache.maven.artifact.ant.shaded.StringUtils

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
        // 'too long'         | createString(ASSAY_STATUS_MAX_SIZE) | false | 'maxSize.exceeded'  // can't seem to hit only getting not.inList

    }

    void "test assayName constraints #desc assayName: "() {

        final String field = 'assayName'

        when:
        domainInstance[(field)] = valueUnderTest
        domainInstance.validate()

        then:
        assertFieldValidationExpectations(domainInstance, field, valid, errorCode)

        where:
        desc               | valueUnderTest                        | valid | errorCode
        'null not valid'   | null                                  | false | 'nullable'
        'blank not valid'  | ''                                    | false | 'blank'
        'blank not valid'  | '   '                                 | false | 'blank'

        'too long'         | createString(ASSAY_NAME_MAX_SIZE + 1) | false | 'maxSize.exceeded'
        'exactly at limit' | createString(ASSAY_NAME_MAX_SIZE)     | true  | null


    }

    void "test assayVersion constraints #desc assayVersion: '#valueUnderTest'"() {

        final String field = 'assayVersion'

        when:
        domainInstance[(field)] = valueUnderTest
        domainInstance.validate()

        then:
        assertFieldValidationExpectations(domainInstance, field, valid, errorCode)

        where:
        desc                           | valueUnderTest                            | valid | errorCode
        'null not valid'               | null                                      | false | 'nullable'
        'blank not valid'              | ''                                        | false | 'blank'
        'blank not valid'              | '   '                                     | false | 'blank'

        'too long'                     | createString(ASSAY_VERSION_MAX_SIZE + 1)  | false | 'maxSize.exceeded'

        'exactly at limit, not digits' | createString(ASSAY_VERSION_MAX_SIZE)      | false | 'matches.invalid'

        'exactly at limit, digits'     | createString(ASSAY_VERSION_MAX_SIZE, '9') | true  | null
    }

    void "test designedBy constraints #desc designedBy: '#valueUnderTest'"() {

        final String field = 'designedBy'

        when:
        domainInstance[(field)] = valueUnderTest
        domainInstance.validate()

        then:
        assertFieldValidationExpectations(domainInstance, field, valid, errorCode)

        where:
        desc               | valueUnderTest                         | valid | errorCode
        'too long'         | createString(DESIGNED_BY_MAX_SIZE + 1) | false | 'maxSize.exceeded'

        'exactly at limit' | createString(DESIGNED_BY_MAX_SIZE)     | true  | null
        'null valid'       | null                                   | true  | null
        'blank valid'      | ''                                     | true  | null
        'blank valid'      | '  '                                   | true  | null
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

        // ''                 | READY_FOR_EXTRACTION_MAX_SIZE | false | 'maxSize.exceeded'
        // only see violation of inList not maxSize

    }

    void "test assayType constraints #desc assayType: '#valueUnderTest'"() {

        final String field = 'assayType'

        when:
        domainInstance[(field)] = valueUnderTest
        domainInstance.validate()

        then:
        assertFieldValidationExpectations(domainInstance, field, valid, errorCode)

        where:
        desc               | valueUnderTest  | valid | errorCode
        'null valid'       | null            | false | null

        'value not inList' | 'Foo'           | false | 'not.inList'
        'valid value'      | 'Regular'       | true  | null
        'valid value'      | 'Panel - Array' | true  | null
        'valid value'      | 'Panel - Group' | true  | null
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

