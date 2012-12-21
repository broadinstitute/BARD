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
        def resp = controller.handleJsErrors(error, url, line, browser)
        then:
        assert response.status == 200
        assert resp.contains("Message:error on line 230 using browser:Firefox at URL:url")

    }

    void "test findNonPrivateIpAddress #label"() {

        when:
        final String address = controller.findNonPrivateIpAddress(input)
        then:
        assert address == expected
        where:
        label          | input         | expected
        "Empty String" | ""            | ""
        "No Match"     | "Some String" | ""
    }

}
