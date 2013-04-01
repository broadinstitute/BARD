package bard.core.rest.spring

import bard.core.SearchParams
import bard.core.SuggestParams
import bard.core.exceptions.RestApiException
import bard.core.rest.helper.RESTTestHelper
import bard.core.rest.spring.assays.Assay
import bard.core.rest.spring.util.Counts
import bard.core.rest.spring.util.ETag
import bard.core.rest.spring.util.Facet
import bard.core.rest.spring.util.StructureSearchParams
import grails.plugin.spock.IntegrationSpec
import org.springframework.web.client.HttpClientErrorException
import spock.lang.Shared
import spock.lang.Unroll
import bard.core.rest.spring.compounds.*

/**
 * Tests for CompoundRestService in JDO
 */
@Mixin(RESTTestHelper)
@Unroll
class CompoundRestServiceIntegrationSpec extends IntegrationSpec {
    CompoundRestService compoundRestService
    @Shared
    List<Long> cids = [2722L, 5394L]

    void "searchCompoundsByCIDs #label"() {
        when:
        CompoundResult compoundResult = compoundRestService.searchCompoundsByCids(compoundIds, searchParams)
        then:
        assert (compoundResult != null) == expected
        where:
        label          | searchParams                       | compoundIds | expected
        "With cids"    | new SearchParams(skip: 0, top: 10) | cids        | true
        "With no cids" | new SearchParams(skip: 0, top: 10) | []          | false

    }


    void "searchCompoundsByCids withETags #label"() {
        given: "That we have made a request with some cids that returns an etag"
        CompoundResult compoundResultWithIds = compoundRestService.searchCompoundsByCids(compoundIds, searchParams)
        when: "We use the returned etags to make another request"
        List<Compound> compounds = compoundRestService.searchCompoundsByCids(searchParams, compoundResultWithIds?.etags)
        then: "We get back the expected results"
        assert (!compounds.isEmpty()) == expected
        where:
        label           | searchParams                       | compoundIds | expected
        "With ETags"    | new SearchParams(skip: 0, top: 10) | cids        | true
        "With no ETags" | new SearchParams(skip: 0, top: 10) | []          | false

    }

    void "test getSummaryForCompound"() {
        given:
        Long cid = 6019589
        when:
        CompoundSummary compoundSummary = compoundRestService.getSummaryForCompound(cid)
        then:
        assert compoundSummary
        assert compoundSummary.hitAssays
        assert compoundSummary.hitExptdata
        assert compoundSummary.nhit
        assert compoundSummary.ntest
        assert compoundSummary.testedAssays
        assert compoundSummary.testedExptdata
        assert compoundSummary.testedExptdata.resultData
    }

//    void "test retrieving assays from a compound #label"() {
//
//        when: "The get method is called with the given CID: #cid"
//        List<Assay> allAssaysForThisCompound = this.compoundRestService.getTestedAssays(cid, false)
//        for (Assay assay : allAssaysForThisCompound) {
//            assert assay
//        }
//        and:
//        Collection<Assay> activeAssaysForThisCompound = this.compoundRestService.getTestedAssays(cid, true)
//        for (Assay assay : activeAssaysForThisCompound) {
//            assert assay
//        }
//        then:
//        assert allAssaysForThisCompound.size() > activeAssaysForThisCompound.size()   // might not hold for all compounds, but it holds for these
//        where:
//        label                     | cid
//        "Find a compound 9660191" | new Long(9660191)
//    }

    void "getTested Assays"() {
        when:
        final List<Assay> assays = compoundRestService.getTestedAssays(3466563, true)
        then:
        assert assays
    }
    /**
     * Copied from RESTTestServices#testServices10
     */
    void testCompoundSuggestions() {
        given:
        final SuggestParams suggestParams = constructSuggestParams();
        when:
        final Map<String, List<String>> cs = compoundRestService.suggest(suggestParams);
        then:
        assertSuggestions(cs);
    }


    void "test getMultipleCompoundAnnotations"() {
        given:

        CompoundResult cmpds = compoundRestService.searchCompoundsByIds(cids)
        when:
        CompoundAnnotations compoundAnnotations = compoundRestService.findAnnotations(cmpds.compounds.get(0).cid)
        then:
        assert compoundAnnotations
        assert compoundAnnotations.anno_key.size() == compoundAnnotations.anno_val.size()


    }

    void "test findCompoundAnnotations"() {
        given:
        Compound compound = compoundRestService.getCompoundById(2722);
        when:
        CompoundAnnotations compoundAnnotations = compoundRestService.findAnnotations(compound.cid)
        then:
        assert compoundAnnotations
        assert compoundAnnotations.anno_key.size() == compoundAnnotations.anno_val.size()
    }


    void "test findAnnotations"() {
        given:
        final Long cid = 2722
        when:
        final CompoundAnnotations compoundAnnotations = compoundRestService.findAnnotations(cid)
        then:
        assert compoundAnnotations
        assert compoundAnnotations.anno_key.size() == compoundAnnotations.anno_val.size()
    }

    /**
     * Should take care of SOLR Exceptions reported to NCGC
     */
    void "testFiltersWithCompoundService with number ranges"() {
        given:
        String uriWithFilters = compoundRestService.getSearchResource() + "q=%22dna+repair%22&filter=fq(mwt:%5B100+TO+200%5D),&skip=0&top=10&expand=true"
        URI uri = new URI(uriWithFilters)
        when:
        CompoundResult compoundResult = (CompoundResult) this.compoundRestService.getForObject(uri, CompoundResult)
        then:
        assert compoundResult
        final List<Compound> compounds = compoundResult.compounds
        assert compounds, "CompoundService SearchResults must not be null"
        assert !compounds.isEmpty(), "CompoundService SearchResults must not be empty"
        assert compoundResult.numberOfHits > 0, "CompoundService SearchResults must have at least one element"
        final List<Facet> facets = compoundResult.getFacets();
        assert facets != null, "List of Facets is not null"
        assert !facets.isEmpty(), "List of Facets is not empty"

    }

    void testFiltersWithCompoundService() {
        given:
        final List<String[]> filters = new ArrayList<String[]>();
        filters.add(["tpsa", "55.1"] as String[])

        //construct Search Params
        final SearchParams searchParams = new SearchParams("dna repair", filters);

        when:
        CompoundResult compoundResult = this.compoundRestService.findCompoundsByFreeTextSearch(searchParams);
        then:
        final List<Compound> compounds = compoundResult.compounds
        assert compounds, "CompoundService SearchResults must not be null"
        assert !compounds.isEmpty(), "CompoundService SearchResults must not be empty"
        assert compoundResult.numberOfHits > 0, "CompoundService SearchResults must have at least one element"

        for (Compound compound : compounds) {
            assert compound.getIupacName(), "Compound  must have a name"
        }

        final List<Facet> facets = compoundResult.getFacets();
        assert facets != null, "List of Facets is not null"
        assert !facets.isEmpty(), "List of Facets is not empty"

    }

    void "test putETag"() {
        given:
        final String eTagName = "My awesome compound collection"
        final List<Long> moreCids = [1, 2, 3]
        final List<Long> cids = [
                3235555, 3235556,
                3235557, 3235558,
                3235559, 3235560,
                3235561, 3235562,
                3235563, 3235564,
                3235565, 3235566,
                3235567, 3235568,
                3235569, 3235570,
                3235571
        ]
        String etag = this.compoundRestService.newETag(eTagName, cids);
        when:
        // adding a few more id's to this etag
        int cnt = compoundRestService.putETag(etag, moreCids);
        then:
        assert cnt == 3
    }

    void "test create an ETag"() {
        given:
        final String eTagName = "My awesome compound collection"
        final List<Long> cids = [
                3235555, 3235556,
                3235557, 3235558,
                3235559, 3235560,
                3235561, 3235562,
                3235563, 3235564,
                3235565, 3235566,
                3235567, 3235568,
                3235569, 3235570,
                3235571
        ]
        when:
        Object etag = this.compoundRestService.newETag(eTagName, cids);
        then:
        assert etag
    }

    void "test Get Compounds, #label"() {
        when: "We call the get method of the the RESTCompoundService"
        CompoundResult compoundResult = this.compoundRestService.searchCompoundsByIds(cids)
        then: "We expect to get back a list of 10 results"
        assert compoundResult.numberOfHits == cids.size()
        assert compoundResult.etag
        assert compoundResult.etags
        assertCompounds(compoundResult.compounds)

        where:
        label                         | cids
        "Search with a list of CIDs"  | [2722, 119, 137, 185, 187, 190, 191, 224, 240, 499]
        "Search with a single of CID" | [2722]

    }
    /**
     * Do a search for gobp_term:"Ras protein signal transduction"
     *  Compounds and Projects return "-1" as the totals.
     */
    void "search 'gobp_term:Ras protein signal transduction' has problems bug: https://www.pivotaltracker.com/story/show/36644851"() {
        given:
        final SearchParams searchParams = new SearchParams("Ras protein signal transduction")
        searchParams.setSkip(0)
        searchParams.setTop(10);
        List<String[]> filters = []
        filters.add(["gobp_term", "Ras protein signal transduction"] as String[])
        searchParams.filters = filters
        when: "We execute the search"
        final CompoundResult searchResult = this.compoundRestService.findCompoundsByFreeTextSearch(searchParams)
        then: "We expect the following"
        assert searchResult.numberOfHits > 0
    }

    void "test REST Compound Service dna repair and look at collections returned, bug: https://www.pivotaltracker.com/story/show/36349429"() {
        given: "That we can construct a valid search parameter"
        final SearchParams params = new SearchParams(searchString)
        params.setSkip(skip)
        params.setTop(top);
        when: "We we call search method of the the RESTCompoundService"
        final CompoundResult searchResult = this.compoundRestService.findCompoundsByFreeTextSearch(params)
        then: "We expected that the number of results should not exceed the number of counts on the Collection Filter(Approved drugs)"

        final List<Facet> facets = searchResult.facets
        int numberOfCountsOnApprovedDrugFilter = 0
        for (Facet facet : facets) {
            if (facet.facetName == 'COLLECTION') {
                final Counts counts = facet.counts
                final Map<String, Object> facetValues = counts.getAdditionalProperties()
                if (facetValues.containsKey('Approved drugs')) {
                    numberOfCountsOnApprovedDrugFilter = facetValues.get('Approved drugs') as Integer
                    break;
                }
            }

        }
        final int numberSearchResults = searchResult.numberOfHits
        assert numberSearchResults > numberOfCountsOnApprovedDrugFilter, "We found the number of search results to be ${numberSearchResults} and the number of filters with 'Approved Drugs' to be ${numberOfCountsOnApprovedDrugFilter} "
        where:
        label    | searchString | skip | top
        "Search" | "dna repair" | 0    | 10
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
        final CompoundResult structureSearch = this.compoundRestService.structureSearch(structureSearchParams)
        then: "The following assertions are true"
        List<Compound> compounds = structureSearch.compounds
        assertCompounds(compounds)
        assert structureSearch.numberOfHits >= expectedNumberOfCompounds
        assert expectedNumberOfCompounds == compounds.size()
        assert !structureSearch.facets
        where:
        label                            | structureSearchParamsType                 | smiles                                        | skip | top | expectedNumberOfCompounds
        "Super Structure search"         | StructureSearchParams.Type.Superstructure | "O=S(*C)(Cc1ccc2ncc(CCNC)c2c1)=O"             | 0    | 10  | 3
        "Super Structure, skip 1 top 10" | StructureSearchParams.Type.Superstructure | "O=S(*C)(Cc1ccc2ncc(CCNC)c2c1)=O"             | 1    | 10  | 2
        "Similarity Structure Search"    | StructureSearchParams.Type.Similarity     | "CN(C)CCC1=CNC2=C1C=C(CS(=O)(=O)N3CCCC3)C=C2" | 0    | 10  | 1
        "Exact Structure Search"         | StructureSearchParams.Type.Exact          | "CN(C)CCC1=CNC2=C1C=C(CS(=O)(=O)N3CCCC3)C=C2" | 0    | 10  | 1
        "Sub Structure Search"           | StructureSearchParams.Type.Substructure   | "CN(C)CCC1=CNC2=C1C=C(CS(=O)(=O)N3CCCC3)C=C2" | 0    | 10  | 1
        "Default (to Substructure)"      | StructureSearchParams.Type.Substructure   | "C1CSSC1CCCCC(=O)N"                           | 0    | 10  | 1
    }

    void "test SubStructure Search with brackets in smiles: #label"() {
        given: "Structure Paramaters with top #top  and skip #skip"
        StructureSearchParams structureSearchParams =
            new StructureSearchParams(smiles)
        structureSearchParams.setSkip(skip)
        structureSearchParams.setTop(top);
        when: "We call the search method of the Compound Service"
        final CompoundResult compoundResult = this.compoundRestService.structureSearch(structureSearchParams)
        then: "The following assertions are true"
        List<Compound> compoundTemplates = compoundResult.compounds
        assert !compoundTemplates.isEmpty()
        assert compoundResult.etags
        where:
        label                  | structureSearchParamsType               | smiles                                                                   | skip | top | expectedNumberOfCompounds
        "Sub Structure Search" | StructureSearchParams.Type.Substructure | "[H]C1([H])CN(C([H])([H])C([H])([H])C1([H])[H])S(=O)(=O)C1=CC=C(NC)C=C1" | 0    | 2   | 1
    }
    /**
     *
     */
    void "test Get Promiscuity Score No Score #label"() {

        when: "The getPromiscuity method is called with the CID: #cid"
        this.compoundRestService.findPromiscuityScoreForCompound(cid)
        then: "A Compound is returned with the expected information"
        thrown(HttpClientErrorException)
        where:
        label                             | cid      | scaffoldSize
        "A CID with no Promiscuity Score" | 16760208 | 0
    }
    /**
     *
     */
    void "test Get Promiscuity No Score #label"() {

        when: "The getPromiscuity method is called with the CID: #cid"
        this.compoundRestService.findPromiscuityForCompound(cid)
        then: "A Compound is returned with the expected information"
        thrown(HttpClientErrorException)
        where:
        label                             | cid      | scaffoldSize
        "A CID with no Promiscuity Score" | 16760208 | 0
    }

    void "test findPromiscuityScore #label"() {

        when: "The getPromiscuity method is called with the CID: #cid"
        final PromiscuityScore promiscuityScore = this.compoundRestService.findPromiscuityScoreForCompound(cid)
        then: "A Compound is returned with the expected information"
        assert promiscuityScore
        assert promiscuityScore.cid == cid
        final List<Scaffold> scaffolds = promiscuityScore.scaffolds
        assert scaffolds
        assert scaffolds.size() == scaffoldSize
        Scaffold scaffold = scaffolds.get(0)
        assert scaffold.scafid == 53
        assert scaffold.pScore == 456.0
        assert scaffold.scafsmi == "c1ccc2cccnc2c1"
        assert scaffold.sTested
        assert scaffold.sActive
        assert scaffold.aTested
        assert scaffold.aActive
        assert scaffold.wTested
        assert scaffold.inDrug

        where:
        label                            | cid  | scaffoldSize
        "A CID With A Promiscuity Score" | 2722 | 1
    }

    void "test findPromiscuity #label"() {

        when: "The getPromiscuity method is called with the CID: #cid"
        final Promiscuity promiscuity = this.compoundRestService.findPromiscuityForCompound(cid)
        then: "A Compound is returned with the expected information"
        assert promiscuity
        assert promiscuity.cid == cid
        final List<PromiscuityScaffold> scaffolds = promiscuity.promiscuityScaffolds
        assert scaffolds
        assert scaffolds.size() == scaffoldSize
        PromiscuityScaffold scaffold = scaffolds.get(0)
        assert scaffold.scaffoldId == 53
        assert scaffold.promiscuityScore == 456.0
        assert scaffold.smiles == "c1ccc2cccnc2c1"
        assert scaffold.testedSubstances
        assert scaffold.activeSubstances
        assert scaffold.testedAssays
        assert scaffold.activeAssays
        assert scaffold.testedWells
        assert scaffold.activeWells
        assert scaffold.inDrug

        where:
        label                      | cid  | scaffoldSize
        "A CID With A Promiscuity" | 2722 | 1
    }
    /**
     *
     */
    void "test Fail, CID does not exists: #label"() {
        when: "The get method is called with the given CID: #cid"
        this.compoundRestService.getCompoundById(cid)
        then: "No Compound is returned with the expected information"
        thrown(RestApiException)
        where:
        label                          | cid
        "Find a non-existing compound" | new Integer(-658342)
    }
    /**
     *
     */
    void "test REST Compound Service #label #searchString DNA repair question"() {
        given: "A search string, #searchString, and asking to retrieve the first #top search results"
        final SearchParams params = new SearchParams(searchString)
        params.setSkip(skip)
        params.setTop(top);
        when: "We we call search method of the the RESTCompoundService"
        final CompoundResult compoundResult = this.compoundRestService.findCompoundsByFreeTextSearch(params)
        then: "We expected to get back a list of 10 results"
        List<Compound> compoundTemplates = compoundResult.compounds
        assert !compoundTemplates.isEmpty()
        assert compoundResult.numberOfHits >= expectedNumberOfCompounds
        assert expectedNumberOfCompounds == compoundTemplates.size()
        assertCompounds(compoundTemplates)
        assertFacets(compoundResult.facets)
        where:
        label    | searchString | skip | top | expectedNumberOfCompounds
        "Search" | "dna repair" | 0    | 10  | 10
        "Search" | "dna repair" | 10   | 10  | 10
    }
    /**
     *
     */
    void "test Get Compound which is #label"() {

        when: "The get method is called with the drug's CID: #cid"
        final Compound foundCompound = this.compoundRestService.getCompoundById(cid)
        then: "A Compound is returned with the expected information"
        assert foundCompound
        assert foundCompound.cid == cid
        assert foundCompound.isDrug() == isDrug
        assert foundCompound.isProbe() == isProbe
        where:
        label                 | cid     | isDrug | isProbe
        "An existing Drug"    | 2722    | true   | false
        "An existing Probe"   | 9795907 | false  | true
        "Not a Drug or Probe" | 666     | false  | false
    }
    /**
     *
     */
    //After the change inb V15 non of the ML Probe has annotaiotns. When that changes, we can uncomment the asserts below.
    void "test Compound With Probe Annotations #label"() {

        when:
        final Compound foundCompound = this.compoundRestService.getCompoundById(cid)
        then:
        assert foundCompound
        assert foundCompound.cid == cid
        assert foundCompound.isProbe()
        final List<ProbeAnnotation> probeAnnotations = foundCompound.getProbeAnnotations()
//        assert !probeAnnotations.isEmpty()
        final ProbeAnnotation probe = foundCompound.getProbe()
//        assert probe
//        assert foundCompound.getProbeCid()
//        assert foundCompound.getProbeSid()


        where:
        label               | cid
        "An existing Probe" | 3236979
    }

    void "getSynonyms #label"() {
        when:
        List<String> synonyms = this.compoundRestService.getSynonymsForCompound(cid)
        then:
        assert !synonyms.isEmpty()
        where:
        label                    | cid
        "Compound with CID 2722" | new Integer(2722)

    }

    void "test findCompoundByFreeTextSearch"() {
        given:
        SearchParams searchParams = new SearchParams("\"dna repair\"");
        searchParams.setSkip(new Long(0));
        searchParams.setTop(new Long(10));

        when:
        final CompoundResult compoundsByFreeTextSearch = this.compoundRestService.findCompoundsByFreeTextSearch(searchParams)
        then:
        assert compoundsByFreeTextSearch
        compoundsByFreeTextSearch.numberOfHits == 6
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
        final CompoundResult compoundsByFreeTextSearch = this.compoundRestService.findCompoundsByFreeTextSearch(params)
        then:
        assert compoundsByFreeTextSearch
        final Compound compoundTemplate = compoundsByFreeTextSearch.compounds.get(0)
        assert compoundTemplate.isDrug() == isDrug
        assert compoundTemplate.isProbe() == isProbe
        where:
        label                      | searchString      | skip | top | isDrug | isProbe
        "Search for Approved Drug" | "\"Leflunomide\"" | 0    | 2   | true   | false
    }
    /**
     *
     */
    void "test REST Compound Service #label #searchString with paging"() {
        given: "A search string of 'dna repair' and two Search Params, Params 1 has skip=0 and top=10, params2 has skip=10 and top=10"
        final String searchString = "dna repair"
        SearchParams params1 = new SearchParams(searchString)
        params1.setSkip(0)
        params1.setTop(10);

        SearchParams params2 = new SearchParams(searchString)
        params2.setSkip(10)
        params2.setTop(10);

        when: "We call the search method of RestCompoundService with params1"
        final CompoundResult compoundsByFreeTextSearch1 = this.compoundRestService.findCompoundsByFreeTextSearch(params1)
        and: "params2"
        final CompoundResult compoundsByFreeTextSearch2 = this.compoundRestService.findCompoundsByFreeTextSearch(params2)


        then: "We expect that the list of ids returned for each search, would be different"
        assert !compoundsByFreeTextSearch2.compounds.isEmpty()
        assert !compoundsByFreeTextSearch1.compounds.isEmpty()
        Collection<Long> compoundIds2 = compoundsByFreeTextSearch2.compounds.collect { Compound compound -> compound.cid as Long }
        Collection<Long> compoundIds1 = compoundsByFreeTextSearch1.compounds.collect { Compound compound -> compound.cid as Long }
        assert compoundIds1 != compoundIds2
    }
    /**
     *
     */
    void "test Facet keys (ids) are unique"() {
        given: "That we have created a valid search params object with a search string of 'dna repair', skip=0 and top=10"
        final SearchParams params = new SearchParams("dna repair")
        params.setSkip(0)
        params.setTop(10);
        when: "We we call the search method of the the RESTCompoundService with the params object"
        final CompoundResult compoundsByFreeTextSearch = this.compoundRestService.findCompoundsByFreeTextSearch(params)
        then: "We expect to get back unique facets"
        assertFacetIdsAreUnique(compoundsByFreeTextSearch)
    }


    void "test Get a Single Compound #label"() {
        when: "The get method is called with the given CID: #cid"
        final Compound compound = this.compoundRestService.getCompoundById(cid)
        then: "A Compound is returned with the expected information"
        assert compound.smiles == expectedSmiles
        assert compound.cid == cid
        assert compound.compoundAnnotations
        assert compound.compoundAnnotations.compoundCollection
        where:
        label                       | cid               | expectedSmiles
        "Find an existing compound" | new Integer(2722) | "OC1=C(Cl)C=C(Cl)C2=C1N=CC=C2"
    }

    void "findAllETagsForResource"() {
        when:
        final List<ETag> eTags = compoundRestService.findAllETagsForResource()
        then:
        assert eTags
    }

    void "test build #label"() {
        given:
        StructureSearchParams params = new StructureSearchParams(smiles, structureType)
        params.skip = skip
        params.top = top
        when:
        final CompoundResult compoundResult = compoundRestService.structureSearch(params)
        then:
        assert compoundResult.numberOfHits >= expectedSize
        assert compoundResult.etags
        where:
        label                                | skip | top  | expectedSize | structureType                           | smiles
        "With Skip and Top"                  | 0    | 5    | 5            | StructureSearchParams.Type.Substructure | "n1cccc2ccccc12"
        "Skip and Top both null"             | null | null | 1            | StructureSearchParams.Type.Exact        | "n1cccc2ccccc12"
        "Skip and Top both , does not exist" | null | null | 0            | StructureSearchParams.Type.Exact        | "c"
    }


}