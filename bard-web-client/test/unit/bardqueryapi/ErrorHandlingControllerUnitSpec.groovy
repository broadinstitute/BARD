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
