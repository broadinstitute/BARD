package bardqueryapi

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

    /**
     * {@link SearchFilter}'s
     */
    List<SearchFilter> filters = ListUtils.lazyList([], FactoryUtils.instantiateFactory(SearchFilter))

    /**
     * Return all of teh filters that have been currently applied by client
     * @return {@link SearchFilter}'s
     */
    List<SearchFilter> getAppliedFilters() {
        filters.findAll {SearchFilter filter ->  filter.filterValue}
    }

    void setSearchString (String searchString) {
        this.searchString =  searchString?.trim()
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
    ProjectFacetForm
}