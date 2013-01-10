package bard.core.rest.spring.experiment

import com.fasterxml.jackson.databind.ObjectMapper
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class ActivityDataUnitSpec extends Specification {
    @Shared
    ObjectMapper objectMapper = new ObjectMapper()

    @Shared
    String JSON_WITH_QUALIFIER = '''
    {
        "displayName":"IC50",
        "dictElemId":963,
        "responseUnit":"um",
        "testConcUnit":"uM",
        "value":"1.949",
        "qualifierValue":"="
    }
    '''
    @Shared
    String JSON_NO_QUALIFIER = '''
   {
        "displayName":"IC50",
        "dictElemId":963,
        "responseUnit":"um",
        "testConcUnit":"uM",
        "value":"1.949"

   }
   '''


    void "test JSON #label"() {
        when:
        ActivityData activityData = objectMapper.readValue(JSON_DATA, ActivityData.class)
        then:
        assert activityData
        assert activityData.displayName=="IC50"
        assert activityData.dictElemId==963
        assert activityData.value=="1.949"
        assert activityData.responseUnit=="um"
        assert activityData.testConcentrationUnit=="uM"

        if (hasQualifier) {
            assert activityData.qualifier == "="
        } else {
            assert !activityData.qualifier
        }
        where:
        label                   | JSON_DATA           | hasQualifier
        "JSON Has qualifier"    | JSON_WITH_QUALIFIER | true
        "JSON Has no qualifier" | JSON_NO_QUALIFIER   | false
    }


}

