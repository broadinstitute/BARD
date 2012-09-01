<%@ page import="com.metasieve.shoppingcart.ShoppingCartService; bardqueryapi.QueryCartService" %>
<%
   ShoppingCartService  shoppingCartService = grailsApplication.classLoader.loadClass('com.metasieve.shoppingcart.ShoppingCartService').newInstance()
   QueryCartService  queryCartService  = grailsApplication.classLoader.loadClass('bardqueryapi.QueryCartService').newInstance()
   def mapOfUniqueItems = queryCartService.totalNumberOfUniqueItemsInCart(shoppingCartService)
%>
<div class="span2">
    <div class="well">
        <h5><i class="icon-shopping-cart"></i><a class="trigger" href="#">Query Cart</a></h5>
        <div class="btn-group">
            <a class="btn-small dropdown-toggle" data-toggle="dropdown" href="#">
                <i class="icon-eye-open"></i> Visualize
                <span class="caret"></span>
            </a>
            <ul class="dropdown-menu">
                <li><a href="#">Visualize as Molecular Spreadsheet</a></li>
                <li><a href="#">Visualize in Advanced Analysis Client</a></li>
            </ul>
        </div>
    </div>
</div>