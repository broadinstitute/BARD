package bardqueryapi

import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class SearchFilterUnitSpec extends Specification {
    @Shared SearchFilter filter1 = new SearchFilter(filterName: "AFN", filterValue: "AFV")
    @Shared SearchFilter filter2 = new SearchFilter(filterName: "BFN", filterValue: "BFV")

    void setup() {

    }

    void tearDown() {
        // Tear down logic here
    }

    /**
     * {@link SearchFilter#hashCode()}
     */
    void "searchFilter.hashCode #label"() {
        given: "A valid Search Filter Object"

        SearchFilter searchFilter =
            new SearchFilter(filterName: filterName, filterValue: filterValue)
        when: "We call the hashCode method"
        final int hashCode = searchFilter.hashCode()
        then: "The expected hashCode is returned"
        assert hashCode
        hashCode == expectedHashCode
        where:
        label                             | filterName   | filterValue   | expectedHashCode
        "With FilterName and FilterValue" | "filterName" | "filterValue" | -1804990745
        "With FilterName Only"            | "filterName" | null          | -906181106
        "With FilterValue Only"           | null         | "filterValue" | -898793302

    }
    /**
     * {@link SearchFilter#compareTo}
     */
    void "searchFilter.compareTo #label"() {
        when: "We call the compareTo method"
        final int compareToVal = searchFilter1.compareTo(searchFilter2)
        then: "We expect the method to return the expected value"
        assert compareToVal == expectedAnswer
        where:
        label                           | searchFilter1 | searchFilter2 | expectedAnswer
        "searchFilter1==searchFilter2"  | filter1       | filter1       | 0
        "searchFilter1 > searchFilter2" | filter2       | filter1       | 1
        "searchFilter1 < searchFilter2" | filter1       | filter2       | -1
    }

    /**
     * {@link SearchFilter#equals}
     */
    void "searchFilter.equals #label"() {
        when: "We call the equals method"
        final boolean returnedValue = searchFilter1.equals(searchFilter2)
        then: "We expected method to return the expected value"
        assert returnedValue == expectedAnswer
        where:
        label                      | searchFilter1 | searchFilter2 | expectedAnswer
        "this equals that"         | filter1       | filter1       | true
        "that is null"             | filter2       | null          | false
        "this != that"             | filter1       | filter2       | false
        "this.class != that.class" | filter1       | 2             | false
    }
}

