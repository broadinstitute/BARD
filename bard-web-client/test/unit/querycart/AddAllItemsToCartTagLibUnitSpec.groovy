package querycart

import spock.lang.Unroll
import grails.test.mixin.TestFor
import spock.lang.Specification

@TestFor(AddAllItemsToCartTagLib)
@Unroll
class AddAllItemsToCartTagLibUnitSpec extends Specification {


    void "test selectAllItemsInPage #label"() {
        given:
        String template = '<g:selectAllItemsInPage mainDivName="assays"/>'

        when:
        String actualResults = applyTemplate(template).toString()

        then:
        assert actualResults == '<input type="button" class="btn span2" id="addAllItemsToCart" value="Add All Items To Cart" name="addAllItemsToCart" mainDivName="assays">'
    }
}
