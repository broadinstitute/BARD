package bardqueryapi

import static org.junit.Assert.*

import grails.test.mixin.*
import grails.test.mixin.support.*
import spock.lang.Unroll
import grails.plugins.springsecurity.SpringSecurityService

/**
 * See the API for {@link grails.test.mixin.support.GrailsUnitTestMixin} for usage instructions
 */
@TestMixin(GrailsUnitTestMixin)
@TestFor(BardLoginController)
@Unroll
class BardLoginControllerSpec {

    SpringSecurityService springSecurityService

    void setup() {
        // Setup logic here
    }

    void cleanup() {
        // Tear down logic here
    }

    void "test indec"() {
        when:
        controller.index()

        then:
        assert response.status == 200
    }
}
