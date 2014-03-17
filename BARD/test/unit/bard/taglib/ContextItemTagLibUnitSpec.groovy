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