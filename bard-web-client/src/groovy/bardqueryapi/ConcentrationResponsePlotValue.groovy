package bardqueryapi

import bard.core.rest.spring.experiment.ActivityConcentrationMap

/**
 * Created with IntelliJ IDEA.
 * User: jasiedu
 * Date: 2/8/13
 * Time: 8:02 PM
 * To change this template use File | Settings | File Templates.
 */
class ConcentrationResponsePlotValue implements WebQueryValue {

    public String toString(){
        return this.getClass().getName()
    }
    List<ActivityConcentrationMap> activityConcentrationMaps

}
