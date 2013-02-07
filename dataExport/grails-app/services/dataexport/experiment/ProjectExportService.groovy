package dataexport.experiment

import bard.db.dictionary.Element
import bard.db.enums.ReadyForExtraction
import bard.db.experiment.Experiment

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
        return utilityService.update(project, id, clientVersion, ReadyForExtraction.byId(latestStatus), "Project")
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
        if (project.readyForExtraction) {
            attributes.put('readyForExtraction', project.readyForExtraction.getId())
        }
        if (project.groupType) {
            attributes.put('groupType', project.groupType)
        }
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
            if (project.projectExperiments) {
                generateProjectExperiments(markupBuilder, project.projectExperiments)
            }
            if (project.projectSteps) {
                generateProjectSteps(markupBuilder, project.projectSteps)
            }
            generateProjectLinks(markupBuilder, project)
        }
    }

    protected void generateProjectSteps(MarkupBuilder markupBuilder, Set<ProjectStep> projectSteps) {
        markupBuilder.projectSteps{
            for(ProjectStep projectStep in projectSteps){
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
                for (ProjectContext context: contexts) {
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

    protected void generateProjectExperiment(MarkupBuilder markupBuilder, ProjectExperiment projectExperiment) {
        markupBuilder.projectExperiment(projectExperimentId: projectExperiment.id) {
            Experiment experiment = projectExperiment.experiment
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
            Element stage = projectExperiment.stage
            if (projectExperiment.stage) {
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
            generateProjectExperimentContexts(markupBuilder, projectExperiment.projectExperimentContexts)
        }
    }

    protected void generateProjectExperimentContexts(MarkupBuilder markupBuilder, List<ProjectExperimentContext> projectExperimentContexts) {
        if (projectExperimentContexts) {
            markupBuilder.contexts() {
                for (ProjectExperimentContext projectExperimentContext : projectExperimentContexts) {
                    generateProjectExperimentContext(markupBuilder, projectExperimentContext)
                }
            }
        }
    }

    protected void generateProjectExperimentContext(MarkupBuilder markupBuilder, ProjectExperimentContext projectExperimentContext) {
        Map attributes = ['id': projectExperimentContext.id,
                'displayOrder': projectExperimentContext.projectExperiment.projectExperimentContexts.indexOf(projectExperimentContext)]
        markupBuilder.context(attributes) {
            //TODO: AssayContext uses preferred Name. Is it something we should do?
            addContextInformation(markupBuilder, projectExperimentContext)
            generateProjectExperimentContextItems(markupBuilder, projectExperimentContext.contextItems)
        }
    }

    protected void generateProjectExperimentContextItems(MarkupBuilder markupBuilder, List<ProjectExperimentContextItem> projectExperimentContextItems) {
        if (projectExperimentContextItems) {
            markupBuilder.contextItems() {
                for (ProjectExperimentContextItem projectExperimentContextItem : projectExperimentContextItems) {
                    generateProjectExperimentContextItem(markupBuilder, projectExperimentContextItem)
                }
            }
        }
    }

    protected void generateProjectExperimentContextItem(MarkupBuilder markupBuilder, ProjectExperimentContextItem projectExperimentContextItem) {
        generateContextItem(
                markupBuilder,
                projectExperimentContextItem,
                null,
                "contextItem",
                this.mediaTypesDTO.elementMediaType,
                grailsLinkGenerator,
                projectExperimentContextItem.id,
                projectExperimentContextItem.context.contextItems.indexOf(projectExperimentContextItem))
    }
}
