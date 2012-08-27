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

class RESTCompoundServiceIntegrationSpec extends IntegrationSpec {
    EntityServiceManager esm
    CompoundService compoundService
    final static String baseURL = "http://bard.nih.gov/api/v1"

    @Before
    void setup() {
        this.esm = new RESTEntityServiceManager(baseURL);
        this.compoundService = esm.getService(Compound.class);


    }

    @After
    void tearDown() {
        this.esm.shutdown()
    }

    void assertCompoundAdapter(CompoundAdapter compoundAdapter) {
        //TODO: only assert required fields
        assert compoundAdapter.pubChemCID
        assert compoundAdapter.pubChemSIDs
        assert compoundAdapter.structureSMILES
        assert compoundAdapter.exactMass()
        assert compoundAdapter.formula()
    }
    /**
     * TODO: Find out why no annotations
     */
    void "test Get a Single Compound #label"() {

        when: "The get method is called with the given CID: #cid"
        final Compound compound = this.compoundService.get(cid)
        final CompoundAdapter compoundAdapter = new CompoundAdapter(compound)
        then: "A Compound is returned with the expected information"
        assert compoundAdapter
        assertCompoundAdapter(compoundAdapter)
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
     * TODO: Find out why no annotations
     */
    void "test Fail, Get a Single #label"() {

        when: "The get method is called with the given CID: #cid"
        final Compound compound = this.compoundService.get(cid)
        then: "No Compound is returned with the expected information"
        assert !compound
        where:
        label                          | cid
        "Find a non-existing compound" | new Integer(-658342)
    }
    /**
     * TODO: Ask NCGC that this should include a highlight option even if it is a default String
     * TODO: Ask Steve, do we need facet information?
     * TODO: What is the maximum number of ids that one can send?
     */
    void "test Get Compounds, #label"() {
        when: "We call the get method of the the RESTCompoundService"
        final Collection<Compound> compounds = this.compoundService.get(cids)
        then: "We expect to get back a list of 10 results"
        for (Compound compound : compounds) {
            final CompoundAdapter compoundAdapter = new CompoundAdapter(compound)
            assertCompoundAdapter(compoundAdapter)
        }
        assert cids.size() == compounds.size()
        where:
        label                         | cids
        "Search with a list of CIDs"  | [3235555, 3235556, 3235557, 3235558, 3235559, 3235560, 3235561, 3235562, 3235563, 3235564]
        "Search with a single of CID" | [3235555]

    }
    /**
     * TODO: Ask NCGC that this search should return the same thing as the REST API
     */
    void "test REST Compound Service #label #seachString question"() {
        given: "A search string, #searchString, and asking to retrieve the first #top search results"
        final SearchParams params = new SearchParams(searchString).setSkip(skip).setTop(top);
        when: "We we call search method of the the RESTCompoundService"
        final ServiceIterator<Compound> searchIterator = this.compoundService.search(params)
        then: "We expected to get back a list of 10 results"
        int numberOfCompounds = 0
        while (searchIterator.hasNext()) {
            final Compound compound = searchIterator.next();
            final CompoundAdapter compoundAdapter = new CompoundAdapter(compound)
            assertCompoundAdapter(compoundAdapter)
            assert compoundAdapter.annotations
            ++numberOfCompounds
        }
        assert expectedNumberOfCompounds == numberOfCompounds

        searchIterator.done();
        where:
        label    | searchString | skip | top | expectedNumberOfCompounds
        "Search" | "dna repair" | 0    | 10  | 10

    }

/**
 * Do structure searches
 */
    void "test #label"() {
        given:
        final StructureSearchParams structureSearchParams =
            new StructureSearchParams(smiles)
        structureSearchParams.setSkip(skip).setTop(top);
        structureSearchParams.setThreshold(0.9)
        when: ""
        final ServiceIterator<Compound> searchIterator = this.compoundService.search(structureSearchParams)
        then:
        int numberOfCompounds = 0
        while (searchIterator.hasNext()) {
            final Compound compound = searchIterator.next();
            final CompoundAdapter compoundAdapter = new CompoundAdapter(compound)
            assertCompoundAdapter(compoundAdapter)
            ++numberOfCompounds
        }
        assert expectedNumberOfCompounds == numberOfCompounds
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