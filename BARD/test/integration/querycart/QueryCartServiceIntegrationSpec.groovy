package querycart

import com.metasieve.shoppingcart.ShoppingCartService
import grails.plugin.spock.IntegrationSpec

class QueryCartServiceIntegrationSpec extends IntegrationSpec {

    ShoppingCartService shoppingCartService
    QueryCartService queryCartService



    void "test retrieve Carts"() {
        given: "Shopping Carts"
        CartAssay cartAssay = new CartAssay("This is an assay", 1, 1)
        CartCompound cartCompound = new CartCompound("c1ccccc1", "cmpd name", 1, 0, 0)
        CartProject cartProject = new CartProject("my project", 1, 1)
        shoppingCartService.addToShoppingCart(cartAssay)
        shoppingCartService.addToShoppingCart(cartCompound)
        shoppingCartService.addToShoppingCart(cartProject)
        when:
        final List<Long> cids = queryCartService.retrieveCartCompoundIdsFromShoppingCart()
        and:
        final List<Long> pids = queryCartService.retrieveCartProjectIdsFromShoppingCart()
        and:
        final List<Long> adids = queryCartService.retrieveCartAssayIdsFromShoppingCart()
        then:
        assert cids == [1]
        assert pids == [1]
        assert adids == [1]
    }

}
