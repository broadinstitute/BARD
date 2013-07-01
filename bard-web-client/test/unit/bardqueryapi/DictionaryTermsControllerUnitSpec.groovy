package bardqueryapi

import grails.test.mixin.*
import grails.test.mixin.support.*
import spock.lang.Unroll
import bard.core.rest.spring.DictionaryRestService
import bard.core.rest.spring.util.CapDictionary
import spock.lang.Specification

/**
 * See the API for {@link grails.test.mixin.support.GrailsUnitTestMixin} for usage instructions
 */
@TestMixin(GrailsUnitTestMixin)
@TestFor(DictionaryTermsController)
@Unroll

class DictionaryTermsControllerUnitSpec extends Specification {

    void dictionaryTerms() {
        given:
        DictionaryRestService dictionaryRestService = Mock(DictionaryRestService)
        controller.dictionaryRestService = dictionaryRestService
        when:
        controller.dictionaryTerms()
        then:
        dictionaryRestService.getDictionary() >> {new CapDictionary()}
        assert "/dictionaryTerms/dictionaryTerms" == view
    }
}
