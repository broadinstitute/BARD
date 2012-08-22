package bardqueryapi

import com.metasieve.shoppingcart.Shoppable
import com.metasieve.shoppingcart.ShoppingCartService

class QueryCartService {
    ShoppingCartService shoppingCartService
    static final  String  cartAssay = "CartAssay"
    static final  String  cartCompound = "CartCompound"


    ShoppingCartService getQueryCart () {
        shoppingCartService
    }

    /**
     * Count up all the elements of all different types. Do NOT attempt to detect overlap ( that is,
     * if you have a compound record, and you also have an assay record, but the assay also happens
     * to reference your compound, that is still considered to be two records )
     * @param shoppingCartSrvc
     * @return
     */
    int totalNumberOfUniqueItemsInCart ( ShoppingCartService shoppingCartSrvc = shoppingCartService ) {
        shoppingCartSrvc?.getItems()?.size() ?: 0
     }




    LinkedHashMap<String,List<CartElement>> groupUniqueContentsByType( ShoppingCartService shoppingCartSrvc = shoppingCartService ) {
        def  returnValue  = new LinkedHashMap<String,List<CartElement>> ()
        def  temporaryCartAssayHolder  = new ArrayList<CartAssay>()
        def  temporaryCartCompoundHolder  = new ArrayList<CartCompound>()
        if (shoppingCartSrvc?.getItems()) {
            shoppingCartSrvc.getItems().each { shoppingItemElement  ->
            def convertedShoppingItem = Shoppable.findByShoppingItem(shoppingItemElement)
            if ( convertedShoppingItem instanceof CartAssay ) {
                 temporaryCartAssayHolder.add(convertedShoppingItem as CartAssay)
            } else if (convertedShoppingItem instanceof CartCompound) {
                  temporaryCartCompoundHolder.add(convertedShoppingItem as CartCompound)
            }
        }
        }
        returnValue << [ (QueryCartService.cartAssay) : temporaryCartAssayHolder]
        returnValue << [ (QueryCartService.cartCompound) : temporaryCartCompoundHolder]
        returnValue
    }
}
