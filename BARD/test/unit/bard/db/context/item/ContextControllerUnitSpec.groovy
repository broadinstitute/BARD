package bard.db.context.item

import bard.db.ContextService
import bard.db.experiment.ExperimentContext
import bard.db.project.ProjectContext
import bard.db.registration.AssayContext
import bard.taglib.InPlaceEditTagLib
import grails.buildtestdata.mixin.Build
import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import grails.test.mixin.TestMixin
import grails.test.mixin.support.GrailsUnitTestMixin
import org.springframework.security.access.AccessDeniedException
import spock.lang.Specification
import spock.lang.Unroll

import javax.servlet.http.HttpServletResponse

/**
 * See the API for {@link grails.test.mixin.web.ControllerUnitTestMixin} for usage instructions
 */
@TestFor(ContextController)
@Build([ProjectContext, AssayContext, ExperimentContext])
@TestMixin(GrailsUnitTestMixin)
@Mock([InPlaceEditTagLib, ProjectContext, AssayContext, ExperimentContext])
@Unroll
class ContextControllerUnitSpec extends Specification {

    def setup() {
        controller.contextService = Mock(ContextService)
    }

    def cleanup() {
    }

    void 'test createCard- Bad Request'() {
        given:
        String contextClass = "ProjectContext"
        String cardName = "My Card"
        String cardSection = "My Group"

        when:
        controller.createCard(contextClass, null, cardName, cardSection)
        then:
        assert response.status == HttpServletResponse.SC_BAD_REQUEST
        assert response.text == "OwnerId is required"
        assert !model

    }

    void 'test createCard For Project -UnAuthorized'() {
        given:
        ProjectContext projectContext = ProjectContext.build()
        String contextClass = "ProjectContext"
        Long ownerId = projectContext.owner.id
        String cardName = "My Card"
        String cardSection = "My Group"
        when:
        controller.createCard(contextClass, ownerId, cardName, cardSection)
        then:
        controller.contextService.createProjectContext(_, _, _,_) >> { throw new AccessDeniedException("some message") }
        assert response.status == HttpServletResponse.SC_FORBIDDEN
        assert response.text == "editing.forbidden.message"
        assert !model

    }

    void 'test createCard For Experiment -UnAuthorized'() {
        given:
        ExperimentContext experimentContext = ExperimentContext.build()
        String contextClass = "ExperimentContext"
        Long ownerId = experimentContext.owner.id
        String cardName = "My Card"
        String cardSection = "My Group"
        when:
        controller.createCard(contextClass, ownerId, cardName, cardSection)
        then:
        controller.contextService.createExperimentContext(_, _, _,_) >> { throw new AccessDeniedException("some message") }
        assert response.status == HttpServletResponse.SC_FORBIDDEN
        assert response.text == "editing.forbidden.message"
        assert !model

    }

    void 'test createCard For Assay-UnAuthorized'() {
        given:
        AssayContext assayContext = AssayContext.build()
        String contextClass = "AssayContext"
        Long ownerId = assayContext.owner.id
        String cardName = "My Card"
        String cardSection = "My Group"
        when:
        controller.createCard(contextClass, ownerId, cardName, cardSection)
        then:
        controller.contextService.createAssayContext(_,_, _, _) >> { throw new AccessDeniedException("some message") }
        assert response.status == HttpServletResponse.SC_FORBIDDEN
        assert response.text == "editing.forbidden.message"
        assert !model

    }

    void 'test createCard- ProjectContext Success'() {
        given:
        ProjectContext projectContext = ProjectContext.build()
        InPlaceEditTagLib t = Mock(InPlaceEditTagLib)
        String contextClass = "ProjectContext"
        Long ownerId = projectContext.owner.id
        String cardName = "My Card"
        String cardSection = "My Group"
        views['/context/_list.gsp'] = 'mock contents'
        when:
        controller.createCard(contextClass, ownerId, cardName, cardSection)
        then:
        assert response.status == HttpServletResponse.SC_OK
        assert response.text == "mock contents"
    }

    void 'test createCard- ExperimentContext Success'() {
        given:
        ExperimentContext experimentContext = ExperimentContext.build()
        InPlaceEditTagLib t = Mock(InPlaceEditTagLib)
        String contextClass = "ExperimentContext"
        Long ownerId = experimentContext.owner.id
        String cardName = "My Card"
        String cardSection = "My Group"
        views['/context/_list.gsp'] = 'mock contents'
        when:
        controller.createCard(contextClass, ownerId, cardName, cardSection)
        then:
        assert response.status == HttpServletResponse.SC_OK
        assert response.text == "mock contents"
    }

    void 'test createCard- AssayContext Success'() {
        given:
        AssayContext assayContext = AssayContext.build()
        String contextClass = "AssayContext"
        Long ownerId = assayContext.owner.id
        String cardName = "My Card"
        String cardSection = "My Group"
        views['/context/_list.gsp'] = 'mock contents'
        when:
        controller.createCard(contextClass, ownerId, cardName, cardSection)
        then:
        assert response.status == HttpServletResponse.SC_OK
        assert response.text == "mock contents"
    }



    void 'test deleteEmptyCard For Project -UnAuthorized'() {
        given:
        ProjectContext projectContext = ProjectContext.build()
        String contextClass = "ProjectContext"
        when:
        controller.deleteEmptyCard(contextClass, projectContext.id, 'Unclassified')
        then:
        controller.contextService.deleteProjectContext(_,_, _) >> { throw new AccessDeniedException("some message") }
        assert response.status == HttpServletResponse.SC_FORBIDDEN
        assert response.text == "editing.forbidden.message"
        assert !model

    }

    void 'test deleteEmptyCard For Experiment -UnAuthorized'() {
        given:
        ExperimentContext experimentContext = ExperimentContext.build()
        String contextClass = "ExperimentContext"
        when:
        controller.deleteEmptyCard(contextClass, experimentContext.id, 'Unclassified')
        then:
        controller.contextService.deleteExperimentContext(_,_, _) >> { throw new AccessDeniedException("some message") }
        assert response.status == HttpServletResponse.SC_FORBIDDEN
        assert response.text == "editing.forbidden.message"
        assert !model

    }

    void 'test deleteEmptyCard For Assay-UnAuthorized'() {
        given:
        AssayContext assayContext = AssayContext.build()
        String contextClass = "AssayContext"
        when:
        controller.deleteEmptyCard(contextClass, assayContext.id, 'Unclassified')
        then:
        controller.contextService.deleteAssayContext(_,_, _) >> { throw new AccessDeniedException("some message") }
        assert response.status == HttpServletResponse.SC_FORBIDDEN
        assert response.text == "editing.forbidden.message"
        assert !model

    }

    void 'test deleteEmptyCard- ProjectContext Success'() {
        given:
        ProjectContext projectContext = ProjectContext.build()
        String contextClass = "ProjectContext"
        when:
        controller.deleteEmptyCard(contextClass, projectContext.id, 'Unclassified')
        then:
        assert response.status == HttpServletResponse.SC_FOUND
        assert response.redirectUrl == '/project/editContext/1?groupBySection=Unclassified'
    }

    void 'test deleteEmptyCard- ExperimentContext Success'() {
        given:
        ExperimentContext experimentContext = ExperimentContext.build()
        String contextClass = "ExperimentContext"
        when:
        controller.deleteEmptyCard(contextClass, experimentContext.id, 'Unclassified')
        then:
        assert response.status == HttpServletResponse.SC_FOUND
        assert response.redirectUrl == '/experiment/editContext/1?groupBySection=Unclassified'
    }

    void 'test deleteEmptyCard- AssayContext Success'() {
        given:
        AssayContext assayContext = AssayContext.build()
        String contextClass = "AssayContext"
        views['/context/_list.gsp'] = 'mock contents'
        when:
        controller.deleteEmptyCard(contextClass, assayContext.id, 'Unclassified')
        then:
        assert response.status == HttpServletResponse.SC_FOUND
        assert response.redirectUrl == "/assayDefinition/editContext/1?groupBySection=Unclassified"
    }

}