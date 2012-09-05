package bardqueryapi

import java.util.regex.Matcher
import java.util.regex.Pattern

/**
 * Command object used to parse all the search parameters coming in from the client.
 */
@grails.validation.Validateable
class SearchCommand {
    String searchString
    List<SearchFilter> filters

    List<SearchFilter> getAppliedFilters() {
        Pattern pattern = ~/\w([\s]+):['"]?(.+)['"]?/
        List<String> appliedFilterStrings = filters.collect { MapEntry it -> if (it.value == "on") return it.key }
        appliedFilterStrings.collectMany {
            Matcher m = pattern.matcher(it)
            return m.collect {
                return new SearchFilter(it[1],it[2])
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