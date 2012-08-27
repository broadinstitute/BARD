package jdo

import bard.core.rest.RESTEntityServiceManager
import grails.plugin.spock.IntegrationSpec
import org.junit.After
import org.junit.Before
import bard.core.*

/**
 * Tests for RESTAssayService in JDO
 */

class RESTAssayServiceIntegrationSpec extends IntegrationSpec {
    EntityServiceManager esm
    AssayService assayService
    final static String baseURL = "http://bard.nih.gov/api/v1"

    @Before
    void setup() {
        this.esm = new RESTEntityServiceManager(baseURL);
        this.assayService = esm.getService(Assay.class);
    }

    @After
    void tearDown() {
        this.esm.shutdown()
    }

    void assertAssay(final Assay assay) {
        assert assay.protocol
        assert assay.id
        assert assay.comments
        assert assay.name
        assert assay.description
    }
    /**
     * TODO: Uncomment after NCGC fixes?
     */
    void "test Get a Single Assay #label"() {

        when: "The get method is called with the given APID: #apid"
        final Assay assay = this.assayService.get(apid)
        then: "An Assay is returned with the expected information"
        assert assay
        assert apid == assay.id
        assertAssay(assay)
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
     * TODO" Not yet implemented. Currently only implemented for CIDs
     * TODO: Ask NCGC that this should include a highlight option even if it is a default String
     * TODO: Ask Steve, do we need facet information, paging information etc?
     */
    void "test Get Assays #label"() {
        when: "We call the get method of the the RESTAssayService"
        final Collection<Assay> assays = this.assayService.get(apids)
        then: "We expect to get back a list of 3 results"
        for (Assay assay : assays) {
            assertAssay(assay)
        }
        assert apids.size() == assays.size()
        where:
        label                             | apids
        "Search with a list of assay ids" | [600, 644, 666]
        "Search with a single assay id"   | [600]
    }
    /**
     * TODO: Ask NCGC that this search should return the same thing as the REST API
     */
    void "test REST Assay Service #label #seachString question"() {
        given: "A search string, #searchString, and asking to retrieve the first #top search results"
        final SearchParams params = new SearchParams(searchString).setSkip(skip).setTop(top);
        when: "We we call search method of the the RestAssayService"
        final ServiceIterator<Assay> searchIterator = this.assayService.search(params)
        then: "We expected to get back a list of 10 results"
        int numberOfAssays = 0
        while (searchIterator.hasNext()) {
            final Assay assay = searchIterator.next();
            assertAssay(assay)
            ++numberOfAssays
        }
        assert expectedNumberOfAssays == numberOfAssays

        searchIterator.done();
        where:
        label    | searchString | skip | top | expectedNumberOfAssays
        "Search" | "dna repair" | 0    | 10  | 10
    }
}