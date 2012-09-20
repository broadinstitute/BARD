package curverendering;


import bard.core.HillCurveValue

import org.jfree.chart.JFreeChart

import java.awt.Color

public class DoseCurveRenderingService {
    /**
     *
     * @param hillCurveValue
     * @param xNormMin
     * @param xNormMax
     * @param yNormMin
     * @param yNormMax
     * @return JFreeChart
     */
    public JFreeChart createDoseCurve(final HillCurveValue hillCurveValue, Double xNormMin, Double xNormMax, Double yNormMin, Double yNormMax) {
        final Drc data = findDrcData(hillCurveValue)
        return DoseCurveImage.createDoseCurve(data, xNormMin, xNormMax, yNormMin, yNormMax);
    }

    protected Drc findDrcData(final HillCurveValue hillCurveValue) {
        try {

            final Double[] concentrations = hillCurveValue.getConc()
            final Double[] activities = hillCurveValue.getResponse()
            final Boolean[] isValid = new Boolean[activities.length]
            //pre-populate, we are doing this because the DRC requires an array of booleans
            //I could change it but it would require too many changes
            for(int index = 0; index < activities.length;index++){
                 isValid[index] = true
            }
            //public CurveParameters(Double ac50, Date resultTime, Double hill_slope, Double s0, Double sinf, Double lower95CL, Double upper95CL) {
                //ac50 is the slope
                CurveParameters param = new CurveParameters(hillCurveValue.slope, new Date(), hillCurveValue.coef,
                    hillCurveValue.s0,
                    hillCurveValue.sinf,
                    new Double(0), new Double(0));

            return new Drc(concentrations as List<Double>, activities as List<Double>, isValid as List<Boolean>, param, Color.BLACK);

        } catch (Exception e) {
            throw new RuntimeException(
                    "Problem generating DRC curve", e);
        }
    }
}

