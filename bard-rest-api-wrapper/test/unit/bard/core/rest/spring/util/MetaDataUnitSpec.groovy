package bard.core.rest.spring.util

import com.fasterxml.jackson.databind.ObjectMapper
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class MetaDataUnitSpec extends Specification {
    @Shared
    ObjectMapper objectMapper = new ObjectMapper()
    public static final String METADATA_NODE = '''
    {
        "nhit":1,
        "facets":[
                {
                    "facetName":"assay_component_role",
                    "counts":{
                    "dye":3,
                    "measured component":6
                    }
                }
        ],
        "queryTime": 278,
       "elapsedTime": 339,
       "matchingFields":
       {
           "781":
           {
               "description": "Cancer cells",
               "name": "qHTS screen"
           }
       },
       "scores":
       {
           "770": 2.1923265,
           "779": 2.1923265
       }
    }
     '''


    void setup() {

    }

    void tearDown() {
        // Tear down logic here
    }
   void "test MetaData optional Fields"(){
       when:
       MetaData metaData = new MetaData()
       then:
       assert metaData.getScore("Some Key") == 0.0
       assert metaData.getMatchingField("Some Key") ==null
   }
    void "test serialize json to metadata"() {
        when:
        MetaData metaData = objectMapper.readValue(METADATA_NODE, MetaData.class)
        then:
        assert metaData
        assert metaData.facets
        final Facet facet = metaData.facets.get(0)
        assert facet
        assert facet.facetName == "assay_component_role"
        assert facet.counts
        final Map<String, Object> properties = facet.counts.getAdditionalProperties()
        assert properties
        assert (Integer) properties.get("dye") == 3
        assert (Integer) properties.get("measured component") == 6
        assert metaData.elapsedTime == 339
        assert metaData.nhit == 1
        assert metaData.matchingFields
        assert metaData.scores
        assert metaData.getQueryTime()
        assert facet.toString()
        assert facet.toValue()
        assert metaData.facetsToValues()
        assert metaData.getScore("770") ==2.1923265
        final String name = metaData.getMatchingField("781").getName()
        assert (name == "description") || (name == "name")
    }

}

