<%@ page import="bardqueryapi.QueryCartService; com.metasieve.shoppingcart.Shoppable; com.metasieve.shoppingcart.ShoppingCart; bardqueryapi.CartAssay" %>
<%@ page import="com.metasieve.shoppingcart.ShoppingCartService" %>
<%
    ShoppingCartService  shoppingCartService = grailsApplication.classLoader.loadClass('com.metasieve.shoppingcart.ShoppingCartService').newInstance()
    QueryCartService  queryCartService  = grailsApplication.classLoader.loadClass('bardqueryapi.QueryCartService').newInstance()
%>
<table>
    <tbody id="sarCartRefill">
    <tr><td>

<h4>Query Cart - Selected Results</h4>
<h5>${queryCartService.totalNumberOfUniqueItemsInCart(shoppingCartService)} results selected</h5>
<h4>COMPOUNDS</h4>

    <table>
<g:each in="${queryCartService.groupUniqueContentsByType(shoppingCartService)[(QueryCartService.cartCompound)]}" var="elem" status="item">
        <tr>
        <td>
            ${elem.toString()}
        </td>
        <td>
            <a href="/bardwebquery/sarCart/remove/${elem.id}"
               onclick="jQuery.ajax({  type:'POST',
                   data:{'id': '${elem.id}','class': 'class bardqueryapi.CartCompound','version': '0'},
                   url:'/bardwebquery/sarCart/remove',
                   success:function(data,textStatus){
                       jQuery('#sarCartRefill').html(data);
                   },
                   error:function(XMLHttpRequest,textStatus,errorThrown){
                       alert('problem removing compound')
                   }
               });
               return false;"
               action="remove"
               class="removeXMark"
               controller="sarCart">X</a>
        </td>
    </tr>
</g:each>
</table>
    </td></tr>
    <tr><td>
<br/>
<h4>ASSAY DEFINITIONS</h4>
<table>
<g:each in="${queryCartService.groupUniqueContentsByType(shoppingCartService)[(QueryCartService.cartAssay)]}" var="elem" status="item">
    <tr>
        <td>
            ${elem.toString()}
        </td>
        <td>
            <a href="/bardwebquery/sarCart/remove/${elem.id}"
               onclick="jQuery.ajax({  type:'POST',
                   data:{'id': '${elem.id}','class': 'class bardqueryapi.CartAssay','version': '0'},
                   url:'/bardwebquery/sarCart/remove',
                   success:function(data,textStatus){
                       jQuery('#sarCartRefill').html(data);
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
</table>
<br/>
        <div class="leftofline">

        <button>
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
        </button>
        </div>

        <div class="rightofline">

        <button>Clear all</button>

         </div>

    </td></tr>
        </tbody>
</table>
