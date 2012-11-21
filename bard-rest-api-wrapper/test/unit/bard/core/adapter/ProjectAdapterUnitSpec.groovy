package bard.core.adapter

import bard.core.DataSource
import bard.core.Project
import bard.core.StringValue
import spock.lang.Specification
import spock.lang.Unroll
import bard.core.interfaces.EntityNamedSources

@Unroll
class ProjectAdapterUnitSpec extends Specification {
    void setup() {

    }

    void tearDown() {
        // Tear down logic here
    }

    void "test Constructor()"() {

        given:
        Project project = new Project("name")
        when:
        ProjectAdapter projectAdapter = new ProjectAdapter(project)
        then:
        assert projectAdapter.name == "name"
        assert !projectAdapter.getGrantNumber()
        assert !projectAdapter.getLaboratoryName()
    }

    void "test getters()"() {

        given:
        DataSource dataSource = new DataSource("name", "version", "url")
        DataSource dataSourceAnnotations = new DataSource( EntityNamedSources.CAPAnnotationSource, "version", "url")

        String grantNo = "GR001"
        String lab = "lab"
        Project project = new Project("name")
        project.addValue(new StringValue
        (dataSource, "grant number", grantNo));
        project.addValue(new StringValue
        (dataSource, "laboratory name", lab));
        project.addValue(new StringValue
        (dataSourceAnnotations, "Annotation", lab));
        when:
        ProjectAdapter projectAdapter = new ProjectAdapter(project)
        then:
        assert projectAdapter.name == "name"
        assert projectAdapter.getGrantNumber() == grantNo
        assert projectAdapter.getLaboratoryName() == lab
        assert projectAdapter.getNumberOfExperiments() == null
        assert !projectAdapter.getAnnotations().isEmpty()
    }

}

