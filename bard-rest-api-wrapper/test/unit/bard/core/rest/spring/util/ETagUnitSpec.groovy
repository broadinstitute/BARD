package bard.core.rest.spring.util

import com.fasterxml.jackson.databind.ObjectMapper
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class ETagUnitSpec extends Specification {
    @Shared
    ObjectMapper objectMapper = new ObjectMapper()
    final String ETAG_JSON = '''
    {
       "accessed": 1348295120000,
       "etag_id": "de6add5339d1af20",
       "count": 7109,
       "status": 1,
       "created": 1348240757000,
       "description": "The NCGC Pharmaceutical Collection",
       "name": "NPC database",
       "type": "gov.nih.ncgc.bard.entity.Compound",
       "url": "http://tripod.nih.gov/npc",
       "modified": 1348240757000
    }
   '''

    void "test serialize json to ETag"() {
        when:
        final ETag etag = objectMapper.readValue(ETAG_JSON, ETag.class)
        then:
        assert etag
        assert etag.accessed == 1348295120000
        assert etag.etag_id == "de6add5339d1af20"
        assert etag.count == 7109
        assert etag.status == 1
        assert etag.created == 1348240757000
        assert etag.description == "The NCGC Pharmaceutical Collection"
        etag.name == "NPC database"
        etag.type == "gov.nih.ncgc.bard.entity.Compound"
        etag.url == "http://tripod.nih.gov/npc"
        etag.modified == 1348240757000
    }

}

