package bard.db.registration

import bard.db.audit.BardContextUtils
import bard.db.model.AbstractContextItemIntegrationSpec
import org.hibernate.SessionFactory
import org.junit.Before
import spock.lang.Unroll

import static test.TestUtils.assertFieldValidationExpectations

/**
 * Created with IntelliJ IDEA.
 * User: ddurkin
 * Date: 8/21/12
 * Time: 9:18 AM
 * To change this template use File | Settings | File Templates.
 */
@Unroll
class AssayContextItemConstraintIntegrationSpec extends AbstractContextItemIntegrationSpec {

    @Before
    void doSetup() {
        domainInstance = AssayContextItem.buildWithoutSave()
        domainInstance.attributeElement.save()
    }

    void "test attributeType constraints #desc attributeType: '#valueUnderTest'"() {

        final String field = 'attributeType'

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
        desc                   | valueUnderTest      | valid | errorCode
        'null'                 | null                | false | 'nullable'

        'AttributeType.Fixed'  | AttributeType.Fixed | true  | null
        'AttributeType.List'   | AttributeType.List  | true  | null
        'AttributeType.Range'  | AttributeType.Range | true  | null
        'AttributeType.Number' | AttributeType.Free  | true  | null

    }

}