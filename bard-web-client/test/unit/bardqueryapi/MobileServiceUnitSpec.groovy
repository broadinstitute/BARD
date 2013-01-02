package bardqueryapi

import static org.junit.Assert.*

import grails.test.mixin.*
import grails.test.mixin.support.*
import org.junit.*
import spock.lang.Unroll
import spock.lang.Specification
import grails.plugins.springsecurity.SpringSecurityService
import javax.servlet.http.HttpServletRequest
import org.springframework.mock.web.MockHttpServletRequest
import spock.lang.Shared

/**
 * See the API for {@link grails.test.mixin.support.GrailsUnitTestMixin} for usage instructions
 */
@Unroll
@TestFor(MobileService)
class MobileServiceUnitSpec extends Specification {

    SpringSecurityService springSecurityService

    void setup() {
        this.springSecurityService = Mock(SpringSecurityService)
        service.springSecurityService = this.springSecurityService
    }

    void tearDown() {
        // Tear down logic here
    }

    void "test detect"() {
        when:
        HttpServletRequest mockedRequest = new MockHttpServletRequest()
        mockedRequest.session.setAttribute('mobileExperienceDisabled', true)
        Boolean result = service.detect(mockedRequest)

        then:
        assert result == false
    }
}
