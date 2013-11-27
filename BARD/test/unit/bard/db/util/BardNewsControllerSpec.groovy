package bard.db.util

import grails.buildtestdata.mixin.Build
import grails.test.mixin.TestFor
import grails.test.mixin.TestMixin
import grails.test.mixin.support.GrailsUnitTestMixin
import spock.lang.Specification
import spock.lang.Unroll

/**
 * See the API for {@link grails.test.mixin.support.GrailsUnitTestMixin} for usage instructions
 */
@TestFor(BardNewsController)
@Unroll
@Build([BardNews])
@TestMixin(GrailsUnitTestMixin)
class BardNewsControllerSpec extends Specification {

    def setup() {
    }

    def cleanup() {
    }

    void "test index redirect"() {
        given:

        when:
        controller.index()

        then:
        assert response.redirectedUrl == '/bardNews/list'
    }

    void "test list"() {
        given:
        BardNews item1 = BardNews.build()
        BardNews item2 = BardNews.build()

        when:
        def response = controller.list()

        then:
        assert response.bardNewsInstanceList.size() == 2
        assert response.bardNewsInstanceTotal == 2
    }

    void "test show"() {
        given:
        BardNews item1 = BardNews.build()

        when:
        def response = controller.show(1L)

        then:
        assert response.bardNewsInstance
        assert response.bardNewsInstance.id == 1
    }
}