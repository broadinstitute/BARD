package bard.core.rest.spring


import bard.core.SearchParams
import bard.core.SuggestParams
import bard.core.rest.helper.RESTTestHelper
import bard.core.rest.spring.util.Counts
import bard.core.rest.spring.util.ETag
import bard.core.rest.spring.util.Facet
import bard.core.rest.spring.util.SearchResult
import grails.plugin.spock.IntegrationSpec
import spock.lang.Unroll
import bard.core.rest.spring.assays.*

import static org.junit.Assert.assertTrue
import spock.lang.IgnoreRest

/**
 * Tests for RESTAssayService in JDO
 */
@Mixin(RESTTestHelper)
@Unroll
class AssayRestServiceIntegrationSpec extends IntegrationSpec {
    AssayRestService assayRestService

    void "getAssayAnnotationFromSearch"() {
        given:
        final SearchParams searchParams = new SearchParams("dna repair")
        final FreeTextAssayResult assaySearchResult = assayRestService.findAssaysByFreeTextSearch(searchParams)
        final long adid = assaySearchResult.assays.get(0).id
        when:
        final List<AssayAnnotation> annotations = assayRestService.findAnnotations(adid)
        then:
        assert annotations
        for (AssayAnnotation assayAnnotation : annotations) {
            // assert assayAnnotation?.display
            assert assayAnnotation.source
            assert assayAnnotation.id
            assert assayAnnotation.key
        }
    }

    void "getAssayAnnotationFromId"() {
        given:
        final ExpandedAssay assay = assayRestService.getAssayById(2868);
        when:
        final List<AssayAnnotation> annotations = assayRestService.findAnnotations(assay.id)
        then:
        assert annotations
        for (AssayAnnotation assayAnnotation : annotations) {
            assert assayAnnotation.display
            assert assayAnnotation.source
            assert assayAnnotation.id
            assert assayAnnotation.key
        }

    }
    @IgnoreRest
    void "getAssayAnnotationFromIds"() {
        given:
        List<Long> adids = [600L, 2868L]
        final ExpandedAssayResult assayResult = assayRestService.searchAssaysByIds(adids)
        final List<ExpandedAssay> assays = assayResult.assays
        final ExpandedAssay assay = assays.get(0)
        when:
        List<AssayAnnotation> annotations = assayRestService.findAnnotations(assay.getId())
        then:
        assert annotations

        for (AssayAnnotation assayAnnotation : annotations) {
            assert assayAnnotation.display
            assert assayAnnotation.source
            assert assayAnnotation.id
            assert assayAnnotation.key
        }
    }

    /**
     * Test filter application
     * We do a search without filters, then we do one with known filters and compare the results
     * They should not return the same number of results
     */

    /**
     * Test filter application
     * We do a search without filters, then we do one with known filters and compare the results
     * They should not return the same number of results
     */
    /**
     * Test filter application
     * We expect an OR when we use one or more filters from the same category
     * <p/>
     * Here we use "spectrophotometry method"  and  "image-based" both from the
     * detection method category
     */

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
        final SearchParams searchParamsWithNoFilters = new SearchParams("\"zinc ion binding\"");
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
        final FreeTextAssayResult assayServiceSearchResultsWithNoFilters = this.assayRestService.findAssaysByFreeTextSearch(searchParamsWithNoFilters);
        and:
        final FreeTextAssayResult assayServiceSearchResultsWithFilters = this.assayRestService.findAssaysByFreeTextSearch(searchParamsWithFilters);
        then:

        List<Facet> facets = assayServiceSearchResultsWithNoFilters.getFacets();
        int countHomogenous = 0;
        int countFluorPol = 0;
        int countFluorIntensity = 0;
        int countbioluminescence = 0;
        for (Facet facet : facets) {
            if (facet.getFacetName().equals("detection_method_type")) {
                final Counts counts = facet.counts
                final Map<String, Object> properties = counts.getAdditionalProperties()
                Object child = properties.get("homogeneous time-resolved fluorescence");
                countHomogenous = new Integer(child.toString());

                child = properties.get("fluorescence polarization");
                countFluorPol = new Integer(child.toString());

                child = properties.get("fluorescence intensity");
                countFluorIntensity = new Integer(child.toString());

                child = properties.get("bioluminescence");
                countbioluminescence = new Integer(child.toString());
            }
        }
        int sumOfFiltersReturnedIntiallyBySystem = countbioluminescence + countFluorIntensity + countFluorPol + countHomogenous;

        final long countWithFilters = assayServiceSearchResultsWithFilters.numberOfHits

        assertTrue("The total number of hits after applying the filters," + countWithFilters + "  should not exceed the sum of " +
                "the filters returned by the system, " + sumOfFiltersReturnedIntiallyBySystem +
                " prior to applying the filters", countWithFilters <= sumOfFiltersReturnedIntiallyBySystem);
    }


    void testApplyMultipleFiltersWithinTheSameCategoryWithAssayService() {
        given:
        //Do a search with no filters
        final SearchParams searchParamsWithNoFilters = new SearchParams("\"dna repair\"");
        searchParamsWithNoFilters.setSkip(new Long(0));
        searchParamsWithNoFilters.setTop(new Long(10));

        //now apply filters with detection method type=spectrophotometry method or detection method type= image-based
        final List<String[]> filters = new ArrayList<String[]>();
        filters.add(["detection_method_type", "\"spectrophotometry method\""] as String[])
        filters.add(["detection_method_type", "\"image-based\""] as String[])

        final SearchParams searchParamsWithFilters = new SearchParams("\"dna repair\"");
        searchParamsWithFilters.setSkip(new Long(0));
        searchParamsWithFilters.setTop(new Long(10));
        searchParamsWithFilters.setFilters(filters);

        when:
        SearchResult<ExpandedAssay> assayServiceSearchResultsWithNoFilters = this.assayRestService.findAssaysByFreeTextSearch(searchParamsWithNoFilters);
        and:
        final SearchResult<ExpandedAssay> assayServiceSearchResultsWithFilters = this.assayRestService.findAssaysByFreeTextSearch(searchParamsWithFilters);
        then:
        final long countWithNoFilters = assayServiceSearchResultsWithNoFilters.numberOfHits;
        final long countWithFilters = assayServiceSearchResultsWithFilters.numberOfHits;
        long counter = countWithNoFilters - countWithFilters;
        assertTrue("We expect the difference to be greater than zero", counter > 0);
    }

    void testApplyFiltersWithAssayService() {
        given:
        //Do a search with no filters
        final SearchParams searchParamsWithNoFilters = new SearchParams("\"dna repair\"");
        searchParamsWithNoFilters.setSkip(new Long(0));
        searchParamsWithNoFilters.setTop(new Long(10));

        //now apply filters with detection method type=spectrophotometry method
        //There are at least 7 of them
        final List<String[]> filters = new ArrayList<String[]>();
        filters.add(["detection_method_type", "\"spectrophotometry method\""] as String[])


        final SearchParams searchParamsWithFilters = new SearchParams("\"dna repair\"");
        searchParamsWithFilters.setSkip(new Long(0));
        searchParamsWithFilters.setTop(new Long(10));
        searchParamsWithFilters.setFilters(filters);
        when:
        FreeTextAssayResult assayServiceSearchResultsWithNoFilters = this.assayRestService.findAssaysByFreeTextSearch(searchParamsWithNoFilters);
        and:
        FreeTextAssayResult assayServiceSearchResultsWithFilters = this.assayRestService.findAssaysByFreeTextSearch(searchParamsWithFilters);
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
        final FreeTextAssayResult assaysByFreeTextSearch = this.assayRestService.findAssaysByFreeTextSearch(searchParams)

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
        final SearchParams searchParamsWithNoFilters = new SearchParams("\"dna repair\"");
        searchParamsWithNoFilters.setSkip(new Long(0));
        searchParamsWithNoFilters.setTop(new Long(10));

        //now apply filters with detection method type=spectrophotometry method
        //There are at least 7 of them
        final List<String[]> filters = new ArrayList<String[]>();
        filters.add(["target_name", "\"DNA (cytosine-5)-methyltransferase 1\""] as String[])

        final SearchParams searchParamsWithFilters = new SearchParams("\"dna repair\"");
        searchParamsWithFilters.setSkip(new Long(0));
        searchParamsWithFilters.setTop(new Long(10));
        searchParamsWithFilters.setFilters(filters);

        when:
        SearchResult<ExpandedAssay> assayServiceSearchResultsWithNoFilters = this.assayRestService.findAssaysByFreeTextSearch(searchParamsWithNoFilters);
        and:
        final SearchResult<ExpandedAssay> assayServiceSearchResultsWithFilters = this.assayRestService.findAssaysByFreeTextSearch(searchParamsWithFilters);

        then:

        final long countWithFilters = assayServiceSearchResultsWithFilters.numberOfHits;
        final long countWithNoFilters = assayServiceSearchResultsWithNoFilters.numberOfHits
        assert countWithNoFilters > countWithFilters, "Count with Filters= " + countWithFilters + " count without filters = " + countWithNoFilters

    }

    void testApplyMultipleFiltersFromDifferentCategoriesWithAssayService() {
        given:
        //Do a search with no filters
        final SearchParams searchParamsFilters = new SearchParams("\"dna repair\"");
        searchParamsFilters.setSkip(new Long(0));
        searchParamsFilters.setTop(new Long(10));

        //now apply filters with detection_method_type="spectrophotometry method" and  detection_method_type=" image-based"
        final List<String[]> filters = new ArrayList<String[]>();
        filters.add(["detection method type", "\"spectrophotometry method\""] as String[])
        filters.add(["assay component role", "\"target\""] as String[])

        searchParamsFilters.setFilters(filters);
        when:
        FreeTextAssayResult assaySearchResult = this.assayRestService.findAssaysByFreeTextSearch(searchParamsFilters);
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
        "Assay ID" | 644
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
        when: "We call the getFactest matehod"
        final List<Facet> facets = this.assayRestService.getFacetsByETag(etag)
        and: "A list of assays"
        final ExpandedAssayResult assayResult = this.assayRestService.searchAssaysByIds(adids)
        then: "We expect to get back a list of facets"
        assert !facets.isEmpty()
        assertFacetIdsAreUnique(facets)
        assertAssays(assayResult.assays)
        where:
        label                             | adids
        "Search with a list of assay ids" | [3894, 4174, 4202]
    }

    public void "testServices Free Text Search"() {
        given:
        SearchParams sp = new SearchParams("dna repair");
        sp.setSkip(new Long(0));
        sp.setTop(new Long(10));
        when:
        FreeTextAssayResult assaySearchResult = this.assayRestService.findAssaysByFreeTextSearch(sp);
        then:
        assert assaySearchResult.assays
        assertAssaySearches(assaySearchResult.assays)
    }

    void "test free text Assay search with filters"() {
        given: "Some two GO biological term filters"
        List<String[]> filters = []
        filters.add(["gobp_term", "DNA repair"] as String[])
        filters.add(["gobp_term", "response to UV-C"] as String[])

        and: "A search parameter is constructed using the filters and the search string 'dna repair'"
        SearchParams searchParams = new SearchParams("dna repair", filters);
        when: "The search method of a AssayService is invoked with the search params constructed above"
        FreeTextAssayResult searchResult = this.assayRestService.findAssaysByFreeTextSearch(searchParams)
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
        "Search with a list of assay ids" | [3894, 4174, 4202]
        "Search with a single assay id"   | [3894]
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
        final FreeTextAssayResult assaySearchResult = this.assayRestService.findAssaysByFreeTextSearch(params)
        then: "We expected to get back a list of #expectedNumberOfAssays assays"
        List<Assay> assays = assaySearchResult.assays
        assert assays
        assert !assays.isEmpty()

        assertAssaySearches(assays)
        assert assaySearchResult.numberOfHits >= expectedNumberOfAssays
        assert expectedNumberOfAssays == assays.size()
        where:
        label    | searchString | skip | top | expectedNumberOfAssays
        "Search" | "dna repair" | 0    | 10  | 10
    }

    /**
     *
     */
    void "test REST Assay Service #label #seachString with paging"() {
        given: "A search string of 'dna repair' and two Search Params, Params 1 has skip=0 and top=10, params2 has skip=10 and top=10"
        final String searchString = "dna repair"
        SearchParams params1 = new SearchParams(searchString)
        params1.setSkip(0)
        params1.setTop(10);

        SearchParams params2 = new SearchParams(searchString)
        params2.setSkip(10)
        params2.setTop(10);

        when: "We call the search method of RestAssayService with params1"
        final FreeTextAssayResult searchResult1 = this.assayRestService.findAssaysByFreeTextSearch(params1)
        and: "params2"
        final FreeTextAssayResult searchResult2 = this.assayRestService.findAssaysByFreeTextSearch(params2)


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
    void "test REST Assay Service with Zinc receptor bug https://www.pivotaltracker.com/story/show/36708637"() {
        given: "A search string, #searchString, and asking to retrieve the first #top search results"
        final SearchParams params = new SearchParams(searchString)
        params.setSkip(skip)
        params.setTop(top);
        when: "We we call search method of the the RestAssayService"
        final FreeTextAssayResult searchResult = this.assayRestService.findAssaysByFreeTextSearch(params)
        then: "We expected to get back a list of #expectedNumberOfAssays assays"
        Collection<Assay> assays = searchResult.assays
        assert assays
        assert !assays.isEmpty()

        assertAssaySearches(assays)
        assert searchResult.numberOfHits >= expectedNumberOfAssays
        assert expectedNumberOfAssays == assays.size()
        where:
        label    | searchString    | skip | top | expectedNumberOfAssays
        "Search" | "zinc receptor" | 0    | 10  | 10
    }

    /**
     *
     */
//    void "test Fail, Assay #adid does not exist "() {
//
//        when: "The get method is called with the given ADID: #adid"
//        final Assay assay = this.assayRestService.getAssayById(adid)
//        then: "A Null Assay is returned"
//        assert !assay
//        where:
//        label                       | adid
//        "Find a non-existing Assay" | new Integer(-644)
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
        final FreeTextAssayResult searchResult = this.assayRestService.findAssaysByFreeTextSearch(params)
        then: "We expected to get back unique facets"
        assertFacetIdsAreUnique(searchResult.facets)
    }

//    void "test get assay annotations #label"() {
//        when: "A list of assays"
//        final Collection<Assay> assays = this.restAssayService.get(adids)
//        AssayAdapter assayAdapter = new AssayAdapter(assays[0])
//
//        then:
//        assert !assays.isEmpty()
////        assert !assayAdapter.annotations.isEmpty()
//
//        where:
//        label         | adids
//        "Single ADID" | [2868]
//    }

}