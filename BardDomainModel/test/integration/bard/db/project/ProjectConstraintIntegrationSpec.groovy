package bard.db.project

import bard.db.BardIntegrationSpec
import bard.db.enums.ReadyForExtraction
import grails.plugin.spock.IntegrationSpec
import org.junit.Before
import spock.lang.Unroll

import static bard.db.project.Project.*
import static test.TestUtils.assertFieldValidationExpectations
import static test.TestUtils.createString

/**
 * Created with IntelliJ IDEA.
 * User: ddurkin
 * Date: 8/23/12
 * Time: 12:07 AM
 * To change this template use File | Settings | File Templates.
 */
@Unroll
class ProjectConstraintIntegrationSpec extends BardIntegrationSpec {

    Project domainInstance

    @Before
    void doSetup() {
        domainInstance = Project.buildWithoutSave()
    }

    void "test projectName constraints #desc projectName: '#valueUnderTest'"() {
        final String field = 'name'

        when: 'a value is set for the field under test'
        domainInstance[(field)] = valueUnderTest
        domainInstance.validate()

        then: 'verify valid or invalid for expected reason'
        assertFieldValidationExpectations(domainInstance, field, valid, errorCode)

        and: 'verify the domain can be persisted to the db'
        if (valid) {
            domainInstance == domainInstance.save(flush: true)
        }

        where:
        desc               | valueUnderTest                          | valid | errorCode
        'null '            | null                                    | false | 'nullable'
        'blank value'      | ''                                      | false | 'blank'
        'blank value'      | '  '                                    | false | 'blank'
        'too long'         | createString(PROJECT_NAME_MAX_SIZE + 1) | false | 'maxSize.exceeded'
        'exactly at limit' | createString(PROJECT_NAME_MAX_SIZE)     | true  | null
    }

    void "test groupType constraints #desc groupType: '#valueUnderTest'"() {
        final String field = 'groupType'

        when: 'a value is set for the field under test'
        domainInstance[(field)] = valueUnderTest
        domainInstance.validate()

        then: 'verify valid or invalid for expected reason'
        assertFieldValidationExpectations(domainInstance, field, valid, errorCode)

        and: 'verify the domain can be persisted to the db'
        if (valid) {
            domainInstance == domainInstance.save(flush: true)
        }

        where:
        desc          | valueUnderTest | valid | errorCode
        'null '       | null           | false | 'nullable'
        'blank value' | ''             | false | 'blank'
        'blank value' | '  '           | false | 'blank'
        'not in list' | 'Foo'          | false | 'not.inList'

        'valid value' | 'Project'      | true  | null
        'valid value' | 'Probe Report' | true  | null
        'valid value' | 'Campaign'     | true  | null
        'valid value' | 'Panel'        | true  | null
        'valid value' | 'Study'        | true  | null
        'valid value' | 'Template'     | true  | null
    }

    void "test description constraints #desc description: '#valueUnderTest'"() {
        final String field = 'description'

        when: 'a value is set for the field under test'
        domainInstance[(field)] = valueUnderTest
        domainInstance.validate()

        then: 'verify valid or invalid for expected reason'
        assertFieldValidationExpectations(domainInstance, field, valid, errorCode)

        and: 'verify the domain can be persisted to the db'
        if (valid) {
            domainInstance == domainInstance.save(flush: true)
        }

        where:
        desc               | valueUnderTest                         | valid | errorCode
        'blank value'      | ''                                     | false | 'blank'
        'blank value'      | '  '                                   | false | 'blank'
        'too long'         | createString(DESCRIPTION_MAX_SIZE + 1) | false | 'maxSize.exceeded'

        'exactly at limit' | createString(DESCRIPTION_MAX_SIZE)     | true  | null
        'null valid'       | null                                   | true  | null
    }

    void "test readyForExtraction constraints #desc readyForExtraction: '#valueUnderTest'"() {
        final String field = 'readyForExtraction'

        when: 'a value is set for the field under test'
        domainInstance[(field)] = valueUnderTest
        domainInstance.validate()

        then: 'verify valid or invalid for expected reason'
        assertFieldValidationExpectations(domainInstance, field, valid, errorCode)

        and: 'verify the domain can be persisted to the db'
        if (valid) {
            domainInstance == domainInstance.save(flush: true)
        }

        where:
        desc             | valueUnderTest               | valid | errorCode
        'null not valid' | null                         | false | 'nullable'

        'valid valud'    | ReadyForExtraction.NOT_READY | true  | null
        'valid value'    | ReadyForExtraction.READY     | true  | null
        'valid value'    | ReadyForExtraction.STARTED   | true  | null
        'valid value'    | ReadyForExtraction.COMPLETE  | true  | null
    }

    void "test modifiedBy constraints #desc modifiedBy: '#valueUnderTest'"() {

        final String field = 'modifiedBy'

        when: 'a value is set for the field under test'
        domainInstance[(field)] = valueUnderTest
        domainInstance.validate()

        then: 'verify valid or invalid for expected reason'
        assertFieldValidationExpectations(domainInstance, field, valid, errorCode)

        and: 'verify the domain can be persisted to the db'
        if (valid) {
            domainInstance == domainInstance.save(flush: true)
        }

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

        when: 'a value is set for the field under test'
        domainInstance[(field)] = valueUnderTest
        domainInstance.validate()

        then: 'verify valid or invalid for expected reason'
        assertFieldValidationExpectations(domainInstance, field, valid, errorCode)

        and: 'verify the domain can be persisted to the db'
        if (valid) {
            domainInstance == domainInstance.save(flush: true)
        }

        where:
        desc             | valueUnderTest | valid | errorCode
        'null not valid' | null           | false | 'nullable'
        'date valid'     | new Date()     | true  | null
    }

    void "test lastUpdated constraints #desc lastUpdated: '#valueUnderTest'"() {
        final String field = 'lastUpdated'

        when: 'a value is set for the field under test'
        domainInstance[(field)] = valueUnderTest
        domainInstance.validate()

        then: 'verify valid or invalid for expected reason'
        assertFieldValidationExpectations(domainInstance, field, valid, errorCode)

        and: 'verify the domain can be persisted to the db'
        if (valid) {
            domainInstance == domainInstance.save(flush: true)
        }

        where:
        desc             | valueUnderTest | valid | errorCode
        'null not valid' | null           | false | 'nullable'
        'date valid'     | new Date()     | true  | null
    }
}