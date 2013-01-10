package bard.core.rest.spring.experiment

import com.fasterxml.jackson.databind.ObjectMapper
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class RootElementUnitSpec extends Specification {
    @Shared
    ObjectMapper objectMapper = new ObjectMapper()
    String ROOT_ELEMENTS_WITH_CHILD_NODES = '''
  {
     "displayName":"AvgGluFoldShift",
     "dictElemId":1387,
     "value":"26.7",
     "childElements":[
        {
           "displayName":"GluFoldShiftExperiment1",
           "dictElemId":1020,
           "value":"20.6"
        },
        {
           "displayName":"GluFoldShiftExperiment2",
           "dictElemId":1020,
           "value":"38.8"
        },
        {
           "displayName":"GluFoldShiftExperiment3",
           "dictElemId":1387,
           "value":"20.8"
        },
        {
           "displayName":"StddevGluFoldShift",
           "dictElemId":613,
           "value":"10.4"
        },
        {
           "displayName":"SEMGluFoldShift",
           "dictElemId":1335,
           "value":"6"
        }
     ]
  }
     '''
    String ROOT_ELEMENT = '''
    {
        "displayName":"Outcome","dictElemId":899,"value":"Active"
    }
  '''

    void "test root element no child"() {
        when:
        final RootElement rootElement = objectMapper.readValue(ROOT_ELEMENT, RootElement.class)
        then:
        assert rootElement
        assert rootElement.displayName== "Outcome"
        assert rootElement.dictElemId==899
        assert rootElement.value=="Active"

    }

    void "test root elements with child nodes"() {

        when:
        final RootElement rootElement = objectMapper.readValue(ROOT_ELEMENTS_WITH_CHILD_NODES, RootElement.class)
        then:
        assert rootElement
        assert rootElement.displayName=="AvgGluFoldShift"
        assert rootElement.dictElemId==1387
        assert rootElement.value=="26.7"

        final List<ActivityData> childElements = rootElement.childElements
        assert childElements
        assert childElements.size() == 5
        for(ActivityData activityData in childElements){
            assert activityData.displayName
            assert activityData.dictElemId
            assert activityData.value
        }

    }
}

