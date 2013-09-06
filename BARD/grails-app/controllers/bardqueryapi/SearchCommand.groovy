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