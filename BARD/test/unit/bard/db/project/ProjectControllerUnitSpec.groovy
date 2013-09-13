package bard.db.project

import acl.CapPermissionService
import bard.db.dictionary.Element
import bard.db.dictionary.StageTree
import bard.db.enums.ProjectGroupType
import bard.db.enums.ProjectStatus
import bard.db.experiment.Experiment
import bard.db.registration.AbstractInlineEditingControllerUnitSpec
import bard.db.registration.Assay
import bard.db.registration.EditingHelper
import bard.db.registration.ExternalReference
import bardqueryapi.IQueryService
import bardqueryapi.QueryService
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
import spock.lang.IgnoreRest
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
@Build([Project, ProjectExperiment, Experiment, ProjectStep, Element, ExternalReference, StageTree])
@Mock([Project, ProjectExperiment, Experiment, ProjectStep, Element, ExternalReference, StageTree])
@TestMixin(GrailsUnitTestMixin)
@Unroll
class ProjectControllerUnitSpec extends AbstractInlineEditingControllerUnitSpec {
    @Shared Project project
    @Shared ProjectExperiment projectExperimentFrom
    @Shared ProjectExperiment projectExperimentTo
    @Shared StageTree stageTree1
    @Shared StageTree stageTree2
    ProjectService projectService
    SpringSecurityService springSecurityService

    @Before
    void setup() {
        SpringSecurityUtils.metaClass.'static'.ifAnyGranted = { String role ->
            return true
        }
        controller.metaClass.mixin(EditingHelper)
        controller.springSecurityService = Mock(SpringSecurityService)
        controller.capPermissionService = Mock(CapPermissionService)
        project = Project.build()
        Element element1 = Element.build(label: "primary assay")
        Element element2 = Element.build(label: "secondary assay")
        stageTree1 = StageTree.build(element: element1)
        stageTree2 = StageTree.build(element: element2)
        Experiment experimentFrom = Experiment.build()
        Experiment experimentTo = Experiment.build()
        projectExperimentFrom = ProjectExperiment.build(project: project, experiment: experimentFrom, stage: element1)
        projectExperimentTo = ProjectExperiment.build(project: project, experiment: experimentTo, stage: element2)
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
        when:

        controller.save(projectCommand)
        then:
        response.status == expectedStatus
        where:
        desc                       | projectCommand                                                                                                                                | expectedStatus
        "Full Project"             | new ProjectCommand(name: "name", description: "description", projectStatus: ProjectStatus.APPROVED, projectGroupType: ProjectGroupType.PANEL) | HttpServletResponse.SC_FOUND
        "Invalid Project- No name" | new ProjectCommand(description: "description", projectStatus: ProjectStatus.APPROVED, projectGroupType: ProjectGroupType.PANEL)               | HttpServletResponse.SC_OK

    }

    void 'test edit Project Status success'() {
        given:
        Project newProject = Project.build(version: 0, projectStatus: ProjectStatus.DRAFT)  //no designer
        Project updatedProject = Project.build(name: "My New Name", version: 1, lastUpdated: new Date(), projectStatus: ProjectStatus.APPROVED)
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

    void 'test edit Project Status - access denied'() {
        given:
        accessDeniedRoleMock()
        Project newProject = Project.build(version: 0, projectStatus: ProjectStatus.DRAFT)  //no designer
        Project updatedProject = Project.build(name: "My New Name", version: 1, lastUpdated: new Date(), projectStatus: ProjectStatus.APPROVED)
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
        Project newProject = Project.build(version: 0, projectStatus: ProjectStatus.APPROVED)
        InlineEditableCommand inlineEditableCommand =
            new InlineEditableCommand(pk: newProject.id, version: newProject.version, name: newProject.name, value: ProjectStatus.APPROVED.id)
        controller.metaClass.message = { Map p -> return "foo" }

        when:
        controller.editProjectStatus(inlineEditableCommand)
        then:
        controller.projectService.updateProjectStatus(_, _) >> { throw new Exception("") }
        assertEditingErrorMessage()
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
        ProjectExperiment projectExperimentFrom1 = ProjectExperiment.build(project: project, experiment: Experiment.build())

        InlineEditableCommand inlineEditableCommand =
            new InlineEditableCommand(pk: projectExperimentFrom1.id, name: stage, value: projectExperimentTo.stage.label)
        when:
        controller.updateProjectStage(inlineEditableCommand)
        then:
        projectService.updateProjectStage(_, _, _) >> { return projectExperimentTo }
        assert response.text == expectedStage
        assert response.status == 200

        where:
        desc                                                  | stage  | expectedStage
        "ProjectExperiment has null stage element ID"         | null   | "secondary assay"
        "ProjectExperiment has stage ID that is not a number" | "name" | "secondary assay"
    }

    void 'test update stage for an Experiment  - access denied'() {
        given:
        accessDeniedRoleMock()
        ProjectExperiment projectExperimentFrom1 = ProjectExperiment.build(project: project, experiment: Experiment.build())

        InlineEditableCommand inlineEditableCommand =
            new InlineEditableCommand(pk: projectExperimentFrom1.id, name: stage, value: projectExperimentTo.stage.label)
        when:
        controller.updateProjectStage(inlineEditableCommand)
        then:
        projectService.updateProjectStage(_, _, _) >> { throw new AccessDeniedException("msg") }
        assertAccesDeniedErrorMessage()
        where:
        desc                                                  | stage  | expectedStage
        "ProjectExperiment has null stage element ID"         | null   | "secondary assay"
        "ProjectExperiment has stage ID that is not a number" | "name" | "secondary assay"
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
        projectService.linkExperiment(_, _, _) >> {}
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
        projectService.linkExperiment(_, _, _) >> { throw new AccessDeniedException("msg") }
        assertAccesDeniedErrorMessage()


        where:
        description | fromExperimentId             | toExperimentId             | responsetext
        "success"   | { projectExperimentFrom.id } | { projectExperimentTo.id } | 'mock contents'
    }


    void 'test link experiment with project fail {#description}'() {
        given:
        projectService.linkExperiment(_, _, _) >> { throw new UserFixableException('serviceError') }

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

    void 'test associate experiment with project success'() {
        given:
        views['/project/_showstep.gsp'] = 'mock contents'
        Element stage1 = Element.build()

        when:
        params.stageId = stage1.id.toString()
        request.setParameter('selectedExperiments[]', projectExperimentFrom.experiment.id + "-experiment name")
        params.projectId = project.id.toString()
        controller.projectService = projectService
        controller.associateExperimentsToProject()

        then:
        1 * projectService.addExperimentToProject(projectExperimentFrom.experiment, project.id, stage1) >> {}
        0 * projectService.addExperimentToProject(_, _, _) >> {}
        assert response.text == 'mock contents'
    }

    void 'test associate experiment with project - access denied'() {
        given:
        accessDeniedRoleMock()

        views['/project/_showstep.gsp'] = 'mock contents'

        Element stage1 = Element.build()

        when:
        params.stageId = stage1.id
        request.setParameter('selectedExperiments[]', projectExperimentFrom.experiment.id + "-experiment name")
        params.projectId = project.id
        controller.projectService = projectService

        controller.associateExperimentsToProject()

        then:
        projectService.addExperimentToProject(_, _, _) >> { throw new AccessDeniedException("msg") }
        assertAccesDeniedErrorMessage()
    }

    void 'test associate experiment with project fail'() {
        given:
        projectService.addExperimentToProject(_, _, _) >> { throw new UserFixableException() }

        Element stage1 = Element.build()

        when:
        params.stageId = stage1.id
        request.setParameter('selectedExperiments[]', "200-experiment name")
        params.projectId = project.id
        controller.projectService = projectService

        controller.associateExperimentsToProject()

        then:
        println(response.text)
        assert response.text.startsWith('serviceError')
    }

    void 'test ajaxFindAvailableExperimentByName'() {
        given:
        projectService.isExperimentAssociatedWithProject(_, _) >> { false }

        when:
        params.experimentName = projectExperimentFrom.experiment.experimentName
        params.projectid = project.id
        controller.projectService = projectService

        controller.ajaxFindAvailableExperimentByName(params.experimentName, params.projectid)

        then:
        assert response.text.contains(projectExperimentFrom.experiment.displayName)
    }

    void 'test ajaxFindAvailableExperimentByAssayId'() {
        given:
        projectService.isExperimentAssociatedWithProject(_, _) >> { false }
        Assay assay = Assay.build()
        Experiment ex = Experiment.build(assay: assay)
        assay.addToExperiments(ex)

        when:
        params.assayId = assay.id
        params.projectid = project.id
        controller.projectService = projectService

        controller.ajaxFindAvailableExperimentByAssayId(params.assayId, params.projectid)

        then:
        assert response.text.contains(ex.displayName)
    }

    void 'test ajaxFindAvailableExperimentById'() {
        given:
        projectService.isExperimentAssociatedWithProject(_, _) >> { false }
        Assay assay = Assay.build()
        Experiment ex = Experiment.build(assay: assay)
        assay.addToExperiments(ex)

        when:
        params.experimentId = ex.id
        params.projectid = project.id
        controller.projectService = projectService

        controller.ajaxFindAvailableExperimentById(params.experimentId, params.projectid)

        then:
        assert response.text.contains(ex.displayName)
    }

    void 'test editSummary'() {
        given:
        Assay assay = Assay.build()
        Experiment ex = Experiment.build(assay: assay)
        assay.addToExperiments(ex)
        views['/project/_summaryDetail.gsp'] = 'mock contents'

        when:
        params.experimentId = ex.id
        params.projectid = project.id
        controller.projectService = projectService

        controller.ajaxFindAvailableExperimentById(params.experimentId, params.projectid)

        then:
        assert response.text.contains(ex.displayName)
    }

//    void 'test showEditSummary'() {
//        given:
//        views['/project/_editSummary.gsp'] = 'mock editSummary page'
//
//        when:
//        params.instanceId = project.id
//        controller.projectService = projectService
//
//        controller.showEditSummary(params.instanceId)
//
//        then:
//        assert response.text == 'mock editSummary page'
//    }

    void 'test findById'() {
        when:
        params.projectId = project.id.toString()

        controller.findById()

        then:
        assert response.redirectedUrl == "/project/show/${project.id}"
    }

    void 'test findByName'() {
        when:
        params.projectName = project.name

        controller.findByName()

        then:
        assert response.redirectedUrl == "/project/show/${project.id}"
    }

    void 'test findByName with multiple matches'() {
        given:
        Project.build(name: project.name)
        when:
        params.projectName = project.name

        controller.findByName()

        then:
        assert view == "/project/findByName"
    }

    void 'test getProjectNames'() {
        when:
        params.term = project.name
        controller.getProjectNames()

        then:
        assert response.text.contains(project.name)
    }
}
