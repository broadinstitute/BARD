package bard.core.rest.spring.util

import bard.core.Value
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
      "facetName" : "class_name",
      "counts" :
         {
            "transferase" : 1
         }
   },
   {
      "facetName" : "assay_type",
      "counts" :
         {
            "secreted protein assay" : 1
         }
   },
   {
      "facetName" : "assay_format",
      "counts" :
         {
            "small-molecule format" : 2
         }
   },
   {
      "facetName" : "target_name",
      "counts" :
         {
            "wee1-like protein kinase" : 1
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

    void "test MetaData optional Fields"() {
        when:
        MetaData metaData = new MetaData()
        then:
        assert metaData.getScore("Some Key") == 0.0
        assert metaData.getMatchingField("Some Key") == null
    }

    void "test serialize json to metadata"() {
        when:
        MetaData metaData = objectMapper.readValue(METADATA_NODE, MetaData.class)
        then:
        assert metaData
        assert metaData.facets
        final Facet facet = metaData.facets.get(0)
        assert facet
        assert facet.facetName == "class_name"
        assert facet.counts
        final Map<String, Object> properties = facet.counts.getAdditionalProperties()
        assert properties
        assert (Integer) properties.get("transferase") == 1
        assert metaData.elapsedTime == 339
        assert metaData.nhit == 1
        assert metaData.matchingFields
        assert metaData.scores
        assert metaData.getQueryTime()
        assert facet.toString()
        assert facet.toValue()
        assert metaData.facetsToValues()
        assert metaData.getScore("770") == 2.1923265
        final String name = metaData.getMatchingField("781").getName()
        assert (name == "description") || (name == "name")
    }

    void "test facetsToValues sorting"() {
        when:
        MetaData metaData = objectMapper.readValue(METADATA_NODE, MetaData.class)
        Collection<Value> facetValues = metaData.facetsToValues()

        then:
        assert metaData
        assert metaData.facets
        assert facetValues.get(0).id == 'target_name'
        assert facetValues.get(1).id == 'assay_format'
        assert facetValues.get(2).id == 'assay_type'
        assert facetValues.get(3).id == 'class_name'
    }

}

