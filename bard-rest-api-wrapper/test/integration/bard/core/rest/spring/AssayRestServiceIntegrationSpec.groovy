package bard.core.rest.spring

import bard.core.SearchParams
import bard.core.SuggestParams
import bard.core.rest.helper.RESTTestHelper
import bard.core.rest.spring.util.ETag
import bard.core.rest.spring.util.Facet
import bard.core.rest.spring.util.Target
import bard.core.rest.spring.util.TargetClassification
import grails.plugin.spock.IntegrationSpec
import spock.lang.Shared
import spock.lang.Unroll
import bard.core.rest.spring.assays.*

import static org.junit.Assert.assertTrue

/**
 * Tests for RESTAssayService in JDO
 */
@Mixin(RESTTestHelper)
@Unroll
class AssayRestServiceIntegrationSpec extends IntegrationSpec {
    AssayRestService assayRestService
    @Shared
    List<Long> ADIDS_FOR_TESTS = [25, 26, 27]

    @Shared
    List<Long> CAP_ADIDS = [5168, 5981, 5982]
    String FREE_TEXT_SEARCH_STRING = "dna repair"




    void "searchAssaysByCapIds #label"() {
        when:
        AssayResult assayResult = assayRestService.searchAssaysByCapIds(capIds, searchParams)
        then:
        assert (assayResult != null) == expected
        where:
        label            | searchParams                       | capIds    | expected
        "With capIds"    | new SearchParams(skip: 0, top: 10) | CAP_ADIDS | true
        "With no capIds" | new SearchParams(skip: 0, top: 10) | []        | false

    }


    void "searchAssaysByCapIds withETags #label"() {
        given: "That we have made a request with some CAP Ids that returns an etag"
        AssayResult assayResultWithIds = assayRestService.searchAssaysByCapIds(capIds, searchParams)
        when: "We use the returned etags to make another request"
        List<Assay> assays = assayRestService.searchAssaysByCapIds(searchParams, assayResultWithIds?.etags)
        then: "We get back the expected results"
        assert (!assays.isEmpty()) == expected
        where:
        label           | searchParams                       | capIds    | expected
        "With ETags"    | new SearchParams(skip: 0, top: 10) | CAP_ADIDS | true
        "With no ETags" | new SearchParams(skip: 0, top: 10) | []        | false

    }

    void "getAssayAnnotationFromSearch"() {
        given:
        final SearchParams searchParams = new SearchParams("quench")
        final AssayResult assayResult = assayRestService.findAssaysByFreeTextSearch(searchParams)
        final long adid = assayResult.assays.get(0).id
        when:
        final BardAnnotation annotation = assayRestService.findAnnotations(adid)
        then:
        assert assayResult.metaData
        //  assert assayResult.link
        assert assayResult.etag
        assert assayResult.facetsToValues
        assert annotation
        final List<Context> contexts = annotation.contexts
        assert contexts
        final List<List<Context>> layouts = Context.splitForColumnLayout(contexts)
        assert layouts
        final List<Context> contexts1 = layouts.get(0)
        assert contexts1
        assert annotation.docs
        assert annotation.measures
    }

    void "getAssayAnnotationFromId"() {
        given:
        final ExpandedAssay assay = assayRestService.getAssayById(27);
        when:
        final BardAnnotation annotation = assayRestService.findAnnotations(assay.id)
        then:
        assert annotation
        assert annotation.contexts
//         assert annotation.docs
        assert annotation.measures

    }

    void "getAssayAnnotationFromIds"() {
        given:
        final ExpandedAssayResult assayResult = assayRestService.searchAssaysByIds(ADIDS_FOR_TESTS)
        final List<ExpandedAssay> assays = assayResult.assays
        final ExpandedAssay assay = assays.get(0)
        when:
        BardAnnotation annotation = assayRestService.findAnnotations(assay.getId())
        then:
        assert annotation
        assert annotation.contexts
        assert annotation.docs
        assert annotation.measures

    }

    /**
     * typing in zinc, and choosing the auto-suggest option:
     * zinc ion binding as GO molecular function term
     * <p/>
     * Then choose the top for filters under detection method:
     * homogeneous time-resolved fluorescence (1)
     * fluorescence polarization (2)
     * fluorescence intensity (8)
     * bioluminescence (9)
     * These filters sum to 20 items, but after applying them the system has 120 items
     * Bug https://www.pivotaltracker.com/story/show/36709723
     *
     */
    void testAssaySuggestions() {
        given:
        //construct Search Params
        final SuggestParams suggestParams = constructSuggestParams();
        when:
        final Map<String, List<String>> assays = assayRestService.suggest(suggestParams);
        then:
        assertSuggestions(assays);
    }

    void testMultipleFilters() {
        given:
        //Do a search with no filters
        final SearchParams searchParamsWithNoFilters = new SearchParams("\"quench\"");
        searchParamsWithNoFilters.setSkip(new Long(0));
        searchParamsWithNoFilters.setTop(new Long(10));

        //now apply filters with detection method type=spectrophotometry method or detection method type= image-based
        final List<String[]> filters = new ArrayList<String[]>();
        filters.add(["detection_method_type", "\"homogeneous time-resolved fluorescence\""] as String[])
        filters.add(["detection_method_type", "\"fluorescence polarization\""] as String[])
        filters.add(["detection_method_type", "\"fluorescence intensity\""] as String[])
        filters.add(["detection_method_type", "\"bioluminescence\""] as String[])

        final SearchParams searchParamsWithFilters = new SearchParams("\"zinc ion binding\"");
        searchParamsWithFilters.setSkip(new Long(0));
        searchParamsWithFilters.setTop(new Long(10));
        searchParamsWithFilters.setFilters(filters);

        when:
        final AssayResult assayServiceSearchResultsWithNoFilters = this.assayRestService.findAssaysByFreeTextSearch(searchParamsWithNoFilters);
        and:
        final AssayResult assayServiceSearchResultsWithFilters = this.assayRestService.findAssaysByFreeTextSearch(searchParamsWithFilters);
        then:

        List<Facet> facets = assayServiceSearchResultsWithNoFilters.getFacets();
        assert facets.size()
        assert assayServiceSearchResultsWithNoFilters.numberOfHits > assayServiceSearchResultsWithFilters.numberOfHits
    }


    void testApplyMultipleFiltersWithinTheSameCategoryWithAssayService() {
        given:
        //Do a search with no filters
        final SearchParams searchParamsWithNoFilters = new SearchParams("\"DiI\"");
        searchParamsWithNoFilters.setSkip(new Long(0));
        searchParamsWithNoFilters.setTop(new Long(10));

        //now apply filters with detection method type=fluorescence intensity or detection method type= luminescence method
        final List<String[]> filters = new ArrayList<String[]>();
        filters.add(["detection_method_type", "\"fluorescence intensity\""] as String[])
        filters.add(["detection_method_type", "\"luminescence method\""] as String[])

        final SearchParams searchParamsWithFilters = new SearchParams("\"DiI\"");
        searchParamsWithFilters.setSkip(new Long(0));
        searchParamsWithFilters.setTop(new Long(10));
        searchParamsWithFilters.setFilters(filters);

        when:
        AssayResult assayServiceSearchResultsWithNoFilters = this.assayRestService.findAssaysByFreeTextSearch(searchParamsWithNoFilters);
        and:
        final AssayResult assayServiceSearchResultsWithFilters = this.assayRestService.findAssaysByFreeTextSearch(searchParamsWithFilters);
        then:
        final long countWithNoFilters = assayServiceSearchResultsWithNoFilters.numberOfHits;
        final long countWithFilters = assayServiceSearchResultsWithFilters.numberOfHits;
        long counter = countWithNoFilters - countWithFilters;
        assertTrue("We expect the difference to be greater than zero", counter > 0);
    }

    void testApplyFiltersWithAssayService() {
        given:
        //Do a search with no filters
        final SearchParams searchParamsWithNoFilters = new SearchParams("\"DiI\"");
        searchParamsWithNoFilters.setSkip(new Long(0));
        searchParamsWithNoFilters.setTop(new Long(10));

        //now apply filters with detection method type=fluorescence intensity
        //There are at least 4 of them
        final List<String[]> filters = new ArrayList<String[]>();
        filters.add(["detection_method_type", "\"fluorescence intensity\""] as String[])


        final SearchParams searchParamsWithFilters = new SearchParams("\"DiI\"");
        searchParamsWithFilters.setSkip(new Long(0));
        searchParamsWithFilters.setTop(new Long(10));
        searchParamsWithFilters.setFilters(filters);
        when:
        AssayResult assayServiceSearchResultsWithNoFilters = this.assayRestService.findAssaysByFreeTextSearch(searchParamsWithNoFilters);
        and:
        AssayResult assayServiceSearchResultsWithFilters = this.assayRestService.findAssaysByFreeTextSearch(searchParamsWithFilters);
        then:
        final long countWithNoFilters = assayServiceSearchResultsWithNoFilters.numberOfHits;
        final long countWithFilters = assayServiceSearchResultsWithFilters.numberOfHits;
        long counter = countWithNoFilters - countWithFilters;
        assertTrue("There should be difference in the number of hits, between a query with no filters and one with filters", counter > 0);
    }
    /**
     * Copied from RESTTestServices#testServices9
     */
    void testFiltersWithAssayService() {
        given:
        //construct Search Params
        final SearchParams searchParams = constructSearchParamsWithFilters();

        when:
        final AssayResult assaysByFreeTextSearch = this.assayRestService.findAssaysByFreeTextSearch(searchParams)

        then:
        assertTrue("AssayService SearchResults from 'DNA repair query' must not be null", assaysByFreeTextSearch != null);

        final List<Assay> assays = assaysByFreeTextSearch.assays
        assertTrue("AssayService SearchResults from 'DNA repair query' must not be empty", assays.size() > 0);
        assertTrue "AssayService SearchResults from 'DNA repair query' must have at least one element", assaysByFreeTextSearch.numberOfHits >= 1
        for (Assay assay : assays) {
            assert assay.name, "Name must exist"
            assert assay.id, "Assay from 'DNA repair query' must have an ADID"
        }
        final List<Facet> facets = assaysByFreeTextSearch.getFacets();
        assertTrue("List of Facets from 'DNA repair query' is not null", facets != null);
        assertTrue("List of Facets from 'DNA repair query' is not empty", !facets.isEmpty());

    }


    void testFiltersWithAssayServiceParenthesisInFilterValue() {
        given:
        //Do a search with no filters
        final SearchParams searchParamsWithNoFilters = new SearchParams("\"DiI\"");
        searchParamsWithNoFilters.setSkip(new Long(0));
        searchParamsWithNoFilters.setTop(new Long(10));

        //now apply filters with detection method type=target cell
        //There are at least 4 of them
        final List<String[]> filters = new ArrayList<String[]>();
        filters.add(["assay_component_role", "\"target cell\""] as String[])

        final SearchParams searchParamsWithFilters = new SearchParams("\"DiI\"");
        searchParamsWithFilters.setSkip(new Long(0));
        searchParamsWithFilters.setTop(new Long(10));
        searchParamsWithFilters.setFilters(filters);

        when:
        AssayResult assayServiceSearchResultsWithNoFilters = this.assayRestService.findAssaysByFreeTextSearch(searchParamsWithNoFilters);
        and:
        final AssayResult assayServiceSearchResultsWithFilters = this.assayRestService.findAssaysByFreeTextSearch(searchParamsWithFilters);

        then:

        final long countWithFilters = assayServiceSearchResultsWithFilters.numberOfHits;
        final long countWithNoFilters = assayServiceSearchResultsWithNoFilters.numberOfHits
        assert countWithNoFilters > countWithFilters, "Count with Filters= " + countWithFilters + " count without filters = " + countWithNoFilters

    }

    void testApplyMultipleFiltersFromDifferentCategoriesWithAssayService() {
        given:
        //Do a search with no filters
        final SearchParams searchParamsFilters = new SearchParams("\"DiI\"");
        searchParamsFilters.setSkip(new Long(0));
        searchParamsFilters.setTop(new Long(10));

        //now apply filters with detection_method_type="fluorescence intensity" and  Assay component role="target cell"
        final List<String[]> filters = new ArrayList<String[]>();
        filters.add(["detection_method_type", "\"fluorescence intensity\""] as String[])
        filters.add(["target_name", "\"cholesterol import\""] as String[])

        searchParamsFilters.setFilters(filters);
        when:
        AssayResult assaySearchResult = this.assayRestService.findAssaysByFreeTextSearch(searchParamsFilters);
        then:
        final long countWithFilters = assaySearchResult.numberOfHits
        assert countWithFilters > 0, "Expect at least one filter"
    }

    void "test Get Assays with facets single arg"() {
        when: "We call the getFactest matehod"
        final String etag = assayRestService.newETag("My assay collection", null);
        then: "We expect to get back a list of facets"
        assert etag
    }
    /**
     *
     */
    void "test Get a Single Assay with the given #adid"() {

        when: "The get method is called with the given ADID: #adid"
        final ExpandedAssay assay = this.assayRestService.getAssayById(adid)
        then: "An Assay is returned with the expected information"
        assert assay
        assertAssay(assay)
        where:
        label      | adid
        "Assay ID" | ADIDS_FOR_TESTS.get(0)
    }

    void "test assay #label"() {
        when: "The get method is called with the given ADID: #adid"
        final ExpandedAssay assay = this.assayRestService.getAssayById(adid)
        then: "An Assay is returned with the expected information"
        assert assay
        final List<Target> targets = assay.getBiology()
//        assert targets
        int counter = 0  //to count the number of classifications
        for (Target target : targets) {
            assert target.acc
            if (target.getTargetClassifications()) {
                final List<TargetClassification> classifications = target.getTargetClassifications()
                assert classifications
                for (TargetClassification targetClassification : classifications) {
                    assert targetClassification.id
                    assert targetClassification.source
                    assert targetClassification.description
                    assert targetClassification.levelIdentifier
                    assert targetClassification.name
                    ++counter
                }
            }
        }

        if (targets) {
            assert counter > 0
        }

        where:
        label         | adid
        "with Target" | 25
    }

    void "test  getETags(long top, long skip)"() {
        when:
        final List<ETag> eTags = assayRestService.getETags(0, 10)
        then:
        assert eTags != null
    }


    void "test Get Assays with facets, #label"() {
        given: "An ETAG"
        final String etag = assayRestService.newETag("My assay collection", adids);
        when: "We call the getFacets method"
        final List<Facet> facets = this.assayRestService.getFacetsByETag(etag)
        and: "A list of assays"
        final ExpandedAssayResult assayResult = this.assayRestService.searchAssaysByIds(adids)
        then: "We expect to get back a list of facets"
        assert !facets.isEmpty()
        assertFacetIdsAreUnique(facets)
        assertAssays(assayResult.assays)
        where:
        label                             | adids
        "Search with a list of assay ids" | ADIDS_FOR_TESTS
    }

    public void "testServices Free Text Search"() {
        given:
        SearchParams sp = new SearchParams(FREE_TEXT_SEARCH_STRING);
        sp.setSkip(new Long(0));
        sp.setTop(new Long(10));
        when:
        AssayResult assaySearchResult = this.assayRestService.findAssaysByFreeTextSearch(sp);
        then:
        assert assaySearchResult.assays
        assertAssaySearches(assaySearchResult.assays)
    }

    void "test free text Assay search with filters"() {
        given: "A GO biological term filters"
        List<String[]> filters = []
        filters.add(["gobp_term", "cell death"] as String[])

        and: "A search parameter is constructed using the filters and the search string 'dna repair'"
        SearchParams searchParams = new SearchParams(FREE_TEXT_SEARCH_STRING, filters);
        when: "The search method of a AssayService is invoked with the search params constructed above"
        AssayResult searchResult = this.assayRestService.findAssaysByFreeTextSearch(searchParams)
        and: "We collect all of the assays into a collection"
        List<Assay> foundAssays = searchResult.assays
        then: "We can assert that the foundAssays Collection is not null and is not empty"
        assert foundAssays
        assert !foundAssays.isEmpty()

        and: "That the found Assays were obtained as a GO Biological Process Term"
        assertAssaySearches(foundAssays)

    }
    /**
     *
     */
    void "test Get Assays with a list of ADIDS #adids"() {
        when: "We call the get method of the the RESTAssayService with a list of assay ids"
        final ExpandedAssayResult assayResult = this.assayRestService.searchAssaysByIds(adids)
        then: "We expect to get back a list of assays"
        assertAssays(assayResult.assays)
        assert assayResult.numberOfHits == adids.size()
        where:
        label                             | adids
        "Search with a list of assay ids" | ADIDS_FOR_TESTS
        "Search with a single assay id"   | [ADIDS_FOR_TESTS.get(0)]
    }
/**
 *
 */
    void "test REST Assay Service #label #searchString question"() {
        given: "A search string, #searchString, and asking to retrieve the first #top search results"
        final SearchParams params = new SearchParams(searchString)
        params.setSkip(skip)
        params.setTop(top);
        when: "We we call search method of the the RestAssayService"
        final AssayResult assaySearchResult = this.assayRestService.findAssaysByFreeTextSearch(params)
        then: "We expected to get back a list of #expectedNumberOfAssays assays"
        List<Assay> assays = assaySearchResult.assays
        assert assays
        assert !assays.isEmpty()

        assertAssaySearches(assays)
        assert assaySearchResult.numberOfHits >= expectedNumberOfAssays
        assert expectedNumberOfAssays == assays.size()
        where:
        label    | searchString | skip | top | expectedNumberOfAssays
        "Search" | "DiI"        | 0    | 3   | 3
    }

    /**
     *
     */
    void "test REST Assay Service #label #seachString with paging"() {
        given: "A search string of 'DNA repair' and two Search Params, Params 1 has skip=0 and top=4, params2 has skip=4 and top=10"
        SearchParams params1 = new SearchParams(FREE_TEXT_SEARCH_STRING)
        params1.setSkip(0)
        params1.setTop(4);

        SearchParams params2 = new SearchParams(FREE_TEXT_SEARCH_STRING)
        params2.setSkip(4)
        params2.setTop(10);

        when: "We call the search method of RestAssayService with params1"
        final AssayResult searchResult1 = this.assayRestService.findAssaysByFreeTextSearch(params1)
        and: "params2"
        final AssayResult searchResult2 = this.assayRestService.findAssaysByFreeTextSearch(params2)


        then: "We expect that the list of ids returned for each search, would be different"
        Collection<Long> assayIds2 = searchResult2.assays.collect { Assay assay -> assay.id as Long }
        Collection<Long> assayIds1 = searchResult1.assays.collect { Assay assay -> assay.id as Long }
        assert assayIds1 != assayIds2
    }
/**
 * searching for the text
 zinc receptor
 times out after a long time
 */
//    void "test REST Assay Service with Zinc receptor bug https://www.pivotaltracker.com/story/show/36708637"() {
//        given: "A search string, #searchString, and asking to retrieve the first #top search results"
//        final SearchParams params = new SearchParams(searchString)
//        params.setSkip(skip)
//        params.setTop(top);
//        when: "We we call search method of the the RestAssayService"
//        final AssayResult searchResult = this.assayRestService.findAssaysByFreeTextSearch(params)
//        then: "We expected to get back a list of #expectedNumberOfAssays assays"
//        Collection<Assay> assays = searchResult.assays
//        assert assays
//        assert !assays.isEmpty()
//
//        assertAssaySearches(assays)
//        assert searchResult.numberOfHits >= expectedNumberOfAssays
//        assert expectedNumberOfAssays == assays.size()
//        where:
//        label    | searchString    | skip | top | expectedNumberOfAssays
//        "Search" | "zinc receptor" | 0    | 10  | 10
//    }

/**
 *
 */
    void "test Facet keys (ids) are unique"() {
        given: "That we have created a valid search params object"
        final SearchParams params = new SearchParams("dna repair")
        params.setSkip(0)
        params.setTop(10);
        when: "We we call search method of the the RESTAssayService"
        final AssayResult searchResult = this.assayRestService.findAssaysByFreeTextSearch(params)
        then: "We expected to get back unique facets"
        assertFacetIdsAreUnique(searchResult.facets)
    }


}