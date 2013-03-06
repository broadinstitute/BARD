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
        controller.create()

        then:
        response.status == 200
    }

    def 'test save'() {
        setup:
        Assay assay = Assay.build()
        ExperimentService experimentService = Mock(ExperimentService)
        controller.experimentService = experimentService

        when:
        params.assayId = assay.id
        params.experimentName = "name"
        params.description = "desc"
        params.experimentTree = "[]"
        controller.save()

        then:
        1*experimentService.updateMeasures(_,_)
        Experiment.getAll().size() == 1
        def experiment = Experiment.getAll().first()
        assert response.redirectedUrl == "/experiment/show/${experiment.id}"
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
