package bard.core.rest.spring.experiment

import com.fasterxml.jackson.databind.ObjectMapper
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll
import javax.servlet.ServletContext
import org.codehaus.groovy.grails.commons.spring.GrailsWebApplicationContext
import bard.core.rest.spring.DataExportRestService
import org.codehaus.groovy.grails.web.context.ServletContextHolder

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
    ServletContext servletContext
    GrailsWebApplicationContext ctx
    DataExportRestService dataExportRestService
    void setup() {
        servletContext = Mock(ServletContext)
        ServletContextHolder.metaClass.static.getServletContext = {servletContext}
        ctx = Mock()
        dataExportRestService =  Mock(DataExportRestService)
    }

    void cleanup() {
        //Clean up the metaClass mocking we added.
        def remove = GroovySystem.metaClassRegistry.&removeMetaClass
        remove ServletContextHolder
    }

    void "test JSON #label"() {
        when:
        ActivityData activityData = objectMapper.readValue(JSON_DATA, ActivityData.class)
        then:
        servletContext.getAttribute(_)>>{ctx}
        ctx.dataExportRestService()>>{dataExportRestService}

        assert activityData
        assert activityData.pubChemDisplayName=="IC50"
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

