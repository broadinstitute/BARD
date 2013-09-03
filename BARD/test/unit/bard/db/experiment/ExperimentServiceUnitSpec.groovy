package bard.db.experiment

import bard.db.dictionary.Element
import bard.db.enums.ExperimentStatus
import bard.db.enums.HierarchyType
import bard.db.registration.*
import grails.buildtestdata.mixin.Build
import grails.converters.JSON
import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import grails.test.mixin.TestMixin
import grails.test.mixin.services.ServiceUnitTestMixin
import registration.AssayService
import spock.lang.Specification

/**
 * Created with IntelliJ IDEA.
 * User: pmontgom
 * Date: 5/2/13
 * Time: 2:07 PM
 * To change this template use File | Settings | File Templates.
 */
@Build([Assay, Experiment, AssayContextExperimentMeasure, AssayContext, ExperimentMeasure, AssayContextItem, AssayDocument])
@Mock([Assay, Experiment, ExperimentMeasure, AssayContext, AssayContextExperimentMeasure, AssayContextItem, AssayDocument])
@TestMixin(ServiceUnitTestMixin)
@TestFor(ExperimentService)
public class ExperimentServiceUnitSpec extends Specification {

    void 'test create experiment'() {
        given:
        mockDomain(Experiment)
        Assay assay = Assay.build()

        when:
        Experiment experiment = service.createNewExperiment(assay.id, "name", "desc")

        then:
        experiment.experimentName == "name"
        experiment.description == "desc"
        experiment.assay == assay
        experiment.experimentMeasures.size() == 0
    }
    //TODO: Rework
//    @Ignore
//    void 'update measures'() {
//        given:
//        mockDomain(ExperimentMeasure);
//
//        when:
//        Measure measure = Measure.build()
//        Experiment experiment = Experiment.build()
//        ExperimentMeasure parent = ExperimentMeasure.build(experiment: experiment)
//        ExperimentMeasure child = ExperimentMeasure.build(parent: parent, experiment: experiment)
//        experiment.setExperimentMeasures([parent, child] as Set)
//
//        then:
//        ExperimentMeasure.findAll().size() == 2
//
//        when: "we drop a child"
//        service.updateMeasures(experiment.id, JSON.parse("[{\"id\": ${parent.id}, \"parentId\": null, \"measureId\": ${parent.measure.id}}]"))
//
//        then:
//        experiment.experimentMeasures.size() == 1
//        ExperimentMeasure.findAll().size() == 1
//
//        when: "we add a child"
//        service.updateMeasures(experiment.id, JSON.parse("[{\"id\": ${parent.id}, \"parentId\": null, \"measureId\": ${parent.measure.id}}, {\"id\": \"new-1\", \"parentId\": ${parent.id}, \"measureId\": ${measure.id}}]"))
//
//        then:
//        experiment.experimentMeasures.size() == 2
//        (experiment.experimentMeasures.findAll { it.parent == null}).size() == 1
//
//        when: "we drop child and create element at top level"
//        service.updateMeasures(experiment.id, JSON.parse("[{\"id\": ${parent.id}, \"parentId\": null, \"measureId\": ${parent.measure.id}}, {\"id\": \"new-2\", \"parentId\": null, \"measureId\": ${measure.id}}]"))
//
//        then:
//        experiment.experimentMeasures.size() == 2
//        (experiment.experimentMeasures.findAll { it.parent == null}).size() == 2
//
//        when: "we drop everything and recreate parent child"
//        service.updateMeasures(experiment.id, JSON.parse("[{\"id\": \"new-1\", \"parentId\": null, \"measureId\": ${parent.measure.id}}, {\"id\": \"new-2\", \"parentId\": \"new-1\", \"measureId\": ${measure.id}}]"))
//
//        then:
//        experiment.experimentMeasures.size() == 2
//        (experiment.experimentMeasures.findAll { it.parent == null}).size() == 1
//    }

    void 'test splitting experiment from assay'() {
        given:
        service.assayService = new AssayService()
        Assay assay = Assay.build()
        AssayContext context = AssayContext.build(assay: assay, contextName: "alpha")

        Element resultType = Element.build()
        Element statsModifier = Element.build()

        Experiment experiment = Experiment.build(assay: assay)
        ExperimentMeasure experimentMeasure = ExperimentMeasure.build(experiment: experiment, statsModifier: statsModifier, resultType: resultType)
        AssayContextExperimentMeasure.build(assayContext: context, experimentMeasure: experimentMeasure)
        when:
        service.splitExperimentsFromAssay(assay.id, [experiment])

        then:
        experiment.assay != assay // the assay is different
        //experimentMeasure.measure != measure // and measure is different

        // but the new measure is identical to the original measure
        experimentMeasure.resultType == resultType
        experimentMeasure.statsModifier == statsModifier

        Assay newAssay = experiment.assay
        newAssay.assayContexts.size() == 1
        newAssay.assayContexts.first().contextName == "alpha"

        // verify the assayContextMeasures are hooked up right
        //  experimentMeasure.measure.assayContextMeasures.size() == 1
        //experimentMeasure.measure.assayContextMeasures.first().assayContext == newAssay.assayContexts.first()
        //experimentMeasure.measure.assayContextMeasures.first().measure == experimentMeasure.measure
    }

    void "test update Experiment Status"() {
        given:
        final Experiment experiment = Experiment.build(experimentName: 'experimentName20', experimentStatus: ExperimentStatus.DRAFT)
        final ExperimentStatus newExperimentStatus = ExperimentStatus.APPROVED
        when:
        final Experiment updatedExperiment = service.updateExperimentStatus(experiment.id, newExperimentStatus)
        then:
        assert newExperimentStatus == updatedExperiment.experimentStatus
    }

    void "test update Run From Date"() {
        given:
        final Experiment experiment = Experiment.build(experimentName: 'experimentName20')
        final Date runFromDate = new Date()
        when:
        final Experiment updatedExperiment = service.updateRunFromDate(experiment.id, runFromDate)
        then:
        assert runFromDate == updatedExperiment.runDateFrom
    }

    void "test update Run To Date"() {
        given:
        final Experiment experiment = Experiment.build(experimentName: 'experimentName20')
        final Date runToDate = new Date()
        when:
        final Experiment updatedExperiment = service.updateRunToDate(experiment.id, runToDate)
        then:
        assert runToDate == updatedExperiment.runDateTo
    }

    void "test update hold until Date"() {
        given:
        final Experiment experiment = Experiment.build(experimentName: 'experimentName20')
        final Date holdUntilDate = new Date()
        when:
        final Experiment updatedExperiment = service.updateHoldUntilDate(experiment.id, holdUntilDate)
        then:
        assert holdUntilDate == updatedExperiment.holdUntilDate
    }

    void "test update description"() {
        given:
        final Experiment experiment = Experiment.build(experimentName: 'experimentName20', description: "description1")
        final String newDescription = "Description333"
        when:
        final Experiment updatedExperiment = service.updateExperimentDescription(experiment.id, newDescription)
        then:
        assert newDescription == updatedExperiment.description
    }

    void "test update experiment name"() {
        given:
        final Experiment experiment = Experiment.build(experimentName: 'experimentName20', description: "description1111")
        final String newExperimentName = "ExperimentName333"
        when:
        final Experiment updatedExperiment = service.updateExperimentName(experiment.id, newExperimentName)
        then:
        assert newExperimentName == updatedExperiment.experimentName
    }
}
