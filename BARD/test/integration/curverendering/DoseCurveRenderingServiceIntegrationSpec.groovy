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

package curverendering

import bard.core.rest.spring.CompoundRestService
import bard.core.rest.spring.ExperimentRestService
import bardqueryapi.DrcCurveCommand
import bardqueryapi.QueryService
import grails.plugin.spock.IntegrationSpec
import org.jfree.chart.ChartUtilities
import org.jfree.chart.JFreeChart
import spock.lang.IgnoreRest
import spock.lang.Unroll
import bard.core.rest.spring.experiment.*
import spock.lang.Ignore

@Unroll
class DoseCurveRenderingServiceIntegrationSpec extends IntegrationSpec {

    QueryService queryService
    CompoundRestService compoundRestService
    ExperimentRestService experimentRestService
    DoseCurveRenderingService doseCurveRenderingService

    void "test doseResponseCurves"() {
        given:
        List<Curve> curves =
            [
                    new Curve(
                            activities: [
                                    new Double(1), new Double(2)
                            ],
                            concentrations:
                                    [
                                            new Double(1), new Double(2)
                                    ],
                            s0: 0.2, sinf: 2.2, slope: 2.1, hillSlope: 2.0
                    )
            ]
        DrcCurveCommand drcCurveCommand = new DrcCurveCommand(curves: curves, xAxisLabel: 'X', yAxisLabel: 'Y', width: 20, height: 20)

        when:
        final byte[] doseCurve = doseCurveRenderingService.createDoseCurves(drcCurveCommand)
        then:
        assert doseCurve.length

    }

    void "tests createDoseCurve with CommandObject"() {

        given:
        final Map map = [activities: [new Double(1), new Double(2)],
                concentrations: [new Double(1), new Double(2)],
                s0: 0.2, sinf: 2.2, ac50: 2.1, hillSlope: 2.0, height: 200, width: 200, xAxisLabel: 'X', yAxisLabel: 'Y']
        DrcCurveCommand drcCurveCommand = new DrcCurveCommand()
        drcCurveCommand.slope = map.ac50
        drcCurveCommand.activities = map.activities
        drcCurveCommand.concentrations = map.concentrations
        drcCurveCommand.height = map.height
        drcCurveCommand.hillSlope = map.hillSlope
        drcCurveCommand.s0 = map.s0
        drcCurveCommand.sinf = map.sinf
        drcCurveCommand.width = map.width
        drcCurveCommand.xAxisLabel = map.xAxisLabel
        drcCurveCommand.yAxisLabel = map.yAxisLabel
        when:
        final byte[] doseCurve = this.doseCurveRenderingService.createDoseCurve(drcCurveCommand)
        then:
        assert doseCurve.length

    }
    @IgnoreRest
    void "tests createDoseCurvex #label"() {
        given: "That we have created an ETag from a list of CIDs"
        final Map map = [
                activities: [
                        new Double(-0.02800000086426735),
                        new Double(-0.02800000086426735),
                        new Double(-0.02500000037252903),
                        new Double(-0.017999999225139618),
                        new Double(-0.01899999938905239),
                        new Double(-0.017999999225139618),
                        new Double(-0.02199999988079071),
                        new Double(-0.02199999988079071),
                        new Double(-0.017999999225139618),
                        new Double(-0.014999999664723873),
                        new Double(-0.017000000923871994),
                        new Double(-0.017999999225139618),
                        new Double(-0.014999999664723873),
                        new Double(-0.017000000923871994),
                        new Double(-0.01600000075995922),
                        new Double(-0.017000000923871994),
                        new Double(-0.017999999225139618),
                        new Double(-0.020999999716877937),
                        new Double(-0.024000000208616257),
                        new Double(-0.023000000044703484)
                ],
                concentrations: [
                        new Double(0.0),
                        new Double(0.0),
                        new Double(9.999999974752427E-7),
                        new Double(1.9999999949504854E-6),
                        new Double(7.000000096013537E-6),
                        new Double(2.099999983329326E-5),
                        new Double(5.999999848427251E-5),
                        new Double(1.9500000053085387E-4),
                        new Double(5.600000149570405E-4),
                        new Double(0.0015999999595806003),
                        new Double(0.004999999888241291),
                        new Double(0.014999999664723873),
                        new Double(0.04600000008940697),
                        new Double(0.13500000536441803),
                        new Double(0.41999998688697815),
                        new Double(1.2000000476837158),
                        new Double(3.799999952316284),
                        new Double(11.0),
                        new Double(35.0),
                        new Double(100)
                ],
                s0: -0.02, sinf: -0.02, ac50: null, hillSlope: null, height: 200, width: 200, xAxisLabel: 'X', yAxisLabel: 'Y']
        when: "We call the createDoseCurve method with the spreadSheetActivity.hillCurveValue value and the other parameters"

        JFreeChart jFreeChart =
            this.doseCurveRenderingService.
                    createDoseCurve(map.concentrations,
                            map.activities,
                            null,
                            null,
                            map.s0,
                            map.sinf,
                            'X', 'Y', null, null, null, null)

        then: "We expect to get back a JFreeChart back"
        assert jFreeChart
        final File file = new File("testChar1.jpg")
        ChartUtilities.saveChartAsJPEG(file, jFreeChart, 200, 200);
        assert file.exists()


//        where:
//        label                                    | cids        | experimentId
//        "An existing experiment with activities" | [46897918L] | 551

    }

    void "tests createDoseCurve #label"() {
        given: "That we have created an ETag from a list of CIDs"
        final String compoundETag = compoundRestService.newETag("Compound ETags For Activities", cids);

        and: "We call the activities method on the restExperimentService with the experiment and the ETag"
        final ExperimentData activityValueSearchResult = this.experimentRestService.activities(experimentId, compoundETag);
        final List<Activity> searchResults = activityValueSearchResult.activities

        and: "We extract the first experimen tValue in the resulting collection"
        final Activity activity = (Activity) searchResults.get(0)

        and: "We call the extractActivitiesFromExperiment method with the experiment Value to get the SpreadSheetActivity"
        ResultData resultData = activity.resultData

        when: "We call the createDoseCurve method with the spreadSheetActivity.hillCurveValue value and the other parameters"
        final PriorityElement priorityElement = resultData.priorityElements.get(0)
        final ConcentrationResponseSeries concentrationResponseSeries = priorityElement.getConcentrationResponseSeries()
        final ActivityConcentrationMap map = ConcentrationResponseSeries.toDoseResponsePoints(concentrationResponseSeries.concentrationResponsePoints)
        final CurveFitParameters curveFitParameters = concentrationResponseSeries.curveFitParameters
        JFreeChart jFreeChart =
            this.doseCurveRenderingService.
                    createDoseCurve(map.concentrations,
                            map.activities,
                            priorityElement.getSlope(),
                            curveFitParameters.hillCoef,
                            curveFitParameters.s0,
                            curveFitParameters.SInf,
                            'X', 'Y', null, null, null, null)

        then: "We expect to get back a JFreeChart back"
        assert jFreeChart
        final File file = new File("testChar.jpg")
        ChartUtilities.saveChartAsJPEG(file, jFreeChart, 500, 500);
        assert file.exists()


        where:
        label                                    | cids        | experimentId
        "An existing experiment with activities" | [46897918L] | 551

    }

    void "tests createDoseCurve with points #label"() {
        given: "That we have created an ETag from a list of CIDs"
        final String compoundETag = compoundRestService.newETag("Compound ETags For Activities", cids);


        and: "We call the activities method on the restExperimentService with the experiment and the ETag"
        final ExperimentData experimentData = this.experimentRestService.activities(experimentId, compoundETag);
        final List<Activity> searchResults = experimentData.activities

        and: "We extract the first experimen tValue in the resulting collection"
        Activity activity = searchResults.get(0)


        and: "We call the extractActivitiesFromExperiment method with the experiment Value to get the SpreadSheetActivity"
        ResultData resultData = activity.resultData

        when: "We call the createDoseCurve method with the spreadSheetActivity.hillCurveValue value and the other parameters"
        final PriorityElement priorityElement = resultData.priorityElements.get(0)
        final ConcentrationResponseSeries concentrationResponseSeries = priorityElement.getConcentrationResponseSeries()
        ActivityConcentrationMap map = ConcentrationResponseSeries.toDoseResponsePoints(concentrationResponseSeries.concentrationResponsePoints)
        final CurveFitParameters curveFitParameters = concentrationResponseSeries.curveFitParameters
        JFreeChart jFreeChart =
            this.doseCurveRenderingService.
                    createDoseCurve(map.concentrations,
                            map.activities,
                            priorityElement.getSlope(),
                            curveFitParameters.hillCoef,
                            curveFitParameters.s0,
                            curveFitParameters.SInf,
                            'X', 'Y', null, null, null, null)

        then: "We expect to get back a JFreeChart back"
        assert jFreeChart
        final File file = new File("testChar.jpg")
        ChartUtilities.saveChartAsJPEG(file, jFreeChart, 500, 500);
        assert file.exists()


        where:
        label                                    | cids        | experimentId
        "An existing experiment with activities" | [46897918L] | 551

    }

    @Ignore
    void "tests plot multiple curves on same graph #label"() {
        given: "That we have created an ETag from a list of CIDs"
        final ExperimentData experimentData = this.experimentRestService.activities(experimentId);
        final List<Activity> searchResults = experimentData.activities

        and: "We extract the first experimen tValue in the resulting collection"
        Activity activity = searchResults.get(0)


        and: "We call the extractActivitiesFromExperiment method with the experiment Value to get the SpreadSheetActivity"
        ResultData resultData = activity.resultData

        when: "We call the createDoseCurve method with the spreadSheetActivity.hillCurveValue value and the other parameters"
        final PriorityElement priorityElement = resultData.priorityElements.get(0)
        final List<ActivityConcentration> primaryElements = priorityElement.getPrimaryElements()
        List<Curve> curves = []
        String yaxis = ""
        String xaxis = "Concentration " + priorityElement.testConcentrationUnit
        for (ActivityConcentration primaryElement : primaryElements) {
            final ConcentrationResponseSeries concentrationResponseSeries = primaryElement.getConcentrationResponseSeries()
            yaxis = concentrationResponseSeries.getYAxisLabel()

            final CurveFitParameters curveFitParameters = concentrationResponseSeries.curveFitParameters

            final ActivityConcentrationMap map = ConcentrationResponseSeries.toDoseResponsePoints(concentrationResponseSeries.concentrationResponsePoints)
            Curve curve = new Curve(
                    concentrations: map.concentrations,
                    activities: map.activities,
                    hillSlope: curveFitParameters.hillCoef,
                    s0: curveFitParameters.s0,
                    sinf: curveFitParameters.SInf,
                    slope: primaryElement.getSlope())
            curves.add(curve)
        }
        JFreeChart jFreeChart =
            this.doseCurveRenderingService.
                    createDoseCurves(curves,
                            xaxis, yaxis, null, null, null, null)

        then: "We expect to get back a JFreeChart back"
        assert jFreeChart
        final File file = new File("testChar.jpg")
        ChartUtilities.saveChartAsJPEG(file, jFreeChart, 500, 500);
        assert file.exists()


        where:
        label                                    | experimentId
        "An existing experiment with activities" | new Long(22)

    }

    void "tests findDrcData"() {
        given: "That we have created an ETag from a list of CIDs"
        final Object compoundETag = compoundRestService.newETag("Compound ETags For Activities", cids);

        and: "We call the activities method on the restExperimentService with the experiment and the ETag"
        final ExperimentData experimentIterator = this.experimentRestService.activities(experimentId, compoundETag);


        and: "We extract the first experimen tValue in the resulting collection"
        Activity activity = experimentIterator.activities.get(0)


        and: "We call the extractActivitiesFromExperiment method with the experiment Value to get the SpreadSheetActivity"
        ResultData resultData = activity.resultData

        when: "We call the findDrcData method with the spreadSheetActivity.hillCurveValue value"
        final PriorityElement priorityElement = resultData.priorityElements.get(0)
        final ConcentrationResponseSeries concentrationResponseSeries = priorityElement.getConcentrationResponseSeries()
        final ActivityConcentrationMap map = ConcentrationResponseSeries.toDoseResponsePoints(concentrationResponseSeries.concentrationResponsePoints)
        final CurveFitParameters curveFitParameters = concentrationResponseSeries.curveFitParameters

        final Drc drc =
            this.doseCurveRenderingService.findDrcData(
                    map.concentrations,
                    map.activities,
                    priorityElement.getSlope(),
                    curveFitParameters.hillCoef,
                    curveFitParameters.s0,
                    curveFitParameters.SInf, java.awt.Color.BLACK)

        then: "We expect to get back a Drc object with the number of activities matching the number of concentrations"
        assert drc
        assert drc.concentrations
        assert drc.activities
        assert drc.curveParameters
        assert drc.concentrations.size() == drc.activities.size()
        //we need to assert the curve parameters

        where:
        label                                    | cids        | experimentId
        "An existing experiment with activities" | [46897918L] | 551

    }

}
