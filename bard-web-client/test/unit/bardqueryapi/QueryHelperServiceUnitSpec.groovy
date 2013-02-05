package bardqueryapi

import bard.core.SearchParams
import bard.core.adapter.AssayAdapter
import bard.core.adapter.CompoundAdapter
import bard.core.adapter.ProjectAdapter
import bard.core.rest.spring.DataExportRestService
import bard.core.rest.spring.assays.Assay
import bard.core.rest.spring.assays.AssayResult
import bard.core.rest.spring.compounds.Compound
import bard.core.rest.spring.compounds.CompoundResult
import bard.core.rest.spring.experiment.Activity
import bard.core.rest.spring.experiment.ActivityData
import bard.core.rest.spring.experiment.PriorityElement
import bard.core.rest.spring.experiment.ResultData
import bard.core.rest.spring.project.Project
import bard.core.rest.spring.project.ProjectResult
import bard.core.rest.spring.util.DictionaryElement
import bard.rest.api.wrapper.Dummy
import grails.test.mixin.TestFor
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

/**
 * See the API for {@link grails.test.mixin.support.GrailsUnitTestMixin} for usage instructions
 */
@Unroll
@TestFor(QueryHelperService)
class QueryHelperServiceUnitSpec extends Specification {
    @Shared Map<String, String> EMPTY_LABEL = [label: "", value: ""]
    @Shared Map<String, String> GO_TERM = [label: "Go Biological Process as <strong>GO Biological Process Term</strong>", value: "gobp_term:\"Go Biological Process\""]
    @Shared List<SearchFilter> searchFilters = [new SearchFilter(filterName: "a", filterValue: "b")]
    @Shared String display = "display"
    @Shared String description = "description"
    @Shared long dictElemId = 211


    void "test extractExperimentDetails #label"() {
        when:
        Map map = service.extractExperimentDetails(activities)
        then:

        assert map == expectedMap
        where:
        label                      | activities       | expectedMap
        "Empty Activities"         | []               | [priorityDisplay: '', dictionaryId: null, hasPlot: false, hasChildElements: false, yNormMin: 0.0, yNormMax: 0.0]
        "Activity, no Result Data" | [new Activity()] | [priorityDisplay: '', dictionaryId: null, hasPlot: false, hasChildElements: false, yNormMin: 0.0, yNormMax: 0.0]
    }


    void "test extractMapFromResultData #label"() {
        when:
        Map foundMap = service.extractMapFromResultData(resultData)
        then:
        assert expectedMap == foundMap

        where:
        label                                   | resultData                                                                                                       | expectedMap
        "Has Priority, ResponseClass=CR_SER"    | new ResultData(responseClass: "CR_SER", priorityElements: [new PriorityElement(pubChemDisplayName: display)])    | [priorityDisplay: display, priorityDescription: null, dictionaryId: null, hasPlot: true, hasChildElements: false]
        "Has Priority, ResponseClass=CR_NO_SER" | new ResultData(responseClass: "CR_NO_SER", priorityElements: [new PriorityElement(pubChemDisplayName: display)]) | [priorityDisplay: 'display', priorityDescription: null, dictionaryId: null, hasPlot: false, hasChildElements: false]
        "No Priority, ResponseClass=CR_NO_SER"  | new ResultData(responseClass: "CR_NO_SER", priorityElements: [])                                                 | [priorityDisplay: '', priorityDescription: '', dictionaryId: null, hasPlot: false, hasChildElements: false, yNormMin: null, yNormMax: null]

    }

    void "test extractExperimentDetails with activities"() {
        given:
        final DictionaryElement dictionaryElement = new DictionaryElement(elementId: dictElemId, label: display, description: description)
        DataExportRestService dataExportRestService = Mock(DataExportRestService)
        Dummy d = new Dummy()
        d.dataExportRestService = dataExportRestService

        PriorityElement priorityElement = new PriorityElement(pubChemDisplayName: display, dictElemId: dictElemId, childElements: [new ActivityData()])
        priorityElement.dummy = d
        Map expectedMap = [priorityDisplay:display, dictionaryId:'211', hasPlot:true, hasChildElements:true, yNormMin:0.0, yNormMax:0.0]
        List<Activity> activities = [new Activity(resultData: new ResultData(responseClass: "CR_SER",
                priorityElements: [priorityElement]))]
        when:
        Map map = service.extractExperimentDetails(activities)
        then:
        d.dataExportRestService.findDictionaryElementById(_) >> {dictionaryElement}
        assert expectedMap == map
    }

    void "test extractPriorityDisplayDescription #label"() {
        given:
        DataExportRestService dataExportRestService = Mock(DataExportRestService)
        Dummy d = new Dummy()
        d.dataExportRestService = dataExportRestService

        PriorityElement priorityElement = new PriorityElement(pubChemDisplayName: expectedPriorityDisplay, dictElemId: dictId)
        priorityElement.dummy = d
        when:
        Map map = service.extractPriorityDisplayDescription(priorityElement)
        then:
        d.dataExportRestService.findDictionaryElementById(_) >> {dictionaryElement}
        assert map.priorityDisplay == expectedPriorityDisplay
        assert map.priorityDescription == expectedPriorityDescription
        assert map.dictionaryId == expectedDictionaryId
        where:
        label                      | expectedPriorityDisplay | expectedPriorityDescription | dictId     | expectedDictionaryId | dictionaryElement
        "Map with dictionaryID"    | display                 | description                 | dictElemId | dictElemId           | new DictionaryElement(elementId: dictElemId, label: display, description: description)
        "Map without dictionaryID" | display                 | null                        | 0          | null                 | null

    }
    /**
     * {@link QueryHelperService#convertSearchFiltersToFilters(List)}
     */
    void "test convert SearchFilters To Filters"() {
        given: "A list of search filters"
        when: "We call the convertSearchFilters method with the given list of filters"
        final List<String[]> filters = service.convertSearchFiltersToFilters(searchFilters)

        then: ""
        assert filters
        assert filters.size() == 1
        final String[] filter = filters.get(0)
        assert filter.length == 2
        assert filter[0] == searchFilters[0].filterName
        assert filter[1] == searchFilters[0].filterValue
    }
    /**
     * {@link QueryHelperService#findFiltersInSearchBox(List, String)}
     */
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

    void "test get Auto Suggest Terms No Terms"() {
        when:
        List<Map<String, String>> autoCompleteResults = service.getAutoSuggestTerms(QueryHelperService.AUTO_SUGGEST_FILTERS, [], "gobp_term")

        then:
        assert !autoCompleteResults


    }

    void "test get Auto Suggest Terms No autoSuggest Key"() {
        when:
        List<Map<String, String>> autoCompleteResults = service.getAutoSuggestTerms(QueryHelperService.AUTO_SUGGEST_FILTERS, [], "")

        then:
        assert !autoCompleteResults


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
        "Term exist in filters Map"                    | "gobp_term"           | "DNA Repair" | [[label: "DNA Repair", value: "DNA Repair"], GO_TERM]
        "Current Suggest Key is null"                  | ""                    | "gobp_term"  | [[label: "gobp_term", value: "gobp_term"], GO_TERM]
        "Term is null"                                 | "target_name:String"  | ""           | [EMPTY_LABEL, GO_TERM]
        "Current Suggest Key is null and Term is null" | ""                    | ""           | [EMPTY_LABEL, GO_TERM]

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
        "Current Suggest Key is null"                  | ""                    | "gobp_term"  | [:]
        "Term is null"                                 | "target_name:String"  | ""           | [:]
        "Current Suggest Key is null and Term is null" | ""                    | ""           | [:]
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
        given:
        CompoundResult compoundResult = new CompoundResult()
        compoundResult.compounds = compounds
        when:
        final List<CompoundAdapter> foundCompoundAdapters = service.compoundsToAdapters(compoundResult)

        then:
        assert foundCompoundAdapters.size() == expectedCompoundAdapters.size()


        where:
        label                | compounds                                            | expectedCompoundAdapters
        "Single Compound"    | [new Compound(name: "c1")]                           | [new CompoundAdapter(new Compound(name: "c1"))]
        "Multiple Compounds" | [new Compound(name: "c1"), new Compound(name: "c2")] | [new CompoundAdapter(new Compound(name: "c1")), new CompoundAdapter(new Compound(name: "c2"))]
        "No Compounds"       | []                                                   | []

    }

    void "test assaysToAdapters - FreeTextAssays #label"() {
        when:
        final List<AssayAdapter> foundAssayAdapters = service.assaysToAdapters(assays)

        then:
        assert foundAssayAdapters.size() == expectedResults

        where:
        label         | assays                                           | expectedResults
        "No Assays"   | new AssayResult()                                | 0
        "With Assays" | new AssayResult(assays: [new Assay(name: "c1")]) | 1

    }

    void "test assaysToAdapters #label"() {
        when:
        final List<AssayAdapter> foundAssayAdapters = service.assaysToAdapters(assays, null)

        then:
        assert foundAssayAdapters.size() == expectedAssayAdapters.size()
        for (int index = 0; index < foundAssayAdapters.size(); index++) {
            assert foundAssayAdapters.get(index).name == expectedAssayAdapters.get(index).name
        }

        where:
        label             | assays                                         | expectedAssayAdapters
        "Single Assay"    | [new Assay(name: "c1")]                        | [new AssayAdapter(new Assay(name: "c1"))]
        "Multiple Assays" | [new Assay(name: "c1"), new Assay(name: "c2")] | [new AssayAdapter(new Assay(name: "c1")), new AssayAdapter(new Assay(name: "c2"))]
        "No Assays"       | []                                             | []

    }

    void "test projectsToAdapters #label"() {
        given:
        final ProjectResult projectResult = new ProjectResult()
        projectResult.projects = projects
        when:
        final List<ProjectAdapter> foundProjectsAdapters = service.projectsToAdapters(projectResult)

        then:
        assert foundProjectsAdapters.size() == expectedProjectsAdapters.size()
        for (int index = 0; index < foundProjectsAdapters.size(); index++) {
            assert foundProjectsAdapters.get(index).name == expectedProjectsAdapters.get(index).name
        }

        where:
        label                        | projects                                           | expectedProjectsAdapters
        "Single ProjectSearchResult" | [new Project(name: "c1")]                          | [new ProjectAdapter(new Project(name: "c1"))]
        "Multiple Projects"          | [new Project(name: "c1"), new Project(name: "c2")] | [new ProjectAdapter(new Project(name: "c1")), new ProjectAdapter(new Project(name: "c2"))]
        "No Projects"                | []                                                 | []

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
        "Search Filter with number ranges" | new SearchParams() | [new SearchFilter("name1", "100")]

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

    void "testStripCustomFiltersFromSearchString #label"() {
        when:
        final String updateSearchString = service.stripCustomFiltersFromSearchString(searchString)

        then:
        assert updateSearchString == expectedSearchString

        where:
        label                 | searchString      | expectedSearchString
        "No custom Filters"   | "stuff"           | null
        "With Custom Filters" | "gobp_term:stuff" | "stuff"
        "Empty String"        | ""                | null
    }

    void "testStripCustomStringFromSearchString #label"() {
        when:
        final String updateSearchString = service.stripCustomStringFromSearchString(searchString)

        then:
        assert updateSearchString == expectedSearchString

        where:
        label                 | searchString      | expectedSearchString
        "No custom Filters"   | "stuff"           | "stuff"
        "With Custom Filters" | "gobp_term:stuff" | "stuff"
        "Empty String"        | ""                | ""
    }
}
