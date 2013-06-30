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
}

