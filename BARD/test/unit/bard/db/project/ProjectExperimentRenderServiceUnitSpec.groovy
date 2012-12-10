package bard.db.project

import static org.junit.Assert.*

import grails.test.mixin.*
import grails.test.mixin.support.*
import org.junit.*
import spock.lang.Specification
import grails.buildtestdata.mixin.Build
import spock.lang.Unroll

/**
 * Created with IntelliJ IDEA.
 * User: xiaorong
 * Date: 12/9/12
 * Time: 8:30 PM
 * To change this template use File | Settings | File Templates.
 */
@TestFor(ProjectExperimentRenderService)
@Build([Project, ProjectExperiment, ProjectStep])
@Unroll
class ProjectExperimentRenderServiceUnitSpec extends Specification{
    ProjectExperimentRenderService renderService = new ProjectExperimentRenderService()

    void "test isIsolatedNode #desc"(){
        given: "an "

        ProjectExperiment pe = ProjectExperiment.build(followingProjectSteps: createProjectSteps(numberOfFollowingSteps),
                precedingProjectSteps: createProjectSteps(numberOfPrecedingSteps))
        when:
        renderService.isIsolatedNode(pe)

        then:
        assert isIsolatedNode == renderService.isIsolatedNode(pe)

        where:
        desc            |numberOfFollowingSteps             |numberOfPrecedingSteps             |isIsolatedNode
        "No steps"      |0                                  |0                                  |true
        "Have preceding step"   |0                          |1                                  |false
        "Have following step"   |1                          |0                                  |false

    }

    // TODO more tests here

    def createProjectSteps(int i) {
        def steps = []
        i.times{
            steps.add(ProjectStep.build())
        }
        return steps
    }
}
