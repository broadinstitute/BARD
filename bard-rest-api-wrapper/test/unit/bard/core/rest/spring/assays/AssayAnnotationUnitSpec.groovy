package bard.core.rest.spring.assays

import com.fasterxml.jackson.databind.ObjectMapper
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class AssayAnnotationUnitSpec extends Specification {
    @Shared
    ObjectMapper objectMapper = new ObjectMapper()

    public static final String ASSAY_ANNOTATION = '''
    {
       "source": "cap",
       "id": "31690",
       "display": "http://www.uniprot.org/uniprot/Q9Y253",
       "contextRef": "context",
       "key": "UniProt",
       "value": "value",
       "extValueId": "Q9Y253"
    }
    '''

    void "test serialization to AssayAnnotation"() {
        when:
        final AssayAnnotation assayAnnotation = objectMapper.readValue(ASSAY_ANNOTATION, AssayAnnotation.class)
        then:
        assert assayAnnotation.contextRef=="context"
        assert  assayAnnotation.display=="http://www.uniprot.org/uniprot/Q9Y253"
        assert  assayAnnotation.extValueId=="Q9Y253"
        assert assayAnnotation.id =="31690"
        assert assayAnnotation.key=="UniProt"
        assert assayAnnotation.source =="cap"
        assert assayAnnotation.value=="value"
    }


}

