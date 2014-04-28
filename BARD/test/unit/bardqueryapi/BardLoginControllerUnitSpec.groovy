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
        springSecurityService.isLoggedIn() >> { isLoggedIn }
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
        springSecurityService.isLoggedIn() >> { isLoggedIn }
        mobileService.detect(_) >> { isMobileDevice }
        assert response.status == expectedResponseStatus
        assert response.redirectUrl == expectedRedirectUrl

        where:
        label                       | isLoggedIn | expectedResponseStatus | expectedRedirectUrl | isMobileDevice | expectedResponseText
        'logged in'                 | true       | 302                    | '/'                 | false          | ''
        'not logged in, mobile'     | false      | 200                    | null                | true           | '$.mobile.ajaxEnabled = false;'
        'not logged in, not mobile' | false      | 200                    | null                | false          | ''
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
        this.springSecurityService.isLoggedIn() >> { isLoggedIn }
        this.authenticationTrustResolver.isRememberMe(_) >> { isRememberMe }
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
        this.authenticationTrustResolver.isRememberMe(_) >> { true }
        assert response.status == 200
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
        this.springSecurityService.isAjax(_) >> { isAjax }
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
        this.springSecurityService.getAuthentication() >> { this.testingAuthenticationToken }
        this.testingAuthenticationToken.getName() >> { 'aName' }
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
