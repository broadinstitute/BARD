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
    String userName = remote { ctx.grailsApplication.config.CbipCrowd.mockUsers.user.username}
    String password = remote { ctx.grailsApplication.config.CbipCrowd.mockUsers.user.password}

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
