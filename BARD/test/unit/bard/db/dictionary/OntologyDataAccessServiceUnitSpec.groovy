package bard.db.dictionary

import bard.validation.ext.BardExternalOntologyFactory
import bard.validation.ext.ExternalItem
import bard.validation.ext.ExternalOntologyAPI
import bard.validation.ext.ExternalOntologyException
import grails.test.mixin.TestFor
import org.apache.commons.logging.Log
import spock.lang.Specification
import spock.lang.Unroll

/**
 * Created with IntelliJ IDEA.
 * User: ddurkin
 * Date: 3/11/13
 * Time: 4:58 PM
 * To change this template use File | Settings | File Templates.
 */
@Unroll
@TestFor(OntologyDataAccessService)
class OntologyDataAccessServiceUnitSpec extends Specification {

    OntologyDataAccessService ontologyDataAccessService = new OntologyDataAccessService()
    BardExternalOntologyFactory externalOntologyFactory
    ExternalOntologyAPI externalOntologyAPI
    Log log

    void setup() {
        externalOntologyFactory = Mock(BardExternalOntologyFactory)
        externalOntologyAPI = Mock(ExternalOntologyAPI)
        log = Mock(Log)
        ontologyDataAccessService.externalOntologyFactory = externalOntologyFactory
        ontologyDataAccessService.log = log

    }

    void "test findExternalItemById for #expectedException when #desc"(String desc, String externalUrl, String id, String expectedMessage) {
        when:
        ontologyDataAccessService.findExternalItemById(externalUrl, id)

        then:
        IllegalArgumentException e = thrown(IllegalArgumentException)
        e.message == expectedMessage

        where:
        desc                | externalUrl      | id   | expectedMessage
        "null exteranlUrl"  | null             | null | 'externalUrl cannot be blank'
        "blank exteranlUrl" | ''               | null | 'externalUrl cannot be blank'
        "blank exteranlUrl" | ' '              | null | 'externalUrl cannot be blank'
        "null blank term"   | 'http://foo.com' | ''   | 'id cannot be blank'
        "null term"         | 'http://foo.com' | null | 'id cannot be blank'
        "null blank term"   | 'http://foo.com' | ' '  | 'id cannot be blank'
    }

    void "test findExternalItemById no exerternalOntologyApi implementation"() {
        given:
        final String externalUrl = 'http://foo.com'
        final String id = '1'

        when:
        ExternalItem externalItem = ontologyDataAccessService.findExternalItemById(externalUrl, id)

        then:
        1 * externalOntologyFactory.getExternalOntologyAPI(externalUrl, ontologyDataAccessService.externalOntologyProperites) >> null
        externalItem == null
    }

    void "test findExternalItemById with exerternalOntologyApi implementation returning item"() {
        given:
        final String externalUrl = 'http://foo.com'
        final String id = '1'
        final ExternalItem returnedExternalItem = new ExternalItem(id, 'someDisplay')

        when:
        ExternalItem externalItem = ontologyDataAccessService.findExternalItemById(externalUrl, id)

        then:
        1 * externalOntologyFactory.getExternalOntologyAPI(externalUrl, ontologyDataAccessService.externalOntologyProperites) >> externalOntologyAPI
        1 * externalOntologyAPI.findById(id) >> returnedExternalItem
        externalItem == returnedExternalItem
    }

    void "test findExternalItemById with externalOntologyFactory throwing an exception"() {
        given: 'valid params'
        final String externalUrl = 'http://foo.com'
        final String id = '1'

        when: 'an exception is thrown'
        ExternalItem externalItem = ontologyDataAccessService.findExternalItemById(externalUrl, id)

        then: 'logging should happen at the error level and an exception should be thrown'
        1 * externalOntologyFactory.getExternalOntologyAPI(externalUrl, ontologyDataAccessService.externalOntologyProperites) >> { throw new ExternalOntologyException("some exception") }
        1 * log.error(_,_)
        thrown(ExternalOntologyException)
    }

    void "test findExternalItemsByTerm for #expectedException when #desc"(String desc, String externalUrl, String term, String expectedMessage) {

        when:
        ontologyDataAccessService.findExternalItemsByTerm(externalUrl, null)

        then:
        IllegalArgumentException e = thrown(IllegalArgumentException)
        e.message == expectedMessage

        where:
        desc                | externalUrl      | term | expectedMessage
        "null exteranlUrl"  | null             | null | 'externalUrl cannot be blank'
        "blank exteranlUrl" | ''               | null | 'externalUrl cannot be blank'
        "blank exteranlUrl" | ' '              | null | 'externalUrl cannot be blank'
        "null blank term"   | 'http://foo.com' | ''   | 'term cannot be blank'
        "null term"         | 'http://foo.com' | null | 'term cannot be blank'
        "null blank term"   | 'http://foo.com' | ' '  | 'term cannot be blank'
    }

    void "test findExternalItemsByTerm no exerternalOntologyApi implementation"() {
        given:
        final String externalUrl = 'http://foo.com'
        final String term = '1'

        when:
        List<ExternalItem> externalItems = ontologyDataAccessService.findExternalItemsByTerm(externalUrl, term)

        then:
        1 * externalOntologyFactory.getExternalOntologyAPI(externalUrl, ontologyDataAccessService.externalOntologyProperites) >> { null }
        externalItems.isEmpty()
    }

    void "test findExternalItemsByTerm with exerternalOntologyApi implementation returning item"() {
        given:
        final String externalUrl = 'http://foo.com'
        final String term = '1'
        final List<ExternalItem> returnedExternalItems = [new ExternalItem(term, 'someDisplay')]

        when:
        List<ExternalItem> externalItems = ontologyDataAccessService.findExternalItemsByTerm(externalUrl, term)

        then:
        1 * externalOntologyFactory.getExternalOntologyAPI(externalUrl, ontologyDataAccessService.externalOntologyProperites) >> externalOntologyAPI
        1 * externalOntologyAPI.findMatching(term, OntologyDataAccessService.DEFAULT_EXTERNAL_ONTOLOGY_MATCHING_PAGE_SIZE) >> returnedExternalItems
        externalItems == returnedExternalItems
    }

    void "test findExternalItemsByTerm with externalOntologyFactory throwing an exception"() {
        given: 'valid params'
        final String externalUrl = 'http://foo.com'
        final String term = '1'

        when: 'an exception is thrown'
        List<ExternalItem> externalItems = ontologyDataAccessService.findExternalItemsByTerm(externalUrl, term)

        then: 'logging should happen at the error level and an exception should be thrown'
        1 * externalOntologyFactory.getExternalOntologyAPI(externalUrl, ontologyDataAccessService.externalOntologyProperites) >> { throw new ExternalOntologyException("some exception") }
        1 * log.error(_,_)
        thrown(ExternalOntologyException)
    }

}