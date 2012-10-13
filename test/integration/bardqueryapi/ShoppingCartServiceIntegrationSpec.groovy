package bardqueryapi
import static org.junit.Assert.assertNotNull
import static org.junit.Assert.assertNull

import com.metasieve.shoppingcart.Shoppable
import com.metasieve.shoppingcart.ShoppingCartService
import com.metasieve.shoppingcart.ShoppingItem
import grails.plugin.spock.IntegrationSpec


class ShoppingCartServiceIntegrationSpec extends IntegrationSpec {

    ShoppingCartService shoppingCartService
    QueryCartService queryCartService

    void setup() {
        // Setup logic here
    }

    void tearDown() {
        // Tear down logic here
    }




    void "Test core shopping cart functionality -- if this doesn't work then nothing will"() {
        given: "A shopping cart"
        assertNotNull shoppingCartService
        CartAssay cartAssay = new CartAssay(assayTitle: "This is an assay")

        when: "We make a Query to NCGC's rest API to get a list of assays with that target"
        shoppingCartService.addToShoppingCart(cartAssay)
        2.times {
            shoppingCartService.addToShoppingCart(cartAssay)
        }
        shoppingCartService.addToShoppingCart(cartAssay, 2)

        then: "We get back a list assay ids"
        assert shoppingCartService.getQuantity(cartAssay) == 5
        for (cartAssay1 in shoppingCartService.checkOut()) {
            assertNotNull(cartAssay1["item"])
        }
    }





    void "Make sure shopping cart handles all data types"() {
        given: "A shopping cart"
        assertNotNull shoppingCartService
        CartAssay cartAssay = new CartAssay(assayTitle: "Assay 1")
        CartAssay cartAssay1 = new CartAssay(assayTitle: "Assay 2")
        CartCompound cartCompound = new CartCompound(smiles: "c1ccccc1")

        when: "We make a Query to NCGC's rest API to get a list of assays with that target"
        shoppingCartService.addToShoppingCart(cartAssay)
        2.times {
            shoppingCartService.addToShoppingCart(cartCompound)
        }
        shoppingCartService.addToShoppingCart(cartAssay1, 2)

        then: "We get back a list assay ids"
        assert shoppingCartService.getQuantity(cartAssay) == 1
        assert shoppingCartService.getQuantity(cartAssay1) == 2
        assert shoppingCartService.getQuantity(cartCompound) == 2

        for (ShoppingItem shoppingItem in shoppingCartService.items) {
            def convertedShoppingItem = Shoppable.findByShoppingItem(shoppingItem)
            if (convertedShoppingItem instanceof CartAssay) {
                CartAssay cartAssay2 = convertedShoppingItem as CartAssay
                assertNotNull cartAssay2.assayTitle
                assert convertedShoppingItem instanceof Shoppable
                Shoppable shoppable = convertedShoppingItem as Shoppable
                assert shoppable.id > 0
                assertNotNull shoppable.version
            }
        }
    }

    void "Does each cart type work"() {
        given: "A shopping cart"
        assertNotNull shoppingCartService
        CartAssay cartAssay = new CartAssay(assayTitle: "Assay 1")
        CartCompound cartCompound = new CartCompound(smiles: "c1ccccc1", name: "cmpd name", compoundId: 47)
        CartProject cartProject = new CartProject(projectName: "my project")

        when: "We make a Query to NCGC's rest API to get a list of assays with that target"
        shoppingCartService.addToShoppingCart(cartAssay)
        shoppingCartService.addToShoppingCart(cartCompound)
        shoppingCartService.addToShoppingCart(cartProject)

        then: "We get back a list assay ids"
        assert queryCartService.totalNumberOfUniqueItemsInCart() == 3

        Map<String, List> groupedContents = queryCartService.groupUniqueContentsByType()
        assert groupedContents.size() == 3
        assert groupedContents[(QueryCartService.cartAssay)].size() == 1
        assert groupedContents[(QueryCartService.cartCompound)].size() == 1
        assert groupedContents[(QueryCartService.cartProject)].size() == 1
        queryCartService.groupUniqueContentsByType()[(QueryCartService.cartAssay)].each {  cartElement ->
            assert cartElement.id > 0
        }
    }


    void "Make sure queryCartService detects unique elements as expected"() {
        given: "A shopping cart"
        assertNotNull shoppingCartService
        CartAssay cartAssay = new CartAssay(assayTitle: "Assay 1")
        CartAssay cartAssay1 = new CartAssay(assayTitle: "Assay 2")
        CartCompound cartCompound = new CartCompound(smiles: "c1ccccc1", name: "cmpd name", compoundId: 47)

        when: "We make a Query to NCGC's rest API to get a list of assays with that target"
        shoppingCartService.addToShoppingCart(cartAssay)
        shoppingCartService.addToShoppingCart(cartAssay)
        2.times {
            shoppingCartService.addToShoppingCart(cartCompound)
        }
        shoppingCartService.addToShoppingCart(cartAssay1, 2)

        then: "We get back a list assay ids"
        assert queryCartService.totalNumberOfUniqueItemsInCart() == 3

        Map<String, List> groupedContents = queryCartService.groupUniqueContentsByType()
        assert groupedContents.size() == 3
        assert groupedContents[(QueryCartService.cartAssay)].size() == 2
        assert groupedContents[(QueryCartService.cartCompound)].size() == 1
        assert groupedContents[(QueryCartService.cartProject)].size() == 0
        queryCartService.groupUniqueContentsByType()[(QueryCartService.cartAssay)].each {  cartElement ->
            assert cartElement.id > 0
        }
    }

    /**
     * Check the shopping cart uniqueness of CartAssays.  Three cases to consider:
     *   1) multiple instances of the same record
     *   2) different records with the same name
     *   3) different records with different names
     *  The first two cases should be treated identically, which is to say that only one record
     *  that is considered equivalent may be allowed in the shopping cart.  If the records have
     *  different names then we should be able to add as many records to the shopping cart as we like.
     */
    void "Exploring the nature of CartAssay uniqueness"() {
        given: "A shopping cart and some testing objects"
        assertNotNull shoppingCartService
        CartAssay cartAssay_identical1 = new CartAssay(assayTitle: "Assay 1")
        CartAssay cartAssay_identical2 = cartAssay_identical1
        CartAssay cartAssay_sameName1 = new CartAssay(assayTitle: "Assay 2")
        CartAssay cartAssay_sameName2 = new CartAssay(assayTitle: "Assay 2")
        CartAssay cartAssay_differentName1 = new CartAssay(assayTitle: "Assay 3")
        CartAssay cartAssay_differentName2 = new CartAssay(assayTitle: "Assay 4")

        when: "the shopping cart is functional and ready for testing"
        queryCartService.emptyShoppingCart()
        queryCartService.addToShoppingCart(cartAssay_identical1)
        assert queryCartService.totalNumberOfUniqueItemsInCart() == 1


        then: "The cart must exclude objects only when we consider them to be identical"
        assertNull queryCartService.addToShoppingCart(cartAssay_identical2)
        assert queryCartService.totalNumberOfUniqueItemsInCart() == 1
        assertNotNull queryCartService.addToShoppingCart(cartAssay_sameName1)
        assert queryCartService.totalNumberOfUniqueItemsInCart() == 2
        assertNull queryCartService.addToShoppingCart(cartAssay_sameName2)
        assert queryCartService.totalNumberOfUniqueItemsInCart() == 2
        assertNotNull queryCartService.addToShoppingCart(cartAssay_differentName1)
        assert queryCartService.totalNumberOfUniqueItemsInCart() == 3
        assertNotNull queryCartService.addToShoppingCart(cartAssay_differentName2)
        Map<String, List> objectMap = queryCartService.groupUniqueContentsByType()
        queryCartService.totalNumberOfUniqueItemsInCart(objectMap) == 4
        queryCartService.totalNumberOfUniqueItemsInCart(objectMap, QueryCartService.cartAssay) == 4
        assert queryCartService.totalNumberOfUniqueItemsInCart() == 4
    }

    /**
     * Check the shopping cart uniqueness of CartCompounds.  Three cases to consider:
     *   1) multiple instances of the same record
     *   2) different records with the same name
     *   3) different records with different names
     *  The first two cases should be treated identically, which is to say that only one record
     *  that is considered equivalent may be allowed in the shopping cart.  If the records have
     *  different names then we should be able to add as many records to the shopping cart as we like.
     */
    void "Exploring the nature of CartCompound uniqueness"() {
        given: "A shopping cart and some testing objects"
        assertNotNull shoppingCartService
        CartCompound cartCompound_identical1 = new CartCompound(smiles: "c1ccccc1", name: "cmpdname", compoundId: 47)
        CartCompound cartCompound_identical2 = cartCompound_identical1
        CartCompound cartCompound_sameName1 = new CartCompound(smiles: "c2ccccc2", name: "cmpdname", compoundId: 47)
        CartCompound cartCompound_sameName2 = new CartCompound(smiles: "c2ccccc2", name: "cmpdname", compoundId: 47)
        CartCompound cartCompound_differentName1 = new CartCompound(smiles: "c3ccccc3", name: "cmpdname", compoundId: 47)
        CartCompound cartCompound_differentName2 = new CartCompound(smiles: "c4ccccc4", name: "cmpdname", compoundId: 47)

        when: "the shopping cart is functional and ready for testing"
        queryCartService.emptyShoppingCart()
        queryCartService.addToShoppingCart(cartCompound_identical1)
        assert queryCartService.totalNumberOfUniqueItemsInCart() == 1


        then: "The cart must exclude objects only when we consider them to be identical"
        assertNull queryCartService.addToShoppingCart(cartCompound_identical2)
        assert queryCartService.totalNumberOfUniqueItemsInCart() == 1
        assertNotNull queryCartService.addToShoppingCart(cartCompound_sameName1)
        assert queryCartService.totalNumberOfUniqueItemsInCart() == 2
        assertNull queryCartService.addToShoppingCart(cartCompound_sameName2)
        assert queryCartService.totalNumberOfUniqueItemsInCart() == 2
        assertNotNull queryCartService.addToShoppingCart(cartCompound_differentName1)
        assert queryCartService.totalNumberOfUniqueItemsInCart() == 3
        assertNotNull queryCartService.addToShoppingCart(cartCompound_differentName2)
        Map<String, List> objectMap = queryCartService.groupUniqueContentsByType()
        queryCartService.totalNumberOfUniqueItemsInCart(objectMap) == 4
        queryCartService.totalNumberOfUniqueItemsInCart(objectMap, QueryCartService.cartCompound) == 4
        assert queryCartService.totalNumberOfUniqueItemsInCart() == 4
    }

    /**
     * Check the shopping cart uniqueness of CartProjects.  Three cases to consider:
     *   1) multiple instances of the same record
     *   2) different records with the same name
     *   3) different records with different names
     *  The first two cases should be treated identically, which is to say that only one record
     *  that is considered equivalent may be allowed in the shopping cart.  If the records have
     *  different names then we should be able to add as many records to the shopping cart as we like.
     */
    void "Exploring the nature of CartProject uniqueness"() {
        given: "A shopping cart and some testing objects"
        assertNotNull shoppingCartService
        CartProject cartProject_identical1 = new CartProject(projectName: "projectname1")
        CartProject cartProject_identical2 = cartProject_identical1
        CartProject cartProject_sameName1 = new CartProject(projectName: "projectname2")
        CartProject cartProject_sameName2 = new CartProject(projectName: "projectname2")
        CartProject cartProject_differentName1 = new CartProject(projectName: "projectname3")
        CartProject cartProject_differentName2 = new CartProject(projectName: "projectname4")

        when: "the shopping cart is functional and ready for testing"
        Map<String, List> objectMap = queryCartService.groupUniqueContentsByType()
        queryCartService.totalNumberOfUniqueItemsInCart(objectMap) == 0
        queryCartService.emptyShoppingCart()
        queryCartService.addToShoppingCart(cartProject_identical1)
        assert queryCartService.totalNumberOfUniqueItemsInCart() == 1


        then: "The cart must exclude objects only when we consider them to be identical"
        assertNull queryCartService.addToShoppingCart(cartProject_identical2)
        assert queryCartService.totalNumberOfUniqueItemsInCart() == 1
        assertNotNull queryCartService.addToShoppingCart(cartProject_sameName1)
        assert queryCartService.totalNumberOfUniqueItemsInCart() == 2
        assertNull queryCartService.addToShoppingCart(cartProject_sameName2)
        assert queryCartService.totalNumberOfUniqueItemsInCart() == 2
        assertNotNull queryCartService.addToShoppingCart(cartProject_differentName1)
        assert queryCartService.totalNumberOfUniqueItemsInCart() == 3
        assertNotNull queryCartService.addToShoppingCart(cartProject_differentName2)
        assert queryCartService.totalNumberOfUniqueItemsInCart() == 4
        Map<String, List> objectMap2 = queryCartService.groupUniqueContentsByType()
        queryCartService.totalNumberOfUniqueItemsInCart(objectMap2) == 4
        queryCartService.totalNumberOfUniqueItemsInCart(objectMap2, QueryCartService.cartProject) == 4
        queryCartService.totalNumberOfUniqueItemsInCart() == 4
    }


}
