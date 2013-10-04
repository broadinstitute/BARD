package bard.db.project

import acl.CapPermissionService
import bard.db.command.BardCommand
import bard.db.dictionary.Element
import bard.db.dictionary.StageTree
import bard.db.enums.ContextType
import bard.db.enums.ProjectGroupType
import bard.db.enums.ProjectStatus
import bard.db.experiment.Experiment
import bard.db.model.AbstractContextOwner
import bard.db.people.Role
import bard.db.registration.Assay
import bard.db.registration.EditingHelper
import bardqueryapi.IQueryService
import grails.converters.JSON
import grails.plugins.springsecurity.Secured
import grails.plugins.springsecurity.SpringSecurityService
import grails.validation.Validateable
import groovy.transform.InheritConstructors
import org.springframework.http.HttpStatus
import org.springframework.security.access.AccessDeniedException
import org.springframework.web.client.HttpClientErrorException

import javax.servlet.http.HttpServletResponse

@Mixin(EditingHelper)
@Secured(['isAuthenticated()'])
class ProjectController {
    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]
    ProjectExperimentRenderService projectExperimentRenderService
    ProjectService projectService
    SpringSecurityService springSecurityService
    def permissionEvaluator
    CapPermissionService capPermissionService
    IQueryService queryService

    def groupProjects() {
        String username = springSecurityService.principal?.username
        List<Project> projects = capPermissionService.findAllObjectsForRoles(Project)
        LinkedHashSet<Project> uniqueProjects = new LinkedHashSet<Project>(projects)
        render(view: "groupProjects", model: [projects: uniqueProjects])
    }

    def create(ProjectCommand projectCommand) {
        if (!projectCommand) {
            projectCommand: new ProjectCommand()
        }
        [projectCommand: projectCommand]
    }

    def save(ProjectCommand projectCommand) {
        if (!projectCommand.validate()) {
            create(projectCommand)
            return
        }
        Project project = projectCommand.createProject()
        if (project) {
            redirect(action: "show", id: project.id)
            return
        }
        create(projectCommand)
    }


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

    def editOwnerRole(InlineEditableCommand inlineEditableCommand) {
        try {
            final Role ownerRole = Role.findByDisplayName(inlineEditableCommand.value)?:Role.findByAuthority(inlineEditableCommand.value)
            if (!ownerRole) {
                editBadUserInputErrorMessage("Could not find a registered team with name ${inlineEditableCommand.value}")
                return
            }
            Project project = Project.findById(inlineEditableCommand.pk)
            final String message = inlineEditableCommand.validateVersions(project.version, Project.class)
            if (message) {
                conflictMessage(message)
                return
            }

            if (!BardCommand.isRoleInUsersRoleList(ownerRole)) {
                editBadUserInputErrorMessage("You do not have the permission to select team: ${inlineEditableCommand.value}")
                return
            }
            project = projectService.updateOwnerRole(inlineEditableCommand.pk, ownerRole)
            generateAndRenderJSONResponse(project.version, project.modifiedBy, null, project.lastUpdated, project.ownerRole.displayName)

        }
        catch (AccessDeniedException ade) {
            log.error(ade)
            render accessDeniedErrorMessage()
        } catch (Exception ee) {
            log.error("error in editProjectOwnerRole", ee)
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

            final String inputValue = inlineEditableCommand.value.trim()
            String maxSizeMessage = validateInputSize(Project.PROJECT_NAME_MAX_SIZE, inputValue.length())
            if (maxSizeMessage) {
                editExceedsLimitErrorMessage(maxSizeMessage)
                return
            }
            project = projectService.updateProjectName(inlineEditableCommand.pk, inputValue)

            if (project?.hasErrors()) {
                throw new Exception("Error while editing Project Name")
            }
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

            final String inputValue = inlineEditableCommand.value.trim()
            String maxSizeMessage = validateInputSize(Project.DESCRIPTION_MAX_SIZE, inputValue.length())
            if (maxSizeMessage) {
                editExceedsLimitErrorMessage(maxSizeMessage)
                return
            }
            project = projectService.updateProjectDescription(inlineEditableCommand.pk, inlineEditableCommand.value.trim())

            if (project?.hasErrors()) {
                throw new Exception("Error while editing Project Description")
            }

            generateAndRenderJSONResponse(project.version, project.modifiedBy, null, project.lastUpdated, project.description)

        } catch (AccessDeniedException ade) {
            log.error(ade)
            render accessDeniedErrorMessage()
        } catch (Exception ee) {
            log.error("error in editDescription", ee)
            editErrorMessage()
        }
    }

    def getProjectNames() {
        def query = params?.term
        def projects = Project.findAllByNameIlike("%${query}%", [sort: "name", order: "asc"])
        render projects.collect { it.name } as JSON
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

    def groupType() {
        List<String> sorted = []
        final Collection<ProjectGroupType> projectGroupTypes = ProjectGroupType.values()
        for (ProjectGroupType projectGroupType : projectGroupTypes) {
            sorted.add(projectGroupType.id)
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
        if (params.disableEdit)
            editable = false

        String owner = capPermissionService.getOwner(projectInstance)

        Map projectMap = null;
        try {
            if (projectInstance.ncgcWarehouseId != null) {
                projectMap = this.queryService.showProject(projectInstance.ncgcWarehouseId)
            }
        }
        catch (HttpClientErrorException httpClientErrorException) {
            if (httpClientErrorException.statusCode != HttpStatus.NOT_FOUND) {
                throw new RuntimeException("Exception for Project capId:${params.id} ncgcWarehouseId:${projectInstance.ncgcWarehouseId}", httpClientErrorException)
            }
        }

        return [instance: projectInstance,
                projectOwner: owner,
                pexperiment: projectExperimentRenderService.contructGraph(projectInstance),
                editable: editable ? 'canedit' : 'cannotedit',
                projectAdapter: projectMap?.projectAdapter,
                experiments: projectMap?.experiments,
                assays: projectMap?.assays,
                searchString: params.searchString
        ]
    }

    @Deprecated //this method is not used anymore. The editing is now done from the SHOW page.
    def edit() {
        def projectInstance = Project.get(params.id)

        if (!projectInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'project.label', default: 'Project'), params.id])
            return
        } else {
            flash.message = null
        }

        [instance: projectInstance, pexperiment: projectExperimentRenderService.contructGraph(projectInstance)]
    }


    def reloadProjectSteps(Long projectId) {
        try {
            Project project = Project.findById(projectId)
            boolean editable = canEdit(permissionEvaluator, springSecurityService, project)
            render(template: "showstep", model: [experiments: project.projectExperiments, editable: editable ? 'canedit' : 'cannotedit', pegraph: projectExperimentRenderService.contructGraph(project), instanceId: project.id])
        } catch (Exception e) {
            render 'serviceError:' + e.message
        }
    }

    def removeExperimentFromProject(Long experimentId, Long projectId) {
        def experiment = Experiment.findById(experimentId)
        try {
            projectService.removeExperimentFromProject(experiment, projectId)
            Project project = Project.findById(projectId)
            render(template: "showstep", model: [experiments: project.projectExperiments, editable: 'canedit', pegraph: projectExperimentRenderService.contructGraph(project), instanceId: project.id])
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
            projectService.removeEdgeFromProject(fromExperiment, toExperiment, project.id)
            project = Project.findById(projectId)
            render(template: "showstep", model: [experiments: project.projectExperiments, editable: 'canedit', pegraph: projectExperimentRenderService.contructGraph(project), instanceId: project.id])
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

        def fromExperiment = Experiment.findById(fromExperimentId)
        def toExperiment = Experiment.findById(toExperimentId)

        try {
            projectService.linkExperiment(fromExperiment, toExperiment, projectId)
            Project project = Project.findById(projectId)
            render(template: "showstep", model: [experiments: project.projectExperiments, editable: 'canedit', pegraph: projectExperimentRenderService.contructGraph(project), instanceId: project.id])
        } catch (AccessDeniedException ade) {
            log.error(ade)
            render accessDeniedErrorMessage()
        } catch (UserFixableException e) {
            log.error(e)
            render status: HttpServletResponse.SC_INTERNAL_SERVER_ERROR, text: e.message
        }
    }

    def associateExperimentsToProject() {
        // get all values regardless none, single, or multiple, ajax seemed serialized array and passed [] at the end of the param name.
        Set<String> selectedExperiments = request.getParameterValues('selectedExperiments[]') as Set<String>
        Long projectId = params.getLong('projectId')
        Project project = Project.findById(projectId)
        Long stageId = params.getLong('stageId')
        Element element = Element.findById(stageId)

        if (selectedExperiments?.isEmpty()) {
            render status: HttpServletResponse.SC_BAD_REQUEST, text: 'Experiment must be selected'
        } else {
            try {
                selectedExperiments?.each { String experimentDisplayName ->
                    String experimentId = experimentDisplayName.split("-")[0]
                    Experiment experiment = Experiment.findById(experimentId as Long)
                    projectService.addExperimentToProject(experiment, projectId, element)
                    render(template: "showstep", model: [editable: 'canedit', experiments: project.projectExperiments, pegraph: projectExperimentRenderService.contructGraph(project), instanceId: project.id])
                }
            }
            catch (AccessDeniedException ade) {
                log.error("Associate Experiments to Project", ade)
                render accessDeniedErrorMessage()
            } catch (UserFixableException e) {
                render 'serviceError:' + e.message
            } catch (Exception ee) {
                log.error(ee)
                render status: HttpServletResponse.SC_INTERNAL_SERVER_ERROR, text: "Server error occurred."
            }
        }
    }

    def editSummary(Long instanceId, String projectName, String description, String projectStatus) {
        Project projectInstance = Project.findById(instanceId)
        boolean editable = canEdit(permissionEvaluator, springSecurityService, projectInstance)
        if (editable) {
            projectInstance.name = projectName
            projectInstance.description = description
            projectInstance.projectStatus = Enum.valueOf(ProjectStatus, projectStatus);
            projectInstance.save(flush: true)
            projectInstance = Project.findById(instanceId)
            render(template: "summaryDetail", model: [project: projectInstance])
        } else {
            render accessDeniedErrorMessage()
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

    def editContext(Long id, String groupBySection) {
        Project instance = Project.get(id)
        if (!instance) {
            // FIXME:  Should not use flash if we do not redirect afterwards
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'project.label', default: 'Project'), id])
            return

        }
        AbstractContextOwner.ContextGroup contextGroup = instance.groupBySection(ContextType.byId(groupBySection?.decodeURL()))
        [instance: instance, contexts: [contextGroup]]
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
                    projectExperiment = projectService.updateProjectStage(projectExperiment.project.id, projectExperiment, newStage)
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

    def showEditSummary(Long instanceId) {
        def instance = Project.findById(instanceId)
        render(template: "editSummary", model: [project: instance])
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


}

@InheritConstructors
@Validateable
class ProjectCommand extends BardCommand {

    String name
    String description
    ProjectGroupType projectGroupType = ProjectGroupType.PROJECT
    ProjectStatus projectStatus = ProjectStatus.DRAFT

    SpringSecurityService springSecurityService
    Role ownerRole


    static constraints = {
        importFrom(Project, include: ["ownerRole", "name", "description", "groupType", "projectStatus"])
        ownerRole(nullable: false, validator: { value, command, err ->
            /*We make it required in the command object even though it is optional in the domain.
         We will make it required in the domain as soon as we are done back populating the data*/
            //validate that the selected role is in the roles associated with the user
            if (!BardCommand.isRoleInUsersRoleList(value)) {
                err.rejectValue('ownerRole', "message.code", "You do not have the privileges to create Projects for this team : ${value.displayName}");
            }
        })
    }

    Project createProject() {

        if (validate()) {
            Project project = new Project()
            copyFromCmdToDomain(project)
            project.save(flush: true)
            return project
        }
        return null
    }

    void copyFromCmdToDomain(Project project) {
        project.groupType = this.projectGroupType
        project.name = this.name
        project.modifiedBy = springSecurityService.principal?.username
        project.description = this.description
        project.dateCreated = new Date()
        project.projectStatus = this.projectStatus
        project.ownerRole = this.ownerRole

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

