package bardqueryapi

import org.apache.commons.lang.builder.HashCodeBuilder
import org.apache.commons.lang.builder.EqualsBuilder

/**
 * Created with IntelliJ IDEA.
 * User: jasiedu
 * Date: 8/31/12
 * Time: 6:02 PM
 * To change this template use File | Settings | File Templates.
 */
class SearchFilter {
    final String filterName
    final String filterValue

    public SearchFilter(final String filterName, final String filterValue) {
        this.filterName = filterName
        this.filterValue = filterValue
    }
    public int hashCode() {
        return new HashCodeBuilder(17, 31). // two randomly chosen prime numbers
        // if deriving: appendSuper(super.hashCode()).
                append(this.filterName).
                append(this.filterValue).
                toHashCode();
    }

    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (obj == this)
            return true;
        if (obj.getClass() != getClass())
            return false;

        SearchFilter rhs = (SearchFilter) obj;
        return new EqualsBuilder().
        // if deriving: appendSuper(super.equals(obj)).
                append(this.filterName, rhs.filterName).
                append(this.filterValue, rhs.filterValue).
                isEquals();
    }

}
