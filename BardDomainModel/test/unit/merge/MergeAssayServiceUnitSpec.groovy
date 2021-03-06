/* Copyright (c) 2014, The Broad Institute
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
    * Redistributions of source code must retain the above copyright
      notice, this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright
      notice, this list of conditions and the following disclaimer in the
      documentation and/or other materials provided with the distribution.
    * Neither the name of The Broad Institute nor the
      names of its contributors may be used to endorse or promote products
      derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL The Broad Institute BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package merge

import bard.db.dictionary.Element
import bard.db.enums.ExpectedValueType
import bard.db.enums.Status
import bard.db.experiment.AssayContextExperimentMeasure
import bard.db.experiment.Experiment
import bard.db.registration.Assay
import bard.db.experiment.ExperimentContext
import bard.db.experiment.ExperimentContextItem
import bard.db.experiment.ExperimentMeasure
import bard.db.registration.*
import grails.buildtestdata.mixin.Build
import grails.test.mixin.Mock
import grails.test.mixin.TestMixin
import grails.test.mixin.support.GrailsUnitTestMixin
import spock.lang.Specification
import spock.lang.Unroll

import static bard.db.enums.ExpectedValueType.*
import static bard.db.registration.AttributeType.Fixed
import static bard.db.registration.AttributeType.Free

/**
 * Created with IntelliJ IDEA.
 * Date: 3/21/13
 * Time: 2:23 PM
 * To change this template use File | Settings | File Templates.
 */
@Build([AssayContext, AssayContextItem, Assay, Experiment, ExperimentMeasure, Element, AssayContextExperimentMeasure])
@Mock([AssayContext, AssayContextItem, Assay, Experiment, ExperimentMeasure, ExperimentContext, ExperimentContextItem, AssayContextExperimentMeasure])
@TestMixin(GrailsUnitTestMixin)
@Unroll
class MergeAssayServiceUnitSpec extends Specification {

    MergeAssayService mergeAssayService = new MergeAssayService()

    void merge(Assay targetAssay, Assay srcAssay) {
        MergeAssayService mergeAssayService = new MergeAssayService()
        String modifiedBy = 'xx'
        def removingAssays = [srcAssay]

        mergeAssayService.handleExperiments(removingAssays, targetAssay, modifiedBy)
        // associate experiments with kept

        mergeAssayService.handleMeasures(targetAssay, removingAssays)         // associate measure
    }

    void verifyMeasuresAreConsistent(Assay assay) {
        for (experiment in assay.experiments) {
            for (experimentMeasure in experiment.experimentMeasures) {
                assert experimentMeasure.experiment.assay == assay
            }
        }
    }

//    void "test moving measures"() {
//        given: 'a pair of assays with two measures, and each has an experiment with two experiment measures. One of the measures is the same, one of the measures is different'
//        mockDomain(Measure)
//        mockDomain(ExperimentMeasure)
//
//        Element rtA = Element.build(label: "element1")
//        Element rtB = Element.build(label: "element2")
//        Element rtC = Element.build(label: "element3")
//
//        Assay assayA = Assay.build()
//        Experiment experimentA = Experiment.build(assay: assayA)
//        Measure measureAA = Measure.build(assay: assayA, resultType: rtA)
//        Measure measureAB = Measure.build(assay: assayA, resultType: rtB) // the same measure is collected in both assays
//        experimentA.addToExperimentMeasures(ExperimentMeasure.build(measure: measureAA))
//        experimentA.addToExperimentMeasures(ExperimentMeasure.build(measure: measureAB))
//
//        Assay assayB = Assay.build()
//        Experiment experimentB = Experiment.build(assay: assayB)
//        Measure measureBA = Measure.build(assay: assayB, resultType: rtB)
//        Measure measureBB = Measure.build(assay: assayB, resultType: rtC)
//        experimentB.addToExperimentMeasures(ExperimentMeasure.build(measure: measureBA))
//        experimentB.addToExperimentMeasures(ExperimentMeasure.build(measure: measureBB))
//
//        when:
//        merge(assayA, assayB)
//
//        then: "we should have two experiments on the assay"
//        assayA.experiments.size() == 2
//
//        then: "we should have three measures.  One got newly created, one existed before, and one was shared"
//        assayA.measures.size() == 3
//
//        then: "All experiment measures should point to the target assay"
//        verifyMeasuresAreConsistent(assayA)
//
//        then: "Each experiment should still have the same measures as before"
//        experimentA.experimentMeasures.size() == 2
//        experimentB.experimentMeasures.size() == 2

//        Assay assayA = Assay.build()
//        Measure measureAA = Measure.build(assay: assayA, resultType: rtA)
//        Measure measureAB = Measure.build(assay: assayA, resultType: rtB)
//
//        Assay assayB = Assay.build()
//        Experiment experimentB = Experiment.build(assay: assayB)
//        Measure measureBA = Measure.build(assay: assayB, resultType: rtA)
//        Measure measureBB = Measure.build(assay: assayB, resultType: rtB, statsModifier: statModifier) // having a different modifier means this is a different measurement
//
//        Assay.metaClass.isDirty = { return false }
//
//        when:
//        merge(assayA, assayB)
//
//        then: "We should end up with three measures:  rtA, rtB and (rtB, statsModifier)"
//        assayA.measures.size() == 3
//    }

    void "test moving measures"() {
        given: 'a pair of experiments with a measure'
        mockDomain(ExperimentMeasure)

        Element rtA = Element.build(label: "element1")
        Element rtB = Element.build(label: "element2")

        Assay assayA = Assay.build()
        Experiment experimentA = Experiment.build(assay: assayA)
        ExperimentMeasure measureAA = ExperimentMeasure.build(experiment: experimentA, resultType: rtA)

        Assay assayB = Assay.build()
        Experiment experimentB = Experiment.build(assay: assayB)
        ExperimentMeasure measureBA = ExperimentMeasure.build(experiment: experimentB, resultType: rtB)
        Assay.metaClass.isDirty =
                { return false }
        when:
        merge(assayA, assayB)

        then: "we should have two experiments on the assay"
        assayA.experiments.size() == 2

        then: "Each experiment should still have the same measures as before"
        experimentA.experimentMeasures.size() == 1
        experimentB.experimentMeasures.size() == 1
    }

    void "test measures with context association"() {
        setup:
        "we have two experiments, with measures that will be merged, and one has a link to a context"
        Element rt = Element.build(label: "element1")

        Assay assayA = Assay.build()
        Experiment experimentA = Experiment.build(assay: assayA)
        ExperimentMeasure experimentMeasureAA = ExperimentMeasure.build(experiment: experimentA, resultType: rt)

        Assay assayB = Assay.build()
        Experiment experimentB = Experiment.build(assay: assayB)
        ExperimentMeasure experimentMeasureBA = ExperimentMeasure.build(experiment: experimentB, resultType: rt)

        AssayContext contextB = AssayContext.build(assay: assayB)
        Element attributeElement = Element.build(label: 'text attr', expectedValueType: ExpectedValueType.FREE_TEXT)
        AssayContextItem.build(assayContext: contextB, attributeElement: attributeElement, valueDisplay: "value display")
        AssayContextExperimentMeasure link = AssayContextExperimentMeasure.build(assayContext: contextB, experimentMeasure: experimentMeasureBA)
        Assay.metaClass.isDirty =
                { return false }
        when:
        merge(assayA, assayB)

        then: "we should end up with the link on the measure on the target assay"
        assayA.experiments.size() == 2
        experimentA.experimentMeasures.size() == 1
        ExperimentMeasure experimentMeasure = experimentB.experimentMeasures.first()
        AssayContextExperimentMeasure assayContextMeasure = experimentMeasure.assayContextExperimentMeasures.first()
        assayContextMeasure.assayContext.assay == assayB
    }

    void "test retire assay(s) with no experiments"() {
        setup:
        String user = "testUser"
        Assay sourceAssay1 = Assay.build(assayStatus: Status.APPROVED)
        Assay sourceAssay4 = Assay.build(assayStatus: Status.PROVISIONAL)
        Assay sourceAssay2 = Assay.build(assayStatus: Status.APPROVED)
        Assay sourceAssay3 = Assay.build(assayStatus: Status.APPROVED)
        Experiment experimentA = Experiment.build(assay: sourceAssay3)
        Experiment experimentB = Experiment.build(assay: sourceAssay3)
        List<Assay> assays = [sourceAssay1, sourceAssay2, sourceAssay3, sourceAssay4]

        when:
        mergeAssayService.retireAssaysWithNoExperiments(assays, user)

        then:
        assert sourceAssay1.assayStatus == Status.RETIRED
        assert sourceAssay1.modifiedBy == user
        assert sourceAssay4.assayStatus == Status.RETIRED
        assert sourceAssay4.modifiedBy == user
        assert sourceAssay2.assayStatus == Status.RETIRED
        assert sourceAssay2.modifiedBy == user
        assert sourceAssay3.assayStatus == Status.APPROVED
        assert sourceAssay3.modifiedBy == null
    }

}

