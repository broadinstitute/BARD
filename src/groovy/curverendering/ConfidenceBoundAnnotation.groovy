package curverendering;


import org.jfree.chart.annotations.AbstractXYAnnotation
import org.jfree.chart.axis.ValueAxis
import org.jfree.chart.plot.Plot
import org.jfree.chart.plot.PlotOrientation
import org.jfree.chart.plot.PlotRenderingInfo
import org.jfree.chart.plot.XYPlot
import org.jfree.ui.RectangleEdge

import java.awt.BasicStroke
import java.awt.Color
import java.awt.Graphics2D
import java.awt.geom.GeneralPath
import java.awt.geom.Rectangle2D

/**
 * Created with IntelliJ IDEA.
 * User: jasiedu
 * Date: 9/19/12
 * Time: 12:29 PM
 * To change this template use File | Settings | File Templates.
 */
class ConfidenceBoundAnnotation extends AbstractXYAnnotation {

    float confidenceX1
    float confidenceX2
    float confidenceY;
    float confidenceAnnotationHeight = 10;
    Color color;

    public ConfidenceBoundAnnotation(float confidenceX1, float confidenceX2, float confidenceY, Color color) {
        this.confidenceX1 = confidenceX1;
        this.confidenceX2 = confidenceX2;
        this.confidenceY = confidenceY;
        this.color = color;
    }

    @Override
    public void draw(Graphics2D gd, XYPlot plot, Rectangle2D dataArea, ValueAxis domainAxis, ValueAxis rangeAxis, int i, PlotRenderingInfo info) {

        PlotOrientation orientation = plot.getOrientation();
        RectangleEdge domainEdge = Plot.resolveDomainAxisLocation(
                plot.getDomainAxisLocation(), orientation);

        RectangleEdge rangeEdge = Plot.resolveRangeAxisLocation(
                plot.getRangeAxisLocation(), orientation);

        float j2DX1 = 0.0f;
        float j2DX2 = 0.0f;
        float j2DY1 = 0.0f;
        if (orientation == PlotOrientation.VERTICAL) {
            j2DX1 = (float) domainAxis.valueToJava2D(
                    confidenceX1, dataArea, domainEdge);
            j2DX2 = (float) domainAxis.valueToJava2D(
                    confidenceX2, dataArea, domainEdge);
            j2DY1 = (float) rangeAxis.valueToJava2D(
                    confidenceY, dataArea, rangeEdge);
        } else if (orientation == PlotOrientation.HORIZONTAL) {
            throw new RuntimeException("unimplemented");
        }
        gd.setPaint(this.color);
        gd.setStroke(new BasicStroke());

        GeneralPath bar = new GeneralPath();
        // the horizontal line
        bar.moveTo(j2DX1, j2DY1);
        bar.lineTo(j2DX2, j2DY1);
        // the left vertical line
        bar.moveTo(j2DX1, j2DY1 - confidenceAnnotationHeight / 2);
        bar.lineTo(j2DX1, j2DY1 + confidenceAnnotationHeight / 2);
        // the right vertical line
        bar.moveTo(j2DX2, j2DY1 - confidenceAnnotationHeight / 2);
        bar.lineTo(j2DX2, j2DY1 + confidenceAnnotationHeight / 2);

        gd.draw(bar);
    }
}
