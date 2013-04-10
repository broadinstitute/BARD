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

    /**
     * Business rules for validating a contextItem are pretty complicated
     * The value a contextItem holds can be held in 1 or more columns, but only certain combinations are valid and the
     * value of other fields particularly the attributeElement impact what state is valid
     * @see <a href="https://github.com/broadinstitute/BARD/wiki/Business-rules#general-business-rules-for-assay_context_item">general-business-rules-for-assay_context_item</a>
     *
     */
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
        desc                                                 | attributeExternalUrl | valueElementMap | unitElementMap         | valueMap                                                             | expectedValid | globalErrorsCodes                                     | fieldErrorCodes
        'no value'                                           | null                 | null            | null                   | [:]                                                                  | true          | []                                                    | [:]

        'valid externalUrl'                                  | 'http://bard.org'    | null            | null                   | [extValueId: 'someId', valueDisplay: 'someDisplay']                  | true          | []                                                    | [extValueId: null, valueDisplay: null]
        'externalUrl both blank'                             | 'http://bard.org'    | null            | null                   | [:]                                                                  | false         | ['contextItem.attribute.externalURL.required.fields'] | [extValueId: 'contextItem.extValueId.blank', valueDisplay: 'contextItem.valueDisplay.blank']
        'externalUrl blank extValueId'                       | 'http://bard.org'    | null            | null                   | [extValueId: null, valueDisplay: 'someDisplay']                      | false         | ['contextItem.attribute.externalURL.required.fields'] | [extValueId: 'contextItem.extValueId.blank']
        'externalUrl blank display'                          | 'http://bard.org'    | null            | null                   | [extValueId: 'someId']                                               | false         | ['contextItem.attribute.externalURL.required.fields'] | [valueDisplay: 'contextItem.valueDisplay.blank']

        'valid externalUrl but valueElement not null'        | 'http://bard.org'    | [label: 'foo']  | null                   | [extValueId: 'someId', valueDisplay: 'someDisplay']                  | false         | []                                                    | [extValueId: null, valueDisplay: null, valueElement: 'contextItem.valueElement.not.null']
        'valid externalUrl but qualifier not null'           | 'http://bard.org'    | null            | null                   | [extValueId: 'someId', valueDisplay: 'someDisplay', qualifier: '= '] | false         | []                                                    | [extValueId: null, valueDisplay: null, qualifier: 'contextItem.qualifier.not.null']
        'valid externalUrl but valueNum not null'            | 'http://bard.org'    | null            | null                   | [extValueId: 'someId', valueDisplay: 'someDisplay', valueNum: 2]     | false         | []                                                    | [extValueId: null, valueDisplay: null, valueNum: 'contextItem.valueNum.not.null']
        'valid externalUrl but valueMin not null'            | 'http://bard.org'    | null            | null                   | [extValueId: 'someId', valueDisplay: 'someDisplay', valueMin: 1]     | false         | []                                                    | [extValueId: null, valueDisplay: null, valueMin: 'contextItem.valueMin.not.null']
        'valid externalUrl but valueMax not null'            | 'http://bard.org'    | null            | null                   | [extValueId: 'someId', valueDisplay: 'someDisplay', valueMax: 2]     | false         | []                                                    | [extValueId: null, valueDisplay: null, valueMax: 'contextItem.valueMax.not.null']

        'valid attributeWithUnit'                            | null                 | null            | [abbreviation: 'abbr'] | [qualifier: '= ', valueNum: 2.0]                                     | true          | []                                                    | [:]
        'valid attributeWithUnit qualifier optional'         | null                 | null            | [abbreviation: 'abbr'] | [valueNum: 2.0]                                                      | true          | []                                                    | [:]

        'valid attributeWithUnit but extValueId not null '   | null                 | null            | [abbreviation: 'abbr'] | [qualifier: '= ', valueNum: 2.0, extValueId: 'someId']               | false         | []                                                    | [extValueId: 'contextItem.extValueId.not.null']
        'valid attributeWithUnit but valueElement not null ' | null                 | [label: 'foo']  | [abbreviation: 'abbr'] | [qualifier: '= ', valueNum: 2.0]                                     | false         | []                                                    | [valueElement: 'contextItem.valueElement.not.null']
        'valid attributeWithUnit but valueMin not null '     | null                 | null            | [abbreviation: 'abbr'] | [qualifier: '= ', valueNum: 2.0, valueMin: 1.0]                      | false         | []                                                    | [valueMin: 'contextItem.valueMin.not.null']
        'valid attributeWithUnit but valueMax not null '     | null                 | null            | [abbreviation: 'abbr'] | [qualifier: '= ', valueNum: 2.0, valueMax: 3.0]                      | false         | []                                                    | [valueMax: 'contextItem.valueMax.not.null']

        'valueElement valid'                                 | null                 | [label: 'foo']  | null                   | [valueDisplay: 'someDisplay']                                        | true          | []                                                    | [:]
        'valueElement blank display'                         | null                 | [label: 'foo']  | null                   | [:]                                                                  | false         | []                                                    | [valueDisplay: 'contextItem.valueDisplay.blank']

        'valueElement but extValueId not null'               | null                 | [label: 'foo']  | null                   | [valueDisplay: 'someDisplay', extValueId: 'someId']                  | false         | []                                                    | [valueDisplay: null, extValueId: 'contextItem.extValueId.not.null']
        'valueElement but qualifier not null'                | null                 | [label: 'foo']  | null                   | [valueDisplay: 'someDisplay', qualifier: '= ']                       | false         | []                                                    | [valueDisplay: null, qualifier: 'contextItem.qualifier.not.null']
        'valueElement but valueNum not null'                 | null                 | [label: 'foo']  | null                   | [valueDisplay: 'someDisplay', valueNum: 2]                           | false         | []                                                    | [valueDisplay: null, valueNum: 'contextItem.valueNum.not.null']
        'valueElement but valueMin not null'                 | null                 | [label: 'foo']  | null                   | [valueDisplay: 'someDisplay', valueMin: 1]                           | false         | []                                                    | [valueDisplay: null, valueMin: 'contextItem.valueMin.not.null']
        'valueElement but valueMax not null'                 | null                 | [label: 'foo']  | null                   | [valueDisplay: 'someDisplay', valueMax: 2]                           | false         | []                                                    | [valueDisplay: null, valueMax: 'contextItem.valueMax.not.null']


    }


    private Element optionallyCreateElement(Map map) {
        if (map != null) {
            return Element.build(map)
        }
    }

}

