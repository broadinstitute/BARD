package curverendering

import org.apache.commons.lang3.tuple.MutablePair
import org.jfree.chart.axis.NumberAxis
import spock.lang.Specification
import spock.lang.Unroll

import java.awt.Color
import org.jfree.chart.JFreeChart
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer
import org.jfree.data.xy.DefaultXYDataset
import org.jfree.chart.plot.XYPlot

@Unroll
class DoseCurveImageUnitSpec extends Specification {
    void setup() {
        // DoseCurveImage
    }

    void tearDown() {
        // Tear down logic here
    }
    /**
     * {@link DoseCurveImage#createAndConfigureYAxis(Bounds, Color, String)}
     */
    void "test Create and Configure Y-Axis"() {
        given:
        Color axisColor = Color.BLACK

        when:
        final NumberAxis yAxis = DoseCurveImage.createAndConfigureYAxis(null, axisColor, "Y")
        then:
        assert yAxis
        assert yAxis.autoRangeIncludesZero
        assert yAxis.axisLinePaint == axisColor
        assert yAxis.labelPaint == axisColor
        assert yAxis.tickLabelPaint == axisColor
        assert yAxis.label == "Y"
    }
    /**
     * {@link DoseCurveImage#createAndConfigureXAxis(Bounds, Color, String)}
     */
    void "test Create and Configure X-Axis"() {
        given:
        Color axisColor = Color.BLACK

        when:
        final NumberAxis xAxis = DoseCurveImage.createAndConfigureXAxis(null, axisColor, "X")
        then:
        assert xAxis
        assert !xAxis.autoRangeIncludesZero
        assert xAxis.axisLinePaint == axisColor
        assert xAxis.labelPaint == axisColor
        assert xAxis.tickLabelPaint == axisColor
        assert xAxis.label == "X"
    }
    /**
     * {@link DoseCurveImage#aggregateValidAndInvalidPoints}
     */
    void "test aggregate valid and invalid X"() {
        given:
        //The first value in the list is true, the second is false
        List<Boolean> isValid = [Boolean.TRUE, Boolean.FALSE]
        List<Double> x = [new Double(1000), new Double(2000)]
        List<Double> y = [new Double(1000), new Double(2000)]

        //set up the valid and invalid arrays
        def validX = new double[x.size()];
        def validY = new double[x.size()];
        def invalidX = new double[y.size()];
        def invalidY = new double[y.size()];

        when:
        DoseCurveImage.aggregateValidAndInvalidPoints(isValid, x, y, validX as double[], validY as double[], invalidX as double[], invalidY as double[])
        then: "We expect that the first value in each array has a number greater than zero, while the second value must equal zero"
        //assert that the first values are greater than zero
        assert invalidY[0] > 0
        assert invalidX[0] > 0
        assert validY[0] > 0
        assert validX[0] > 0

        //assert that the second value in each array is zero
        assert invalidY[1] == 0
        assert invalidX[1] == 0
        assert validY[1] == 0
        assert validX[1] == 0
    }
    /**
     *
     * {@link DoseCurveImage#findWhereToStartAndStopOnXAxis}
     *
     */
    void "test find Where To Start And Stop On X-Axis"() {
        when:
        final MutablePair<Double, Double> axis = DoseCurveImage.findWhereToStartAndStopOnXAxis(validX as double[])
        then:
        assert axis
        axis.left == expectedLeftValue
        axis.right == expectedRightValue

        where:
        label                              | validX         | expectedLeftValue | expectedRightValue
        "Smallest value at first position" | [10, 20, 30]   | new Double(10)    | new Double(30)
        "Smallest value at last position"  | [10, -20, -30] | new Double(-30)   | new Double(10)
    }
    /**
     * {@link DoseCurveImage}
     */
    void "test adjust Bounds no concentration or activities"() {
        given: "That the Drc object has no concentration or activities"
        List<Double> concentrations = []
        List<Double> activities = []
        List<Boolean> isValid = []
        CurveParameters curveParameters = null
        Color color = Color.BLACK
        Drc drc = new Drc(concentrations, activities, isValid, curveParameters, color)
        when: "We call the adjustBounds methods"
        final Bounds bounds = DoseCurveImage.adjustBounds(drc, null, null, null, null, null)
        then: "We expect a bounds value of null"
        assert !bounds
    }
    /**
     * {@link DoseCurveImage}
     */
    void "test adjust Bounds"() {
        given:
        List<Double> concentrations = [new Double(4), new Double(8)]
        List<Double> activities = [new Double(4), new Double(8)]
        List<Boolean> isValid = [Boolean.TRUE, Boolean.TRUE]
        CurveParameters curveParameters = null
        Color color = Color.BLACK
        Bounds originalBounds = new Bounds(0, 1, 0, 1)
        Drc drc = new Drc(concentrations, activities, isValid, curveParameters, color)
        when: "We call the adjustBounds methods"
        final Bounds returnedBounds = DoseCurveImage.adjustBounds(drc, originalBounds, 1, 2, 1, 2)
        then:
        assert returnedBounds
        assert returnedBounds.xMax == originalBounds.xMax
        assert returnedBounds.yMax == originalBounds.yMax

        assert returnedBounds.xMin == originalBounds.xMin
        assert returnedBounds.yMin == originalBounds.yMin


    }
    /**
     * {@link DoseCurveImage}
     */
    void "test adjust Bounds With Empty List of Drcs"() {
        given:
        List<Drc> drcs = []
        when: "We call the adjustBounds methods"
        final Bounds returnedBounds = DoseCurveImage.adjustBounds(drcs, 1, 2, 1, 2)
        then: "We expect to get back null"
        assert !returnedBounds
    }
    /**
     * {@link DoseCurveImage}
     */
    void "test set Bound Parameters"() {
        given:
        Bounds bounds = new Bounds(0, 0, 0, 0)
        when:
        DoseCurveImage.setBoundParameters(bounds, 1, 2, 1, 2)
        then:
        assert bounds.xMax == 100
        assert bounds.yMax == 2
        assert bounds.xMin == 10
        assert bounds.yMin == 1
    }

    void "test addFittedCurve"() {
        given:
        String name = null
        DefaultXYDataset dataset = null
        XYLineAndShapeRenderer renderer = null

        CurveParameters curveParameters = null
        Color curveColor = null
        double[] validX = null
        when:
        DoseCurveImage.addFittedCurve(name, dataset, renderer, curveParameters, curveColor, validX)
        then:
        assert renderer == null

    }

    void "test addConfidenceBounds"() {
        given:
        CurveParameters curveParameters=null
        Color curveColor = Color.ORANGE
        XYPlot plot = new XYPlot()
        when:
        DoseCurveImage.addConfidenceBounds(curveParameters, curveColor, plot)
        then:
        assert !plot.annotations

    }

    void "test createDoseCurve"() {
        given:
        final Double slope = new Double(2)
        final Double HILL_SLOPE = new Double(2)
        final Double S0 = new Double(2)
        final Double SINF = new Double(2)
        final Double lower95CL = new Double(2)
        final Double upper95CL = new Double(2)
        final Date resultTime = new Date()
        List<Double> concentrations = [new Double(2), new Double(4)]
        List<Double> activities = [new Double(2), new Double(4)]
        List<Boolean> isValid = [true, true];
        CurveParameters curveParameters = new CurveParameters(slope, resultTime, HILL_SLOPE, S0, SINF, lower95CL, upper95CL)
        Color color = Color.BLACK;


        Drc drc = new Drc(concentrations, activities, isValid, curveParameters, color)
        String xAxisLabel = "X"
        String yAxisLabel = "Y"
        Double xNormMin = new Double(2)
        Double xNormMax = new Double(2)
        Double yNormMin = new Double(2)
        Double yNormMax = new Double(2)
        when:
        JFreeChart jFreeChart = DoseCurveImage.createDoseCurve(drc, xAxisLabel, yAxisLabel, xNormMin, xNormMax, yNormMin, yNormMax)
        then:
        assert jFreeChart
    }

    void "test createDoseCurves with empty drc list"() {
        given:
        String xAxisLabel = "X"
        String yAxisLabel = "Y"
        Double xNormMin = new Double(2)
        Double xNormMax = new Double(2)
        Double yNormMin = new Double(2)
        Double yNormMax = new Double(2)
        when:
        JFreeChart jFreeChart = DoseCurveImage.createDoseCurves([], xAxisLabel, yAxisLabel, xNormMin, xNormMax, yNormMin, yNormMax)
        then:
        assert !jFreeChart
    }

    void "test createDoseCurves"() {
        given:
        final Double slope = new Double(2)
        final Double HILL_SLOPE = new Double(2)
        final Double S0 = new Double(2)
        final Double SINF = new Double(2)
        final Double lower95CL = new Double(2)
        final Double upper95CL = new Double(2)
        final Date resultTime = new Date()
        List<Double> concentrations = [new Double(2), new Double(4)]
        List<Double> activities = [new Double(2), new Double(4)]
        List<Boolean> isValid = [true, true];
        CurveParameters curveParameters = new CurveParameters(slope, resultTime, HILL_SLOPE, S0, SINF, lower95CL, upper95CL)
        Color color = Color.BLACK;


        Drc drc = new Drc(concentrations, activities, isValid, curveParameters, color)
        String xAxisLabel = "X"
        String yAxisLabel = "Y"
        Double xNormMin = new Double(2)
        Double xNormMax = new Double(2)
        Double yNormMin = new Double(2)
        Double yNormMax = new Double(2)
        when:
        JFreeChart jFreeChart = DoseCurveImage.createDoseCurves([drc], xAxisLabel, yAxisLabel, xNormMin, xNormMax, yNormMin, yNormMax)
        then:
        assert jFreeChart
    }
    /**
     * {@link DoseCurveImage}
     */
    void "test create Dose Curve with null DRC"() {
        when: "We call the createDoseCurve method with a null Drc object"
        final JFreeChart curve = DoseCurveImage.createDoseCurve(null, null, null, null, null, null, null)
        then: "We expect a null curve back"
        assert !curve
    }
    /**
     * {@link DoseCurveImage}
     */
    void "test find Bounds with non-null Normalized values"() {
        when:
        final Bounds bounds = DoseCurveImage.findBounds([], 1, 2, 1, 2)
        then: "We expect a null curve back"
        assert bounds
        assert bounds.xMax == 100
        assert bounds.yMax == 2
        assert bounds.xMin == 10
        assert bounds.yMin == 1
    }
}

