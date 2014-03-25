import bard.db.experiment.JsonResult
import groovy.transform.EqualsAndHashCode
import org.apache.commons.lang3.builder.EqualsBuilder
import org.apache.commons.lang3.builder.HashCodeBuilder

/**
 * Created by ddurkin on 3/20/14.
 */

class ResultSetPipelinePath {

    Long resultTypeId;
    Long statsModifierId;
    String resultType;
    String relationship;
    ResultSetPipelinePath parent

    ResultSetPipelinePath(JsonResult jr, parent) {
        this.resultTypeId = jr.resultTypeId
        this.statsModifierId = jr.statsModifierId;
        this.resultType = jr.resultType;
        this.relationship = jr.relationship;
        this.parent = parent
    }

    List<ResultSetPipelinePath> getPath() {
        if (this.parent == null) {
            return [this]
        } else {
            def path = parent.getPath()
            path.add(this)
            return path
        }
    }

    @Override
    int hashCode() {
        HashCodeBuilder builder = new HashCodeBuilder()
        builder.append(resultTypeId)
        builder.append(statsModifierId)
        builder.append(parent)
        return builder.toHashCode()
    }

    @Override
    boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (this.is(obj)) {
            return true;
        }
        if (obj.getClass() != getClass()) {
            return false;
        }
        ResultSetPipelinePath rhs = (ResultSetPipelinePath) obj;
        EqualsBuilder builder = new EqualsBuilder()
        builder.append(this.resultTypeId, rhs.resultTypeId)
        builder.append(this.statsModifierId, rhs.statsModifierId)
        builder.append(this.parent, rhs.parent)
        builder.isEquals()
    }

    String toString() {
        [resultType].findAll().join(' ')
    }
}
