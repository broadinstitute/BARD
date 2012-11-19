package bard.core

import bard.core.interfaces.ExperimentCategory
import bard.core.interfaces.ExperimentRole
import bard.core.interfaces.ExperimentType
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class ExperimentUnitSpec extends Specification {

    void setup() {

    }

    void tearDown() {
        // Tear down logic here
    }

    void "test Constructors #label"() {
        when:
        Experiment currentExperiment = experiment
        then:
        currentExperiment.getName() == expectedName
        where:
        label                | experiment             | expectedName
        "No arg constructor" | new Experiment()       | null
        "1 arg constructor"  | new Experiment("name") | "name"
    }

    void "test setters #label"() {
        given:
        Experiment currentExperiment = new Experiment("name")
        when:
        currentExperiment.setAssay(new Assay("name"))
        currentExperiment.setCategory(ExperimentCategory.MLPCN)
        currentExperiment.setPubchemAid(1234)
        currentExperiment.setType(ExperimentType.Confirmatory)
        currentExperiment.setRole(ExperimentRole.Counterscreen)
        then:
        currentExperiment.getName() == "name"
        currentExperiment.getAssay().getName() == "name"
        currentExperiment.getCategory() == ExperimentCategory.MLPCN
        currentExperiment.getPubchemAid() == 1234
        currentExperiment.getType() == ExperimentType.Confirmatory
        currentExperiment.getRole() == ExperimentRole.Counterscreen
    }

    void "test addProject #label"() {
        given:
        Experiment currentExperiment = new Experiment("name")
        Project project = new Project("name")
        when:
        currentExperiment.addProject(project)
        then:
        currentExperiment.getName() == "name"
        currentExperiment.getProjectCount() == 1
        assert !currentExperiment.getProjects().isEmpty()
    }

}

