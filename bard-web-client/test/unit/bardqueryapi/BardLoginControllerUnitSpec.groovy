package bardqueryapi

import grails.plugins.springsecurity.SpringSecurityService
import grails.test.mixin.TestFor
import grails.test.mixin.TestMixin
import grails.test.mixin.support.GrailsUnitTestMixin
import org.springframework.security.web.WebAttributes
import spock.lang.Specification
import spock.lang.Unroll

import javax.servlet.http.HttpServletResponse

import org.springframework.security.authentication.*

/**
 * See the API for {@link grails.test.mixin.support.GrailsUnitTestMixin} for usage instructions
 */
@TestMixin(GrailsUnitTestMixin)
@TestFor(BardLoginController)
@Unroll
class BardLoginControllerUnitSpec extends Specification {

    SpringSecurityService springSecurityService
    AuthenticationTrustResolver authenticationTrustResolver
    MobileService mobileService
    TestingAuthenticationToken testingAuthenticationToken

    void setup() {
        this.springSecurityService = Mock(SpringSecurityService)
        controller.springSecurityService = this.springSecurityService
        this.mobileService = Mock(MobileService)
        controller.mobileService = this.mobileService
        this.authenticationTrustResolver = Mock(AuthenticationTrustResolver)
        controller.authenticationTrustResolver = this.authenticationTrustResolver
        this.testingAuthenticationToken = Mock(TestingAuthenticationToken)
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

    void "test authAjax()"() {
        when:
        controller.authAjax()

        then:
        assert response.getHeader('Location') == '/bardLogin/authAjax'
        assert response.status == HttpServletResponse.SC_UNAUTHORIZED
    }

    void "test denided() #label"() {
        when:
        controller.denied()

        then:
        this.springSecurityService.isLoggedIn() >> {isLoggedIn}
        this.authenticationTrustResolver.isRememberMe(_) >> {isRememberMe}
        assert response.redirectedUrl == expectedUrl

        where:
        label                           | isLoggedIn | isRememberMe | expectedUrl
        'not logged in, not RememberMe' | false      | false        | null
        'logged in, RememberMe'         | true       | true         | '/bardLogin/full'
    }

    void "test full() #label"() {
        when:
        controller.full()

        then:
        this.authenticationTrustResolver.isRememberMe(_) >> {true}
        assert view == '/login/auth'
        assert model.postUrl == '/j_spring_security_check'
        assert model.hasCookie == true
    }

    void "test authfail() #label"() {
        when:
        controller.session.setAttribute(WebAttributes.AUTHENTICATION_EXCEPTION, exception)
        controller.authfail()

        then:
        assert flash.message == expectedResult
        assert response.redirectedUrl == '/bardLogin/auth'

        where:
        label                         | exception                           | expectedResult
        'AccountExpiredException'     | new AccountExpiredException('')     | 'springSecurity.errors.login.expired'
        'CredentialsExpiredException' | new CredentialsExpiredException('') | 'springSecurity.errors.login.passwordExpired'
        'DisabledException'           | new DisabledException('')           | 'springSecurity.errors.login.disabled'
        'LockedException'             | new LockedException('')             | 'springSecurity.errors.login.locked'
        'general exception'           | 'exception'                         | 'springSecurity.errors.login.fail'
        'no exception'                | false                               | ''
    }

    void "test authfail() isAjax #label"() {
        when:
        controller.session.setAttribute(WebAttributes.AUTHENTICATION_EXCEPTION, 'exception')
        controller.authfail()

        then:
        this.springSecurityService.isAjax(_) >> {isAjax}
        assert (isAjax) ? (response?.json?.'error' == expectedResult) : (response.redirectedUrl == '/bardLogin/auth')

        where:
        label          | isAjax | expectedResult
        'isAjax=true'  | true   | 'springSecurity.errors.login.fail'
        'isAjax=false' | false  | null
    }

    void "test ajaxSuccess()"() {
        when:
        controller.ajaxSuccess()

        then:
        this.springSecurityService.getAuthentication() >> {this.testingAuthenticationToken}
        this.testingAuthenticationToken.getName() >> {'aName'}
        assert response.json.'success' == true
        assert response.json.'username' == 'aName'
    }

    void "test ajaxDenied()"() {
        when:
        controller.ajaxDenied()

        then:
        assert response.json.'error' == 'access denied'
    }
}
