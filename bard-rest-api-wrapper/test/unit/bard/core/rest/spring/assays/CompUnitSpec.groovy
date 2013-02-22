package bard.core.rest.spring.assays

import com.fasterxml.jackson.databind.ObjectMapper
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class CompUnitSpec extends Specification {
    @Shared
    ObjectMapper objectMapper = new ObjectMapper()

    public static final String COMP = '''
   {
        "entityId": null,
        "entity": "assay",
        "source": "cap-context",
        "id": 7186,
        "display": ".05 um",
        "contextRef": "Context for percent activity",
        "key": "screening concentration",
        "value": null,
        "extValueId": null,
        "url": null,
        "displayOrder": 0,
        "related": "measureRefs:22510"
    }
       '''

    @Shared Comp comp1 = new Comp(display: 'comp1 name', key: 'key1', value: 'value1')
    @Shared Comp comp2 = new Comp(display: 'comp2 name', key: 'key2', value: 'value2')
    @Shared Comp comp3 = new Comp(display: 'comp3 name', key: 'key3', value: 'value3')


    void "test serialization to Comp"() {
        when:
        final Comp comp = objectMapper.readValue(COMP, Comp.class)
        then:
        assert comp.display == ".05 um"
        assert comp.entity == "assay"
        assert !comp.entityId
        assert comp.source == "cap-context"
        assert comp.id == 7186
        assert comp.contextRef == "Context for percent activity"
        assert comp.key == "screening concentration"
        assert !comp.value
        assert !comp.extValueId
        assert !comp.url
        assert comp.displayOrder == 0
        assert comp.related == "measureRefs:22510"
    }

    void "test splitForColumnLayout: #label"() {
        when:
        List<List<Comp>> result = Comp.splitForColumnLayout(comps)

        then:
        assert result == expectedResult

        where:
        label                 | comps                 | expectedResult
        'one context-item'    | [comp1]               | [[comp1]]
        'two context-items'   | [comp1, comp2]        | [[comp1], [comp2]]
        'three context-items' | [comp1, comp2, comp3] | [[comp1, comp2], [comp3]]
        'zero context-items'  | []                    | []
    }
}

