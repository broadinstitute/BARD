package adf.exp

import bard.db.experiment.JsonResult
import bard.db.experiment.JsonResultContextItem

/**
 * Created with IntelliJ IDEA.
 * User: pmontgom
 * Date: 4/8/14
 * Time: 1:51 PM
 * To change this template use File | Settings | File Templates.
 */
class ResultKey {
    Long resultTypeId;
    Long statsModifierId;
    String resultType;

    public ResultKey(JsonResult jr) {
        this.resultType = jr.resultType
        this.resultTypeId = jr.resultTypeId;
        this.statsModifierId = jr.statsModifierId;
    }

    public ResultKey(JsonResultContextItem ci) {
        this.resultType = ci.attribute
    }

    boolean equals(o) {
        if (this.is(o)) return true
        if (!(o instanceof ResultKey)) return false

        ResultKey resultKey = (ResultKey) o

        if (resultTypeId != resultKey.resultTypeId) return false
        if (statsModifierId != resultKey.statsModifierId) return false

        return true
    }

    int hashCode() {
        int result
        result = (resultTypeId != null ? resultTypeId.hashCode() : 0)
        result = 31 * result + (statsModifierId != null ? statsModifierId.hashCode() : 0)
        return result
    }

    public String toString() {
        return resultType
        // findAll stripping out any nulls
        //return [resultType, statsModifierId].findAll().join(' ')
    }
}
