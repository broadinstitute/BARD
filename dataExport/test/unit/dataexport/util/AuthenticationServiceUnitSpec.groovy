/* Copyright (c) 2014, The Broad Institute
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
    * Redistributions of source code must retain the above copyright
      notice, this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright
      notice, this list of conditions and the following disclaimer in the
      documentation and/or other materials provided with the distribution.
    * Neither the name of The Broad Institute nor the
      names of its contributors may be used to endorse or promote products
      derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL The Broad Institute BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

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

        service.grailsApplication.config.dataexport.externalapplication.apiKey.header = 'APIKEY'
        service.grailsApplication.config.dataexport.externalapplication.apiKey.hashed = 'apikey'
        Boolean response = service.authenticate(httpServletRequest)

        then:
        (0..1) * httpServletRequest.getRequestURL() >> { new StringBuffer("url") }
        1 * httpServletRequest.getHeader(_) >> { 'apikey' }
        (1..2) * httpServletRequest.getRemoteAddr() >> { remoteAddress }

        assert response == expectedResult

        where:
        label                                                | remoteAddress     | expectedResult
        "IPv4 remote address is in the whitelist"            | '1.2.3.4'         | true
        "IPv4 remote address is in the whitelist - wildcard" | '1.5.3.4'         | true
        "IPv4 remote address is not in the whitelist"        | '1.2.3.4'         | false
        "IPv6 matched - identical literals"                  | '1:2:3:4:5:6:7:8' | true
        "IPv6 matched - wildcard"                            | '1:*:3:4:5:6:7:8' | true
        "IPv6 matched - mismatch"                            | '1:2:3:4:5:6:7:8' | false
    }
}
