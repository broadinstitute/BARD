package bard.db.registration

import grails.plugin.spock.IntegrationSpec
import org.junit.After
import org.junit.Before
import spock.lang.Unroll

import static bard.db.registration.ExternalSystem.*
import static test.TestUtils.assertFieldValidationExpectations
import static test.TestUtils.createString

/**
 * Created with IntelliJ IDEA.
 * User: ddurkin
 * Date: 11/13/12
 * Time: 2:51 PM
 * To change this template use File | Settings | File Templates.
 */
@Unroll
class ExternalSystemConstraintIntegrationSpec extends IntegrationSpec {

    def domainInstance

    @Before
    void doSetup() {
        domainInstance = ExternalSystem.buildWithoutSave()
    }

    @After
    void doAfter() {
        if (domainInstance.validate()) {
            domainInstance.save(flush: true)
        }
    }

    void "test systemName constraints #desc systemName: "() {

        final String field = 'systemName'

        when:
        domainInstance[(field)] = valueUnderTest
        domainInstance.validate()

        then:
        assertFieldValidationExpectations(domainInstance, field, valid, errorCode)

        where:
        desc               | valueUnderTest                         | valid | errorCode
        'null not valid'   | null                                   | false | 'nullable'
        'blank not valid'  | ''                                     | false | 'blank'
        'blank not valid'  | '   '                                  | false | 'blank'

        'too long'         | createString(SYSTEM_NAME_MAX_SIZE + 1) | false | 'maxSize.exceeded'
        'exactly at limit' | createString(SYSTEM_NAME_MAX_SIZE)     | true  | null
    }

    void "test owner constraints #desc owner: "() {

        final String field = 'owner'

        when:
        domainInstance[(field)] = valueUnderTest
        domainInstance.validate()

        then:
        assertFieldValidationExpectations(domainInstance, field, valid, errorCode)

        where:
        desc               | valueUnderTest                   | valid | errorCode
        'blank not valid'  | ''                               | false | 'blank'
        'blank not valid'  | '   '                            | false | 'blank'
        'too long'         | createString(OWNER_MAX_SIZE + 1) | false | 'maxSize.exceeded'

        'null valid'       | null                             | true  | null
        'exactly at limit' | createString(OWNER_MAX_SIZE)     | true  | null
    }

    void "test systemUrl constraints #desc systemUrl: "() {

        final String field = 'systemUrl'

        when:
        domainInstance[(field)] = valueUnderTest
        domainInstance.validate()

        then:
        assertFieldValidationExpectations(domainInstance, field, valid, errorCode)

        where:
        desc               | valueUnderTest                        | valid | errorCode
        'blank not valid'  | ''                                    | false | 'blank'
        'blank not valid'  | '   '                                 | false | 'blank'
        'too long'         | createString(SYSTEM_URL_MAX_SIZE + 1) | false | 'maxSize.exceeded'

        'null valid'       | null                                  | true  | null
        'exactly at limit' | createString(SYSTEM_URL_MAX_SIZE)     | true  | null
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
