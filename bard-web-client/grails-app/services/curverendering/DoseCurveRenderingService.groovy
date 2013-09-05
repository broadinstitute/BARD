package curverendering;


import bardqueryapi.DrcCurveCommand
import org.jfree.chart.JFreeChart
import org.jfree.chart.encoders.EncoderUtil
import org.jfree.chart.encoders.ImageFormat

import java.awt.Color

class DoseCurveRenderingService {

    byte[] createDoseCurve(DrcCurveCommand drcCurveCommand) {
        JFreeChart chart =
            this.createDoseCurve(
                    drcCurveCommand.concentrations,
                    drcCurveCommand.activities,
                    drcCurveCommand.slope,
                    drcCurveCommand.hillSlope,
                    drcCurveCommand.s0,
                    drcCurveCommand.sinf,
                    drcCurveCommand.xAxisLabel,
                    drcCurveCommand.yAxisLabel,
                    null,
                    null,
                    drcCurveCommand.yNormMin,
                    drcCurveCommand.yNormMax)
        // write the image byte array to the binding
        return EncoderUtil.encode(chart.createBufferedImage(drcCurveCommand.width.intValue(),
                drcCurveCommand.height.intValue()), ImageFormat.PNG)
    }
    byte[] createDoseCurves(DrcCurveCommand drcCurveCommand) {
        JFreeChart chart =
            this.createDoseCurves(
                    drcCurveCommand.curves,
                    drcCurveCommand.xAxisLabel,
                    drcCurveCommand.yAxisLabel,
                    null,
                    null,
                    drcCurveCommand.yNormMin,
                    drcCurveCommand.yNormMax)
        // write the image byte array to the binding
        return EncoderUtil.encode(chart.createBufferedImage(drcCurveCommand.width.intValue(),
                drcCurveCommand.height.intValue()), ImageFormat.PNG)
    }
    /**
     *
     * @param concentrations
     * @param activities
     * @param slope
     * @param hillSlope
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
            final Double hillSlope,
            final Double s0,
            final Double sinf,
            final String xAxisLabel,
            final String yAxisLabel,
            final Double xNormMin,
            final Double xNormMax,
            final Double yNormMin,
            final Double yNormMax) {

        return createDoseCurves([new Curve(concentrations: concentrations,activities: activities,slope: slope,hillSlope: hillSlope,s0: s0,sinf: sinf)],
                xAxisLabel, yAxisLabel, xNormMin, xNormMax, yNormMin, yNormMax)
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
    JFreeChart createDoseCurves(
            final List<Curve> curves,
            final String xAxisLabel,
            final String yAxisLabel,
            final Double xNormMin,
            final Double xNormMax,
            final Double yNormMin,
            final Double yNormMax) {
        final List<Drc> drcs = []
        for(Curve curve: curves){
            final Drc doseResponseCurve = findDrcData(curve.concentrations, curve.activities, curve.slope, curve.hillSlope, curve.s0, curve.sinf, Color.BLACK)
            drcs.add(doseResponseCurve)
        }
        return DoseCurveImage.createDoseCurves(drcs, xAxisLabel, yAxisLabel, xNormMin, xNormMax, yNormMin, yNormMax)
    }
    /**
     *
     * @param concentrations
     * @param activities
     * @param slope
     * @param hillSlope
     * @param s0
     * @param sinf
     * @return Drc
     */
    Drc findDrcData(List<Double> concentrations, List<Double> activities, Double slope, Double hillSlope, Double s0, Double sinf, Color color) {

        final List<Boolean> isValid = []
        //pre-populate, we are doing this because the DRC requires an array of booleans
        //I could change it but it would require too many changes
        for (Double activity : activities) {
            isValid.add(Boolean.TRUE)
        }
        CurveParameters curveParameters = new CurveParameters(
                slope,
                new Date(), hillSlope,
                s0,
                sinf,
                new Double(0), new Double(0))

        return new Drc(concentrations, activities, isValid, curveParameters, color)
    }
}

