package bard.db.registration

import bard.db.experiment.Experiment
import bard.db.project.ExperimentController
import grails.buildtestdata.mixin.Build
import grails.test.mixin.TestFor
import grails.test.mixin.TestMixin
import grails.test.mixin.domain.DomainClassUnitTestMixin
import spock.lang.Specification

/**
 * Created with IntelliJ IDEA.
 * User: pmontgom
 * Date: 2/10/13
 * Time: 1:04 PM
 * To change this template use File | Settings | File Templates.
 */
@TestFor(ExperimentController)
@TestMixin(DomainClassUnitTestMixin)
@Build([Assay, Experiment])
class ExperimentControllerSpec extends Specification {
    def 'test create'() {
        setup:
        controller.measureTreeService = Mock(MeasureTreeService)
        controller.measureTreeService.createMeasureTree(_,_) >> []

        when:
        Assay assay = Assay.build()
        params.assayId = assay.id
        def m = controller.create()

        then:
        m.assay == assay
        m.experiment.id == null
    }

    def 'test save'() {
        setup:
        Assay assay = Assay.build()
        Experiment experiment = Experiment.build(assay: assay)
        ExperimentService experimentService = Mock(ExperimentService)

        when:
        controller.experimentService = experimentService
        params.assayId = assay.id
        params.experimentName = "name"
        params.description = "desc"
        controller.save()

        then:
        1 * experimentService.createNewExperiment(assay, "name", "desc") >> experiment
        assert response.redirectedUrl == '/experiment/show/' + experiment.id
    }

    def 'test show'() {
        setup:
        controller.measureTreeService = Mock(MeasureTreeService)
        controller.measureTreeService.createMeasureTree(_,_) >> []

        when:
        Experiment exp = Experiment.build()
        params.id = exp.id
        def m = controller.show()

        then:
        m.instance == exp
    }
}