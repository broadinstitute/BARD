package bard.db

import bard.db.dictionary.Element
import bard.db.dictionary.ElementHierarchy
import bard.db.dictionary.Ontology
import bard.db.dictionary.OntologyItem
import bard.db.experiment.*
import bard.db.project.*
import bard.db.registration.*
import grails.buildtestdata.mixin.Build
import grails.test.mixin.Mock
import spock.lang.Specification
import spock.lang.Unroll

/**
 * Created with IntelliJ IDEA.
 * User: pmontgom
 * Date: 5/15/13
 * Time: 11:17 AM
 * To change this template use File | Settings | File Templates.
 */
@Build([Element, ElementHierarchy, Project, ProjectContext, ProjectContextItem, ProjectDocument, ExternalReference, ExternalSystem, ProjectExperiment, ProjectExperimentContext, ProjectExperimentContextItem, ProjectStep,
Experiment, ElementHierarchy, Ontology, OntologyItem, Assay, AssayContext, AssayContextExperimentMeasure, AssayDocument, AssayContextItem, Experiment, ExperimentContext, ExperimentContextItem, ExperimentMeasure, ExperimentFile])
@Mock([Element, ElementHierarchy, Project, ProjectContext, ProjectContextItem, ProjectDocument, ExternalReference, ExternalSystem, ProjectExperiment, ProjectExperimentContext, ProjectExperimentContextItem, ProjectStep,
Experiment, ElementHierarchy, Ontology, OntologyItem, Assay, AssayContext, AssayContextExperimentMeasure, AssayDocument, AssayContextItem, Experiment, ExperimentContext, ExperimentContextItem, ExperimentMeasure, ExperimentFile])
@Unroll
class ReadyForExtractListenerUnitSpec extends Specification {
    def 'test assay setting ready for extract for #description'() {
        setup:
        Assay assayx = Assay.build()
        assert assayx.id != null
        Assay.get(assayx.id) != null

        when:
        def item = targetConstructor.call(assayx)
        def impacted = ReadyForExtractFlushListener.getObjectsImpactedByChange(item)

        then:
        impacted.size() == 1
        impacted[0] == assayx

        where:
        description                     | targetConstructor
        "Assay"                         | { assay -> assay }
        "AssayContext"                  | { assay -> AssayContext.build(assay: assay) }
        "AssayContextExperimentMeasure" | { assay -> AssayContextExperimentMeasure.build(assayContext: AssayContext.build(assay: assay)) }
        "AssayDocument"                 | { assay -> AssayDocument.build(assay: assay) }
    }

    def 'test element setting ready for extract for #description'() {
        setup:
        Element element = Element.build()

        when:
        def item = targetConstructor.call(element)
        def impacted = new HashSet(ReadyForExtractFlushListener.getObjectsImpactedByChange(item))

        then:
        impacted.size() == 1
        impacted.first() == element

        where:
        description        | targetConstructor
        "Element"          | { e -> e }
        "ElementHierarchy" | { e -> ElementHierarchy.build(childElement: e) }
        "OntologyItem"     | { e -> OntologyItem.build(element: e) }
        "Ontology"         | { e -> Ontology.build(ontologyItems: [OntologyItem.build(element: e)]) }
    }

    def 'test experiment setting ready for extract for #description'() {
        setup:
        Experiment experimentx = Experiment.build()

        when:
        def item = targetConstructor.call(experimentx)
        def impacted = ReadyForExtractFlushListener.getObjectsImpactedByChange(item)

        then:
        impacted.size() == 1
        impacted[0] == experimentx

        where:
        description             | targetConstructor
        "Experiment"            | { experiment -> experiment }
        "ExperimentContext"     | { experiment -> ExperimentContext.build(experiment: experiment) }
        "ExperimentContextItem" | { experiment -> ExperimentContextItem.build(context: ExperimentContext.build(experiment: experiment)) }
        "ExperimentMeasure"     | { experiment -> ExperimentMeasure.build(experiment: experiment) }
        "ExperimentFile"        | { experiment -> ExperimentFile.build(experiment: experiment) }
    }

    def 'test project setting ready for extract for #description'() {
        setup:
        Project projectx = Project.build()

        when:
        def item = targetConstructor.call(projectx)
        def impacted = new HashSet(ReadyForExtractFlushListener.getObjectsImpactedByChange(item))

        then:
        impacted.size() == 1
        impacted.first() == projectx

        where:
        description                    | targetConstructor
        "Project"                      | { project -> project }
        "ProjectContext"               | { project -> ProjectContext.build(project: project) }
        "ProjectContextItem"           | { project -> ProjectContextItem.build(context: ProjectContext.build(project: project)) }
        "ProjectDocument"              | { project -> ProjectDocument.build(project: project) }
        "ExternalReference"            | { project -> ExternalReference.build(project: project) }
        "ExternalSystem"               | { project -> ExternalSystem.build(externalReferences: [ExternalReference.build(project: project)]) }
        "ProjectExperiment"            | { project -> ProjectExperiment.build(project: project) }
        "ProjectExperimentContext"     | { project -> ProjectExperimentContext.build(projectExperiment: ProjectExperiment.build(project: project)) }
        "ProjectExperimentContextItem" | { project -> ProjectExperimentContextItem.build(context: ProjectExperimentContext.build(projectExperiment: ProjectExperiment.build(project: project))) }
        "ProjectStep next"             | { project -> buildStep(ProjectExperiment.build(project: project), ProjectExperiment.build(project: project)) }
        "ProjectStep prev"             | { project -> buildStep(ProjectExperiment.build(project: project), ProjectExperiment.build(project: project)) }
    }

    ProjectStep buildStep(ProjectExperiment next, ProjectExperiment prev) {
        ProjectStep step = ProjectStep.build()
        step.nextProjectExperiment = next
        step.previousProjectExperiment = prev
        return step
    }
}