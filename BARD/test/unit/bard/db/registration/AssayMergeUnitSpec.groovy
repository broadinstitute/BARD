package bard.db.registration

import bard.db.dictionary.Element
import bard.db.experiment.Experiment
import bard.db.experiment.ExperimentContextItem
import bard.db.experiment.ExperimentMeasure
import grails.buildtestdata.mixin.Build
import grails.test.mixin.Mock
import org.apache.commons.lang.NotImplementedException
import spock.lang.Ignore
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

@Build([AssayContext, AssayContextItem, AssayContextMeasure, Measure, Assay, Experiment, ExperimentMeasure])
@Mock([AssayContext, AssayContextItem, AssayContextMeasure, Measure])
@Ignore
class AssayMergeUnitSpec extends Specification {

    void merge(Assay targetAssay, Assay srcAssay) {
        throw new NotImplementedException();
    }

    void verifyMeasuresAreConsistent(Assay assay) {
        for(experiment in assay.experiments) {
            for(experimentMeasure in experiment.experimentMeasures) {
                assert experimentMeasure.measure.assay == assay
            }
        }
    }

    void "test merging measures"() {
        Element rtA = Element.build()
        Element rtB = Element.build()
        Element statModifier = Element.build()

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

        Element rtA = Element.build()
        Element rtB = Element.build()
        Element rtC = Element.build()

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

        then:  "we should have two experiments on the assay"
        assayA.experiments.size() == 2

        then:  "we should have three measures.  One got newly created, one existed before, and one was shared"
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
        Element rt = Element.build()

        Assay assayA = Assay.build()
        Experiment experimentA = Experiment.build(assay: assayA)
        Measure measureAA = Measure.build(assay: assayA, resultType: rt)

        Assay assayB = Assay.build()
        Experiment experimentB = Experiment.build(assay: assayB)
        Measure measureBA = Measure.build(assay: assayB, resultType: rt)
        AssayContext contextB = AssayContext.build(assay: assayB)
        AssayContextMeasure link = AssayContextMeasure.build(assayContext: contextB, measure: measureBA)

        when:
        merge(assayA, assayB)

        then:  "we should end up with the link on the measure on the target assay"
        assayA.experiments.size() == 2
        assayA.measures.size() == 1
        Measure measure = assayA.measures.first()
        measure.assayContextMeasures.size() == 1
        AssayContextMeasure assayContextMeasure = measure.assayContextMeasures.first()
        assayContextMeasure.assayContext.assay == assayA
    }

    void "test merging of assay contexts where fixed items mismatch"() {
        setup:
        Element attribute = Element.build()
        Element valueA = Element.build()
        Element valueB = Element.build()

        Assay assayA = Assay.build()
        AssayContext contextA = AssayContext.build(assay: assayA)
        AssayContextItem itemA = AssayContextItem.build(assayContext: contextA, attributeType: AttributeType.Fixed, attributeElement: attribute, valueElement: valueA)
        Experiment experimentA = Experiment.build(assay: assayA)

        Assay assayB = Assay.build()
        AssayContext contextB = AssayContext.build(assay:  assayB)
        AssayContextItem itemB = AssayContextItem.build(assayContext: contextB, attributeType: AttributeType.Fixed, attributeElement: attribute, valueElement: valueB)
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
        Element valueA = Element.build()
        Element attribute = Element.build()

        Assay assayA = Assay.build()
        AssayContext contextA = AssayContext.build(assay: assayA)
        AssayContextItem itemA = AssayContextItem.build(assayContext: contextA, attributeType: AttributeType.Fixed, attributeElement: attribute, valueElement: valueA)
        Experiment experimentA = Experiment.build(assay: assayA)

        Assay assayB = Assay.build()
        AssayContext contextB = AssayContext.build(assay:  assayB)
        AssayContextItem itemB = AssayContextItem.build(assayContext: contextB, attributeType: AttributeType.Fixed, attributeElement: attribute, valueElement: valueA)
        Experiment experimentB = Experiment.build(assay: assayB)

        when:
        merge(assayA, assayB)

        then: "the assay contexts should not have changed nor the experiment contexts changed"
        experimentA.experimentContexts.size() == 0
        experimentB.experimentContexts.size() == 0
        contextA.contextItems.size() == 1
        contextA.contextItems.first().attributeType == AttributeType.Fixed
    }
}
