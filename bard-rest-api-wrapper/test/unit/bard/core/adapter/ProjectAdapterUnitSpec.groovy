package bard.core.adapter

import bard.core.rest.spring.project.Project
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class ProjectAdapterUnitSpec extends Specification {
    void setup() {

    }

    void tearDown() {
        // Tear down logic here
    }

    void "test Constructor()"() {

        given:
        Project project = new Project()
        project.name = "name"
        when:
        ProjectAdapter projectAdapter = new ProjectAdapter(project)
        then:
        assert projectAdapter.name == "name"
        assert !projectAdapter.getGrantNumber()
        assert !projectAdapter.getLaboratoryName()
    }

    void "test getters()"() {

        given:

        String grantNo = "GR001"
        String lab = "lab"
        Project project = new Project()
        project.name = "name"

        when:
        ProjectAdapter projectAdapter = new ProjectAdapter(project)
        then:
        assert projectAdapter.name == "name"
        assert projectAdapter.getNumberOfExperiments() == 0
        assert projectAdapter.getAnnotations().isEmpty()
    }

}

