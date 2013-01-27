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

            addCurve(name, dataset, renderer, plot, x, y, isValid, drc.getCurveParameters(), drc.getColor());
        }
    }

    public static JFreeChart createChart(Map<String, Drc> curves, Bounds bounds, Color axisColor, String xAxisLabel, String yAxisLabel) {

        // create and configure x axis
        final NumberAxis domainAxis = createAndConfigureXAxis(bounds, axisColor, xAxisLabel);

        // create and configure y axis
        final NumberAxis rangeAxis = createAndConfigureYAxis(bounds, axisColor, yAxisLabel);

        DefaultXYDataset dataset = new DefaultXYDataset();

        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer(false, true);
        final XYPlot plot = new XYPlot(dataset, domainAxis, rangeAxis, renderer);

        addCurves(curves, dataset, renderer, plot);

        final JFreeChart chart = new JFreeChart("Dose response curve", plot);

        chart.getRenderingHints().put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        chart.setTitle(
                (String) null);
        chart.removeLegend(); // temporarily remove legend
        return chart;
    }


    public static void aggregateValidAndInvalidPoints(List<Boolean> isValid, List<Double> x, List<Double> y, double validX[], double validY[], double invalidX[], double invalidY[]) {
        int validCount = 0;
        int invalidCount = 0;

        for (int i = 0; i < y.size(); i++) {
            double nx = Math.log10(x.get(i));
            if (isValid.get(i)) {
                validX[validCount] = nx;
                validY[validCount] = y.get(i);
                validCount++;
            } else {
                invalidX[invalidCount] = nx;
                invalidY[invalidCount] = y.get(i);
                invalidCount++;
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
        double validX[] = new double[x.size()];
        double validY[] = new double[x.size()];
        double invalidX[] = new double[y.size()];
        double invalidY[] = new double[y.size()];

        aggregateValidAndInvalidPoints(isValid, x, y, validX, validY, invalidX, invalidY);

        // add the data and rendering for the valid points
        int seriesIndex = dataset.getSeriesCount();
        dataset.addSeries(name, new double[][]{Arrays.copyOf(validX, validX.length), Arrays.copyOf(validY, validY.length)});
        setRenderingSeriesForValidPoints(renderer, seriesIndex, curveColor);

        // add the data and rendering for the invalid points
        seriesIndex = dataset.getSeriesCount();
        dataset.addSeries(name + " masked", new double[][]{Arrays.copyOf(invalidX, invalidX.length), Arrays.copyOf(invalidY, invalidY.length)});
        setRenderingSeriesForInValidPoints(renderer, seriesIndex);

        // add the fitted curve (if possible)
        addFittedCurve(name, dataset, renderer, curveParameters, curveColor, validX);
        // add confidence bounds
        addConfidenceBounds(curveParameters, curveColor, plot);
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
            Bounds bounds = findBounds(drcs, xNormMin, xNormMax, yNormMin, yNormMax);
            return DoseCurveImage.createChart(curves, bounds, Color.BLACK, xAxisLabel, yAxisLabel);
        }
        return null;
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
            final double powerXMin = Math.pow(10, xNormMin);
            final double powerXMax = Math.pow(10, xNormMax);
            return new Bounds(powerXMin, powerXMax, yNormMin, yNormMax);
        }
        return adjustBounds(drcs, xNormMin, xNormMax, yNormMin, yNormMax);

    }
}
