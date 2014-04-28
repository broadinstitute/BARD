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

package bard.taglib

import bard.db.dictionary.Element
import bard.db.registration.AssayContextItem
import grails.buildtestdata.mixin.Build
import grails.test.mixin.TestFor
import spock.lang.Specification
import spock.lang.Unroll

import static bard.taglib.ContextItemTagLib.PERSON_URL

/**
 * See the API for {@link grails.test.mixin.web.GroovyPageUnitTestMixin} for usage instructions
 */
@TestFor(ContextItemTagLib)
@Build([AssayContextItem, Element])
@Unroll
class ContextItemTagLibUnitSpec extends Specification {

    def setup() {
    }

    def cleanup() {
    }

    void "test renderContextItemValueDisplay externalUrl: #externalUrl"() {
        AssayContextItem item = AssayContextItem.build()
        item.attributeElement.externalURL = externalUrl
        item.valueDisplay = valueDisplay
        item.extValueId = extValueId

        expect:
        applyTemplate('<g:renderContextItemValueDisplay contextItem="${contextItem}" />', [contextItem: item]) == expected

        where:
        externalUrl       | valueDisplay | extValueId | expected
        null              | 'foo'        | null       | 'foo'
        PERSON_URL        | 'foo'        | null       | 'foo'
        'http://foo.com/' | 'foo'        | '123'      | "<a href='http://foo.com/123' target='_blank' >foo</a>"
    }
}
