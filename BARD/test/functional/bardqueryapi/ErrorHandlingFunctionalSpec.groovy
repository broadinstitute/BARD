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

import grails.plugin.remotecontrol.RemoteControl
import groovyx.net.http.HttpResponseDecorator

import groovyx.net.http.RESTClient
import spock.lang.Specification
import spock.lang.Unroll

import static groovyx.net.http.ContentType.URLENC

@Unroll
class ErrorHandlingFunctionalSpec extends Specification {
    RemoteControl remote = new RemoteControl()
    String baseUrl = remote { ctx.grailsApplication.config.grails.serverURL }

    void "test findNonPrivateIpAddress"() {
        given:
        String requestUrl = "${baseUrl}/ErrorHandling/"
        RESTClient http = new RESTClient(requestUrl)
        final String errorMessage = "error"
        final String urlMessage = "url"
        final String lineNumber = "58"
        final String fireFox = "Chrome"
        def postBody = [error: errorMessage, url: urlMessage, line: lineNumber, browser: fireFox]
        when:
        HttpResponseDecorator serverResponse =
            (HttpResponseDecorator) http.post(
                    path: 'handleJsErrors',
                    body: postBody,
                    requestContentType: URLENC
            )
        then:
        assert serverResponse.success
        assert serverResponse.contentType == 'text/plain'
        assert serverResponse.data.any {
            final String string = it.toString()
            string.contains(errorMessage) && string.contains(urlMessage) && string.contains(lineNumber) && string.contains(fireFox)
        }
    }

}
