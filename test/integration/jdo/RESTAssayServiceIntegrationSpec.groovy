package jdo

import bard.core.adapter.AssayAdapter
import bard.core.rest.RESTEntityServiceManager
import grails.plugin.spock.IntegrationSpec
import org.junit.After
import org.junit.Before
import bard.core.*

/**
 * Tests for RESTAssayService in JDO
 */
@Mixin(RESTTestHelper)
class RESTAssayServiceIntegrationSpec extends IntegrationSpec implements RESTServiceInterface {
    EntityServiceManager esm
    AssayService assayService

    @Before
    void setup() {
        this.esm = new RESTEntityServiceManager(baseURL);
        this.assayService = esm.getService(Assay.class);
    }

    @After
    void tearDown() {
        this.esm.shutdown()
    }



    void assertAssayAdapter(final AssayAdapter assayAdapter) {
        assert assayAdapter.assay
        assertAssay(assayAdapter.assay)

    }
    /**
     *
     */
    void "test Get a Single Assay #label"() {

        when: "The get method is called with the given APID: #apid"
        final Assay assay = this.assayService.get(apid)
        then: "An Assay is returned with the expected information"
        assert assay
        assert apid == assay.id
        assertAssay(assay,false)
        where:
        label                    | apid
        "Find an existing Assay" | new Integer(644)
    }
    /**
     *
     */
    void "test Fail #label"() {

        when: "The get method is called with the given APID: #apid"
        final Assay assay = this.assayService.get(apid)
        then: "An No Assay is returned"
        assert !assay
        where:
        label                       | apid
        "Find a non-existing Assay" | new Integer(-644)
    }
    /**
     *
     */
    void "test Get Assays #label"() {
        when: "We call the get method of the the RESTAssayService with a list of assay ids"
        final Collection<Assay> assays = this.assayService.get(adids)
        then: "We expect to get back a list of 3 results"
        assertAssays(assays, false)
        assert adids.size() == assays.size()
        where:
        label                             | adids
        "Search with a list of assay ids" | [600, 644, 666]
        "Search with a single assay id"   | [600]
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
     * @param isStringSearch
     */
    void assertAssay(final Assay assay, final boolean isStringSearch = false) {
        AssayAdapter assayAdapter = new AssayAdapter(assay)
        assert assay.protocol
        assert assay.id == assayAdapter.assay.id
        assert assay.comments == assayAdapter.assay.comments
        assert assay.name == assayAdapter.assay.name
        assert assay.description == assayAdapter.assay.description
        assert assay.category == assayAdapter.assay.category
        assert assay.type == assayAdapter.assay.type
        assert assay.role == assayAdapter.assay.role

        if (isStringSearch) {
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
        final ServiceIterator<Assay> searchIterator = this.assayService.search(params)
        then: "We expected to get back a list of #expectedNumberOfAssays assays"
        Collection<Assay> assays = searchIterator.collect()
        assertAssays(assays,false)
        assert searchIterator.count >= expectedNumberOfAssays
        assert expectedNumberOfAssays ==assays.size()
        searchIterator.done();
        where:
        label    | searchString | skip | top | expectedNumberOfAssays
        "Search" | "dna repair" | 0    | 10  | 10
    }
    /**
     *
     */
    void "test Facet keys (ids) are unique"() {
        given: "That we have created a valid search params object"
        final SearchParams params = new SearchParams("dna repair")
        params.setSkip(0)
        params.setTop(10);
        when: "We we call search method of the the RESTCompoundService"
        final ServiceIterator<Assay> searchIterator = this.assayService.search(params)
        then: "We expected to get back unique facets"
        assertFacetIdsAreUnique(searchIterator)
        searchIterator.done();
    }
    /**
     *
     */
    void "test Get Assays with facets, #label"() {
        given:
        final Object etag = assayService.newETag("My awesome assay collection", adids);
        when: "We call the getFactest matehod"
        final Collection<Value> facets = this.assayService.getFacets(etag)
        and: "A list of assays"
        final Collection<Assay> assays = this.assayService.get(adids)
        then: "We expect to get back a list of facets"
        assert facets
        assertFacetIdsAreUnique(facets)
        assertAssays(assays, false)
        where:
        label                             | adids
        "Search with a list of assay ids" | [600, 644, 666]
    }

}