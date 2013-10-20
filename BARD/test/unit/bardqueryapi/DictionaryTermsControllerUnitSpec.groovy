package bardqueryapi

import bard.db.dictionary.Element
import grails.buildtestdata.mixin.Build
import grails.test.mixin.TestFor
import grails.test.mixin.TestMixin
import grails.test.mixin.support.GrailsUnitTestMixin
import spock.lang.Specification
import spock.lang.Unroll

import javax.servlet.http.HttpServletResponse

/**
 * See the API for {@link grails.test.mixin.support.GrailsUnitTestMixin} for usage instructions
 */
@TestMixin(GrailsUnitTestMixin)
@TestFor(DictionaryTermsController)
@Build(Element)
@Unroll
class DictionaryTermsControllerUnitSpec extends Specification {

    void dictionaryTerms() {
        when:
        controller.dictionaryTerms()
        Element.metaClass.'static'.list() >> {[Element.build()]}
        then:
        assert "/dictionaryTerms/dictionaryTerms" == view
    }
    void "index"() {
        when:
        controller.index()
        then:
        assert response.status == HttpServletResponse.SC_FOUND
    }
}
