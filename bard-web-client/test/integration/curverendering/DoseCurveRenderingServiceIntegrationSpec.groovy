package curverendering

import bard.core.HillCurveValue
import bard.core.rest.spring.CompoundRestService
import bard.core.rest.spring.ExperimentRestService
import bard.core.rest.spring.experiment.Activity
import bard.core.rest.spring.experiment.ExperimentData
import bardqueryapi.DrcCurveCommand
import grails.plugin.spock.IntegrationSpec
import molspreadsheet.MolecularSpreadSheetService
import molspreadsheet.SpreadSheetActivity
import org.jfree.chart.ChartUtilities
import org.jfree.chart.JFreeChart
import org.junit.After
import org.junit.Before
import spock.lang.Unroll

@Unroll
class DoseCurveRenderingServiceIntegrationSpec extends IntegrationSpec {

    MolecularSpreadSheetService molecularSpreadSheetService
    CompoundRestService compoundRestService
    ExperimentRestService experimentRestService
    DoseCurveRenderingService doseCurveRenderingService


    @Before
    void setup() {

    }

    @After
    void tearDown() {

    }

    void "tests createDoseCurve with CommandObject"() {

        given:
        final Map map = [activities: [new Double(1), new Double(2)],
                concentrations: [new Double(1), new Double(2)],
                s0: 0.2, sinf: 2.2, ac50: 2.1, hillSlope: 2.0, height: 200, width: 200, xAxisLabel: 'X', yAxisLabel: 'Y']
        DrcCurveCommand drcCurveCommand = new DrcCurveCommand()
        drcCurveCommand.ac50 = map.ac50
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
        final Activity experimentValue = (Activity) searchResults.get(0)

        and: "We call the extractActivitiesFromExperiment method with the experiment Value to get the SpreadSheetActivity"
        SpreadSheetActivity spreadSheetActivity = molecularSpreadSheetService.extractActivitiesFromExperiment(experimentValue)
        when: "We call the createDoseCurve method with the spreadSheetActivity.hillCurveValue value and the other parameters"
        JFreeChart jFreeChart =
            this.doseCurveRenderingService.
                    createDoseCurve(spreadSheetActivity.hillCurveValueList[0].conc as List<Double>,
                            spreadSheetActivity.hillCurveValueList[0].response as List<Double>,
                            spreadSheetActivity.hillCurveValueList[0].slope,
                            spreadSheetActivity.hillCurveValueList[0].coef,
                            spreadSheetActivity.hillCurveValueList[0].s0,
                            spreadSheetActivity.hillCurveValueList[0].sinf,
                            'X', 'Y', null, null, null, null)

        then: "We expect to get back a JFreeChart back"
        assert jFreeChart
        final File file = new File("testChar.jpg")
        ChartUtilities.saveChartAsJPEG(file, jFreeChart, 500, 500);
        assert file.exists()


        where:
        label                                    | cids                                                      | experimentId
        "An existing experiment with activities" | [new Long(2836861), new Long(5882673), new Long(5604367)] | new Long(346)

    }

    void "tests createDoseCurve with points #label"() {
        given: "That we have created an ETag from a list of CIDs"
        final String compoundETag = compoundRestService.newETag("Compound ETags For Activities", cids);


        and: "We call the activities method on the restExperimentService with the experiment and the ETag"
        final ExperimentData experimentData = this.experimentRestService.activities(experimentId, compoundETag);
        final List<Activity> searchResults = experimentData.activities

        and: "We extract the first experimen tValue in the resulting collection"
        Activity experimentValue = searchResults.get(0)

        and: "We call the extractActivitiesFromExperiment method with the experiment Value to get the SpreadSheetActivity"
        SpreadSheetActivity spreadSheetActivity = molecularSpreadSheetService.extractActivitiesFromExperiment(experimentValue)

        when: "We call the createDoseCurve method with the spreadSheetActivity.hillCurveValue value and the other parameters"
        final HillCurveValue hillCurveValue = spreadSheetActivity.hillCurveValueList[0]
        JFreeChart jFreeChart = this.doseCurveRenderingService.createDoseCurve(hillCurveValue.conc as List<Double>,
                hillCurveValue.response as List<Double>, hillCurveValue.slope, hillCurveValue.coef, hillCurveValue.s0, hillCurveValue.sinf, 'X', 'Y',
                null, null, null, null)

        then: "We expect to get back a JFreeChart back"
        assert jFreeChart
        final File file = new File("testChar.jpg")
        ChartUtilities.saveChartAsJPEG(file, jFreeChart, 500, 500);
        assert file.exists()


        where:
        label                                    | cids                                                      | experimentId
        "An existing experiment with activities" | [new Long(2836861), new Long(5882673), new Long(5604367)] | new Long(346)

    }

    void "tests findDrcData"() {
        given: "That we have created an ETag from a list of CIDs"
        final Object compoundETag = compoundRestService.newETag("Compound ETags For Activities", cids);

        and: "We call the activities method on the restExperimentService with the experiment and the ETag"
        final ExperimentData experimentIterator = this.experimentRestService.activities(experimentId, compoundETag);


        and: "We extract the first experimen tValue in the resulting collection"
        Activity experimentValue = experimentIterator.activities.get(0)

        and: "We call the extractActivitiesFromExperiment method with the experiment Value to get the SpreadSheetActivity"
        SpreadSheetActivity spreadSheetActivity = molecularSpreadSheetService.extractActivitiesFromExperiment(experimentValue)

        when: "We call the findDrcData method with the spreadSheetActivity.hillCurveValue value"
        final HillCurveValue hillCurveValue = spreadSheetActivity.getHillCurveValueList()[0]
        final Drc drc =
            this.doseCurveRenderingService.findDrcData(hillCurveValue.conc as List<Double>,
                    hillCurveValue.response as List<Double>, hillCurveValue.slope, hillCurveValue.coef, hillCurveValue.s0, hillCurveValue.sinf)

        then: "We expect to get back a Drc object with the number of activities matching the number of concentrations"
        assert drc
        assert drc.concentrations
        assert drc.activities
        assert drc.curveParameters
        assert drc.concentrations.size() == drc.activities.size()
        //we need to assert the curve parameters

        where:
        label                                    | cids                                                      | experimentId
        "An existing experiment with activities" | [new Long(2836861), new Long(5882673), new Long(5604367)] | new Long(346)

    }

    void "tests findDrcData No AC50"() {
        given: "That we have an Experiment"
        final Long experimentId = new Long(1326)

        and: "We call the activities method on the restExperimentService with the experiment and the ETag"
        final ExperimentData experimentIterator = this.experimentRestService.activities(experimentId);

        and: "We extract the first experimen tValue in the resulting collection"
        Activity experimentValue = experimentIterator.activities.get(0)


        and: "We call the extractActivitiesFromExperiment method with the experiment Value to get the SpreadSheetActivity"
        SpreadSheetActivity spreadSheetActivity = molecularSpreadSheetService.extractActivitiesFromExperiment(experimentValue)

        when: "We call the findDrcData method with the spreadSheetActivity.hillCurveValue value"
        final HillCurveValue hillCurveValue = spreadSheetActivity.hillCurveValueList[0]
        JFreeChart jFreeChart = this.doseCurveRenderingService.createDoseCurve(hillCurveValue.conc as List<Double>,
                hillCurveValue.response as List<Double>, hillCurveValue.slope, hillCurveValue.coef, hillCurveValue.s0, hillCurveValue.sinf, 'X', 'Y',
                null, null, null, null)

        then: "We expect to get back a JFreeChart back"
        assert jFreeChart
        final File file = new File("testCharNoFit.jpg")
        ChartUtilities.saveChartAsJPEG(file, jFreeChart, 500, 500);
        assert file.exists()
    }

}
