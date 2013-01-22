package bard.core.rest.spring.experiment

import com.fasterxml.jackson.databind.ObjectMapper
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll
import javax.servlet.ServletContext
import org.codehaus.groovy.grails.commons.spring.GrailsWebApplicationContext
import bard.core.rest.spring.DataExportRestService
import org.codehaus.groovy.grails.web.context.ServletContextHolder
import bard.rest.api.wrapper.Dummy

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


}

