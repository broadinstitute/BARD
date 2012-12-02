package bard.core.rest.spring.util

import com.fasterxml.jackson.databind.ObjectMapper
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class MatchingFieldsUnitSpec extends Specification {
    @Shared
    ObjectMapper objectMapper = new ObjectMapper()
    public static final String MATCHING_FIELDS_NODE =
        '''
    {
       "matchingFields":
       {
           "781":
           {
               "description": "Cancer cells divide rapidly",
                "name": "qHTS screen for small molecules that"
            },
           "782":
           {
               "description": "Cancer cells",
               "name": "qHTS screen for small molecules that"
           }
       }
    }
    '''


    void setup() {

    }

    void tearDown() {
        // Tear down logic here
    }

    void "test serialize json to matchingFields"() {
        when:
        MatchingFields matchingFields = objectMapper.readValue(MATCHING_FIELDS_NODE, MatchingFields.class)
        then:
        assert matchingFields
        Map<String, NameDescription> map = matchingFields.getMatchingFieldsMap()
        assert  map
        assert map.size() == 2
        assert map.get("781")
        assert map.get("782")
    }

}

