package bard.db.dictionary

import bard.db.enums.AddChildMethod
import bard.util.BardCacheUtilsService
import grails.buildtestdata.mixin.Build
import grails.converters.JSON
import grails.plugins.springsecurity.SpringSecurityService
import grails.test.mixin.TestFor
import spock.lang.Specification
import spock.lang.Unroll
import test.TestUtils

/**
 * Created with IntelliJ IDEA.
 * User: dlahr
 * Date: 4/24/13
 * Time: 11:48 AM
 * To change this template use File | Settings | File Templates.
 */
@TestFor(ElementService)
@Build([Element])
@Unroll
class ElementServiceUnitSpec extends Specification {
    def setup() {
        this.service.springSecurityService = Mock(SpringSecurityService)
        this.service.bardCacheUtilsService = Mock(BardCacheUtilsService)
    }

    void "test convertPathsToSelectWidgetStructures"() {

        given:
        List<ElementAndFullPath> elementListWithPaths = [new ElementAndFullPath(Element.build(description: "description1"))]

        when:
        def result = service.convertPathsToSelectWidgetStructures(elementListWithPaths)

        then:
        assert result[0]."id" == 1
        assert result[0]."text" == "label1"
        assert result[0]."description" == "description1"
        assert result[0]."fullPath" == '/label1/'
    }
}
