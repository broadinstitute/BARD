package barddataexport.util

import grails.test.mixin.*

import spock.lang.Specification
import spock.lang.Unroll
import javax.servlet.http.HttpServletRequest
import spock.lang.Shared

/**
 * See the API for {@link grails.test.mixin.support.GrailsUnitTestMixin} for usage instructions
 */
//@TestMixin(GrailsUnitTestMixin)
@TestFor(AuthenticationService)
@Unroll
class AuthenticationServiceUnitSpec extends Specification {

    @Shared HttpServletRequest httpServletRequest

    void setup() {
        httpServletRequest = Mock()
    }

    void tearDown() {
        // Tear down logic here
    }

    /**
     */
    void "test doIpAddressesMatch #label"() {

        when:
        Boolean response = service.doIpAddressesMatch(lhs, rhs)

        then:
        assert response == expectedMatched

        where:
        label                              | lhs       | rhs       | expectedMatched
        "matched - identical literals"     | '1.2.3.4' | '1.2.3.4' | true
        "matched - wildcard"               | '1.*.3.4' | '1.5.3.4' | true
        "not matched - different literals" | '1.2.3.4' | '1.2.3.5' | false
        "not matched - wildcard"           | '1.*.3.4' | '1.2.3.5' | false
    }

    /**
     */
    void "test authenticate #label"() {

        when:
        service.grailsApplication.config.barddataexport.externalapplication.ipAddress.whiteList = whiteList
        service.grailsApplication.config.barddataexport.externalapplication.apiKey.hashed = 'apikey'
        Boolean response = service.authenticate(httpServletRequest)

        then:
        1 * httpServletRequest.getHeader(_) >> { 'apikey' }
        1 * httpServletRequest.getRemoteAddr() >> { remoteAddress }

        assert response == expectedResult

        where:
        label                                           | remoteAddress | whiteList   | expectedResult
        "remote address is in the whitelist"            | '1.2.3.4'     | ['1.2.3.4'] | true
        "remote address is in the whitelist - wildcard" | '1.5.3.4'     | ['1.*.3.4'] | true
        "remote address is not in the whitelist"        | '1.2.3.4'     | ['1.*.3.5'] | false
    }
}
