package bard.db.project

import acl.CapPermissionService
import bard.db.command.BardCommand
import bard.db.dictionary.Element
import bard.db.dictionary.StageTree
import bard.db.enums.ContextType
import bard.db.enums.ExperimentStatus
import bard.db.enums.ProjectGroupType
import bard.db.enums.ProjectStatus
import bard.db.experiment.Experiment
import bard.db.model.AbstractContextOwner
import bard.db.people.Role
import bard.db.registration.Assay
import bard.db.registration.EditingHelper
import bard.db.registration.IdType
import bard.db.registration.MergeAssayDefinitionService
import bardqueryapi.IQueryService
import grails.converters.JSON
import grails.plugins.springsecurity.Secured
import grails.plugins.springsecurity.SpringSecurityService
import grails.validation.Validateable
import groovy.transform.InheritConstructors
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.springframework.http.HttpStatus
import org.springframework.security.access.AccessDeniedException
import org.springframework.web.client.HttpClientErrorException

import javax.servlet.http.HttpServletResponse

@Mixin(EditingHelper)
class ProjectController {
    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]
    ProjectExperimentRenderService projectExperimentRenderService
    ProjectService projectService
    SpringSecurityService springSecurityService
    def permissionEvaluator
    CapPermissionService capPermissionService
    IQueryService queryService
    GrailsApplication grailsApplication

    @Secured(['isAuthenticated()'])
    def myProjects() {
        List<Project> projects = capPermissionService.findAllByOwnerRolesAndClass(Project)
        Set<Project> uniqueProjects = new HashSet<Project>(projects)
        render(view: "myProjects", model: [projects: uniqueProjects])
    }

    @Secured(['isAuthenticated()'])
    def create(ProjectCommand projectCommand) {
        if (!projectCommand) {
            projectCommand: new ProjectCommand()
        }
        [projectCommand: projectCommand]
    }

    @Secured(['isAuthenticated()'])
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

    @Secured(['isAuthenticated()'])
    def editProjectStatus(InlineEditableCommand inlineEditableCommand) {
        try {
            final ProjectStatus projectStatus = ProjectStatus.byId(inlineEditableCommand.value)
            final Project project = Project.findById(inlineEditableCommand.pk)
            final String message = inlineEditableCommand.validateVersions(project.version, Project.class)
            if (message) {
                conflictMessage(message)
                return
            }

            final List<Experiment> unApprovedExperiments = project.findUnApprovedExperiments() as List<Experiment>
            if (!unApprovedExperiments) {
                project = projectService.updateProjectStatus(inlineEditableCommand.pk, projectStatus)
                generateAndRenderJSONResponse(project.version, project.modifiedBy, project.lastUpdated, project.projectStatus.id)
            } else {
                List<Long> unApprovedIds = unApprovedExperiments.collect { it.id }
                render(status: HttpServletResponse.SC_BAD_REQUEST, text: "Before you can approve this project, you must approve the following experiments: " + unApprovedIds.sort().join(","), contentType: 'text/plain', template: null)
            }
        }
        catch (AccessDeniedException ade) {
            log.error(ade,ade)
            render accessDeniedErrorMessage()
        } catch (Exception ee) {
            log.error("error in editProjectStatus", ee)
            editErrorMessage()
        }
    }

    @Secured(['isAuthenticated()'])
    def editOwnerRole(InlineEditableCommand inlineEditableCommand) {
        try {
            final Role ownerRole = Role.findByDisplayName(inlineEditableCommand.value) ?: Role.findByAuthority(inlineEditableCommand.value)
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
            generateAndRenderJSONResponse(project.version, project.modifiedBy, project.lastUpdated, project.ownerRole.displayName)

        }
        catch (AccessDeniedException ade) {
           log.error(ade,ade)
            render accessDeniedErrorMessage()
        } catch (Exception ee) {
            log.error("error in editProjectOwnerRole", ee)
            editErrorMessage()
        }
    }

    @Secured(['isAuthenticated()'])
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
            generateAndRenderJSONResponse(project.version, project.modifiedBy, project.lastUpdated, project.name)

        } catch (AccessDeniedException ade) {
           log.error(ade,ade)
            render accessDeniedErrorMessage()
        } catch (Exception ee) {
            log.error(ee, ee)
            editErrorMessage()
        }
    }

    @Secured(['isAuthenticated()'])
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

            generateAndRenderJSONResponse(project.version, project.modifiedBy, project.lastUpdated, project.description)

        } catch (AccessDeniedException ade) {
           log.error(ade,ade)
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

    /**
     * Draft is excluded as End users cannot set a status back to Draft
     * @return list of strings representing available status options
     */
    def projectStatus() {
        List<String> sorted = []
        final Collection<ProjectStatus> projectStatuses = ProjectStatus.values()
        for (ProjectStatus projectStatus : projectStatuses) {
            if (projectStatus != ProjectStatus.DRAFT) {
                sorted.add(projectStatus.id)
            }
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

        String owner = projectInstance.getOwner()

        Map projectMap = null;
        try {
            if (projectInstance.ncgcWarehouseId != null) {
                try {
                    projectMap = this.queryService.showProject(projectInstance.ncgcWarehouseId)
                } catch (Exception ee) {
                    log.error(ee, ee)
                }
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

    @Secured(['isAuthenticated()'])
    def reloadProjectSteps(Long projectId) {
        try {
            Project project = Project.findById(projectId)
            boolean editable = canEdit(permissionEvaluator, springSecurityService, project)
            render(template: "showstep", model: [experiments: project.projectExperiments, editable: editable ? 'canedit' : 'cannotedit', pegraph: projectExperimentRenderService.contructGraph(project), instanceId: project.id])
        } catch (Exception e) {
            log.error(e, e)
            render 'serviceError:' + e.message
        }
    }

    @Secured(['isAuthenticated()'])
    def removeExperimentFromProject(Long experimentId, Long projectId) {
        def experiment = Experiment.findById(experimentId)
        try {
            projectService.removeExperimentFromProject(experiment, projectId)
            Project project = Project.findById(projectId)
            render(template: "showstep", model: [experiments: project.projectExperiments, editable: 'canedit', pegraph: projectExperimentRenderService.contructGraph(project), instanceId: project.id])
        } catch (AccessDeniedException ade) {
            log.error(ade, ade)
            render accessDeniedErrorMessage()
        } catch (UserFixableException e) {
            log.error(e, e)
            render 'serviceError:' + e.message
        }
    }

    @Secured(['isAuthenticated()'])
    def removeEdgeFromProject(Long fromExperimentId, Long toExperimentId, Long projectId) {
        def fromExperiment = Experiment.findById(fromExperimentId)
        def toExperiment = Experiment.findById(toExperimentId)
        def project = Project.findById(projectId)
        try {
            projectService.removeEdgeFromProject(fromExperiment, toExperiment, project.id)
            project = Project.findById(projectId)
            render(template: "showstep", model: [experiments: project.projectExperiments, editable: 'canedit', pegraph: projectExperimentRenderService.contructGraph(project), instanceId: project.id])
        } catch (AccessDeniedException ade) {
            log.error(ade, ade)
            render accessDeniedErrorMessage()
        } catch (UserFixableException e) {
            log.error(e, e)
            render 'serviceError:' + e.message
        }
    }

    @Secured(['isAuthenticated()'])
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
            log.error(ade, ade)
            render accessDeniedErrorMessage()
        } catch (UserFixableException e) {
            log.error(e, e)
            render status: HttpServletResponse.SC_INTERNAL_SERVER_ERROR, text: e.message
        }
    }



    @Secured(['isAuthenticated()'])
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

    @Secured(['isAuthenticated()'])
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

    @Secured(['isAuthenticated()'])
    def updateProjectStage(InlineEditableCommand inlineEditableCommand) {
        //pass in the project experiment
        try {
            ProjectSingleExperiment projectExperiment = ProjectSingleExperiment.findById(inlineEditableCommand.pk)

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
            log.error(ade, ade)
            render accessDeniedErrorMessage()
        } catch (Exception ee) {
            log.error(ee, ee)
            render status: HttpServletResponse.SC_INTERNAL_SERVER_ERROR, text: "An internal Server error happend while you were performing this task. Contact the BARD team to help resove this issue", contentType: 'text/plain', template: null
        }
    }

    @Secured(['isAuthenticated()'])
    def showEditSummary(Long instanceId) {
        def instance = Project.findById(instanceId)
        render(template: "editSummary", model: [project: instance])
    }

    @Secured(['isAuthenticated()'])
    def showExperimentsToAddProject(AssociateExperimentsCommand command) {
        command.validate()
        if (!command.fromAddPage) { //if we going to this page from a different page, then we need to clear
            //all errors
            command.clearErrors()
            return [command: command]
        }
        if (command.hasErrors()) {
            return [command: command]
        }

        if (command.idType == IdType.ADID && !command.experimentIds) {
            command.findAvailableExperiments()
            return [command: command]
        }
        try {
            command.addExperimentsToProject()
            redirect(action: "show", id: command.projectId, fragment: "experiment-and-step-header")
        }
        catch (AccessDeniedException ade) {
            log.error("Access denied Experiments to Project", ade)
            render accessDeniedErrorMessage()
        } catch (UserFixableException e) {
            log.error(e, e)
            command.errorMessages.add(e.message)
            return [command: command]
        } catch (Exception ee) {
            log.error(ee, ee)
            command.errorMessages.add("An error has occurred, Please log an issue with the BARD team at ${grailsApplication.config.bard.users.email} to fix this issue")
            return [command: command]
        }

    }
}

@InheritConstructors
@Validateable
class AssociateExperimentsCommand extends BardCommand {
    Long projectId
    List<Long> experimentIds = []
    Long stageId
    //if this is true then it means this command objetc must be valid
    boolean fromAddPage = false

    //The source entity (Could be ADID, AID or EID)
    String sourceEntityIds

    IdType idType = IdType.EID
    List<String> errorMessages = []
    Set<Experiment> availableExperiments = [] as HashSet<Experiment>
    MergeAssayDefinitionService mergeAssayDefinitionService
    ProjectService projectService

    //if this is true then we should validate the experimentIds. This value is set to true in the templae
    //selectExperimentsToAddToProjects
    boolean validateExperimentIds = false

    static constraints = {
        projectId(nullable: false, validator: { value, command, err ->
            if (!Project.get(value)) {
                err.rejectValue('projectId', "associateExperimentsCommand.project.id.not.found",
                        ["${value}"] as Object[],
                        "Could not find Project with PID : ${value}");
            }
        })
        stageId(nullable: true, validator: { value, command, err ->
            if (command.fromAddPage && value) {
                if (!Element.get(value.toLong())) {
                    err.rejectValue('stageId', "associateExperimentsCommand.stage.id.not.found",
                            ["${value}"] as Object[],
                            "Could not find Stage with ID : ${value}");
                }
            }
        })
        experimentIds(nullable: true, validator: { value, command, err ->
            if (command.fromAddPage && command.idType == IdType.ADID && command.validateExperimentIds) {
                if (!value) {
                    err.rejectValue('experimentIds', "associateExperimentsCommand.experiment.ids.min.size", "Select at least one experiment to add");
                } else {
                    for (Long experimentId : value) {
                        if (!Experiment.get(experimentId)) {
                            err.rejectValue('experimentIds', "associateExperimentsCommand.experiment.id.not.found",
                                    ["${value}"] as Object[],
                                    "Could not find Experiment with ID: ${experimentId}");
                        }
                    }
                }
            }
        })
        sourceEntityIds(nullable: false, blank: false, validator: { value, command, err ->

            if (command.fromAddPage) {
                final List<Long> entityIds = MergeAssayDefinitionService.convertStringToIdList(value)
                final Project project = command.getProject()
                for (Long entityId : entityIds) {
                    final def entity = command.mergeAssayDefinitionService.convertIdToEntity(command.idType, entityId)
                    if (!entity) {
                        if (command.idType == IdType.ADID) {
                            err.rejectValue('sourceEntityIds', "associateExperimentsCommand.assayDefinition.id.not.found",
                                    ["${entityId}"] as Object[], "Assay with ADID: ${entityId} cannot be found"
                            )
                        } else if (command.idType == IdType.AID) {
                            err.rejectValue('sourceEntityIds', "associateExperimentsCommand.experiment.aid.not.found",
                                    ["${entityId}"] as Object[],
                                    "Experiment AID: ${entityId} cannot be found")
                        } else {
                            err.rejectValue('sourceEntityIds', "associateExperimentsCommand.experiment.id.not.found",
                                    ["${entityId}"] as Object[],
                                    "Experiment EID: ${entityId} cannot be found")
                        }
                    } else if (entity instanceof Experiment) {
                        final Experiment experiment = (Experiment) entity
                        if (command.projectService.isExperimentAssociatedWithProject(experiment, project)) {
                            if (command.idType == IdType.AID) {
                                err.rejectValue('sourceEntityIds', "associateExperimentsCommand.experiment.aid.already.part.project",
                                        ["${entityId}", "${command.projectId}"] as Object[],
                                        "Experiment AID: ${entityId} is already part of this Project ${command.projectId}");
                            } else {
                                err.rejectValue('sourceEntityIds',
                                        "associateExperimentsCommand.experiment.eid.already.part.project",
                                        ["${entityId}", "${command.projectId}"] as Object[],
                                        "Experiment EID: ${entityId} is already part of this Project ${command.projectId}");
                            }

                        } else if (experiment.experimentStatus == ExperimentStatus.RETIRED) {
                            if (command.idType == IdType.AID) {
                                err.rejectValue('sourceEntityIds', "associateExperimentsCommand.experiment.aid.retired",
                                        ["${entityId}", "${command.projectId}"] as Object[],
                                        "Experiment AID: ${entityId} is retired and cannot be added to this project");
                            } else {
                                err.rejectValue('sourceEntityIds', "associateExperimentsCommand.experiment.eid.retired",
                                        ["${entityId}", "${command.projectId}"] as Object[],
                                        "Experiment EID: ${experiment.id} is retired and cannot be added to this project");
                            }

                        }
                    }
                }
            }
        })
        idType(nullable: false)
    }

    List<Experiment> getExperiments() {
        List<Experiment> experiments = []

        if (idType != IdType.ADID) {
            final List<Long> entityIds = MergeAssayDefinitionService.convertStringToIdList(sourceEntityIds)
            for (Long entityId : entityIds) {
                final def entity = mergeAssayDefinitionService.convertIdToEntity(this.idType, entityId)
                if (entity instanceof Experiment) {
                    final Experiment experiment = (Experiment) entity
                    experiments.add(experiment)
                }
            }
        } else {
            for (Long experimentId : experimentIds) {
                experiments.add(Experiment.get(experimentId))
            }
        }
        return experiments
    }

    Project getProject() {
        return Project.get(projectId)
    }

    Element getStage() {
        if (this.stageId) {
            return Element.get(stageId)
        }
    }

    void addExperimentsToProject() {
        for (Experiment experiment : getExperiments()) {
            projectService.addExperimentToProject(experiment, projectId, getStage())
        }

    }


    void findAvailableExperiments() {

        if (this.idType != IdType.ADID) {
            this.errorMessages.add("This method only handles ADID's")
            return
        }
        availableExperiments = [] as HashSet<Experiment>
        final Project project = getProject()
        final List<Long> entityIds = MergeAssayDefinitionService.convertStringToIdList(sourceEntityIds)

        for (Long entityId : entityIds) {
            final def entity = mergeAssayDefinitionService.convertIdToEntity(this.idType, entityId)
            Set<Experiment> experiments = [] as HashSet<Experiment>
            if (entity instanceof Assay) {
                final Assay assay = (Assay) entity
                experiments = assay.experiments
            }
            for (Experiment experiment : experiments) {
                if (!projectService.isExperimentAssociatedWithProject(experiment, project) &&
                        experiment.experimentStatus != ExperimentStatus.RETIRED) {
                    this.availableExperiments.add(experiment)
                }
            }
        }
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
    String ownerRole


    static constraints = {
        importFrom(Project, include: ["name", "description", "groupType", "projectStatus"])
        ownerRole(nullable: false, blank: false, validator: { value, command, err ->
            Role role = Role.findByAuthority(value)
            if (!isRoleInUsersRoleList(role)) {
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
        project.ownerRole = Role.findByAuthority(ownerRole)
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

