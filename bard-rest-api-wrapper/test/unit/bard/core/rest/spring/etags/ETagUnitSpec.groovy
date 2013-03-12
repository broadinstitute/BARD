package bard.core.rest.spring.etags

import com.fasterxml.jackson.databind.ObjectMapper
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class ETagUnitSpec extends Specification {

    @Shared
    ObjectMapper objectMapper = new ObjectMapper()

    public static final String ETAG = '''
    [
        {
            "etag": "2b63a2bd2eb8280f",
            "type": "assays"
        },
        {
            "etag": "60ec019a66228e35",
            "type": "projects"
        },
        {
            "etag": "aa41d4c605122873",
            "type": "compounds"
        }
    ]
    '''

    void assertETag(final String expectedType, final String expectedEtag,
                    final String foundType, final String foundETag) {
        assert expectedType == foundType
        assert expectedEtag == foundETag

    }

    void "newCompositeETag #label"() {
        when:
        final ETags etags = objectMapper.readValue(ETAG, ETags.class)
        then:
        assert etags
        final List<ETag> foundETags = etags.getByType(type)
        assert foundETags
        assert 1 == foundETags.size()
        assert 3 == etags.buildMap().size()
        assertETag(type, etag, foundETags.get(0).getType(), foundETags.get(0).getEtag())
        where:
        label           | type        | etag
        "Assay Type"    | "assays"    | "2b63a2bd2eb8280f"
        "Project Type"  | "projects"  | "60ec019a66228e35"
        "Compound Type" | "compounds" | "aa41d4c605122873"

    }

}


