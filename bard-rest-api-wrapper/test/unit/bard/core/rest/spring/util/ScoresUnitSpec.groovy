package bard.core.rest.spring.util

import com.fasterxml.jackson.databind.ObjectMapper
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class ScoresUnitSpec extends Specification {
    @Shared
    ObjectMapper objectMapper = new ObjectMapper()
    public static final String SCORES_NODE = '''
    {
       "a": 2.1923265,
       "b": 2.1923265,
       "b": 2.1923265,
       "d": 2.1923265,
       "e": 2.1923265,
       "f": 1.9182856,
       "g": 1.9182856,
       "h": 1.9182856
   }
     '''

    void "test serialize json to scores"() {
        when:
        Scores scores = objectMapper.readValue(SCORES_NODE, Scores.class)
        then:
        assert scores
        final Map<String, Double> scoreMap = scores.getScoreMap()
        assert scoreMap.size() == 7
        assert scores.getScore("a") == 2.1923265
    }

}

