package bardqueryapi

import org.apache.commons.lang.builder.EqualsBuilder
import org.apache.commons.lang.builder.HashCodeBuilder
import org.apache.commons.lang3.builder.CompareToBuilder

/**
 * Created with IntelliJ IDEA.
 * User: jasiedu
 * Date: 8/31/12
 * Time: 6:02 PM
 * To change this template use File | Settings | File Templates.
 */
class SearchFilter implements Comparable<SearchFilter> {
    String filterName
    String filterValue

    public SearchFilter() {

    }

    public SearchFilter(final String filterName, final String filterValue) {
        this.filterName = filterName
        this.filterValue = filterValue
    }

    public int hashCode() {
        return new HashCodeBuilder(17, 31). // two randomly chosen prime numbers
                append(this.filterName).
                append(this.filterValue).
                toHashCode();
    }

    //Note: in Groovy, when compareTo() is implemented, .equals() would use compareTo to test for equality.
    int compareTo(SearchFilter thatSearchFilter) {
        return new CompareToBuilder()
                .append(this.filterName, thatSearchFilter.filterName)
                .append(this.filterValue, thatSearchFilter.filterValue)
                .toComparison()
    }

    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj.getClass() != getClass()) {
            return false;
        }

        SearchFilter rhs = (SearchFilter) obj;
        return new EqualsBuilder().
                append(this.filterName, rhs.filterName).
                append(this.filterValue, rhs.filterValue).
                isEquals();
    }
}
