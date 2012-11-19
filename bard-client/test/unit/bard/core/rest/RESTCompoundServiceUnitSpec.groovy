package bard.core.rest

import bard.core.interfaces.EntityServiceManager
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ArrayNode
import jdo.JSONNodeTestHelper
import org.apache.http.client.methods.HttpGet
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll
import bard.core.*

@Unroll
class RESTCompoundServiceUnitSpec extends Specification {
    RESTCompoundService restCompoundService
    RESTAssayService restAssayService
    EntityServiceManager entityServiceManager
    @Shared ObjectMapper mapper = new ObjectMapper();
    @Shared String TESTED_ASSAY_NODE = JSONNodeTestHelper.TESTED_ASSAY_NODE
    @Shared String COMPOUND_SYNONYMS = JSONNodeTestHelper.COMPOUND_SYNONYMS
    @Shared String COMPOUND_NODE = JSONNodeTestHelper.COMPOUND_SUMMARY_SEARCH_RESULTS
    @Shared String COMPOUND_EXPANDED_SEARCH_RESULTS = JSONNodeTestHelper.COMPOUND_EXPANDED_SEARCH_RESULTS
    @Shared String ASSAY_NODE = '''
          {
            "assay_id":"2377",
            "name":"NCI Yeast Anticancer Drug Screen. Data for the mgt1 strain",
            "highlight":"gi|6320001|ref|NP_010081.1|DNA repair methyltransferase (6-O-methylguanine-DNA methylase) involved in protection against DNA alkylation damage; Mgt1p [Saccharomyces cerevisiae]"
        }
        '''

    void setup() {
        this.entityServiceManager = Mock(RESTEntityServiceManager)
        this.restAssayService = new RESTAssayService(this.entityServiceManager, "base")
        this.restCompoundService = new RESTCompoundService(this.entityServiceManager, "base")
    }

    void tearDown() {
        // Tear down logic here
    }
   void "getPromiscuityResource"(){
       given:
       long cid = 200
       when:
       String url = restCompoundService.getPromiscuityResource(cid)
       then:
       assert url =="base/plugins/badapple/prom/cid/200?expand=true"
   }
    void "buildQueryForTestedAssays #label"() {
        given:
        final Compound compound = new Compound("name")
        compound.setId(200)
        when:
        String url = this.restCompoundService.buildQueryForTestedAssays(compound, active);
        then:
        assert url == expectedUrl
        where:
        label                | active | expectedUrl
        "Active Compounds"   | true   | "base/compounds/200/assays?expand=true&filter=active"
        "InActive Compounds" | false  | "base/compounds/200/assays?expand=true"
    }

    void "extractedTestAssays #label"() {
        when:
        List<Assay> assays = this.restCompoundService.extractedTestAssays(rootNode)
        then:
        this.entityServiceManager.getService(Assay.class) >> {restAssayService}
        restAssayService.getEntity(_, _) >> {}
        assert assays.isEmpty() == isNodeEmpty
        where:
        label                                   | rootNode                           | isNodeEmpty
        "Root Node has Full Properties"         | mapper.readTree(TESTED_ASSAY_NODE) | false
        "Root Node Does not Contain Collection" | mapper.readTree(ASSAY_NODE)        | true
        "Null Root Node"                        | null                               | true


    }

    void "jsonNodeToAssay #label"() {
        given:

        when:
        Assay assay = this.restCompoundService.jsonNodeToAssay(jsonNode, restAssayService)
        then:
        this.entityServiceManager.getService(Assay.class) >> {restAssayService}
        restAssayService.getEntity(_, _) >> {}
        assert (assay == null) == isNodeEmpty
        where:
        label                                   | jsonNode                                  | isNodeEmpty
        "Root Node Does not Contain Collection" | mapper.readTree("{\"capAssayId\": 4063}") | false
        "Null Node"                             | null                                      | true

    }

    void "addSynonyms #label"() {
        given:
        final List<Value> synonyms = []
        when:
        this.restCompoundService.addSynonyms(synonyms, jsonNode, new DataSource("name"))
        then:
        assert synonyms.isEmpty() == isNodeEmpty
        where:
        label                   | jsonNode                           | isNodeEmpty
        "Node with Synonyms"    | mapper.readTree(COMPOUND_SYNONYMS) | false
        "Node with no Synonyms" | mapper.readTree(ASSAY_NODE)        | true
        "Null Node"             | null                               | true

    }

    void "addSynonym #label"() {
        given:
        final List<Value> synonyms = []
        when:
        this.restCompoundService.addSynonym(synonyms, jsonNode, new DataSource("name"))
        then:
        assert synonyms.isEmpty() == isNodeEmpty
        where:
        label               | jsonNode                         | isNodeEmpty
        "Node with Synonym" | mapper.readTree("\"NSC228155\"") | false
        "Null Node"         | null                             | true

    }

    void "searchResult with Exceptions"() {
        given:
        final Compound compound = new Compound()
        when:
        this.restCompoundService.searchResult(compound, Substance.class)
        then:
        thrown(IllegalArgumentException)

    }


    void "getEntitySearch #label"() {
        when:
        final Compound resultCompound = this.restCompoundService.getEntitySearch(compound, node)

        then:
        assert resultCompound
        assert resultCompound.getId() == 2722
        where:
        label                 | node                           | compound
        "Project is not null" | mapper.readTree(COMPOUND_NODE) | new Compound()
        "Project is null"     | mapper.readTree(COMPOUND_NODE) | null
    }

    void "getEntity #label"() {
        when:
        final Compound resultCompound = this.restCompoundService.getEntity(compound, node)

        then:
        assert resultCompound
        assert resultCompound.getId() == 600
        where:
        label                 | node                                              | compound
        "Project is not null" | mapper.readTree(COMPOUND_EXPANDED_SEARCH_RESULTS) | new Compound()
        "Project is null"     | mapper.readTree(COMPOUND_EXPANDED_SEARCH_RESULTS) | null
    }

    void "addHighlight #label"() {
        given:
        final Compound compound = new Compound()
        when:
        this.restCompoundService.addHighlight(compound, node)
        then:
        assert compound.getValues().isEmpty() == expectedResult
        where:
        label                             | node                                          | expectedResult
        "'highlight' key in JSON Node"    | mapper.readTree("{ \"highlight\": \"high\"}") | false
        "No 'highlight' key in JSON Node" | mapper.readTree("{ \"someName\": \"high\"}")  | true
    }

    void "addIupacName #label"() {
        given:
        final Compound compound = new Compound()
        when:
        this.restCompoundService.addIupacName(compound, node)
        then:
        assert compound.getValues().isEmpty() == expectedResult
        assert compound.getName() == expectedName
        where:
        label                             | node                                                 | expectedResult | expectedName
        "'iupacName' key in JSON Node"    | mapper.readTree("{ \"iupacName\": \"propan-2-0l\"}") | false          | "propan-2-0l"
        "No 'iupacName' key in JSON Node" | mapper.readTree("{ \"someName\": \"high\"}")         | true           | null
    }

    void "addPreferredTerm #label"() {
        given:
        final Compound compound = new Compound()
        when:
        this.restCompoundService.addPreferredTerm(compound, node)
        then:
        assert compound.getName() == expectedName
        where:
        label                                  | node                                               | expectedName
        "'preferred_term' key in JSON Node"    | mapper.readTree("{ \"preferred_term\": \"high\"}") | "high"
        "No 'preferred_term' key in JSON Node" | mapper.readTree("{ \"someName\": \"high\"}")       | null

    }

    void "addCompoundCID #label"() {
        given:
        final Compound compound = new Compound()
        when:
        this.restCompoundService.addCompoundCID(compound, node)
        then:
        assert compound.getId() == expectedId
        assert compound.getValues().isEmpty() == expectedValues
        where:
        label                       | node                                         | expectedId | expectedValues
        "'cid' key in JSON Node"    | mapper.readTree("{ \"cid\": 200}")           | 200        | false
        "No 'cid' key in JSON Node" | mapper.readTree("{ \"someName\": \"high\"}") | null       | true
    }

    void "addCompoundName #label"() {
        given:
        final Compound compound = new Compound()
        when:
        this.restCompoundService.addCompoundName(compound, node)
        then:
        assert compound.getName() == expectedName
        where:
        label                        | node                                         | expectedName
        "'name' key in JSON Node"    | mapper.readTree("{ \"name\": \"name\"}")     | "name"
        "No 'name' key in JSON Node" | mapper.readTree("{ \"someName\": \"high\"}") | null
    }

    void "addETagsToHTTPHeader"() {
        given:
        HttpGet httpGet = new HttpGet()
        final Map<String, Long> etags = [key1: 200L, key2: 100L]
        when:
        this.restCompoundService.addETagsToHTTPHeader(httpGet, etags)
        then:
        assert httpGet.getFirstHeader("If-Match").getValue() == "\"key2\""
    }

    void "getParentETag"() {
        given:
        final Map<String, Long> etags = [key1: 100L, key2: 300L]

        when:
        String key = RESTCompoundService.getParentETag(etags)
        then:
        assert key == "key1"


    }

    void "jsonArrayNodeToAssays"() {
        given:
        ArrayNode aa = mapper.createArrayNode()
        aa.add(null)
        when:
        List<Assay> assays = this.restCompoundService.jsonArrayNodeToAssays(aa, this.restAssayService)
        then:
        assert !assays
    }

    void "addSIDs #label"() {
        given:
        final Compound compound = new Compound()
        when:
        this.restCompoundService.addSIDs(compound, node)
        then:
        assert compound.getValues().isEmpty() == expectedValues
        where:
        label                        | node                                              | expectedValues
        "'sids' key in JSON Node"    | mapper.readTree(COMPOUND_EXPANDED_SEARCH_RESULTS) | false
        "No 'sids' key in JSON Node" | mapper.readTree("{ \"someName\": \"high\"}")      | true
    }

}

