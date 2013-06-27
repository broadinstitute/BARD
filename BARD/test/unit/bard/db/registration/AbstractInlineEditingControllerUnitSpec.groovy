package bard.db.registration

import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils
import spock.lang.Specification

import javax.servlet.http.HttpServletResponse

/**
 */



abstract class AbstractInlineEditingControllerUnitSpec extends Specification {

    void accessDeniedRoleMock() {
        SpringSecurityUtils.metaClass.'static'.ifAnyGranted = { String role ->
            return false
        }
    }
    void assertEditingErrorMessage() {
        assert response.status == HttpServletResponse.SC_INTERNAL_SERVER_ERROR
        assert response.text == "foo"
        assert response.contentType == "text/plain;charset=utf-8"


    }
    void assertAccesDeniedErrorMessage() {
        assert response.status == HttpServletResponse.SC_FORBIDDEN
        assert response.text == "editing.forbidden.message"
        assert response.contentType == "text/plain;charset=utf-8"


    }
    void assertOptimisticLockFailure(){
        assert response.status == HttpServletResponse.SC_CONFLICT
        assert response.text == "default.optimistic.locking.failure"
        assert response.contentType == "text/plain;charset=utf-8"

    }

}