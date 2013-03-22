package bard.db.registration

import bard.db.dictionary.Element
import bard.db.dictionary.OntologyDataAccessService
import bard.validation.ext.ExternalItem
import bard.validation.ext.ExternalOntologyException
import grails.buildtestdata.mixin.Build
import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import groovy.json.JsonSlurper
import spock.lang.Specification
import spock.lang.Unroll

/**
 * Created with IntelliJ IDEA.
 * User: ddurkin
 * Date: 3/14/13
 * Time: 11:39 AM
 * To change this template use File | Settings | File Templates.
 */
@TestFor(OntologyJSonController)
@Build(Element)
@Mock(Element)
@Unroll
class OntologyJsonControllerUnitSpec extends Specification {

    OntologyDataAccessService ontologyDataAccessService = Mock(OntologyDataAccessService)

    void setup() {
        controller.ontologyDataAccessService = ontologyDataAccessService
    }

    void "test findExternalItemsByTerm #desc "() {
        given: 'valid params'
        final String externalUrl = 'http://bard.org'
        final String term = 'foo'
        final Element element = Element.build(externalURL: externalUrl)


        when: 'we look for items'
        controller.findExternalItemsByTerm(element.id, term)
        final String resultJson = controller.response.contentAsString

        then: 'we serialize items as JSON'
        1 * ontologyDataAccessService.findExternalItemsByTerm(externalUrl, term) >> { serviceReturnValue.call() }
        resultJson
        println(resultJson)
        Map resultMap = new JsonSlurper().parseText(resultJson)
        println(resultMap)
        resultMap == expectedMap

        where:
        desc               | serviceReturnValue                                                     | expectedMap
        'no items found'   | { [] }                                                                 | [externalItems: []]
        '1 item found'     | { [new ExternalItem('1', 'item 1')] }                                  | [externalItems: [[id: '1', text: '(id:1) item 1', display: 'item 1']]]
        '2 items found'    | { [new ExternalItem('1', 'item 1'), new ExternalItem('2', 'item 2')] } | [externalItems: [[id: '1', text: '(id:1) item 1', display: 'item 1'], [id: '2', text: '(id:2) item 2', display: 'item 2']]]
        'exception thrown' | { throw new ExternalOntologyException("some Exception") }              | [error: 'some Exception', externalItems: []]
    }
}