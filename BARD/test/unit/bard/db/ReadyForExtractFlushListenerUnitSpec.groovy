/* Copyright (c) 2014, The Broad Institute
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
    * Redistributions of source code must retain the above copyright
      notice, this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright
      notice, this list of conditions and the following disclaimer in the
      documentation and/or other materials provided with the distribution.
    * Neither the name of The Broad Institute nor the
      names of its contributors may be used to endorse or promote products
      derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL The Broad Institute BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package bard.db

import bard.db.dictionary.Element
import bard.db.dictionary.ElementHierarchy
import bard.db.dictionary.Ontology
import bard.db.dictionary.OntologyItem
import bard.db.enums.Status
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
@Build([Element, ElementHierarchy, Project, ProjectContext, ProjectContextItem, ProjectDocument, ExternalReference, ExternalSystem, ProjectSingleExperiment, ProjectExperimentContext, ProjectExperimentContextItem, ProjectStep,
Experiment, ElementHierarchy, Ontology, OntologyItem, Assay, AssayContext, AssayContextExperimentMeasure, AssayDocument, AssayContextItem, Experiment, ExperimentContext, ExperimentContextItem, ExperimentMeasure, ExperimentFile])
@Mock([Element, ElementHierarchy, Project, ProjectContext, ProjectContextItem, ProjectDocument, ExternalReference, ExternalSystem, ProjectSingleExperiment, ProjectExperimentContext, ProjectExperimentContextItem, ProjectStep,
Experiment, ElementHierarchy, Ontology, OntologyItem, Assay, AssayContext, AssayContextExperimentMeasure, AssayDocument, AssayContextItem, Experiment, ExperimentContext, ExperimentContextItem, ExperimentMeasure, ExperimentFile])
@Unroll
class ReadyForExtractFlushListenerUnitSpec extends Specification {
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
        description     | targetConstructor
        "Assay"         | { assay -> assay }
        "AssayContext"  | { assay -> AssayContext.build(assay: assay) }
        "AssayDocument" | { assay -> AssayDocument.build(assay: assay) }
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
        "ProjectSingleExperiment"      | { project -> ProjectSingleExperiment.build(project: project) }
        "ProjectExperimentContext"     | { project -> ProjectExperimentContext.build(projectExperiment: ProjectSingleExperiment.build(project: project)) }
        "ProjectExperimentContextItem" | { project -> ProjectExperimentContextItem.build(context: ProjectExperimentContext.build(projectExperiment: ProjectSingleExperiment.build(project: project))) }
        "ProjectStep next"             | { project -> buildStep(ProjectSingleExperiment.build(project: project), ProjectSingleExperiment.build(project: project)) }
        "ProjectStep prev"             | { project -> buildStep(ProjectSingleExperiment.build(project: project), ProjectSingleExperiment.build(project: project)) }
    }


    def 'test experiment with projects setting ready for extract for #description'() {
        setup:
        Project projectx = Project.build(projectStatus: status)
        Experiment experiment1 = Experiment.build()
        Experiment experiment2 = Experiment.build()

        buildStep(ProjectSingleExperiment.build(project: projectx, experiment: experiment1), ProjectSingleExperiment.build(project: projectx, experiment: experiment2))

        when:
        experiment1.experimentStatus = status
        def impacted = new HashSet(ReadyForExtractFlushListener.getObjectsImpactedByChange(experiment1))

        then:
        boolean isExperiment = false
        boolean isProject = false
        impacted.size() == 2
        impacted.each {
            if (it instanceof Project) {
                isProject = true
            }
            if (it instanceof Experiment) {
                isExperiment = true
            }
        }
        assert isExperiment
        assert isProject

        where:
        desc                      | status
        "With Approved status"    | Status.APPROVED
        "With Provisional status" | Status.PROVISIONAL
    }

    ProjectStep buildStep(ProjectSingleExperiment next, ProjectSingleExperiment prev) {
        ProjectStep step = ProjectStep.build()
        step.nextProjectExperiment = next
        step.previousProjectExperiment = prev
        return step
    }
}
