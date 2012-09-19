package curverendering;

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


    public static JFreeChart createChart(Map<String, Drc> curves, Bounds bounds, Color axisColor) {

        // create and confgiure x axis
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

        // create and configure y axis
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

        DefaultXYDataset dataset = new DefaultXYDataset();
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer(false, true);
        final XYPlot plot = new XYPlot(dataset, domainAxis, rangeAxis, renderer);

       // int curveIndex = 0;
        for (Map.Entry<String, Drc> curve : curves.entrySet()) {
            Drc drc = curve.getValue();
            String name = curve.getKey();

            List<Double> x = drc.getConcentrations();
            List<Double> y = drc.getActivities();
            List<Boolean> isValid = drc.getIsValid();

            addCurve(name, dataset, renderer, plot, x, y, isValid, drc.getCurveParameters(), drc.getColor());
           // curveIndex++;
        }
        final JFreeChart chart = new JFreeChart("Dose response curve", plot);

        chart.getRenderingHints().put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        chart.setTitle(
                (String) null);
        chart.removeLegend(); // temporarily remove legend
        return chart;
    }

    private static void addCurve(String name, DefaultXYDataset dataset, XYLineAndShapeRenderer renderer, XYPlot plot, List<Double> x, List<Double> y, List<Boolean> isValid, CurveParameters curveParameters, Color curveColor) {
        double validX[] = new double[x.size()];
        double validY[] = new double[x.size()];
        double invalidX[] = new double[y.size()];
        double invalidY[] = new double[y.size()];

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

        int seriesIndex = dataset.getSeriesCount();


        // add the valid data points
        dataset.addSeries(name, new double[][]{Arrays.copyOf(validX, validCount), Arrays.copyOf(validY, validCount)});
        renderer.setSeriesLinesVisible(seriesIndex, false);
        renderer.setSeriesShapesVisible(seriesIndex, true);
        renderer.setSeriesVisibleInLegend(seriesIndex, true);
        renderer.setSeriesShape(seriesIndex, new Ellipse2D.Double(-3.0, -3.0, 6.0, 6.0));
        renderer.setSeriesPaint(seriesIndex, new Color(curveColor.getRed(), curveColor.getGreen(), curveColor.getBlue(), 88));

        // add the data and rendering for the invalid points
        seriesIndex = dataset.getSeriesCount();
        dataset.addSeries(name + " masked", new double[][]{Arrays.copyOf(invalidX, invalidCount), Arrays.copyOf(invalidY, invalidCount)});
        // render these points as grayed out x's
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

        // add the fitted curve (if possible)
        if (curveParameters != null
                && curveParameters.S0 != null
                && curveParameters.SINF != null
                && curveParameters.AC50 != null
                && curveParameters.HILL_SLOPE != null) {

            double xStart = 0;
            double xStop = 0;
            for (int i = 0; i < validCount; i++) {
                if (i == 0 || validX[i] < xStart) {
                    xStart = validX[i];
                }
                if (i == 0 || validX[i] > xStop) {
                    xStop = validX[i];
                }
            }

            seriesIndex = dataset.getSeriesCount();
            dataset.addSeries(name + " fit", generateDataForSigmoidCurve(curveParameters.S0, curveParameters.SINF, curveParameters.AC50, curveParameters.HILL_SLOPE, xStart, xStop, 50));
            renderer.setSeriesLinesVisible(seriesIndex, true);
            renderer.setSeriesShapesVisible(seriesIndex, false);
            renderer.setSeriesVisibleInLegend(seriesIndex, false);
            renderer.setSeriesPaint(seriesIndex, curveColor);
            float lineWidth = 1.5f;
            BasicStroke stroke = new BasicStroke(lineWidth);
            renderer.setSeriesStroke(seriesIndex, stroke); //series line style
        }

        // add confidence bounds
        if (curveParameters != null
                && curveParameters.S0 != null
                && curveParameters.SINF != null
                && curveParameters.lower95CL != null
                && curveParameters.upper95CL != null) {
            final float confidenceY = (float) (curveParameters.S0 + curveParameters.SINF) / 2;
            final float confidenceX1 = (float) Math.log10(curveParameters.lower95CL);
            final float confidenceX2 = (float) Math.log10(curveParameters.upper95CL);

            // add an "annotation" which renders the confidence bounds.  Based
            // on XYLineAnnotation in the JFreeChart codebase
            plot.addAnnotation(new ConfidenceBoundAnnotation(confidenceX1, confidenceX2, confidenceY, curveColor));
        }
    }

    public static double[][] generateDataForSigmoidCurve(double s0, double sinf, double EC50, double hillSlope, double xStart, double xEnd, int pointCount) {
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

    private static Bounds adjustBounds(Drc cell, Bounds bounds, Double xNormMin, Double xNormMax, Double yNormMin, Double yNormMax) {
        if (cell.getActivities().size() > 0 && cell.getConcentrations().size() > 0) {
            double maxActivity = cell.getActivities().get(0);
            double minActivity = cell.getActivities().get(0);
            double maxConcentration = cell.getConcentrations().get(0);
            double minConcentration = cell.getConcentrations().get(0);

            for (double f : cell.getActivities()) {
                if (yNormMax == null)
                    maxActivity = Math.max(maxActivity, f);
                if (yNormMin == null)
                    minActivity = Math.min(minActivity, f);
            }

            for (double f : cell.getConcentrations()) {
                if (xNormMax == null)
                    maxConcentration = Math.max(maxConcentration, f);
                if (xNormMin == null)
                    minConcentration = Math.min(minConcentration, f);
            }

            if (bounds == null) {
                bounds = new Bounds(minConcentration, maxConcentration, minActivity, maxActivity);
            } else {
                bounds.expandToContain(minConcentration, maxConcentration, minActivity, maxActivity);
            }
        }
        return bounds;
    }

    static Bounds adjustBounds(List<Drc> dcrs, Double xNormMin, Double xNormMax, Double yNormMin, Double yNormMax) {
        // users specify data range they would like to show in the image. Concentrations (xAxis) should be
        // logarithm values, so need to convert to real values.
        Bounds bounds = null;

        // users only want to define y axis at this moment
        for (Drc dcr : dcrs) {
            bounds = adjustBounds(dcr, bounds, xNormMin, xNormMax, yNormMin, yNormMax);
        }
        if(bounds == null){
            return bounds;
        }
        if (xNormMin != null) {
            bounds.xMin = Math.pow(10, xNormMin);
        }
        if (xNormMax != null) {
            bounds.xMax = Math.pow(10, xNormMax);
        }
        if (yNormMin != null) {
            bounds.yMin =yNormMin;
        }
        if (yNormMax != null) {
            bounds.yMax = yNormMax;
        }

        return bounds;
    }

    public static JFreeChart createDoseCurve(Drc drc, Double xNormMin, Double xNormMax, Double yNormMin, Double yNormMax) {
        Color[] colors = new Color[]{Color.BLUE, Color.GREEN, Color.MAGENTA, Color.ORANGE, Color.PINK, Color.RED};
        Map<String, Drc> curves = new HashMap<String, Drc>();
        List<Drc> drcs = new ArrayList<Drc>();
        int colorIndex = 0;


        if (drc == null) {
            return null;
        }
        if (colorIndex >= colors.length) {
            colorIndex = 0;
        }
        drc.setColor(colors[colorIndex]);
        curves.put(colorIndex + ":" + drc.getCurveParameters().resultTime.toString(), drc);
        drcs.add(drc);
        //colorIndex++;

        Bounds bounds ;
        if (xNormMin != null && xNormMax != null && yNormMin != null && yNormMax != null) {
            bounds = new Bounds(Math.pow(10, xNormMin), Math.pow(10, xNormMax), yNormMin, yNormMax);
        } else {
            bounds = adjustBounds(drcs, xNormMin, xNormMax, yNormMin, yNormMax);
        }
        return DoseCurveImage.createChart(curves, bounds, Color.BLACK);
    }
}
