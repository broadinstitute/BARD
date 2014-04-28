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

package bardqueryapi

import grails.test.mixin.TestFor
import grails.test.mixin.TestMixin
import grails.test.mixin.support.GrailsUnitTestMixin
import spock.lang.Specification
import spock.lang.Unroll

/**
 * See the API for {@link grails.test.mixin.support.GrailsUnitTestMixin} for usage instructions
 */
@TestMixin(GrailsUnitTestMixin)
@TestFor(ErrorHandlingController)
@Unroll
class ErrorHandlingControllerUnitSpec extends Specification {


    void setup() {
        controller.metaClass.mixin(InetAddressUtil)

    }

    void "test handleJsErrors"() {
        given:
        String error = "error"
        String url = "url"
        String line = "230"
        String browser = "Firefox"

        when:
        controller.handleJsErrors(error, url, line, browser)
        then:
        assert response.status == 200
        assert response.contentAsString.contains("Message:error on line 230 using browser:Firefox at URL:url")
    }

    void "test findNonPrivateIpAddress #label"() {

        when:
        final String address = controller.findNonPrivateIpAddress(input)
        then:
        assert address == expected
        where:
        label                      | input         | expected
        "Empty String"             | ""            | ""
        "No Match"                 | "Some String" | ""
        "A non-private IP Address" | "1.2.3.4"     | "1.2.3.4"
        "Avprivate IP Address (1)" | "127.0.0.1"   | ""
        "Avprivate IP Address (2)" | "10.0.0.0"    | ""
        "Avprivate IP Address (3)" | "172.16.0.0"  | ""
        "Avprivate IP Address (4)" | "172.20.0.0"  | ""
        "Avprivate IP Address (5)" | "172.30.0.0"  | ""
        "Avprivate IP Address (6)" | "192.168.0.0" | ""
    }

    void "test getAddressFromRequest() #label"() {
        when:
        if (header)
            controller.request.addHeader('X-Forwarded-For', header)
        controller.request.setRemoteAddr('remoteAddress')
        String result = controller.getAddressFromRequest()

        then:
        assert result == expectedResult

        where:
        label                                                | header     | expectedResult
        'non private IP address with header=X-Forwarded-For' | '1.2.3.4'  | '1.2.3.4'
        'a private IP address with header=X-Forwarded-For'   | '10.0.0.0' | ''
        'a private IP address with header=X-Forwarded-For'   | null       | 'remoteAddress'
    }
}
