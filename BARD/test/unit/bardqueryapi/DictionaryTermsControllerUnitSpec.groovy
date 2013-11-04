package bardqueryapi
import bard.db.dictionary.Descriptor
import bard.db.dictionary.OntologyDataAccessService
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
@Unroll
class DictionaryTermsControllerUnitSpec extends Specification {

    OntologyDataAccessService ontologyDataAccessService

    void setup() {
        ontologyDataAccessService = Mock(OntologyDataAccessService)
        controller.ontologyDataAccessService = ontologyDataAccessService
    }

    void dictionaryTerms() {
        when:
        controller.dictionaryTerms()

        then:
        ontologyDataAccessService.getDescriptors(null,null) >> new ArrayList<Descriptor>()
        assert "/dictionaryTerms/dictionaryTerms" == view
    }
    void "index"() {
        when:
        controller.index()
        then:
        assert response.status == HttpServletResponse.SC_FOUND
    }
}
