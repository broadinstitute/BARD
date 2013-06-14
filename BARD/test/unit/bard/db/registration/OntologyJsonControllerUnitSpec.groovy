package bard.db.registration

import bard.db.dictionary.Element
import bard.db.dictionary.OntologyDataAccessService
import bard.db.enums.ExpectedValueType
import bard.validation.ext.ExternalItem
import bard.validation.ext.ExternalOntologyException
import grails.buildtestdata.TestDataConfigurationHolder
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

    void "test element asMapForSelect2 #desc"() {
        given:
        final Element element = Element.build(elementMap)
        element.unit = Element.build()
        assert element.validate()

        when:
        Map actualMap = controller.asMapForSelect2(element)

        then:
        (_..1) * ontologyDataAccessService.externalOntologyHasIntegratedSearch(element.externalURL) >> { hasIntegratedSearch }
        actualMap.id == element.id
        actualMap.text == element.label
        actualMap.unitId == element.unit.id
        actualMap.expectedValueType == element.expectedValueType.name()
        actualMap.externalUrl == element.externalURL
        actualMap.hasIntegratedSearch == hasIntegratedSearch
        actualMap.size() == 6

        where:
        desc                                       | elementMap                                                                                                  | hasIntegratedSearch
        'external valueType with integratedSearch' | [label: 'l1', expectedValueType: ExpectedValueType.EXTERNAL_ONTOLOGY, externalURL: 'http://someUrl.com']    | true
        'external valueType no integratedSearch'   | [label: 'l1', expectedValueType: ExpectedValueType.EXTERNAL_ONTOLOGY, externalURL: 'http://anotherUrl.com'] | false
        'element valueType'                        | [label: 'l1', expectedValueType: ExpectedValueType.ELEMENT]                                                 | false
        'numeric valueType'                        | [label: 'l1', expectedValueType: ExpectedValueType.NUMERIC]                                                 | false
        'free text valueType'                      | [label: 'l1', expectedValueType: ExpectedValueType.FREE_TEXT]                                               | false
        'None valueType'                           | [label: 'l1', expectedValueType: ExpectedValueType.NONE]                                                    | false
    }

    void "test getDescriptors #desc"() {
        given:
        controller.params.term = "someTerm"
        TestDataConfigurationHolder.reset()

        when: 'we look for items'
        controller.getDescriptors()
        final String resultJson = controller.response.contentAsString

        then: 'we serialize items as JSON'
        1 * ontologyDataAccessService.getElementsForAttributes(_ as String) >> { serviceReturnValue.call() }
        println(resultJson)
        Map resultMap = new JsonSlurper().parseText(resultJson)
        println(resultMap)
        resultMap.toString() == expectedMap.toString()

        where:
        desc                | serviceReturnValue                                                             | expectedMap
        'no elements found' | { [] }                                                                         | [results: []]
        '1 element found'   | { [Element.build(label: 'l1', expectedValueType: ExpectedValueType.NUMERIC)] } | [results: [[id: '1', text: 'l1', expectedValueType: ExpectedValueType.NUMERIC, hasIntegratedSearch: false, externalUrl: null, unitId: null]]]
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
        '1 item found'     | { [new ExternalItem('1', 'item 1')] }                                  | [externalItems: [[id: '1', text: '(1) item 1', display: 'item 1']]]
        '2 items found'    | { [new ExternalItem('1', 'item 1'), new ExternalItem('2', 'item 2')] } | [externalItems: [[id: '1', text: '(1) item 1', display: 'item 1'], [id: '2', text: '(2) item 2', display: 'item 2']]]
        'exception thrown' | { throw new ExternalOntologyException("some Exception") }              | [error: 'some Exception', externalItems: []]
    }
}