package bard.db.experiment


import bard.db.experiment.Experiment
import bard.db.registration.Panel
import org.junit.*
import grails.test.mixin.*
import spock.lang.Shared
import spock.lang.Specification
import grails.buildtestdata.mixin.Build
import spock.lang.Unroll
import grails.test.mixin.TestMixin
import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import grails.buildtestdata.mixin.Build
import grails.test.mixin.domain.DomainClassUnitTestMixin
import org.junit.Before

import javax.servlet.http.HttpServletResponse


@Build([Panel, Experiment, PanelExperiment])
@Mock(PanelExperiment)
@TestFor(PanelExperimentController)
@TestMixin(DomainClassUnitTestMixin)
@Unroll
class ExternalReferenceControllerUnitSpec extends Specification {

    @Shared
    Experiment experiment
    @Shared
    Panel panel

    @Before
    void setup() {
        this.experiment = Experiment.build()
        this.panel = Panel.build()
    }

    void "test create"() {
        given:

        when:
        def response = controller.create()

        then:
        assert response.panelExperimentCommand
    }
}
