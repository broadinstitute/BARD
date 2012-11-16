package bard.core.rest

import bard.core.interfaces.EntityService
import bard.core.interfaces.EntityServiceManager
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.ObjectNode
import jdo.JSONNodeTestHelper
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll
import bard.core.*

@Unroll
class RESTAssayServiceUnitSpec extends Specification {
    RESTAssayService restAssayService
    EntityServiceManager entityServiceManager
    @Shared ObjectMapper mapper = new ObjectMapper();
    @Shared String TARGET_NODE = JSONNodeTestHelper.TARGET_NODE
    @Shared String NO_TARGET_NODE = JSONNodeTestHelper.COMPOUND_SEARCH_RESULTS

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
    }

    void tearDown() {
        // Tear down logic here
    }

    void "createPublication #label"() {
        given:
        JsonNode rootNode = mapper.createObjectNode()
        final String title = "Tatu"
        final String doi = "doi"
        final String abs = "abs"
        final String pubMed = "2"
        ObjectNode objectNode = ((ObjectNode) rootNode).put(EntityService.TITLE, title).
                put(EntityService.DOI, doi).
                put(EntityService.ABS, abs).
                put(EntityService.PUBMED_ID, pubMed)
        when:
        final Publication publication = this.restAssayService.createPublication(objectNode)
        then:
        assert publication.abs == abs
        assert publication.title == title
        assert publication.doi == doi
        assert publication.pubmedId == 2

    }

    void "createPublication Empty Properties"() {
        given:
        JsonNode rootNode = mapper.createObjectNode()
        ObjectNode objectNode = ((ObjectNode) rootNode).put("dgd", "Tatu")
        when:
        final Publication publication = this.restAssayService.createPublication(objectNode)
        then:
        assert !publication.abs

    }

    void "addEntitySummary Empty Properties"() {
        given:
        final Assay assay = new Assay()
        final JsonNode node = mapper.createObjectNode()
        when:
        this.restAssayService.addEntitySearchSummary(assay, node)
        then:
        assert !assay.getCapAssayId()
        assert !assay.getDescription()
        assert !assay.getName()
        assert !assay.getProtocol()
        assert !assay.getComments()
    }

    void "addEntitySearchSummary Empty Properties"() {
        given:
        final Assay assay = new Assay()
        final JsonNode node = mapper.createObjectNode()
        when:
        this.restAssayService.addEntitySummary(assay, node)
        then:
        assert !assay.getCapAssayId()
        assert !assay.getDescription()
    }

    void "addTarget Full Properties #label"() {
        given:
        final Assay assay = new Assay()
        when:
        this.restAssayService.addTarget(node, assay)

        then:
        final Collection<Biology> targets = assay.getTargets()
        assert targets.isEmpty() == isNodeEmpty
        where:
        label             | node                            | isNodeEmpty
        "Full Properties" | mapper.readTree(TARGET_NODE)    | false
        "No ACC Node"     | mapper.readTree(NO_TARGET_NODE) | true
    }

    void "addHighlight No Highlight Property in JSON"() {
        given:
        final Assay assay = new Assay()
        final JsonNode node = mapper.createObjectNode()
        when:
        this.restAssayService.addHighlight(assay, node)

        then:
        assert !assay.getValue(Entity.SearchHighlightValue)

    }


    void "getEntitySearch With Assay"() {
        when:
        final Assay resultAssay = this.restAssayService.getEntitySearch(assay, node)

        then:
        assert resultAssay
        assert resultAssay.getId() == 2377
        where:
        label               | node                        | assay
        "Assay is not null" | mapper.readTree(ASSAY_NODE) | new Assay()
        "Assay is null"     | mapper.readTree(ASSAY_NODE) | null
    }

    void "addKeyValuesAsString Empty Keys and Values Node"() {
        //final Assay assay, final ArrayNode keys, final ArrayNode vals, final DataSource ds) {
        given:
        Assay assay = new Assay()
        ArrayNode keys = null
        ArrayNode vals = null
        DataSource ds = new DataSource("name")
        when:
        this.restAssayService.addKeyValueAsString(assay, keys, vals, ds)
        then:
        assert !assay.getValues(ds).children.isEmpty()
    }
//    void "searchResult #label"(){
//        given:
//        Assay assay = new Assay()
//        SearchResult searchResult = new RESTSearchResult()
//        when:
//        final SearchResult<Project> result = this.restAssayService.searchResult(assay, Project)
//        then:
//        restAssayService.getSearchResult(_,_) >>{searchResult}
//        restAssayService.getServiceManager() >> {entityServiceManager}
//        restAssayService.getServiceManager().getService(_) >> {restAssayService}
//
//        assert result
//
//    }
}

