package bardqueryapi

import grails.plugins.springsecurity.SpringSecurityService
import grails.test.mixin.TestFor
import grails.test.mixin.TestMixin
import grails.test.mixin.support.GrailsUnitTestMixin
import spock.lang.Specification
import spock.lang.Unroll

/**
 * See the API for {@link grails.test.mixin.support.GrailsUnitTestMixin} for usage instructions
 */
@TestMixin(GrailsUnitTestMixin)
@TestFor(BardLoginController)
@Unroll
class BardLoginControllerUnitSpec extends Specification {

    SpringSecurityService springSecurityService
    MobileService mobileService

    void setup() {
        this.springSecurityService = Mock(SpringSecurityService)
        controller.springSecurityService = this.springSecurityService
        this.mobileService = Mock(MobileService)
        controller.mobileService = this.mobileService
    }

    void cleanup() {}

    void "test index() #label"() {
        when:
        controller.index()

        then:
        springSecurityService.isLoggedIn() >> {isLoggedIn}
        assert response.status == expectedResponseStatus
        assert response.redirectUrl == expectedRedirectUrl

        where:
        label           | isLoggedIn | expectedResponseStatus | expectedRedirectUrl
        'not logged in' | false      | 302                    | '/bardLogin/auth'
        'logged in'     | true       | 302                    | '/'
    }

    void "test auth() #label"() {
        when:
        controller.auth()

        then:
        springSecurityService.isLoggedIn() >> {isLoggedIn}
        mobileService.detect(_) >> {isMobile}
        assert response.status == expectedResponseStatus
        assert response.redirectUrl == expectedRedirectUrl
        assert response.text.contains(expectedResponseText)

        where:
        label                       | isLoggedIn | expectedResponseStatus | expectedRedirectUrl | isMobile | expectedResponseText
        'logged in'                 | true       | 302                    | '/'                 | false    | ''
        'not logged in, mobile'     | false      | 200                    | null                | true     | '$.mobile.ajaxEnabled = false;'
        'not logged in, not mobile' | false      | 200                    | null                | false    | '<r:require modules="core,bootstrap"></r:require>'
    }

}
