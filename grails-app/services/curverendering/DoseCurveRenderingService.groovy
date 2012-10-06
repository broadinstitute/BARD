package curverendering;


import bard.core.HillCurveValue
import org.jfree.chart.JFreeChart

import java.awt.Color

public class DoseCurveRenderingService {

    /**
     *
     * @param concentrations
     * @param activities
     * @param slope - ac50
     * @param coef - hillSlope
     * @param s0
     * @param sinf
     * @param xNormMin
     * @param xNormMax
     * @param yNormMin
     * @param yNormMax
     * @return JFreeChart
     */
    public JFreeChart createDoseCurve(
            final List<Double> concentrations,
            final List<Double> activities,
            final Double slope,
            final Double coef,
            final Double s0,
            final Double sinf,
            final Double xNormMin,
            final Double xNormMax,
            final Double yNormMin,
            final Double yNormMax) {

        final Drc doseResponseCurve = findDrcData(concentrations, activities, slope, coef, s0, sinf)
        return DoseCurveImage.createDoseCurve(doseResponseCurve, xNormMin, xNormMax, yNormMin, yNormMax);
    }

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
        return createDoseCurve(hillCurveValue.getConc() as List<Double>, hillCurveValue.getResponse() as List<Double>, hillCurveValue.slope, hillCurveValue.coef, hillCurveValue.s0,
                hillCurveValue.sinf, xNormMin, xNormMax, yNormMin, yNormMax)
    }

    /**
     *
     * @param concentrations
     * @param activities
     * @param ac50
     * @param hillSlope
     * @param s0
     * @param sinf
     * @return Drc
     */
    public Drc findDrcData(final List<Double> concentrations, final List<Double> activities, final Double ac50, final Double hillSlope,
                              final Double s0, final Double sinf) {
        try {

            final List<Boolean> isValid = new ArrayList<Boolean>()
            //pre-populate, we are doing this because the DRC requires an array of booleans
            //I could change it but it would require too many changes
            for (Double activity : activities) {
                isValid.add(Boolean.TRUE)
            }
            //ac50 is the slope
            CurveParameters curveParameters = new CurveParameters(
                    ac50,
                    new Date(), hillSlope,
                    s0,
                    sinf,
                    new Double(0), new Double(0));

            return new Drc(concentrations, activities, isValid, curveParameters, Color.BLACK);

        } catch (Exception e) {
            throw new RuntimeException(
                    "Problem generating DRC curve", e);
        }
    }
}

