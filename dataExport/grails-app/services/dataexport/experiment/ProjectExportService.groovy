package dataexport.experiment

import bard.db.enums.ReadyForExtraction
import bard.db.experiment.Experiment
import bard.db.model.AbstractContext
import bard.db.registration.ExternalReference
import dataexport.registration.BardHttpResponse
import dataexport.registration.MediaTypesDTO
import dataexport.util.ExportAbstractService
import dataexport.util.UtilityService
import exceptions.NotFoundException
import groovy.xml.MarkupBuilder
import org.codehaus.groovy.grails.web.mapping.LinkGenerator
import bard.db.project.*

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
    public BardHttpResponse update(final Long id, final Long clientVersion, final String latestStatus) {
        final Project project = Project.findById(id)
        return utilityService.update(project, id, clientVersion, latestStatus as ReadyForExtraction, "Project")
    }
    /**
     * Generate projectStep
     *
     * @param markupBuilder
     * @param projectStep
     */
    protected void generateProjectStep(final MarkupBuilder markupBuilder, final ProjectStep projectStep) {

        markupBuilder.projectStep(projectStepId: projectStep.id.toString()) {
            description(projectStep.description)
            final Experiment experiment = projectStep.experiment
            experimentRef(label: experiment.experimentName) {
                Map map = [mapping: 'experiment', rel: 'related',
                        mediaType: this.mediaTypesDTO.experimentMediaType,
                        params: [id: experiment.id]]
                generateLink(map, markupBuilder, this.grailsLinkGenerator)
            }
            final Experiment precedingExperiment = projectStep.precedingExperiment
            if (precedingExperiment) {
                precedingExperimentRef(label: precedingExperiment.experimentName) {
                    Map map = [
                            mapping: 'experiment', rel: 'related',
                            mediaType: this.mediaTypesDTO.experimentsMediaType,
                            params: [id: precedingExperiment.id]
                    ]
                    generateLink(map, markupBuilder, this.grailsLinkGenerator)
                }
            }
            if (projectStep.stepContexts) {
                generateStepContexts(markupBuilder, projectStep.stepContexts)
            }
        }
    }

    protected void generateProjectSteps(final MarkupBuilder markupBuilder, Set<ProjectStep> projectStepSet) {
        markupBuilder.projectSteps() {
            for (ProjectStep projectStep : projectStepSet) {
                generateProjectStep(markupBuilder, projectStep)
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
        if (project.readyForExtraction) {
            attributes.put('readyForExtraction', project.readyForExtraction.toString())
        }
        if (project.groupType) {
            attributes.put('groupType', project.groupType)
        }
        markupBuilder.project(
                attributes
        ) {
            if (project.projectName) {
                projectName(project.projectName)
            }
            if (project.description) {
                description(project.description)
            }
            if (project.projectSteps) {
                generateProjectSteps(markupBuilder, project.projectSteps)
            }
            if (project.projectContexts) {
                generateProjectContexts(markupBuilder, project.projectContexts)
            }

            generateProjectLinks(markupBuilder, project)
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
        for (ProjectDocument projectDocument : project.projectDocuments) {
            map = [mapping: 'projectDocument', absolute: true, rel: 'item', mediaType: this.mediaTypesDTO.projectDocMediaType, params: [id: projectDocument.id]]
            generateLink(map, markupBuilder, this.grailsLinkGenerator)
        }
    }

    //---- TODO: The following look a lot like their Assay counterpart. At some point we need to refactor so they can all share common code
    /***
     *
     *
     * @param markupBuilder
     * @param projectContextItems
     */
    protected void generateProjectContextItems(final MarkupBuilder markupBuilder, List<ProjectContextItem> projectContextItems) {
        markupBuilder.projectContextItems() {
            for (ProjectContextItem projectContextItem : projectContextItems) {
                generateProjectContextItem(markupBuilder, projectContextItem)
            }

        }
    }

    protected void generateProjectContextItem(final MarkupBuilder markupBuilder, final ProjectContextItem projectContextItem) {
        generateContextItem(
                markupBuilder, projectContextItem,
                null, "projectContextItem",
                this.mediaTypesDTO.elementMediaType,
                grailsLinkGenerator, projectContextItem.id,
                projectContextItem.projectContext.projectContextItems.indexOf(projectContextItem))
    }

    protected void generateProjectContext(final MarkupBuilder markupBuilder, final ProjectContext projectContext) {
        def attributes = ['projectContextId': projectContext.id,
                'displayOrder': projectContext.project.projectContexts.indexOf(projectContext)]
        markupBuilder.projectContext(attributes) {
            //TODO: AssayContext uses preferred Name. Is it something we should do?
            addContextInformation(markupBuilder, projectContext)
            generateProjectContextItems(markupBuilder, projectContext.projectContextItems)
        }
    }

    private void addContextInformation(final MarkupBuilder markupBuilder, final AbstractContext context) {
        markupBuilder.contextName(context.contextName)
        if (context.contextGroup) {
            markupBuilder.contextGroup(context.contextGroup)
        }
    }

    protected void generateStepContext(final MarkupBuilder markupBuilder, final StepContext stepContext) {
        def attributes = ['stepContextId': stepContext.id,
                'displayOrder': stepContext.projectStep.stepContexts.indexOf(stepContext)]
        markupBuilder.stepContext(attributes) {
            //TODO: AssayContext uses preferred Name. Is it something we should do?
            addContextInformation(markupBuilder, stepContext)
            generateStepContextItems(markupBuilder, stepContext.stepContextItems)
        }
    }

    protected void generateStepContextItems(final MarkupBuilder markupBuilder, List<StepContextItem> stepContextItems) {
        markupBuilder.stepContextItems() {
            for (StepContextItem stepContextItem : stepContextItems) {
                generateStepContextItem(markupBuilder, stepContextItem)
            }

        }
    }

    protected void generateStepContextItem(final MarkupBuilder markupBuilder, final StepContextItem stepContextItem) {
        generateContextItem(
                markupBuilder, stepContextItem,
                null, "stepContextItem",
                this.mediaTypesDTO.elementMediaType,
                grailsLinkGenerator, stepContextItem.id,
                stepContextItem.stepContext.stepContextItems.indexOf(stepContextItem))
    }
    /**
     * Generate a project contexts
     * @param markupBuilder
     * @param projectContexts
     */
    protected void generateProjectContexts(final MarkupBuilder markupBuilder, final List<ProjectContext> projectContexts) {
        markupBuilder.projectContexts() {
            for (ProjectContext projectContext : projectContexts) {
                generateProjectContext(markupBuilder, projectContext)
            }
        }

    }

    protected void generateStepContexts(final MarkupBuilder markupBuilder, final List<StepContext> stepContexts) {
        markupBuilder.stepContexts() {
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
        final List<Project> projects = Project.findAllByReadyForExtraction(ReadyForExtraction.Ready)
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
        generateDocument(this.grailsLinkGenerator, markupBuilder, projectDocument,
                'projectDocument', 'project',
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
}
