package bard.db.experiment

import bard.db.dictionary.TestUtils
import grails.test.mixin.TestFor
import spock.lang.Specification
import spock.lang.Unroll

/**
 * See the API for {@link grails.test.mixin.domain.DomainClassUnitTestMixin} for usage instructions
 */
@TestFor(ProjectExperiment)
@Unroll
class ProjectExperimentUnitSpec extends Specification {

    private ProjectExperiment createValidProjectExperiment() {
        new ProjectExperiment(project: new Project(), experiment: new Experiment())
    }


    void "test project not null project: '#project'"() {
        given:
        mockForConstraintsTests(ProjectExperiment)

        when:
        ProjectExperiment projectExperiment = createValidProjectExperiment()
        projectExperiment.project = project
        projectExperiment.validate()

        then:
        TestUtils.assertFieldValidationExpectations(projectExperiment, 'project', valid, errorCode)

        where:
        project       | valid | errorCode
        null          | false | 'nullable'
        new Project() | true  | null
    }

    void "test experiment not null experiment: '#experiment'"() {
        given:
        mockForConstraintsTests(ProjectExperiment)

        when:
        ProjectExperiment projectExperiment = createValidProjectExperiment()
        projectExperiment.experiment = experiment
        projectExperiment.validate()

        then:
        TestUtils.assertFieldValidationExpectations(projectExperiment, 'experiment', valid, errorCode)

        where:
        experiment       | valid | errorCode
        null             | false | 'nullable'
        new Experiment() | true  | null
    }
}
