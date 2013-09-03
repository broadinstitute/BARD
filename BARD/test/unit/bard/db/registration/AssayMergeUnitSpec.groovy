package bard.db.registration

import bard.db.dictionary.Element
import bard.db.experiment.AssayContextExperimentMeasure
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

@Build([AssayContext, AssayContextItem, AssayContextExperimentMeasure, ExperimentMeasure, Assay, Experiment, ExperimentMeasure])
@Mock([AssayContext, AssayContextItem, AssayContextExperimentMeasure, ExperimentMeasure])
@Ignore
class AssayMergeUnitSpec extends Specification {

    void merge(Assay targetAssay, Assay srcAssay) {
        throw new NotImplementedException();
    }

    void verifyMeasuresAreConsistent(Experiment experiment) {
        //  for(experiment in assay.experiments) {
        for (experimentMeasure in experiment.experimentMeasures) {
            assert experimentMeasure.experiment == experiment
        }
        //  }
    }

    void "test merging measures"() {
        Element rtA = Element.build()
        Element rtB = Element.build()
        Element statModifier = Element.build()

        Assay assayA = Assay.build()
        Experiment experimentA = Experiment.build(assay: assayA)
        ExperimentMeasure measureAA = ExperimentMeasure.build(experiment: experimentA, resultType: rtA)
        ExperimentMeasure measureAB = ExperimentMeasure.build(experiment: experimentA, resultType: rtB)

        Assay assayB = Assay.build()
        Experiment experimentB = Experiment.build(assay: assayB)
        ExperimentMeasure measureBA = ExperimentMeasure.build(experiment: experimentB, resultType: rtA)
        ExperimentMeasure measureBB = ExperimentMeasure.build(experiment: experimentB, resultType: rtB, statsModifier: statModifier) // having a different modifier means this is a different measurement

        when:
        merge(assayA, assayB)

        then: "We should end up with three measures:  rtA, rtB and (rtB, statsModifier)"
        assayA.experiments.first().experimentMeasures.size() == 2
        assayB.experiments.first().experimentMeasures.size() == 2
    }

    void "test moving measures"() {
        given: 'a pair of assays with two measures, and each has an experiment with two experiment measures. One of the measures is the same, one of the measures is different'

        Element rtA = Element.build()
        Element rtB = Element.build()
        Element rtC = Element.build()

        Assay assayA = Assay.build()
        Experiment experimentA = Experiment.build(assay: assayA)
        ExperimentMeasure measureAA = ExperimentMeasure.build(experiment: experimentA, resultType: rtA)
        ExperimentMeasure measureAB = ExperimentMeasure.build(experiment: experimentA, resultType: rtB) // the same measure is collected in both assays

        Assay assayB = Assay.build()
        Experiment experimentB = Experiment.build(assay: assayB)
        ExperimentMeasure measureBA = ExperimentMeasure.build(experiment: experimentB, resultType: rtB)
        ExperimentMeasure measureBB = ExperimentMeasure.build(experiment: experimentB, resultType: rtC)

        when:
        merge(assayA, assayB)

        then: "Each experiment should still have the same measures as before"
        experimentA.experimentMeasures.size() == 2
        experimentB.experimentMeasures.size() == 2
    }

//    void "test measures with context association"() {
//        setup:
//        "we have two assays, with measures that will be merged, and one has a link to a context"
//        Element rt = Element.build()
//
//        Assay assayA = Assay.build()
//        Experiment experimentA = Experiment.build(assay: assayA)
//        Measure measureAA = Measure.build(assay: assayA, resultType: rt)
//
//        Assay assayB = Assay.build()
//        Experiment experimentB = Experiment.build(assay: assayB)
//        Measure measureBA = Measure.build(assay: assayB, resultType: rt)
//        AssayContext contextB = AssayContext.build(assay: assayB)
//        AssayContextMeasure link = AssayContextMeasure.build(assayContext: contextB, measure: measureBA)
//
//        when:
//        merge(assayA, assayB)
//
//        then:  "we should end up with the link on the measure on the target assay"
//        assayA.experiments.size() == 2
//        assayA.measures.size() == 1
//        Measure measure = assayA.measures.first()
//        measure.assayContextExperimentMeasures.size() == 1
//        AssayContextMeasure assayContextMeasure = measure.assayContextExperimentMeasures.first()
//        assayContextMeasure.assayContext.assay == assayA
//    }

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
        AssayContext contextB = AssayContext.build(assay: assayB)
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
        AssayContext contextB = AssayContext.build(assay: assayB)
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
