package bard.core.rest.spring.assays

import com.fasterxml.jackson.databind.ObjectMapper
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class ContextUnitSpec extends Specification {
    @Shared
    ObjectMapper objectMapper = new ObjectMapper()

    public static final String CONTEXT = '''
    {
        "id": 7186,
        "name": "Context for percent activity",
        "comps":
        [
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
        ]
    }
    '''

    void "test serialization to Context"() {
        when:
        final Context context = objectMapper.readValue(CONTEXT, Context.class)

        then:
        assert context.id == 7186
        assert context.name == "Context for percent activity"
        List<Comp> comps = context.comps
        assert comps
        assert comps.size() == 1
        Comp comp = comps.get(0)
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
}
