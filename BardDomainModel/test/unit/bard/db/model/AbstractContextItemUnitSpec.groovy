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
        desc                                          | attributeExternalUrl | valueElementMap | unitElementMap | valueMap                                                             | expectedValid | globalErrorsCodes                                     | fieldErrorCodes
        'no value'                                    | null                 | null            | null           | [:]                                                                  | true          | []                                                    | [:]

        'externalUrl both blank'                      | 'http://bard.org'    | null            | null           | [:]                                                                  | false         | ['contextItem.attribute.externalURL.required.fields'] | [extValueId: 'contextItem.extValueId.blank', valueDisplay: 'contextItem.valueDisplay.blank']
        'externalUrl blank extValueId'                | 'http://bard.org'    | null            | null           | [extValueId: null, valueDisplay: 'someDisplay']                      | false         | ['contextItem.attribute.externalURL.required.fields'] | [extValueId: 'contextItem.extValueId.blank']
        'externalUrl blank display'                   | 'http://bard.org'    | null            | null           | [extValueId: 'someId']                                               | false         | ['contextItem.attribute.externalURL.required.fields'] | [valueDisplay: 'contextItem.valueDisplay.blank']
        'valid externalUrl'                           | 'http://bard.org'    | null            | null           | [extValueId: 'someId', valueDisplay: 'someDisplay']                  | true          | []                                                    | [extValueId: null, valueDisplay: null]

        'valid externalUrl but valueElement not null' | 'http://bard.org'    | [label: 'foo']  | null           | [extValueId: 'someId', valueDisplay: 'someDisplay']                  | false         | []                                                    | [extValueId: null, valueDisplay: null, valueElement: 'contextItem.valueElement.not.null']
        'valid externalUrl but qualifier not null'    | 'http://bard.org'    | null            | null           | [extValueId: 'someId', valueDisplay: 'someDisplay', qualifier: '= '] | false         | []                                                    | [extValueId: null, valueDisplay: null, qualifier: 'contextItem.qualifier.not.null']
        'valid externalUrl but valueNum not null'     | 'http://bard.org'    | null            | null           | [extValueId: 'someId', valueDisplay: 'someDisplay', valueNum: 2]     | false         | []                                                    | [extValueId: null, valueDisplay: null, valueNum: 'contextItem.valueNum.not.null']
        'valid externalUrl but valueMin not null'     | 'http://bard.org'    | null            | null           | [extValueId: 'someId', valueDisplay: 'someDisplay', valueMin: 1]     | false         | []                                                    | [extValueId: null, valueDisplay: null, valueMin: 'contextItem.valueMin.not.null']
        'valid externalUrl but valueMax not null'     | 'http://bard.org'    | null            | null           | [extValueId: 'someId', valueDisplay: 'someDisplay', valueMax: 2]     | false         | []                                                    | [extValueId: null, valueDisplay: null, valueMax: 'contextItem.valueMax.not.null']


    }


    private Element optionallyCreateElement(Map map) {
        if (map != null) {
            return Element.build(map)
        }
    }

}

