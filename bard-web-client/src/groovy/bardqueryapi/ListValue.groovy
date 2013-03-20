package bardqueryapi

import bard.core.rest.spring.experiment.ExperimentShow

/**
 * Created with IntelliJ IDEA.
 * User: jasiedu
 * Date: 2/8/13
 * Time: 8:02 PM
 * To change this template use File | Settings | File Templates.
 */
class ListValue implements WebQueryValue {

    public String toString(){
        return this.value
    }

    List<WebQueryValue> value = []
}
