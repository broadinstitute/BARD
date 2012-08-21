package bardqueryapi

import com.metasieve.shoppingcart.Shoppable
import com.metasieve.shoppingcart.ShoppingCartService

class QueryCartService {
    ShoppingCartService shoppingCartService
    final QueryCartService queryCartService
    static final  String  cartAssay = "CartAssay"
    static final  String  cartCompound = "CartCompound"


    ShoppingCartService getQueryCart () {
        shoppingCartService
    }




    LinkedHashMap<String,List<CartElement>> groupUniqueContentsByType(  ) {
        def  returnValue  = new LinkedHashMap<String,List<CartElement>> ()
        def  temporaryCartAssayHolder  = new ArrayList<CartAssay>()
        def  temporaryCartCompoundHolder  = new ArrayList<CartCompound>()
        if (shoppingCartService?.getItems()) {
        shoppingCartService.getItems().each { shoppingItemElement  ->
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
