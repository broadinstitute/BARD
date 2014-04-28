/* Copyright (c) 2014, The Broad Institute
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
    * Redistributions of source code must retain the above copyright
      notice, this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright
      notice, this list of conditions and the following disclaimer in the
      documentation and/or other materials provided with the distribution.
    * Neither the name of The Broad Institute nor the
      names of its contributors may be used to endorse or promote products
      derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL The Broad Institute BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

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
