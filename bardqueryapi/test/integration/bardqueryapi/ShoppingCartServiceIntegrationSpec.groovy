package bardqueryapi

import com.metasieve.shoppingcart.ShoppingCartService
import com.metasieve.shoppingcart.ShoppingItem
import grails.plugin.spock.IntegrationSpec

import static org.junit.Assert.assertNotNull
import com.metasieve.shoppingcart.Shoppable

class ShoppingCartServiceIntegrationSpec extends IntegrationSpec {

    ShoppingCartService shoppingCartService
    QueryCartService queryCartService

    void setup() {
        // Setup logic here
    }

    void tearDown() {
        // Tear down logic here
    }




    void "Make sure shopping cart handles all data types"() {
        given: "A shopping cart"
           assertNotNull shoppingCartService
           CartAssay cartAssay = new CartAssay(assayTitle:"Assay 1")
        CartAssay cartAssay1 = new CartAssay(assayTitle:"Assay 2")
        CartCompound cartCompound =  new  CartCompound(smiles: "c1ccccc1")

        when: "We make a Query to NCGC's rest API to get a list of assays with that target"
          shoppingCartService.addToShoppingCart(cartAssay)
          2.times {
              shoppingCartService.addToShoppingCart(cartCompound)
          }
        shoppingCartService.addToShoppingCart(cartAssay1,2)
       // shoppingCartService.addToShoppingCart(new CartAssay(assayTitle:"Assay 1"),2)

        then: "We get back a list assay ids"
          assert shoppingCartService.getQuantity(cartAssay)==1
          assert shoppingCartService.getQuantity(cartAssay1)==2
          assert shoppingCartService.getQuantity(cartCompound)==2
        assert queryCartService.totalNumberOfUniqueItemsInCart()==3

        for (ShoppingItem shoppingItem  in shoppingCartService.getItems()) {
              def convertedShoppingItem = Shoppable.findByShoppingItem(shoppingItem)
              if ( convertedShoppingItem instanceof CartAssay ) {
              CartAssay cartAssay2 = convertedShoppingItem as  CartAssay
              assertNotNull cartAssay2.assayTitle
              assert  convertedShoppingItem instanceof Shoppable
              Shoppable shoppable = convertedShoppingItem as Shoppable
              assert shoppable.id > 0
              assertNotNull shoppable.version
              }
          }
        LinkedHashMap<String,List<CartElement>> groupedContents = queryCartService.groupUniqueContentsByType(  )
        assert groupedContents.size()==2
        assert groupedContents[(QueryCartService.cartAssay)].size()==2
        assert groupedContents[(QueryCartService.cartCompound)].size()==1
        queryCartService.groupUniqueContentsByType()[(QueryCartService.cartAssay)].each{
            print it.id
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
        }
    }


}
