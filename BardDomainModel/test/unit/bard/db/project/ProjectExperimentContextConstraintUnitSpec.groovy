package bard.db.project

import bard.db.model.AbstractContextConstraintUnitSpec
import grails.buildtestdata.mixin.Build
import grails.test.mixin.Mock
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
@Build([ProjectExperiment, ProjectExperimentContext])
@Mock([ProjectExperiment, ProjectExperimentContext])
@Unroll
class ProjectExperimentContextConstraintUnitSpec extends AbstractContextConstraintUnitSpec {

    @Before
    @Override
    void doSetup() {
        domainInstance = ProjectExperimentContext.buildWithoutSave()
    }

    void "test projectExperiment constraints #desc projectExperiment: '#valueUnderTest'"() {
        final String field = 'projectExperiment'

        when: 'a value is set for the field under test'
        domainInstance[(field)] = valueUnderTest.call()
        domainInstance.validate()

        then: 'verify valid or invalid for expected reason'
        assertFieldValidationExpectations(domainInstance, field, valid, errorCode)

        where:
        desc             | valueUnderTest                | valid | errorCode
        'null not valid' | { null }                      | false | 'nullable'
        'valid step'     | { ProjectExperiment.build() } | true  | null
    }

}