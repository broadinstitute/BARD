package bard.db.registration

import bard.db.dictionary.Element
import bard.db.enums.AssayStatus
import bard.db.enums.AssayType
import bard.db.enums.ExpectedValueType
import grails.buildtestdata.mixin.Build
import grails.test.mixin.Mock
import spock.lang.Specification
import spock.lang.Unroll
import bard.db.guidance.owner.*

import static bard.db.guidance.owner.MinimumOfOneBiologyGuidanceRule.*
import static bard.db.registration.AttributeType.*

import static bard.db.guidance.owner.ShouldOnlyHaveOneItemPerNonFixedAttributeElementRule.getErrorMsg

/**
 * Created with IntelliJ IDEA.
 * User: pmontgom
 * Date: 2/11/13
 * Time: 11:51 AM
 * To change this template use File | Settings | File Templates.
 */
@Build([Assay, AssayContext, AssayContextItem, Element])
@Mock([Assay, AssayContext, AssayContextItem, Element])
@Unroll
class AssayUnitSpec extends Specification {
    def 'test allowsNewExperiments when #desc'() {
        when:
        Assay assay = Assay.build(assayType: assayType, assayStatus: assayStatus)

        then:
        assay.allowsNewExperiments() == expectedAllowsNewExperiments

        where:
        desc              | assayType          | assayStatus         | measureCount | expectedAllowsNewExperiments
        'retired assay'   | AssayType.REGULAR  | AssayStatus.RETIRED | 1            | false
        'template assay'  | AssayType.TEMPLATE | AssayStatus.DRAFT   | 1            | false
        'everything good' | AssayType.REGULAR  | AssayStatus.DRAFT   | 1            | true
    }


    void 'test guidance for Assay #desc'() {
        given:
        final Assay assay = Assay.build()
        List<List<Map>> itemMaps = attributeElementValueElementMaps.call() //[[attribute1 element pairMaps], [value1 element pairMaps]]
        // putting each item in it's own context
        if (itemMaps) {
            itemMaps.each { List<Map> pairMaps ->
                final AssayContext assayContext = AssayContext.build(assay: assay)
                final Element attribute = Element.findByLabel(pairMaps.first().label) ?: Element.build(pairMaps.first())
                final Element valueElement = pairMaps.last() ? (Element.findByLabel(pairMaps.last()?.label) ?: Element.build(pairMaps.last())) : null
                assayContext.addContextItem(AssayContextItem.build(attributeElement: attribute, valueElement: valueElement, valueDisplay: valueDisplay))
            }
        }

        when:
        final List<String> actualGuidanceMessages = assay.guidance.message

        then:
        actualGuidanceMessages == expectedGuidanceMessages

        where:
        desc                                                        | attributeElementValueElementMaps                                                                                | valueDisplay | expectedGuidanceMessages
        'biology required (true)'                                   | { null }                                                                                                        | null         | [ONE_BIOLOGY_ATTRIBUTE_REQUIRED]
        'biology required (false)'                                  | { [[[label: 'biology'], []]] }                                                                                  | null         | []
        'biology required with valueElement==small-molecule format' | { [[[label: 'assay format', expectedValueType: ExpectedValueType.ELEMENT], [label: 'small-molecule format']]] } | 'not null'   | []
        'more than 1 biology ok'                                    | { [[[label: 'biology'], []], [[label: 'biology'], []]] }                                                        | null         | []
    }

    void 'test that OneItemPerNonFixedAttributeElement is wired into assay'() {
        given: 'an assay with some context items'
        final Assay assay = Assay.build()
        final String attributeLabel = 'biology'
        final Element attribute = Element.findByLabel(attributeLabel) ?: Element.build([label: attributeLabel])

        // putting each item in it's own context
        assayContextItemMaps.each { Map assayContextItemMap ->
            final AssayContext assayContext = AssayContext.build(assay: assay)
            assayContextItemMap << ['attributeElement': attribute, assayContext: assayContext]
            final AssayContextItem aci = AssayContextItem.buildWithoutSave(assayContextItemMap)
        }

        when: 'we have the rule evaluate the assay'
        final ArrayList<String> actualGuidanceMessages = assay.guidance.message

        then: 'we should see only the expected messages'
        actualGuidanceMessages == expectedGuidanceMessages

        where:
        desc                                         | assayContextItemMaps                           | expectedGuidanceMessages
        '2 items with attributeType != Fixed not ok' | [[attributeType: Free], [attributeType: Free]] | [getErrorMsg('biology')]
    }


}