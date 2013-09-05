package curverendering

import org.jfree.chart.axis.NumberAxis
import org.jfree.chart.axis.ValueAxis
import org.jfree.chart.plot.PlotOrientation
import org.jfree.ui.RectangleEdge
import spock.lang.Specification
import spock.lang.Unroll

import java.awt.Color
import java.awt.Rectangle
import java.awt.geom.Rectangle2D

@Unroll
class ConfidenceBoundAnnotationUnitSpec extends Specification {
    void setup() {

    }

    void tearDown() {
        // Tear down logic here
    }

    void "test compute2DEdges With Vertical Orientation"() {
        given: " A ConfidencBoundAnnotation Object"
        ConfidenceBoundAnnotation confidenceBoundAnnotation =
            new ConfidenceBoundAnnotation(0.3f, 0.5f, 0.2f, Color.BLACK)
        RectangleEdge domainEdge = RectangleEdge.RIGHT
        RectangleEdge rangeEdge = RectangleEdge.TOP
        Rectangle2D dataArea = new Rectangle(2, 2, 2, 2)
        ValueAxis domainAxis = new NumberAxis()
        ValueAxis rangeAxis = new NumberAxis()

        when: "We call the expandToContain() method on the given Boundss object"
        Map<String, Float> map = confidenceBoundAnnotation.compute2DEdges(PlotOrientation.VERTICAL, domainEdge, rangeEdge, dataArea, domainAxis, rangeAxis)
        then: "The expected to get back the expected warning level"
        assert map
        assert map.j2DX1
        assert map.j2DX2
        assert map.j2DY1
    }

    void "test compute2DEdges With Horizontal Orientation"() {
        given: " A ConfidencBoundAnnotation Object"
        ConfidenceBoundAnnotation confidenceBoundAnnotation =
            new ConfidenceBoundAnnotation(0.3f, 0.5f, 0.2f, Color.BLACK)
        RectangleEdge domainEdge = RectangleEdge.RIGHT
        RectangleEdge rangeEdge = RectangleEdge.TOP
        Rectangle2D dataArea = new Rectangle(2, 2, 2, 2)
        ValueAxis domainAxis = new NumberAxis()
        ValueAxis rangeAxis = new NumberAxis()

        when: "We call the expandToContain() method on the given Boundss object"
        confidenceBoundAnnotation.compute2DEdges(PlotOrientation.HORIZONTAL, domainEdge, rangeEdge, dataArea, domainAxis, rangeAxis)
        then: "The expected to get back the expected warning level"
        Exception ee = thrown()
        assert ee.message == "Orientation PlotOrientation.HORIZONTAL, Not Yet Implemented"
    }

}

