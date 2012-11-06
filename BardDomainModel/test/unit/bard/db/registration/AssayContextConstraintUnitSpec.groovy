package bard.db.registration

import grails.buildtestdata.mixin.Build
import org.junit.Before
import spock.lang.Unroll

import static test.TestUtils.assertFieldValidationExpectations

/**
 * Created with IntelliJ IDEA.
 * User: ddurkin
 * Date: 8/17/12
 * Time: 1:25 PM
 * To change this template use File | Settings | File Templates.
 */
@Build([Assay, AssayContext])
@Unroll
class AssayContextConstraintUnitSpec extends AbstractContextConstraintUnitSpec {


    @Before
    @Override
    void doSetup() {
        domainInstance = AssayContext.buildWithoutSave()
        validParent = Assay.build()
    }

    void "test assay constraints #desc assay: '#valueUnderTest'"() {
        final String field = 'assay'

        when: 'a value is set for the field under test'
        println("field : $field")
        println("valueUnderTest : $valueUnderTest")
        println("validExperiment : $validParent")
        domainInstance[(field)] = valueUnderTest
        domainInstance.validate()

        then: 'verify valid or invalid for expected reason'
        assertFieldValidationExpectations(domainInstance, field, valid, errorCode)

        and: 'verify the domainspreadsheetmapping can be persisted to the db'
        if (valid) {
            domainInstance == domainInstance.save(flush: true)
        }

        where:
        desc             | valueUnderTest | valid | errorCode
        'null not valid' | null           | false | 'nullable'
        'valid assay'    | validParent    | true  | null

    }

}
