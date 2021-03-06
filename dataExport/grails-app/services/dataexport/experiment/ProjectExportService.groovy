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

package dataexport.experiment

import bard.db.dictionary.Element
import bard.db.enums.ReadyForExtraction
import bard.db.enums.Status
import bard.db.experiment.Experiment
import bard.db.project.*
import bard.db.registration.ExternalReference
import dataexport.registration.BardHttpResponse
import dataexport.registration.MediaTypesDTO
import dataexport.util.ExportAbstractService
import dataexport.util.UtilityService
import exceptions.NotFoundException
import groovy.xml.MarkupBuilder
import org.apache.commons.lang.StringUtils
import org.codehaus.groovy.grails.web.mapping.LinkGenerator

import javax.xml.datatype.DatatypeFactory
import javax.xml.datatype.XMLGregorianCalendar

class ProjectExportService extends ExportAbstractService {
    MediaTypesDTO mediaTypesDTO
    UtilityService utilityService
    LinkGenerator grailsLinkGenerator

    /**
     * Set the ReadyForExtraction value on the element to 'Complete'
     *
     * Return a 409, conflict, if the version supplied by client is less than the version in the database
     *
     * Return a 412, precondition failed, if the version supplied by client is not equal to the version in the database
     *
     * Return a 404 , if the element cannot be found
     *
     * @param id
     * @param version
     * Returns the HTTPStatus Code
     */
    public BardHttpResponse update(Long id, Long clientVersion, ReadyForExtraction latestStatus) {
        final Project project = Project.findById(id)
        return utilityService.update(project, id, clientVersion, latestStatus, "Project")
    }
    /**
     * Generate projectStep
     *
     * @param markupBuilder
     * @param projectStep
     */
    protected void generateProjectStep(final MarkupBuilder markupBuilder, final ProjectStep projectStep) {
        Map attributes = [projectStepId: projectStep.id,
                nextProjectExperimentRef: projectStep.nextProjectExperiment.id,
                precedingProjectExperimentRef: projectStep.previousProjectExperiment.id]

        markupBuilder.projectStep(attributes) {
            String edgeName = projectStep.edgeName
            if (edgeName) {
                markupBuilder.edgeName(edgeName)
            }
            if (projectStep.stepContexts) {
                generateStepContexts(markupBuilder, projectStep.stepContexts)
            }
        }
    }

    /**
     * Generate a Project
     * @param markupBuilder
     * @param project
     */
    protected void generateProject(final MarkupBuilder markupBuilder, final Project project) {
        def attributes = [:]
        attributes.put('projectId', project.id)
        extractOwner(project, attributes)
        if (project.readyForExtraction) {
            attributes.put('readyForExtraction', project.readyForExtraction.getId())
        }
        if (project.groupType) {
            attributes.put('groupType', project.groupType.id)
        }
        if (project.lastUpdated) {
            final GregorianCalendar gregorianCalendar = new GregorianCalendar();
            gregorianCalendar.setTime(project.lastUpdated);
            final XMLGregorianCalendar lastUpdatedDate = DatatypeFactory.newInstance().newXMLGregorianCalendar(gregorianCalendar);
            attributes.put('lastUpdated', lastUpdatedDate.toString())

        }
        if (StringUtils.isNotBlank(project.modifiedBy)) {
            attributes.put('modifiedBy', StringUtils.substringBefore(project.modifiedBy,'@'))
        }
        attributes.put('status', project.projectStatus.id)
        markupBuilder.project(attributes) {
            if (project.name) {
                projectName(project.name)
            }
            if (project.description) {
                description(project.description)
            }
            if (project.contexts) {
                generateProjectContexts(markupBuilder, project.contexts)
            }
            final Set<ProjectExperiment> projectExperiments = project.projectExperiments
            final Set<ProjectExperiment> projectExperimentsReadyForExraction = new HashSet<ProjectExperiment>()
            for (ProjectExperiment projectExperiment : projectExperiments) {
                for(experiment in projectExperiment.experiments) {
                    if (experiment.readyForExtraction == ReadyForExtraction.READY ||
                            experiment.experimentStatus == Status.APPROVED ||
                            experiment.experimentStatus == Status.PROVISIONAL ||
                            experiment.experimentStatus == Status.RETIRED) {
                        projectExperimentsReadyForExraction.add(projectExperiment)
                    }
                }
            }
            if (projectExperimentsReadyForExraction) {
                generateProjectExperiments(markupBuilder, projectExperimentsReadyForExraction)
            }
            if (project.projectSteps) {
                generateProjectSteps(markupBuilder, project.projectSteps)
            }
            generateProjectLinks(markupBuilder, project)
        }
    }

    protected void generateProjectSteps(MarkupBuilder markupBuilder, Set<ProjectStep> projectSteps) {
        markupBuilder.projectSteps {
            for (ProjectStep projectStep in projectSteps) {
                generateProjectStep(markupBuilder, projectStep)
            }
        }
    }

    protected void generateProjectExperiments(MarkupBuilder markupBuilder, Set<ProjectExperiment> projectExperiments) {
        markupBuilder.projectExperiments() {
            for (ProjectExperiment projectExperiment : projectExperiments) {
                generateProjectExperiment(markupBuilder, projectExperiment)
            }
        }
    }

    protected void generateProjectLinks(final MarkupBuilder markupBuilder, final Project project) {
        Map map = [mapping: 'project', absolute: true, rel: 'edit', mediaType: this.mediaTypesDTO.projectMediaType, params: [id: project.id]]
        generateLink(map, markupBuilder, this.grailsLinkGenerator)

        map = [mapping: 'projects', absolute: true, rel: 'up', mediaType: this.mediaTypesDTO.projectsMediaType]
        generateLink(map, markupBuilder, this.grailsLinkGenerator)


        final Set<ExternalReference> externalReferences = project.externalReferences
        generateExternalReferencesLink(markupBuilder, externalReferences as List<ExternalReference>, this.grailsLinkGenerator, this.mediaTypesDTO)

        //add links for each document
        for (ProjectDocument projectDocument : project.documents) {
            map = [mapping: 'projectDocument', absolute: true, rel: 'item', mediaType: this.mediaTypesDTO.projectDocMediaType, params: [id: projectDocument.id]]
            generateLink(map, markupBuilder, this.grailsLinkGenerator)
        }
    }

    //---- TODO: The following look a lot like their Assay counterpart. At some point we need to refactor so they can all share common code
    /***
     *
     *
     * @param markupBuilder
     * @param contextItems
     */
    protected void generateProjectContextItems(final MarkupBuilder markupBuilder, List<ProjectContextItem> contextItems) {
        if (contextItems) {
            markupBuilder.contextItems() {
                for (ProjectContextItem contextItem : contextItems) {
                    generateProjectContextItem(markupBuilder, contextItem)
                }
            }
        }
    }

    protected void generateProjectContextItem(final MarkupBuilder markupBuilder, final ProjectContextItem contextItem) {
        generateContextItem(
                markupBuilder, contextItem,
                null, "contextItem",
                this.mediaTypesDTO.elementMediaType,
                grailsLinkGenerator, contextItem.id,
                contextItem.context.contextItems.indexOf(contextItem))
    }

    protected void generateProjectContext(final MarkupBuilder markupBuilder, final ProjectContext context) {
        def attributes = ['id': context.id,
                'displayOrder': context.project.contexts.indexOf(context)]
        markupBuilder.context(attributes) {
            //TODO: AssayContext uses preferred Name. Is it something we should do?
            addContextInformation(markupBuilder, context)
            generateProjectContextItems(markupBuilder, context.contextItems)
        }
    }



    protected void generateStepContext(final MarkupBuilder markupBuilder, final StepContext stepContext) {
        def attributes = ['id': stepContext.id,
                'displayOrder': stepContext.projectStep.stepContexts.indexOf(stepContext)]
        markupBuilder.context(attributes) {
            //TODO: AssayContext uses preferred Name. Is it something we should do?
            addContextInformation(markupBuilder, stepContext)
            generateStepContextItems(markupBuilder, stepContext.stepContextItems)
        }
    }

    protected void generateStepContextItems(final MarkupBuilder markupBuilder, List<StepContextItem> stepContextItems) {
        if (stepContextItems) {
            markupBuilder.contextItems() {
                for (StepContextItem stepContextItem : stepContextItems) {
                    generateStepContextItem(markupBuilder, stepContextItem)
                }

            }
        }
    }

    protected void generateStepContextItem(final MarkupBuilder markupBuilder, final StepContextItem stepContextItem) {
        generateContextItem(
                markupBuilder, stepContextItem,
                null, "contextItem",
                this.mediaTypesDTO.elementMediaType,
                grailsLinkGenerator, stepContextItem.id,
                stepContextItem.stepContext.stepContextItems.indexOf(stepContextItem))
    }
    /**
     * Generate a project contexts
     * @param markupBuilder
     * @param context
     */
    protected void generateProjectContexts(final MarkupBuilder markupBuilder, final List<ProjectContext> contexts) {
        if (contexts) {
            markupBuilder.contexts() {
                for (ProjectContext context : contexts) {
                    generateProjectContext(markupBuilder, context)
                }
            }
        }
    }

    protected void generateStepContexts(final MarkupBuilder markupBuilder, final List<StepContext> stepContexts) {
        markupBuilder.contexts() {
            for (StepContext stepContext : stepContexts) {
                generateStepContext(markupBuilder, stepContext)
            }
        }
    }
    /**
     *  Generate a project from a given projectId
     * @param markupBuilder
     * @param projectId
     */
    public Long generateProject(final MarkupBuilder markupBuilder, final Long projectId) {
        final Project project = Project.get(projectId)
        if (!project) {
            throw new NotFoundException("Could not find Project with Id ${projectId}")
        }
        this.generateProject(markupBuilder, project)
        return project.version
    }
    /**
     * @param xml
     */
    public void generateProjects(def markupBuilder) {
        final List<Project> projects = Project.findAllByReadyForExtraction(ReadyForExtraction.READY)
        final int numberOfProjects = projects.size()
        markupBuilder.projects(count: numberOfProjects) {
            for (Project project : projects) {
                Map map = [
                        mapping: 'project',
                        absolute: true, rel: 'item',
                        mediaType: this.mediaTypesDTO.projectMediaType,
                        params: [id: project.id]
                ]
                generateLink(map, markupBuilder, this.grailsLinkGenerator)
            }
        }
    }

    public void generateProjectDocument(final MarkupBuilder markupBuilder, ProjectDocument projectDocument) {
        generateDocument(this.grailsLinkGenerator,
                markupBuilder,
                projectDocument,
                'projectDocument',
                'project',
                projectDocument.id,
                projectDocument.project.id,
                this.mediaTypesDTO.projectDocMediaType,
                this.mediaTypesDTO.projectMediaType
        )
    }
    /**
     * Stream an project document
     * @param markupBuilder
     * @param projectDocument
     */
    public Long generateProjectDocument(
            final MarkupBuilder markupBuilder, final Long projectDocumentId) {
        final ProjectDocument projectDocument = ProjectDocument.get(projectDocumentId)
        if (!projectDocument) {
            final String errorMessage = "Project Document with Id ${projectDocumentId} does not exists"
            log.error(errorMessage)
            throw new NotFoundException(errorMessage)
        }
        generateProjectDocument(markupBuilder, projectDocument)
        return projectDocument.version
    }

    protected void generateProjectExperiment(MarkupBuilder markupBuilder, ProjectExperiment projectExperiment) {
        for (experiment in projectExperiment.experiments) {
            generateProjectSingleExperiment(markupBuilder, projectExperiment.id, projectExperiment.stage, experiment)
        }
    }

    protected void generateProjectSingleExperiment(MarkupBuilder markupBuilder, Long projectExperimentId, Element stage, Experiment experiment) {
        markupBuilder.projectExperiment(projectExperimentId: projectExperimentId) {
            experimentRef(label: experiment.experimentName) {
                Map map = [
                        mapping: 'experiment',
                        absolute: true,
                        rel: 'related',
                        mediaType: this.mediaTypesDTO.experimentMediaType,
                        params: [id: experiment.id]
                ]
                generateLink(map, markupBuilder, this.grailsLinkGenerator)
            }
            if (stage) {
                stageRef(label: stage.label) {
                    Map map = [
                            mapping: 'element',
                            absolute: true,
                            rel: 'related',
                            mediaType: this.mediaTypesDTO.elementMediaType,
                            params: [id: stage.id]
                    ]
                    generateLink(map, markupBuilder, this.grailsLinkGenerator)
                }
            }
        }
    }

}
