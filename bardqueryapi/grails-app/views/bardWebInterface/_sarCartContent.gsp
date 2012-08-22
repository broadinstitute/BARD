<%@ page import="bardqueryapi.QueryCartService; com.metasieve.shoppingcart.Shoppable; com.metasieve.shoppingcart.ShoppingCart; bardqueryapi.CartAssay" %>
<%@ page import="com.metasieve.shoppingcart.ShoppingCartService" %>
<%
    ShoppingCartService  shoppingCartService = grailsApplication.classLoader.loadClass('com.metasieve.shoppingcart.ShoppingCartService').newInstance()
    QueryCartService  queryCartService  = grailsApplication.classLoader.loadClass('bardqueryapi.QueryCartService').newInstance()
%>
<h2>Query Cart - Selected Results</h2>
<h4>${queryCartService.totalNumberOfUniqueItemsInCart()} results selected</h4>
<h3>COMPOUNDS</h3>
<g:each in="${queryCartService.groupUniqueContentsByType(shoppingCartService)[(QueryCartService.cartCompound)]}" var="elem" status="item">
        <tr>
        <td>
            ${elem.toString()}
        </td>
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
<br/>
<h3>ASSAY DEFINITIONS</h3>
<g:each in="${queryCartService.groupUniqueContentsByType(shoppingCartService)[(QueryCartService.cartAssay)]}" var="elem" status="item">
    <tr>
        <td>
            ${elem.toString()}
        </td>
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
<br/>
