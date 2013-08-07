package bard.db.context.item

import bard.db.registration.AssayContext
import bard.db.registration.AssayContextItem

/**
 * Created with IntelliJ IDEA.
 * User: jasiedu
 * Date: 8/3/13
 * Time: 6:05 AM
 * To change this template use File | Settings | File Templates.
 */
import org.apache.commons.lang3.builder.CompareToBuilder
import org.apache.commons.lang3.builder.EqualsBuilder
import org.apache.commons.lang3.builder.HashCodeBuilder

/**
 * Created with IntelliJ IDEA.
 * User: jasiedu
 * Date: 8/3/13
 * Time: 5:42 AM
 * To change this template use File | Settings | File Templates.
 */
class ContextDTO implements Comparable<ContextDTO> {


    final String contextName;
    final Long contextId

    public ContextDTO(final String contextName, final Long contextId) {
        this.contextName = contextName;
        this.contextId = contextId;
    }

    public ContextDTO(AssayContext assayContext) {
        this.contextName = assayContext.preferredName;
        this.contextId = assayContext.id
    }


    @Override
    public int compareTo(ContextDTO thatContextDTO) {
        return new CompareToBuilder()
                .append(this.contextName, thatContextDTO.contextName)
                .toComparison();
    }

    /**
     * Define equality of state.
     */
    @Override
    public boolean equals(Object aThat) {
        if (aThat == null) {
            return false;
        }
        if (this == aThat) {
            return true;
        }
        if (!(aThat instanceof ContextDTO)) return false;

        ContextDTO that = (ContextDTO) aThat;
        return new EqualsBuilder().
                append(this.contextName, that.contextName).
                isEquals();
    }

    /**
     * A class that overrides equals must also override hashCode.
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).
                append(this.contextName).
                toHashCode();
    }
}
