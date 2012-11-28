package bard.db.dictionary

import grails.plugin.spock.IntegrationSpec
import org.junit.Before
import spock.lang.Unroll

import static bard.db.dictionary.UnitTree.FULL_PATH_MAX_SIZE
import static test.TestUtils.assertFieldValidationExpectations
import static test.TestUtils.createString

/**
 * Created with IntelliJ IDEA.
 * User: ddurkin
 * Date: 11/27/12
 * Time: 2:57 PM
 * To change this template use File | Settings | File Templates.
 */
@Unroll
class LaboratoryTreeConstraintIntegrationSpec extends IntegrationSpec {
    LaboratoryTree domainInstance

    @Before
    void doSetup() {
        domainInstance = LaboratoryTree.buildWithoutSave()
        domainInstance.element.save()
    }

    void "test parent constraints #desc parent: '#valueUnderTest'"() {
        final String field = 'parent'

        when: 'a value is set for the field under test'
        domainInstance[(field)] = valueUnderTest.call()
        domainInstance.save()

        then: 'verify valid or invalid for expected reason'
        assertFieldValidationExpectations(domainInstance, field, valid, errorCode)

        and: 'verify the domain can be persisted to the db'
        if (valid) {
            domainInstance == domainInstance.save(flush: true)
        }

        where:
        desc          | valueUnderTest     | valid | errorCode
        'null valid'  | {null}             | true  | null
        'valid value' | {LaboratoryTree.build()} | true  | null
    }

    void "test element constraints #desc element: '#valueUnderTest'"() {
        final String field = 'element'

        when: 'a value is set for the field under test'
        domainInstance[(field)] = valueUnderTest.call()
        domainInstance.save()

        then: 'verify valid or invalid for expected reason'
        assertFieldValidationExpectations(domainInstance, field, valid, errorCode)

        and: 'verify the domain can be persisted to the db'
        if (valid) {
            domainInstance == domainInstance.save(flush: true)
        }

        where:
        desc          | valueUnderTest    | valid | errorCode
        'null value'  | {null}            | false | 'nullable'
        'valid value' | {Element.build()} | true  | null
    }

}