package bard.db.experiment

import bard.db.registration.AbstractContextConstraintUnitSpec
import grails.buildtestdata.mixin.Build
import org.junit.Before
import spock.lang.Shared
import spock.lang.Unroll

import static test.TestUtils.assertFieldValidationExpectations

/**
 * Created with IntelliJ IDEA.
 * User: ddurkin
 * Date: 8/17/12
 * Time: 1:25 PM
 * To change this template use File | Settings | File Templates.
 */
@Build([Project,ProjectContext])
@Unroll
class ProjectContextConstraintUnitSpec extends AbstractContextConstraintUnitSpec {

    @Shared Project validProject

    @Before
    void doSetup() {
        domainInstance = ProjectContext.buildWithoutSave()
        validProject = Project.build()
    }

    void "test project constraints #desc project: '#valueUnderTest'"() {
        final String field = 'project'

        when: 'a value is set for the field under test'
        domainInstance[(field)] = valueUnderTest
        println("field : $field")
        println("valueUnderTest : $valueUnderTest")
        println("validProject : $validProject")
        domainInstance.validate()

        then: 'verify valid or invalid for expected reason'
        assertFieldValidationExpectations(domainInstance, field, valid, errorCode)

        and: 'verify the domainspreadsheetmapping can be persisted to the db'
        if (valid) {
            domainInstance == domainInstance.save(flush: true)
        }

        where:
        desc               | valueUnderTest  | valid | errorCode
        'null not valid'   | null            | false | 'nullable'
        'valid project' | validProject | true  | null
    }

}