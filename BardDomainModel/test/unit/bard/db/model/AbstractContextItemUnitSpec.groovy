package bard.db.model

import bard.db.dictionary.Element
import bard.db.project.ProjectContextItem
import grails.buildtestdata.mixin.Build
import grails.test.mixin.TestMixin
import grails.test.mixin.domain.DomainClassUnitTestMixin
import org.junit.Before
import spock.lang.Specification
import spock.lang.Unroll

import static test.TestUtils.assertFieldValidationExpectations

/**
 * Created with IntelliJ IDEA.
 * User: ddurkin
 * Date: 4/8/13
 * Time: 4:13 PM
 * To change this template use File | Settings | File Templates.
 */
@Unroll
@Build([Element, ProjectContextItem])
@TestMixin(DomainClassUnitTestMixin)
abstract class AbstractContextItemUnitSpec<T extends AbstractContextItem> extends Specification {

    T domainInstance

    @Before
    abstract void doSetup()

    void "test deriveDisplayValue  #desc"() {

        given:
        Element valueElement = optionallyCreateElement(valueElementMap)
        Element unitElement = optionallyCreateElement(unitElementMap)

        when:
        domainInstance.valueElement = valueElement
        domainInstance.qualifier = qualifier
        domainInstance.valueNum = valueNum
        domainInstance.attributeElement.unit = unitElement
        domainInstance.valueMin = valueMin
        domainInstance.valueMax = valueMax


        then:
        domainInstance.validate()
        domainInstance.deriveDisplayValue() == expectedDisplayValue

        where:
        desc                            | expectedDisplayValue | valueElementMap | qualifier | valueNum | unitElementMap         | valueMin | valueMax
        'all null'                      | null                 | null            | null      | null     | null                   | null     | null
        'only valueElement'             | 't'                  | [label: 't']    | null      | null     | null                   | null     | null
        'only valueNum'                 | '1.0'                | null            | null      | 1.0      | null                   | null     | null
        'qualifier and valueNum'        | '< 1.0'              | null            | '< '      | 1.0      | null                   | null     | null
        'qualifier, valueNum and units' | '< 1.0 abbr'         | null            | '< '      | 1.0      | [abbreviation: 'abbr'] | null     | null
        'range'                         | '5.0 - 6.0'          | null            | null      | null     | null                   | 5.0      | 6.0
    }


    void "test validation  #desc"() {

        given:
        Element valueElement = optionallyCreateElement(valueElementMap)
        Element unitElement = optionallyCreateElement(unitElementMap)

        domainInstance.attributeElement.externalURL = attributeExternalUrl
        domainInstance.valueElement = valueElement
        domainInstance.extValueId = valueMap.extValueId
        domainInstance.qualifier = valueMap.qualifier
        domainInstance.valueNum = valueMap.valueNum
        domainInstance.attributeElement.unit = unitElement
        domainInstance.valueMin = valueMap.valueMin
        domainInstance.valueMax = valueMap.valueMax
        domainInstance.valueDisplay = valueMap.valueDisplay

        when:
        domainInstance.validate()

        then: 'verify field errors'
        for (entry in fieldErrorCodes) {
            assertFieldValidationExpectations(domainInstance, entry.key, expectedValid, entry.value)
        }
        and: 'verify global errors'
        domainInstance.errors?.getGlobalErrors()?.code == globalErrorsCodes


        where:
        desc                              | attributeExternalUrl | valueElementMap | unitElementMap | valueMap                                            | expectedValid | globalErrorsCodes                                     | fieldErrorCodes
        'no value'                        | null                 | null            | null           | [:]                                                 | true          | []                                                    | [:]

        'externalUrl both blank'          | 'http://someUrl.com' | null            | null           | [:]                                                 | false         | ['contextItem.attribute.externalURL.required.fields'] | [extValueId: 'contextItem.extValueId.blank', valueDisplay: 'contextItem.valueDisplay.blank']
        'no externalUrl blank extValueId' | 'http://someUrl.com' | null            | null           | [extValueId: null, valueDisplay: 'someDisplay']     | false         | ['contextItem.attribute.externalURL.required.fields'] | [extValueId: 'contextItem.extValueId.blank']
        'no externalUrl blank display'    | 'http://someUrl.com' | null            | null           | [extValueId: 'someId']                              | false         | ['contextItem.attribute.externalURL.required.fields'] | [valueDisplay: 'contextItem.valueDisplay.blank']
        'valid externalUrl'               | 'http://someUrl.com' | null            | null           | [extValueId: 'someId', valueDisplay: 'someDisplay'] | true          | []                                                    | [extValueId: null, valueDisplay: null]


    }


    private Element optionallyCreateElement(Map map) {
        if (map) {
            return Element.build(map)
        }
    }

}

