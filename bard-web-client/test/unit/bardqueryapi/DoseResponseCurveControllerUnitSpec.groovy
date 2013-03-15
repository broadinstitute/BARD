package bardqueryapi

import curverendering.Curve
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
    BardUtilitiesService bardUtilitiesService

    void setup() {
        controller.metaClass.mixin(InetAddressUtil)
        doseCurveRenderingService = Mock(DoseCurveRenderingService)
        bardUtilitiesService = Mock(BardUtilitiesService)
        controller.bardUtilitiesService = bardUtilitiesService
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
        _ * doseCurveRenderingService.createDoseCurve(_) >> {array}
        assert flash.message == 'Points required in order to draw a Dose Response Curve'
        assert response.status == 500
    }

    void "test doseResponse action null bytes"() {
        given:
        byte[] array = null
        mockCommandObject(DrcCurveCommand)
        Map paramMap = [activities: [new Double(1), new Double(2)],
                concentrations: [new Double(1), new Double(2)], s0: 0.2, sinf: 2.2, slope: 2.1, hillSlope: 2.0, xAxisLabel: 'X', yAxisLabel: 'Y']

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
                concentrations: [new Double(1), new Double(2)], s0: 0.2, sinf: 2.2, slope: 2.1, hillSlope: 2.0, xAxisLabel: 'X', yAxisLabel: 'Y']

        controller.metaClass.getParams {-> paramMap}
        DrcCurveCommand drcCurveCommand = new DrcCurveCommand(paramMap)

        when:
        controller.doseResponseCurve(drcCurveCommand)

        then:
        doseCurveRenderingService.createDoseCurve(_) >> {array}
        assert response.status == 200
    }

    void "test doseResponseCurves with exception"() {
        given:
        List<Curve> curves =
            [
                    new Curve(
                            activities: [new Double(1), new Double(2)],
                            concentrations: [new Double(1), new Double(2)],
                            s0: 0.2, sinf: 2.2, slope: 2.1, hillSlope: 2.0
                    )
            ]
        mockCommandObject(DrcCurveCommand)
        Map paramMap = [curves: curves, xAxisLabel: 'X', yAxisLabel: 'Y']

        controller.metaClass.getParams {-> paramMap}
        DrcCurveCommand drcCurveCommand = new DrcCurveCommand(paramMap)

        when:
        controller.doseResponseCurves(drcCurveCommand)
        then:
        doseCurveRenderingService.createDoseCurves(_) >> {throw new Exception()}
        assert response.status == 500
    }

    void "test doseResponseCurves action"() {

        given:
        List<Curve> curves =
            [
                    new Curve(
                            activities: [new Double(1), new Double(2)],
                            concentrations: [new Double(1), new Double(2)],
                            s0: 0.2, sinf: 2.2, slope: 2.1, hillSlope: 2.0
                    )
            ]
        def byteArray = [0, 0, 0, 0, 0] as byte[]

        mockCommandObject(DrcCurveCommand)
        Map paramMap = [curves: curves, xAxisLabel: 'X', yAxisLabel: 'Y']

        controller.metaClass.getParams {-> paramMap}
        DrcCurveCommand drcCurveCommand = new DrcCurveCommand(paramMap)

        when:
        controller.doseResponseCurves(drcCurveCommand)
        then:
        doseCurveRenderingService.createDoseCurves(_) >> {byteArray}
        assert response.status == 200

    }


    void "test doseResponseCurves action with two curves"() {

        given:
        List<Curve> curves =
            [
                    new Curve(
                            activities: [new Double(1), new Double(2)],
                            concentrations: [new Double(1), new Double(2)],
                            s0: 0.2, sinf: 2.2, slope: 2.1, hillSlope: 2.0
                    ),
                    new Curve(
                            activities: [new Double(3), new Double(4)],
                            concentrations: [new Double(3), new Double(4)],
                            s0: 0.2, sinf: 3.2, slope: 3.1, hillSlope: 2.0
                    )
            ]
        def byteArray = [0, 0, 0, 0, 0] as byte[]

        mockCommandObject(DrcCurveCommand)
        Map paramMap = [curves: curves, xAxisLabel: 'X', yAxisLabel: 'Y']

        controller.metaClass.getParams {-> paramMap}
        DrcCurveCommand drcCurveCommand = new DrcCurveCommand(paramMap)

        when:
        controller.doseResponseCurves(drcCurveCommand)
        then:
        doseCurveRenderingService.createDoseCurves(_) >> {byteArray}
        assert response.status == 200

    }

    void "test doseResponseCurves action null Command Object"() {
        given:
        byte[] array = null

        mockCommandObject(DrcCurveCommand)
        DrcCurveCommand drcCurveCommand = null
        when:
        controller.doseResponseCurves(drcCurveCommand)
        then:
        _ * doseCurveRenderingService.createDoseCurve(_) >> {array}
        assert flash.message == 'Points required in order to draw a Dose Response Curve'
        assert response.status == 500
    }

    void "test doseResponseCurves action null bytes"() {

        given:
        List<Curve> curves =
            [
                    new Curve(
                            activities: [new Double(1), new Double(2)],
                            concentrations: [new Double(1), new Double(2)],
                            s0: 0.2, sinf: 2.2, slope: 2.1, hillSlope: 2.0
                    )
            ]
        def byteArray = null

        mockCommandObject(DrcCurveCommand)
        Map paramMap = [curves: curves, xAxisLabel: 'X', yAxisLabel: 'Y']

        controller.metaClass.getParams {-> paramMap}
        DrcCurveCommand drcCurveCommand = new DrcCurveCommand(paramMap)

        when:
        controller.doseResponseCurve(drcCurveCommand)

        then:
        doseCurveRenderingService.createDoseCurve(_) >> {byteArray}
        assert response.status == 500
    }

}
