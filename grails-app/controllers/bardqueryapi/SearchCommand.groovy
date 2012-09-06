package bardqueryapi

import org.apache.commons.collections.FactoryUtils
import org.apache.commons.collections.ListUtils

/**
 * Command object used to parse all the search parameters coming in from the client.
 */
@grails.validation.Validateable
class SearchCommand {
    String searchString
    List<SearchFilter> filters = ListUtils.lazyList([], FactoryUtils.instantiateFactory(SearchFilter))

    List<SearchFilter> getAppliedFilters() {
        return filters.collect() { SearchFilter filter ->
            if (filter.filterValue) {
                return filter
            }
        }
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