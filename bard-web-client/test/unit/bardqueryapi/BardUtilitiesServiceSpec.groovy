package bardqueryapi

import static org.junit.Assert.*

import grails.test.mixin.*
import grails.test.mixin.support.*
import org.junit.*
import spock.lang.Specification
import spock.lang.Unroll
import grails.plugins.springsecurity.SpringSecurityService

/**
 * See the API for {@link grails.test.mixin.support.GrailsUnitTestMixin} for usage instructions
 */
@Unroll
@TestFor(BardUtilitiesService)
class BardUtilitiesServiceSpec extends Specification {
    SpringSecurityService springSecurityService = Mock(SpringSecurityService)


    void "test getUsername"() {
        given:
        String currentUser="TEST"
        service.springSecurityService = springSecurityService
        when:
        String username = service.getUsername()
        then:
        springSecurityService.getPrincipal() >> {return new MockPrincipal(currentUser)}
        assert username == currentUser
    }

    void "test getUsername No User"() {
        given:
        String currentUser="TEST"
        service.springSecurityService = springSecurityService
        when:
        String username = service.getUsername()
        then:
        springSecurityService.getPrincipal() >> {"dummy"}
        assert !username
    }
}
class MockPrincipal {
    String username

    MockPrincipal(String username) {
        this.username = username
    }

    boolean hasProperty() {return true}
}
