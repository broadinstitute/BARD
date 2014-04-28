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

package bard.core.rest.spring.experiment

import bard.core.rest.spring.DictionaryRestService
import com.fasterxml.jackson.databind.ObjectMapper
import org.codehaus.groovy.grails.commons.spring.GrailsWebApplicationContext
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

import javax.servlet.ServletContext
import org.codehaus.groovy.grails.web.context.ServletContextHolder

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
    ServletContext servletContext
    GrailsWebApplicationContext ctx
    DictionaryRestService dictionaryRestService
    void setup() {
        servletContext = Mock(ServletContext)
        ServletContextHolder.metaClass.static.getServletContext = {servletContext}
        ctx = Mock()
         dictionaryRestService =  Mock(DictionaryRestService)
    }

    void cleanup() {
        //Clean up the metaClass mocking we added.
        def remove = GroovySystem.metaClassRegistry.&removeMetaClass
        remove ServletContextHolder
    }
    void "test root element no child"() {
        when:
        final RootElement rootElement = objectMapper.readValue(ROOT_ELEMENT, RootElement.class)
        then:
        servletContext.getAttribute(_)>>{ctx}
        ctx.dictionaryRestService()>>{dictionaryRestService}

        assert rootElement
        assert rootElement.displayName== "Outcome"
        assert rootElement.dictElemId==899
        assert rootElement.value=="Active"

    }

    void "test root elements with child nodes"() {

        when:
        final RootElement rootElement = objectMapper.readValue(ROOT_ELEMENTS_WITH_CHILD_NODES, RootElement.class)
        then:
        servletContext.getAttribute(_)>>{ctx}
        ctx.dictionaryRestService()>>{dictionaryRestService}

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

