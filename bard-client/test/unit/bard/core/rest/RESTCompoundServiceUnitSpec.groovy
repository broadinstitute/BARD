package bard.core.rest

import bard.core.Assay
import bard.core.Compound
import bard.core.DataSource
import bard.core.Value
import bard.core.interfaces.EntityServiceManager
import com.fasterxml.jackson.databind.ObjectMapper
import jdo.JSONNodeTestHelper
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class RESTCompoundServiceUnitSpec extends Specification {
    RESTCompoundService restCompoundService
    RESTAssayService restAssayService
    EntityServiceManager entityServiceManager
    @Shared ObjectMapper mapper = new ObjectMapper();
    @Shared String TESTED_ASSAY_NODE = JSONNodeTestHelper.TESTED_ASSAY_NODE
    @Shared String COMPOUND_SYNONYMS = JSONNodeTestHelper.COMPOUND_SYNONYMS

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

}

