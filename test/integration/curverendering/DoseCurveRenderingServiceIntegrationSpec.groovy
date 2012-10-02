package curverendering

import bard.core.Experiment
import bard.core.HillCurveValue
import bard.core.ServiceIterator
import bard.core.Value
import bard.core.rest.RESTCompoundService
import bard.core.rest.RESTExperimentService
import bardqueryapi.MolecularSpreadSheetService
import bardqueryapi.SpreadSheetActivity
import grails.plugin.spock.IntegrationSpec
import org.jfree.chart.ChartUtilities
import org.jfree.chart.JFreeChart
import org.junit.After
import org.junit.Before
import spock.lang.Unroll

@Unroll
class DoseCurveRenderingServiceIntegrationSpec extends IntegrationSpec {

    MolecularSpreadSheetService molecularSpreadSheetService
    RESTCompoundService restCompoundService
    RESTExperimentService restExperimentService
    DoseCurveRenderingService doseCurveRenderingService


    @Before
    void setup() {
        this.restExperimentService = molecularSpreadSheetService.queryServiceWrapper.restExperimentService
        this.restCompoundService = molecularSpreadSheetService.queryServiceWrapper.restCompoundService

    }

    @After
    void tearDown() {

    }



    void "tests createDoseCurve"() {
        given: "That we have created an ETag from a list of CIDs"
        final Object compoundETag = restCompoundService.newETag("Compound ETags For Activities", cids);

        and: "That we have an Experiment in which the Compounds were tested in."
        Experiment experiment = restExperimentService.get(experimentId)

        and: "We call the activities method on the restExperimentService with the experiment and the ETag"
        final ServiceIterator<Value> experimentIterator = this.restExperimentService.activities(experiment, compoundETag);
        Collection collect = experimentIterator.collect()

        and: "We extract the first experimen tValue in the resulting collection"
        Value experimentValue = collect.iterator().next()

        and: "We call the extractActivitiesFromExperiment method with the experiment Value to get the SpreadSheetActivity"
        SpreadSheetActivity spreadSheetActivity = molecularSpreadSheetService.extractActivitiesFromExperiment(experimentValue,experimentId)

        when: "We call the createDoseCurve method with the spreadSheetActivity.hillCurveValue value and the other parameters"
        JFreeChart jFreeChart = this.doseCurveRenderingService.createDoseCurve(spreadSheetActivity.hillCurveValue, null, null, null, null)

        then: "We expect to get back a JFreeChart back"
        assert jFreeChart
        try {
            final File file = new File("testChar.jpg")
            ChartUtilities.saveChartAsJPEG(file, jFreeChart, 500, 500);
            assert file.exists()
        } catch (IOException e) {
            System.err.println("Problem occurred creating chart.");
        }

        where:
        label                                    | cids                                                      | experimentId
        "An existing experiment with activities" | [new Long(2836861), new Long(5882673), new Long(5604367)] | new Long(346)

    }
    void "tests createDoseCurve with points"() {
        given: "That we have created an ETag from a list of CIDs"
        final Object compoundETag = restCompoundService.newETag("Compound ETags For Activities", cids);

        and: "That we have an Experiment in which the Compounds were tested in."
        Experiment experiment = restExperimentService.get(experimentId)

        and: "We call the activities method on the restExperimentService with the experiment and the ETag"
        final ServiceIterator<Value> experimentIterator = this.restExperimentService.activities(experiment, compoundETag);
        Collection collect = experimentIterator.collect()

        and: "We extract the first experimen tValue in the resulting collection"
        Value experimentValue = collect.iterator().next()

        and: "We call the extractActivitiesFromExperiment method with the experiment Value to get the SpreadSheetActivity"
        SpreadSheetActivity spreadSheetActivity = molecularSpreadSheetService.extractActivitiesFromExperiment(experimentValue,experimentId)

        when: "We call the createDoseCurve method with the spreadSheetActivity.hillCurveValue value and the other parameters"
        final HillCurveValue hillCurveValue = spreadSheetActivity.hillCurveValue
        JFreeChart jFreeChart = this.doseCurveRenderingService.createDoseCurve(hillCurveValue.getConc() as List<Double>,
                hillCurveValue.getResponse() as List<Double>, hillCurveValue.slope, hillCurveValue.coef, hillCurveValue.s0, hillCurveValue.sinf,
                null, null, null, null)

        then: "We expect to get back a JFreeChart back"
        assert jFreeChart
        try {
            final File file = new File("testChar.jpg")
            ChartUtilities.saveChartAsJPEG(file, jFreeChart, 500, 500);
            assert file.exists()
        } catch (IOException e) {
            System.err.println("Problem occurred creating chart.");
        }

        where:
        label                                    | cids                                                      | experimentId
        "An existing experiment with activities" | [new Long(2836861), new Long(5882673), new Long(5604367)] | new Long(346)

    }
    void "tests findDrcData"() {
        given: "That we have created an ETag from a list of CIDs"
        final Object compoundETag = restCompoundService.newETag("Compound ETags For Activities", cids);

        and: "That we have an Experiment in which the Compounds were tested in."
        Experiment experiment = restExperimentService.get(experimentId)

        and: "We call the activities method on the restExperimentService with the experiment and the ETag"
        final ServiceIterator<Value> experimentIterator = this.restExperimentService.activities(experiment, compoundETag);
        Collection collect = experimentIterator.collect()

        and: "We extract the first experimen tValue in the resulting collection"
        Value experimentValue = collect.iterator().next()

        and: "We call the extractActivitiesFromExperiment method with the experiment Value to get the SpreadSheetActivity"
        SpreadSheetActivity spreadSheetActivity = molecularSpreadSheetService.extractActivitiesFromExperiment(experimentValue,experimentId)

        when: "We call the findDrcData method with the spreadSheetActivity.hillCurveValue value"
        final HillCurveValue hillCurveValue = spreadSheetActivity.hillCurveValue
        final Drc drc =
            this.doseCurveRenderingService.findDrcData(hillCurveValue.getConc() as List<Double>,
                    hillCurveValue.getResponse() as List<Double>, hillCurveValue.slope, hillCurveValue.coef, hillCurveValue.s0, hillCurveValue.sinf)

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
        Experiment experiment = restExperimentService.get(experimentId)

        and: "We call the activities method on the restExperimentService with the experiment and the ETag"
        final ServiceIterator<Value> experimentIterator = this.restExperimentService.activities(experiment);
        Collection collect = experimentIterator.collect()

        and: "We extract the first experimen tValue in the resulting collection"
        Value experimentValue = collect.iterator().next()

        and: "We call the extractActivitiesFromExperiment method with the experiment Value to get the SpreadSheetActivity"
        SpreadSheetActivity spreadSheetActivity = molecularSpreadSheetService.extractActivitiesFromExperiment(experimentValue,experimentId)

        when: "We call the findDrcData method with the spreadSheetActivity.hillCurveValue value"
        final HillCurveValue hillCurveValue = spreadSheetActivity.hillCurveValue
        JFreeChart jFreeChart = this.doseCurveRenderingService.createDoseCurve(hillCurveValue.getConc() as List<Double>,
                hillCurveValue.getResponse() as List<Double>, hillCurveValue.slope, hillCurveValue.coef, hillCurveValue.s0, hillCurveValue.sinf,
                null, null, null, null)

        then: "We expect to get back a JFreeChart back"
        assert jFreeChart
        try {
            final File file = new File("testCharNoFit.jpg")
            ChartUtilities.saveChartAsJPEG(file, jFreeChart, 500, 500);
            assert file.exists()
        } catch (IOException e) {
            System.err.println("Problem occurred creating chart.");
        }

    }

}
