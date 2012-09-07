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
    List<SearchFilter> filters = ListUtils.lazyList([], FactoryUtils.instantiateFactory(SearchFilter))

    List<SearchFilter> getAppliedFilters() {
        filters.findAll {SearchFilter filter ->  filter.filterValue}
    }


    static constraints = {
        searchString(blank: false)
        formName(blank: false)
    }
}

enum FacetFormType {
    AssayFacetForm,
    CompoundFacetForm,
    ProjectFacetForm
}