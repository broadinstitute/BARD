package merge

import spock.lang.Shared

import static bard.db.enums.ExpectedValueType.*
import static bard.db.registration.AttributeType.Fixed
import static bard.db.registration.AttributeType.Free

import bard.db.enums.ExpectedValueType

/**
 * Created with IntelliJ IDEA.
 * User: xiaorong
 * Date: 3/21/13
 * Time: 2:23 PM
 * To change this template use File | Settings | File Templates.
 */

import bard.db.dictionary.Element
import bard.db.experiment.Experiment
import bard.db.experiment.ExperimentContextItem
import bard.db.experiment.ExperimentMeasure
import grails.buildtestdata.mixin.Build
import grails.test.mixin.Mock
import spock.lang.Specification
import spock.lang.Unroll
import bard.db.registration.Measure
import bard.db.registration.AssayContextMeasure
import bard.db.registration.AssayContextItem
import bard.db.registration.AssayContext
import bard.db.registration.Assay
import bard.db.experiment.ExperimentContext
import bard.db.registration.AttributeType

@Build([AssayContext, AssayContextItem, AssayContextMeasure, Measure, Assay, Experiment, ExperimentMeasure, Element])
@Mock([AssayContext, AssayContextItem, AssayContextMeasure, Measure, ExperimentMeasure, ExperimentContext, ExperimentContextItem])
@Unroll
class MergeAssayServiceUnitSpec extends Specification {

    MergeAssayService mergeAssayService = new MergeAssayService()

    void merge(Assay targetAssay, Assay srcAssay) {
        MergeAssayService mergeAssayService = new MergeAssayService()
        String modifiedBy = 'xx'
        def removingAssays = [srcAssay]

        mergeAssayService.mergeAssayContextItem(removingAssays, targetAssay, modifiedBy)  // merege assay contextitem, experiment contextitem

        mergeAssayService.handleExperiments(removingAssays, targetAssay, modifiedBy)     // associate experiments with kept

        mergeAssayService.handleDocuments(removingAssays, targetAssay, modifiedBy)       // associate document

        mergeAssayService.handleMeasure(null, removingAssays, targetAssay, modifiedBy)         // associate measure
    }

    void verifyMeasuresAreConsistent(Assay assay) {
        for (experiment in assay.experiments) {
            for (experimentMeasure in experiment.experimentMeasures) {
                assert experimentMeasure.measure.assay == assay
            }
        }
    }

    void "test merging measures"() {
        mockDomain(Measure)

        Element rtA = Element.build(label: "element1")
        Element rtB = Element.build(label: "element2")
        Element statModifier = Element.build(label: "modifier")

        Assay assayA = Assay.build()
        Measure measureAA = Measure.build(assay: assayA, resultType: rtA)
        Measure measureAB = Measure.build(assay: assayA, resultType: rtB)

        Assay assayB = Assay.build()
        Experiment experimentB = Experiment.build(assay: assayB)
        Measure measureBA = Measure.build(assay: assayB, resultType: rtA)
        Measure measureBB = Measure.build(assay: assayB, resultType: rtB, statsModifier: statModifier) // having a different modifier means this is a different measurement

        when:
        merge(assayA, assayB)

        then: "We should end up with three measures:  rtA, rtB and (rtB, statsModifier)"
        assayA.measures.size() == 3
    }

    void "test moving measures"() {
        given: 'a pair of assays with two measures, and each has an experiment with two experiment measures. One of the measures is the same, one of the measures is different'
        mockDomain(Measure)
        mockDomain(ExperimentMeasure)

        Element rtA = Element.build(label: "element1")
        Element rtB = Element.build(label: "element2")
        Element rtC = Element.build(label: "element3")

        Assay assayA = Assay.build()
        Experiment experimentA = Experiment.build(assay: assayA)
        Measure measureAA = Measure.build(assay: assayA, resultType: rtA)
        Measure measureAB = Measure.build(assay: assayA, resultType: rtB) // the same measure is collected in both assays
        experimentA.addToExperimentMeasures(ExperimentMeasure.build(measure: measureAA))
        experimentA.addToExperimentMeasures(ExperimentMeasure.build(measure: measureAB))

        Assay assayB = Assay.build()
        Experiment experimentB = Experiment.build(assay: assayB)
        Measure measureBA = Measure.build(assay: assayB, resultType: rtB)
        Measure measureBB = Measure.build(assay: assayB, resultType: rtC)
        experimentB.addToExperimentMeasures(ExperimentMeasure.build(measure: measureBA))
        experimentB.addToExperimentMeasures(ExperimentMeasure.build(measure: measureBB))

        when:
        merge(assayA, assayB)

        then: "we should have two experiments on the assay"
        assayA.experiments.size() == 2

        then: "we should have three measures.  One got newly created, one existed before, and one was shared"
        assayA.measures.size() == 3

        then: "All experiment measures should point to the target assay"
        verifyMeasuresAreConsistent(assayA)

        then: "Each experiment should still have the same measures as before"
        experimentA.experimentMeasures.size() == 2
        experimentB.experimentMeasures.size() == 2
    }

    void "test measures with context association"() {
        setup:
        "we have two assays, with measures that will be merged, and one has a link to a context"
        Element rt = Element.build(label: "element1")

        Assay assayA = Assay.build()
        Experiment experimentA = Experiment.build(assay: assayA)
        Measure measureAA = Measure.build(assay: assayA, resultType: rt)

        Assay assayB = Assay.build()
        Experiment experimentB = Experiment.build(assay: assayB)
        Measure measureBA = Measure.build(assay: assayB, resultType: rt)
        AssayContext contextB = AssayContext.build(assay: assayB)
        Element attributeElement = Element.build(label: 'text attr', expectedValueType: ExpectedValueType.FREE_TEXT)
        AssayContextItem.build(assayContext: contextB, attributeElement: attributeElement, valueDisplay: "value display")
        AssayContextMeasure link = AssayContextMeasure.build(assayContext: contextB, measure: measureBA)

        when:
        merge(assayA, assayB)

        then: "we should end up with the link on the measure on the target assay"
        assayA.experiments.size() == 2
        assayA.measures.size() == 1
        Measure measure = assayA.measures.first()
        measure.assayContextMeasures.size() == 1
        AssayContextMeasure assayContextMeasure = measure.assayContextMeasures.first()
        assayContextMeasure.assayContext.assay == assayA
    }

    void "test merging of assay contexts where fixed items mismatch"() {
        setup:
        Element attribute = Element.build(label: "element1", expectedValueType: ExpectedValueType.ELEMENT)
        Element valueA = Element.build(label: "element2")
        Element valueB = Element.build(label: "element3")

        Assay assayA = Assay.build()
        AssayContext contextA = AssayContext.build(assay: assayA)
        AssayContextItem itemA = AssayContextItem.build(assayContext: contextA, attributeType: AttributeType.Fixed, attributeElement: attribute, valueElement: valueA, valueDisplay: "value display")
        Experiment experimentA = Experiment.build(assay: assayA)

        Assay assayB = Assay.build()
        AssayContext contextB = AssayContext.build(assay: assayB)
        AssayContextItem itemB = AssayContextItem.build(assayContext: contextB, attributeType: AttributeType.Fixed, attributeElement: attribute, valueElement: valueB, valueDisplay: "value display")
        Experiment experimentB = Experiment.build(assay: assayB)

        when:
        merge(assayA, assayB)

        then: "the context item should have turned into a list and each experiment should now have a context item"
        experimentA.experimentContexts.size() == 1
        experimentB.experimentContexts.size() == 1
        ExperimentContextItem expItemA = experimentA.experimentContexts.first().contextItems.first()
        ExperimentContextItem expItemB = experimentB.experimentContexts.first().contextItems.first()
        expItemA.attributeElement == attribute
        expItemA.valueElement == valueA
        expItemB.attributeElement == attribute
        expItemB.valueElement == valueB
        contextA.contextItems.size() == 2
        contextA.contextItems.first().attributeType == AttributeType.List
    }

    void "test merging of assay contexts where fixed items match"() {
        setup:
        Element valueA = Element.build(label: "element1")
        Element attribute = Element.build(label: "element2", expectedValueType: ExpectedValueType.ELEMENT)

        Assay assayA = Assay.build()
        AssayContext contextA = AssayContext.build(assay: assayA)
        AssayContextItem itemA = AssayContextItem.build(assayContext: contextA, attributeType: AttributeType.Fixed, attributeElement: attribute, valueElement: valueA, valueDisplay: "value display")
        Experiment experimentA = Experiment.build(assay: assayA)

        Assay assayB = Assay.build()
        AssayContext contextB = AssayContext.build(assay: assayB)
        AssayContextItem itemB = AssayContextItem.build(assayContext: contextB, attributeType: AttributeType.Fixed, attributeElement: attribute, valueElement: valueA, valueDisplay: "value Display")
        Experiment experimentB = Experiment.build(assay: assayB)

        when:
        merge(assayA, assayB)

        then: "the assay contexts should not have changed nor the experiment contexts changed"
        experimentA.experimentContexts.size() == 0
        experimentB.experimentContexts.size() == 0
        contextA.contextItems.size() == 2
        contextA.contextItems.first().attributeType == AttributeType.Fixed
    }

    //TODO: add more tests
    void testIsAssayContextItemEquals() {
        setup:
        final Element freeText = Element.build(id: 1, label: "e1", expectedValueType: FREE_TEXT)
        final Element anotherFreeText = Element.build(id: 2, label: "e2", expectedValueType: FREE_TEXT)

        final Element dictionary = Element.build(expectedValueType: ELEMENT)
        final Element anotherDictionary = Element.build(expectedValueType: ELEMENT)
        final Element valueElement = Element.build(id: 3, label: "e3")

        final Element externalOntology = Element.build(id: 1, label: "ex1", externalURL: "xurl", expectedValueType: EXTERNAL_ONTOLOGY)

        double value = 11.01
        String extValueId = "external value id"
        String valueDisplay = "value for display"

        when:
        AssayContextItem aci1 = AssayContextItem.build(attributeElement: freeText, valueDisplay: valueDisplay)
        AssayContextItem aci2 = AssayContextItem.build(attributeElement: anotherFreeText, valueDisplay: valueDisplay)

        then: "these two item are not the same because attribute element are not match"
        assert !mergeAssayService.isAssayContextItemEquals(aci1, aci2)

        when:
        aci1 = AssayContextItem.build(attributeElement: freeText, attributeType: Free, valueDisplay: null)
        aci2 = AssayContextItem.build(attributeElement: freeText, attributeType: Free, valueDisplay: null)
        then: """these two item are the same because attribute are the same, and type are FREE"""
        assert mergeAssayService.isAssayContextItemEquals(aci1, aci2)

        when:
        aci2.attributeType = AttributeType.Range
        then: "this two item are not the same because attribute are the same, and type are different, other fields are not the same"
        assert !mergeAssayService.isAssayContextItemEquals(aci1, aci2)

        when:
        aci1 = AssayContextItem.build(attributeElement: dictionary, valueElement: anotherFreeText, attributeType: Fixed, valueDisplay: valueDisplay)
        aci2 = AssayContextItem.build(attributeElement: dictionary, valueElement: anotherFreeText, attributeType: Fixed, valueDisplay: valueDisplay)
        then: "this two items are the same since they have same valueElement"
        assert mergeAssayService.isAssayContextItemEquals(aci1, aci2)

        when:
        aci2.valueElement = valueElement
        then: "This two are not the same since they have different valueElement"
        assert !mergeAssayService.isAssayContextItemEquals(aci1, aci2)

        when:
        aci2.valueElement = null
        then: "This two are not the same since one valueElement is null, another one is not"
        assert !mergeAssayService.isAssayContextItemEquals(aci1, aci2)

        when:
        aci1 = AssayContextItem.build(attributeElement: externalOntology, extValueId: extValueId, attributeType: Fixed, valueDisplay: valueDisplay)
        aci2 = AssayContextItem.build(attributeElement: externalOntology, extValueId: extValueId, attributeType: Fixed, valueDisplay: valueDisplay)
        then: "This two are the same due to same extValueId"
        assert mergeAssayService.isAssayContextItemEquals(aci1, aci2)
        when:
        aci2.extValueId = extValueId + " and some more stuff"
        then: "This two are not the same due to extValueId"
        assert !mergeAssayService.isAssayContextItemEquals(aci1, aci2)
        when:
        aci2.extValueId = null
        then: "This two are not the same, one extValueId is null"
        assert !mergeAssayService.isAssayContextItemEquals(aci1, aci2)
    }

//    was looking at converting the above test to a where block style test
//    @Shared Element lhsAttributeElement
//    @Shared Element rhsAttributeElement
//    void "testIsAssayContextItemEquals with where block #desc"() {
//
//        given:
//        lhsAttributeElement = lhsAttribute.call()
//        rhsAttributeElement = rhsAttribute.call()
//
//        lhsItemMap.attributeElement = lhsAttributeElement
//        rhsItemMap.attributeElement = rhsAttributeElement
//
//        when:
//        AssayContextItem lhsItem = AssayContextItem.build(lhsItemMap)
//        AssayContextItem rhsItem = AssayContextItem.build(rhsItemMap)
//
//        then:
//        mergeAssayService.isAssayContextItemEquals(lhsItem, rhsItem) == expectedEquals
//
//        where:
//        desc                                             | expectedEquals | lhsAttribute                                    | rhsAttribute                                    | lhsItemMap                | rhsItemMap
//        "not equals attribute elements don't match"      | false          | { Element.build(expectedValueType: FREE_TEXT) } | { Element.build(expectedValueType: FREE_TEXT) } | [valueDisplay: "display"] | [valueDisplay: "display"]
//        "equals attributes same and attribute Type FREE" | true           | { Element.build(expectedValueType: FREE_TEXT) } | { lhsAttributeElement }                         | [attributeType: Free]     | [attributeType: Free]
//
//    }
}
