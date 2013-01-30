package curverendering

import bard.core.rest.spring.CompoundRestService
import bard.core.rest.spring.ExperimentRestService
import bardqueryapi.DrcCurveCommand
import bardqueryapi.QueryService
import grails.plugin.spock.IntegrationSpec
import org.jfree.chart.ChartUtilities
import org.jfree.chart.JFreeChart
import spock.lang.Unroll
import bard.core.rest.spring.experiment.*

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
        final Map<String, List<Double>> map = ConcentrationResponseSeries.toDoseResponsePoints(concentrationResponseSeries.concentrationResponsePoints)
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
        label                                    | cids                        | experimentId
        "An existing experiment with activities" | [72574L, 5405444L, 650462L] | 10282

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
        final Map<String, List<Double>> map = ConcentrationResponseSeries.toDoseResponsePoints(concentrationResponseSeries.concentrationResponsePoints)
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
        label                                    | cids                        | experimentId
        "An existing experiment with activities" | [72574L, 5405444L, 650462L] | 10282

    }

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
        String xaxis = "Concetration " + priorityElement.testConcentrationUnit
        for (ActivityConcentration primaryElement : primaryElements) {
            final ConcentrationResponseSeries concentrationResponseSeries = primaryElement.getConcentrationResponseSeries()
            yaxis = concentrationResponseSeries.getYAxisLabel()

            final CurveFitParameters curveFitParameters = concentrationResponseSeries.curveFitParameters

            final Map<String, List<Double>> map = ConcentrationResponseSeries.toDoseResponsePoints(concentrationResponseSeries.concentrationResponsePoints)
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
        "An existing experiment with activities" | new Long(14117)

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
        final Map<String, List<Double>> map = ConcentrationResponseSeries.toDoseResponsePoints(concentrationResponseSeries.concentrationResponsePoints)
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
        label                                    | cids                        | experimentId
        "An existing experiment with activities" | [72574L, 5405444L, 650462L] | 10282

    }

}
