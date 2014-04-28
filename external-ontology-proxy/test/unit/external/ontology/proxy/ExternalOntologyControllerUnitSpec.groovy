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

package external.ontology.proxy

import bard.validation.ext.ExternalItemImpl
import bard.validation.ext.ExternalOntologyAPI
import bard.validation.ext.ExternalOntologyException
import bard.validation.ext.ExternalOntologyFactory
import grails.test.mixin.TestFor
import org.apache.commons.logging.Log
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

import javax.servlet.http.HttpServletResponse

import static external.ontology.proxy.ExternalOntologyController.EXTERNAL_ONTOLOGY_PROPERTIES

/**
 * See the API for {@link grails.test.mixin.web.ControllerUnitTestMixin} for usage instructions
 */
@TestFor(ExternalOntologyController)
@Unroll
class ExternalOntologyControllerUnitSpec extends Specification {

    ExternalOntologyFactory externalOntologyFactory

    Log log

    @Shared ExternalOntologyAPI externalOntologyAPI

    void setup() {
        externalOntologyFactory = Mock(ExternalOntologyFactory)
        log = Mock(Log)

        controller.externalOntologyFactory = externalOntologyFactory
        controller.log = log
    }


    void "test findExternalItemById with bad params #desc"() {
        when:
        controller.findExternalItemById(externalUrl, id)

        then:
        def e = thrown(IllegalArgumentException)
        e.message == expectedResult

        where:
        desc                | externalUrl | id          | expectedResult
        "null  externalUrl" | null        | "not blank" | "externalUrl cannot be blank"
        "blank externalUrl" | ""          | "not blank" | "externalUrl cannot be blank"
        "blank externalUrl" | " "         | "not blank" | "externalUrl cannot be blank"
        "null  id"          | "not blank" | null        | "id cannot be blank"
        "blank id"          | "not blank" | ""          | "id cannot be blank"
        "blank id"          | "not blank" | " "         | "id cannot be blank"
    }

    void "test findExternalItemById externalOntologyAPI logic #desc"() {
        given: 'a mock externalOntology implementation'
        externalOntologyAPI = Mock(ExternalOntologyAPI)

        when: 'looking for an id'
        controller.findExternalItemById(externalUrl, id)

        then: 'we get an expected json response'
        1 * externalOntologyFactory.getExternalOntologyAPI(externalUrl, EXTERNAL_ONTOLOGY_PROPERTIES) >> { factoryMockReturn.call() }
        (_..1) * externalOntologyAPI.findById(id) >> { externalOntologyAPIMockReturn.call() }
        response.contentAsString == expectedResult

        where:
        desc                                   | externalUrl | id          | factoryMockReturn       | externalOntologyAPIMockReturn             | expectedResult
        "externalOntologyfactory returns null" | "not blank" | "not blank" | { null }                | { null }                                  | "{}"
        "externalOntologyAPI returns null"     | "not blank" | "not blank" | { externalOntologyAPI } | { null }                                  | "{}"
        "ExternalItem returned"                | "not blank" | "not blank" | { externalOntologyAPI } | { new ExternalItemImpl("id", "display") } | '{"id":"id","display":"display"}'
    }



    void "test findExternalItemById ExternalOntologyException "() {
        when:
        controller.findExternalItemById("foo", "bar")

        then:
        1 * externalOntologyFactory.getExternalOntologyAPI(_, _) >> { throw new ExternalOntologyException("problem") }
        response.status == HttpServletResponse.SC_INTERNAL_SERVER_ERROR
    }


    void "test findExternalItemsByTerm with bad params #desc"() {
        when:
        controller.findExternalItemsByTerm(externalUrl, term, 0)

        then:
        def e = thrown(IllegalArgumentException)
        e.message == expectedResult

        where:
        desc                | externalUrl | term        | expectedResult
        "null  externalUrl" | null        | "not blank" | "externalUrl cannot be blank"
        "blank externalUrl" | ""          | "not blank" | "externalUrl cannot be blank"
        "blank externalUrl" | " "         | "not blank" | "externalUrl cannot be blank"
        "null  term"        | "not blank" | null        | "term cannot be blank"
        "blank term"        | "not blank" | ""          | "term cannot be blank"
        "blank term"        | "not blank" | " "         | "term cannot be blank"
    }

    void "test findExternalItemsByTerm externalOntologyAPI logic #desc"() {
        given: 'a mock externalOntology implementation'
        externalOntologyAPI = Mock(ExternalOntologyAPI)
        final Integer limit = 10

        when: 'looking for an id'
        controller.findExternalItemsByTerm(externalUrl, term, limit)

        then: 'we get an expected json response'
        1 * externalOntologyFactory.getExternalOntologyAPI(externalUrl, EXTERNAL_ONTOLOGY_PROPERTIES) >> { factoryMockReturn.call() }
        (_..1) * externalOntologyAPI.findMatching(term, _) >> { externalOntologyAPIMockReturn.call() }
        response.contentAsString == expectedResult

        where:
        desc                                      | externalUrl | term        | factoryMockReturn       | externalOntologyAPIMockReturn                                          | expectedResult
        "factory returns null"                    | "not blank" | "not blank" | { null }                | { null }                                                               | '{"externalItems":[]}'
        "externalOntologyAPI returns empty list"  | "not blank" | "not blank" | { externalOntologyAPI } | { null }                                                               | '{"externalItems":[]}'
        "externalOntologyAPI returns empty list"  | "not blank" | "not blank" | { externalOntologyAPI } | { [] }                                                                 | '{"externalItems":[]}'
        "ExternalItem returned"                   | "not blank" | "not blank" | { externalOntologyAPI } | { [new ExternalItemImpl("1", "display")] }                             | '{"externalItems":[{"id":"1","display":"display"}]}'
        "ExternalItems returned sorted"           | "not blank" | "not blank" | { externalOntologyAPI } | { [new ExternalItemImpl("1", "B"), new ExternalItemImpl("2", "a")] }   | '{"externalItems":[{"id":"2","display":"a"},{"id":"1","display":"B"}]}'
        "corner case, sort ok with display props" | "not blank" | "not blank" | { externalOntologyAPI } | { [new ExternalItemImpl("1", null), new ExternalItemImpl("2", null)] } | '{"externalItems":[{"id":"1","display":""},{"id":"2","display":""}]}'
    }

    void "test expectedApiLimitParam is #expectedApiLimitParam when controllerLimitParam is #controllerLimitParam "() {
        given: 'a mock externalOntology implementation'
        externalOntologyAPI = Mock(ExternalOntologyAPI)
        final String url = 'url'
        final String term = 'term'

        when: 'the api is called'
        controller.findExternalItemsByTerm(url, term, controllerLimitParam)

        then: 'the limit is constrained to a number between 1 and 500'
        1 * externalOntologyFactory.getExternalOntologyAPI('url', EXTERNAL_ONTOLOGY_PROPERTIES) >> externalOntologyAPI
        1 * externalOntologyAPI.findMatching('term', expectedApiLimitParam)
        0 * externalOntologyAPI._


        where:
        controllerLimitParam                                                     | expectedApiLimitParam
        -1                                                                      | ExternalOntologyController.MIN_EXTERNAL_ONTOLOGY_MATCHING_PAGE_SIZE
        0                                                                       | ExternalOntologyController.MIN_EXTERNAL_ONTOLOGY_MATCHING_PAGE_SIZE
        1                                                                       | 1
        10                                                                      | 10
        100                                                                     | 100
        500                                                                     | 500
        ExternalOntologyController.MAX_EXTERNAL_ONTOLOGY_MATCHING_PAGE_SIZE + 1 | ExternalOntologyController.MAX_EXTERNAL_ONTOLOGY_MATCHING_PAGE_SIZE
    }

    void "test findExternalItemsByTerm ExternalOntologyException "() {
        when:
        controller.findExternalItemsByTerm("foo", "bar", 10)

        then:
        1 * externalOntologyFactory.getExternalOntologyAPI(_, _) >> { throw new ExternalOntologyException("problem") }
        response.status == HttpServletResponse.SC_INTERNAL_SERVER_ERROR
    }

    void "test externalOntologyHasIntegratedSearch #desc"() {
        when:
        controller.externalOntologyHasIntegratedSearch(externalUrl)

        then:
        (0..1) * externalOntologyFactory.getExternalOntologyAPI(externalUrl, EXTERNAL_ONTOLOGY_PROPERTIES) >> { mockReturn.call() }
        response.contentAsString == expectedResult

        where:
        desc                          | externalUrl | mockReturn                     | expectedResult
        "null"                        | null        | { null }                       | '{"hasSupport":false}'
        "blank"                       | ""          | { null }                       | '{"hasSupport":false}'
        "blank"                       | " "         | { null }                       | '{"hasSupport":false}'
        "non blank url no impl"       | "foo"       | { null }                       | '{"hasSupport":false}'
        "non blank url non null impl" | "foo"       | { [:] as ExternalOntologyAPI } | '{"hasSupport":true}'
    }

    void "test externalOntologyHasIntegratedSearch with exception #desc"() {
        when:
        controller.externalOntologyHasIntegratedSearch(externalUrl)

        then:
        (0..1) * externalOntologyFactory.getExternalOntologyAPI(externalUrl, EXTERNAL_ONTOLOGY_PROPERTIES) >> { mockReturn.call() }
        response.status == expectedResult

        where:
        desc                             | externalUrl | mockReturn                                     | expectedResult
        "non blank url exception thrown" | "foo"       | { throw new ExternalOntologyException("bar") } | HttpServletResponse.SC_INTERNAL_SERVER_ERROR
    }
}
