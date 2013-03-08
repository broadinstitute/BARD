package bard.db.project

import grails.test.mixin.Mock
import grails.test.mixin.TestMixin
import grails.test.mixin.support.GrailsUnitTestMixin
import spock.lang.Specification
import grails.test.mixin.TestFor
import grails.buildtestdata.mixin.Build
import org.junit.Before
import bard.db.experiment.Experiment
import spock.lang.Shared
import bard.db.dictionary.StageElement
import bard.db.dictionary.Element
import bard.db.registration.Assay

/**
 * Created with IntelliJ IDEA.
 * User: xiaorong
 * Date: 12/9/12
 * Time: 8:30 PM
 * To change this template use File | Settings | File Templates.
 */
@TestFor(ProjectController)
@Build([Project, ProjectExperiment, Experiment, ProjectStep, Element])
@Mock([Project, ProjectExperiment, Experiment, ProjectStep, Element])
@TestMixin(GrailsUnitTestMixin)
class ProjectControllerUnitSpec extends Specification {
    @Shared Project project
    @Shared ProjectExperiment projectExperimentFrom
    @Shared ProjectExperiment projectExperimentTo

    ProjectService projectService
    @Before
    void setup() {
        project = Project.build()
        Experiment experimentFrom = Experiment.build()
        Experiment experimentTo = Experiment.build()
        projectExperimentFrom = ProjectExperiment.build(project: project, experiment: experimentFrom)
        projectExperimentTo = ProjectExperiment.build(project: project, experiment: experimentTo)
        ProjectStep projectStep = ProjectStep.build(previousProjectExperiment: projectExperimentFrom, nextProjectExperiment: projectExperimentTo)
        projectExperimentFrom.addToFollowingProjectSteps(projectStep)
        projectExperimentTo.addToPrecedingProjectSteps(projectStep)
        assert project.validate()
        defineBeans {
            projectExperimentRenderService(ProjectExperimentRenderService)
        }
        projectService = Mock()
    }

    void 'test show'() {
        when:
        params.id = project.id
        def model = controller.show()

        then:
        model.instance == project
        model.pexperiment != null
    }

    void 'test remove experiment from project success'() {
        given:


        views['/project/_showstep.gsp'] = 'mock contents'

        when:
        params.experimentId = experimentId
        params.projectid = project.id
        controller.projectService = projectService
        def model = controller.removeExperimentFromProject(params.experimentId, params.projectid)

        then:
        1 * projectService.removeExperimentFromProject(_,_)
        assert response.text == responsetext

        where:
        description | experimentId             | responsetext
        "success"   | projectExperimentFrom.id | 'mock contents'
    }

    void 'test remove experiment from project fail {#description}'() {
        given:
        projectService.removeExperimentFromProject(_, _) >> {throw new UserFixableException()}

        when:
        params.experimentId = experimentId
        params.projectid = project.id
        controller.projectService = projectService

        def model = controller.removeExperimentFromProject(params.experimentId, params.projectid)

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
        params.fromExperimentId = fromExperimentId
        params.toExperimentId = toExperimentId
        params.projectid = project.id
        controller.projectService = projectService

        def model = controller.removeEdgeFromProject(params.fromExperimentId, params.toExperimentId, params.projectid)

        then:
        assert response.text == responsetext

        where:
        description | fromExperimentId         | toExperimentId         | responsetext
        "success"   | projectExperimentFrom.id | projectExperimentTo.id | 'mock contents'
    }

    void 'test remove edge from project fail {#description}'() {
        given:
        projectService.removeEdgeFromProject(_, _, _) >> {throw new UserFixableException()}

        when:
        params.fromExperimentId = fromExperimentId
        params.toExperimentId = toExperimentId
        params.projectid = project.id
        controller.projectService = projectService

        def model = controller.removeEdgeFromProject(params.fromExperimentId, params.toExperimentId, params.projectid)

        then:
        assert response.text.startsWith(responsetext)

        where:
        description                              | fromExperimentId         | toExperimentId         | responsetext
        "failed due to fromExperiment not found" | -999                     | projectExperimentTo.id | 'serviceError'
        "failed due to toExperiment not found"   | projectExperimentFrom.id    | -999                   | 'serviceError'
    }

    void 'test link experiment with project success'() {
        given:
        projectService.linkExperiment(_, _, _) >> {}
        views['/project/_showstep.gsp'] = 'mock contents'

        when:
        params.fromExperimentId = fromExperimentId
        params.toExperimentId = toExperimentId
        params.projectid = project.id
        controller.projectService = projectService

        def model = controller.linkExperiment(params.fromExperimentId, params.toExperimentId, params.projectid)

        then:
        assert response.text == responsetext

        where:
        description | fromExperimentId         | toExperimentId         | responsetext
        "success"   | projectExperimentFrom.id | projectExperimentTo.id | 'mock contents'
    }

    void 'test link experiment with project fail {#description}'() {
        given:
        projectService.linkExperiment(_, _, _) >> {throw new UserFixableException()}

        when:
        params.fromExperimentId = fromExperimentId
        params.toExperimentId = toExperimentId
        params.projectid = project.id
        controller.projectService = projectService

        def model = controller.linkExperiment(params.fromExperimentId, params.toExperimentId, params.projectid)

        then:
        assert response.text.startsWith(responsetext)

        where:
        description                              | fromExperimentId         | toExperimentId         | responsetext
        "failed due to fromExperiment not found" | -999                     | projectExperimentTo.id | 'serviceError'
        "failed due to toExperiment not found"   | projectExperimentFrom.id    | -999                   | 'serviceError'
    }

    void 'test associate experiment with project success'() {
        given:
        projectService.addExperimentToProject(_,_,_) >> {}
        views['/project/_showstep.gsp'] = 'mock contents'

        Element stage1 = Element.build()

        when:
        params.stageId = stage1.id
        request.setParameter('selectedExperiments[]', projectExperimentFrom.experiment.id + "-experiment name")
        params.projectId = project.id
        controller.projectService = projectService

        def model = controller.associateExperimentsToProject()

        then:
        assert response.text == 'mock contents'
    }

    void 'test associate experiment with project fail'() {
        given:
        projectService.addExperimentToProject(_, _, _) >> {throw new UserFixableException()}

        Element stage1 = Element.build()

        when:
        params.stageId = stage1.id
        request.setParameter('selectedExperiments[]', projectExperimentFrom.experiment.id + "-experiment name")
        params.projectid = project.id
        controller.projectService = projectService

        def model = controller.associateExperimentsToProject()

        then:
        assert response.text.startsWith('serviceError')
    }

    void 'test ajaxFindAvailableExperimentByName'() {
        given:
        projectService.isExperimentAssociatedWithProject(_, _) >> {false}

        when:
        params.experimentName = projectExperimentFrom.experiment.experimentName
        params.projectid = project.id
        controller.projectService = projectService

        def model = controller.ajaxFindAvailableExperimentByName(params.experimentName, params.projectid)

        then:
        assert response.text.contains(projectExperimentFrom.experiment.displayName)
    }

    void 'test ajaxFindAvailableExperimentByAssayId'() {
        given:
        projectService.isExperimentAssociatedWithProject(_, _) >> {false}
        Assay assay = Assay.build()
        Experiment ex = Experiment.build(assay:assay)
        assay.addToExperiments(ex)

        when:
        params.assayId = assay.id
        params.projectid = project.id
        controller.projectService = projectService

        def model = controller.ajaxFindAvailableExperimentByAssayId(params.assayId, params.projectid)

        then:
        assert response.text.contains(ex.displayName)
    }

    void 'test ajaxFindAvailableExperimentById'() {
        given:
        projectService.isExperimentAssociatedWithProject(_, _) >> {false}
        Assay assay = Assay.build()
        Experiment ex = Experiment.build(assay:assay)
        assay.addToExperiments(ex)

        when:
        params.experimentId = ex.id
        params.projectid = project.id
        controller.projectService = projectService

        def model = controller.ajaxFindAvailableExperimentById(params.experimentId, params.projectid)

        then:
        assert response.text.contains(ex.displayName)
    }

    void 'test editSummary'() {
        given:
        Assay assay = Assay.build()
        Experiment ex = Experiment.build(assay:assay)
        assay.addToExperiments(ex)
        views['/project/_summaryDetail.gsp'] = 'mock contents'

        when:
        params.experimentId = ex.id
        params.projectid = project.id
        controller.projectService = projectService

        def model = controller.ajaxFindAvailableExperimentById(params.experimentId, params.projectid)

        then:
        assert response.text.contains(ex.displayName)
    }

    void 'test showEditSummary'() {
        given:
        views['/project/_editSummary.gsp'] = 'mock editSummary page'

        when:
        params.instanceId = project.id
        controller.projectService = projectService

        def model = controller.showEditSummary(params.instanceId)

        then:
        assert response.text == 'mock editSummary page'
    }

    void 'test findById'() {
        when:
        params.projectId = project.id.toString()

        def model = controller.findById()

        then:
        assert response.redirectedUrl == "/project/show/${project.id}"
    }

    void 'test findByName'() {
        when:
        params.projectName = project.name

        def model = controller.findByName()

        then:
        assert response.redirectedUrl == "/project/show/${project.id}"
    }

    void 'test findByName with multiple matches'() {
        given:
        Project project1 = Project.build(name: project.name)
        when:
        params.projectName = project.name

        def model = controller.findByName()

        then:
        assert view == "/project/findByName"
    }

    void 'test getProjectNames'() {
        when:
        params.term = project.name
        def model = controller.getProjectNames()

        then:
        assert response.text.contains(project.name)
    }
}
