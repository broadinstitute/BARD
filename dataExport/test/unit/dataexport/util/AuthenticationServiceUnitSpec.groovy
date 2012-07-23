package dataexport.util

import grails.test.mixin.*

import spock.lang.Specification
import spock.lang.Unroll
import javax.servlet.http.HttpServletRequest
import spock.lang.Shared
import spock.lang.Ignore

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
        label                                   | lhs               | rhs               | expectedMatched
        "IPv4 matched - identical literals"     | '1.2.3.4'         | '1.2.3.4'         | true
        "IPv4 matched - wildcard"               | '1.*.3.4'         | '1.5.3.4'         | true
        "IPv4 not matched - different literals" | '1.2.3.4'         | '1.2.3.5'         | false
        "IPv4 not matched - wildcard"           | '1.*.3.4'         | '1.2.3.5'         | false
        "IPv6 matched - identical literals"     | '1:2:3:4:5:6:7:8' | '1:2:3:4:5:6:7:8' | true
        "IPv6 matched - wildcard"               | '1:*:3:4:5:6:7:8' | '1:9:3:4:5:6:7:8' | true
        "IPv6 matched - mismatch"               | '1:2:3:4:5:6:7:8' | '1:9:3:4:5:6:7:8' | false
    }

    /**
     */
    @Ignore
    void "test authenticate #label"() {

        when:
        service.grailsApplication.config.dataexport.externalapplication.ipAddress.whiteList = whiteList
        service.grailsApplication.config.dataexport.externalapplication.apiKey.header = 'APIKEY'
        service.grailsApplication.config.dataexport.externalapplication.apiKey.hashed = 'apikey'
        Boolean response = service.authenticate(httpServletRequest)

        then:
        (0..1) * httpServletRequest.getRequestURL() >> { new StringBuffer("url") }
        1 * httpServletRequest.getHeader(_) >> { 'apikey' }
        (1..2) * httpServletRequest.getRemoteAddr() >> { remoteAddress }

        assert response == expectedResult

        where:
        label                                                | remoteAddress     | whiteList                   | expectedResult
        "IPv4 remote address is in the whitelist"            | '1.2.3.4'         | ['1.2.3.4': 'text']         | true
        "IPv4 remote address is in the whitelist - wildcard" | '1.5.3.4'         | ['1.*.3.4': 'text']         | true
        "IPv4 remote address is not in the whitelist"        | '1.2.3.4'         | ['1.*.3.5': 'text']         | false
        "IPv6 matched - identical literals"                  | '1:2:3:4:5:6:7:8' | ['1:2:3:4:5:6:7:8': 'text'] | true
        "IPv6 matched - wildcard"                            | '1:*:3:4:5:6:7:8' | ['1:9:3:4:5:6:7:8': 'text'] | true
        "IPv6 matched - mismatch"                            | '1:2:3:4:5:6:7:8' | ['1:9:3:4:5:6:7:8': 'text'] | false
    }
}
