package bard.db.registration

import bard.db.dictionary.Element
import bard.db.enums.AssayStatus
import bard.db.enums.AssayType
import grails.buildtestdata.mixin.Build
import grails.test.mixin.Mock
import spock.lang.Specification
import spock.lang.Unroll

import static bard.db.guidance.assay.MinimumOfOneBiologyGuidanceRule.*

/**
 * Created with IntelliJ IDEA.
 * User: pmontgom
 * Date: 2/11/13
 * Time: 11:51 AM
 * To change this template use File | Settings | File Templates.
 */
@Build([Assay, AssayContext, AssayContextItem, Measure, Element])
@Mock([Assay, AssayContext, AssayContextItem, Measure, Element])
@Unroll
class AssayUnitSpec extends Specification {
    def 'test allowsNewExperiments when #desc'() {
        when:
        Set measures = new HashSet()
        for (int i = 0; i < measureCount; i++) {
            measures.add(Measure.build())
        }
        Assay assay = Assay.build(assayType: assayType, assayStatus: assayStatus, measures: measures)

        then:
        assay.allowsNewExperiments() == expectedAllowsNewExperiments

        where:
        desc              | assayType          | assayStatus         | measureCount | expectedAllowsNewExperiments
        'retired assay'   | AssayType.REGULAR  | AssayStatus.RETIRED | 1            | false
        'template assay'  | AssayType.TEMPLATE | AssayStatus.DRAFT   | 1            | false
        'no measures'     | AssayType.REGULAR  | AssayStatus.DRAFT   | 0            | false
        'everything good' | AssayType.REGULAR  | AssayStatus.DRAFT   | 1            | true
    }


    def 'test guidance that biology is defined'() {
        given:
        final Assay assay = Assay.build()
        List<Map> itemMaps = attributeElementMaps.call()
        // putting each item in it's own context
        if (itemMaps) {
            itemMaps.each { Map map ->
                final AssayContext assayContext = AssayContext.build(assay: assay)
                assay.addToAssayContexts(assayContext)
                final Element attribute = Element.findByLabel(map.label) ?: Element.build(map)
                assayContext.addContextItem(AssayContextItem.build(attributeElement: attribute))

            }
        }

        when:
        final List<String> actualGuidanceMessages = assay.guidance.message

        then:
        actualGuidanceMessages == expectedGuidanceMessages

        where:
        desc                     | attributeElementMaps                         | expectedGuidanceMessages
        'biology required'       | { null }                                     | [ONE_BIOLOGY_ATTRIBUTE_REQUIRED]
        'biology required'       | { [[label: 'biology']] }                     | []
        'more than 1 biology ok' | { [[label: 'biology'], [label: 'biology']] } | []

    }
}