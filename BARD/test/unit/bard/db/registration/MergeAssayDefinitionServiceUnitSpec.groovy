package bard.db.registration

import bard.db.dictionary.Element
import bard.db.experiment.Experiment
import bard.db.experiment.ExperimentContext
import bard.db.experiment.ExperimentContextItem
import bard.db.experiment.ExperimentMeasure
import grails.buildtestdata.mixin.Build
import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import grails.test.mixin.TestMixin
import grails.test.mixin.services.ServiceUnitTestMixin
import spock.lang.IgnoreRest
import spock.lang.Specification
import spock.lang.Unroll

/**
 * Created with IntelliJ IDEA.
 * User: jasiedu
 * Date: 5/2/13
 * Time: 2:07 PM
 * To change this template use File | Settings | File Templates.
 */
@Build([Assay, Measure, Experiment, AssayContextMeasure, AssayContext, ExperimentMeasure, AssayContextItem, ExperimentContextItem, ExperimentContext, Element])
@Mock([Assay, Measure, Experiment, ExperimentMeasure, AssayContext, AssayContextMeasure, AssayContextItem, ExperimentContextItem, ExperimentContext, Element])
@TestMixin(ServiceUnitTestMixin)
@TestFor(MergeAssayDefinitionService)
@Unroll
public class MergeAssayDefinitionServiceUnitSpec extends Specification {

    void 'test validateExperimentContextItem - no assay context item in map'() {
        given:
        final ExperimentContextItem experimentContextItem = ExperimentContextItem.build()
        final Map<Element, AssayContextItem> targetElementToAssayContextItemMap = [:]
        final List<String> errorMessages = []
        when:
        service.validateExperimentContextItem(experimentContextItem, targetElementToAssayContextItemMap, errorMessages)
        then:
        assert errorMessages
        assert errorMessages.get(0) == "Experiment Context Item : ${experimentContextItem.id}, on Experiment: ${experimentContextItem.experimentContext.experiment.id}, which is part of the Assay: ${experimentContextItem.experimentContext.experiment.assay.id} does not exist as a context item on the target assay"
    }

    void 'test validateExperimentContextItem - assay context item has fixed attribute'() {
        given:
        Assay assayOne = Assay.build()
        Element element = Element.build()
        AssayContext contextOne = AssayContext.build(assay: assayOne, contextName: "alpha")
        AssayContextItem assayContextItem = AssayContextItem.build(assayContext: contextOne, attributeType: AttributeType.Fixed, attributeElement: element)
        Measure measureOne = Measure.build(assay: assayOne)
        AssayContextMeasure.build(assayContext: contextOne, measure: measureOne)

        final ExperimentContextItem experimentContextItem = ExperimentContextItem.build(attributeElement: element)
        final Map<Element, AssayContextItem> targetElementToAssayContextItemMap = [:]
        targetElementToAssayContextItemMap.put(element, assayContextItem)
        final List<String> errorMessages = []
        when:
        service.validateExperimentContextItem(experimentContextItem, targetElementToAssayContextItemMap, errorMessages)
        then:
        assert errorMessages
        assert errorMessages.get(0) == "Cannot validate Experiment Context Item : ${experimentContextItem.id}, on Experiment: ${experimentContextItem.experimentContext.experiment.id}, which is part of the Assay: ${experimentContextItem.experimentContext.experiment.assay.id}  because the target context item 1 has an attribute type of Fixed"
    }

    void 'test validateConfirmMergeInputs - exceptions #desc'() {
        when:
        service.validateConfirmMergeInputs(targetAssayId, assayIdsToMerge, idType)

        then:
        RuntimeException e = thrown()
        assert e.message == expectedErrorMessage
        where:
        desc                                | targetAssayId | assayIdsToMerge | idType      | expectedErrorMessage
        "Target Assay Id is null"           | null          | "23"            | IdType.ADID | "The ID of the Assay to merge into is required and must be a number"
        "Target Assay Ids to merge is null" | 23            | null            | IdType.ADID | "Enter at least one Assay Definition ID(ADID) to move"
        "Assay Id Type is null"             | 23            | "23"            | null        | "Select one of Assay Definition ID or PubChem AID or Experiment ID"
    }

    void 'test validateConfirmMergeInputs - success'() {
        when:
        service.validateConfirmMergeInputs(targetAssayId, assayIdsToMerge, idType)

        then:
        notThrown(RuntimeException)
        where:
        desc                   | targetAssayId | assayIdsToMerge | idType
        "AssayIdType is ADID"  | 2             | "23"            | IdType.ADID
        "Assay Id Type is AID" | 3             | "23"            | IdType.AID
    }

    void 'test convertAssaysToMerge - Merge Assay with itself as target'() {
        given:
        Assay assayOne = Assay.build()
        AssayContext contextOne = AssayContext.build(assay: assayOne, contextName: "alpha")
        AssayContextItem.build(assayContext: contextOne)
        Measure measureOne = Measure.build(assay: assayOne)
        AssayContextMeasure.build(assayContext: contextOne, measure: measureOne)
        when:
        service.normalizeEntitiesToMoveToExperimentIds([assayOne.id], IdType.ADID, assayOne)

        then:
        RuntimeException e = thrown()
        assert e.message == "Assay with Assay Definition ID ${assayOne.id} cannot be merged into itself. Please remove it from the 'Assays to move list'"
    }

    void 'test normalizeEntitiesToMoveToExperimentIds - Assays to move contains non-existing assays'() {
        given:
        Long nonExistingAssayId = -1
        Assay assayOne = Assay.build()
        AssayContext contextOne = AssayContext.build(assay: assayOne, contextName: "alpha")
        AssayContextItem.build(assayContext: contextOne)
        Measure measureOne = Measure.build(assay: assayOne)
        AssayContextMeasure.build(assayContext: contextOne, measure: measureOne)

        Assay assayTwo = Assay.build()
        AssayContext contextTwo = AssayContext.build(assay: assayTwo, contextName: "alpha2")
        AssayContextItem.build(assayContext: contextTwo)
        Measure measureTwo = Measure.build(assay: assayTwo)
        AssayContextMeasure.build(assayContext: contextTwo, measure: measureTwo)
        when:
        service.normalizeEntitiesToMoveToExperimentIds([assayTwo.id, nonExistingAssayId], IdType.ADID, assayOne)

        then:
        RuntimeException e = thrown()
        assert e.message == "Could not find assays with Assay Definition ID: ${nonExistingAssayId}"
    }

    void 'test convertAssaysToMerge - success'() {
        setup:

        Assay assayOne = Assay.build()
        AssayContext contextOne = AssayContext.build(assay: assayOne, contextName: "alpha")
        AssayContextItem.build(assayContext: contextOne)
        Measure measureOne = Measure.build(assay: assayOne)
        AssayContextMeasure.build(assayContext: contextOne, measure: measureOne)

        Assay assayTwo = Assay.build()
        Experiment experiment = Experiment.build(assay: assayTwo)
        AssayContext contextTwo = AssayContext.build(assay: assayTwo, contextName: "alpha2")
        AssayContextItem.build(assayContext: contextTwo)
        Measure measureTwo = Measure.build(assay: assayTwo)
        AssayContextMeasure.build(assayContext: contextTwo, measure: measureTwo)

        when:
        List<Long> assaysToMerge = service.normalizeEntitiesToMoveToExperimentIds([assayTwo.id], IdType.ADID, assayOne)

        then:
        assert assaysToMerge
        assert assaysToMerge.get(0) == experiment.id
    }

}
