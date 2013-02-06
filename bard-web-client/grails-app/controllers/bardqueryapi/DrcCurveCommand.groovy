package bardqueryapi

import org.apache.commons.collections.Factory
import org.apache.commons.collections.ListUtils
import curverendering.Curve

/**
 * Command object used to parse all the search parameters coming in from the client.
 */
@grails.validation.Validateable
class DrcCurveCommand {
    String xAxisLabel
    String yAxisLabel
    Double width
    Double height
    Double s0
    Double sinf
    Double slope
    Double hillSlope
    Double yNormMin
    Double yNormMax
    List<Double> concentrations = ListUtils.lazyList([], new ListUtilsFactory())
    List<Double> activities = ListUtils.lazyList([], new ListUtilsFactory())
    List<Curve> curves = ListUtils.lazyList([], new ListCurveFactory())
}
class ListUtilsFactory implements Factory {
    public Object create() {
        return new Double(0);
    }
}
class ListCurveFactory implements Factory {
    public Object create() {
        return new Curve();
    }
}

