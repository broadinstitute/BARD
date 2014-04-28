/* Copyright (c) 2014, The Broad Institute
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
    * Redistributions of source code must retain the above copyright
      notice, this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright
      notice, this list of conditions and the following disclaimer in the
      documentation and/or other materials provided with the distribution.
    * Neither the name of The Broad Institute nor the
      names of its contributors may be used to endorse or promote products
      derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL The Broad Institute BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

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

