package bard.db.registration

import bard.db.experiment.Experiment
import bard.db.experiment.ExperimentMeasure
import bard.db.experiment.Substance
import grails.buildtestdata.mixin.Build
import grails.test.mixin.domain.DomainClassUnitTestMixin
import grails.test.mixin.services.ServiceUnitTestMixin

import static org.junit.Assert.*

import grails.test.mixin.*
import grails.test.mixin.support.*
import org.junit.*

/**
 * See the API for {@link grails.test.mixin.support.GrailsUnitTestMixin} for usage instructions
 */
@Build([Assay, Measure])
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
}
