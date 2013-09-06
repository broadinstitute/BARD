package querycart

class AddAllItemsToCartTagLib {

    def selectAllItemsInPage = { attrs, body ->

        out << "<div class='btn-group'>" +
                "<a class='btn' id='addAllItemsToCart'mainDivName='" +
                attrs.mainDivName +
                "'><i id='addAllItemsToCartButtonIcon' class=''></i> Add All Items To Cart</a></div>"

    }
}
