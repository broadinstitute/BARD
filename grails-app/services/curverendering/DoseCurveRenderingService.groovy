package curverendering;


import bard.core.HillCurveValue
import org.jfree.chart.JFreeChart

import java.awt.Color
import org.jfree.chart.encoders.EncoderUtil
import org.jfree.chart.encoders.ImageFormat
import bardqueryapi.DrcCurveCommand

class DoseCurveRenderingService {

    byte[] createDoseCurve(DrcCurveCommand drcCurveCommand){
        JFreeChart chart =
            this.createDoseCurve(
                    drcCurveCommand.concentrations,
                    drcCurveCommand.activities,
                    drcCurveCommand.ac50,
                    drcCurveCommand.hillSlope,
                    drcCurveCommand.s0,
                    drcCurveCommand.sinf,
                    drcCurveCommand.xAxisLabel,
                    drcCurveCommand.yAxisLabel,
                    null,
                    null,
                    null,
                    null)
        // write the image byte array to the binding
       return EncoderUtil.encode(chart.createBufferedImage(drcCurveCommand.width.intValue(),
               drcCurveCommand.height.intValue()), ImageFormat.PNG)
    }

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
    JFreeChart createDoseCurve(
            final List<Double> concentrations,
            final List<Double> activities,
            final Double slope,
            final Double coef,
            final Double s0,
            final Double sinf,
            final String xAxisLabel,
            final String yAxisLabel,
            final Double xNormMin,
            final Double xNormMax,
            final Double yNormMin,
            final Double yNormMax) {

        final Drc doseResponseCurve = findDrcData(concentrations, activities, slope, coef, s0, sinf)
        return DoseCurveImage.createDoseCurve(doseResponseCurve, xAxisLabel,yAxisLabel,xNormMin, xNormMax, yNormMin, yNormMax)
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
    Drc findDrcData(final List<Double> concentrations, final List<Double> activities, final Double ac50, final Double hillSlope,
                           final Double s0, final Double sinf) {

        final List<Boolean> isValid = []
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
                new Double(0), new Double(0))

        return new Drc(concentrations, activities, isValid, curveParameters, Color.BLACK)
    }
}

