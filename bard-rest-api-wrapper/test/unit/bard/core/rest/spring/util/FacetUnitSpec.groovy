package bard.core.rest.spring.util

import com.fasterxml.jackson.databind.ObjectMapper
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class FacetUnitSpec extends Specification {
    @Shared
    ObjectMapper objectMapper = new ObjectMapper()
    final String FACET_JSON = '''
   {
      "facetName":"COLLECTION",
      "counts":{
         "US DEA":338,
         "NPC informatics":10201
      }
   }
   '''

    void "test serialize json to facet"() {
        when:
        Facet facet = objectMapper.readValue(FACET_JSON, Facet.class)
        then:
        assert facet
        assert facet.facetName == "COLLECTION"
        assert facet.counts
        final Map<String, Object> properties = facet.counts.getAdditionalProperties()
        assert properties
        assert (Integer) properties.get("US DEA") == 338
        assert (Integer) properties.get("NPC informatics") == 10201
        assert facet.toString()
        assert facet.toValue()
    }

    void "test toValue Empty Additional Parameters"() {
        when:
        Facet facet = new Facet()
        facet.setCounts(new Counts(additionalProperties: [:]))
        then:
        facet.toValue() == null


    }
    void "test toValue Additional Params has no Value"() {
        when:
        Facet facet = new Facet()
        facet.setCounts(new Counts(additionalProperties: ["key":null, "key1":"notANumber"]))
        then:
        facet.toValue() == null
    }

}

