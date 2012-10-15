package querycart

import grails.test.mixin.TestFor
import grails.test.mixin.TestMixin
import grails.test.mixin.support.GrailsUnitTestMixin
import spock.lang.Unroll
import spock.lang.Specification
import com.metasieve.shoppingcart.ShoppingCartService
import com.metasieve.shoppingcart.Shoppable
import grails.test.mixin.Mock

@TestMixin(GrailsUnitTestMixin)
@TestFor(QueryCartController)
@Mock(Shoppable)
@Unroll
class QueryCartControllerUnitSpec extends Specification {
    ShoppingCartService shoppingCartService
    QueryCartService queryCartService
    CartAssay cartAssay

    void setup() {
        this.shoppingCartService = Mock(ShoppingCartService)
        controller.shoppingCartService = this.shoppingCartService
        this.queryCartService = Mock(QueryCartService)
        controller.queryCartService = this.queryCartService
        queryCartService.groupUniqueContentsByType() >> {a: 1}
        cartAssay = new CartAssay(assayTitle: "foo")
    }

    void tearDown() {
        // Tear down logic here
    }





    void "test add empty"() {
        given:
        params.stt = "1"
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
        params.stt = "1"
        params.class = 'class querycart.CartAssay'
        params.assayTitle = 'my assay'
        params.id = "2"


        when:
        queryCartService.addToShoppingCart(null) >> {cartAssay}
        controller.add()

        then:
        assert response.status == 200
    }






    void "test add CartProject"() {
        given:
        params.stt = "1"
        params.class = 'class querycart.CartProject'
        params.assayTitle = 'my assay'
        params.id = "2"


        when:
        queryCartService.addToShoppingCart(null) >> {1}
        controller.add()

        then:
        assert response.status == 200
    }



    void "test add CartCompound params.stt=#label"() {
        given:
        params.stt = "1"
        params.class = 'class querycart.CartCompound'
        params.assayTitle = 'my assay'
        params.id = "2"
        params.stt = paramsStt

        when:
        queryCartService.addToShoppingCart(_) >> {1}
        queryCartService.groupUniqueContentsByType(_) >> {[:]}
        queryCartService.totalNumberOfUniqueItemsInCart(_) >> {0}
        controller.add()

        then:
        assert response.status == 200

        where:
        label            | paramsStt
        'params.stt = 0' | '0'
        'params.stt = 1' | '1'
    }




    void "test updateOnscreenCart"() {
        given:
        params.stt = "1"
        LinkedHashMap<String, List> mapOfUniqueItems = []
        mapOfUniqueItems[QueryCartService.cartAssay] = [cartAssay]


        when:
        queryCartService.groupUniqueContentsByType(_) >> {mapOfUniqueItems}
        controller.updateOnscreenCart()

        then:
        assert response.status == 200
    }


    void "test updateSummary"() {
        given:
        LinkedHashMap<String, List> mapOfUniqueItems = []
        mapOfUniqueItems[QueryCartService.cartAssay] = [cartAssay]


        when:
        queryCartService.groupUniqueContentsByType(_, _) >> {mapOfUniqueItems}
        controller.updateSummary()

        then:
        assert response.status == 200
    }




    void "test updateDetails"() {
        given:
        LinkedHashMap<String, List> mapOfUniqueItems = []
        mapOfUniqueItems[QueryCartService.cartAssay] = [cartAssay]


        when:
        queryCartService.groupUniqueContentsByType(_) >> {mapOfUniqueItems}
        controller.updateDetails()

        then:
        assert response.status == 200
    }


    void "test remove"() {
        given:
        params.id = "1"
        LinkedHashMap<String, List> mapOfUniqueItems = []
        mapOfUniqueItems[QueryCartService.cartAssay] = [cartAssay]

        when:
        queryCartService.groupUniqueContentsByType(_) >> {mapOfUniqueItems}
        queryCartService.removeFromShoppingCart(_) >> {
            if (returnedRemoveFromShoppingCart == -1) {
                throw new RuntimeException('exception')
            }
            return null
        }
        controller.remove()

        then:
        assert response.status == 200

        where:
        label                | returnedRemoveFromShoppingCart
        'Good case'          | null
        'throw an exception' | -1
    }


    void "test removeAll"() {
        given:
        LinkedHashMap<String, List> mapOfUniqueItems = []
        mapOfUniqueItems[QueryCartService.cartAssay] = [cartAssay]

        when:
        queryCartService.groupUniqueContentsByType(_) >> {mapOfUniqueItems}
        queryCartService.emptyShoppingCart() >> {null}
        controller.removeAll()

        then:
        assert response.status == 200
    }

    void "test handleAddingToShoppingCart() #label"() {
        given:

        when:
        queryCartService.addToShoppingCart(_) >> {
            if (returnedValue == -1) {
                throw new RuntimeException('exception')
            }
            return returnedValue
        }
        def retVal = controller.handleAddingToShoppingCart(shoppable)

        then:
        assert retVal == expectedReturnedValue

        where:
        label                                  | shoppable                                                    | returnedValue | expectedReturnedValue
        'returnedValue=1'                      | new CartCompound(smiles: 'C-C', name: 'name', compoundId: 1) | 1             | 1
        'shoppable is null'                    | null as Shoppable                                            | 0             | null
        'queryCartService throws an exception' | new CartCompound(smiles: 'C-C', name: 'name', compoundId: 1) | -1            | null
    }

    void "test updateOnscreenCart() #label"() {
        given:
        params.stt = paramsStt

        when:
        def retVal = controller.updateOnscreenCart()

        then:
        queryCartService.addToShoppingCart(_) >> {1}
        queryCartService.groupUniqueContentsByType(_) >> {[:]}
        queryCartService.totalNumberOfUniqueItemsInCart(_) >> {0}
        assert retVal == expectedResult

        where:
        label                   | paramsStt | expectedResult
        'somthingReallyChanged' | "1"       | null
        'nothing has changed'   | "0"       | null
    }

}
