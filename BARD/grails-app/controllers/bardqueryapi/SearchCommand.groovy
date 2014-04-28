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

import bard.core.rest.spring.util.Facet
import org.apache.commons.collections.FactoryUtils
import org.apache.commons.collections.ListUtils

/**
 * Command object used to encapsulate all the search parameters coming in from the client.
 */
@grails.validation.Validateable
class SearchCommand {
    String searchString
    String formName
    String offset
    String max
    String nhits //the number of hits if we already have it

    /**
     * {@link SearchFilter}'s
     */
    List<SearchFilter> filters = ListUtils.lazyList([], FactoryUtils.instantiateFactory(SearchFilter))

    /**
     * Return all of the filters that have been currently applied by client
     * @return {@link SearchFilter}'s
     */
    List<SearchFilter> getAppliedFilters() {
        List<SearchFilter> appliedFilters = filters.findAll { SearchFilter filter -> filter.filterValue }
        //Get back the original facet names as were received from the REST API, before translating them to a more friendly filter names.
        //We need back the original names since these are the filter names and values the REST API is expecting to get in its search resource.
        //Use a reverse-mapping to obtain the translation back from filter the facet.
        for (SearchFilter filter in appliedFilters) {
            filter.filterName = Facet.VALUE_TO_FACET_TRANSLATION_MAP[filter.filterName] ?: filter.filterName
        }

        return appliedFilters
    }

    void setSearchString(String searchString) {
        this.searchString = searchString?.trim()
    }

    static constraints = {
        searchString(blank: false)
    }
}

/**
 * The different kinds of facets used in this project
 */
enum FacetFormType {
    AssayFacetForm,
    CompoundFacetForm,
    ProjectFacetForm,
    ExperimentFacetForm,
    CompoundBioActivitySummaryForm
}
