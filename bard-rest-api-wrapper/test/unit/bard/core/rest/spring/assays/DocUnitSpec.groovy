package bard.core.rest.spring.assays

import com.fasterxml.jackson.databind.ObjectMapper
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class DocUnitSpec extends Specification {
    @Shared
    ObjectMapper objectMapper = new ObjectMapper()

    public static final String DOC = '''
    {
        "id": 18225,
        "name": "External URL",
        "comps":
        [
            {
                "entityId": null,
                "entity": "assay",
                "source": "cap-doc",
                "id": 18225,
                "display": "External Database Link",
                "contextRef": "External URL",
                "key": "doc",
                "value": "http://www.southernresearch.org",
                "extValueId": "http://www.southernresearch.org",
                "url": null,
                "displayOrder": 0,
                "related": null
            }
        ]
    }
    '''

    void "test serialization to Doc"() {
        when:
        final Doc doc = objectMapper.readValue(DOC, Doc.class)
        then:
        assert doc.id==18225
        assert doc.name== "External URL"
        List<Annotation> comps = doc.comps
        assert comps
        assert comps.size() == 1
        Annotation comp = comps.get(0)
        assert comp.display=="External Database Link"
        assert comp.entity=="assay"
        assert !comp.entityId
        assert comp.source== "cap-doc"
        assert comp.id==18225
        assert comp.contextRef=="External URL"
        assert comp.key== "doc"
        assert comp.value=="http://www.southernresearch.org"
        assert comp.extValueId=="http://www.southernresearch.org"
        assert !comp.url
        assert comp.displayOrder==0
        assert !comp.related
    }


}

