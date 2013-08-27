package bardqueryapi

import grails.plugins.springsecurity.SpringSecurityService
import grails.test.mixin.TestFor
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils
import org.codehaus.groovy.grails.web.context.ServletContextHolder
import org.springframework.mock.web.MockHttpServletRequest
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

    void "test detect ROLE_MOBILE"() {
        when:
        HttpServletRequest mockedRequest = new MockHttpServletRequest()
        mockedRequest.session.setAttribute('mobileExperienceDisabled', false)
        SpringSecurityUtils.metaClass.static.ifAnyGranted = {String role -> return true}
        Boolean result = service.detect(mockedRequest)

        then:
        assert result == true
    }

    void "test isMobile() #label"() {
        when:
        HttpServletRequest mockedRequest = Mock(HttpServletRequest)
        HttpSession httpSession = Mock(HttpSession)
        mockedRequest.session >> {httpSession}
        httpSession.getAttribute(_) >> {false}
        SpringSecurityUtils.metaClass.static.ifAnyGranted = {String role -> return false}
        DummyDevice device = Mock(DummyDevice)
        mockedRequest.getAttribute('currentDevice') >> {return device}
        device.isMobile() >> {isMobile}
        Boolean result = service.detect(mockedRequest)

        then:
        assert result == expectedResult

        where:
        label                 | isMobile | expectedResult
        'a mobile device'     | true     | true
        'a non-mobile device' | false    | false
    }

    void "test userAgent #label"() {
        when:
        HttpServletRequest mockedRequest = Mock(HttpServletRequest)
        HttpSession httpSession = Mock(HttpSession)
        mockedRequest.session >> {httpSession}
        httpSession.getAttribute(_) >> {false}
        SpringSecurityUtils.metaClass.static.ifAnyGranted = {String role -> return false}
        DummyDevice device = Mock(DummyDevice)
        mockedRequest.getAttribute('currentDevice') >> {return device}
        device.isMobile() >> {true}
        mockedRequest.getHeader('User-Agent') >> {userAgent}
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
        grailsApplication.isWarDeployed() >> {true}
        ServletContext servletContext = Mock(ServletContext)
        ServletContextHolder.metaClass.static.getServletContext = {servletContext}
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
        grailsApplication.isWarDeployed() >> {false}
        Boolean result = service.gspExists(gspExistName)
        then:
        assert result == expectedResult

        where:
        label              | gspExistName              | expectedResult
        'a gsp was found'  | '/bardWebInterface/index' | true
        'no gsp was found' | '/noGSP'                  | false
    }

}

protected class DummyDevice {
    Boolean isMobile() {
        return true
    }
}