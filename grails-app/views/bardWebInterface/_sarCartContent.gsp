<%@ page import="bardqueryapi.QueryCartService; com.metasieve.shoppingcart.Shoppable; com.metasieve.shoppingcart.ShoppingCart; bardqueryapi.CartAssay" %>
<%@ page import="com.metasieve.shoppingcart.ShoppingCartService" %>
<%
    ShoppingCartService  shoppingCartService = grailsApplication.classLoader.loadClass('com.metasieve.shoppingcart.ShoppingCartService').newInstance()
    QueryCartService  queryCartService  = grailsApplication.classLoader.loadClass('bardqueryapi.QueryCartService').newInstance()
%>
<table>
    <tbody id="sarCartRefill">
    <tr><td>

<h3>Query Cart - Selected Results</h3>
<h4>${queryCartService.totalNumberOfUniqueItemsInCart(shoppingCartService)} results selected</h4>
<h3>COMPOUNDS</h3>

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
<h3>ASSAY DEFINITIONS</h3>
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
        <g:form name="aidForm" controller="bardWebInterface" action="search">

            <div class="content ">
                <table class="skinnyTable" width=100%>
                    <tr>
                        <td width=100%>
                         </td>
                        <td>
                            <g:submitButton name="search"
                                            value="Generate Molecular Spreadsheet"/>
                        </td>
                    </tr>
                 </table>
            </div>
        </g:form>

    </td></tr>
        </tbody>
</table>
