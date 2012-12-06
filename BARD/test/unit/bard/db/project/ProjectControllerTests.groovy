package bard.db.project



import grails.test.mixin.*
import org.junit.*
import bard.db.registration.Assay
import grails.buildtestdata.mixin.Build

/**
 * See the API for {@link grails.test.mixin.web.ControllerUnitTestMixin} for usage instructions
 */
@TestFor(ProjectController)
@Build(Project)
class ProjectControllerTests {

    void testShow() {
        controller.show()
        assert flash.message != null
        def project = Project.buildWithoutSave()
        assert project.save() != null
        params.id = project.id
        def model = controller.show()
        assert model.instance == project
    }
}
