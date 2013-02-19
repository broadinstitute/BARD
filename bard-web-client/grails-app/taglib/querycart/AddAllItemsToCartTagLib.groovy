package querycart

class AddAllItemsToCartTagLib {

    def selectAllItemsInPage = { attrs, body ->

        out << '<input type="button" class="btn span2" id="addAllItemsToCart" value="Add All Items To Cart" name="addAllItemsToCart" mainDivName="' + attrs.mainDivName + '">'

    }
}
