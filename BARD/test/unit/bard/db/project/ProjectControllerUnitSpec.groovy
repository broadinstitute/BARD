package bard.db.project

import acl.CapPermissionService
import bard.db.dictionary.Element
import bard.db.dictionary.StageTree
import bard.db.enums.ProjectGroupType
import bard.db.enums.Status
import bard.db.experiment.Experiment
import bard.db.people.Role
import bard.db.registration.*
import bardqueryapi.IQueryService
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import grails.buildtestdata.mixin.Build
import grails.converters.JSON
import grails.plugins.springsecurity.SpringSecurityService
import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import grails.test.mixin.TestMixin
import grails.test.mixin.support.GrailsUnitTestMixin
import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils
import org.junit.Before
import org.springframework.security.access.AccessDeniedException
import spock.lang.Shared
import spock.lang.Unroll

import javax.servlet.http.HttpServletResponse

/**
 * Created with IntelliJ IDEA.
 * User: xiaorong
 * Date: 12/9/12
 * Time: 8:30 PM
 * To change this template use File | Settings | File Templates.
 */
@TestFor(ProjectController)
@Build([Role, Assay, Project, ProjectSingleExperiment, Experiment, ProjectStep, Element, ExternalReference, StageTree])
@Mock([Role, Assay, Project, ProjectSingleExperiment, Experiment, ProjectStep, Element, ExternalReference, StageTree])
@TestMixin(GrailsUnitTestMixin)
@Unroll
class ProjectControllerUnitSpec extends AbstractInlineEditingControllerUnitSpec {
    @Shared Project project
    @Shared ProjectSingleExperiment projectExperimentFrom
    @Shared ProjectSingleExperiment projectExperimentTo
    @Shared StageTree stageTree1
    @Shared StageTree stageTree2
    ProjectService projectService
    SpringSecurityService springSecurityService
    Role role
    Role otherRole

    @Before
    void setup() {
        SpringSecurityUtils.metaClass.'static'.ifAnyGranted = { String role ->
            return true
        }

        controller.metaClass.mixin(EditingHelper)
        controller.springSecurityService = Mock(SpringSecurityService)
        controller.capPermissionService = Mock(CapPermissionService)
        this.role = Role.build(authority: "ROLE_TEAM_A")
        this.otherRole = Role.build(authority: "ROLE_TEAM_B", displayName: "displayName");

        project = Project.build(ownerRole: role)
        Element element1 = Element.build(label: "primary assay")
        Element element2 = Element.build(label: "secondary assay")
        stageTree1 = StageTree.build(element: element1)
        stageTree2 = StageTree.build(element: element2)
        Experiment experimentFrom = Experiment.build()
        Experiment experimentTo = Experiment.build()
        projectExperimentFrom = ProjectSingleExperiment.build(project: project, experiment: experimentFrom, stage: element1)
        projectExperimentTo = ProjectSingleExperiment.build(project: project, experiment: experimentTo, stage: element2)
        ProjectStep projectStep = ProjectStep.build(previousProjectExperiment: projectExperimentFrom, nextProjectExperiment: projectExperimentTo)
        projectExperimentFrom.addToFollowingProjectSteps(projectStep)
        projectExperimentTo.addToPrecedingProjectSteps(projectStep)
        assert project.validate()
        defineBeans {
            projectExperimentRenderService(ProjectExperimentRenderService)
        }
        projectService = Mock(ProjectService)
        controller.projectService = projectService
    }


    void 'test create Project'() {
        given:
        ProjectCommand projectCommand = new ProjectCommand()
        when:
        controller.create(projectCommand)
        then:
        assert !model
    }


    void 'test save Project #desc'() {

        given:
        projectCommand.springSecurityService = controller.springSecurityService

        projectCommand.ownerRole = this.role.authority
        SpringSecurityUtils.metaClass.'static'.SpringSecurityUtils.getPrincipalAuthorities = {
            return [this.role, this.otherRole]
        }
        when:

        controller.save(projectCommand)
        then:
        response.status == expectedStatus
        where:
        desc                       | projectCommand                                                                                                                                | expectedStatus
        "Full Project"             | new ProjectCommand(name: "name", description: "description", projectStatus: Status.APPROVED, projectGroupType: ProjectGroupType.PANEL) | HttpServletResponse.SC_FOUND
        "Invalid Project- No name" | new ProjectCommand(description: "description", projectStatus: Status.APPROVED, projectGroupType: ProjectGroupType.PANEL)               | HttpServletResponse.SC_OK

    }

    void 'test edit Project Status success'() {
        given:
        Project newProject = Project.build(version: 0, projectStatus: Status.DRAFT)
        Project updatedProject = Project.build(name: "My New Name", version: 1, lastUpdated: new Date(), projectStatus: Status.APPROVED)
        InlineEditableCommand inlineEditableCommand = new InlineEditableCommand(pk: newProject.id,
                version: newProject.version, name: newProject.name, value: updatedProject.projectStatus.id)
        when:
        controller.editProjectStatus(inlineEditableCommand)
        then:
        controller.projectService.updateProjectStatus(_, _) >> { return updatedProject }
        assert response.status == HttpServletResponse.SC_OK
        ObjectMapper mapper = new ObjectMapper();
        JsonNode responseJSON = mapper.readValue(response.text, JsonNode.class);

        assert responseJSON.get("version").asText() == "0"
        assert responseJSON.get("data").asText() == updatedProject.projectStatus.id
        assert responseJSON.get("lastUpdated").asText()
        assert response.contentType == "text/json;charset=utf-8"
    }


    void 'test edit Project Status has unapproved experiments'() {
        given:
        Project newProject = Project.build(version: 0, projectStatus: Status.DRAFT)
        Experiment experimentFrom = Experiment.build(experimentStatus: Status.APPROVED)
        Experiment experimentTo = Experiment.build(experimentStatus: Status.DRAFT)
        ProjectSingleExperiment.build(project: newProject, experiment: experimentFrom)
        ProjectSingleExperiment.build(project: newProject, experiment: experimentTo)

        InlineEditableCommand inlineEditableCommand = new InlineEditableCommand(pk: newProject.id,
                version: newProject.version, name: newProject.name, value: Status.APPROVED.id)
        when:
        controller.editProjectStatus(inlineEditableCommand)
        then:
        assert response.status == HttpServletResponse.SC_BAD_REQUEST
        assert "Before you can approve this project, you must approve the following experiments: ${experimentTo.id}" == response.text
    }

    void 'test edit Project Status has retired experiments'() {
        given:
        Project updatedProject = Project.build(name: "My New Name", version: 1, lastUpdated: new Date(), projectStatus: Status.APPROVED)
        Project newProject = Project.build(version: 0, projectStatus: Status.DRAFT)
        Experiment experimentFrom = Experiment.build(experimentStatus: Status.APPROVED)
        Experiment experimentTo = Experiment.build(experimentStatus: Status.RETIRED)
        ProjectSingleExperiment.build(project: newProject, experiment: experimentFrom)
        ProjectSingleExperiment.build(project: newProject, experiment: experimentTo)

        InlineEditableCommand inlineEditableCommand = new InlineEditableCommand(pk: newProject.id,
                version: newProject.version, name: newProject.name, value: Status.APPROVED.id)
        when:
        controller.editProjectStatus(inlineEditableCommand)
        then:
        controller.projectService.updateProjectStatus(_, _) >> { return updatedProject }
        assert response.status == HttpServletResponse.SC_OK
        ObjectMapper mapper = new ObjectMapper();
        JsonNode responseJSON = mapper.readValue(response.text, JsonNode.class);

        assert responseJSON.get("version").asText() == "0"
        assert responseJSON.get("data").asText() == updatedProject.projectStatus.id
        assert responseJSON.get("lastUpdated").asText()
        assert response.contentType == "text/json;charset=utf-8"
    }

    void 'test edit Project Status - access denied'() {
        given:
        accessDeniedRoleMock()
        Project newProject = Project.build(version: 0, projectStatus: Status.DRAFT)  //no designer
        Project updatedProject = Project.build(name: "My New Name", version: 1, lastUpdated: new Date(), projectStatus: Status.APPROVED)
        InlineEditableCommand inlineEditableCommand = new InlineEditableCommand(pk: newProject.id,
                version: newProject.version, name: newProject.name, value: updatedProject.projectStatus.id)
        when:
        controller.editProjectStatus(inlineEditableCommand)
        then:
        controller.projectService.updateProjectStatus(_, _) >> { throw new AccessDeniedException("msg") }
        assertAccesDeniedErrorMessage()
    }

    void 'test edit Project Status with errors'() {
        given:
        Project newProject = Project.build(version: 0, projectStatus: Status.APPROVED)
        InlineEditableCommand inlineEditableCommand =
            new InlineEditableCommand(pk: newProject.id, version: newProject.version, name: newProject.name, value: Status.APPROVED.id)
        controller.metaClass.message = { Map p -> return "foo" }

        when:
        controller.editProjectStatus(inlineEditableCommand)
        then:
        controller.projectService.updateProjectStatus(_, _) >> { throw new Exception("") }
        assertEditingErrorMessage()
    }

    void 'test edit Project owner success'() {
        given:
        Project newProject = Project.build(version: 0, name: "My Name", ownerRole: this.role)  //no designer


        Project updatedProject = Project.build(name: "My New Name", version: 1, lastUpdated: new Date(), ownerRole: this.otherRole)
        InlineEditableCommand inlineEditableCommand = new InlineEditableCommand(pk: newProject.id,
                version: newProject.version, name: newProject.name, value: updatedProject.ownerRole.displayName)
        SpringSecurityUtils.metaClass.'static'.SpringSecurityUtils.getPrincipalAuthorities = {
            return [this.role, this.otherRole]
        }
        when:
        controller.editOwnerRole(inlineEditableCommand)
        then:
        controller.projectService.updateOwnerRole(_, _) >> { return updatedProject }
        assert response.status == HttpServletResponse.SC_OK
        ObjectMapper mapper = new ObjectMapper();
        JsonNode responseJSON = mapper.readValue(response.text, JsonNode.class);

        assert responseJSON.get("version").asText() == "0"
        assert responseJSON.get("data").asText() == updatedProject.ownerRole.displayName
        assert responseJSON.get("lastUpdated").asText()
        assert response.contentType == "text/json;charset=utf-8"
    }

    void 'test edit Project owner new role not in list - fail'() {
        given:
        SpringSecurityUtils.metaClass.'static'.ifAnyGranted = { String role ->
            return false
        }
        Project newProject = Project.build(version: 0, name: "My Name", ownerRole: this.role)  //no designer

        Role notInUsersRole = Role.build(authority: "ROLE_TEAM_C", displayName: "displayName");
        Project updatedProject = Project.build(name: "My New Name", version: 1, lastUpdated: new Date(), ownerRole: notInUsersRole)
        InlineEditableCommand inlineEditableCommand = new InlineEditableCommand(pk: newProject.id,
                version: newProject.version, name: newProject.name, value: updatedProject.ownerRole.displayName)
        when:
        controller.editOwnerRole(inlineEditableCommand)
        then:
        assert response.status == HttpServletResponse.SC_BAD_REQUEST

    }

    void 'test edit Project owner - access denied'() {
        given:
        accessDeniedRoleMock()
        Project newProject = Project.build(version: 0, name: "My Name", ownerRole: this.role)
        Project updatedProject = Project.build(name: "My New Name", version: 1, lastUpdated: new Date(), ownerRole: this.otherRole)
        InlineEditableCommand inlineEditableCommand = new InlineEditableCommand(pk: newProject.id,
                version: newProject.version, name: newProject.name, value: updatedProject.ownerRole.displayName)

        SpringSecurityUtils.metaClass.'static'.SpringSecurityUtils.getPrincipalAuthorities = {
            return [this.role, this.otherRole]
        }
        when:
        controller.editOwnerRole(inlineEditableCommand)
        then:
        controller.projectService.updateOwnerRole(_, _) >> { throw new AccessDeniedException("msg") }
        assertAccesDeniedErrorMessage()
    }

    void 'test edit Project Name success'() {
        given:
        Project newProject = Project.build(version: 0, name: "My Name")  //no designer
        Project updatedProject = Project.build(name: "My New Name", version: 1, lastUpdated: new Date())
        InlineEditableCommand inlineEditableCommand = new InlineEditableCommand(pk: newProject.id,
                version: newProject.version, name: newProject.name, value: updatedProject.name)
        when:
        controller.editProjectName(inlineEditableCommand)
        then:
        controller.projectService.updateProjectName(_, _) >> { return updatedProject }
        assert response.status == HttpServletResponse.SC_OK
        ObjectMapper mapper = new ObjectMapper();
        JsonNode responseJSON = mapper.readValue(response.text, JsonNode.class);

        assert responseJSON.get("version").asText() == "0"
        assert responseJSON.get("data").asText() == updatedProject.name
        assert responseJSON.get("lastUpdated").asText()
        assert response.contentType == "text/json;charset=utf-8"
    }

    void 'test edit Project Name - access denied'() {
        given:
        accessDeniedRoleMock()
        Project newProject = Project.build(version: 0, name: "My Name")  //no designer
        Project updatedProject = Project.build(name: "My New Name", version: 1, lastUpdated: new Date())
        InlineEditableCommand inlineEditableCommand = new InlineEditableCommand(pk: newProject.id,
                version: newProject.version, name: newProject.name, value: updatedProject.name)
        when:
        controller.editProjectName(inlineEditableCommand)
        then:
        controller.projectService.updateProjectName(_, _) >> { throw new AccessDeniedException("msg") }
        assertAccesDeniedErrorMessage()
    }

    void 'test edit Project Name with errors'() {
        given:
        Project newProject = Project.build(version: 0)
        InlineEditableCommand inlineEditableCommand = new InlineEditableCommand(pk: newProject.id, version: newProject.version, name: newProject.name)
        controller.metaClass.message = { Map p -> return "foo" }

        when:
        controller.editProjectName(inlineEditableCommand)
        then:
        controller.projectService.updateProjectName(_, _) >> { throw new Exception("") }
        assertEditingErrorMessage()
    }


    void 'test edit Project Description success'() {
        given:
        Project newProject = Project.build(version: 0, name: "My Name", description: "My Description")  //no designer
        Project updatedProject = Project.build(description: "My New Description", name: "My New Name", version: 1, lastUpdated: new Date())
        InlineEditableCommand inlineEditableCommand = new InlineEditableCommand(pk: newProject.id,
                version: newProject.version, name: newProject.name, value: updatedProject.description)
        when:
        controller.editDescription(inlineEditableCommand)
        then:
        controller.projectService.updateProjectDescription(_, _) >> { return updatedProject }
        assert response.status == HttpServletResponse.SC_OK
        ObjectMapper mapper = new ObjectMapper();
        JsonNode responseJSON = mapper.readValue(response.text, JsonNode.class);

        assert responseJSON.get("version").asText() == "0"
        assert responseJSON.get("data").asText() == updatedProject.description
        assert responseJSON.get("lastUpdated").asText()
        assert response.contentType == "text/json;charset=utf-8"
    }

    void 'test edit Project Description - access denied'() {
        given:
        accessDeniedRoleMock()
        Project newProject = Project.build(version: 0, name: "My Name", description: "My Description")  //no designer
        Project updatedProject = Project.build(description: "My New Description", name: "My New Name", version: 1, lastUpdated: new Date())
        InlineEditableCommand inlineEditableCommand = new InlineEditableCommand(pk: newProject.id,
                version: newProject.version, name: newProject.name, value: updatedProject.description)
        when:
        controller.editDescription(inlineEditableCommand)
        then:
        controller.projectService.updateProjectDescription(_, _) >> { throw new AccessDeniedException("msg") }
        assertAccesDeniedErrorMessage()
    }

    void 'test edit Project Description with errors'() {
        given:
        Project newProject = Project.build(version: 0)
        InlineEditableCommand inlineEditableCommand = new InlineEditableCommand(pk: newProject.id,
                version: newProject.version, name: newProject.name, value: newProject.description)
        controller.metaClass.message = { Map p -> return "foo" }

        when:
        controller.editDescription(inlineEditableCommand)
        then:
        controller.projectService.updateProjectDescription(_, _) >> { throw new Exception("") }
        assertEditingErrorMessage()
    }

    void 'test projectStages'() {

        when:
        controller.projectStages()

        then:
        assert response.text
    }

    void 'test projectStatus only has Approved and Retired '() {
        when:
        controller.projectStatus()

        then:
        final String responseAsString = response.contentAsString
        responseAsString == '["Approved","Retired"]'
    }

    void 'test reloadProjectSteps'() {
        given:
        views['/project/_showstep.gsp'] = 'mock contents'
        controller.projectService = projectService
        when:
        controller.reloadProjectSteps(project.id)
        then:
        assert response.text == 'mock contents'
    }


    void 'test update stage for an Experiment  #desc'() {
        given:
        ProjectSingleExperiment projectExperimentFrom1 = ProjectSingleExperiment.build(project: project, experiment: Experiment.build())

        InlineEditableCommand inlineEditableCommand =
            new InlineEditableCommand(pk: projectExperimentFrom1.id, name: stage, value: projectExperimentTo.stage.label)
        when:
        controller.updateProjectStage(inlineEditableCommand)
        then:
        projectService.updateProjectStage(_, _, _) >> { return projectExperimentTo }
        assert response.text == expectedStage
        assert response.status == 200

        where:
        desc                                                        | stage  | expectedStage
        "ProjectSingleExperiment has null stage element ID"         | null   | "secondary assay"
        "ProjectSingleExperiment has stage ID that is not a number" | "name" | "secondary assay"
    }

    void 'test update stage for an Experiment  - access denied'() {
        given:
        accessDeniedRoleMock()
        ProjectSingleExperiment projectExperimentFrom1 = ProjectSingleExperiment.build(project: project, experiment: Experiment.build())

        InlineEditableCommand inlineEditableCommand =
            new InlineEditableCommand(pk: projectExperimentFrom1.id, name: stage, value: projectExperimentTo.stage.label)
        when:
        controller.updateProjectStage(inlineEditableCommand)
        then:
        projectService.updateProjectStage(_, _, _) >> { throw new AccessDeniedException("msg") }
        assertAccesDeniedErrorMessage()
        where:
        desc                                                        | stage  | expectedStage
        "ProjectSingleExperiment has null stage element ID"         | null   | "secondary assay"
        "ProjectSingleExperiment has stage ID that is not a number" | "name" | "secondary assay"
    }

    void 'test updateProjectStage change the stage'() {
        given:
        InlineEditableCommand inlineEditableCommand =
            new InlineEditableCommand(pk: projectExperimentFrom.id, name: projectExperimentFrom.stage.id, value: projectExperimentTo.stage.label)
        when:
        controller.updateProjectStage(inlineEditableCommand)
        then:
        projectService.updateProjectStage(_, _, _) >> { return projectExperimentTo }
        assert response.text == "secondary assay"
        assert response.status == 200
    }

    void 'test updateProjectStage change the stage - access denied'() {
        given:
        accessDeniedRoleMock()
        InlineEditableCommand inlineEditableCommand =
            new InlineEditableCommand(pk: projectExperimentFrom.id, name: projectExperimentFrom.stage.id, value: projectExperimentTo.stage.label)
        when:
        controller.updateProjectStage(inlineEditableCommand)
        then:
        projectService.updateProjectStage(_, _, _) >> { throw new AccessDeniedException("msg") }
        assertAccesDeniedErrorMessage()
    }

    void 'test updateProjectStage no change in stage'() {
        given:
        InlineEditableCommand inlineEditableCommand =
            new InlineEditableCommand(pk: projectExperimentFrom.id, name: projectExperimentFrom.stage.id, value: projectExperimentFrom.stage.label)
        when:
        controller.updateProjectStage(inlineEditableCommand)
        then:
        assert projectExperimentFrom.stage.id != projectExperimentTo.stage.id
        assert response.text == "primary assay"
        assert response.status == 200

    }

    void 'test updateProjectStage new stage not found'() {
        given:
        String label = "Some unknown stage"
        InlineEditableCommand inlineEditableCommand =
            new InlineEditableCommand(pk: projectExperimentFrom.id, name: projectExperimentFrom.stage.id, value: label)
        when:
        controller.updateProjectStage(inlineEditableCommand)
        then:
        assert projectExperimentFrom.stage.id != projectExperimentTo.stage.id
        assert response.text == "Could not find stage with label ${label}"
        assert response.status == 404

    }

    void 'test reload project fail {#description}'() {
        given:
        controller.projectService = projectService

        when:
        controller.reloadProjectSteps(null)
        then:
        assert response.text.startsWith(responsetext)

        where:
        description                          | responsetext
        "failed due to experiment not found" | "serviceError"
    }

    void 'test show'() {
        given:
        CapPermissionService capPermissionService = Mock(CapPermissionService)
        controller.capPermissionService = capPermissionService
        ProjectExperimentRenderService projectExperimentRenderService = Mock(ProjectExperimentRenderService)
        controller.projectExperimentRenderService = projectExperimentRenderService
        controller.queryService = Mock(IQueryService)

        when:
        params.id = project.id
        def model = controller.show()

        then:
        capPermissionService.getOwner(_) >> { 'owner' }
        projectExperimentRenderService.contructGraph(project) >> { new JSON() }
        model.instance == project
        model.pexperiment != null
    }

    void 'test show #desc'() {
        when:
        params.id = idClosure.call() // deferred execution so project is initialized
        def model = controller.show()

        then:
        flash?.message == expectedFlashMessage
        model?.instance == expectedProject.call()

        where:
        desc                  | idClosure      | expectedFlashMessage        | expectedProject
        'with bad id'         | { -100L }      | 'default.not.found.message' | { null }
        'with non numeric id' | { 'foo' }      | 'default.not.found.message' | { null }
        'with null id'        | { null }       | 'default.not.found.message' | { null }
        'with good id'        | { project.id } | null                        | { project }
    }

    void 'test remove experiment from project success'() {
        given:


        views['/project/_showstep.gsp'] = 'mock contents'

        when:
        params.experimentId = { experimentId }.call()
        params.projectid = project.id
        controller.projectService = projectService
        controller.removeExperimentFromProject(params.experimentId.call(), params.projectid)

        then:
        1 * projectService.removeExperimentFromProject(_, _)
        assert response.text == responsetext

        where:
        description | experimentId                 | responsetext
        "success"   | { projectExperimentFrom.id } | 'mock contents'
    }

    void 'test remove experiment from project -access denied'() {
        given:
        accessDeniedRoleMock()

        views['/project/_showstep.gsp'] = 'mock contents'

        when:
        params.experimentId = experimentId.call()
        params.projectid = project.id
        controller.projectService = projectService
        controller.removeExperimentFromProject(params.experimentId, params.projectid)

        then:
        1 * projectService.removeExperimentFromProject(_, _) >> { throw new AccessDeniedException("msg") }
        assertAccesDeniedErrorMessage()

        where:
        description | experimentId                 | responsetext
        "success"   | { projectExperimentFrom.id } | 'mock contents'
    }

    void 'test remove experiment from project fail {#description}'() {
        given:
        projectService.removeExperimentFromProject(_, _) >> { throw new UserFixableException() }

        when:
        params.experimentId = experimentId
        params.projectid = project.id
        controller.projectService = projectService

        controller.removeExperimentFromProject(params.experimentId, params.projectid)

        then:
        assert response.text.startsWith(responsetext)

        where:
        description                          | experimentId | responsetext
        "failed due to experiment not found" | -999         | "serviceError"
    }

    void 'test remove edge from project success'() {
        given:
        projectService.removeEdgeFromProject(_, _, _) >> {}
        views['/project/_showstep.gsp'] = 'mock contents'

        when:
        params.fromExperimentId = fromExperimentId.call()
        params.toExperimentId = toExperimentId.call()
        params.projectid = project.id
        controller.projectService = projectService

        controller.removeEdgeFromProject(params.fromExperimentId, params.toExperimentId, params.projectid)

        then:
        assert response.text == responsetext

        where:
        description | fromExperimentId             | toExperimentId             | responsetext
        "success"   | { projectExperimentFrom.id } | { projectExperimentTo.id } | 'mock contents'
    }

    void 'test remove edge from project - access denied'() {
        given:
        accessDeniedRoleMock()

        views['/project/_showstep.gsp'] = 'mock contents'

        when:
        params.fromExperimentId = fromExperimentId.call()
        params.toExperimentId = toExperimentId.call()
        params.projectid = project.id
        controller.projectService = projectService

        controller.removeEdgeFromProject(params.fromExperimentId, params.toExperimentId, params.projectid)

        then:
        projectService.removeEdgeFromProject(_, _, _) >> { throw new AccessDeniedException("msg") }
        assertAccesDeniedErrorMessage()

        where:
        description | fromExperimentId             | toExperimentId             | responsetext
        "success"   | { projectExperimentFrom.id } | { projectExperimentTo.id } | 'mock contents'
    }

    void 'test remove edge from project fail {#description}'() {
        given:
        projectService.removeEdgeFromProject(_, _, _) >> { throw new UserFixableException() }

        when:
        params.fromExperimentId = fromExperimentId.call()
        params.toExperimentId = toExperimentId.call()
        params.projectid = project.id
        controller.projectService = projectService

        controller.removeEdgeFromProject(params.fromExperimentId, params.toExperimentId, params.projectid)

        then:
        assert response.text.startsWith(responsetext)

        where:
        description                              | fromExperimentId             | toExperimentId             | responsetext
        "failed due to fromExperiment not found" | { -999 }                     | { projectExperimentTo.id } | 'serviceError'
        "failed due to toExperiment not found"   | { projectExperimentFrom.id } | { -999 }                   | 'serviceError'
    }

    void 'test link experiment with project success'() {
        given:
        projectService.linkProjectExperiment(_, _, _) >> {}
        views['/project/_showstep.gsp'] = 'mock contents'

        when:
        params.fromExperimentId = fromExperimentId.call()
        params.toExperimentId = toExperimentId.call()
        params.projectid = project.id
        controller.projectService = projectService

        controller.linkExperiment(params.fromExperimentId, params.toExperimentId, params.projectid)

        then:
        assert response.text == responsetext

        where:
        description | fromExperimentId             | toExperimentId             | responsetext
        "success"   | { projectExperimentFrom.id } | { projectExperimentTo.id } | 'mock contents'
    }

    void 'test link experiment with project - access denied'() {
        given:
        accessDeniedRoleMock()

        views['/project/_showstep.gsp'] = 'mock contents'

        when:
        params.fromExperimentId = fromExperimentId.call()
        params.toExperimentId = toExperimentId.call()
        params.projectid = project.id
        controller.projectService = projectService

        controller.linkExperiment(params.fromExperimentId, params.toExperimentId, params.projectid)

        then:
        projectService.linkProjectExperiment(_, _, _) >> { throw new AccessDeniedException("msg") }
        assertAccesDeniedErrorMessage()


        where:
        description | fromExperimentId             | toExperimentId             | responsetext
        "success"   | { projectExperimentFrom.id } | { projectExperimentTo.id } | 'mock contents'
    }


    void 'test link experiment with project fail {#description}'() {
        given:
        projectService.linkProjectExperiment(_, _, _) >> { throw new UserFixableException('serviceError') }

        when:
        params.fromExperimentId = fromExperimentId.call()
        params.toExperimentId = toExperimentId.call()
        params.projectid = project.id
        controller.projectService = projectService

        controller.linkExperiment(params.fromExperimentId, params.toExperimentId, params.projectid)

        then:
        assert response?.text == 'serviceError'
        where:
        description                              | fromExperimentId             | toExperimentId
        "failed due to fromExperiment not found" | { -999L }                    | { projectExperimentTo.id }
        "failed due to toExperiment not found"   | { projectExperimentFrom.id } | { -999L }
    }

    void 'test link experiment with project fail - Bad request {#description}'() {
        given:
        controller.projectService = projectService

        when:

        controller.linkExperiment(fromExperimentId.call(), toExperimentId.call(), project.id)

        then:
        assert response.text == "Both 'From Experiment ID' and 'To Experiment ID' are required"
        assert response.status == HttpServletResponse.SC_BAD_REQUEST
        where:
        description                              | fromExperimentId             | toExperimentId
        "failed due to fromExperiment not found" | { null }                     | { projectExperimentTo.id }
        "failed due to toExperiment not found"   | { projectExperimentFrom.id } | { null }
    }




    void 'test getProjectNames'() {
        when:
        params.term = project.name
        controller.getProjectNames()

        then:
        assert response.text.contains(project.name)
    }


    void "test show Experiments To Add Project With ADID Type #desc"() {
        given:
        AssociateExperimentsCommand associateExperimentsCommand = new AssociateExperimentsCommand(fromAddPage: fromPage, idType: idType)
        associateExperimentsCommand.validate()

        when:
        controller.showExperimentsToAddProject(associateExperimentsCommand)
        then:
        assert associateExperimentsCommand.hasErrors() == hasErrors
        where:
        desc                               | idType      | hasErrors | fromPage
        "No Errors From Show Project Page" | IdType.ADID | true      | true
        "Errors From Add Experiment Page"  | IdType.EID  | false     | false
    }


    void "test show Experiments To Add Project with #desc"() {

        given:

        Assay assay = Assay.build(ownerRole: role)
        Project project = Project.build(ownerRole: role)
        Experiment.build(assay: assay, experimentStatus: experimentStatus, ownerRole: role)
        String sourceEntityIds = assay.id.toString()
        AssociateExperimentsCommand associateExperimentsCommand =
            new AssociateExperimentsCommand(fromAddPage: true,
                    projectId: project.id,
                    idType: idType,
                    sourceEntityIds: sourceEntityIds,
                    mergeAssayDefinitionService: Mock(MergeAssayDefinitionService),
                    projectService: Mock(ProjectService))

        when:
        def model = controller.showExperimentsToAddProject(associateExperimentsCommand)
        then:
        associateExperimentsCommand.mergeAssayDefinitionService.convertIdToEntity(_, _) >> { assay }
        associateExperimentsCommand.projectService.isExperimentAssociatedWithProject(_, _) >> { isAlreadyAssociatedToProject }
        assert model.command
        AssociateExperimentsCommand command = model.command
        assert command.availableExperiments.size() == expectedNumExperiments
        assert command.errorMessages == errorMessages

        where:
        desc                                                           | idType      | experimentStatus          | errorMessages | expectedNumExperiments | isAlreadyAssociatedToProject
        "ADID with approved experiment"                                | IdType.ADID | Status.APPROVED | []            | 1                      | false
        "ADID with approved experiment, already associated to Project" | IdType.ADID | Status.APPROVED | []            | 0                      | true
        "ADID with retired experiment"                                 | IdType.ADID | Status.RETIRED  | []            | 0                      | false
    }

    void "test show Experiments To Add Project #desc"() {

        given:
        AssociateExperimentsCommand associateExperimentsCommand = new AssociateExperimentsCommand(fromAddPage: fromPage)

        when:
        def model = controller.showExperimentsToAddProject(associateExperimentsCommand)
        then:
        assert model.command
        AssociateExperimentsCommand command = model.command
        assert command.hasErrors() == hasErrors

        where:
        desc                               | idType     | hasErrors | fromPage
        "No Errors From Show Project Page" | IdType.EID | false     | false
        "Errors From Add Experiment Page"  | IdType.EID | true      | true
    }



    void 'test show Experiments To Add Project - Runtime Exception'() {
        given:
        accessDeniedRoleMock()
        Assay assay = Assay.build(ownerRole: role)
        Project project = Project.build(ownerRole: role)
        Experiment experiment = Experiment.build(assay: assay, experimentStatus: Status.APPROVED, ownerRole: role)
        String sourceEntityIds = experiment.id.toString()

        AssociateExperimentsCommand associateExperimentsCommand =
            new AssociateExperimentsCommand(fromAddPage: true,
                    projectId: project.id,
                    idType: IdType.EID,
                    sourceEntityIds: sourceEntityIds,
                    mergeAssayDefinitionService: Mock(MergeAssayDefinitionService),
                    projectService: Mock(ProjectService))
        when:
        def model = controller.showExperimentsToAddProject(associateExperimentsCommand)
        then:
        associateExperimentsCommand.mergeAssayDefinitionService.convertIdToEntity(_, _) >> { experiment }
        associateExperimentsCommand.projectService.addExperimentToProject(_, _, _) >> { throw new RuntimeException("msg") }

        assert model.command.errorMessages == ["An error has occurred, Please log an issue with the BARD team at bard-users@broadinstitute.org to fix this issue"]
    }

    void 'test show Experiments To Add Project - UserFixableException'() {
        given:
        accessDeniedRoleMock()
        Assay assay = Assay.build(ownerRole: role)
        Project project = Project.build(ownerRole: role)
        Experiment experiment = Experiment.build(assay: assay, experimentStatus: Status.APPROVED, ownerRole: role)
        String sourceEntityIds = experiment.id.toString()

        AssociateExperimentsCommand associateExperimentsCommand =
            new AssociateExperimentsCommand(fromAddPage: true,
                    projectId: project.id,
                    idType: IdType.EID,
                    sourceEntityIds: sourceEntityIds,
                    mergeAssayDefinitionService: Mock(MergeAssayDefinitionService),
                    projectService: Mock(ProjectService))
        when:
        def model = controller.showExperimentsToAddProject(associateExperimentsCommand)
        then:
        associateExperimentsCommand.mergeAssayDefinitionService.convertIdToEntity(_, _) >> { experiment }
        associateExperimentsCommand.projectService.addExperimentToProject(_, _, _) >> { throw new UserFixableException("msg") }

        assert model.command.errorMessages == ["msg"]
    }

    void 'test show Experiments To Add Project - access denied'() {
        given:
        accessDeniedRoleMock()
        Assay assay = Assay.build(ownerRole: role)
        Project project = Project.build(ownerRole: role)
        Experiment experiment = Experiment.build(assay: assay, experimentStatus: Status.APPROVED, ownerRole: role)
        String sourceEntityIds = experiment.id.toString()

        AssociateExperimentsCommand associateExperimentsCommand =
            new AssociateExperimentsCommand(fromAddPage: true,
                    projectId: project.id,
                    idType: IdType.EID,
                    sourceEntityIds: sourceEntityIds,
                    mergeAssayDefinitionService: Mock(MergeAssayDefinitionService),
                    projectService: Mock(ProjectService))
        when:
        controller.showExperimentsToAddProject(associateExperimentsCommand)
        then:
        associateExperimentsCommand.mergeAssayDefinitionService.convertIdToEntity(_, _) >> { experiment }
        associateExperimentsCommand.projectService.addExperimentToProject(_, _, _) >> { throw new AccessDeniedException("msg") }
        assertAccesDeniedErrorMessage()
    }

    void 'test show Experiments To Add Project'() {
        given:
        Assay assay = Assay.build(ownerRole: role)
        Project project = Project.build(ownerRole: role)
        Experiment experiment = Experiment.build(assay: assay, experimentStatus: Status.APPROVED, ownerRole: role)
        String sourceEntityIds = experiment.id.toString()

        AssociateExperimentsCommand associateExperimentsCommand =
            new AssociateExperimentsCommand(fromAddPage: true,
                    projectId: project.id,
                    idType: IdType.EID,
                    sourceEntityIds: sourceEntityIds,
                    mergeAssayDefinitionService: Mock(MergeAssayDefinitionService),
                    projectService: Mock(ProjectService))
        when:
        controller.showExperimentsToAddProject(associateExperimentsCommand)
        then:
        associateExperimentsCommand.mergeAssayDefinitionService.convertIdToEntity(_, _) >> { experiment }
        associateExperimentsCommand.projectService.addExperimentToProject(_, _, _) >> {}
        assert response.status == HttpServletResponse.SC_FOUND
        assert response.redirectedUrl == "/project/show/${project.id}#experiment-and-step-header"
    }

}
