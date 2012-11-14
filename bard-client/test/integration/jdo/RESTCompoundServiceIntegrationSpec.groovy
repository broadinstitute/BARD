package jdo

//import spock.lang.Timeout


import bard.core.adapter.CompoundAdapter
import bard.core.interfaces.SearchResult
import org.junit.Assert
import spock.lang.Unroll
import bard.core.*

/**
 * Tests for RESTCompoundService in JDO
 */
@Mixin(jdo.helper.RESTTestHelper)
@Unroll
class RESTCompoundServiceIntegrationSpec extends AbstractRESTServiceSpec {

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

        final Collection<Value> annotations = compoundAdapter.getAnnotations()
        assert annotations
        assert !annotations.isEmpty()

        if (isStringSearch) {
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
            assert compoundAdapter.TPSA()
            List<String> synonyms = compoundAdapter.getSynonyms() as List<String>
            assert synonyms != null

        }

    }
    /**
     *
     */
    void "test Get Promiscuity Score No Score #label"() {
        when: "The getPromiscuity method is called with the CID: #cid"
        final PromiscuityScore promiscuityScore = this.restCompoundService.getPromiscuityScore(cid)
        then: "A Compound is returned with the expected information"
        assert !promiscuityScore
        where:
        label                             | cid      | scaffoldSize
        "A CID with no Promiscuity Score" | 16760208 | 0
    }
    /**
     *
     */
    void "test Get Promiscuity Score #label"() {
        when: "The getPromiscuity method is called with the CID: #cid"
        final PromiscuityScore promiscuityScore = this.restCompoundService.getPromiscuityScore(cid)
        then: "A Compound is returned with the expected information"
        assert promiscuityScore
        assert promiscuityScore.cid == cid
        assert promiscuityScore.scaffolds
        assert promiscuityScore.scaffolds.size() == scaffoldSize
        where:
        label                            | cid  | scaffoldSize
        "A CID With A Promiscuity Score" | 2722 | 1
    }

    /**
     *
     */
    void "test Get Compound which is #label"() {

        when: "The get method is called with the drug's CID: #cid"
        final Compound compound = this.restCompoundService.get(cid)
        then: "A Compound is returned with the expected information"
        assert compound
        final CompoundAdapter compoundAdapter = new CompoundAdapter(compound)
        assert cid == compoundAdapter.pubChemCID
        assert compoundAdapter.isDrug() == isDrug
        assert compoundAdapter.isProbe() == isProbe
        where:
        label                 | cid     | isDrug | isProbe
        "An existing Drug"    | 2722    | true   | false
        "An existing Probe"   | 9795907 | false  | true
        "Not a Drug or Probe" | 666     | false  | false
    }
    /**
     *
     */
    void "test Get a Single Compound #label"() {

        when: "The get method is called with the given CID: #cid"
        final Compound compound = this.restCompoundService.get(cid)
        then: "A Compound is returned with the expected information"
        assertCompound(compound)
        final CompoundAdapter compoundAdapter = new CompoundAdapter(compound)
        assert cid == compoundAdapter.pubChemCID
        assert expectedSmiles == compoundAdapter.structureSMILES
        Long[] sids = compoundAdapter.pubChemSIDs
        assert sids.length
        where:
        label                       | cid               | expectedSmiles
        "Find an existing compound" | new Integer(2722) | "OC1=C(Cl)C=C(Cl)C2=C1N=CC=C2"
    }

    void "test retrieving assays from a compound #label"() {

        when: "The get method is called with the given CID: #cid"
        final Compound compound = this.restCompoundService.get(cid)
        then: "A Compound is returned with the expected information"
        Collection<Assay> allAssaysForThisCompound = this.restCompoundService.getTestedAssays(compound, false)
        for (Assay assay : allAssaysForThisCompound) {
            Assert.assertNotNull assay
        }
        Collection<Assay> activeAssaysForThisCompound = this.restCompoundService.getTestedAssays(compound, true)
        for (Assay assay : activeAssaysForThisCompound) {
            Assert.assertNotNull assay
        }
        assert allAssaysForThisCompound.size() > activeAssaysForThisCompound.size()   // might not hold for all compounds, but it holds for these
        where:
        label                     | cid
        "Find a compound 313619"  | new Long(313619)
        "Find a compound 9660191" | new Long(9660191)
    }

    /**
     *
     */
    void "test Fail, CID does not exists: #label"() {

        when: "The get method is called with the given CID: #cid"
        final Compound compound = this.restCompoundService.get(cid)
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
        final Collection<Compound> compounds = this.restCompoundService.get(cids)
        then: "We expect to get back a list of 10 results"
        assertCompounds(compounds, false)
        assert cids.size() == compounds.size()
        where:
        label                         | cids
        "Search with a list of CIDs"  | [2722, 119, 137, 185, 187, 190, 191, 224, 240, 499]
        "Search with a single of CID" | [2722]

    }
    /**
     *
     */
    void "test Get Compounds with facets, #label"() {
        given:
        Object etag = restCompoundService.newETag("My awesome compound collection", cids);
        when: "We call the getFactest matehod"
        Collection<Value> facets = this.restCompoundService.getFacets(etag)
        and:
        Collection<Compound> compounds = this.restCompoundService.get(cids)
        then: "We expect to get back a list of facets"
        assert facets
        assertFacetIdsAreUnique(facets)
        assertCompounds(compounds, false)
        where:
        label                         | cids
        "Search with a list of CIDs"  | [2722, 119, 137, 185, 187, 190, 191, 224, 240, 499]
        "Search with a single of CID" | [2722]

    }

    /**
     *
     */
    void "test REST Compound Service #label #seachString DNA repair question"() {
        given: "A search string, #searchString, and asking to retrieve the first #top search results"
        final SearchParams params = new SearchParams(searchString)
        params.setSkip(skip)
        params.setTop(top);
        when: "We we call search method of the the RESTCompoundService"
        final SearchResult<Compound> searchResult = this.restCompoundService.search(params)
        then: "We expected to get back a list of 10 results"
        Collection<Compound> compounds = searchResult.searchResults
        assert !compounds.isEmpty()

        assertCompounds(compounds, true)
        assert searchResult.count >= expectedNumberOfCompounds
        assert expectedNumberOfCompounds == compounds.size()
        assertFacets(searchResult.facets)
        where:
        label    | searchString | skip | top | expectedNumberOfCompounds
        "Search" | "dna repair" | 0    | 10  | 10
        "Search" | "dna repair" | 10   | 10  | 10
    }
    /**
     *
     */
    void "test REST Compound Service  #label"() {
        given: "That we can construct a valid search parameter"
        final SearchParams params = new SearchParams(searchString)
        params.setSkip(skip)
        params.setTop(top);
        when: "We we call search method of the the RESTCompoundService"
        final SearchResult<Compound> searchResult = this.restCompoundService.search(params)
        then:
        final List<Compound> compounds = searchResult.next(1)
        Compound compound = compounds.get(0)
        CompoundAdapter compoundAdapter = new CompoundAdapter(compound)
        assert compoundAdapter.isDrug() == isDrug
        assert compoundAdapter.isProbe() == isProbe
        where:
        label                      | searchString     | skip | top | isDrug | isProbe
        "Search for Approved Drug" | "\"Chloroxine\"" | 0    | 2   | true   | false
    }
    /**
     *  TODO: Uncomment when latest is released
     */
//    void "test REST Compound Service dna repair and look at collections returned, bug: https://www.pivotaltracker.com/story/show/36349429"() {
//        given: "That we can construct a valid search parameter"
//        final SearchParams params = new SearchParams(searchString)
//        params.setSkip(skip)
//        params.setTop(top);
//        when: "We we call search method of the the RESTCompoundService"
//        final SearchResult<Compound> searchResult = this.restCompoundService.search(params)
//        then: "We expected that the number of results should not exceed the number of counts on the Collection Filter(Approved drugs)"
//
//        final Collection<Value> facets = searchResult.facets
//        int numberOfCountsOnApprovedDrugFilter = 0
//        for (Value value : facets) {
//            if (value.id == 'COLLECTION') {
//                final Value child = value.getChild("Approved drugs")
//                numberOfCountsOnApprovedDrugFilter = child.value as Integer
//                break;
//            }
//        }
//        int numberSearchResults = searchResult.count
//        println "We found the number of search results to be ${numberSearchResults} and the number of filters with 'Approved Drugs' to be ${numberOfCountsOnApprovedDrugFilter} "
//        assert numberSearchResults > numberOfCountsOnApprovedDrugFilter
//        where:
//        label    | searchString | skip | top
//        "Search" | "dna repair" | 0    | 10
//    }
    /**
     * Do a search for gobp_term:"Ras protein signal transduction"
     *  Compounds and Projects return "-1" as the totals.
     */
    void "use auto-suggest 'gobp_term:Ras protein signal transduction' has problems bug: https://www.pivotaltracker.com/story/show/36644851"() {
        given:
        final SearchParams searchParams = new SearchParams("Ras protein signal transduction")
        searchParams.setSkip(0)
        searchParams.setTop(10);
        List<String[]> filters = []
        filters.add(["gobp_term", "Ras protein signal transduction"] as String[])
        searchParams.filters = filters
        when: "We execute the search"
        final SearchResult<Compound> searchResult = this.restCompoundService.search(searchParams)
        then: "We expect the following"
        assert searchResult.count > 0
    }
    /**
     *
     */
    void "test REST Compound Service #label #seachString with paging"() {
        given: "A search string of 'dna repair' and two Search Params, Params 1 has skip=0 and top=10, params2 has skip=10 and top=10"
        final String searchString = "dna repair"
        SearchParams params1 = new SearchParams(searchString)
        params1.setSkip(0)
        params1.setTop(10);

        SearchParams params2 = new SearchParams(searchString)
        params2.setSkip(10)
        params2.setTop(10);

        when: "We call the search method of RestCompoundService with params1"
        final SearchResult<Compound> searchResult1 = this.restCompoundService.search(params1)
        and: "params2"
        final SearchResult<Compound> searchResult2 = this.restCompoundService.search(params2)


        then: "We expect that the list of ids returned for each search, would be different"
        assert !searchResult2.searchResults.isEmpty()
        assert !searchResult1.searchResults.isEmpty()
        Collection<Long> compoundIds2 = searchResult2.searchResults.collect { Compound compound -> compound.id as Long }
        Collection<Long> compoundIds1 = searchResult1.searchResults.collect { Compound compound -> compound.id as Long }
        assert compoundIds1 != compoundIds2
    }
    /**
     */
    void "test Facet keys (ids) are unique"() {
        given: "That we have created a valid search params object with a search string of 'dna repair', skip=0 and top=10"
        final SearchParams params = new SearchParams("dna repair")
        params.setSkip(0)
        params.setTop(10);
        when: "We we call the search method of the the RESTCompoundService with the params object"
        final SearchResult<Compound> searchResult = this.restCompoundService.search(params)
        then: "We expect to get back unique facets"
        assertFacetIdsAreUnique(searchResult)
    }
    /**
     * Do sub structure searches
     */
    void "test SubStructure Search with brackets in smiles: #label"() {
        given: "Structure Paramaters with top #top  and skip #skip"
        final StructureSearchParams structureSearchParams =
            new StructureSearchParams(smiles)
        structureSearchParams.setSkip(skip).setTop(top);
        when: "We call the search method of the Compound Service"
        final SearchResult<Compound> searchResult = this.restCompoundService.search(structureSearchParams)
        then: "The following assertions are true"
        assert searchResult.count > 0
        where:
        label                  | structureSearchParamsType               | smiles                                                                   | skip | top | expectedNumberOfCompounds
        "Sub Structure Search" | StructureSearchParams.Type.Substructure | "[H]C1([H])CN(C([H])([H])C([H])([H])C1([H])[H])S(=O)(=O)C1=CC=C(NC)C=C1" | 0    | 2   | 1
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
        final SearchResult<Compound> searchResult = this.restCompoundService.search(structureSearchParams)
        then: "The following assertions are true"
        Collection<Compound> compounds = searchResult.searchResults
        assertCompounds(compounds, false)
        final Collection<Value> facets = searchResult.facets
        assert searchResult.getCount() >= expectedNumberOfCompounds
        assert expectedNumberOfCompounds == compounds.size()
        assertFacets(facets)
        assertFacetIdsAreUnique(facets)
        where:
        label                            | structureSearchParamsType                 | smiles                                        | skip | top | expectedNumberOfCompounds
        "Super Structure search"         | StructureSearchParams.Type.Superstructure | "O=S(*C)(Cc1ccc2ncc(CCNC)c2c1)=O"             | 0    | 10  | 3
        "Super Structure, skip 1 top 10" | StructureSearchParams.Type.Superstructure | "O=S(*C)(Cc1ccc2ncc(CCNC)c2c1)=O"             | 1    | 10  | 2
        "Similarity Structure Search"    | StructureSearchParams.Type.Similarity     | "CN(C)CCC1=CNC2=C1C=C(CS(=O)(=O)N3CCCC3)C=C2" | 0    | 10  | 1
        "Exact Structure Search"         | StructureSearchParams.Type.Exact          | "CN(C)CCC1=CNC2=C1C=C(CS(=O)(=O)N3CCCC3)C=C2" | 0    | 10  | 1
        "Sub Structure Search"           | StructureSearchParams.Type.Substructure   | "CN(C)CCC1=CNC2=C1C=C(CS(=O)(=O)N3CCCC3)C=C2" | 0    | 10  | 1
        "Default (to Substructure)"      | StructureSearchParams.Type.Substructure   | "C1CSSC1CCCCC(=O)N"                           | 0    | 10  | 1
    }
}
