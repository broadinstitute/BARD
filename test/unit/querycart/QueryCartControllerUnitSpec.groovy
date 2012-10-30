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
@Mock([Shoppable, QueryItem, CartAssay])
@Unroll
class QueryCartControllerUnitSpec extends Specification {
    ShoppingCartService shoppingCartService
    QueryCartService queryCartService
    CartAssay cartAssay

    static final Long ID_IN_CART = 1

    void setup() {
        this.shoppingCartService = Mock(ShoppingCartService)
        controller.shoppingCartService = this.shoppingCartService
        this.queryCartService = Mock(QueryCartService)
        controller.queryCartService = this.queryCartService
        queryCartService.groupUniqueContentsByType() >> {a: ID_IN_CART}
        cartAssay = new CartAssay("foo",ID_IN_CART)
        cartAssay.save()
    }

    void tearDown() {
        // Tear down logic here
    }





    void "test add empty"() {
        given:
        params.showCartDetails = "true"
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
        params.showCartDetails = "true"
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
        params.showCartDetails = "true"
        params.class = 'class querycart.CartProject'
        params.assayTitle = 'my assay'
        params.id = "2"


        when:
        queryCartService.addToShoppingCart(null) >> {1}
        controller.add()

        then:
        assert response.status == 200
    }



    void "test add CartCompound params.showCartDetails=#label"() {
        given:
        params.showCartDetails = "true"
        params.class = 'class querycart.CartCompound'
        params.name = 'my compound'
        params.smiles = 'CC'
        params.id = "2"
        params.showCartDetails = paramsShowDetails

        when:
        queryCartService.addToShoppingCart(_) >> {1}
        queryCartService.groupUniqueContentsByType(_) >> {[:]}
        queryCartService.totalNumberOfUniqueItemsInCart(_) >> {0}
        controller.add()

        then:
        assert response.status == 200

        where:
        label                            | paramsShowDetails
        'params.showCartDetails = false' | 'false'
        'params.showCartDetails = true'  | 'true'
    }




    void "test updateOnscreenCart"() {
        given:
        params.showCartDetails = "true"
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
        label                                  | shoppable                           | returnedValue | expectedReturnedValue
        'returnedValue=1'                      | new CartCompound('C-C', 'name', 1)  | 1             | 1
        'shoppable is null'                    | null as Shoppable                   | 0             | null
        'queryCartService throws an exception' | new CartCompound('C-C', 'name',  1) | -1            | null
    }

    void "test updateOnscreenCart() #label"() {
        given:
        params.showCartDetails = paramsShowCartDetails
        String mockContent = 'mock content'
        views['/bardWebInterface/_queryCartIndicator.gsp'] = mockContent
        views['/bardWebInterface/_sarCartContent.gsp'] = mockContent

        when:
        1 * queryCartService.groupUniqueContentsByType(_) >> {[:]}
        1 * queryCartService.totalNumberOfUniqueItemsInCart(_ as Map) >> {0}
        queryCartService.totalNumberOfUniqueItemsInCart(_,_) >> {0}

        controller.updateOnscreenCart()

        then:
        assert response.text == mockContent

        where:
        label                   | paramsShowCartDetails
        'show details'          | "true"
        'show summary'          | "false"
    }

    static final QueryItemType TYPE_IN_CART = QueryItemType.AssayDefinition

    void "test isInCart for #label"() {
        given:
        params.externalId = idToCheck as String
        params.type = typeToCheck as String

        when:
        if(shouldFind) {
            1 * queryCartService.isInShoppingCart(_) >> true
        }
        else {
            0 * queryCartService.isInShoppingCart(_)
        }
        controller.isInCart()

        then:
        assert response.text == "["+shouldFind+"]"

        where:
        label          | idToCheck  | typeToCheck             | shouldFind
        "Item in cart" | ID_IN_CART | TYPE_IN_CART            | true
        "diff ID"      | 6          | TYPE_IN_CART            | false
        "diff type"    | ID_IN_CART | QueryItemType.Compound  | false
        "null ID"      | null       | QueryItemType.Project   | false
        "null type"    | ID_IN_CART | null                    | false
    }

}
