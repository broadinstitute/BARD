package bard.db.project

import bard.db.command.BardCommand
import bard.db.dictionary.Element
import bard.db.dictionary.StageTree
import bard.db.enums.ProjectStatus
import bard.db.experiment.Experiment
import bard.db.model.AbstractContextOwner
import bard.db.registration.Assay
import bard.db.registration.EditingHelper
import grails.converters.JSON
import grails.plugins.springsecurity.Secured
import grails.plugins.springsecurity.SpringSecurityService
import grails.validation.Validateable
import groovy.transform.InheritConstructors
import org.springframework.security.access.AccessDeniedException

import javax.servlet.http.HttpServletResponse

@Mixin(EditingHelper)
@Secured(['isAuthenticated()'])
class ProjectController {
    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]
    ProjectExperimentRenderService projectExperimentRenderService
    ProjectService projectService
    SpringSecurityService springSecurityService
    def permissionEvaluator

    def editProjectStatus(InlineEditableCommand inlineEditableCommand) {
        try {
            final ProjectStatus projectStatus = ProjectStatus.byId(inlineEditableCommand.value)
            final Project project = Project.findById(inlineEditableCommand.pk)
            final String message = inlineEditableCommand.validateVersions(project.version, Project.class)
            if (message) {
                conflictMessage(message)
                return
            }
            project = projectService.updateProjectStatus(inlineEditableCommand.pk, projectStatus)
            generateAndRenderJSONResponse(project.version, project.modifiedBy, null, project.lastUpdated, project.projectStatus.id)
        }
        catch (AccessDeniedException ade) {
            log.error(ade)
            render accessDeniedErrorMessage()
        } catch (Exception ee) {
            log.error("error in editProjectStatus", ee)
            editErrorMessage()
        }
    }

    def editProjectName(InlineEditableCommand inlineEditableCommand) {
        try {
            Project project = Project.findById(inlineEditableCommand.pk)
            final String message = inlineEditableCommand.validateVersions(project.version, Project.class)
            if (message) {
                conflictMessage(message)
                return
            }
            project = projectService.updateProjectName(inlineEditableCommand.pk, inlineEditableCommand.value.trim())
            generateAndRenderJSONResponse(project.version, project.modifiedBy, null, project.lastUpdated, project.name)

        } catch (AccessDeniedException ade) {
            log.error(ade)
            render accessDeniedErrorMessage()
        } catch (Exception ee) {
            log.error(ee)
            editErrorMessage()
        }
    }

    def editDescription(InlineEditableCommand inlineEditableCommand) {
        try {
            Project project = Project.findById(inlineEditableCommand.pk)
            final String message = inlineEditableCommand.validateVersions(project.version, Project.class)
            if (message) {
                conflictMessage(message)
                return
            }
            project = projectService.updateProjectDescription(inlineEditableCommand.pk, inlineEditableCommand.value.trim())
            generateAndRenderJSONResponse(project.version, project.modifiedBy, null, project.lastUpdated, project.description)

        } catch (AccessDeniedException ade) {
            log.error(ade)
            render accessDeniedErrorMessage()
        } catch (Exception ee) {
            log.error("error in editDescription", ee)
            editErrorMessage()
        }
    }

    def projectStatus() {
        List<String> sorted = []
        final Collection<ProjectStatus> projectStatuses = ProjectStatus.values()
        for (ProjectStatus projectStatus : projectStatuses) {
            sorted.add(projectStatus.id)
        }
        sorted.sort()
        final JSON json = sorted as JSON
        render text: json, contentType: 'text/json', template: null
    }

    def show(Long id) {
        def projectInstance = Project.get(id)
        if (!projectInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'project.label', default: 'Project'), params.id])
            return
        }
        boolean editable = canEdit(permissionEvaluator, springSecurityService, projectInstance)

        [instance: projectInstance, pexperiment: projectExperimentRenderService.contructGraph(projectInstance), editable: editable ? 'canedit' : 'cannotedit']
    }

    def edit() {
        def projectInstance = Project.get(params.id)

        if (!projectInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'project.label', default: 'Project'), params.id])
            return
        } else
            flash.message = null

        [instance: projectInstance, pexperiment: projectExperimentRenderService.contructGraph(projectInstance)]
    }

    def reloadProjectSteps(Long projectId) {
        try {
            Project project = Project.findById(projectId)
            render(template: "showstep", model: [experiments: project.projectExperiments, pegraph: projectExperimentRenderService.contructGraph(project), instanceId: project.id])
        } catch (Exception e) {
            render 'serviceError:' + e.message
        }
    }

    def removeExperimentFromProject(Long experimentId, Long projectId) {
        def experiment = Experiment.findById(experimentId)
        def project = Project.findById(projectId)
        try {
            projectService.removeExperimentFromProject(experiment, project)
            project = Project.findById(projectId)
            render(template: "showstep", model: [experiments: project.projectExperiments, pegraph: projectExperimentRenderService.contructGraph(project), instanceId: project.id])
        } catch (AccessDeniedException ade) {
            log.error(ade)
            render accessDeniedErrorMessage()
        } catch (UserFixableException e) {
            render 'serviceError:' + e.message
        }
    }

    def removeEdgeFromProject(Long fromExperimentId, Long toExperimentId, Long projectId) {
        def fromExperiment = Experiment.findById(fromExperimentId)
        def toExperiment = Experiment.findById(toExperimentId)
        def project = Project.findById(projectId)
        try {
            projectService.removeEdgeFromProject(fromExperiment, toExperiment, project)
            project = Project.findById(projectId)
            render(template: "showstep", model: [experiments: project.projectExperiments, pegraph: projectExperimentRenderService.contructGraph(project), instanceId: project.id])
        } catch (AccessDeniedException ade) {
            log.error(ade)
            render accessDeniedErrorMessage()
        } catch (UserFixableException e) {
            render 'serviceError:' + e.message
        }
    }

    def linkExperiment(Long fromExperimentId, Long toExperimentId, Long projectId) {
        if (fromExperimentId == null || toExperimentId == null) {
            render status: HttpServletResponse.SC_BAD_REQUEST, text: "Both 'From Experiment ID' and 'To Experiment ID' are required"
            return
        }
        def project = Project.findById(projectId)
        def fromExperiment = Experiment.findById(fromExperimentId)
        def toExperiment = Experiment.findById(toExperimentId)

        try {
            projectService.linkExperiment(fromExperiment, toExperiment, project)
            project = Project.findById(projectId)
            render(template: "showstep", model: [experiments: project.projectExperiments, pegraph: projectExperimentRenderService.contructGraph(project), instanceId: project.id])
        } catch (AccessDeniedException ade) {
            log.error(ade)
            render accessDeniedErrorMessage()
        } catch (UserFixableException e) {
            log.error(e)
            render status: HttpServletResponse.SC_INTERNAL_SERVER_ERROR, text: 'An internal server error has occurred. Please notify the BARD team'
        }
    }

    def associateExperimentsToProject() {
        // get all values regardless none, single, or multiple, ajax seemed serialized array and passed [] at the end of the param name.
        def param1 = request.getParameterValues('selectedExperiments[]')
        def projectId = params['projectId']
        def project = Project.findById(projectId)
        def stageId = params['stageId']
        def element = Element.findById(stageId)
        // get rid of duplicated selection if there is any
        Set<String> selectedExperiments = new HashSet<String>()

        param1.each {
            selectedExperiments.add(it)
        }
        if (selectedExperiments.isEmpty()) {
            render status: HttpServletResponse.SC_BAD_REQUEST, text: 'Experiment must be selected'
        } else {
            try {
                selectedExperiments?.each { String experimentDisplayName ->
                    def experimentId = experimentDisplayName.split("-")[0]
                    def experiment = Experiment.findById(experimentId)
                    projectService.addExperimentToProject(experiment, project, element)
                    render(template: "showstep", model: [experiments: project.projectExperiments, pegraph: projectExperimentRenderService.contructGraph(project), instanceId: project.id])
                }
            }
            catch (AccessDeniedException ade) {
                log.error(ade)
                render accessDeniedErrorMessage()
            } catch (UserFixableException e) {
                render 'serviceError:' + e.message
            } catch (Exception ee) {
                log.error(ee)
                render status: HttpServletResponse.SC_INTERNAL_SERVER_ERROR, text: "Server error occurred."
            }
        }
    }

    def projectStages() {
        List<String> sorted = []
        final Collection<StageTree> stageTrees = StageTree.findAll()
        for (StageTree stageTree : stageTrees) {
            sorted.add(stageTree.label)
        }
        sorted.sort()
        final JSON json = sorted as JSON
        render text: json, contentType: 'text/json', template: null
    }

    def updateProjectStage(InlineEditableCommand inlineEditableCommand) {
        //pass in the project experiment
        try {
            ProjectExperiment projectExperiment = ProjectExperiment.findById(inlineEditableCommand.pk)

            //x-editable will not send a new value only when the original is different from the selected
            //but we still add this piece of defensive code anyway just so it still works if someone hacks the URL
            Long originalStageElementId = null
            if (inlineEditableCommand?.name?.isNumber()) {
                originalStageElementId = new Long(inlineEditableCommand.name)
            }
            Element newStage = Element.findByLabel(inlineEditableCommand.value)

            if (originalStageElementId != newStage?.id) {//there has been a change

                if (newStage) {
                    projectExperiment = projectService.updateProjectStage(projectExperiment.project, projectExperiment, newStage)
                } else {
                    render status: 404, text: "Could not find stage with label ${inlineEditableCommand.value}", contentType: 'text/plain', template: null
                    return
                }
            }
            render text: "${projectExperiment.stage.label}", contentType: 'text/plain', template: null
        } catch (AccessDeniedException ade) {
            log.error(ade)
            render accessDeniedErrorMessage()
        } catch (Exception ee) {
            render status: HttpServletResponse.SC_INTERNAL_SERVER_ERROR, text: "An internal Server error happend while you were performing this task. Contact the BARD team to help resove this issue", contentType: 'text/plain', template: null
        }
    }

    def ajaxFindAvailableExperimentByName(String experimentName, Long projectId) {
        List<Experiment> experiments = Experiment.findAllByExperimentNameIlike("%${experimentName}%")
        Project project = Project.findById(projectId)
        Set<Experiment> exps = []
        experiments.each { Experiment experiment ->
            if (!projectService.isExperimentAssociatedWithProject(experiment, project))
                exps.add(experiment)
        }
        render exps.collect { it.displayName } as JSON
    }

    def ajaxFindAvailableExperimentByAssayId(Long assayId, Long projectId) {
        Assay assay = Assay.findById(assayId)
        Project project = Project.findById(projectId)
        List<Experiment> experiments = Experiment.findAllByAssay(assay)
        Set<Experiment> exps = []
        experiments.each { Experiment experiment ->
            if (!projectService.isExperimentAssociatedWithProject(experiment, project))
                exps.add(experiment)
        }
        render exps.collect { it.displayName } as JSON
    }

    def ajaxFindAvailableExperimentById(Long experimentId, Long projectId) {
        Project project = Project.findById(projectId)
        Experiment experiment = Experiment.findById(experimentId)
        Set<Experiment> exps = []
        if (!projectService.isExperimentAssociatedWithProject(experiment, project))
            exps.add(experiment)
        render exps.collect { it.displayName } as JSON
    }
    //TODO: Move into a service so we can secure it
    def editSummary(Long instanceId, String projectName, String description, String projectStatus) {
        def instance = Project.findById(instanceId)
        instance.name = projectName
        instance.description = description
        instance.projectStatus = Enum.valueOf(ProjectStatus, projectStatus);
        instance.save(flush: true)
        instance = Project.findById(instanceId)
        render(template: "summaryDetail", model: [project: instance])
    }

    def editContext(Long id,String groupBySection) {
        Project instance = Project.get(id)
        if (!instance) {
            // FIXME:  Should not use flash if we do not redirect afterwards
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'project.label', default: 'Project'), id])
            return
        }
        AbstractContextOwner.ContextGroup contextGroup =instance.groupBySection(groupBySection?.decodeURL())
        [instance: instance, contexts : [contextGroup]]
    }

    def showEditSummary(Long instanceId) {
        def instance = Project.findById(instanceId)
        render(template: "editSummary", model: [project: instance])
    }

    def findById() {
        if (params.projectId && params.projectId.isLong()) {
            def instance = Project.findById(params.projectId)
            if (instance)
                redirect(action: "show", id: instance.id)
            else
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'project.label', default: 'Project'), params.projectId])
        }
    }

    def findByName() {
        if (params.projectName) {
            def projects = Project.findAllByNameIlike("%${params.projectName}%")
            if (projects?.size() > 1) {
                if (params.sort == null) {
                    params.sort = "id"
                }
                projects.sort {
                    a, b ->
                        if (params.order == 'desc') {
                            b."${params.sort}" <=> a."${params.sort}"
                        } else {
                            a."${params.sort}" <=> b."${params.sort}"
                        }
                }
                render(view: "findByName", params: params, model: [projects: projects])
            } else if (projects?.size() == 1)
                redirect(action: "show", id: projects.get(0).id)
            else
                flash.message = message(code: 'default.not.found.property.message', args: [message(code: 'project.label', default: 'Project'), "name", params.projectName])
        }
    }

    def getProjectNames() {
        def query = params?.term
        def projects = Project.findAllByNameIlike("%${query}%", [sort: "name", order: "asc"])
        render projects.collect { it.name } as JSON
    }
}
@InheritConstructors
@Validateable
class InlineEditableCommand extends BardCommand {
    Long pk //primary key of the current entity
    String name
    String value //the new value
    Long version  //version of the current entity
    Long owningEntityId //if this has an owning entity

    static constraints = {
        pk(blank: false, nullable: false)
        name(blank: false, nullable: false)
        value(blank: false, nullable: false)
        version(blank: false, nullable: false)
        owningEntityId(nullable: true)
    }

    String validateVersions(Long currentVersion, Class entityClass) {
        StringBuilder b = new StringBuilder()
        if (this.version?.longValue() != currentVersion.longValue()) {
            getErrors().reject('default.optimistic.locking.failure', [entityClass] as Object[], 'optimistic lock failure')
            b.append(g.message(code: 'default.optimistic.locking.failure'))
        }
        return b.toString()
    }

}

