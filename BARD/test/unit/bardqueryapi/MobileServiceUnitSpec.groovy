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
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils
import org.codehaus.groovy.grails.web.context.ServletContextHolder
import org.springframework.mock.web.MockHttpServletRequest
import spock.lang.Ignore
import spock.lang.Specification
import spock.lang.Unroll

import javax.servlet.ServletContext
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpSession

/**
 * See the API for {@link grails.test.mixin.support.GrailsUnitTestMixin} for usage instructions
 */
@Unroll
@TestFor(MobileService)
class MobileServiceUnitSpec extends Specification {

    SpringSecurityService springSecurityService
    GrailsApplication grailsApplication

    void setup() {
        this.springSecurityService = Mock(SpringSecurityService)
        service.springSecurityService = this.springSecurityService
        this.grailsApplication = Mock(GrailsApplication)
        service.grailsApplication = this.grailsApplication
    }

    void cleanup() {
        //Clean up the metaClass mocking we added.
        def remove = GroovySystem.metaClassRegistry.&removeMetaClass
        remove SpringSecurityUtils
        remove ServletContextHolder
    }

    void "test detect mobileExperienceDisabled"() {
        when:
        HttpServletRequest mockedRequest = new MockHttpServletRequest()
        mockedRequest.session.setAttribute('mobileExperienceDisabled', true)
        Boolean result = service.detect(mockedRequest)

        then:
        assert result == false
    }

    @Ignore
    void "test detect ROLE_MOBILE"() {
        when:
        HttpServletRequest mockedRequest = new MockHttpServletRequest()
        mockedRequest.session.setAttribute('mobileExperienceDisabled', false)
        SpringSecurityUtils.metaClass.static.ifAnyGranted = { String role -> return true }
        Boolean result = service.detect(mockedRequest)

        then:
        assert result == true
    }

    @Ignore
    void "test isMobile() #label"() {
        when:
        HttpServletRequest mockedRequest = Mock(HttpServletRequest)
        HttpSession httpSession = Mock(HttpSession)
        mockedRequest.session >> { httpSession }
        httpSession.getAttribute(_) >> { false }
        SpringSecurityUtils.metaClass.static.ifAnyGranted = { String role -> return false }
        DummyDevice device = Mock(DummyDevice)
        mockedRequest.getAttribute('currentDevice') >> { return device }
        device.isMobile() >> { isMobile }
        Boolean result = service.detect(mockedRequest)

        then:
        assert result == expectedResult

        where:
        label                 | isMobileDevice | expectedResult
        'a mobile device'     | true           | true
        'a non-mobile device' | false          | false
    }

    @Ignore
    void "test userAgent #label"() {
        when:
        HttpServletRequest mockedRequest = Mock(HttpServletRequest)
        HttpSession httpSession = Mock(HttpSession)
        mockedRequest.session >> { httpSession }
        httpSession.getAttribute(_) >> { false }
        SpringSecurityUtils.metaClass.static.ifAnyGranted = { String role -> return false }
        DummyDevice device = Mock(DummyDevice)
        mockedRequest.getAttribute('currentDevice') >> { return device }
        device.isMobile() >> { true }
        mockedRequest.getHeader('User-Agent') >> { userAgent }
        Boolean result = service.detect(mockedRequest)

        then:
        assert result == expectedResult

        where:
        label                     | userAgent        | expectedResult
        'an iPad device'          | 'iPad'           | true
        'an Android device'       | 'Android'        | true
        'a mobile Android device' | 'Android Mobile' | true
    }

    void "test gspExists war #label"() {
        when:
        grailsApplication.isWarDeployed() >> { true }
        ServletContext servletContext = Mock(ServletContext)
        ServletContextHolder.metaClass.static.getServletContext = { servletContext }
        servletContext.getResourcePaths(_) >>> resourcePaths
        Boolean result = service.gspExists(gspExistName)
        then:
        assert result == expectedResult

        where:
        label              | gspExistName | resourcePaths              | expectedResult
        'a gsp was found'  | '/index'     | [['index.gsp']]            | true
        'no gsp was found' | '/index'     | [['root'], ['search.gsp']] | false
    }


    void "test gspExists dev #label"() {
        when:
        grailsApplication.isWarDeployed() >> { false }
        Boolean result = service.gspExists(gspExistName)
        then:
        assert result == expectedResult

        where:
        label              | gspExistName       | expectedResult
        'a gsp was found'  | '/about/aboutBard' | true
        'no gsp was found' | '/noGSP'           | false
    }

}

class DummyDevice {
    Boolean isMobile() {
        return true
    }
}
