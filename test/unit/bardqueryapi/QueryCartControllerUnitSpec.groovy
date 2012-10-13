package bardqueryapi

import grails.test.mixin.TestFor
import grails.test.mixin.TestMixin
import grails.test.mixin.support.GrailsUnitTestMixin
import spock.lang.Unroll
import spock.lang.Specification
import com.metasieve.shoppingcart.ShoppingCartService


@TestMixin(GrailsUnitTestMixin)
@TestFor(QueryCartController)
@Unroll
class QueryCartControllerUnitSpec extends Specification  {
    ShoppingCartService shoppingCartService
    QueryCartService queryCartService
    CartAssay cartAssay

    void setup() {
        this.shoppingCartService = Mock(ShoppingCartService)
        controller.shoppingCartService =  this.shoppingCartService
        this.queryCartService = Mock(QueryCartService)
        controller.queryCartService =  this.queryCartService
        queryCartService.groupUniqueContentsByType() >> {a:1}
        cartAssay = new CartAssay(assayTitle: "foo")
    }

    void tearDown() {
        // Tear down logic here
    }





    void "test add empty"() {
        given:
        params.stt="1"
        params.class = 'class bardqueryapi.Fake'
        params.assayTitle = 'my assay'
        params.id = "2"

        when:
        controller.add()
        //handleAddTest(controller)

        then:

        assert response.status == 200
    }









    void "test add CartAssay"() {
        given:
        params.stt="1"
        params.class = 'class bardqueryapi.CartAssay'
        params.assayTitle = 'my assay'
        params.id = "2"


        when:
        queryCartService.addToShoppingCart(null) >>   {cartAssay}
        controller.add()

        then:
        assert  response.status == 200
    }






    void "test add CartProject"() {
        given:
        params.stt="1"
        params.class = 'class bardqueryapi.CartProject'
        params.assayTitle = 'my assay'
        params.id = "2"


        when:
        queryCartService.addToShoppingCart(null) >>   {1}
        controller.add()

        then:
        assert  response.status == 200
    }



    void "test add CartCompound"() {
        given:
        params.stt="1"
        params.class = 'class bardqueryapi.CartCompound'
        params.assayTitle = 'my assay'
        params.id = "2"


        when:
        queryCartService.addToShoppingCart(null) >>   {1}
        controller.add()

        then:
        assert  response.status == 200
    }




    void "test updateOnscreenCart"() {
        given:
        params.stt="1"
        LinkedHashMap<String, List> mapOfUniqueItems = []
        mapOfUniqueItems[QueryCartService.cartAssay] = [cartAssay]


        when:
        queryCartService.groupUniqueContentsByType(_) >>   {mapOfUniqueItems}
        controller.updateOnscreenCart()

        then:
        assert response.status == 200
    }


    void "test updateSummary"() {
        given:
        LinkedHashMap<String, List> mapOfUniqueItems = []
        mapOfUniqueItems[QueryCartService.cartAssay] = [cartAssay]


        when:
        queryCartService.groupUniqueContentsByType(_,_) >>   {mapOfUniqueItems}
        controller.updateSummary()

        then:
        assert response.status == 200
    }




    void "test updateDetails"() {
        given:
        LinkedHashMap<String, List> mapOfUniqueItems = []
        mapOfUniqueItems[QueryCartService.cartAssay] = [cartAssay]


        when:
        queryCartService.groupUniqueContentsByType(_) >>   {mapOfUniqueItems}
        controller.updateDetails()

        then:
        assert response.status == 200
    }


    void "test remove"() {
        given:
        params.id="1"
        LinkedHashMap<String, List> mapOfUniqueItems = []
        mapOfUniqueItems[QueryCartService.cartAssay] = [cartAssay]

        when:
        queryCartService.groupUniqueContentsByType(_) >>   {mapOfUniqueItems}
        queryCartService.removeFromShoppingCart(_) >> {null}
        controller.remove()

        then:
        assert response.status == 200
    }


    void "test removeAll"() {
    given:
    LinkedHashMap<String, List> mapOfUniqueItems = []
    mapOfUniqueItems[QueryCartService.cartAssay] = [cartAssay]

    when:
    queryCartService.groupUniqueContentsByType(_) >>   {mapOfUniqueItems}
    queryCartService.emptyShoppingCart() >> {null}
    controller.removeAll()

    then:
    assert response.status == 200
    }




}
