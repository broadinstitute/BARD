package bard.db.registration

import org.junit.Before
import spock.lang.Unroll

import static test.TestUtils.assertFieldValidationExpectations

/**
 * Created with IntelliJ IDEA.
 * User: ddurkin
 * Date: 8/17/12
 * Time: 2:38 PM
 * To change this template use File | Settings | File Templates.
 */
@Unroll
class AssayContextConstraintIntegrationSpec extends AbstractContextConstraintIntegrationSpec {


    @Before
    void doSetup() {
        domainInstance = AssayContext.buildWithoutSave()
    }

    void "test assay constraints #desc assay: '#valueUnderTest'"() {
        final String field = 'assay'

        when: 'a value is set for the field under test'
        domainInstance[(field)] = valueUnderTest.call()
        domainInstance.validate()

        then: 'verify valid or invalid for expected reason'
        assertFieldValidationExpectations(domainInstance, field, valid, errorCode)

        and: 'verify the domainspreadsheetmapping can be persisted to the db'
        if (valid) {
            domainInstance == domainInstance.save(flush: true)
        }

        /* the where clause seems to be processed before build-test-data plugin adds
         * build methods to the domains so, defering running it by nesting in a closure
         */
        where:
        desc             | valueUnderTest  | valid | errorCode
        'null not valid' | {null}          | false | 'nullable'
        'valid assay'    | {Assay.build()} | true  | null

    }

}
