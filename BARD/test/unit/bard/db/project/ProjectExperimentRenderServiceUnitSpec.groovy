package bard.db.project

import spock.lang.IgnoreRest

import static org.junit.Assert.*

import grails.test.mixin.*
import grails.test.mixin.support.*
import org.junit.*
import spock.lang.Specification
import grails.buildtestdata.mixin.Build
import spock.lang.Unroll
import bard.db.experiment.Experiment
import bard.db.registration.Assay
import bard.db.dictionary.Element
import bard.db.registration.ExternalReference

/**
 * Created with IntelliJ IDEA.
 * User: xiaorong
 * Date: 12/9/12
 * Time: 8:30 PM
 * To change this template use File | Settings | File Templates.
 */
@TestFor(ProjectExperimentRenderService)
@Build([Project, ProjectSingleExperiment, ProjectStep, Experiment, Assay, Element, ExternalReference])
@Mock([Project, ProjectSingleExperiment, ProjectStep, Experiment, Assay, Element, ExternalReference])
@Unroll
class ProjectExperimentRenderServiceUnitSpec extends Specification {
    ProjectExperimentRenderService renderService = new ProjectExperimentRenderService()

    void "test process project" () {
        given: "an project"
        Experiment e1 = Experiment.build(id: 1)
        Experiment e2 = Experiment.build(id: 2)
        Experiment e3 = Experiment.build(id: 3)
        Experiment e4 = Experiment.build(id: 4)
        Project p = Project.build(id: 1)
        ProjectSingleExperiment pe1 = ProjectSingleExperiment.build(project: p, experiment: e1)
        ProjectSingleExperiment pe2 = ProjectSingleExperiment.build(project: p, experiment: e2)
        ProjectSingleExperiment pe3 = ProjectSingleExperiment.build(project: p, experiment: e3)
        ProjectSingleExperiment pe4 = ProjectSingleExperiment.build(project: p, experiment: e4)
        ProjectStep ps12 = ProjectStep.build(previousProjectExperiment: pe1, nextProjectExperiment: pe2)
        ProjectStep ps13 = ProjectStep.build(previousProjectExperiment: pe1, nextProjectExperiment: pe3)

        pe1.addToFollowingProjectSteps(ps12)
        pe1.addToFollowingProjectSteps(ps13)
        pe2.addToPrecedingProjectSteps(ps12)
        pe3.addToPrecedingProjectSteps(ps13)

        p.addToProjectExperiments(pe1)
        p.addToProjectExperiments(pe2)
        p.addToProjectExperiments(pe3)
        p.addToProjectExperiments(pe4)

        when:
        def result = renderService.processProject(p)
        def nodes = []
        nodes << renderService.constructNode(pe1)
        nodes << renderService.constructNode(pe2)
        nodes << renderService.constructNode(pe3)
        def isolated = []
        isolated << renderService.constructNode(pe4)
        def edges = []
        edges << new Edge(from: 1, to: 2, label: ps12.edgeName)
        edges << new Edge(from: 1, to: 3, label: ps13.edgeName)
        def expectedResult = ["connectedNodes": nodes, "edges": edges, "isolatedNodes": isolated]

        then:
        assert result.toString() == expectedResult.toString()
    }

    void "test isIsolatedNode #desc"() {
        given: "an "

        ProjectSingleExperiment pe = ProjectSingleExperiment.build(followingProjectSteps: createProjectSteps(numberOfFollowingSteps),
                precedingProjectSteps: createProjectSteps(numberOfPrecedingSteps))
        when:
        def result = renderService.isIsolatedNode(pe)

        then:
        assert isIsolatedNode == result

        where:
        desc                  | numberOfFollowingSteps | numberOfPrecedingSteps | isIsolatedNode
        "No steps"            | 0                      | 0                      | true
        "Have preceding step" | 0                      | 1                      | false
        "Have following step" | 1                      | 0                      | false

    }

    void "test constructNode #desc"() {

        given: "an "

        ProjectSingleExperiment pe = ProjectSingleExperiment.build(id: 1, experiment: Experiment.build(id: 2, experimentName: "experimentName", assay: Assay.build(id: 3)),
                followingProjectSteps: createProjectSteps(0),
                precedingProjectSteps: createProjectSteps(0),
                stage: Element.build(label: "stageLabel")
        )
        when:
        Node node = renderService.constructNode(pe)

        then:
        assert id == node.id
        assert eid == node.keyValues["eid"]
        assert assay == node.keyValues["assay"]
        assert ename == node.keyValues["ename"]
        assert stage == node.keyValues["stage"]

        where:
        desc             | id  | eid | assay | ename            | stage
        "construct node" | "1" | 2   | 3     | "experimentName" | "stageLabel"
    }

    void "testContructEdge #desc"() {
        given: "an "

        ProjectStep st = ProjectStep.build()
        List<Long> processingQ = []

        when:
        Edge edge = renderService.constructEdge(st, processingQ)

        then:
        assert edge == new Edge(from: st?.previousProjectExperiment?.id, to: st?.nextProjectExperiment?.id, label: st?.edgeName)
        processingQ.size == 2

    }

    void "test constructEdges #desc"() {
        given: "an "

        ProjectSingleExperiment pe = ProjectSingleExperiment.build(followingProjectSteps: createProjectSteps(numberOfFollowingSteps),
                precedingProjectSteps: createProjectSteps(numberOfPrecedingSteps))
        List<Long> processingQ = []
        when:
        def edges = renderService.constructEdges(pe, processingQ)

        then:
        assert edges.size() == numberOfEdges
        assert processingQ.size() == sizeOfQ

        where:
        desc                                         | numberOfFollowingSteps | numberOfPrecedingSteps | numberOfEdges | sizeOfQ
        "No steps"                                   | 0                      | 0                      | 0             | 0
        "Have 1 preceding step"                      | 0                      | 1                      | 1             | 2
        "Have 1 following step"                      | 1                      | 0                      | 1             | 2
        "Have 1 following step and 1 preceding stpe" | 1                      | 1                      | 2             | 4

    }

    void "test count incoming and outgoing edges for nodes"(){
        given: "an"
        Set<Edge> edges = new HashSet<Edge>()
        edges.add(new Edge(from: "1", to: "2"))
        edges.add(new Edge(from: "1", to: "3"))
        edges.add(new Edge(from: "1", to: "4"))

        def nodes = []
        nodes.add(new Node(id: "1", keyValues: [incount:0, outcount:0]))
        nodes.add(new Node(id: "2", keyValues: [incount:0, outcount:0]))
        nodes.add(new Node(id: "3", keyValues: [incount:0, outcount:0]))
        nodes.add(new Node(id: "4", keyValues: [incount:0, outcount:0]))
        when:
        renderService.countInOutEdges(edges, nodes)
        then:
        nodes.each{Node node->
            if (node.id == "1") {assert node.keyValues.incount == '0'; assert node.keyValues.outcount == '3'}
            if (node.id == "2") {assert node.keyValues.incount == '1'; assert node.keyValues.outcount == '0'}
            if (node.id == "3") {assert node.keyValues.incount == '1'; assert node.keyValues.outcount == '0'}
            if (node.id == "4") {assert node.keyValues.incount == '1'; assert node.keyValues.outcount == '0'}
        }

    }

    void "test get aid by experiment"() {
        given: "an"
        Experiment experiment = Experiment.build()
        ExternalReference externalReference = ExternalReference.build(experiment: experiment, extAssayRef: "aid=1234")
        when:
        String aid = renderService.getAidByExperiment(externalReference.experiment)
        then:
        assert aid == "1234"
    }

    def createProjectSteps(int i) {
        def steps = []
        i.times {
            steps.add(ProjectStep.build())
        }
        return steps
    }
}
