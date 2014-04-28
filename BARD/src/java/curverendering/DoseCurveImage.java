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

package curverendering;

import org.apache.commons.lang3.tuple.MutablePair;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.DefaultXYDataset;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.util.*;
import java.util.List;

public class DoseCurveImage {
    public static final Color[] colors = new Color[]{Color.BLUE, Color.GREEN, Color.MAGENTA, Color.ORANGE, Color.PINK, Color.RED};


    public static NumberAxis createAndConfigureYAxis(Bounds bounds, Color axisColor, String label) {
        final NumberAxis rangeAxis = new NumberAxis(null);
        rangeAxis.setAutoRangeIncludesZero(true);
        rangeAxis.setAxisLinePaint(axisColor);
        rangeAxis.setTickLabelPaint(axisColor);
        rangeAxis.setLabelPaint(axisColor);
        if (bounds != null) {
            double inset = (bounds.yMax - bounds.yMin) * 0.05;
            rangeAxis.setLowerBound(bounds.yMin - inset);
            rangeAxis.setUpperBound(bounds.yMax + inset);
        }
        rangeAxis.setLabel(label);
        return rangeAxis;
    }

    public static NumberAxis createAndConfigureXAxis(Bounds bounds, Color axisColor, String label) {
        final NumberAxis domainAxis = new NumberAxis(null);
        domainAxis.setAutoRangeIncludesZero(false);
        domainAxis.setAxisLinePaint(axisColor);
        domainAxis.setTickLabelPaint(axisColor);
        domainAxis.setLabelPaint(axisColor);
        if (bounds != null) {
            double inset = (Math.log10(bounds.xMax) - Math.log10(bounds.xMin)) * 0.05;
            domainAxis.setLowerBound(Math.log10(bounds.xMin) - inset);
            domainAxis.setUpperBound(Math.log10(bounds.xMax) + inset);
        }
        domainAxis.setLabel(label);
        return domainAxis;
    }

    public static void addCurves(Map<String, Drc> curves, DefaultXYDataset dataset, XYLineAndShapeRenderer renderer, XYPlot plot) {
        // int curveIndex = 0;
        for (Map.Entry<String, Drc> curve : curves.entrySet()) {
            Drc drc = curve.getValue();
            String name = curve.getKey();

            List<Double> x = drc.getConcentrations();
            List<Double> y = drc.getActivities();
            List<Boolean> isValid = drc.getIsValid();
            if ((x != null) && (y != null)) {
                addCurve(name, dataset, renderer, plot, x, y, isValid, drc.getCurveParameters(), drc.getColor());
            }

        }
    }

    public static JFreeChart createChart(Map<String, Drc> curves, Bounds bounds, Color axisColor, String xAxisLabel, String yAxisLabel) {

        // create and configure x axis
        final NumberAxis domainAxis = createAndConfigureXAxis(bounds, axisColor, xAxisLabel);

        // create and configure y axis
        final NumberAxis rangeAxis = createAndConfigureYAxis(bounds, axisColor, yAxisLabel);

        DefaultXYDataset dataset = new DefaultXYDataset();

        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer(false, true);
        renderer.setDrawOutlines(false); //hide the X at the origin of axes.
        final XYPlot plot = new XYPlot(dataset, domainAxis, rangeAxis, renderer);

        addCurves(curves, dataset, renderer, plot);

        final JFreeChart chart = new JFreeChart("Dose response curve", plot);

        chart.getRenderingHints().put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        chart.setTitle(
                (String) null);
        chart.removeLegend(); // temporarily remove legend
        return chart;
    }


    public static void aggregateValidAndInvalidPoints(List<Boolean> isValid, List<Double> x, List<Double> y, List<Double> validXs, List<Double> validYs, List<Double> invalidXs, List<Double> invalidYs) {

        for (int i = 0; i < y.size(); i++) {
            Double nx = Math.log10(x.get(i));
            if (nx == Double.NEGATIVE_INFINITY) {
                continue;
            }
            if (isValid.get(i)) {
                validXs.add(nx);
                validYs.add(y.get(i));
            } else {
                invalidXs.add(nx);
                invalidYs.add(y.get(i));
            }
        }
    }

    public static void setRenderingSeriesForValidPoints(XYLineAndShapeRenderer renderer, int seriesIndex, Color curveColor) {
        renderer.setSeriesLinesVisible(seriesIndex, false);
        renderer.setSeriesShapesVisible(seriesIndex, true);
        renderer.setSeriesVisibleInLegend(seriesIndex, true);
        renderer.setSeriesShape(seriesIndex, new Ellipse2D.Double(-3.0, -3.0, 6.0, 6.0));
        renderer.setSeriesPaint(seriesIndex, new Color(curveColor.getRed(), curveColor.getGreen(), curveColor.getBlue(), 88));

    }

    private static void setRenderingSeriesForInValidPoints(XYLineAndShapeRenderer renderer, int seriesIndex) {
        GeneralPath xShape = new GeneralPath();
        xShape.moveTo(-3.0, -3.0);
        xShape.lineTo(3.0, 3.0);
        xShape.moveTo(3.0, -3.0);
        xShape.lineTo(-3.0, 3.0);

        renderer.setSeriesLinesVisible(seriesIndex, false);
        renderer.setSeriesShapesVisible(seriesIndex, true);
        renderer.setSeriesVisibleInLegend(seriesIndex, false);
        renderer.setSeriesPaint(seriesIndex, Color.GRAY);
        renderer.setSeriesShape(seriesIndex, xShape);
    }

    /**
     * Find where to start and stop on the X-Axis
     *
     * @param validX - The array of valid values to appear on X-Axis
     * @return MutablePair
     *         The left value = xStart
     *         The right value == xStop
     */
    static MutablePair<Double, Double> findWhereToStartAndStopOnXAxis(double[] validX) {
        double xStart = 0;
        double xStop = 0;
        for (int i = 0; i < validX.length; i++) {
            if (i == 0 || validX[i] < xStart) {
                xStart = validX[i];
            }
            if (i == 0 || validX[i] > xStop) {
                xStop = validX[i];
            }
        }
        MutablePair<Double, Double> startStop = new MutablePair<Double, Double>();
        startStop.setLeft(xStart);
        startStop.setRight(xStop);
        return startStop;
    }

    public static void addFittedCurve(String name, DefaultXYDataset dataset, XYLineAndShapeRenderer renderer,
                                      CurveParameters curveParameters, Color curveColor, double[] validX) {
        // add the fitted curve (if possible)
        if (curveParameters != null
                && curveParameters.getS0() != null
                && curveParameters.getSINF() != null
                && curveParameters.getSlope() != null
                && curveParameters.getHILL_SLOPE() != null) {

            MutablePair<Double, Double> whereToStartAndStopOnXAxis = findWhereToStartAndStopOnXAxis(validX);
            double xStart = whereToStartAndStopOnXAxis.getLeft();
            double xStop = whereToStartAndStopOnXAxis.getRight();

            int seriesIndex = dataset.getSeriesCount();
            dataset.addSeries(name + " fit",
                    generateDataForSigmoidCurve(curveParameters.getS0(),
                            curveParameters.getSINF(), curveParameters.getSlope(),
                            curveParameters.getHILL_SLOPE(), xStart, xStop, 50));
            renderer.setSeriesLinesVisible(seriesIndex, true);
            renderer.setSeriesShapesVisible(seriesIndex, false);
            renderer.setSeriesVisibleInLegend(seriesIndex, false);
            renderer.setSeriesPaint(seriesIndex, curveColor);
            float lineWidth = 1.5f;
            BasicStroke stroke = new BasicStroke(lineWidth);
            renderer.setSeriesStroke(seriesIndex, stroke); //series line style
        }
    }


    private static void addCurve(String name, DefaultXYDataset dataset, XYLineAndShapeRenderer renderer, XYPlot plot, List<Double> x,
                                 List<Double> y, List<Boolean> isValid, CurveParameters curveParameters, Color curveColor) {
        List<Double> validXs = new ArrayList<Double>();
        List<Double> validYs = new ArrayList<Double>();
        List<Double> invalidXs = new ArrayList<Double>();
        List<Double> invalidYs = new ArrayList<Double>();

        aggregateValidAndInvalidPoints(isValid, x, y, validXs, validYs, invalidXs, invalidYs);

        // add the data and rendering for the valid points
        int seriesIndex = dataset.getSeriesCount();
        dataset.addSeries(name, new double[][]{convertListDoubleToDoublePrimitiveArray(validXs), convertListDoubleToDoublePrimitiveArray(validYs)});
        setRenderingSeriesForValidPoints(renderer, seriesIndex, curveColor);

        // add the data and rendering for the invalid points
        if (invalidXs.size() > 0) {
            seriesIndex = dataset.getSeriesCount();
            dataset.addSeries(name + " masked", new double[][]{convertListDoubleToDoublePrimitiveArray(invalidXs), convertListDoubleToDoublePrimitiveArray(invalidYs)});
            setRenderingSeriesForInValidPoints(renderer, seriesIndex);
        }

        // add the fitted curve (if possible)
        addFittedCurve(name, dataset, renderer, curveParameters, curveColor, convertListDoubleToDoublePrimitiveArray(validXs));
        // add confidence bounds
        addConfidenceBounds(curveParameters, curveColor, plot);
    }

    public static double[] convertListDoubleToDoublePrimitiveArray(List<Double> doubleList) {
        double[] doubles = new double[doubleList.size()];
        int i = 0;

        for (Double d : doubleList) {
            doubles[i++] = d;
        }

        return doubles;
    }

    public static void addConfidenceBounds(CurveParameters curveParameters, Color curveColor, XYPlot plot) {
        // add confidence bounds
        if (curveParameters != null
                && curveParameters.getS0() != null
                && curveParameters.getSINF() != null
                && curveParameters.getLower95CL() != null
                && curveParameters.getUpper95CL() != null) {
            final float confidenceY = (float) (curveParameters.getS0() + curveParameters.getSINF()) / 2;
            final float confidenceX1 = (float) Math.log10(curveParameters.getLower95CL());
            final float confidenceX2 = (float) Math.log10(curveParameters.getUpper95CL());

            // add an "annotation" which renders the confidence bounds.  Based
            // on XYLineAnnotation in the JFreeChart codebase
            plot.addAnnotation(new ConfidenceBoundAnnotation(confidenceX1, confidenceX2, confidenceY, curveColor));
        }
    }

    /**
     * Construct points from the given curve fit paramaters
     *
     * @param s0         - s0
     * @param sinf       - sinf
     * @param EC50       - EC50,AC50 or slope
     * @param hillSlope  - The hillSlope
     * @param xStart     - Where to start on the x-axis
     * @param xEnd       - Where to end on the y-axis
     * @param pointCount - the number of points
     * @return double[][]
     */
    private static double[][] generateDataForSigmoidCurve(double s0, double sinf, double EC50, double hillSlope, double xStart, double xEnd, int pointCount) {
        double[][] points = new double[2][pointCount];

        double logEC50 = Math.log10(EC50);

        for (int i = 0; i < pointCount; i++) {
            double x = ((xEnd - xStart) * i / ((float) pointCount - 1)) + xStart;
            double y = s0 + (sinf - s0) / (1 + Math.pow(10, ((logEC50 - x) * hillSlope)));

            points[0][i] = x;
            points[1][i] = y;
        }

        return points;
    }

    public static Bounds adjustBounds(Drc cell, Bounds bounds, Double xNormMin, Double xNormMax, Double yNormMin, Double yNormMax) {
        if (cell.getActivities().size() > 0 && cell.getConcentrations().size() > 0) {

            double maxActivity = cell.getActivities().get(0);
            double minActivity = cell.getActivities().get(0);
            double maxConcentration = cell.getConcentrations().get(0);
            double minConcentration = cell.getConcentrations().get(0);

            MutablePair<Double, Double> activity = findMinMax(cell.getActivities());

            if (yNormMax == null) {
                maxActivity = activity.getRight();
            }
            if (yNormMin == null) {
                minActivity = activity.getLeft();
            }

            MutablePair<Double, Double> concentration = findMinMax(cell.getConcentrations());
            if (xNormMax == null) {
                maxConcentration = concentration.getRight();
            }
            if (xNormMin == null) {
                minConcentration = concentration.getLeft();
            }


            if (bounds == null) {
                bounds = new Bounds(minConcentration, maxConcentration, minActivity, maxActivity);
            } else {
                bounds.expandToContain(minConcentration, maxConcentration, minActivity, maxActivity);
            }
        }
        return bounds;
    }

    /**
     * Find the max and min from the given list
     *
     * @param list - A list of doubles from which to get the min and max values
     * @return {@link MutablePair}
     *         The left value is the min and the right value is the max
     */
    private static MutablePair<Double, Double> findMinMax(List<Double> list) {
        double maxValue = list.get(0);
        double minValue = list.get(0);

        MutablePair<Double, Double> maxMinValues = new MutablePair<Double, Double>();
        for (double f : list) {
            maxValue = Math.max(maxValue, f);
            minValue = Math.min(minValue, f);
        }
        maxMinValues.setLeft(minValue);
        maxMinValues.setRight(maxValue);
        return maxMinValues;
    }

    /**
     * Adjust the bounds by normalizing
     *
     * @param drcs     - Dose response points
     * @param xNormMin - The minimum normalized x value
     * @param xNormMax - The maximum normalized x value
     * @param yNormMin - The minimum normalized Y value
     * @param yNormMax - The maximum normalized Y value
     * @return {@link Bounds}
     */
    public static Bounds adjustBounds(List<Drc> drcs, Double xNormMin, Double xNormMax, Double yNormMin, Double yNormMax) {
        // users specify data range they would like to show in the image. Concentrations (xAxis) should be
        // logarithm values, so need to convert to real values.
        Bounds bounds = null;

        // users only want to define y axis at this moment
        for (Drc dcr : drcs) {
            bounds = adjustBounds(dcr, bounds, xNormMin, xNormMax, yNormMin, yNormMax);
        }
        if (bounds != null) {
            setBoundParameters(bounds, xNormMin, xNormMax, yNormMin, yNormMax);
        }

        return bounds;
    }

    /**
     * Adjust the bounds by normalizing with the given parameters
     *
     * @param bounds   {@link Bounds}
     * @param xNormMin - The minimum normalized x value
     * @param xNormMax - The maximum normalized x value
     * @param yNormMin - The minimum normalized Y value
     * @param yNormMax - The maximum normalized Y value
     */
    public static void setBoundParameters(Bounds bounds, Double xNormMin, Double xNormMax, Double yNormMin, Double yNormMax) {
        if (xNormMin != null) {
            bounds.xMin = Math.pow(10, xNormMin);
        }
        if (xNormMax != null) {
            bounds.xMax = Math.pow(10, xNormMax);
        }
        if (yNormMin != null) {
            bounds.yMin = yNormMin;
        }
        if (yNormMax != null) {
            bounds.yMax = yNormMax;
        }
    }

    /**
     * Construct a JFreeChart from the given data
     *
     * @param drc      - Dose response point
     * @param xNormMin - The minimum normalized x value
     * @param xNormMax - The maximum normalized x value
     * @param yNormMin - The minimum normalized Y value
     * @param yNormMax - The maximum normalized Y value
     * @return {@link JFreeChart}
     */
    public static JFreeChart createDoseCurve(Drc drc, String xAxisLabel, String yAxisLabel, Double xNormMin, Double xNormMax, Double yNormMin, Double yNormMax) {
        List<Drc> drcs = new ArrayList<Drc>();
        if (drc != null) {
            drcs.add(drc);
            return createDoseCurves(drcs, xAxisLabel, yAxisLabel, xNormMin, xNormMax, yNormMin, yNormMax);
        }
        return null;
    }

    /**
     * Construct a JFreeChart from the given data
     *
     * @param drcs     - list of Dose response point
     * @param xNormMin - The minimum normalized x value
     * @param xNormMax - The maximum normalized x value
     * @param yNormMin - The minimum normalized Y value
     * @param yNormMax - The maximum normalized Y value
     * @return {@link JFreeChart}
     */
    public static JFreeChart createDoseCurves(List<Drc> drcs, String xAxisLabel, String yAxisLabel, Double xNormMin, Double xNormMax, Double yNormMin, Double yNormMax) {
        Map<String, Drc> curves = new HashMap<String, Drc>();
        int colorIndex = 0;
        if (!drcs.isEmpty()) {
            for (Drc drc : drcs) {
                drc.setColor(colors[colorIndex]);
                curves.put(colorIndex + ":" + drc.getCurveParameters().getResultTime().toString(), drc);
                ++colorIndex;
            }
            Bounds bounds = derivedDefaultBounds(drcs, xNormMin, xNormMax, yNormMin, yNormMax);
            return DoseCurveImage.createChart(curves, bounds, Color.BLACK, xAxisLabel, yAxisLabel);
        }
        return null;
    }

    /**
     * We need to derive bounds, but also allow for curves that contain no elements.  Hopefully all the curves we get back from the backend
     * will at one point contain real data but as of now they don't, which was leading our old pathway through the code to crash.
     * <p/>
     * Another special case: if we are given both a maximum and a minimum then don't regenerate those numbers ( we're assuming the caller
     * had a reason to set those numbers explicitly).  If instead the numbers are both null then we'll need to step through and find the bounds
     * across all the curves we've been given.
     *
     * @param drcs
     * @param xNormMin
     * @param xNormMax
     * @param yNormMin
     * @param yNormMax
     * @return
     */
    public static Bounds derivedDefaultBounds(List<Drc> drcs, Double xNormMin, Double xNormMax, Double yNormMin, Double yNormMax) {
        if (!drcs.isEmpty()) {  // if we don't have curve data then there's nothing else we can do
            if ((xNormMin == null) || (xNormMax == null)) {  // at least one of the x bounds was null.  Let's calculate some real x bounds.
                for (Drc drc : drcs) {
                    if (drc.getConcentrations() != null) {
                        for (double concentration : drc.getConcentrations()) {
                            if (xNormMin == null)
                                xNormMin = new Double(concentration);
                            if (xNormMax == null)
                                xNormMax = new Double(concentration);
                            if (concentration < xNormMin.doubleValue()) {
                                xNormMin = new Double(concentration);
                            }
                            if (concentration > xNormMax.doubleValue()) {
                                xNormMax = new Double(concentration);
                            }
                        }
                    }

                }
            }
            if ((yNormMin == null) || (xNormMax == null)) {  // at least one of the y bounds was null.  Let's calculate some real bounds.
                for (Drc drc : drcs) {
                    if (drc.getActivities() != null) {
                        for (double activity : drc.getActivities()) {
                            if ((yNormMin == null))
                                yNormMin = new Double(activity);
                            if ((yNormMax == null))
                                yNormMax = new Double(activity);
                            if (activity < yNormMin.doubleValue()) {
                                yNormMin = new Double(activity);
                            }
                            if (activity > yNormMax.doubleValue()) {
                                yNormMax = new Double(activity);
                            }
                        }

                    }

                }
            }

        }
        if ((xNormMin != null) && (xNormMax != null) && (yNormMin != null) && (yNormMax != null)) {
            return (findBounds(drcs, xNormMin, xNormMax, yNormMin, yNormMax)); // this routine doesn't do too well with null values
        } else {
            return new Bounds(-1d, 1d, -1d, 1d);// This is our emergency exit.  This way we won't thrown exception, but we have no data so there's no curve we can possibly display either.
        }

    }


    /**
     * @param drcs     - Dose response points
     * @param xNormMin - The minimum normalized x value
     * @param xNormMax - The maximum normalized x value
     * @param yNormMin - The minimum normalized Y value
     * @param yNormMax - The maximum normalized Y value
     * @return {@link Bounds}
     */
    static Bounds findBounds(final List<Drc> drcs, final Double xNormMin, final Double xNormMax, final Double yNormMin, final Double yNormMax) {
        if (xNormMin != null && xNormMax != null && yNormMin != null && yNormMax != null) {
            return new Bounds(xNormMin, xNormMax, yNormMin, yNormMax);
        }
        return adjustBounds(drcs, xNormMin, xNormMax, yNormMin, yNormMax);

    }
}
