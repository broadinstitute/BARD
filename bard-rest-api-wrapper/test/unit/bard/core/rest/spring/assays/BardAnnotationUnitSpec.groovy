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
            "related":null
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
               "display":"Hill coefficient",
               "contextRef":null,
               "key":null,
               "value":"Hill coefficient",
               "extValueId":null,
               "url":null,
               "displayOrder":0,
               "related":""
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
        then:
        assert bardAnnotation.measures
        assert bardAnnotation.measures.size() == 1
        assert bardAnnotation.docs
        assert bardAnnotation.docs.size() == 1
        assert bardAnnotation.contexts
        assert bardAnnotation.contexts.size() == 3


    }

    void "test areAnnotationsEmpty() #label"() {
        when:
        Boolean result = BardAnnotation.areAnnotationsEmpty(annotations)

        then:
        assert result == expectedResult

        where:
        label                      | annotations                                                        | expectedResult
        'no annotations at all'    | []                                                                 | false
        'a single empty annotatin' | [new BardAnnotation()]                                             | false
        'a single empty Context'   | [new BardAnnotation(contexts: [new Context()])]                    | false
        'a non-empty annotations'  | [new BardAnnotation(contexts: [new Context(contextItems: [new Annotation()])])] | true
    }

    void "test areOtherAnnotationsEmpty() #label"() {
        when:
        Boolean result = BardAnnotation.areOtherAnnotationsEmpty(annotations)

        then:
        assert result == expectedResult

        where:
        label                        | annotations                                          | expectedResult
        'no otherAnnotations at all' | []                                                   | false
        'a single empty annotatin'   | [new BardAnnotation()]                               | false
        'a non-empty annotations'    | [new BardAnnotation(otherAnnotations: [new Annotation()])] | true
    }

    void "test findContextsContainingKey #label"() {
        when:
        final BardAnnotation bardAnnotation = objectMapper.readValue(BARD_ANNOTATION, BardAnnotation.class)
        List<Context> result = bardAnnotation.findContextsContainingKey(key, entity)

        then:
        assert result.size() == expectedContextCount

        where:
        label | key | entity | expectedContextCount
        'find footprint' | 'footprint' | 'assay' | 1
        'find species'   | 'species'   | 'assay' | 1
        'find "a" for assays' | 'a' | 'assay' | 2
        'find result type for experiments' | 'result type' | 'experiment' | 1
        'nothing found' | 'find nothing' | 'assay' | 0
    }
}

