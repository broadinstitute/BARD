package jdo

import bard.core.rest.RESTAssayService
import bard.core.rest.RESTCompoundService
import bard.core.rest.RESTExperimentService
import bard.core.rest.RESTProjectService
import com.fasterxml.jackson.databind.ObjectMapper
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll
import bard.core.*

/**
 * Created with IntelliJ IDEA.
 * User: jasiedu
 * Date: 11/1/12
 * Time: 8:26 PM
 * To change this template use File | Settings | File Templates.
 */
@Unroll
class RESTAbstractEntityServiceUnitSpec extends Specification {

    @Shared List<String[]> filters = [["gobp_term", "DNA repair"] as String[]]
    @Shared ObjectMapper mapper = new ObjectMapper()
    @Shared String JSON_COUNTS = JSONNodeTestHelper.JSON_COUNTS
    @Shared String FACETS_ONLY = JSONNodeTestHelper.FACETS_ONLY
    @Shared String JSON_NODE_FULL_ETAG = JSONNodeTestHelper.JSON_NODE_FULL_ETAG
    @Shared String JSON_NODE_ONLY_ETAG_ID = JSONNodeTestHelper.JSON_NODE_ONLY_ETAG_ID
    @Shared String JSON_NODE_NO_ETAG_ID = JSONNodeTestHelper.JSON_NODE_NO_ETAG_ID
    @Shared String JSON_NODE_NO_FACETS = JSONNodeTestHelper.JSON_NODE_NO_FACETS
    @Shared String JSON_NODE_WITH_FACETS = JSONNodeTestHelper.JSON_NODE_WITH_FACETS
    @Shared String JSON_NODE_NO_HITS = JSONNodeTestHelper.JSON_NODE_NO_HITS
    @Shared String JSON_NODE_NO_META_DATA = JSONNodeTestHelper.JSON_NODE_NO_META_DATA
    @Shared String JSON_FACET = JSONNodeTestHelper.JSON_FACET
    @Shared String JSON_FACET_ZERO_COUNT_VALUE = JSONNodeTestHelper.JSON_FACET_ZERO_COUNT_VALUE
    @Shared String JSON_FACET_NO_COUNT_KEY = JSONNodeTestHelper.JSON_FACET_NO_COUNT_KEY
    @Shared String ASSAY_EXPANDED_SEARCH_RESULTS = JSONNodeTestHelper.ASSAY_EXPANDED_SEARCH_RESULTS
    @Shared String COMPOUND_EXPANDED_SEARCH_RESULTS = JSONNodeTestHelper.COMPOUND_EXPANDED_SEARCH_RESULTS
    @Shared String PROJECT_EXPANDED_SEARCH_RESULTS = JSONNodeTestHelper.PROJECT_EXPANDED_SEARCH_RESULTS
    @Shared String EXPERIMENT_EXPANDED_SEARCH_RESULTS = JSONNodeTestHelper.EXPERIMENT_EXPANDED_SEARCH_RESULTS
    @Shared String PROJECT_SEARCH_RESULTS = JSONNodeTestHelper.PROJECT_SEARCH_RESULTS
    @Shared String ASSAY_SEARCH_RESULTS = JSONNodeTestHelper.ASSAY_SEARCH_RESULTS
    @Shared String COMPOUND_SEARCH_RESULTS = JSONNodeTestHelper.COMPOUND_SEARCH_RESULTS
    @Shared String SUGGEST_PAIR = JSONNodeTestHelper.SUGGEST_PAIR
    @Shared RESTCompoundService restCompoundService = new RESTCompoundService(null, "http://bard.nih.gov/api/v7")
    @Shared RESTProjectService restProjectService = new RESTProjectService(null, "http://bard.nih.gov/api/v7")
    @Shared RESTAssayService restAssayService = new RESTAssayService(null, "http://bard.nih.gov/api/v7")
    @Shared RESTExperimentService restExperimentService = new RESTExperimentService(null, "http://bard.nih.gov/api/v7")

    void "test toString #label"() {
        when:
        String stringRep = restCompoundService.toString(ids)
        then:
        assert stringRep == expectedValue
        where:
        label              | ids                                  | expectedValue
        "Entities"         | [new Assay(id: 2), new Assay(id: 3)] | "2,3"
        "Long of ids"      | [2, 3]                               | "2,3"
        "Empty Collection" | []                                   | null
    }

    void "test extractETagIdFromNode #label"() {

        when:
        final Value value = restCompoundService.extractETagIdFromNode(jsonNode)
        then:
        assert value?.id == expectedValue
        where:
        label                | jsonNode                              | expectedValue
        "Valid ETag Id Node" | mapper.readTree("{\"etag_id\":2345}") | "2345"
        "Empty ETag Id Node" | mapper.readTree("{}")                 | null
        "Null ETag Id Node"  | mapper.readTree("{\"test_id\":2345}") | null
    }

    void "test addSuggestPair"() {
        given:
        final Map<String, List<String>> suggestions = [:]
        final String fieldName = "protocol"
        when:
        restCompoundService.addSuggestPair(jsonNode, fieldName, suggestions)
        then:
        suggestions.size() == 1
        suggestions.get(fieldName).size() == 5
        where:
        label          | jsonNode
        "Suggest Pair" | mapper.readTree(SUGGEST_PAIR)
    }

    void "test handleDocsNode Assays #label"() {

        given:
        final List<Assay> entities = []
        final List<Value> facets = []
        when:
        restAssayService.handleDocsNode(entities, facets, jsonNode)
        then:
        assert entities.size() == 2
        assert facets.size() == 3
        Assay assay = entities.get(0)
        assert assay
        where:
        label            | jsonNode
        "Assay Resource" | mapper.readTree(ASSAY_SEARCH_RESULTS)
    }

    void "test handleDocsNode Projects #label"() {

        given:
        final List<Project> entities = []
        final List<Value> facets = []
        when:
        restProjectService.handleDocsNode(entities, facets, jsonNode)
        then:
        assert entities.size() == 2
        Project project = entities.get(0)
        facets.size() == 2
        assert project
        where:
        label              | jsonNode
        "Project Resource" | mapper.readTree(PROJECT_SEARCH_RESULTS)
    }

    void "test handleDocsNode Compounds #label"() {

        given:
        final List<Compound> entities = []
        final List<Value> facets = []
        when:
        restCompoundService.handleDocsNode(entities, facets, jsonNode)
        then:
        assert entities.size() == 2
        Compound compound = entities.get(0)
        facets.size == 3
        assert compound
        where:
        label               | jsonNode
        "Compound Resource" | mapper.readTree(COMPOUND_SEARCH_RESULTS)
    }

    void "test getETagId #label"() {

        when:
        final String id = restProjectService.getETagId(etag)
        then:
        assert id == expectedQuery
        where:
        label                     | etag                                | expectedQuery
        "ETag is String"          | "1234"                              | "1234"
        "ETag is a value object'" | new Value(new DataSource(), "1234") | "1234"
        "ETag is null"            | null                                | null

    }

    void "test buildETagQuery #label"() {
        when:
        String query = restProjectService.buildETagQuery(etag)
        then:
        assert query == expectedQuery
        where:
        label                     | etag                                | expectedQuery
        "ETag is String"          | "1234"                              | "etag/1234/facets"
        "ETag is a value object'" | new Value(new DataSource(), "1234") | "etag/1234/facets"
        "ETag is null"            | null                                | "etag/null/facets"


    }

    void "test buildGetQuery #label"() {
        when:
        String query = restProjectService.buildGetQuery(resource, expand, top, skip);
        then:
        assert query == expectedQuery
        where:
        label                                    | resource        | skip | top | expand | expectedQuery
        "Resource with '?'"                      | "http://broad?" | 1    | 1   | false  | "http://broad?&skip=1&top=1"
        "Resource with '?' and 'expand=true'"    | "http://broad?" | 1    | 1   | true   | "http://broad?&skip=1&top=1&expand=true"
        "Resource without '?' and 'expand=true'" | "http://broad"  | 1    | 1   | true   | "http://broad?skip=1&top=1&expand=true"
        "Resource without '?'"                   | "http://broad"  | 1    | 1   | false  | "http://broad?skip=1&top=1"


    }

    void "test buildSuggestQuery"() {
        when:
        String query = restProjectService.buildSuggestQuery(constructSuggestParams())
        then:
        assert query == "http://bard.nih.gov/api/v7/search/projects/suggest?q=dna&top=10"


    }

    void "test extractDescriptionFromNode #label"() {
        given:
        Value etag = new Value(new DataSource("ETAG"), "etag")
        when:
        restProjectService.extractDescriptionFromNode(jsonNode, etag);
        then:
        assert etag.getChildCount() == childCount
        where:
        label                        | jsonNode                                         | childCount
        "JSON with Description Node" | mapper.readTree("{\"description\":\"url\"}")     | 1
        "JSON NO Description Node"   | mapper.readTree("{\"nodescription\":\"nourl\"}") | 0
        "Empty Json Node"            | mapper.readTree("{}")                            | 0

    }

    void "test extractUrlFromNode #label"() {
        given:
        Value etag = new Value(new DataSource("ETAG"), "etag")
        when:
        restProjectService.extractUrlFromNode(jsonNode, etag);
        then:
        assert etag.getChildCount() == childCount
        where:
        label                | jsonNode                                 | childCount
        "JSON with Url Node" | mapper.readTree("{\"url\":\"url\"}")     | 1
        "JSON NO Url Node"   | mapper.readTree("{\"nourl\":\"nourl\"}") | 0
        "Empty Json Node"    | mapper.readTree("{}")                    | 0

    }

    void "test extractCountFromNode #label"() {
        given:
        Value etag = new Value(new DataSource("ETAG"), "etag")
        when:
        restProjectService.extractCountFromNode(jsonNode, etag);
        then:
        assert etag.getChildCount() == childCount
        where:
        label                  | jsonNode                                     | childCount
        "JSON with Count Node" | mapper.readTree("{\"count\":\"2\"}")         | 1
        "JSON NO Count Node"   | mapper.readTree("{\"nocount\":\"nocount\"}") | 0
        "Empty Json Node"      | mapper.readTree("{}")                        | 0
    }

    void "test extractNameFromNode #label"() {
        given:
        Value etag = new Value(new DataSource("ETAG"), "etag")
        when:
        restProjectService.extractNameFromNode(jsonNode, etag);
        then:
        assert etag.getChildCount() == childCount
        where:
        label             | jsonNode                                   | childCount
        "JSON with Name"  | mapper.readTree("{\"name\":\"name\"}")     | 1
        "JSON NO Name"    | mapper.readTree("{\"noname\":\"noname\"}") | 0
        "Empty Json Node" | mapper.readTree("{}")                      | 0
    }

    void "test build QueryForCollectionOfETags"() {
        when:
        final String tags = restProjectService.buildQueryForCollectionOfETags(2, 2)
        then:
        assert tags == "http://bard.nih.gov/api/v7/projects/etag?skip=2&top=2&expand=true"
    }

    void "test addSingleEntity Projects #label"() {
        given:
        final List<Project> projects = []
        when:
        restProjectService.addSingleEntity(projects, jsonNode)
        then:
        assert projects.size() == numberOfEntities
        where:
        label                           | jsonNode                                         | numberOfEntities
        "Project Resource"              | mapper.readTree(PROJECT_EXPANDED_SEARCH_RESULTS) | 1
        "Project Resource, null entity" | mapper.readTree(PROJECT_EXPANDED_SEARCH_RESULTS) | 1
        "Null Json Node"                | null                                             | 0

    }

    void "test addSingleEntity Experiments #label"() {
        given:
        final List<Experiment> experiments = []
        when:
        restExperimentService.addSingleEntity(experiments, jsonNode)
        then:
        assert experiments.size() == numberOfEntities
        where:
        label                              | jsonNode                                            | numberOfEntities
        "Experiment Resource"              | mapper.readTree(EXPERIMENT_EXPANDED_SEARCH_RESULTS) | 1
        "Experiment Resource, null entity" | mapper.readTree(EXPERIMENT_EXPANDED_SEARCH_RESULTS) | 1

    }

    void "test addSingleEntity Compounds #label"() {
        given:
        final List<Compound> compounds = []
        when:
        restCompoundService.addSingleEntity(compounds, jsonNode)
        then:
        assert compounds.size() == numberOfEntities
        where:
        label                            | jsonNode                                          | numberOfEntities
        "Compound Resource"              | mapper.readTree(COMPOUND_EXPANDED_SEARCH_RESULTS) | 1
        "Compound Resource, null Entity" | mapper.readTree(COMPOUND_EXPANDED_SEARCH_RESULTS) | 1

    }

    void "test addSingleEntity Assays #label"() {
        given:
        final List<Assay> assays = []
        when:
        restAssayService.addSingleEntity(assays, jsonNode)
        then:
        assert assays.size() == 1
        where:
        label                         | jsonNode                                       | numberOfEntities
        "Assay Resource"              | mapper.readTree(ASSAY_EXPANDED_SEARCH_RESULTS) | 1
        "Assay Resource, null Entity" | mapper.readTree(ASSAY_EXPANDED_SEARCH_RESULTS) | 1

    }

    void "test parse ETag #label"() {

        when:
        Value value = restCompoundService.parseETag(jsonNode)
        then:
        assert value
        assert value.childCount == childCount
        assert value.source.name == 'bard.core.DataSource'
        assert value.id

        where:
        label                       | jsonNode                                | childCount
        "Full JSON Node"            | mapper.readTree(JSON_NODE_FULL_ETAG)    | 4
        "Only etag_id in JSON Node" | mapper.readTree(JSON_NODE_ONLY_ETAG_ID) | 0

    }

    void "test parse ETag Null value #label"() {

        when:
        Value value = restCompoundService.parseETag(jsonNode)
        then:
        assert !value

        where:
        label                         | jsonNode
        "No etag_id key in JSON Node" | mapper.readTree(JSON_NODE_NO_ETAG_ID)

    }

    void "test parseFacets with array node #label"() {
        given:
        List<Value> facets = []
        when:
        restCompoundService.parseFacets(facets, node)
        then:
        assert facets
        assert facets.size() == 2
        assert facets.get(0).childCount == eachChildCount
        assert facets.get(0).id == facetNames.get(0)
        assert facets.get(1).childCount == eachChildCount
        assert facets.get(1).id == facetNames.get(1)
        where:
        label                   | node                                     | facetNames              | eachChildCount
        "With valid Array Node" | mapper.readTree(COMPOUND_SEARCH_RESULTS) | ["COLLECTION", "xlogp"] | 3
    }

    void "test parseFacets not valid Array node #label"() {
        given:
        List<Value> facets = []
        when:
        restCompoundService.parseFacets(facets, is)
        then:
        assert facets.isEmpty()
        where:
        label                   | is
        "Not a JSON Node Array" | mapper.readTree(JSON_NODE_FULL_ETAG)
    }

    SearchParams constructParams() {
        return new SearchParams("dna repair");
    }

    SearchParams constructParamsWithFilters() {
        final List<String[]> filters = new ArrayList<String[]>();
        filters.add(["num_expt", "6"] as String[])
        return new SearchParams("dna repair", filters);
    }

    SuggestParams constructSuggestParams() {
        return new SuggestParams("dna", 10);
    }

    void "test buildFilters #label"() {

        when:
        final String query = restCompoundService.buildFilters(params);
        then:
        assert query == expectedQuery
        where:
        label                               | params                       | expectedQuery
        "With Search Params and Filters"    | constructParamsWithFilters() | "&filter=fq(num_expt:6)"
        "With Search Params and No Filters" | constructParams()            | ""
        "With  null"                        | null                         | ""
    }

    void "test buildSearchResource #label"() {
        when:
        final String query = restCompoundService.buildSearchResource(params);
        then:
        assert query == expectedQuery
        where:
        label                               | params                       | expectedQuery
        "With Search Params and Filters"    | constructParamsWithFilters() | "http://bard.nih.gov/api/v7/search/compounds/?q=dna+repair&filter=fq(num_expt:6),"
        "With Search Params and No Filters" | constructParams()            | "http://bard.nih.gov/api/v7/search/compounds/?q=dna+repair,"
        "With  null"                        | null                         | null
    }

    void "test extractCountKeyValuePairFromNode #label"() {
        given:
        final String facetName = "facetName"
        Value facet = new Value(new DataSource(), facetName)
        when:
        restCompoundService.extractCountKeyValuePairFromNode(facet, mapper.readTree(JSON_COUNTS))
        then:
        assert facet.id == facetName
        assert facet.getChildCount() == 2


    }

    void "test extractFacetNameAndCountFromNode #label"() {

        given:
        final String facetName = "facetName"
        Value facet = new Value(new DataSource(), facetName)
        when:
        restCompoundService.extractFacetInfoAndCountFromNode(facet, key, value)
        then:
        assert facet.id == facetName
        assert facet.getChildCount() == value
        where:
        label                  | key    | value
        "With Key and Value"   | "key1" | 1
        "With Key, zero Value" | "key2" | 0
    }

    void "test extractNumberHitsFromJson #label"() {
        when:
        final int numberOfHits = restCompoundService.extractNumberHitsFromJson(hitsNode)
        then:
        assert numberOfHits == expectedHits
        where:
        label            | hitsNode                          | expectedHits
        "With Hits node" | mapper.readTree("{\"nhit\":216}") | 216
        "No Hits Node"   | mapper.readTree("\"bogus\":216")  | 0


    }

    void "test extractFacetsFromJson #label"() {


        when:
        final List<Value> facets = restCompoundService.extractFacetsFromJson(facetNode)
        then:
        facets.size() == facetCount

        where:
        label              | facetNode                            | facetCount
        "JSON With facets" | mapper.readTree(FACETS_ONLY)         | 1
        "No Facets"        | mapper.readTree(JSON_NODE_NO_FACETS) | 0


    }

    void "test parseFacets #label"() {
        given:
        List<Value> facets = []
        when:
        int hits = restCompoundService.parseFacets(facets, jsonNode)
        then:
        assert hits == expectedHits
        assert facets.size() == facetCount

        where:
        label              | jsonNode                                | expectedHits | facetCount
        "No Metadata Node" | mapper.readTree(JSON_NODE_NO_META_DATA) | 0            | 0
        "No Hits Node"     | mapper.readTree(JSON_NODE_NO_HITS)      | 0            | 2
        "No Facets"        | mapper.readTree(JSON_NODE_NO_FACETS)    | 1            | 0
        "Valid JSON"       | mapper.readTree(JSON_NODE_WITH_FACETS)  | 1            | 2
    }

    void "test ParseFacet #label"() {
        given:
        DataSource ds = new DataSource("name")
        when:
        Value value = restCompoundService.parseFacet(ds, jsonNode)
        then:
        assert value.id == expectedValue
        assert value.childCount == childrenCount
        where:
        label                     | jsonNode                                     | expectedValue          | childrenCount
        "Valid JSON Node"         | mapper.readTree(JSON_FACET)                  | "assay_component_role" | 1
        "Counts has zero value"   | mapper.readTree(JSON_FACET_ZERO_COUNT_VALUE) | "assay_component_role" | 0
        "JSON Node No count node" | mapper.readTree(JSON_FACET_NO_COUNT_KEY)     | "assay_component_role" | 0
    }
}
