package bardqueryapi

import curverendering.DoseCurveRenderingService
import grails.test.mixin.TestFor
import grails.test.mixin.TestMixin
import grails.test.mixin.support.GrailsUnitTestMixin
import org.jfree.chart.JFreeChart
import spock.lang.Specification
import spock.lang.Unroll

/**
 * See the API for {@link grails.test.mixin.support.GrailsUnitTestMixin} for usage instructions
 */
@TestMixin(GrailsUnitTestMixin)
@TestFor(DoseResponseCurveController)
@Unroll
class DoseResponseCurveControllerUnitSpec extends Specification {

    DoseCurveRenderingService doseCurveRenderingService

    void setup() {
        doseCurveRenderingService = Mock(DoseCurveRenderingService)
        controller.doseCurveRenderingService = this.doseCurveRenderingService
    }

    void tearDown() {
        // Tear down logic here
    }


    void "test doseResponse action"() {
        given:
        mockCommandObject(DrcCurveCommand)
        Map paramMap = [activities: [new Double(1), new Double(2)], concentrations: [new Double(1), new Double(2)], s0: 0.2, sinf: 2.2, ac50: 2.1, hillSlope: 2.0]

        controller.metaClass.getParams {-> paramMap}
        DrcCurveCommand drcCurveCommand = new DrcCurveCommand(paramMap)

        when:
        controller.doseResponseCurve(drcCurveCommand)

        then:
        _ * doseCurveRenderingService.createDoseCurve(_, _, _, _, _, _, _, _, _) >> {Mock(JFreeChart)}
        assert response.status == 500
    }
}
