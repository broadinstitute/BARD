package bard.db.registration

import spock.lang.Specification

import javax.servlet.http.HttpServletResponse

/**
 */



abstract class AbstractInlineEditingControllerUnitSpec extends Specification {

    void assertEditingErrorMessage() {
        assert response.status == HttpServletResponse.SC_INTERNAL_SERVER_ERROR
        assert response.text == "foo"
        assert response.contentType == "text/plain;charset=utf-8"


    }
    void assertOptimisticLockFailure(){
        assert response.status == HttpServletResponse.SC_CONFLICT
        assert response.text == "default.optimistic.locking.failure"
        assert response.contentType == "text/plain;charset=utf-8"

    }

}