package bardqueryapi

import grails.test.mixin.TestFor
import grails.test.mixin.TestMixin
import grails.test.mixin.support.GrailsUnitTestMixin
import spock.lang.Specification

/**
 * See the API for {@link grails.test.mixin.support.GrailsUnitTestMixin} for usage instructions
 */
@TestMixin(GrailsUnitTestMixin)
@TestFor(BardLogoutController)
class BardLogoutControllerUnitSpec extends Specification {

    void setup() {
        // Setup logic here
    }

    void cleanup() {
        // Tear down logic here
    }

    void "test index()"() {
        when:
        controller.index()

        then:
        assert response.status == 302
        assert response.redirectUrl == '/j_spring_security_logout'
    }

    void "test afterLogout()"() {
        when:
        controller.afterLogout()

        then:
        assert response.status == 302
        assert response.redirectUrl == '/bardLogin/auth'
    }
}
