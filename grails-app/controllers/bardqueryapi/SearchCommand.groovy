package bardqueryapi

import org.apache.commons.collections.FactoryUtils
import org.apache.commons.collections.ListUtils

/**
 * Command object used to parse all the search parameters coming in from the client.
 */
@grails.validation.Validateable
class SearchCommand {
    String searchString
    String formName
    String offset
    String max

    List<SearchFilter> filters = ListUtils.lazyList([], FactoryUtils.instantiateFactory(SearchFilter))

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

enum FacetFormType {
    AssayFacetForm,
    CompoundFacetForm,
    ProjectFacetForm
}