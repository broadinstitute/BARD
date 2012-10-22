package querycart

import com.metasieve.shoppingcart.Shoppable
import org.apache.commons.lang.builder.EqualsBuilder
import org.apache.commons.lang.builder.HashCodeBuilder
import org.apache.commons.lang.StringUtils

class QueryItem extends Shoppable {

    static final int MAXIMUM_NAME_FIELD_LENGTH = 4000

    QueryItemType queryItemType
    Long externalId
    String name

    /**
     * Catch the beforeValidate event and apply pre-processing to the fields
     */
    void beforeValidate() {
        this.name = StringUtils.abbreviate(name?.trim(), MAXIMUM_NAME_FIELD_LENGTH)
    }

    static constraints = {
        queryItemType nullable: false
        externalId nullable: false, min: 1L
        name nullable: false, blank: false, maxSize: MAXIMUM_NAME_FIELD_LENGTH
    }

    @Override
    String toString() {
        return name
    }

    /**
     *  equals
     * @param o
     * @return
     */
    boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj.getClass() != getClass()) {
            return false;
        }

        QueryItem that = (QueryItem) obj;
        return new EqualsBuilder().
                append(this.externalId, that.externalId).
                append(this.queryItemType, that.queryItemType).
                isEquals();
    }

    /**
     *  hashCode
     * @return
     */
    int hashCode() {
        return new HashCodeBuilder(17, 31). // two randomly chosen prime numbers
                append(this.externalId).
                append(this.queryItemType).
                toHashCode();
    }

}

enum QueryItemType {
    AssayDefinition,
    Compound,
    Project
}