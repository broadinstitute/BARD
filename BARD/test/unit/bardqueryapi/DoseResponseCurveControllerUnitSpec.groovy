/* Copyright (c) 2014, The Broad Institute
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
    * Redistributions of source code must retain the above copyright
      notice, this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright
      notice, this list of conditions and the following disclaimer in the
      documentation and/or other materials provided with the distribution.
    * Neither the name of The Broad Institute nor the
      names of its contributors may be used to endorse or promote products
      derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL The Broad Institute BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

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
