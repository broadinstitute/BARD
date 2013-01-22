package bard.core.rest.spring.experiment

import bard.core.rest.spring.DataExportRestService
import bard.core.rest.spring.util.DictionaryElement
import bard.rest.api.wrapper.Dummy
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
    DataExportRestService dataExportRestService = Mock(DataExportRestService)


    void "test JSON #label"() {
        given:
        Dummy dummy = new Dummy()
        dummy.dataExportRestService = dataExportRestService
        when:
        ActivityData activityData = objectMapper.readValue(JSON_DATA, ActivityData.class)
        activityData.dummy = dummy
        then:

        assert activityData
        assert activityData.pubChemDisplayName == "IC50"
        assert activityData.dictElemId == 963
        assert activityData.value == "1.949"
        assert activityData.responseUnit == "um"
        assert activityData.testConcentrationUnit == "uM"
        assert activityData.getDictionaryLabel() == "IC50"
        assert activityData.getDictionaryDescription() == "um"
        assert activityData.toDisplay() == display
        assert activityData.qualifier == qualifier

        where:
        label                   | JSON_DATA           | hasQualifier | display             | qualifier
        "JSON Has qualifier"    | JSON_WITH_QUALIFIER | true         | "IC50 : =1.949 um " | "="
        "JSON Has no qualifier" | JSON_NO_QUALIFIER   | false        | "IC50 : 1.949 um "  | null
    }

    void "test toDisplay #label"() {
        given:
        ActivityData activityData = new ActivityData(testConcentration: testConcentration, responseUnit: responseUnit, qualifier: qualifier, testConcentrationUnit: "um")
        activityData.metaClass.getDictionaryLabel = {dictionaryLabel}
        Dummy dummy = new Dummy()
        dummy.dataExportRestService = dataExportRestService
        activityData.dummy = dummy
        when:
        final String display = activityData.toDisplay()

        then:
        assert expectedDisplay.trim() == display.trim()
        where:
        label                         | qualifier | responseUnit | testConcentration | dictionaryLabel          | expectedDisplay
        "IC50"                        | ">"       | "um"         | null              | "IC50"                   | "IC50 : > um"
        "Not in dictionary"           | ""        | ""           | null              | null                     | " "
        "IC50 with testConcentration" | ""        | ""           | 223               | "IC50"                   | "IC50 :     Test Concentration:223.0 um"
        "Outcome"                     | ""        | ""           | 221               | "Outcome"                | ""
        "PubChem activity score"      | ""        | ""           | 1                 | "PubChem activity score" | ""
        "Activity_Score"              | ""        | ""           | 2                 | "Activity_Score"         | ""
        "Score"                       | ""        | ""           | 3                 | "Score"                  | ""

    }


    void "test getDictionaryDescription #label"() {
        given:
        ActivityData activityData = new ActivityData(dictElemId: dictElemId, responseUnit: "pubChem")
        Dummy dummy = new Dummy()
        dummy.dataExportRestService = dataExportRestService
        activityData.dummy = dummy
        when:
        final String foundDescription = activityData.getDictionaryDescription()

        then:
        expectedNumExecutions * dummy.dataExportRestService.findDictionaryElementById(_) >> {dictionaryElement}
        assert expectedDescription == foundDescription
        where:
        label                                     | dictElemId | dictionaryElement                           | expectedDescription | expectedNumExecutions
        "Has a DictElemId and in dictionary"      | 222        | new DictionaryElement(description: "label") | "label"             | 1
        "Has a DictElemId, but not in dictionary" | 221        | null                                        | "pubChem"           | 1
        "Has no DictElemId"                       | 0          | null                                        | "pubChem"           | 0

    }

    void "test getDictionaryLabel #label"() {
        given:
        ActivityData activityData = new ActivityData(dictElemId: dictElemId, pubChemDisplayName: "pubChem")
        Dummy dummy = new Dummy()
        dummy.dataExportRestService = dataExportRestService
        activityData.dummy = dummy
        when:
        final String foundLabel = activityData.getDictionaryLabel()

        then:
        expectedNumExecutions * dummy.dataExportRestService.findDictionaryElementById(_) >> {dictionaryElement}
        assert expectedLabel == foundLabel
        where:
        label                                     | dictElemId | dictionaryElement                     | expectedLabel | expectedNumExecutions
        "Has a DictElemId and in dictionary"      | 222        | new DictionaryElement(label: "label") | "label"       | 1
        "Has a DictElemId, but not in dictionary" | 221        | null                                  | "pubChem"     | 1
        "Has no DictElemId"                       | 0          | null                                  | "pubChem"     | 0

    }


}

