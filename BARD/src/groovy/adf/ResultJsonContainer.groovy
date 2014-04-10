package adf

import bard.db.experiment.JsonResult
import bard.db.experiment.JsonResultContextItem
import org.apache.commons.lang3.StringUtils

/**
 * Created by ddurkin on 3/26/14.
 *
 * composition obj to contain either a JsonResult or JsonResultContextItem but not both
 */

class ResultJsonContainer {

    private final JsonResult jsonResult
    private final JsonResultContextItem jsonResultContextItem

    ResultJsonContainer(JsonResult jr){
        this.jsonResult = jr
    }

    ResultJsonContainer(JsonResultContextItem jrci){
        this.jsonResultContextItem = jrci
    }

    String getValueDisplay(){
        final String valueDisplay = jsonResult ? jsonResult.valueDisplay : jsonResultContextItem.valueDisplay
        StringUtils.trimToEmpty(valueDisplay)
    }
}
