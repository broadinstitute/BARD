package bardqueryapi

import bard.core.rest.spring.experiment.ConcentrationResponseSeries
import org.apache.commons.lang3.tuple.Pair

/**
 * Created with IntelliJ IDEA.
 * User: jasiedu
 * Date: 2/8/13
 * Time: 8:02 PM
 * To change this template use File | Settings | File Templates.
 */
class ExperimentDescriptorsValue implements WebQueryValue {

    public String toString(){
        return this.getClass().getName()
    }
    List<Pair<String, String>> descriptor

}
