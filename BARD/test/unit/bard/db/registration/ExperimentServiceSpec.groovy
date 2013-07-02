package bard.db.registration

import bard.db.enums.HierarchyType
import bard.db.experiment.Experiment
import bard.db.experiment.ExperimentMeasure
import bard.db.experiment.ExperimentService
import grails.buildtestdata.mixin.Build
import grails.converters.JSON
import grails.plugins.springsecurity.SpringSecurityService
import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import grails.test.mixin.TestMixin
import grails.test.mixin.services.ServiceUnitTestMixin
import org.junit.Before
import registration.AssayService
import spock.lang.Specification

/**
 * See the API for {@link grails.test.mixin.support.GrailsUnitTestMixin} for usage instructions
 */
@Build([Assay, Measure, Experiment, AssayContextMeasure, AssayContext, ExperimentMeasure, AssayContextItem, AssayDocument])
@Mock([Assay, Measure, Experiment,ExperimentMeasure,AssayContext,AssayContextMeasure, AssayContextItem, AssayDocument])
@TestMixin(ServiceUnitTestMixin)
@TestFor(ExperimentService)
class ExperimentServiceSpec  extends Specification {
    @Before
    void setup() {

    }
    void 'test create experiment'() {
        given:
        mockDomain(Experiment)
        Measure parent = Measure.build()
        Measure child = Measure.build(parentMeasure: parent)
        Assay assay = Assay.build(measures: [parent, child] as Set)

        when:
        Experiment experiment = service.createNewExperiment(assay.id, "name", "desc")

        then:
        experiment.experimentName == "name"
        experiment.description == "desc"
        experiment.assay == assay
        experiment.experimentMeasures.size() == 2

        when:
        ExperimentMeasure parentExpMeasure = experiment.experimentMeasures.find { it.parent == null}

        then:
        parentExpMeasure != null

        when:
        ExperimentMeasure childExpMeasure = experiment.experimentMeasures.find { it.parent == parentExpMeasure }

        then:
        childExpMeasure != null
        childExpMeasure.parentChildRelationship == HierarchyType.SUPPORTED_BY
    }

    void 'update measures'() {
        given:
        mockDomain(ExperimentMeasure);

        when:
        Measure measure = Measure.build()
        Experiment experiment = Experiment.build()
        ExperimentMeasure parent = ExperimentMeasure.build(experiment: experiment)
        ExperimentMeasure child = ExperimentMeasure.build(parent: parent, experiment: experiment)
        experiment.setExperimentMeasures([parent, child] as Set)

        then:
        ExperimentMeasure.findAll().size() == 2

        when: "we drop a child"
        service.updateMeasures(experiment.id, JSON.parse("[{\"id\": ${parent.id}, \"parentId\": null, \"measureId\": ${parent.measure.id}}]"))

        then:
        experiment.experimentMeasures.size() == 1
        ExperimentMeasure.findAll().size() == 1

        when: "we add a child"
        service.updateMeasures(experiment.id, JSON.parse("[{\"id\": ${parent.id}, \"parentId\": null, \"measureId\": ${parent.measure.id}}, {\"id\": \"new-1\", \"parentId\": ${parent.id}, \"measureId\": ${measure.id}}]"))

        then:
        experiment.experimentMeasures.size() == 2
        (experiment.experimentMeasures.findAll { it.parent == null}).size() == 1

        when: "we drop child and create element at top level"
        service.updateMeasures(experiment.id, JSON.parse("[{\"id\": ${parent.id}, \"parentId\": null, \"measureId\": ${parent.measure.id}}, {\"id\": \"new-2\", \"parentId\": null, \"measureId\": ${measure.id}}]"))

        then:
        experiment.experimentMeasures.size() == 2
        (experiment.experimentMeasures.findAll { it.parent == null}).size() == 2

        when: "we drop everything and recreate parent child"
        service.updateMeasures(experiment.id, JSON.parse("[{\"id\": \"new-1\", \"parentId\": null, \"measureId\": ${parent.measure.id}}, {\"id\": \"new-2\", \"parentId\": \"new-1\", \"measureId\": ${measure.id}}]"))

        then:
        experiment.experimentMeasures.size() == 2
        (experiment.experimentMeasures.findAll { it.parent == null}).size() == 1
    }

    void 'test splitting experiment from assay'() {
        setup:
        service.assayService = new AssayService()
        Assay assay = Assay.build()
        AssayContext context = AssayContext.build(assay: assay, contextName: "alpha")
        Measure measure = Measure.build(assay: assay)
        AssayContextMeasure.build(assayContext: context, measure: measure)
        Experiment experiment = Experiment.build(assay: assay)
        ExperimentMeasure experimentMeasure = ExperimentMeasure.build(experiment: experiment, measure: measure)

        when:
        service.splitExperimentsFromAssay([experiment])

        then:
        experiment.assay != assay // the assay is different
        experimentMeasure.measure != measure // and measure is different

        // but the new measure is identical to the original measure
        experimentMeasure.measure.resultType == measure.resultType
        experimentMeasure.measure.statsModifier == measure.statsModifier

        Assay newAssay = experiment.assay
        newAssay.assayContexts.size() == 1
        newAssay.assayContexts.first().contextName == "alpha"

        // verify the assayContextMeasures are hooked up right
        experimentMeasure.measure.assayContextMeasures.size() == 1
        experimentMeasure.measure.assayContextMeasures.first().assayContext == newAssay.assayContexts.first()
        experimentMeasure.measure.assayContextMeasures.first().measure == experimentMeasure.measure
    }
}
