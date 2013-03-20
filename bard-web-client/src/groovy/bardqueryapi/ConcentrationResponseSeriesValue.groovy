package bardqueryapi

import bard.core.rest.spring.experiment.ActivityConcentrationMap
import bard.core.rest.spring.experiment.CurveFitParameters

/**
 * Represents dose-response data together with supplementary information about the fitted-curve, if such exists.
 *
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
    CurveFitParameters curveFitParameters
    Double slope
    String responseUnit
    String testConcentrationUnit
    Double yNormMin
    Double yNormMax
    String yAxisLabel
    String xAxisLabel
    PairValue title
}
