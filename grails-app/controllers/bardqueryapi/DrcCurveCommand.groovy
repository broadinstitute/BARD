package bardqueryapi

import org.apache.commons.collections.ListUtils
import org.apache.commons.collections.Factory
/**
 * Command object used to parse all the search parameters coming in from the client.
 */
@grails.validation.Validateable
class DrcCurveCommand {
    Double s0
    Double sinf
    Double ac50
    Double hillSlope
    List<Double> concentrations = ListUtils.lazyList([], new ListUtilsFactory() )
    List<Double> activities = ListUtils.lazyList([],new ListUtilsFactory() )

    //TODO: Add  the needed validations here. Command object need to be valid to do request
//    static constraints = {
//        searchString(blank: false)
//    }
}
class ListUtilsFactory implements Factory {
    public Object create() {
        return new Double(0);
    }
}

