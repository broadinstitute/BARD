package querycart

import grails.test.mixin.TestFor
import spock.lang.Specification
import spock.lang.Unroll

@TestFor(AddAllItemsToCartTagLib)
@Unroll
class AddAllItemsToCartTagLibUnitSpec extends Specification {


    void "test selectAllItemsInPage #label"() {
        given:
        String template = '<g:selectAllItemsInPage mainDivName="assays"/>'

        when:
        String actualResults = applyTemplate(template).toString()

        then:
        assert actualResults == "<div class='btn-group'><a class='btn' id='addAllItemsToCart'mainDivName='assays'><i id='addAllItemsToCartButtonIcon' class=''></i> Add All Items To Cart</a></div>"
    }
}
