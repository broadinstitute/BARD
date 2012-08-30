package jdo

import bard.core.adapter.CompoundAdapter
import bard.core.rest.RESTEntityServiceManager
import grails.plugin.spock.IntegrationSpec
import org.junit.After
import org.junit.Before
import bard.core.*

/**
 * Tests for RESTCompoundService in JDO
 */
@Mixin(RESTTestHelper)
class RESTCompoundServiceIntegrationSpec extends IntegrationSpec implements RESTServiceInterface {
    EntityServiceManager esm
    CompoundService compoundService

    @Before
    void setup() {
        this.esm = new RESTEntityServiceManager(baseURL);
        this.compoundService = esm.getService(Compound.class);
    }

    @After
    void tearDown() {
        this.esm.shutdown()
    }
    /**
     *
     * @param compounds
     * @param isStringSearch - Means that this is a result of a string search and not an id search
     * string searches do not have sids, but they do have highlights and annotations
     */
    void assertCompounds(Collection<Compound> compounds, boolean isStringSearch = false) {
        for (Compound compound : compounds) {
            assertCompound(compound, isStringSearch)
        }
    }
    void assertCompound(final Compound compound, final boolean isStringSearch = false) {
        CompoundAdapter compoundAdapter = new CompoundAdapter(compound);
        assert compound.id == compoundAdapter.getPubChemCID()
        assert compoundAdapter.getStructureSMILES()
        assert compound.name == compoundAdapter.getName();
        assert compoundAdapter.formula()
        assert compoundAdapter.mwt()
        assert compoundAdapter.exactMass()

        if (isStringSearch) {
            final Collection<Value> annotations = compoundAdapter.getAnnotations()
            assert annotations
            assert !annotations.isEmpty()
            assert compoundAdapter.searchHighlight

        } else {
            List<Long> sids = compoundAdapter.getPubChemSIDs() as List<Long>;
            assert sids
            assert !sids.isEmpty()
            assert compoundAdapter.hbondDonor() >= 0
            assert compoundAdapter.rotatable() >= 0
            assert compoundAdapter.definedStereo() >= 0
            assert compoundAdapter.stereocenters() >= 0
            assert compoundAdapter.hbondAcceptor() >= 0
            assert compoundAdapter.TPSA() >= 0
            assert compoundAdapter.logP() >= 0
            List<String> synonyms = compoundAdapter.getSynonyms() as List<String>
            assert synonyms != null

        }

    }

    /**
     *
     */
    void "test Get a Single Compound #label"() {

        when: "The get method is called with the given CID: #cid"
        final Compound compound = this.compoundService.get(cid)
        then: "A Compound is returned with the expected information"
        assertCompound(compound)
        final CompoundAdapter compoundAdapter = new CompoundAdapter(compound)
        assert cid == compoundAdapter.pubChemCID
        assert expectedSmiles == compoundAdapter.structureSMILES
        Long[] sids = compoundAdapter.pubChemSIDs
        assert expectedSIDs.size() == sids.length
        assert expectedSIDs == sids
        where:
        label                       | cid                 | expectedSIDs                                                                                        | expectedSmiles
        "Find an existing compound" | new Integer(658342) | [5274057, 47984903, 51638425, 113532087, 124777946, 970329, 6320599, 35591597, 76362856, 112834159] | "C(CN1CCCCC1)N1C(N=CC2=CC=CS2)=NC2=CC=CC=C12"
    }

    /**
     *
     */
    void "test Fail, CID does not exists: #label"() {

        when: "The get method is called with the given CID: #cid"
        final Compound compound = this.compoundService.get(cid)
        then: "No Compound is returned with the expected information"
        assert !compound
        where:
        label                          | cid
        "Find a non-existing compound" | new Integer(-658342)
    }
    /**
     *
     */
    void "test Get Compounds, #label"() {
        when: "We call the get method of the the RESTCompoundService"
        final Collection<Compound> compounds = this.compoundService.get(cids)
        then: "We expect to get back a list of 10 results"
        assertCompounds(compounds, false)
        assert cids.size() == compounds.size()
        where:
        label                         | cids
        "Search with a list of CIDs"  | [3235555, 3235556, 3235557, 3235558, 3235559, 3235560, 3235561, 3235562, 3235563, 3235564]
        "Search with a single of CID" | [3235555]

    }
    /**
     *
     */
    void "test Get Compounds with facets, #label"() {
        given:
        Object etag = compoundService.newETag("My awesome compound collection", cids);
        when: "We call the getFactest matehod"
        Collection<Value> facets = this.compoundService.getFacets(etag)
        and:
        Collection<Compound> compounds = this.compoundService.get(cids)
        then: "We expect to get back a list of facets"
        assert facets
        assertFacetIdsAreUnique(facets)
        assertCompounds(compounds, false)
        where:
        label                         | cids
        "Search with a list of CIDs"  | [3235555, 3235556, 3235557, 3235558, 3235559, 3235560, 3235561, 3235562, 3235563, 3235564]
        "Search with a single of CID" | [3235555]

    }

    /**
     *
     */
    void "test REST Compound Service #label #seachString question"() {
        given: "A search string, #searchString, and asking to retrieve the first #top search results"
        final SearchParams params = new SearchParams(searchString)
        params.setSkip(skip)
        params.setTop(top);
        when: "We we call search method of the the RESTCompoundService"
        final ServiceIterator<Compound> searchIterator = this.compoundService.search(params)
        then: "We expected to get back a list of 10 results"
        Collection<Compound> compounds = searchIterator.collect()
        assertCompounds(compounds, true)
        assert searchIterator.count >= expectedNumberOfCompounds
        assert expectedNumberOfCompounds == compounds.size()
        assertFacets(searchIterator.facets)
        searchIterator.done();
        where:
        label    | searchString | skip | top | expectedNumberOfCompounds
        "Search" | "dna repair" | 0    | 10  | 10
        "Search" | "dna repair" | 10   | 10  | 10
    }

    /**
     */
    void "test Facet keys (ids) are unique"() {
        given: "That we have created a valid search params object with a search string of 'dna repair', skip=0 and top=10"
        final SearchParams params = new SearchParams("dna repair")
        params.setSkip(0)
        params.setTop(10);
        when: "We we call the search method of the the RESTCompoundService with the params object"
        final ServiceIterator<Compound> searchIterator = this.compoundService.search(params)
        then: "We expect to get back unique facets"
        assertFacetIdsAreUnique(searchIterator)
        searchIterator.done();
    }

/**
 * Do structure searches
 */
    void "test Structure Search : #label"() {
        given: "Structure Paramaters with top #top  and skip #skip and a threshhold of 0.9"
        final StructureSearchParams structureSearchParams =
            new StructureSearchParams(smiles)
        structureSearchParams.setSkip(skip).setTop(top);
        BigDecimal threshold = 0.9
        structureSearchParams.setThreshold(threshold)
        when: "We call the search method of the Compound Service"
        final ServiceIterator<Compound> searchIterator = this.compoundService.search(structureSearchParams)
        then: "The following assertions are true"
        Collection<Compound> compounds = searchIterator.collect()
        assertCompounds(compounds, false)
        final Collection<Value> facets = searchIterator.facets
        assert searchIterator.count >= expectedNumberOfCompounds
        assert expectedNumberOfCompounds == compounds.size()
        assertFacets(facets)
        assertFacetIdsAreUnique(facets)

        searchIterator.done()
        where:
        label                           | structureSearchParamsType                 | smiles                                        | skip | top | expectedNumberOfCompounds
        "Super Structure search"        | StructureSearchParams.Type.Superstructure | "O=S(*C)(Cc1ccc2ncc(CCNC)c2c1)=O"             | 0    | 10  | 3
        "Similarity Structure Search"   | StructureSearchParams.Type.Similarity     | "CN(C)CCC1=CNC2=C1C=C(CS(=O)(=O)N3CCCC3)C=C2" | 0    | 10  | 1
        "Exact Structure Search"        | StructureSearchParams.Type.Exact          | "CN(C)CCC1=CNC2=C1C=C(CS(=O)(=O)N3CCCC3)C=C2" | 0    | 10  | 1
        "Sub Structure Search"          | StructureSearchParams.Type.Substructure   | "CN(C)CCC1=CNC2=C1C=C(CS(=O)(=O)N3CCCC3)C=C2" | 0    | 10  | 1
        "Default (to Substructure)"     | StructureSearchParams.Type.Substructure   | "n1cccc2ccccc12"                              | 0    | 10  | 10
        "Substructure, Skip 10, top 10" | StructureSearchParams.Type.Substructure   | "n1cccc2ccccc12"                              | 10   | 10  | 10

    }


}
class RESTTestHelper {
    /**
     * Assert that facets exists
     * @param serviceIterator
     */
    void assertFacets(final ServiceIterator<? extends Entity> serviceIterator) {
        assertFacets(serviceIterator.facets)
    }
    /**
     * Assert that facets exists
     * @param serviceIterator
     */
    void assertFacets(final Collection<Value> facets) {
        assert facets
        for (Value facet : facets) {
            assert facet.children()
        }
    }
    /**
     * Assert that there are no duplicate keys in facets (We skip blank and nulll keys those are tested #assertFacetIdsAreNonBlank)
     * @param serviceIterator
     */
    void assertFacetIdsAreUnique(final ServiceIterator<? extends Entity> serviceIterator) {

        //NOTE: We need to do this, otherwise the facets method returns 0
        while (serviceIterator.hasNext()) {
            serviceIterator.next();
        }
        final Collection<Value> facets = serviceIterator.facets
        assertFacetIdsAreUnique(facets)
    }
    /**
     * Assert that there are no duplicate keys in facets (We skip blank and nulll keys those are tested #assertFacetIdsAreNonBlank)
     * @param serviceIterator
     */
    void assertFacetIdsAreUnique(final Collection<Value> facets) {
        //we keep the parentFacet keys here to check for duplicates
        final Set<String> facetKeys = new HashSet<String>()
        for (Value parentFacet : facets) {
            final String parentFacetId = parentFacet.getId()
            if (parentFacetId) {
                assert !facetKeys.contains(parentFacetId)
                facetKeys.add(parentFacetId)
            }
            for (Iterator<Value> childIterator = parentFacet.children(); childIterator.hasNext();) {
                final Value childFacet = childIterator.next();
                final String childFacetId = childFacet.getId()
                if (childFacetId) {
                    String uniqueChildId = parentFacetId + "-" + childFacetId //Uniqueness should only be checked between facets with the same parent
                    assert !facetKeys.contains(uniqueChildId)
                    facetKeys.add(uniqueChildId)
                }
            }
        }
    }
}