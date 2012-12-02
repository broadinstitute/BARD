package bard.core.rest.spring.util

import com.fasterxml.jackson.databind.ObjectMapper
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class TargetUnitSpec extends Specification {
    @Shared
    ObjectMapper objectMapper = new ObjectMapper()
    public static final String TARGET_NODE = '''
   {
       "acc": "Q9Y253",
       "name": "DNA polymerase eta",
       "description": "describe",
       "status": "Reviewed",
       "geneId": 5429,
       "taxId": 9606,
       "resourcePath": "/targets/accession/Q9Y253"
    }
   '''


    void setup() {

    }

    void tearDown() {
        // Tear down logic here
    }

    void "test serialize json to Target"() {
        when:
        Target target = objectMapper.readValue(TARGET_NODE, Target.class)
        then:
        assert target
        assert target.acc=="Q9Y253"
        assert target.name=="DNA polymerase eta"
        assert target.description=="describe"
        assert target.status=="Reviewed"
        assert target.geneId==5429
        assert target.taxId==9606
        assert target.resourcePath=="/targets/accession/Q9Y253"
    }

}

