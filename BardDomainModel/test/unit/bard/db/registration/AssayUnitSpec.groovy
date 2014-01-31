package bard.db.registration

import bard.db.dictionary.Element
import bard.db.enums.AssayType
import bard.db.enums.ExpectedValueType
import bard.db.enums.Status
import bard.db.guidance.owner.OneItemPerNonFixedAttributeElementRule
import grails.buildtestdata.mixin.Build
import grails.test.mixin.Mock
import spock.lang.Specification
import spock.lang.Unroll

import static OneItemPerNonFixedAttributeElementRule.getErrorMsg
import static bard.db.guidance.owner.MinimumOfOneBiologyGuidanceRule.ASSAY_ONE_BIOLOGY_ATTRIBUTE_REQUIRED
import static bard.db.registration.AttributeType.Free

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
        'retired assay'   | AssayType.REGULAR  | Status.RETIRED | 1            | false
        'template assay'  | AssayType.TEMPLATE | Status.DRAFT   | 1            | false
        'everything good' | AssayType.REGULAR  | Status.DRAFT   | 1            | true
    }

    void 'test that OneItemPerNonFixedAttributeElement is wired into assay'() {
        given: 'an assay with some context items'
        final Assay assay = Assay.build()
        expect:
        assay.getGuidanceRules()*.class*.simpleName == ['MinimumOfOneBiologyGuidanceRule','OneItemPerNonFixedAttributeElementRule','ContextItemShouldHaveValueRule']
    }


}