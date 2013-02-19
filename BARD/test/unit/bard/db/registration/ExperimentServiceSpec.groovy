package bard.db.registration

import bard.db.experiment.Experiment
import bard.db.experiment.ExperimentMeasure
import bard.db.experiment.Substance
import grails.buildtestdata.mixin.Build
import grails.converters.JSON
import grails.test.mixin.domain.DomainClassUnitTestMixin
import grails.test.mixin.services.ServiceUnitTestMixin

import static org.junit.Assert.*

import grails.test.mixin.*
import grails.test.mixin.support.*
import org.junit.*

/**
 * See the API for {@link grails.test.mixin.support.GrailsUnitTestMixin} for usage instructions
 */
@Build([Assay, Measure, Experiment,ExperimentMeasure])
@TestMixin([ServiceUnitTestMixin,DomainClassUnitTestMixin])
class ExperimentServiceSpec  extends spock.lang.Specification {
    void 'test create experiment'() {
        setup:
        mockDomain(Experiment)
        ExperimentService experimentService = new ExperimentService()
        Measure parent = Measure.build()
        Measure child = Measure.build(parentMeasure: parent)
        Assay assay = Assay.build(measures: [parent, child] as Set)

        when:
        Experiment experiment = experimentService.createNewExperiment(assay, "name", "desc")

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
        childExpMeasure.parentChildRelationship == "Derived from"
    }

    void 'update measures'() {
        setup:
        mockDomain(ExperimentMeasure);
        ExperimentService experimentService = new ExperimentService()

        when:
        Measure measure = Measure.build()
        Experiment experiment = Experiment.build()
        ExperimentMeasure parent = ExperimentMeasure.build(experiment: experiment)
        ExperimentMeasure child = ExperimentMeasure.build(parent: parent, experiment: experiment)
        experiment.setExperimentMeasures([parent, child] as Set)

        then:
        ExperimentMeasure.findAll().size() == 2

        when: "we drop a child"
        experimentService.updateMeasures(experiment, JSON.parse("[{\"id\": ${parent.id}, \"parentId\": null, \"measureId\": ${parent.measure.id}}]"))

        then:
        experiment.experimentMeasures.size() == 1
        ExperimentMeasure.findAll().size() == 1

        when: "we add a child"
        experimentService.updateMeasures(experiment, JSON.parse("[{\"id\": ${parent.id}, \"parentId\": null, \"measureId\": ${parent.measure.id}}, {\"id\": \"new-1\", \"parentId\": ${parent.id}, \"measureId\": ${measure.id}}]"))

        then:
        experiment.experimentMeasures.size() == 2
        (experiment.experimentMeasures.findAll { it.parent == null}).size() == 1

        when: "we drop child and create element at top level"
        experimentService.updateMeasures(experiment, JSON.parse("[{\"id\": ${parent.id}, \"parentId\": null, \"measureId\": ${parent.measure.id}}, {\"id\": \"new-2\", \"parentId\": null, \"measureId\": ${measure.id}}]"))

        then:
        experiment.experimentMeasures.size() == 2
        (experiment.experimentMeasures.findAll { it.parent == null}).size() == 2

        when: "we drop everything and recreate parent child"
        experimentService.updateMeasures(experiment, JSON.parse("[{\"id\": \"new-1\", \"parentId\": null, \"measureId\": ${parent.measure.id}}, {\"id\": \"new-2\", \"parentId\": \"new-1\", \"measureId\": ${measure.id}}]"))

        then:
        experiment.experimentMeasures.size() == 2
        (experiment.experimentMeasures.findAll { it.parent == null}).size() == 1
    }
}
