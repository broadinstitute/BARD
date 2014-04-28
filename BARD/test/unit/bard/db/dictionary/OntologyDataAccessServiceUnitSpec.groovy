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

package bard.db.dictionary

import bard.validation.ext.ExternalItem
import bard.validation.ext.ExternalOntologyAPI
import bard.validation.ext.ExternalOntologyException
import bard.validation.ext.ExternalOntologyFactory
import bard.validation.extext.ExternalItemImpl
import grails.test.mixin.TestFor
import org.apache.commons.logging.Log
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

/**
 * Created with IntelliJ IDEA.
 * User: ddurkin
 * Date: 3/11/13
 * Time: 4:58 PM
 * To change this template use File | Settings | File Templates.
 */
@Unroll
@TestFor(OntologyDataAccessService)
class OntologyDataAccessServiceUnitSpec extends Specification {

    ExternalOntologyFactory externalOntologyFactory
    @Shared ExternalOntologyAPI externalOntologyAPI
    Log log

    void setup() {
        externalOntologyFactory = Mock(ExternalOntologyFactory)
        externalOntologyAPI = Mock(ExternalOntologyAPI)
        log = Mock(Log)
        service.externalOntologyFactory = externalOntologyFactory
        service.log = log

    }

    void "test findExternalItemById for #expectedException when #desc"(String desc, String externalUrl, String id, String expectedMessage) {
        when:
        service.findExternalItemById(externalUrl, id)

        then:
        IllegalArgumentException e = thrown(IllegalArgumentException)
        e.message == expectedMessage

        where:
        desc                | externalUrl      | id   | expectedMessage
        "null exteranlUrl"  | null             | null | 'externalUrl cannot be blank'
        "blank exteranlUrl" | ''               | null | 'externalUrl cannot be blank'
        "blank exteranlUrl" | ' '              | null | 'externalUrl cannot be blank'
        "null blank term"   | 'http://foo.com' | ''   | 'id cannot be blank'
        "null term"         | 'http://foo.com' | null | 'id cannot be blank'
        "null blank term"   | 'http://foo.com' | ' '  | 'id cannot be blank'
    }

    void "test findExternalItemById no exerternalOntologyApi implementation"() {
        given:
        final String externalUrl = 'http://foo.com'
        final String id = '1'

        when:
        ExternalItem externalItem = service.findExternalItemById(externalUrl, id)

        then:
        1 * externalOntologyFactory.getExternalOntologyAPI(externalUrl) >> null
        externalItem == null
    }

    void "test findExternalItemById with exerternalOntologyApi implementation returning item"() {
        given:
        final String externalUrl = 'http://foo.com'
        final String id = '1'
        final ExternalItem returnedExternalItem = new ExternalItemImpl(id:id, display:'someDisplay')

        when:
        ExternalItem externalItem = service.findExternalItemById(externalUrl, id)

        then:
        1 * externalOntologyFactory.getExternalOntologyAPI(externalUrl) >> externalOntologyAPI
        1 * externalOntologyAPI.findById(id) >> returnedExternalItem
        externalItem == returnedExternalItem
    }

    void "test findExternalItemById with externalOntologyFactory throwing an exception"() {
        given: 'valid params'
        final String externalUrl = 'http://foo.com'
        final String id = '1'

        when: 'an exception is thrown'
        ExternalItem externalItem = service.findExternalItemById(externalUrl, id)

        then: 'logging should happen at the warn level and an exception should be thrown'
        1 * externalOntologyFactory.getExternalOntologyAPI(externalUrl) >> { throw new ExternalOntologyException("some exception") }
        1 * log.warn(_)
        thrown(ExternalOntologyException)
    }

    void "test findExternalItemsByTerm for #expectedException when #desc"(String desc, String externalUrl, String term, String expectedMessage) {

        when:
        service.findExternalItemsByTerm(externalUrl, null)

        then:
        IllegalArgumentException e = thrown(IllegalArgumentException)
        e.message == expectedMessage

        where:
        desc                | externalUrl      | term | expectedMessage
        "null exteranlUrl"  | null             | null | 'externalUrl cannot be blank'
        "blank exteranlUrl" | ''               | null | 'externalUrl cannot be blank'
        "blank exteranlUrl" | ' '              | null | 'externalUrl cannot be blank'
        "null blank term"   | 'http://foo.com' | ''   | 'term cannot be blank'
        "null term"         | 'http://foo.com' | null | 'term cannot be blank'
        "null blank term"   | 'http://foo.com' | ' '  | 'term cannot be blank'
    }

    void "test findExternalItemsByTerm no exerternalOntologyApi implementation"() {
        given:
        final String externalUrl = 'http://foo.com'
        final String term = '1'

        when:
        List<ExternalItem> externalItems = service.findExternalItemsByTerm(externalUrl, term)

        then:
        1 * externalOntologyFactory.getExternalOntologyAPI(externalUrl) >> { null }
        externalItems.isEmpty()
    }

    void "test findExternalItemsByTerm with exerternalOntologyApi implementation #desc"() {
        given:
        final String externalUrl = 'http://foo.com'
        final String term = 'term'
        final List<ExternalItem> returnedExternalItems = []
        for (itemData in externalItemsData) {
            returnedExternalItems.add(new ExternalItemImpl(id:itemData[0], display:itemData[1]))
        }

        when:
        List<ExternalItem> externalItems = service.findExternalItemsByTerm(externalUrl, term)
        final List<ExternalItem> actualItemsData = externalItems.collect { ExternalItem item -> [item.id, item.display] }

        then:
        1 * externalOntologyFactory.getExternalOntologyAPI(externalUrl) >> externalOntologyAPI
        1 * externalOntologyAPI.findMatching(term, OntologyDataAccessService.DEFAULT_EXTERNAL_ONTOLOGY_MATCHING_PAGE_SIZE) >> returnedExternalItems
//        actualItemsData == expectedItemsData
        actualItemsData.toString() == expectedItemsData.toString()

        where:
        desc                                          | externalItemsData                              | expectedItemsData
        'no items returned'                           | []                                             | []
        '1 items returned'                            | [['1', 'someDisplay']]                         | [['1', 'someDisplay']]
        '2 items returned'                            | [['1', 'someDisplay'], ['2', 'someDisplay 2']] | [['1', 'someDisplay'], ['2', 'someDisplay 2']]
        '3 items case insensitive sorting on display' | [['1', 'c'], ['2', 'a'], ['3', 'B']]           | [['2', 'a'], ['3', 'B'], [1, 'c']]

    }

    void "test findExternalItemsByTerm with externalOntologyFactory throwing an exception"() {
        given: 'valid params'
        final String externalUrl = 'http://foo.com'
        final String term = '1'

        when: 'an exception is thrown'
        List<ExternalItem> externalItems = service.findExternalItemsByTerm(externalUrl, term)

        then: 'logging should happen at the error level and an exception should be thrown'
        1 * externalOntologyFactory.getExternalOntologyAPI(externalUrl) >> { throw new ExternalOntologyException("some exception") }
        1 * log.warn(_)
        thrown(ExternalOntologyException)
    }

    void "test externalOntologyHasIntegratedSearch() with exception"() {
        given: 'valid params'
        final String externalUrl = 'http://foo.com'

        when: 'an exception is thrown'
        boolean hasIntegratedSearch = service.externalOntologyHasIntegratedSearch(externalUrl)

        then: 'logging should happen at the error level and an exception should be thrown'
        1 * externalOntologyFactory.getExternalOntologyAPI(externalUrl) >> { throw new ExternalOntologyException("some exception") }
        1 * log.warn(_)
        hasIntegratedSearch == false
    }

    void "test externalOntologyHasIntegratedSearch() #desc"() {

        when: 'an exception is thrown'
        boolean hasIntegratedSearch = service.externalOntologyHasIntegratedSearch(externalUrl)

        then: 'logging should happen at the error level and an exception should be thrown'
        1 * externalOntologyFactory.getExternalOntologyAPI(externalUrl) >> { returnedExternalOntologyAPI }
        hasIntegratedSearch == expectIntegratedSearch

        where:
        desc                           | externalUrl                   | expectIntegratedSearch | returnedExternalOntologyAPI
        'no externalOntologyAPI found' | 'http://someUrl.com'          | false                  | null
        'externalOntologyAPI found'    | 'http://someSupportedUrl.com' | true                   | externalOntologyAPI
    }
}
