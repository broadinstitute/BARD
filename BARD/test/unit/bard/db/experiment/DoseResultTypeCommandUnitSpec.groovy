package bard.db.experiment

import bard.db.dictionary.AssayDescriptor
import bard.db.dictionary.Descriptor
import bard.db.dictionary.Element
import bard.db.enums.ExpectedValueType
import bard.db.enums.HierarchyType
import bard.db.project.DoseResultTypeCommand
import bard.db.project.ExperimentController
import bard.db.registration.Assay
import bard.db.registration.AssayContext
import bard.db.registration.AssayContextItem
import grails.buildtestdata.mixin.Build
import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import spock.lang.Unroll

import static test.TestUtils.assertFieldValidationExpectations

/**
 * See the API for {@link grails.test.mixin.web.ControllerUnitTestMixin} for usage instructions
 */
@TestFor(ExperimentController)
@Build([Experiment, Element, ExperimentMeasure, AssayDescriptor, AssayContextItem, AssayContext])
@Mock([Assay, Experiment, Element, ExperimentMeasure, AssayDescriptor, AssayContextItem, AssayContext, AssayContextExperimentMeasure])
@Unroll
class DoseResultTypeCommandUnitSpec extends ResultTypeCommandAbstractUnitSpec {


    void "test findScreeningConcentrationContextItemWithNoSiblings - Not Found Assay Context"() {
        final Element concentrationResultType = Element.build(expectedValueType: ExpectedValueType.FREE_TEXT)
        final Element responseResultType = Element.build()
        final Experiment experiment = Experiment.build()
        final Element statsModifier = Element.build()
        final ExperimentMeasure parentExperimentMeasure = ExperimentMeasure.build()
        final HierarchyType hierarchyType = HierarchyType.CALCULATED_FROM
        AssayContextItem assayContextItem = AssayContextItem.build(attributeElement: concentrationResultType)

        Map m = [
                ontologyDataAccessService: this.ontologyDataAccessService,
                experimentId: experiment.id,
                concentrationResultTypeId: concentrationResultType.id,
                responseResultTypeId: responseResultType.id,
                statsModifierId: statsModifier.id,
                parentExperimentMeasureId: parentExperimentMeasure.id,
                parentChildRelationship: hierarchyType.id
        ]
        DoseResultTypeCommand doseResultTypeCommand = new DoseResultTypeCommand(m)
        List<AssayContextItem> assayContextItems = [assayContextItem]

        when:
        AssayContext assayContext = doseResultTypeCommand.findScreeningConcentrationContextItemWithNoSiblings(assayContextItems)
        then: "we find an assay context with a single assay context item"
        ontologyDataAccessService.getDescriptorsForAttributes(_) >> {
            []
        }
        assert !assayContext

    }

    void "test findScreeningConcentrationContextItemWithNoSiblings - Found Assay Context"() {
        final Element concentrationResultType = Element.build(expectedValueType: ExpectedValueType.FREE_TEXT)
        final Element responseResultType = Element.build()
        final Experiment experiment = Experiment.build()
        final Element statsModifier = Element.build()
        final ExperimentMeasure parentExperimentMeasure = ExperimentMeasure.build()
        final HierarchyType hierarchyType = HierarchyType.CALCULATED_FROM
        List<Descriptor> descriptors = [AssayDescriptor.build(element: concentrationResultType), AssayDescriptor.build(element: responseResultType)]
        AssayContextItem assayContextItem = AssayContextItem.build(attributeElement: concentrationResultType)

        Map m = [
                ontologyDataAccessService: this.ontologyDataAccessService,
                experimentId: experiment.id,
                concentrationResultTypeId: concentrationResultType.id,
                responseResultTypeId: responseResultType.id,
                statsModifierId: statsModifier.id,
                parentExperimentMeasureId: parentExperimentMeasure.id,
                parentChildRelationship: hierarchyType.id
        ]
        DoseResultTypeCommand doseResultTypeCommand = new DoseResultTypeCommand(m)
        List<AssayContextItem> assayContextItems = [assayContextItem]

        when:
        AssayContext assayContext = doseResultTypeCommand.findScreeningConcentrationContextItemWithNoSiblings(assayContextItems)
        then: "we find an assay context with a single assay context item"
        ontologyDataAccessService.getDescriptorsForAttributes(_) >> {
            descriptors
        }
        assert assayContext
        assert assayContext.assayContextItems.size() == 1


    }

    void "test validate fields #desc"() {
        given:
        DoseResultTypeCommand doseResultTypeCommand = new DoseResultTypeCommand(requiredFields.call())

        when:
        doseResultTypeCommand[(field)] = valueUnderTest.call()?.id
        doseResultTypeCommand.validate()
        then:
        assertFieldValidationExpectations(doseResultTypeCommand, field, valid, errorCode)
        where:
        desc                                                      | valueUnderTest                    | valid | errorCode  | field                       | requiredFields
        'experiment id is null'                                   | { null }                          | false | 'nullable' | "experimentId"              | { [concentrationResultTypeId: Element.build().id, responseResultTypeId: Element.build().id] }
        'experiment id is valid'                                  | { Experiment.build() }            | true  | null       | "experimentId"              | { [concentrationResultTypeId: Element.build().id, responseResultTypeId: Element.build().id] }

        'concentrationResultType id is null'                      | { null }                          | false | 'nullable' | "concentrationResultTypeId" | { [experimentId: Experiment.build().id, responseResultTypeId: Element.build().id] }
        'concentrationResultType id is valid'                     | { Element.build() }               | true  | null       | "concentrationResultTypeId" | { [experimentId: Experiment.build().id, responseResultTypeId: Element.build().id] }

        'responseResultType id is null'                           | { null }                          | false | 'nullable' | "responseResultTypeId"      | { [experimentId: Experiment.build().id, concentrationResultTypeId: Element.build().id] }
        'responseResultType id is valid'                          | { Element.build() }               | true  | null       | "responseResultTypeId"      | { [experimentId: Experiment.build().id, concentrationResultTypeId: Element.build().id] }

        'stats modifier id is null'                               | { null }                          | true  | null       | "statsModifierId"           | { [experimentId: Experiment.build().id, concentrationResultTypeId: Element.build().id, responseResultTypeId: Element.build().id] }
        'stats modifier id is valid'                              | { Element.build() }               | true  | null       | "statsModifierId"           | { [experimentId: Experiment.build().id, concentrationResultTypeId: Element.build().id, responseResultTypeId: Element.build().id] }

        'parentExperimentMeasure id no hierarchy'                 | { ExperimentMeasure.build() }     | true  | null       | "parentExperimentMeasureId" | { [experimentId: Experiment.build().id, concentrationResultTypeId: Element.build().id, responseResultTypeId: Element.build().id] }
        'parentExperimentMeasure id with hierarchy'               | { ExperimentMeasure.build() }     | true  | null       | "parentExperimentMeasureId" | { [experimentId: Experiment.build().id, concentrationResultTypeId: Element.build().id, responseResultTypeId: Element.build().id] }

        'parentChildRelationship id no parentExperimentMeasure'   | { HierarchyType.CALCULATED_FROM } | true  | null       | "parentChildRelationship"   | { [experimentId: Experiment.build().id, concentrationResultTypeId: Element.build().id, responseResultTypeId: Element.build().id] }
        'parentChildRelationship id with parentExperimentMeasure' | { HierarchyType.CALCULATED_FROM } | true  | null       | "parentChildRelationship"   | { [experimentId: Experiment.build().id, concentrationResultTypeId: Element.build().id, parentExperimentMeasureId: ExperimentMeasure.build().id, responseResultTypeId: Element.build().id] }
    }


    void "test createExperimentMeasures"() {
        given:
        final Element concentrationResultType = Element.build()
        final Element responseResultType = Element.build(expectedValueType: ExpectedValueType.FREE_TEXT)
        final Experiment experiment = Experiment.build()
        final Element statsModifier = Element.build()
        final ExperimentMeasure parentExperimentMeasure = ExperimentMeasure.build()
        final HierarchyType hierarchyType = HierarchyType.CALCULATED_FROM


        Map m = [
                experimentService: this.experimentService,
                ontologyDataAccessService: this.ontologyDataAccessService,
                experimentId: experiment.id,
                concentrationResultTypeId: concentrationResultType.id,
                responseResultTypeId: responseResultType.id,
                statsModifierId: statsModifier.id,
                parentExperimentMeasureId: parentExperimentMeasure.id,
                parentChildRelationship: hierarchyType.id
        ]
        DoseResultTypeCommand doseResultTypeCommand = new DoseResultTypeCommand(m)
        AssayContext assayContext = AssayContext.build(assay: experiment.assay)
        List<Descriptor> descriptors = [AssayDescriptor.build(element: concentrationResultType), AssayDescriptor.build(element: responseResultType)]
        AssayContextItem.build(attributeElement: responseResultType, assayContext: assayContext)
        when:
        List<ExperimentMeasure> experimentMeasures = doseResultTypeCommand.createExperimentMeasures()
        then:
        5 * experimentService.createNewExperimentMeasure(_,_,_,_,_,_,_)  >> {
            parentExperimentMeasure
        }

        ontologyDataAccessService.getDescriptorsForAttributes(_) >> {
            descriptors
        }
        assert experimentMeasures.size() == 5

        //3 of them are curve parameters
        //1 is the response measure
        //1 is the concentration measure
    }

    void "test getters"() {

        given:
        final Element concentrationResultType = Element.build()
        final Element responseResultType = Element.build()
        final Experiment experiment = Experiment.build()
        final Element statsModifier = Element.build()
        final ExperimentMeasure parentExperimentMeasure = ExperimentMeasure.build()
        final HierarchyType hierarchyType = HierarchyType.CALCULATED_FROM


        Map m = [
                ontologyDataAccessService: this.ontologyDataAccessService,
                experimentId: experiment.id,
                concentrationResultTypeId: concentrationResultType.id,
                responseResultTypeId: responseResultType.id,
                statsModifierId: statsModifier.id,
                parentExperimentMeasureId: parentExperimentMeasure.id,
                parentChildRelationship: hierarchyType.id
        ]


        when:
        DoseResultTypeCommand doseResultTypeCommand = new DoseResultTypeCommand(m)
        then:
        assert doseResultTypeCommand.getConcentrationResultType() == concentrationResultType
        assert doseResultTypeCommand.getResponseResultType() == responseResultType
        assert doseResultTypeCommand.getExperiment() == experiment
        assert doseResultTypeCommand.getStatsModifier() == statsModifier
        assert doseResultTypeCommand.parentChildRelationship == hierarchyType.id
        assert doseResultTypeCommand.getParentExperimentMeasure() == parentExperimentMeasure


    }

    void "test createDataResponseMap"() {
        given:
        DoseResultTypeCommand doseResultTypeCommand = new DoseResultTypeCommand(experimentId: Experiment.build().id)

        when:
        Map m = doseResultTypeCommand.createDataResponseMap()
        then:
        assert m.resultTypeCommand instanceof DoseResultTypeCommand
        assert m.currentExperimentMeasures.isEmpty()
    }

    void "test createMeasure"() {
        given:
        Experiment experiment = Experiment.build()
        String modifiedBy = "Me"
        boolean priorityElement = true
        Element resultType = Element.build()
        ExperimentMeasure parent = ExperimentMeasure.build()
        HierarchyType hierarchyType = HierarchyType.CALCULATED_FROM
        Element statsModifier = Element.build()
        DoseResultTypeCommand doseResultTypeCommand = new DoseResultTypeCommand(experimentService: this.experimentService)
        when:
        ExperimentMeasure experimentMeasure =
            doseResultTypeCommand.createExperimentMeasure(experiment,
                    resultType, priorityElement, parent,
                    hierarchyType, statsModifier)
        then:
        1 * experimentService.createNewExperimentMeasure(_,_,_,_,_,_,_)  >> {
            new ExperimentMeasure(experiment: experiment,modifiedBy: modifiedBy,
                    priorityElement: priorityElement,resultType: resultType,
                    parent: parent,parentChildRelationship: hierarchyType,
                    statsModifier: statsModifier)
        }
        assert experimentMeasure.experiment == experiment
        assert experimentMeasure.modifiedBy == modifiedBy
        assert experimentMeasure.priorityElement == priorityElement
        assert experimentMeasure.resultType == resultType
        assert experimentMeasure.parent == parent
        assert experimentMeasure.parentChildRelationship == hierarchyType
        assert experimentMeasure.statsModifier == statsModifier


    }

    void "test associateAssayContext"() {
        given:
        final Element concentrationResultType = Element.build()
        final Element responseResultType = Element.build(expectedValueType: ExpectedValueType.FREE_TEXT)
        final Experiment experiment = Experiment.build()
        final Element statsModifier = Element.build()
        final ExperimentMeasure parentExperimentMeasure = ExperimentMeasure.build()
        final HierarchyType hierarchyType = HierarchyType.CALCULATED_FROM


        Map m = [
                ontologyDataAccessService: this.ontologyDataAccessService,
                experimentId: experiment.id,
                concentrationResultTypeId: concentrationResultType.id,
                responseResultTypeId: responseResultType.id,
                statsModifierId: statsModifier.id,
                parentExperimentMeasureId: parentExperimentMeasure.id,
                parentChildRelationship: hierarchyType.id
        ]
        DoseResultTypeCommand doseResultTypeCommand = new DoseResultTypeCommand(m)
        AssayContext assayContext = AssayContext.build(assay: experiment.assay)
        List<Descriptor> descriptors = [AssayDescriptor.build(element: concentrationResultType), AssayDescriptor.build(element: responseResultType)]
        AssayContextItem.build(attributeElement: responseResultType, assayContext: assayContext)
        ExperimentMeasure experimentMeasure = new ExperimentMeasure(resultType: responseResultType)
        when:
        doseResultTypeCommand.associateAssayContext(experimentMeasure)
        then:
        ontologyDataAccessService.getDescriptorsForAttributes(_) >> {
            descriptors
        }
        assert experimentMeasure.assayContextExperimentMeasures.size() == 1
    }

    void "test createAssayContextForResultType"() {
        given:
        final Element responseResultType = Element.build(expectedValueType: ExpectedValueType.FREE_TEXT)
        final Assay assay = Experiment.build().assay
        DoseResultTypeCommand doseResultTypeCommand = new DoseResultTypeCommand()
        when:
        AssayContext assayContext = doseResultTypeCommand.createAssayContextForResultType(assay, responseResultType.label)
        then:
        assert assayContext
        assert assay.assayContexts.size() == 1
        assert assayContext.assayContextItems.size() == 1
    }

    void "test createCurveFitExperimentMeasure"() {
        given:
        String curveFitElementLabel = "Hill slopeY"
        Element curveFitResultType = Element.build(label: curveFitElementLabel)
        Experiment experiment = Experiment.build()
        String modifiedBy = "Me"
        ExperimentMeasure parentExperimentMeasure = ExperimentMeasure.build()
        Element statsModifier = null
        DoseResultTypeCommand doseResultTypeCommand = new DoseResultTypeCommand(experimentService: this.experimentService)
        when:
        ExperimentMeasure experimentMeasure =
            doseResultTypeCommand.createCurveFitExperimentMeasure(experiment, parentExperimentMeasure, curveFitElementLabel, statsModifier)
        then:
        1 * experimentService.createNewExperimentMeasure(_,_,_,_,_,_,_)  >> {
            new ExperimentMeasure(experiment: experiment,modifiedBy: modifiedBy,
                    priorityElement: false,resultType: curveFitResultType,
                    parent: parentExperimentMeasure,parentChildRelationship: HierarchyType.SUPPORTED_BY,
                    statsModifier: statsModifier)
        }
        assert experimentMeasure.experiment == experiment
        assert experimentMeasure.modifiedBy == modifiedBy
        assert !experimentMeasure.priorityElement
        assert experimentMeasure.resultType == curveFitResultType
        assert experimentMeasure.parent == parentExperimentMeasure
        assert experimentMeasure.parentChildRelationship == HierarchyType.SUPPORTED_BY
        assert experimentMeasure.statsModifier == statsModifier
    }
}
