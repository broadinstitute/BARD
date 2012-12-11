package bard.db.project

import spock.lang.Specification
import grails.test.mixin.TestFor
import grails.buildtestdata.mixin.Build
import org.junit.Before

/**
 * Created with IntelliJ IDEA.
 * User: xiaorong
 * Date: 12/9/12
 * Time: 8:30 PM
 * To change this template use File | Settings | File Templates.
 */
@TestFor(ProjectController)
@Build(Project)
class ProjectControllerUnitSpec extends Specification {
    Project project
    @Before
    void setup() {
        project = Project.build()
        assert project.validate()
    }

    void 'test show'() {
        given:
        defineBeans {
            projectExperimentRenderService(ProjectExperimentRenderService)
        }
        when:
        params.id = project.id
        def model = controller.show()

        then:
        model.instance == project
        model.pexperiment != null
    }
}
