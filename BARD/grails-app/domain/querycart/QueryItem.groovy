package querycart

import com.metasieve.shoppingcart.Shoppable
import org.apache.commons.lang.builder.EqualsBuilder
import org.apache.commons.lang.builder.HashCodeBuilder
import org.apache.commons.lang.StringUtils

class QueryItem extends Shoppable {

    static final int MAXIMUM_NAME_FIELD_LENGTH = 4000

    QueryItemType queryItemType
    Long internalId
    Long externalId
    String name

    QueryItem() {

    }

    QueryItem(String name, Long internalId, Long externalId, QueryItemType queryItemType) {
        this.name = name
        this.externalId = externalId
        this.internalId = internalId
        this.queryItemType = queryItemType
    }

   /**
     * Catch the beforeValidate event and apply pre-processing to the fields
     */
    void beforeValidate() {
        this.name = StringUtils.abbreviate(name?.trim(), MAXIMUM_NAME_FIELD_LENGTH)
    }

    static constraints = {
        queryItemType nullable: false
        internalId nullable: true, min: 1L
        externalId nullable: false, min: 1L, unique: 'queryItemType'
        name nullable: false, blank: false, maxSize: MAXIMUM_NAME_FIELD_LENGTH
    }

    @Override
    String toString() {
        if (this.name == null || this.name == 'null') {
            return ""
        }
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