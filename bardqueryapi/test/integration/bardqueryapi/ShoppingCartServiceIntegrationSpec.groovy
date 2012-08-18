package bardqueryapi
import grails.plugin.spock.IntegrationSpec
import com.metasieve.shoppingcart.ShoppingCartService
import static org.junit.Assert.*

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
    }
}
