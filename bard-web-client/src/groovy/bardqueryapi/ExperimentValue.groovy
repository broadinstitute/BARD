package bardqueryapi

import bard.core.rest.spring.experiment.ExperimentShow

/**
 * Created with IntelliJ IDEA.
 * User: jasiedu
 * Date: 2/8/13
 * Time: 8:02 PM
 * To change this template use File | Settings | File Templates.
 */
/**
 * Represents an experiment box in the Compound Bio Activity Summary panel. Each 'box' includes
 *  an experiment summary title and then a list of WebQueryValue result types (curves, single-points, etc.)
 */
class ExperimentValue implements WebQueryValue {

    public String toString(){
        return this.value
    }

    ExperimentShow value
}
