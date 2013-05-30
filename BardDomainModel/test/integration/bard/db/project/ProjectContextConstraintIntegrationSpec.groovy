package bard.db.project

import bard.db.model.AbstractContextConstraintIntegrationSpec
import org.junit.Before
import spock.lang.Unroll

import static test.TestUtils.assertFieldValidationExpectations
import bard.db.project.Project
import bard.db.project.ProjectContext

/**
 * Created with IntelliJ IDEA.
 * User: ddurkin
 * Date: 11/6/12
 * Time: 5:30 PM
 * To change this template use File | Settings | File Templates.
 */
@Unroll
class ProjectContextConstraintIntegrationSpec extends AbstractContextConstraintIntegrationSpec {
    @Before
    @Override
    void doSetup() {
        domainInstance = ProjectContext.buildWithoutSave()
    }

    void "test project constraints #desc project: '#valueUnderTest'"() {
        final String field = 'project'

        when: 'a value is set for the field under test'
        domainInstance[(field)] = valueUnderTest.call()
        domainInstance.validate()

        then: 'verify valid or invalid for expected reason'
        assertFieldValidationExpectations(domainInstance, field, valid, errorCode)

        and: 'verify the domain can be persisted to the db'
        if (valid) {
            domainInstance == domainInstance.save(flush: true)
        }

        where:
        desc             | valueUnderTest    | valid | errorCode
        'null not valid' | {null}            | false | 'nullable'
        'valid project'  | {Project.build()} | true  | null
    }
}
