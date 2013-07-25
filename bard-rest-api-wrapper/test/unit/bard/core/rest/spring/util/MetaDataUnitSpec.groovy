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
      "facetName" : "assay_component_role",
      "counts" :
         {
            "target cell" : 11
         }
   },
   {
      "facetName" : "assay_mode",
      "counts" :
         {
         }
   },
   {
      "facetName" : "assay_type",
      "counts" :
         {
            "reporter-gene assay" : 4,
            "cytotoxicity assay" : 2,
            "pharmacokinetic assay" : 2,
            "cell cycle assay" : 1,
            "cell-proliferation assay" : 1,
            "secreted protein assay" : 1
         }
   },
   {
      "facetName" : "detection_method_type",
      "counts" :
         {
            "bioluminescence" : 6,
            "mass spectrometry" : 2,
            "AlphaScreen" : 1,
            "flow cytometry" : 1,
            "scintillation counting" : 1
         }
   },
   {
      "facetName" : "target_name",
      "counts" :
         {
            "negative regulation of protein catabolic process" : 5,
            "cell cycle arrest" : 2,
            "cell death" : 2,
            "drug transport" : 2,
            "g2/mitotic-specific cyclin-b1" : 2,
            "cerebellar granule cell precursor proliferation" : 1,
            "cyclin-dependent kinase inhibitor 1" : 1,
            "cyclin-dependent kinase inhibitor 1b" : 1,
            "wee1-like protein kinase" : 1
         }
   },
   {
      "facetName" : "kegg_disease_cat",
      "counts" :
         {
            "Cancer" : 2,
            "Endocrine system disease" : 1,
            "Nervous system disease" : 1
         }
   },
   {
      "facetName" : "biology",
      "counts" :
         {
            "process" : 11,
            "protein" : 4
         }
   },
   {
      "facetName" : "class_name",
      "counts" :
         {
            "enzyme modulator" : 4,
            "kinase modulator" : 4,
            "kinase activator" : 2,
            "kinase inhibitor" : 2,
            "kinase" : 1,
            "non-receptor serine/threonine protein kinase" : 1,
            "protein kinase" : 1,
            "transferase" : 1
         }
   },
   {
      "facetName" : "target_name_process",
      "counts" :
         {
            "negative regulation of protein catabolic process" : 5,
            "cell cycle arrest" : 2,
            "cell death" : 2,
            "drug transport" : 2,
            "cerebellar granule cell precursor proliferation" : 1
         }
   },
   {
      "facetName" : "target_name_protein",
      "counts" :
         {
            "g2/mitotic-specific cyclin-b1" : 2,
            "cyclin-dependent kinase inhibitor 1" : 1,
            "cyclin-dependent kinase inhibitor 1b" : 1,
            "wee1-like protein kinase" : 1
         }
   },
   {
      "facetName" : "target_name_gene",
      "counts" :
         {
         }
   },
   {
      "facetName" : "assay_status",
      "counts" :
         {
            "draft" : 11
         }
   },
   {
      "facetName" : "assay_format",
      "counts" :
         {
            "cell-based format" : 9,
            "small-molecule format" : 2
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
        assert metaData.getScore("770") == 2.1923265
        final String name = metaData.getMatchingField("781").getName()
        assert (name == "description") || (name == "name")
    }

}

