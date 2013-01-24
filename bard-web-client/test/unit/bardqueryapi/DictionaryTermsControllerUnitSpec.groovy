package bardqueryapi

import static org.junit.Assert.*

import grails.test.mixin.*
import grails.test.mixin.support.*
import org.junit.*
import spock.lang.Unroll
import bard.core.rest.spring.DataExportRestService
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
        DataExportRestService dataExportRestService = Mock(DataExportRestService)
        controller.dataExportRestService = dataExportRestService
        when:
        controller.dictionaryTerms()
        then:
        dataExportRestService.getDictionary() >> {new CapDictionary()}
        assert "/dictionaryTerms/dictionaryTerms" == view
    }
}
