package bardqueryapi

import com.metasieve.shoppingcart.ShoppingCartService
import com.metasieve.shoppingcart.ShoppingItem
import grails.plugin.spock.IntegrationSpec

import static org.junit.Assert.assertNotNull
import com.metasieve.shoppingcart.Shoppable

class ShoppingCartServiceIntegrationSpec extends IntegrationSpec {

    ShoppingCartService shoppingCartService

    void setup() {
        // Setup logic here
    }

    void tearDown() {
        // Tear down logic here
    }
    void testGetAssaysForAccessionTarget() {
        given: "A shopping cart"
           assertNotNull shoppingCartService
           CartAssay cartAssay = new CartAssay(assayTitle:"moo")

        when: "We make a Query to NCGC's rest API to get a list of assays with that target"
          shoppingCartService.addToShoppingCart(cartAssay)
          2.times {
              shoppingCartService.addToShoppingCart(cartAssay)
          }
          shoppingCartService.addToShoppingCart(cartAssay,2)

        then: "We get back a list assay ids"
          assert shoppingCartService.getQuantity(cartAssay)==5
          for (ShoppingItem shoppingItem  in shoppingCartService.getItems()) {
              def convertedShoppingItem = Shoppable.findByShoppingItem(shoppingItem)
              assert  convertedShoppingItem instanceof CartAssay
              CartAssay cartAssay1 = convertedShoppingItem as  CartAssay
              assertNotNull cartAssay1.assayTitle
              assert  convertedShoppingItem instanceof Shoppable
              Shoppable shoppable = convertedShoppingItem as Shoppable
              assert shoppable.id > 0
              assertNotNull shoppable.version
          }
    }

    void testGetAssaysForShoppingCartTarget() {
        given: "A shopping cart"
        assertNotNull shoppingCartService
        CartAssay cartAssay = new CartAssay(assayTitle:"moo")

        when: "We make a Query to NCGC's rest API to get a list of assays with that target"
        shoppingCartService.addToShoppingCart(cartAssay)
        2.times {
            shoppingCartService.addToShoppingCart(cartAssay)
        }
        shoppingCartService.addToShoppingCart(cartAssay,2)

        then: "We get back a list assay ids"
        assert shoppingCartService.getQuantity(cartAssay)==5
        for ( cartAssay1  in shoppingCartService.checkOut()) {
            assertNotNull(cartAssay1["item"])
            def x = Shoppable.findByShoppingItem(cartAssay1["item"])
            print  x.dump()
        }
    }


}
