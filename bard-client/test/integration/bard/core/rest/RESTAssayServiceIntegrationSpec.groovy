package bard.core.rest

import bard.core.adapter.AssayAdapter
import bard.core.interfaces.SearchResult
import bard.core.rest.helper.RESTTestHelper
import spock.lang.Unroll
import bard.core.*

/**
 * Tests for RESTAssayService in JDO
 */
@Mixin(RESTTestHelper)
@Unroll
class RESTAssayServiceIntegrationSpec extends AbstractRESTServiceSpec {

    /**
     *
     * @param assayAdapter
     */
    void assertAssayAdapter(final AssayAdapter assayAdapter) {
        assert assayAdapter.assay
        assertAssay(assayAdapter.assay)

    }

    void "test get(resource,expand,top,long) #label"() {
        given:
        final String resource = "http://bard.nih.gov/api/v7/search/assays?q=dna"
        when:
        final List<Assay> assays = restAssayService.get(resource, expand, 10, 0)

        then:
        assert assays.size() == size
        where:
        label           | expand | size
        "Expand =true"  | true   | 10
        "Expand =false" | false  | 10
    }
    void "test get(long top,long skip)  #label"() {
        when:
        final List<Assay> assays = restAssayService.get(10, 0)

        then:
        assert assays.size() == size
        where:
        label           | expand | size
        "Expand =true"  | true   | 10
        "Expand =false" | false  | 10
    }
    void "test  getETags(long top, long skip)"() {
        when:
        final List<Value> tags = restAssayService.getETags(0, 10)
        then:
        assert tags != null
    }

    void "test Project From Single Assay #label=#adid"() {
        given:
        Assay a = this.restAssayService.get(adid);
        when:
        SearchResult<Project> iter =
            restAssayService.searchResult(a, Project.class);
        then:
        Collection<Project> projects = iter.next(1000);
        and:
        assert projects
        assert !projects.isEmpty()
        and:

        for (Project project : projects) {
            assert project.getId()
        }
        where:
        label      | adid
        "Assay ID" | 2868

    }


    public void "testServices Free Text Search"() {
        given:
        SearchParams sp = new SearchParams("dna repair");
        sp.setSkip(new Long(0));
        sp.setTop(new Long(10));
        when:
        SearchResult<Assay> si = this.restAssayService.search(sp);
        then:
        assert si.searchResults
        assert !si.searchResults.isEmpty()
        Assay a = si.searchResults.get(0)
        AssayAdapter ad = new AssayAdapter(a);
        Collection<Value> annos = ad.getAnnotations();
        assert annos
        assert !annos.isEmpty()
    }

    /**
     *
     */
    void "test Get a Single Assay with the given #adid"() {

        when: "The get method is called with the given ADID: #adid"
        final Assay assay = this.restAssayService.get(adid)
        then: "An Assay is returned with the expected information"
        assert assay
        assertAssay(assay, false)
        where:
        label             | adid             | isFreeText
        "First Assay ID"  | new Integer(644) | false
        "Second Assay ID" | new Integer(644) | false
    }
    /**
     *
     */
    void "test Fail, Assay #adid does not exist "() {

        when: "The get method is called with the given ADID: #adid"
        final Assay assay = this.restAssayService.get(adid)
        then: "A Null Assay is returned"
        assert !assay
        where:
        label                       | adid
        "Find a non-existing Assay" | new Integer(-644)
    }
    /**
     *
     */
    void "test Get Assays with a list of ADIDS #adids"() {
        when: "We call the get method of the the RESTAssayService with a list of assay ids"
        final Collection<Assay> assays = this.restAssayService.get(adids)
        then: "We expect to get back a list of assays"
        assertAssays(assays, false)
        assert adids.size() == assays.size()
        where:
        label                             | adids
        "Search with a list of assay ids" | [3894, 4174, 4202]
        "Search with a single assay id"   | [3894]
    }
    /**
     * if you search for the this assay via the REST api, you get one project associated with the assay: http://bard.nih.gov/api/latest/assays/588591/projects
     */
    void "test Get Assays with projects #label"() {
        when: "We call the get method of the the RESTAssayService with an assay ids"
        final Assay assay = this.restAssayService.get(adid)

        then: "We expect to get back a list of assays"
        assertAssays([assay], false)
        final SearchResult<Project> searchResult = restAssayService.searchResult(assay, Project.class)
        final List<Project> entities = searchResult.searchResults
        assert entities
        assert !entities.isEmpty()
        for (Project project : entities) {
            assert project
        }


        where:
        label                           | adid
        "Search with a single assay id" | 644
    }

    /**
     * if you search for the this assay via the REST api, you get one experiment associated with the assay
     */
    void "test Get Assays with experiment #label"() {
        when: "We call the get method of the the RESTAssayService with an assay ids"
        final Assay assay = this.restAssayService.get(adid)

        then: "We expect to get back a list of assays"
        assertAssays([assay], false)
        final SearchResult<Experiment> searchResult = restAssayService.searchResult(assay, Experiment.class)
        final List<Experiment> entities = searchResult.searchResults
        assert entities
        assert !entities.isEmpty()
        for (Experiment experiment : entities) {
            assert experiment
        }


        where:
        label                           | adid
        "Search with a single assay id" | 644
    }
    /**
     * if you search for the this assay via the REST api, you get one experiment associated with the assay
     */
    void "test Get Assay with a Single Compound Illegal Argument excpetion"() {
        when: "We call the get method of the the RESTAssayService with an assay id"
        final Assay assay = this.restAssayService.get(adid)
        restAssayService.searchResult(assay, Compound.class)
        then:
        thrown(IllegalArgumentException)

        where:
        label                           | adid
        "Search with a single assay id" | 644
    }
/**
 *
 * @param assays
 * @param isStringSearch - Means that this is a result of a string search and not an id search
 * string searches do not have sids, but they do have highlights and annotations
 */
    void assertAssays(Collection<Assay> assays, boolean isStringSearch = false) {
        for (Assay assay : assays) {
            assertAssay(assay, isStringSearch)
        }
    }

/**
 *
 * @param assay
 * @param isFreeTextSearch
 */
    void assertAssay(final Assay assay, final boolean isFreeTextSearch = false) {
        AssayAdapter assayAdapter = new AssayAdapter(assay)
        assert assay.id == assayAdapter.id
        assert assay.comments == assayAdapter.comments
        assert assay.name == assayAdapter.name
        assert assay.description == assayAdapter.description
        assert assay.category == assayAdapter.category
        assert assay.type == assayAdapter.type
        assert assay.role == assayAdapter.role

        if (isFreeTextSearch) { //if we are doing a free text search
            final Collection<Value> annotations = assayAdapter.getAnnotations()
            assert annotations
            assert !annotations.isEmpty()
            assert assayAdapter.searchHighlight
        }

    }
/**
 *
 */
    void "test REST Assay Service #label #seachString question"() {
        given: "A search string, #searchString, and asking to retrieve the first #top search results"
        final SearchParams params = new SearchParams(searchString)
        params.setSkip(skip)
        params.setTop(top);
        when: "We we call search method of the the RestAssayService"
        final SearchResult<Assay> searchResult = this.restAssayService.search(params)
        then: "We expected to get back a list of #expectedNumberOfAssays assays"
        Collection<Assay> assays = searchResult.searchResults
        assert assays
        assert !assays.isEmpty()

        assertAssays(assays, false)
        assert searchResult.count >= expectedNumberOfAssays
        assert expectedNumberOfAssays == assays.size()
        where:
        label    | searchString | skip | top | expectedNumberOfAssays
        "Search" | "dna repair" | 0    | 10  | 10
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
        final SearchResult<Assay> searchResult = this.restAssayService.search(params)
        then: "We expected to get back a list of #expectedNumberOfAssays assays"
        Collection<Assay> assays = searchResult.searchResults
        assert assays
        assert !assays.isEmpty()

        assertAssays(assays, false)
        assert searchResult.count >= expectedNumberOfAssays
        assert expectedNumberOfAssays == assays.size()
        where:
        label    | searchString    | skip | top | expectedNumberOfAssays
        "Search" | "zinc receptor" | 0    | 10  | 10
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
        final SearchResult<Assay> searchResult1 = this.restAssayService.search(params1)
        and: "params2"
        final SearchResult<Assay> searchResult2 = this.restAssayService.search(params2)


        then: "We expect that the list of ids returned for each search, would be different"
        Collection<Long> assayIds2 = searchResult2.searchResults.collect { Assay assay -> assay.id as Long }
        Collection<Long> assayIds1 = searchResult1.searchResults.collect { Assay assay -> assay.id as Long }
        assert assayIds1 != assayIds2
    }
/**
 *
 */
    void "test Facet keys (ids) are unique"() {
        given: "That we have created a valid search params object"
        final SearchParams params = new SearchParams("dna repair")
        params.setSkip(0)
        params.setTop(10);
        when: "We we call search method of the the RESTAssayService"
        final SearchResult<Assay> searchResult = this.restAssayService.search(params)
        then: "We expected to get back unique facets"
        assertFacetIdsAreUnique(searchResult)
    }

    void "test free text Assay search with filters"() {
        given: "Some two GO biological term filters"
        List<String[]> filters = []
        filters.add(["gobp_term", "DNA repair"] as String[])
        filters.add(["gobp_term", "response to UV-C"] as String[])

        and: "A search parameter is constructed using the filters and the search string 'dna repair'"
        SearchParams searchParams = new SearchParams("dna repair", filters);
        when: "The search method of a AssayService is invoked with the search params constructed above"
        SearchResult<Assay> searchResult = this.restAssayService.search(searchParams)
        and: "We collect all of the assays into a collection"
        Collection foundAssays = searchResult.searchResults
        then: "We can assert that the foundAssays Collection is not null and is not empty"
        assert foundAssays
        assert !foundAssays.isEmpty()

        and: "That the found Assays were obtained as a GO Biological Process Term"
        assertAssayFilterSearch(foundAssays)

    }

    void assertAssayFilterSearch(Collection<Assay> foundAssays) {
        for (Assay assay : foundAssays) {
            AssayAdapter assayAdapter = new AssayAdapter(assay)
            assert assayAdapter.assay
            assert assayAdapter.searchHighlight
            assert assayAdapter.id
            assert assayAdapter.name
        }
    }

    void "test Get Assays with facets single arg"() {
        when: "We call the getFactest matehod"
        final Object etag = restAssayService.newETag("My assay collection");
        then: "We expect to get back a list of facets"
        assert etag
    }


    void "test Get Assays with facets, #label"() {
        given: "An ETAG"
        final Object etag = restAssayService.newETag("My assay collection", adids);
        when: "We call the getFactest matehod"
        final Collection<Value> facets = this.restAssayService.getFacets(etag)
        and: "A list of assays"
        final Collection<Assay> assays = this.restAssayService.get(adids)
        then: "We expect to get back a list of facets"
        assert !facets.isEmpty()
        assertFacetIdsAreUnique(facets)
        assertAssays(assays, false)
        where:
        label                             | adids
        "Search with a list of assay ids" | [3894, 4174, 4202]
    }

    void "test get assay annotations #label"() {
        when: "A list of assays"
        final Collection<Assay> assays = this.restAssayService.get(adids)
        AssayAdapter assayAdapter = new AssayAdapter(assays[0])

        then:
        assert !assays.isEmpty()
        assert !assayAdapter.annotations.isEmpty()

        where:
        label         | adids
        "Single ADID" | [2868]
    }

}