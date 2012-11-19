package bard.core

import bard.core.interfaces.AssayCategory
import bard.core.interfaces.AssayRole
import bard.core.interfaces.AssayType
import bard.core.interfaces.EntityNamedSources
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class AssayUnitSpec extends Specification {

    void setup() {

    }

    void tearDown() {
        // Tear down logic here
    }

    void "test Constructors #label"() {
        when:
        Assay currentAssay = assay
        then:
        currentAssay.getName() == expectedName
        where:
        label                | assay             | expectedName
        "No arg constructor" | new Assay()       | null
        "1 arg constructor"  | new Assay("name") | "name"
    }

    void "test setters #label"() {
        given:
        Assay currentAssay = new Assay("name")
        when:
        currentAssay.setCapAssayId(200L)
        currentAssay.setCategory(AssayCategory.MLPCN)
        currentAssay.setProtocol("protocol")
        currentAssay.setComments("comments")
        currentAssay.setType(AssayType.Confirmatory)
        currentAssay.setRole(AssayRole.Counterscreen)
        then:
        currentAssay.getName() == "name"
        currentAssay.getCapAssayId() == 200L
        currentAssay.getProtocol() == "protocol"
        currentAssay.getComments() == "comments"
        currentAssay.getCategory() == AssayCategory.MLPCN
        currentAssay.getType() == AssayType.Confirmatory
        currentAssay.getRole() == AssayRole.Counterscreen
    }

    void "test getters and adders #label"() {
        given:
        final Assay currentAssay = new Assay("name")
        when:
        currentAssay.addExperiment(new Experiment())
        currentAssay.addProject(new Project())
        currentAssay.addPublication(new Publication())
        currentAssay.addTarget(new Biology())
        currentAssay.addLink(new Link(new Compound(), new Assay()))
        currentAssay.addValue(new Value())
        then:
        currentAssay.getTargetCount() == 1
        currentAssay.getExperimentCount() == 1
        currentAssay.getProjectCount() == 1
        currentAssay.getLinkCount() == 1
        currentAssay.getPublicationCount() == 1
        currentAssay.getValueCount() == 1
        assert !currentAssay.getTargets().isEmpty()
        assert !currentAssay.getExperiments().isEmpty()
        assert !currentAssay.getPublications().isEmpty()
        assert !currentAssay.getProjects().isEmpty()
        assert !currentAssay.getLinks().isEmpty()
        assert !currentAssay.getValues().isEmpty()
    }

    void "findValues(List<Value>, Value,String)"() {
        given:
        DataSource dataSourceAnnotations = new DataSource(EntityNamedSources.CAPAnnotationSource, "version", "url")
        String lab = "lab"
        Assay assay = new Assay("name")
        final StringValue annotationValueParent = new StringValue(dataSourceAnnotations, "Annotation", lab)
        new StringValue(annotationValueParent, "laboratory name", lab)

        List<Value> lv = []
        when:
        assay.findValues(lv, annotationValueParent, "name")
        then:
        assert lv.isEmpty()
        assert assay.getValues().isEmpty()
    }

    void "findValues(List<Value>, Value,DataSource)"() {
        given:
        DataSource dataSource = new DataSource("name", "version", "url")
        DataSource dataSourceAnnotations = new DataSource(EntityNamedSources.CAPAnnotationSource, "version", "url")
        String lab = "lab"
        Assay assay = new Assay("name")
        final StringValue annotationValueParent = new StringValue(dataSourceAnnotations, "Annotation", lab)
        new StringValue(annotationValueParent, "laboratory name", lab)

        List<Value> lv = []
        when:
        assay.findValues(lv, annotationValueParent, dataSource)
        then:
        assert lv.isEmpty()
        assert assay.getValues().isEmpty()
    }
}

