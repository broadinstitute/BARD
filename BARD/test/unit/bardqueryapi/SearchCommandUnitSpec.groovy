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

import grails.test.mixin.TestFor
import grails.test.mixin.TestMixin
import grails.test.mixin.support.GrailsUnitTestMixin
import spock.lang.Specification

/**
 * Created with IntelliJ IDEA.
 * User: sbrudz
 * Date: 9/2/12
 * Time: 10:35 PM
 * To change this template use File | Settings | File Templates.
 */
@TestMixin(GrailsUnitTestMixin)
@TestFor(BardWebInterfaceController)
class SearchCommandUnitSpec extends Specification {
    /**
     * {@link SearchCommand#setSearchString(String)}
     */
    void "test setSearchString"() {
        given:
        SearchCommand searchCommand = new SearchCommand()
        when:
        searchCommand.setSearchString(null)

        then:
        assert !searchCommand.searchString
        assert !searchCommand.validate()
        assert !searchCommand.appliedFilters
    }
    /**
     * {@link SearchCommand#getAppliedFilters()}
     */
    void "test getAppliedFilters"() {
        given:
        final String filterName = "a"
        final String filterValue = "b"
        final String searchString = "test"
        List<SearchFilter> searchFilters = [new SearchFilter(filterName: filterName, filterValue: filterValue),
                new SearchFilter(filterName: filterName, filterValue: "")]
        when:
        SearchCommand searchCommand = new SearchCommand(searchString: searchString, filters: searchFilters)

        then:
        assert searchCommand.appliedFilters.size() == 1
        assert searchCommand.searchString == searchString
        assert searchCommand.appliedFilters.get(0).filterName == filterName
        assert searchCommand.appliedFilters.get(0).filterValue == filterValue
        assert searchCommand.validate()
    }
}
