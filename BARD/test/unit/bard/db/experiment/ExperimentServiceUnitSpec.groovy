package bard.db.experiment

import bard.db.enums.ExperimentStatus
import bard.db.registration.*
import grails.buildtestdata.mixin.Build
import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import grails.test.mixin.TestMixin
import grails.test.mixin.services.ServiceUnitTestMixin
import spock.lang.Specification

/**
 * Created with IntelliJ IDEA.
 * User: pmontgom
 * Date: 5/2/13
 * Time: 2:07 PM
 * To change this template use File | Settings | File Templates.
 */
@Build([Assay, Measure, Experiment, AssayContextMeasure, AssayContext, ExperimentMeasure, AssayContextItem, AssayDocument])
@Mock([Assay, Measure, Experiment, ExperimentMeasure, AssayContext, AssayContextMeasure, AssayContextItem, AssayDocument])
@TestMixin(ServiceUnitTestMixin)
@TestFor(ExperimentService)
public class ExperimentServiceUnitSpec extends Specification {

    void "test update Experiment Status"(){
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
