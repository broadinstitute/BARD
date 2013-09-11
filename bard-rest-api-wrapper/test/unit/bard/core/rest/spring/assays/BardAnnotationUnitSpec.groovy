package bard.core.rest.spring.assays
import com.fasterxml.jackson.databind.ObjectMapper
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class BardAnnotationUnitSpec extends Specification {
    @Shared
    ObjectMapper objectMapper = new ObjectMapper()

    public static final String BARD_ANNOTATION = '''
{
   "contexts":[
      {
         "id":16715,
         "name":"species name",
         "comps":[
            {
               "entityId":null,
               "entity":"assay",
               "source":"cap-context",
               "id":16715,
               "display":"Homo sapiens",
               "contextRef":"species name",
               "key":"species name",
               "value":null,
               "extValueId":"9606",
               "url":"http://www.ncbi.nlm.nih.gov/Taxonomy/Browser/wwwtax.cgi?id=9606",
               "displayOrder":0,
               "related":null
            }
         ]
      },
      {
        "id": 113970,
        "name": "assay footprint",
        "comps": [
        {
            "entityId": null,
            "entity": "assay",
            "source": "cap-context",
            "id": 113970,
            "display": "384-well plate",
            "contextRef": "assay footprint",
            "key": "assay footprint",
            "value": "384-well plate",
            "extValueId": null,
            "url": null,
            "displayOrder": 0,
            "related": "measureRefs:2762"
        }]
      },
      {
        "id":44,
        "name":"project lead name",
        "comps":[{
            "entityId":null,
            "entity":"assay",
            "source":"cap-context",
            "id":44,
            "display":null,
            "contextRef":"project lead name",
            "key":"project lead name",
            "value":null,
            "extValueId":null,
            "url":null,
            "displayOrder":0,
            "related": null
        }]
      },
      {
        "id":6986,
        "name":"activity threshold",
        "comps":[{
            "entityId":null,
            "entity":"experiment",
            "source":"cap-context",
            "id":6986,
            "display":"IC50",
            "contextRef":"activity threshold",
            "key":"result type",
            "value":"IC50",
            "extValueId":null,
            "url":null,
            "displayOrder":0,
            "related": "measureRefs:2762,2763"
            }]
      },
      {
        "id":99,
        "name":"screening concentration",
        "comps":[{
            "entityId":null,
            "entity":"assay",
            "source":"cap-context",
            "id":99,
            "display":"0 - 6800 uM",
            "contextRef":"screening concentration",
            "key":"screening concentration",
            "value":"0 - 6800 uM",
            "extValueId":null,
            "url":null,
            "displayOrder":0,
            "related": null
        }]
      }
   ],
   "measures":[
      {
         "id":2761,
         "name":null,
         "comps":[
            {
               "entityId":null,
               "entity":"assay",
               "source":"cap-measure",
               "id":2761,
               "display":"logAC50",
               "contextRef":null,
               "key":null,
               "value":"logAC50",
               "extValueId":null,
               "url":null,
               "displayOrder":0,
               "related":"|parentMeasure:2762"
            }
         ]
      },
      {
         "id":2762,
         "name":null,
         "comps":[
            {
               "entityId":null,
               "entity":"assay",
               "source":"cap-measure",
               "id":2762,
               "display":"AC50",
               "contextRef":null,
               "key":null,
               "value":"AC50",
               "extValueId":null,
               "url":null,
               "displayOrder":0,
               "related":"assayContextRefs:6986,113970|parentMeasure:2763"
            }
         ]
      },
      {
         "id":2763,
         "name":null,
         "comps":[
            {
               "entityId":null,
               "entity":"assay",
               "source":"cap-measure",
               "id":2763,
               "display":"percent activity",
               "contextRef":null,
               "key":null,
               "value":"percent activity",
               "extValueId":null,
               "url":null,
               "displayOrder":0,
               "related":"assayContextRefs:6986"
            }
         ]
      }
   ],
   "docs":[
      {
         "id":28391,
         "name":"External URL",
         "comps":[
            {
               "entityId":null,
               "entity":"assay",
               "source":"cap-doc",
               "id":28391,
               "display":"Assay Link",
               "contextRef":"External URL",
               "key":"doc",
               "value":"http://ncgc.nih.gov/db/?aid=614",
               "extValueId":"http://ncgc.nih.gov/db/?aid=614",
               "url":null,
               "displayOrder":0,
               "related":null
            }
         ]
      }
   ]
}
    '''

    void "test serialization to AssayAnnotation"() {
        when:
        final BardAnnotation bardAnnotation = objectMapper.readValue(BARD_ANNOTATION, BardAnnotation.class)
        bardAnnotation.populateContextMeasureRelationships()

        then:
        assert bardAnnotation.measures
        assert bardAnnotation.measures.size() == 3
        assert bardAnnotation.docs
        assert bardAnnotation.docs.size() == 1
        assert bardAnnotation.contexts
        assert bardAnnotation.contexts.size() == 5

        Measure logAC50 = bardAnnotation.measures.get(0)
        Measure AC50 = bardAnnotation.measures.get(1)
        Measure percentActivity = bardAnnotation.measures.get(2)
        Context species = bardAnnotation.contexts.get(0)
        Context footprint = bardAnnotation.contexts.get(1)
        Context threshold = bardAnnotation.contexts.get(3)

        assert logAC50.parent == AC50
        assert logAC50.children.isEmpty()
        assert AC50.parent == percentActivity
        assert AC50.children.size() == 1
        assert AC50.children.contains(logAC50)
        assert percentActivity.parent == null
        assert percentActivity.children.size() == 1
        assert percentActivity.children.contains(AC50)

        assert logAC50.relatedContexts.isEmpty()
        assert AC50.relatedContexts.size() == 2
        assert AC50.relatedContexts.get(0) == threshold
        assert AC50.relatedContexts.get(1) == footprint
        assert percentActivity.relatedContexts.size() == 1
        assert percentActivity.relatedContexts.get(0) == threshold

        assert threshold.relatedMeasures.size() == 2
        assert threshold.relatedMeasures.get(0) == AC50
        assert threshold.relatedMeasures.get(1) == percentActivity
        assert footprint.relatedMeasures.size() == 1
        assert footprint.relatedMeasures.get(0) == AC50
        assert species.relatedMeasures.isEmpty()
    }

    void "test areAnnotationsEmpty() #label"() {
        when:
        Boolean result = BardAnnotation.areAnnotationsEmpty(annotations)

        then:
        assert result == expectedResult

        where:
        label                       | annotations                                                        | expectedResult
        'no annotations at all'     | null                                                                 | true
        'a single empty annotation' | new BardAnnotation()                                             | true
        'a single empty Context'    | new BardAnnotation(contexts: [new Context()])                   | false // we want to see these
        'a non-empty annotations'   | new BardAnnotation(contexts: [new Context(contextItems: [new Annotation()])]) | false
    }

    void "test areOtherAnnotationsEmpty() #label"() {
        when:
        Boolean result = BardAnnotation.areOtherAnnotationsEmpty(annotations)

        then:
        assert result == expectedResult

        where:
        label                         | annotations                                          | expectedResult
        'no otherAnnotations at all'  | null                                                 | true
        'a single empty annotation'   | new BardAnnotation()                              | true
        'a non-empty annotations'     | new BardAnnotation(otherAnnotations: [new Annotation()]) | false
    }

    void "test findAssayContextsContainingKeys #label"() {
        when:
        final BardAnnotation bardAnnotation = objectMapper.readValue(BARD_ANNOTATION, BardAnnotation.class)
        List<Context> result = bardAnnotation.findAssayContextsContainingKeys(keys)

        then:
        assert result.size() == expectedContextCount

        where:
        label | keys | expectedContextCount
        'find footprint' | 'footprint' | 1
        'find species'   | 'species' | 1
        'find "a" for assays' | 'a' | 4
        'make sure experiment contexts are ignored' | 'result type' | 0
        'nothing found' | 'find nothing' | 0
    }

    void "test findAssayContextsContainingKeys with multiple args"() {
        when:
        final BardAnnotation bardAnnotation = objectMapper.readValue(BARD_ANNOTATION, BardAnnotation.class)
        List<Context> result = bardAnnotation.findAssayContextsContainingKeys('footprint', 'species')

        then:
        assert result.size() == 2
    }

    void "test findAssayContextsForExperimentalVariables"() {
        when:
        final BardAnnotation bardAnnotation = objectMapper.readValue(BARD_ANNOTATION, BardAnnotation.class)
        List<Context> result = bardAnnotation.findAssayContextsForExperimentalVariables()

        then:
        assert result.size() == 2
    }

    void "test invalid contextRef"() {
        when:
        final BardAnnotation bardAnnotation = objectMapper.readValue(BARD_ANNOTATION, BardAnnotation.class)
        bardAnnotation.contexts.each { it.contextItems.each { it.related = "" }}
        bardAnnotation.measures.each { it.comps.each { it.related = "" }}
        bardAnnotation.measures.first().comps.first().related = "assayContextRefs:35512"
        bardAnnotation.populateContextMeasureRelationships()

        then:
        assert bardAnnotation.measures.first().relatedContexts.isEmpty()

    }

    void "test unbalanced measure references where only context side has them"() {
        when:
        final BardAnnotation bardAnnotation = objectMapper.readValue(BARD_ANNOTATION, BardAnnotation.class)
        bardAnnotation.measures.each { it.comps.each { it.related = "" }}
        bardAnnotation.populateContextMeasureRelationships()

        then:
        Measure logAC50 = bardAnnotation.measures.get(0)
        Measure AC50 = bardAnnotation.measures.get(1)
        Measure percentActivity = bardAnnotation.measures.get(2)
        Context species = bardAnnotation.contexts.get(0)
        Context footprint = bardAnnotation.contexts.get(1)
        Context threshold = bardAnnotation.contexts.get(3)

        assert logAC50.relatedContexts.isEmpty()
        assert AC50.relatedContexts.size() == 2
        assert AC50.relatedContexts.get(0) == footprint
        assert AC50.relatedContexts.get(1) == threshold
        assert percentActivity.relatedContexts.size() == 1
        assert percentActivity.relatedContexts.get(0) == threshold

        assert threshold.relatedMeasures.size() == 2
        assert threshold.relatedMeasures.get(0) == AC50
        assert threshold.relatedMeasures.get(1) == percentActivity
        assert footprint.relatedMeasures.size() == 1
        assert footprint.relatedMeasures.get(0) == AC50
        assert species.relatedMeasures.isEmpty()

    }

    public static final String REAL_LIFE_ANNOATION = """
{
   "contexts" :
      [
         {
            "id" : 206352,
            "name" : "annotations for percent activity",
            "group" : "Experimental Variables",
            "comps" :
               [
                  {
                     "entityId" : null,
                     "entity" : "assay",
                     "source" : "cap-context",
                     "id" : 206352,
                     "display" : "680.0",
                     "contextRef" : "annotations for percent activity",
                     "contextGroup" : "Experimental Variables",
                     "key" : "screening concentration (molar)",
                     "value" : null,
                     "extValueId" : null,
                     "url" : null,
                     "displayOrder" : 0,
                     "related" : "measureRefs:54508"
                  }
               ]
         },
         {
            "id" : 206351,
            "name" : "annotations for percent activity",
            "group" : "Experimental Variables",
            "comps" :
               [
                  {
                     "entityId" : null,
                     "entity" : "assay",
                     "source" : "cap-context",
                     "id" : 206351,
                     "display" : null,
                     "contextRef" : "annotations for percent activity",
                     "contextGroup" : "Experimental Variables",
                     "key" : "screening concentration (molar)",
                     "value" : null,
                     "extValueId" : null,
                     "url" : null,
                     "displayOrder" : 0,
                     "related" : "measureRefs:54507"
                  },
                  {
                     "entityId" : null,
                     "entity" : "assay",
                     "source" : "cap-context",
                     "id" : 206351,
                     "display" : null,
                     "contextRef" : "annotations for percent activity",
                     "contextGroup" : "Experimental Variables",
                     "key" : "screening concentration (molar)",
                     "value" : null,
                     "extValueId" : null,
                     "url" : null,
                     "displayOrder" : 1,
                     "related" : "measureRefs:54507"
                  }
               ]
         },
         {
            "id" : 206350,
            "name" : "annotations for AC50",
            "group" : "Experimental Variables",
            "comps" :
               [
                  {
                     "entityId" : null,
                     "entity" : "assay",
                     "source" : "cap-context",
                     "id" : 206350,
                     "display" : null,
                     "contextRef" : "annotations for AC50",
                     "contextGroup" : "Experimental Variables",
                     "key" : "number of points",
                     "value" : null,
                     "extValueId" : null,
                     "url" : null,
                     "displayOrder" : 0,
                     "related" : "measureRefs:54502"
                  }
               ]
         },
         {
            "id" : 104736,
            "name" : "assay footprint",
            "group" : "Assay Design",
            "comps" :
               [
                  {
                     "entityId" : null,
                     "entity" : "assay",
                     "source" : "cap-context",
                     "id" : 104736,
                     "display" : "384-well plate",
                     "contextRef" : "assay footprint",
                     "contextGroup" : "Assay Design",
                     "key" : "assay footprint",
                     "value" : "384-well plate",
                     "extValueId" : null,
                     "url" : null,
                     "displayOrder" : 0,
                     "related" : null
                  }
               ]
         },
         {
            "id" : 104737,
            "name" : "activity threshold",
            "group" : "Experimental Variables",
            "comps" :
               [
                  {
                     "entityId" : null,
                     "entity" : "assay",
                     "source" : "cap-context",
                     "id" : 104737,
                     "display" : null,
                     "contextRef" : "activity threshold",
                     "contextGroup" : "Experimental Variables",
                     "key" : "result type",
                     "value" : null,
                     "extValueId" : null,
                     "url" : null,
                     "displayOrder" : 0,
                     "related" : null
                  },
                  {
                     "entityId" : null,
                     "entity" : "assay",
                     "source" : "cap-context",
                     "id" : 104737,
                     "display" : null,
                     "contextRef" : "activity threshold",
                     "contextGroup" : "Experimental Variables",
                     "key" : "activity threshold",
                     "value" : null,
                     "extValueId" : null,
                     "url" : null,
                     "displayOrder" : 1,
                     "related" : null
                  },
                  {
                     "entityId" : null,
                     "entity" : "assay",
                     "source" : "cap-context",
                     "id" : 104737,
                     "display" : null,
                     "contextRef" : "activity threshold",
                     "contextGroup" : "Experimental Variables",
                     "key" : "unit",
                     "value" : null,
                     "extValueId" : null,
                     "url" : null,
                     "displayOrder" : 2,
                     "related" : null
                  }
               ]
         },
         {
            "id" : 104738,
            "name" : "concentration-point number",
            "group" : "Experimental Variables",
            "comps" :
               [
                  {
                     "entityId" : null,
                     "entity" : "assay",
                     "source" : "cap-context",
                     "id" : 104738,
                     "display" : null,
                     "contextRef" : "concentration-point number",
                     "contextGroup" : "Experimental Variables",
                     "key" : "concentration-point number",
                     "value" : null,
                     "extValueId" : null,
                     "url" : null,
                     "displayOrder" : 0,
                     "related" : null
                  },
                  {
                     "entityId" : null,
                     "entity" : "assay",
                     "source" : "cap-context",
                     "id" : 104738,
                     "display" : null,
                     "contextRef" : "concentration-point number",
                     "contextGroup" : "Experimental Variables",
                     "key" : "number of replicates",
                     "value" : null,
                     "extValueId" : null,
                     "url" : null,
                     "displayOrder" : 1,
                     "related" : null
                  }
               ]
         },
         {
            "id" : 104739,
            "name" : "project lead name",
            "group" : "Experimental Variables",
            "comps" :
               [
                  {
                     "entityId" : null,
                     "entity" : "assay",
                     "source" : "cap-context",
                     "id" : 104739,
                     "display" : null,
                     "contextRef" : "project lead name",
                     "contextGroup" : "Experimental Variables",
                     "key" : "project lead name",
                     "value" : null,
                     "extValueId" : null,
                     "url" : null,
                     "displayOrder" : 0,
                     "related" : null
                  }
               ]
         },
         {
            "id" : 104740,
            "name" : "measured component",
            "group" : "Assay Components",
            "comps" :
               [
                  {
                     "entityId" : null,
                     "entity" : "assay",
                     "source" : "cap-context",
                     "id" : 104740,
                     "display" : "measured component",
                     "contextRef" : "measured component",
                     "contextGroup" : "Assay Components",
                     "key" : "assay component role",
                     "value" : "measured component",
                     "extValueId" : null,
                     "url" : null,
                     "displayOrder" : 0,
                     "related" : null
                  },
                  {
                     "entityId" : null,
                     "entity" : "assay",
                     "source" : "cap-context",
                     "id" : 104740,
                     "display" : "CellTiter-Glo Luminescent Cell Viability Assay",
                     "contextRef" : "measured component",
                     "contextGroup" : "Assay Components",
                     "key" : "assay component type",
                     "value" : "CellTiter-Glo Luminescent Cell Viability Assay",
                     "extValueId" : null,
                     "url" : null,
                     "displayOrder" : 1,
                     "related" : null
                  },
                  {
                     "entityId" : null,
                     "entity" : "assay",
                     "source" : "cap-context",
                     "id" : 104740,
                     "display" : "CellTiter-Glo",
                     "contextRef" : "measured component",
                     "contextGroup" : "Assay Components",
                     "key" : "assay component name",
                     "value" : null,
                     "extValueId" : null,
                     "url" : null,
                     "displayOrder" : 2,
                     "related" : null
                  }
               ]
         },
         {
            "id" : 104741,
            "name" : "Measured component",
            "group" : "Assay Readout",
            "comps" :
               [
                  {
                     "entityId" : null,
                     "entity" : "assay",
                     "source" : "cap-context",
                     "id" : 104741,
                     "display" : "measured component",
                     "contextRef" : "Measured component",
                     "contextGroup" : "Assay Readout",
                     "key" : "detection role",
                     "value" : "measured component",
                     "extValueId" : null,
                     "url" : null,
                     "displayOrder" : 0,
                     "related" : null
                  },
                  {
                     "entityId" : null,
                     "entity" : "assay",
                     "source" : "cap-context",
                     "id" : 104741,
                     "display" : "CellTiter-Glo",
                     "contextRef" : "Measured component",
                     "contextGroup" : "Assay Readout",
                     "key" : "assay component name",
                     "value" : null,
                     "extValueId" : null,
                     "url" : null,
                     "displayOrder" : 1,
                     "related" : null
                  }
               ]
         },
         {
            "id" : 104731,
            "name" : "biology",
            "group" : "Biology",
            "comps" :
               [
                  {
                     "entityId" : null,
                     "entity" : "assay",
                     "source" : "cap-context",
                     "id" : 104731,
                     "display" : "biological process",
                     "contextRef" : "biology",
                     "contextGroup" : "Biology",
                     "key" : "biology",
                     "value" : "biological process",
                     "extValueId" : null,
                     "url" : null,
                     "displayOrder" : 0,
                     "related" : null
                  },
                  {
                     "entityId" : null,
                     "entity" : "assay",
                     "source" : "cap-context",
                     "id" : 104731,
                     "display" : "cell death",
                     "contextRef" : "biology",
                     "contextGroup" : "Biology",
                     "key" : "GO biological process term",
                     "value" : null,
                     "extValueId" : "GO:0008219",
                     "url" : "http://amigo.geneontology.org/cgi-bin/amigo/term_details?term=GO:0008219",
                     "displayOrder" : 1,
                     "related" : null
                  }
               ]
         },
         {
            "id" : 104735,
            "name" : "assay readout",
            "group" : "Assay Readout",
            "comps" :
               [
                  {
                     "entityId" : null,
                     "entity" : "assay",
                     "source" : "cap-context",
                     "id" : 104735,
                     "display" : "single parameter",
                     "contextRef" : "assay readout",
                     "contextGroup" : "Assay Readout",
                     "key" : "assay readout",
                     "value" : "single parameter",
                     "extValueId" : null,
                     "url" : null,
                     "displayOrder" : 0,
                     "related" : null
                  },
                  {
                     "entityId" : null,
                     "entity" : "assay",
                     "source" : "cap-context",
                     "id" : 104735,
                     "display" : "measured value",
                     "contextRef" : "assay readout",
                     "contextGroup" : "Assay Readout",
                     "key" : "readout type",
                     "value" : "measured value",
                     "extValueId" : null,
                     "url" : null,
                     "displayOrder" : 1,
                     "related" : null
                  },
                  {
                     "entityId" : null,
                     "entity" : "assay",
                     "source" : "cap-context",
                     "id" : 104735,
                     "display" : "signal decrease corresponding to inhibition",
                     "contextRef" : "assay readout",
                     "contextGroup" : "Assay Readout",
                     "key" : "readout signal direction",
                     "value" : "signal decrease corresponding to inhibition",
                     "extValueId" : null,
                     "url" : null,
                     "displayOrder" : 2,
                     "related" : null
                  }
               ]
         },
         {
            "id" : 104734,
            "name" : "detection method",
            "group" : "Assay Readout",
            "comps" :
               [
                  {
                     "entityId" : null,
                     "entity" : "assay",
                     "source" : "cap-context",
                     "id" : 104734,
                     "display" : "chemiluminescence",
                     "contextRef" : "detection method",
                     "contextGroup" : "Assay Readout",
                     "key" : "detection method type",
                     "value" : "chemiluminescence",
                     "extValueId" : null,
                     "url" : null,
                     "displayOrder" : 0,
                     "related" : null
                  },
                  {
                     "entityId" : null,
                     "entity" : "assay",
                     "source" : "cap-context",
                     "id" : 104734,
                     "display" : "EnVision multilabel reader",
                     "contextRef" : "detection method",
                     "contextGroup" : "Assay Readout",
                     "key" : "detection instrument name",
                     "value" : "EnVision multilabel reader",
                     "extValueId" : null,
                     "url" : null,
                     "displayOrder" : 1,
                     "related" : null
                  },
                  {
                     "entityId" : null,
                     "entity" : "assay",
                     "source" : "cap-context",
                     "id" : 104734,
                     "display" : "Perkin Elmer",
                     "contextRef" : "detection method",
                     "contextGroup" : "Assay Readout",
                     "key" : "instrument manufacturer name",
                     "value" : "Perkin Elmer",
                     "extValueId" : null,
                     "url" : null,
                     "displayOrder" : 2,
                     "related" : null
                  }
               ]
         },
         {
            "id" : 104733,
            "name" : "target cell",
            "group" : "Assay Components",
            "comps" :
               [
                  {
                     "entityId" : null,
                     "entity" : "assay",
                     "source" : "cap-context",
                     "id" : 104733,
                     "display" : "target cell",
                     "contextRef" : "target cell",
                     "contextGroup" : "Assay Components",
                     "key" : "assay component role",
                     "value" : "target cell",
                     "extValueId" : null,
                     "url" : null,
                     "displayOrder" : 0,
                     "related" : null
                  },
                  {
                     "entityId" : null,
                     "entity" : "assay",
                     "source" : "cap-context",
                     "id" : 104733,
                     "display" : "cultured cell",
                     "contextRef" : "target cell",
                     "contextGroup" : "Assay Components",
                     "key" : "assay component type",
                     "value" : "cultured cell",
                     "extValueId" : null,
                     "url" : null,
                     "displayOrder" : 1,
                     "related" : null
                  },
                  {
                     "entityId" : null,
                     "entity" : "assay",
                     "source" : "cap-context",
                     "id" : 104733,
                     "display" : "CHO",
                     "contextRef" : "target cell",
                     "contextGroup" : "Assay Components",
                     "key" : "assay component",
                     "value" : "CHO",
                     "extValueId" : null,
                     "url" : null,
                     "displayOrder" : 2,
                     "related" : null
                  },
                  {
                     "entityId" : null,
                     "entity" : "assay",
                     "source" : "cap-context",
                     "id" : 104733,
                     "display" : "133333.0 cells/mL",
                     "contextRef" : "target cell",
                     "contextGroup" : "Assay Components",
                     "key" : "assay component concentration (cells/volume)",
                     "value" : null,
                     "extValueId" : null,
                     "url" : null,
                     "displayOrder" : 3,
                     "related" : null
                  },
                  {
                     "entityId" : null,
                     "entity" : "assay",
                     "source" : "cap-context",
                     "id" : 104733,
                     "display" : "ldlA[mSR-BI]",
                     "contextRef" : "target cell",
                     "contextGroup" : "Assay Components",
                     "key" : "assay component name",
                     "value" : null,
                     "extValueId" : null,
                     "url" : null,
                     "displayOrder" : 4,
                     "related" : null
                  },
                  {
                     "entityId" : null,
                     "entity" : "assay",
                     "source" : "cap-context",
                     "id" : 104733,
                     "display" : "Cricetulus griseus",
                     "contextRef" : "target cell",
                     "contextGroup" : "Assay Components",
                     "key" : "species name",
                     "value" : null,
                     "extValueId" : "10029",
                     "url" : "http://www.ncbi.nlm.nih.gov/Taxonomy/Browser/wwwtax.cgi?id=10029",
                     "displayOrder" : 5,
                     "related" : null
                  }
               ]
         },
         {
            "id" : 104732,
            "name" : "assay protocol",
            "group" : "Assay Protocol",
            "comps" :
               [
                  {
                     "entityId" : null,
                     "entity" : "assay",
                     "source" : "cap-context",
                     "id" : 104732,
                     "display" : "cell-based format",
                     "contextRef" : "assay protocol",
                     "contextGroup" : "Assay Protocol",
                     "key" : "assay format",
                     "value" : "cell-based format",
                     "extValueId" : null,
                     "url" : null,
                     "displayOrder" : 0,
                     "related" : null
                  },
                  {
                     "entityId" : null,
                     "entity" : "assay",
                     "source" : "cap-context",
                     "id" : 104732,
                     "display" : "toxicity assay",
                     "contextRef" : "assay protocol",
                     "contextGroup" : "Assay Protocol",
                     "key" : "assay type",
                     "value" : "toxicity assay",
                     "extValueId" : null,
                     "url" : null,
                     "displayOrder" : 1,
                     "related" : null
                  }
               ]
         },
         {
            "id" : 93722,
            "name" : "Base medium",
            "group" : "Assay Components",
            "comps" :
               [
                  {
                     "entityId" : null,
                     "entity" : "assay",
                     "source" : "cap-context",
                     "id" : 93722,
                     "display" : "assay medium",
                     "contextRef" : "Base medium",
                     "contextGroup" : "Assay Components",
                     "key" : "assay component role",
                     "value" : "assay medium",
                     "extValueId" : null,
                     "url" : null,
                     "displayOrder" : 0,
                     "related" : null
                  },
                  {
                     "entityId" : null,
                     "entity" : "assay",
                     "source" : "cap-context",
                     "id" : 93722,
                     "display" : "Ham's F12 medium",
                     "contextRef" : "Base medium",
                     "contextGroup" : "Assay Components",
                     "key" : "assay component name",
                     "value" : null,
                     "extValueId" : null,
                     "url" : null,
                     "displayOrder" : 1,
                     "related" : null
                  }
               ]
         },
         {
            "id" : 93723,
            "name" : "Media serum",
            "group" : "Assay Components",
            "comps" :
               [
                  {
                     "entityId" : null,
                     "entity" : "assay",
                     "source" : "cap-context",
                     "id" : 93723,
                     "display" : "Fetal bovine serum",
                     "contextRef" : "Media serum",
                     "contextGroup" : "Assay Components",
                     "key" : "assay component name",
                     "value" : null,
                     "extValueId" : null,
                     "url" : null,
                     "displayOrder" : 0,
                     "related" : null
                  },
                  {
                     "entityId" : null,
                     "entity" : "assay",
                     "source" : "cap-context",
                     "id" : 93723,
                     "display" : "media component",
                     "contextRef" : "Media serum",
                     "contextGroup" : "Assay Components",
                     "key" : "assay component role",
                     "value" : "media component",
                     "extValueId" : null,
                     "url" : null,
                     "displayOrder" : 1,
                     "related" : null
                  },
                  {
                     "entityId" : null,
                     "entity" : "assay",
                     "source" : "cap-context",
                     "id" : 93723,
                     "display" : "= 10.0 %",
                     "contextRef" : "Media serum",
                     "contextGroup" : "Assay Components",
                     "key" : "assay component concentration (%/vol)",
                     "value" : null,
                     "extValueId" : null,
                     "url" : null,
                     "displayOrder" : 2,
                     "related" : null
                  }
               ]
         },
         {
            "id" : 93727,
            "name" : "incubation time",
            "group" : "Assay Design",
            "comps" :
               [
                  {
                     "entityId" : null,
                     "entity" : "assay",
                     "source" : "cap-context",
                     "id" : 93727,
                     "display" : "= 1440.0 min",
                     "contextRef" : "incubation time",
                     "contextGroup" : "Assay Design",
                     "key" : "incubation time",
                     "value" : null,
                     "extValueId" : null,
                     "url" : null,
                     "displayOrder" : 0,
                     "related" : null
                  }
               ]
         },
         {
            "id" : 1577,
            "name" : "screening concentration (molar)",
            "group" : "Experimental Variables",
            "comps" :
               [
                  {
                     "entityId" : null,
                     "entity" : "assay",
                     "source" : "cap-context",
                     "id" : 1577,
                     "display" : "0 - 6800 uM",
                     "contextRef" : "screening concentration (molar)",
                     "contextGroup" : "Experimental Variables",
                     "key" : "screening concentration (molar)",
                     "value" : null,
                     "extValueId" : null,
                     "url" : null,
                     "displayOrder" : 0,
                     "related" : "measureRefs:54508,2309,54534"
                  }
               ]
         },
         {
            "id" : 1575,
            "name" : "number of points",
            "group" : "Experimental Variables",
            "comps" :
               [
                  {
                     "entityId" : null,
                     "entity" : "assay",
                     "source" : "cap-context",
                     "id" : 1575,
                     "display" : null,
                     "contextRef" : "number of points",
                     "contextGroup" : "Experimental Variables",
                     "key" : "number of points",
                     "value" : null,
                     "extValueId" : null,
                     "url" : null,
                     "displayOrder" : 0,
                     "related" : "measureRefs:54528,54502,2307"
                  }
               ]
         },
         {
            "id" : 213339,
            "name" : "annotations for PubChem outcome",
            "group" : "Experimental Variables",
            "comps" :
               [
                  {
                     "entityId" : null,
                     "entity" : "assay",
                     "source" : "cap-context",
                     "id" : 213339,
                     "display" : null,
                     "contextRef" : "annotations for PubChem outcome",
                     "contextGroup" : "Experimental Variables",
                     "key" : "comment",
                     "value" : null,
                     "extValueId" : null,
                     "url" : null,
                     "displayOrder" : 0,
                     "related" : "measureRefs:2305"
                  },
                  {
                     "entityId" : null,
                     "entity" : "assay",
                     "source" : "cap-context",
                     "id" : 213339,
                     "display" : null,
                     "contextRef" : "annotations for PubChem outcome",
                     "contextGroup" : "Experimental Variables",
                     "key" : "comment",
                     "value" : null,
                     "extValueId" : null,
                     "url" : null,
                     "displayOrder" : 1,
                     "related" : "measureRefs:2305"
                  }
               ]
         },
         {
            "id" : 213340,
            "name" : "annotations for AC50",
            "group" : "Experimental Variables",
            "comps" :
               [
                  {
                     "entityId" : null,
                     "entity" : "assay",
                     "source" : "cap-context",
                     "id" : 213340,
                     "display" : null,
                     "contextRef" : "annotations for AC50",
                     "contextGroup" : "Experimental Variables",
                     "key" : "number of points",
                     "value" : null,
                     "extValueId" : null,
                     "url" : null,
                     "displayOrder" : 0,
                     "related" : "measureRefs:54528"
                  },
                  {
                     "entityId" : null,
                     "entity" : "assay",
                     "source" : "cap-context",
                     "id" : 213340,
                     "display" : null,
                     "contextRef" : "annotations for AC50",
                     "contextGroup" : "Experimental Variables",
                     "key" : "number of points",
                     "value" : null,
                     "extValueId" : null,
                     "url" : null,
                     "displayOrder" : 1,
                     "related" : "measureRefs:54528"
                  }
               ]
         },
         {
            "id" : 213341,
            "name" : "annotations for percent activity",
            "group" : "Experimental Variables",
            "comps" :
               [
                  {
                     "entityId" : null,
                     "entity" : "assay",
                     "source" : "cap-context",
                     "id" : 213341,
                     "display" : null,
                     "contextRef" : "annotations for percent activity",
                     "contextGroup" : "Experimental Variables",
                     "key" : "screening concentration (molar)",
                     "value" : null,
                     "extValueId" : null,
                     "url" : null,
                     "displayOrder" : 0,
                     "related" : "measureRefs:54533"
                  },
                  {
                     "entityId" : null,
                     "entity" : "assay",
                     "source" : "cap-context",
                     "id" : 213341,
                     "display" : null,
                     "contextRef" : "annotations for percent activity",
                     "contextGroup" : "Experimental Variables",
                     "key" : "screening concentration (molar)",
                     "value" : null,
                     "extValueId" : null,
                     "url" : null,
                     "displayOrder" : 1,
                     "related" : "measureRefs:54533"
                  }
               ]
         },
         {
            "id" : 213342,
            "name" : "annotations for percent activity",
            "group" : "Experimental Variables",
            "comps" :
               [
                  {
                     "entityId" : null,
                     "entity" : "assay",
                     "source" : "cap-context",
                     "id" : 213342,
                     "display" : "680.0",
                     "contextRef" : "annotations for percent activity",
                     "contextGroup" : "Experimental Variables",
                     "key" : "screening concentration (molar)",
                     "value" : null,
                     "extValueId" : null,
                     "url" : null,
                     "displayOrder" : 0,
                     "related" : "measureRefs:54534,54507,54533,55602"
                  }
               ]
         }
      ],
   "measures" :
      [
         {
            "id" : 2306,
            "name" : null,
            "group" : null,
            "comps" :
               [
                  {
                     "entityId" : null,
                     "entity" : "assay",
                     "source" : "cap-measure",
                     "id" : 2306,
                     "display" : "PubChem activity score",
                     "contextRef" : null,
                     "contextGroup" : null,
                     "key" : null,
                     "value" : "PubChem activity score",
                     "extValueId" : "parentMeasure:2305",
                     "url" : null,
                     "displayOrder" : 0,
                     "related" : "|parentMeasure:2305"
                  }
               ]
         },
         {
            "id" : 54506,
            "name" : null,
            "group" : null,
            "comps" :
               [
                  {
                     "entityId" : null,
                     "entity" : "assay",
                     "source" : "cap-measure",
                     "id" : 54506,
                     "display" : "Hill sinf",
                     "contextRef" : null,
                     "contextGroup" : null,
                     "key" : null,
                     "value" : "Hill sinf",
                     "extValueId" : "parentMeasure:54502",
                     "url" : null,
                     "displayOrder" : 0,
                     "related" : "|parentMeasure:54502"
                  }
               ]
         },
         {
            "id" : 2307,
            "name" : null,
            "group" : null,
            "comps" :
               [
                  {
                     "entityId" : null,
                     "entity" : "assay",
                     "source" : "cap-measure",
                     "id" : 2307,
                     "display" : "AC50",
                     "contextRef" : null,
                     "contextGroup" : null,
                     "key" : "micromolar",
                     "value" : "AC50",
                     "extValueId" : "parentMeasure:2309",
                     "url" : null,
                     "displayOrder" : 0,
                     "related" : "assayContextRefs:1575|parentMeasure:2309"
                  }
               ]
         },
         {
            "id" : 54507,
            "name" : null,
            "group" : null,
            "comps" :
               [
                  {
                     "entityId" : null,
                     "entity" : "assay",
                     "source" : "cap-measure",
                     "id" : 54507,
                     "display" : "percent activity",
                     "contextRef" : null,
                     "contextGroup" : null,
                     "key" : null,
                     "value" : "percent activity",
                     "extValueId" : "parentMeasure:54502",
                     "url" : null,
                     "displayOrder" : 0,
                     "related" : "assayContextRefs:206351,213342|parentMeasure:54502"
                  }
               ]
         },
         {
            "id" : 54504,
            "name" : null,
            "group" : null,
            "comps" :
               [
                  {
                     "entityId" : null,
                     "entity" : "assay",
                     "source" : "cap-measure",
                     "id" : 54504,
                     "display" : "Hill coefficient",
                     "contextRef" : null,
                     "contextGroup" : null,
                     "key" : null,
                     "value" : "Hill coefficient",
                     "extValueId" : "parentMeasure:54502",
                     "url" : null,
                     "displayOrder" : 0,
                     "related" : "|parentMeasure:54502"
                  }
               ]
         },
         {
            "id" : 54505,
            "name" : null,
            "group" : null,
            "comps" :
               [
                  {
                     "entityId" : null,
                     "entity" : "assay",
                     "source" : "cap-measure",
                     "id" : 54505,
                     "display" : "Hill s0",
                     "contextRef" : null,
                     "contextGroup" : null,
                     "key" : null,
                     "value" : "Hill s0",
                     "extValueId" : "parentMeasure:54502",
                     "url" : null,
                     "displayOrder" : 0,
                     "related" : "|parentMeasure:54502"
                  }
               ]
         },
         {
            "id" : 2305,
            "name" : null,
            "group" : null,
            "comps" :
               [
                  {
                     "entityId" : null,
                     "entity" : "assay",
                     "source" : "cap-measure",
                     "id" : 2305,
                     "display" : "PubChem outcome",
                     "contextRef" : null,
                     "contextGroup" : null,
                     "key" : null,
                     "value" : "PubChem outcome",
                     "extValueId" : null,
                     "url" : null,
                     "displayOrder" : 0,
                     "related" : "assayContextRefs:213339"
                  }
               ]
         },
         {
            "id" : 2310,
            "name" : null,
            "group" : null,
            "comps" :
               [
                  {
                     "entityId" : null,
                     "entity" : "assay",
                     "source" : "cap-measure",
                     "id" : 2310,
                     "display" : "Hill coefficient",
                     "contextRef" : null,
                     "contextGroup" : null,
                     "key" : null,
                     "value" : "Hill coefficient",
                     "extValueId" : "parentMeasure:2309",
                     "url" : null,
                     "displayOrder" : 0,
                     "related" : "|parentMeasure:2309"
                  }
               ]
         },
         {
            "id" : 2311,
            "name" : null,
            "group" : null,
            "comps" :
               [
                  {
                     "entityId" : null,
                     "entity" : "assay",
                     "source" : "cap-measure",
                     "id" : 2311,
                     "display" : "Hill sinf",
                     "contextRef" : null,
                     "contextGroup" : null,
                     "key" : null,
                     "value" : "Hill sinf",
                     "extValueId" : "parentMeasure:2309",
                     "url" : null,
                     "displayOrder" : 0,
                     "related" : "|parentMeasure:2309"
                  }
               ]
         },
         {
            "id" : 2308,
            "name" : null,
            "group" : null,
            "comps" :
               [
                  {
                     "entityId" : null,
                     "entity" : "assay",
                     "source" : "cap-measure",
                     "id" : 2308,
                     "display" : "percent activity",
                     "contextRef" : null,
                     "contextGroup" : null,
                     "key" : "percent",
                     "value" : "percent activity",
                     "extValueId" : "parentMeasure:2309",
                     "url" : null,
                     "displayOrder" : 0,
                     "related" : "|parentMeasure:2309"
                  }
               ]
         },
         {
            "id" : 54508,
            "name" : null,
            "group" : null,
            "comps" :
               [
                  {
                     "entityId" : null,
                     "entity" : "assay",
                     "source" : "cap-measure",
                     "id" : 54508,
                     "display" : "percent activity",
                     "contextRef" : null,
                     "contextGroup" : null,
                     "key" : null,
                     "value" : "percent activity",
                     "extValueId" : "parentMeasure:54502",
                     "url" : null,
                     "displayOrder" : 0,
                     "related" : "assayContextRefs:1577,206352|parentMeasure:54502"
                  }
               ]
         },
         {
            "id" : 2309,
            "name" : null,
            "group" : null,
            "comps" :
               [
                  {
                     "entityId" : null,
                     "entity" : "assay",
                     "source" : "cap-measure",
                     "id" : 2309,
                     "display" : "percent activity",
                     "contextRef" : null,
                     "contextGroup" : null,
                     "key" : "percent",
                     "value" : "percent activity",
                     "extValueId" : null,
                     "url" : null,
                     "displayOrder" : 0,
                     "related" : "assayContextRefs:1577"
                  }
               ]
         },
         {
            "id" : 2312,
            "name" : null,
            "group" : null,
            "comps" :
               [
                  {
                     "entityId" : null,
                     "entity" : "assay",
                     "source" : "cap-measure",
                     "id" : 2312,
                     "display" : "Hill s0",
                     "contextRef" : null,
                     "contextGroup" : null,
                     "key" : null,
                     "value" : "Hill s0",
                     "extValueId" : "parentMeasure:2309",
                     "url" : null,
                     "displayOrder" : 0,
                     "related" : "|parentMeasure:2309"
                  }
               ]
         },
         {
            "id" : 2313,
            "name" : null,
            "group" : null,
            "comps" :
               [
                  {
                     "entityId" : null,
                     "entity" : "assay",
                     "source" : "cap-measure",
                     "id" : 2313,
                     "display" : "pAC50",
                     "contextRef" : null,
                     "contextGroup" : null,
                     "key" : null,
                     "value" : "pAC50",
                     "extValueId" : "parentMeasure:2307",
                     "url" : null,
                     "displayOrder" : 0,
                     "related" : "|parentMeasure:2307"
                  }
               ]
         },
         {
            "id" : 54502,
            "name" : null,
            "group" : null,
            "comps" :
               [
                  {
                     "entityId" : null,
                     "entity" : "assay",
                     "source" : "cap-measure",
                     "id" : 54502,
                     "display" : "AC50",
                     "contextRef" : null,
                     "contextGroup" : null,
                     "key" : null,
                     "value" : "AC50",
                     "extValueId" : null,
                     "url" : null,
                     "displayOrder" : 0,
                     "related" : "assayContextRefs:206350,1575"
                  }
               ]
         },
         {
            "id" : 54503,
            "name" : null,
            "group" : null,
            "comps" :
               [
                  {
                     "entityId" : null,
                     "entity" : "assay",
                     "source" : "cap-measure",
                     "id" : 54503,
                     "display" : "pAC50",
                     "contextRef" : null,
                     "contextGroup" : null,
                     "key" : null,
                     "value" : "pAC50",
                     "extValueId" : "parentMeasure:54502",
                     "url" : null,
                     "displayOrder" : 0,
                     "related" : "|parentMeasure:54502"
                  }
               ]
         },
         {
            "id" : 54527,
            "name" : null,
            "group" : null,
            "comps" :
               [
                  {
                     "entityId" : null,
                     "entity" : "assay",
                     "source" : "cap-measure",
                     "id" : 54527,
                     "display" : "pAC50",
                     "contextRef" : null,
                     "contextGroup" : null,
                     "key" : null,
                     "value" : "pAC50",
                     "extValueId" : "parentMeasure:54526",
                     "url" : null,
                     "displayOrder" : 0,
                     "related" : "|parentMeasure:54526"
                  }
               ]
         },
         {
            "id" : 54526,
            "name" : null,
            "group" : null,
            "comps" :
               [
                  {
                     "entityId" : null,
                     "entity" : "assay",
                     "source" : "cap-measure",
                     "id" : 54526,
                     "display" : "AC50",
                     "contextRef" : null,
                     "contextGroup" : null,
                     "key" : null,
                     "value" : "AC50",
                     "extValueId" : null,
                     "url" : null,
                     "displayOrder" : 0,
                     "related" : ""
                  }
               ]
         },
         {
            "id" : 55604,
            "name" : null,
            "group" : null,
            "comps" :
               [
                  {
                     "entityId" : null,
                     "entity" : "assay",
                     "source" : "cap-measure",
                     "id" : 55604,
                     "display" : "PubChem activity score",
                     "contextRef" : null,
                     "contextGroup" : null,
                     "key" : null,
                     "value" : "PubChem activity score",
                     "extValueId" : "parentMeasure:55603",
                     "url" : null,
                     "displayOrder" : 0,
                     "related" : "|parentMeasure:55603"
                  }
               ]
         },
         {
            "id" : 55605,
            "name" : null,
            "group" : null,
            "comps" :
               [
                  {
                     "entityId" : null,
                     "entity" : "assay",
                     "source" : "cap-measure",
                     "id" : 55605,
                     "display" : "pAC50",
                     "contextRef" : null,
                     "contextGroup" : null,
                     "key" : null,
                     "value" : "pAC50",
                     "extValueId" : "parentMeasure:54526",
                     "url" : null,
                     "displayOrder" : 0,
                     "related" : "|parentMeasure:54526"
                  }
               ]
         },
         {
            "id" : 55602,
            "name" : null,
            "group" : null,
            "comps" :
               [
                  {
                     "entityId" : null,
                     "entity" : "assay",
                     "source" : "cap-measure",
                     "id" : 55602,
                     "display" : "percent activity",
                     "contextRef" : null,
                     "contextGroup" : null,
                     "key" : "percent",
                     "value" : "percent activity",
                     "extValueId" : null,
                     "url" : null,
                     "displayOrder" : 0,
                     "related" : "assayContextRefs:213342"
                  }
               ]
         },
         {
            "id" : 55603,
            "name" : null,
            "group" : null,
            "comps" :
               [
                  {
                     "entityId" : null,
                     "entity" : "assay",
                     "source" : "cap-measure",
                     "id" : 55603,
                     "display" : "PubChem outcome",
                     "contextRef" : null,
                     "contextGroup" : null,
                     "key" : null,
                     "value" : "PubChem outcome",
                     "extValueId" : null,
                     "url" : null,
                     "displayOrder" : 0,
                     "related" : ""
                  }
               ]
         },
         {
            "id" : 2367,
            "name" : null,
            "group" : null,
            "comps" :
               [
                  {
                     "entityId" : null,
                     "entity" : "assay",
                     "source" : "cap-measure",
                     "id" : 2367,
                     "display" : "PubChem outcome",
                     "contextRef" : null,
                     "contextGroup" : null,
                     "key" : null,
                     "value" : "PubChem outcome",
                     "extValueId" : "parentMeasure:2305",
                     "url" : null,
                     "displayOrder" : 0,
                     "related" : "|parentMeasure:2305"
                  }
               ]
         },
         {
            "id" : 2371,
            "name" : null,
            "group" : null,
            "comps" :
               [
                  {
                     "entityId" : null,
                     "entity" : "assay",
                     "source" : "cap-measure",
                     "id" : 2371,
                     "display" : "AC50",
                     "contextRef" : null,
                     "contextGroup" : null,
                     "key" : "micromolar",
                     "value" : "AC50",
                     "extValueId" : "parentMeasure:2307",
                     "url" : null,
                     "displayOrder" : 0,
                     "related" : "|parentMeasure:2307"
                  }
               ]
         },
         {
            "id" : 2368,
            "name" : null,
            "group" : null,
            "comps" :
               [
                  {
                     "entityId" : null,
                     "entity" : "assay",
                     "source" : "cap-measure",
                     "id" : 2368,
                     "display" : "PubChem activity score",
                     "contextRef" : null,
                     "contextGroup" : null,
                     "key" : null,
                     "value" : "PubChem activity score",
                     "extValueId" : "parentMeasure:2306",
                     "url" : null,
                     "displayOrder" : 0,
                     "related" : "|parentMeasure:2306"
                  }
               ]
         },
         {
            "id" : 32379,
            "name" : null,
            "group" : null,
            "comps" :
               [
                  {
                     "entityId" : null,
                     "entity" : "assay",
                     "source" : "cap-measure",
                     "id" : 32379,
                     "display" : "pAC50",
                     "contextRef" : null,
                     "contextGroup" : null,
                     "key" : null,
                     "value" : "pAC50",
                     "extValueId" : "parentMeasure:2371",
                     "url" : null,
                     "displayOrder" : 0,
                     "related" : "|parentMeasure:2371"
                  }
               ]
         },
         {
            "id" : 54534,
            "name" : null,
            "group" : null,
            "comps" :
               [
                  {
                     "entityId" : null,
                     "entity" : "assay",
                     "source" : "cap-measure",
                     "id" : 54534,
                     "display" : "percent activity",
                     "contextRef" : null,
                     "contextGroup" : null,
                     "key" : null,
                     "value" : "percent activity",
                     "extValueId" : "parentMeasure:54528",
                     "url" : null,
                     "displayOrder" : 0,
                     "related" : "assayContextRefs:213342,1577|parentMeasure:54528"
                  }
               ]
         },
         {
            "id" : 54533,
            "name" : null,
            "group" : null,
            "comps" :
               [
                  {
                     "entityId" : null,
                     "entity" : "assay",
                     "source" : "cap-measure",
                     "id" : 54533,
                     "display" : "percent activity",
                     "contextRef" : null,
                     "contextGroup" : null,
                     "key" : null,
                     "value" : "percent activity",
                     "extValueId" : "parentMeasure:54528",
                     "url" : null,
                     "displayOrder" : 0,
                     "related" : "assayContextRefs:213341,213342|parentMeasure:54528"
                  }
               ]
         },
         {
            "id" : 54532,
            "name" : null,
            "group" : null,
            "comps" :
               [
                  {
                     "entityId" : null,
                     "entity" : "assay",
                     "source" : "cap-measure",
                     "id" : 54532,
                     "display" : "Hill sinf",
                     "contextRef" : null,
                     "contextGroup" : null,
                     "key" : null,
                     "value" : "Hill sinf",
                     "extValueId" : "parentMeasure:54528",
                     "url" : null,
                     "displayOrder" : 0,
                     "related" : "|parentMeasure:54528"
                  }
               ]
         },
         {
            "id" : 54531,
            "name" : null,
            "group" : null,
            "comps" :
               [
                  {
                     "entityId" : null,
                     "entity" : "assay",
                     "source" : "cap-measure",
                     "id" : 54531,
                     "display" : "Hill s0",
                     "contextRef" : null,
                     "contextGroup" : null,
                     "key" : null,
                     "value" : "Hill s0",
                     "extValueId" : "parentMeasure:54528",
                     "url" : null,
                     "displayOrder" : 0,
                     "related" : "|parentMeasure:54528"
                  }
               ]
         },
         {
            "id" : 54530,
            "name" : null,
            "group" : null,
            "comps" :
               [
                  {
                     "entityId" : null,
                     "entity" : "assay",
                     "source" : "cap-measure",
                     "id" : 54530,
                     "display" : "Hill coefficient",
                     "contextRef" : null,
                     "contextGroup" : null,
                     "key" : null,
                     "value" : "Hill coefficient",
                     "extValueId" : "parentMeasure:54528",
                     "url" : null,
                     "displayOrder" : 0,
                     "related" : "|parentMeasure:54528"
                  }
               ]
         },
         {
            "id" : 54529,
            "name" : null,
            "group" : null,
            "comps" :
               [
                  {
                     "entityId" : null,
                     "entity" : "assay",
                     "source" : "cap-measure",
                     "id" : 54529,
                     "display" : "pAC50",
                     "contextRef" : null,
                     "contextGroup" : null,
                     "key" : null,
                     "value" : "pAC50",
                     "extValueId" : "parentMeasure:54528",
                     "url" : null,
                     "displayOrder" : 0,
                     "related" : "|parentMeasure:54528"
                  }
               ]
         },
         {
            "id" : 54528,
            "name" : null,
            "group" : null,
            "comps" :
               [
                  {
                     "entityId" : null,
                     "entity" : "assay",
                     "source" : "cap-measure",
                     "id" : 54528,
                     "display" : "AC50",
                     "contextRef" : null,
                     "contextGroup" : null,
                     "key" : null,
                     "value" : "AC50",
                     "extValueId" : "parentMeasure:54526",
                     "url" : null,
                     "displayOrder" : 0,
                     "related" : "assayContextRefs:213340,1575|parentMeasure:54526"
                  }
               ]
         },
         {
            "id" : 54537,
            "name" : null,
            "group" : null,
            "comps" :
               [
                  {
                     "entityId" : null,
                     "entity" : "assay",
                     "source" : "cap-measure",
                     "id" : 54537,
                     "display" : "pAC50",
                     "contextRef" : null,
                     "contextGroup" : null,
                     "key" : null,
                     "value" : "pAC50",
                     "extValueId" : null,
                     "url" : null,
                     "displayOrder" : 0,
                     "related" : ""
                  }
               ]
         },
         {
            "id" : 54536,
            "name" : null,
            "group" : null,
            "comps" :
               [
                  {
                     "entityId" : null,
                     "entity" : "assay",
                     "source" : "cap-measure",
                     "id" : 54536,
                     "display" : "percent response",
                     "contextRef" : null,
                     "contextGroup" : null,
                     "key" : null,
                     "value" : "percent response",
                     "extValueId" : "parentMeasure:54502",
                     "url" : null,
                     "displayOrder" : 0,
                     "related" : "|parentMeasure:54502"
                  }
               ]
         }
      ],
   "docs" :
      [
         {
            "id" : 44922,
            "name" : "External URL",
            "group" : null,
            "comps" :
               [
                  {
                     "entityId" : null,
                     "entity" : "assay",
                     "source" : "cap-doc",
                     "id" : 44922,
                     "display" : "External Database Link",
                     "contextRef" : "External URL",
                     "contextGroup" : null,
                     "key" : "doc",
                     "value" : "http://www.broadinstitute.org",
                     "extValueId" : "http://www.broadinstitute.org",
                     "url" : null,
                     "displayOrder" : 0,
                     "related" : null
                  }
               ]
         },
         {
            "id" : 44921,
            "name" : "Publication",
            "group" : null,
            "comps" :
               [
                  {
                     "entityId" : null,
                     "entity" : "assay",
                     "source" : "cap-doc",
                     "id" : 44921,
                     "display" : "Acton S et al. Identification of scavenger receptor SR-BI as a high density lipoprotein receptor. Science 1996 Jan 26; 271(5248) 518-20",
                     "contextRef" : "Publication",
                     "contextGroup" : null,
                     "key" : "doc",
                     "value" : "http://www.ncbi.nlm.nih.gov/pubmed/8560269",
                     "extValueId" : "http://www.ncbi.nlm.nih.gov/pubmed/8560269",
                     "url" : null,
                     "displayOrder" : 0,
                     "related" : null
                  }
               ]
         }
      ],
   "misc" :
      [
         {
            "entityId" : 46,
            "entity" : "assay",
            "source" : "GO",
            "id" : null,
            "display" : "cell death",
            "contextRef" : null,
            "contextGroup" : null,
            "key" : "goid",
            "value" : "GO:0008219",
            "extValueId" : "GO:0008219",
            "url" : "http://amigo.geneontology.org/cgi-bin/amigo/term_details?term=GO:0008219",
            "displayOrder" : -1,
            "related" : "target=,gotype=P,evcode=CAP_DIRECT_TERM,ev=,parentid="
         }
      ]
}"""

    void "test performance for real-life contexts and measures"() {
        when:
        final BardAnnotation bardAnnotation = objectMapper.readValue(REAL_LIFE_ANNOATION, BardAnnotation.class)
        bardAnnotation.populateContextMeasureRelationships()

        then:
        Measure AC50 = bardAnnotation.measures.get(2)
        Measure percentActivity = bardAnnotation.measures.get(3)

        assert AC50.relatedContexts.size() == 1
        assert percentActivity.relatedContexts.size() == 2
    }
}

