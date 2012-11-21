package bard.core.rest

import bard.core.interfaces.EntityService
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
    @Shared ObjectMapper mapper = new ObjectMapper();
    @Shared String TARGET_NODE = JSONNodeTestHelper.TARGET_NODE
    @Shared String TARGETS_NODE = JSONNodeTestHelper.TARGETS_NODE
    @Shared String ASSAY_SUMMARY_SEARCH_RESULTS = JSONNodeTestHelper.ASSAY_SUMMARY_SEARCH_RESULTS
    @Shared String ASSAY_EXPANDED_SEARCH_RESULTS = JSONNodeTestHelper.ASSAY_EXPANDED_SEARCH_RESULTS
    @Shared String NO_TARGET_NODE = JSONNodeTestHelper.COMPOUND_SEARCH_RESULTS

    void setup() {
        this.restAssayService = new RESTAssayService( "base")
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

    void "addTargets Full Properties #label"() {
        given:
        final Assay assay = new Assay()
        when:
        this.restAssayService.addTargets(node, assay)

        then:
        final Collection<Biology> targets = assay.getTargets()
        assert targets.isEmpty() == isNodeEmpty
        where:
        label             | node                            | isNodeEmpty
        "Full Properties" | mapper.readTree(TARGETS_NODE)   | false
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


    void "getEntitySearch #label"() {
        when:
        final Assay resultAssay = this.restAssayService.getEntitySearch(assay, node)

        then:
        assert resultAssay
        assert resultAssay.getId() == 2377
        where:
        label               | node                                          | assay
        "Assay is not null" | mapper.readTree(ASSAY_SUMMARY_SEARCH_RESULTS) | new Assay()
        "Assay is null"     | mapper.readTree(ASSAY_SUMMARY_SEARCH_RESULTS) | null
    }

    void "getEntity #label"() {
        when:
        final Assay resultAssay = this.restAssayService.getEntity(assay, node)

        then:
        assert resultAssay
        assert resultAssay.getId() == 600
        where:
        label               | node                                           | assay
        "Assay is not null" | mapper.readTree(ASSAY_EXPANDED_SEARCH_RESULTS) | new Assay()
        "Assay is null"     | mapper.readTree(ASSAY_EXPANDED_SEARCH_RESULTS) | null
    }

    void "addKeyValuesAsString Empty Keys and Values Node"() {
        given:
        Assay assay = new Assay()
        ArrayNode keys = null
        ArrayNode vals = null
        DataSource ds = new DataSource("name")
        when:
        this.restAssayService.addKeyValuesAsString(assay, keys, vals, ds)
        then:
        assert assay.getValues(ds).children.isEmpty()
    }
}

