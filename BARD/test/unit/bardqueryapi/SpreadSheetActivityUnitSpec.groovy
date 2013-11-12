package bardqueryapi

import bard.core.rest.spring.experiment.Activity
import bard.core.rest.spring.experiment.PriorityElement
import molspreadsheet.SpreadSheetActivity
import spock.lang.Specification
import spock.lang.Unroll
import bard.core.rest.spring.experiment.ConcentrationResponseSeries
import molspreadsheet.MolSpreadSheetColSubHeader
import javax.servlet.ServletContext
import org.codehaus.groovy.grails.commons.spring.GrailsWebApplicationContext
import bard.core.rest.spring.DictionaryRestService
import org.codehaus.groovy.grails.web.context.ServletContextHolder


/**
 * See the API for {@link grails.test.mixin.support.GrailsUnitTestMixin} for usage instructions
 */
@Unroll
class SpreadSheetActivityUnitSpec extends Specification {

    ServletContext servletContext
    GrailsWebApplicationContext ctx
    DictionaryRestService dictionaryRestService
    void setup() {
        servletContext = Mock(ServletContext)
        ServletContextHolder.metaClass.static.getServletContext = {servletContext}
        ctx = Mock()
        dictionaryRestService =  Mock(DictionaryRestService)
        servletContext.getAttribute(_)>>{ctx}
        ctx.dictionaryRestService()>>{dictionaryRestService}
    }

    void cleanup() {
        //Clean up the metaClass mocking we added.
        def remove = GroovySystem.metaClassRegistry.&removeMetaClass
        remove ServletContextHolder
    }
    void "test readOutsToHillCurveValues"() {
        given:
        SpreadSheetActivity spreadSheetActivity = new SpreadSheetActivity()
        when:
        spreadSheetActivity.readOutsToHillCurveValues(null, [])
        then:
        assert !spreadSheetActivity.hillCurveValueList
    }

    void "test extractExperimentalValuesFromAPriorityElement with empty priorityElement"() {
        given:
        SpreadSheetActivity spreadSheetActivity = new SpreadSheetActivity()
        PriorityElement priorityElement = new  PriorityElement ()
        List <MolSpreadSheetColSubHeader> resultTypeNames = []
        when:
        spreadSheetActivity.extractExperimentalValuesFromAPriorityElement(resultTypeNames, priorityElement)
        then:
        resultTypeNames.size() == 1
        resultTypeNames[0].columnTitle ==  ""
    }
    void "test extractExperimentalValuesFromAPriorityElement"() {
        given:
        SpreadSheetActivity spreadSheetActivity = new SpreadSheetActivity()
        PriorityElement priorityElement = new  PriorityElement (displayName: "columnName")
        List <MolSpreadSheetColSubHeader> resultTypeNames = []
        when:
        spreadSheetActivity.extractExperimentalValuesFromAPriorityElement(resultTypeNames, priorityElement)
        then:
        resultTypeNames.size() == 1
        resultTypeNames[0].columnTitle ==  "columnName"
    }
    void "test extractExperimentalValuesFromAPriorityElement backup name"() {
        given:
        SpreadSheetActivity spreadSheetActivity = new SpreadSheetActivity()
        ConcentrationResponseSeries concentrationResponseSeries = new ConcentrationResponseSeries(responseUnit: "uM")
        PriorityElement priorityElement = new  PriorityElement (displayName: "columnName")
        priorityElement.concentrationResponseSeries = concentrationResponseSeries
        List <MolSpreadSheetColSubHeader> resultTypeNames = []
        when:
        spreadSheetActivity.extractExperimentalValuesFromAPriorityElement(resultTypeNames, priorityElement)
        then:
        resultTypeNames.size() == 1
        resultTypeNames[0].columnTitle ==  "columnName"
    }
    void "test extractExperimentalValuesFromAPriorityElement with repeated column name"() {
        given:
        SpreadSheetActivity spreadSheetActivity = new SpreadSheetActivity()
        PriorityElement priorityElement = new  PriorityElement (displayName: 'columnName')
        List <MolSpreadSheetColSubHeader> resultTypeNames  = [new MolSpreadSheetColSubHeader(columnTitle:'columnName') ]
        when:
        spreadSheetActivity.extractExperimentalValuesFromAPriorityElement(resultTypeNames, priorityElement)
        then:
        resultTypeNames.size() == 1
        resultTypeNames[0].columnTitle == 'columnName'
    }
    void "test extractExperimentalValuesFromAPriorityElement with non-repeated column name"() {
        given:
        SpreadSheetActivity spreadSheetActivity = new SpreadSheetActivity()
        PriorityElement priorityElement = new  PriorityElement (displayName: "columnName1")
        List <MolSpreadSheetColSubHeader> resultTypeNames  = [new MolSpreadSheetColSubHeader(columnTitle:'columnName2')]
        when:
        spreadSheetActivity.extractExperimentalValuesFromAPriorityElement(resultTypeNames, priorityElement)
        then:
        resultTypeNames.size() == 2
        resultTypeNames[0].columnTitle == "columnName2"
        resultTypeNames[1].columnTitle == "columnName1"
    }

    void "test addPotency #label"() {
        given:
        SpreadSheetActivity spreadSheetActivity = new SpreadSheetActivity()
        when:
        spreadSheetActivity.addPotency(activity)
        then:
        assert (spreadSheetActivity.potency != Double.NaN) == expected
        where:
        label             | activity                   | expected
        "With potency"    | new Activity(potency: "2") | true
        "Without potency" | new Activity()             | false
    }

    void "test addOutCome #label"() {
        given:
        SpreadSheetActivity spreadSheetActivity = new SpreadSheetActivity()
        when:
        spreadSheetActivity.addOutCome(activity)
        then:
        assert spreadSheetActivity.activityOutcome == expected
        where:
        label             | activity                 | expected
        "With outcome"    | new Activity(outcome: 2) | ActivityOutcome.ACTIVE
        "Without outcome" | new Activity()           | ActivityOutcome.UNSPECIFIED
    }
}
