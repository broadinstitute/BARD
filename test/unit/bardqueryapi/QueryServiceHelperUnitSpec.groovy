package bardqueryapi

import bard.core.Assay
import bard.core.Compound
import bard.core.Project
import bard.core.SearchParams
import bard.core.adapter.AssayAdapter
import bard.core.adapter.CompoundAdapter
import bard.core.adapter.ProjectAdapter
import grails.test.mixin.TestFor
import spock.lang.Specification
import spock.lang.Unroll

/**
 * See the API for {@link grails.test.mixin.support.GrailsUnitTestMixin} for usage instructions
 */
@Unroll
@TestFor(QueryHelperService)
class QueryServiceHelperUnitSpec extends Specification {

    void setup() {
    }

    void tearDown() {
        // Tear down logic here
    }

    void "test find Filters In SearchBox #label"() {
        given: "A list of search filters"
        final List<SearchFilter> searchFilters = []
        when: "#label"
        service.findFiltersInSearchBox(searchFilters, searchString)

        then: "The resulting search filters size must equal the expected value"
        expectedSearchFilterSize == searchFilters.size()

        where:
        label                      | searchString       | filterName | expectedSearchFilterSize
        "String with no colon"     | "searchString"     | "search"   | 0
        "String with colon"        | "gobp_term:String" | "search"   | 1
        "String with colon at end" | "gobp_term:"       | "search"   | 0
        "Empty Search String"      | ""                 | "search"   | 0
        "Empty Filter Name"        | "search"           | ""         | 0
    }

    void "test get Auto Suggest Terms"() {
        when:
        List<Map<String, String>> autoCompleteResults = service.getAutoSuggestTerms(QueryHelperService.AUTO_SUGGEST_FILTERS, ["DNA Repair"], "gobp_term")

        then:
        assert autoCompleteResults


    }

    void "test Auto Complete #label"() {
        given: "map of possible filters"
        final Map<String, List<String>> autoSuggestResponseFromJDO = [gobp_term: ["Go Biological Process"]]
        when: "#label"
        List<Map<String, String>> autoCompleteResults = service.autoComplete(term, autoSuggestResponseFromJDO)

        then: "The expected map should match the constructed map"
        expectedResults == autoCompleteResults

        where:
        label                                          | currentAutoSuggestKey | term         | expectedResults
        "Term exist in filters Map"                    | "gobp_term"           | "DNA Repair" | [[label: "DNA Repair", value: "DNA Repair"], [label: "Go Biological Process as <strong>GO Biological Process Term</strong>", value: "gobp_term:\"Go Biological Process\""]]
        "Current Suggest Key is null"                  | ""                    | "gobp_term"  | [[label: "gobp_term", value: "gobp_term"], [label: "Go Biological Process as <strong>GO Biological Process Term</strong>", value: "gobp_term:\"Go Biological Process\""]]
        "Term is null"                                 | "target_name:String"  | ""           | [[label: "", value: ""], [label: "Go Biological Process as <strong>GO Biological Process Term</strong>", value: "gobp_term:\"Go Biological Process\""]]
        "Current Suggest Key is null and Term is null" | ""                    | ""           | [[label: "", value: ""], [label: "Go Biological Process as <strong>GO Biological Process Term</strong>", value: "gobp_term:\"Go Biological Process\""]]

    }
    /**
     *
     */
    void "test construct Single AutoSuggest Term #label"() {
        given: "map of possible filters"
        final Map<String, String> filtersMap = [gobp_term: "Go Biological Process"]
        when: "#label"
        Map<String, String> autoSuggestResults = service.constructSingleAutoSuggestTerm(filtersMap, currentAutoSuggestKey, term)

        then: "The expected map should match the constructed map"
        expectedResults == autoSuggestResults

        where:
        label                                          | currentAutoSuggestKey | term         | expectedResults
        "Term exist in filters Map"                    | "gobp_term"           | "DNA Repair" | [label: "DNA Repair as <strong>Go Biological Process</strong>", value: "gobp_term:\"DNA Repair\""]
        "Current Suggest Key is null"                  | ""                    | "gobp_term"  | null
        "Term is null"                                 | "target_name:String"  | ""           | null
        "Current Suggest Key is null and Term is null" | ""                    | ""           | null
    }
    /**
     *
     */
    void "test find Filtered Term #label"() {
        when: "#label"
        String filteredString = service.findFilteredTerm(searchString)

        then: "The expected Search Filter must much the created search filter"
        expectedFilteredString == filteredString

        where:
        label                   | searchString         | expectedFilteredString
        "Some unmatched String" | "exact:searchString" | null
        "Go BP Term"            | "gobp_term:String"   | "gobp_term"
        "Target Name Term"      | "target_name:String" | "target_name"
        "Empty Search String"   | ""                   | null
        "Empty Filter Name"     | "search"             | null

    }

    void "test constructFilter #label"() {

        when: "#label"
        final SearchFilter searchFilter = service.constructFilter(filterName, searchString)

        then: "The expected Search Filter must much the created search filter"
        expectedSearchFilter == searchFilter

        where:
        label                      | searchString    | filterName | expectedSearchFilter
        "String with no colon"     | "searchString"  | "search"   | null
        "String with colon"        | "search:String" | "search"   | new SearchFilter(filterName: "search", filterValue: "String")
        "String with colon at end" | "search:"       | "search"   | null
        "Empty Search String"      | ""              | "search"   | null
        "Empty Filter Name"        | "search"        | ""         | null

    }

    void "test compoundsToAdapters #label"() {
        when:
        final List<CompoundAdapter> foundCompoundAdapters = service.compoundsToAdapters(compounds)

        then:
        assert foundCompoundAdapters.size() == expectedCompoundAdapters.size()
        for (int index = 0; index < foundCompoundAdapters.size(); index++) {
            assert foundCompoundAdapters.get(index).name == expectedCompoundAdapters.get(index).name
        }

        where:
        label                | compounds                                | expectedCompoundAdapters
        "Single Compound"    | [new Compound("c1")]                     | [new CompoundAdapter(new Compound("c1"))]
        "Multiple Compounds" | [new Compound("c1"), new Compound("c2")] | [new CompoundAdapter(new Compound("c1")), new CompoundAdapter(new Compound("c2"))]
        "No Compounds"       | []                                       | []

    }

    void "test assaysToAdapters #label"() {
        when:
        final List<AssayAdapter> foundAssayAdapters = service.assaysToAdapters(assays)

        then:
        assert foundAssayAdapters.size() == expectedAssayAdapters.size()
        for (int index = 0; index < foundAssayAdapters.size(); index++) {
            assert foundAssayAdapters.get(index).name == expectedAssayAdapters.get(index).name
        }

        where:
        label             | assays                             | expectedAssayAdapters
        "Single Assay"    | [new Assay("c1")]                  | [new AssayAdapter(new Assay("c1"))]
        "Multiple Assays" | [new Assay("c1"), new Assay("c2")] | [new AssayAdapter(new Assay("c1")), new AssayAdapter(new Assay("c2"))]
        "No Assays"       | []                                 | []

    }

    void "test projectsToAdapters #label"() {
        when:
        final List<ProjectAdapter> foundProjectsAdapters = service.projectsToAdapters(projects)

        then:
        assert foundProjectsAdapters.size() == expectedProjectsAdapters.size()
        for (int index = 0; index < foundProjectsAdapters.size(); index++) {
            assert foundProjectsAdapters.get(index).name == expectedProjectsAdapters.get(index).name
        }

        where:
        label               | projects                               | expectedProjectsAdapters
        "Single Project"    | [new Project("c1")]                    | [new ProjectAdapter(new Project("c1"))]
        "Multiple Projects" | [new Project("c1"), new Project("c2")] | [new ProjectAdapter(new Project("c1")), new ProjectAdapter(new Project("c2"))]
        "No Projects"       | []                                     | []

    }

    void "test applySearchFiltersToSearchParams #label"() {
        when:
        service.applySearchFiltersToSearchParams(searchParams, searchFilters)

        then:
        searchParams.filters.size() == searchFilters.size()
        List<String[]> filters = searchParams.filters
        for (String[] filter : filters) {
            assert filter[1].startsWith("\"")
        }

        where:
        label                     | searchParams       | searchFilters
        "Multiple Search Filters" | new SearchParams() | [new SearchFilter("name1", "value1"), new SearchFilter("name2", "value2")]
        "Single Search Filter"    | new SearchParams() | [new SearchFilter("name1", "value1")]

    }

    void "test applySearchFiltersToSearchParams with filter ranges #label"() {
        when:
        service.applySearchFiltersToSearchParams(searchParams, searchFilters)


        then:
        searchParams.filters.size() == searchFilters.size()
        List<String[]> filters = searchParams.filters
        for (String[] filter : filters) {
            assert !filter[1].startsWith("\"")
        }
        where:
        label                              | searchParams       | searchFilters
        "Search Filter with number ranges" | new SearchParams() | [new SearchFilter("name1", "[* To 100]")]

    }

    void "test applySearchFiltersToSearchParams No Filters"() {
        when:
        service.applySearchFiltersToSearchParams(searchParams, searchFilters)

        then:
        assert !searchParams.filters

        where:
        label               | searchParams       | searchFilters
        "No Search Filters" | new SearchParams() | []
    }

    void "test constructSearchParams #label"() {
        when:
        final SearchParams searchParams = service.constructSearchParams(searchString, top, skip, searchFilters)

        then:
        assert searchParams
        searchParams.skip == skip
        searchParams.top == top
        searchParams.query == searchString
        if (searchFilters.size() > 0) {
            searchParams.filters.size() == searchFilters.size()
        }
        else {
            assert !searchParams.filters
        }

        where:
        label                     | searchString | top | skip | searchFilters
        "No Search Filters"       | "stuff"      | 10  | 0    | []
        "Multiple Search Filters" | "stuff"      | 10  | 0    | [new SearchFilter("name1", "value1"), new SearchFilter("name2", "value2")]
        "Single Search Filter"    | "stuff"      | 10  | 10   | [new SearchFilter("name1", "value1")]

    }
//    /**
//     */
//    void "test autoComplete #label"() {
//
//        when:
//        final List<String> response = service.autoComplete(term)
//
//        then:
//        elasticSearchService.searchQueryStringQuery(_, _) >> { jsonResponse }
//
//        assert response == expectedResponse
//
//        where:
//        label                       | term  | jsonResponse                        | expectedResponse
//        "Partial match of a String" | "Bro" | new JSONObject(AUTO_COMPLETE_NAMES) | ["Broad Institute MLPCN Platelet Activation"]
//        "Empty String"              | ""    | new JSONObject()                    | []
//    }
//    /**
//     */
//    void "test handleAutoComplete #label"() {
//
//        when:
//        final List<String> response = service.handleAutoComplete(term)
//
//        then:
//        elasticSearchService.searchQueryStringQuery(_, _) >> { jsonResponse }
//        assert response == expectedResponse
//
//        where:
//        label                       | term  | jsonResponse                        | expectedResponse
//        "Partial match of a String" | "Bro" | new JSONObject(AUTO_COMPLETE_NAMES) | ["Broad Institute MLPCN Platelet Activation"]
//        "Empty String"              | ""    | new JSONObject()                    | []
//    }
//    /**

}
