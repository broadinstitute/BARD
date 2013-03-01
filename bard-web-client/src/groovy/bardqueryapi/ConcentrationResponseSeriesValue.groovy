package bardqueryapi

import bardqueryapi.WebQueryValue
import org.apache.commons.lang3.tuple.Pair
import bard.core.rest.spring.experiment.ActivityConcentrationMap

/**
 * Created with IntelliJ IDEA.
 * User: jasiedu
 * Date: 2/8/13
 * Time: 8:02 PM
 * To change this template use File | Settings | File Templates.
 */
class ConcentrationResponseSeriesValue implements WebQueryValue {

    public String toString(){
        return this.getClass().getName()
    }
    List<ActivityConcentrationMap> activityConcentrationMaps

}
