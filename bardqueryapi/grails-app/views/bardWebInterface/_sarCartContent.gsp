<%@ page import="bardqueryapi.QueryCartService; com.metasieve.shoppingcart.Shoppable; com.metasieve.shoppingcart.ShoppingCart; bardqueryapi.CartAssay" %>
<%@ page import="com.metasieve.shoppingcart.ShoppingCartService" %>
<%
//    ShoppingCartService  shoppingCartService = grailsApplication.classLoader.loadClass('com.metasieve.shoppingcart.ShoppingCartService').newInstance()
    QueryCartService  queryCartService  = grailsApplication.classLoader.loadClass('bardqueryapi.QueryCartService').newInstance()
%>
<% if (queryCartService.groupUniqueContentsByType()[(QueryCartService.cartAssay)]) {%>
<g:each in="${queryCartService.groupUniqueContentsByType()[(QueryCartService.cartAssay)]}" var="elem" status="item">
    %{--<%--}%
        %{--def shoppingItem = Shoppable.findByShoppingItem(elem)--}%
        %{--CartAssay cartAssay1 = shoppingItem as  CartAssay--}%
        %{--Shoppable shoppable = shoppingItem as  Shoppable--}%
    %{--%>--}%
        <tr>
        <td>
            %{--${cartAssay1.toString()}--}%
            ${elem.toString()}
        </td>
        %{--<td>--}%
            %{--${shoppingCartService.getQuantity(elem)}--}%
        %{--</td>--}%
        <td>
            <a href="/bardqueryapi/sarCart/remove/${elem.id}"
               onclick="jQuery.ajax({  type:'POST',
                   data:{'id': '${elem.id}','class': 'class bardqueryapi.CartAssay','version': '0'},
                   url:'/bardqueryapi/sarCart/remove',
                   success:function(data,textStatus){
                       jQuery('#sarCartContent').html(data);
                   },
                   error:function(XMLHttpRequest,textStatus,errorThrown){
                       alert('problem removing assay')
                   }
               });
               return false;"
               action="remove"
               class="removeXMark"
               controller="sarCart">X</a>
        </td>
    </tr>
</g:each>
<% } %>