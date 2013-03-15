package bardqueryapi

import bard.core.rest.spring.experiment.ActivityConcentrationMap
import bard.core.rest.spring.experiment.CurveFitParameters

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

    ActivityConcentrationMap value
    String title
    CurveFitParameters curveFitParameters
    Double slope
    String responseUnit
    String testConcentrationUnit
}
