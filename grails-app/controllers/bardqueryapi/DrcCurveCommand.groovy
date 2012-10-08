package bardqueryapi

import org.apache.commons.collections.ListUtils
import org.apache.commons.collections.Factory
/**
 * Command object used to parse all the search parameters coming in from the client.
 */
@grails.validation.Validateable
class DrcCurveCommand {
    Double width
    Double height
    Double s0
    Double sinf
    Double ac50
    Double hillSlope
    List<Double> concentrations = ListUtils.lazyList([], new ListUtilsFactory() )
    List<Double> activities = ListUtils.lazyList([],new ListUtilsFactory() )
}
class ListUtilsFactory implements Factory {
    public Object create() {
        return new Double(0);
    }
}

