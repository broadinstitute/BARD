package bard.db.context.item

import bard.db.ContextService
import bard.db.enums.ContextType
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
@Mock([ProjectContext, AssayContext, ExperimentContext])
@Unroll
class ContextControllerUnitSpec extends Specification {

    def setup() {
        controller.contextService = Mock(ContextService)
    }

    void 'test createCard Bad Request for #contextClass.simpleName'() {
        given:
        final String contextClassName = contextClass.simpleName

        when:
        controller.createCard(contextClassName, null, "My Card", ContextType.BIOLOGY.id)

        then:
        assert response.status == HttpServletResponse.SC_BAD_REQUEST
        assert response.text == "OwnerId is required"
        assert !model

        where:
        contextClass << [ProjectContext, AssayContext, ExperimentContext]
    }


    void 'test unAuthorized createCard for #contextClass.simpleName'() {
        given:
        final def context = contextClass.build()
        final String contextClassName = contextClass.simpleName

        when:
        controller.createCard(contextClassName, context.owner.id, "My Card", ContextType.BIOLOGY.id)
        then:
        controller.contextService."create${contextClassName}"(_, _, _, _) >> {
            throw new AccessDeniedException("some message")
        }

        assert response.status == HttpServletResponse.SC_FORBIDDEN
        assert response.text == "editing.forbidden.message"
        assert !model

        where:
        contextClass << [ProjectContext, AssayContext, ExperimentContext]

    }


    void 'test successful createCard for #contextClass.simpleName'() {
        given:
        final def context = contextClass.build()
        final Long ownerId = context.owner.id

        when:
        controller.createCard(contextClass.simpleName, ownerId, "My Card", ContextType.BIOLOGY.id)

        then:
        response.status == HttpServletResponse.SC_FOUND
        response.redirectedUrl == "/${urlRoot}/show/${ownerId}"

        where:
        contextClass      | urlRoot
        ProjectContext    | 'project'
        AssayContext      | 'assayDefinition'
        ExperimentContext | 'experiment'
    }


    void 'test unAuthorized deleteEmptyCard #contextClass.simpleName'() {
        given:
        final def context = contextClass.build()

        when:
        controller.deleteEmptyCard(contextClass.simpleName, context.id, 'Unclassified')

        then:
        controller.contextService."delete${contextClass.simpleName}"(_, _, _) >> {
            throw new AccessDeniedException("some message")
        }
        assert response.status == HttpServletResponse.SC_FORBIDDEN
        assert response.text == "editing.forbidden.message"
        assert !model

        where:
        contextClass << [ProjectContext, AssayContext, ExperimentContext]

    }

    void 'test successful deleteEmptyCard for #contextClass.simpleName'() {
        given:
        final def context = contextClass.build()

        when:
        controller.deleteEmptyCard(contextClass.simpleName, context.id, 'Unclassified')

        then:
        response.status == HttpServletResponse.SC_FOUND
        response.redirectUrl == "/${urlRoot}/show/${context.owner.id}"

        where:
        contextClass      | urlRoot
        ProjectContext    | 'project'
        AssayContext      | 'assayDefinition'
        ExperimentContext | 'experiment'
    }
}