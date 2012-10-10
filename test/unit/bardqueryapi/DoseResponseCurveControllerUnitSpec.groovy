package bardqueryapi

import curverendering.DoseCurveRenderingService
import grails.test.mixin.TestFor
import grails.test.mixin.TestMixin
import grails.test.mixin.support.GrailsUnitTestMixin
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

    void "test doseResponse action null Command Object"() {
        given:
        byte[] array = null
        mockCommandObject(DrcCurveCommand)
        DrcCurveCommand drcCurveCommand = null
        when:
        controller.doseResponseCurve(drcCurveCommand)

        then:
        0 * doseCurveRenderingService.createDoseCurve(_) >> {array}
        assert flash.message == 'Points required in order to draw a Dose Response Curve'
        assert response.status == 500
    }

    void "test doseResponse action null bytes"() {
        given:
        byte[] array = null
        mockCommandObject(DrcCurveCommand)
        Map paramMap = [activities: [new Double(1), new Double(2)],
                concentrations: [new Double(1), new Double(2)], s0: 0.2, sinf: 2.2, ac50: 2.1, hillSlope: 2.0, xAxisLabel:'X',  yAxisLabel:'Y']

        controller.metaClass.getParams {-> paramMap}
        DrcCurveCommand drcCurveCommand = new DrcCurveCommand(paramMap)

        when:
        controller.doseResponseCurve(drcCurveCommand)

        then:
        doseCurveRenderingService.createDoseCurve(_) >> {array}
        assert response.status == 500
    }

    void "test doseResponse action"() {
        given:
        def array = [0, 0, 0, 0, 0] as byte[]
        mockCommandObject(DrcCurveCommand)
        Map paramMap = [activities: [new Double(1), new Double(2)],
                concentrations: [new Double(1), new Double(2)], s0: 0.2, sinf: 2.2, ac50: 2.1, hillSlope: 2.0, xAxisLabel:'X',  yAxisLabel:'Y']

        controller.metaClass.getParams {-> paramMap}
        DrcCurveCommand drcCurveCommand = new DrcCurveCommand(paramMap)

        when:
        controller.doseResponseCurve(drcCurveCommand)

        then:
        doseCurveRenderingService.createDoseCurve(_) >> {array}
        assert response.status == 200
    }
}
