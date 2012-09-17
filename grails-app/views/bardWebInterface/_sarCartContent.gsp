<%@ page import="bardqueryapi.QueryCartService; com.metasieve.shoppingcart.Shoppable; com.metasieve.shoppingcart.ShoppingCart; bardqueryapi.CartAssay" %>
<%@ page import="com.metasieve.shoppingcart.ShoppingCartService" %>
<%
    ShoppingCartService  shoppingCartService = grailsApplication.classLoader.loadClass('com.metasieve.shoppingcart.ShoppingCartService').newInstance()
    QueryCartService  queryCartService  = grailsApplication.classLoader.loadClass('bardqueryapi.QueryCartService').newInstance()
%>

<table class="QcartAppearance" id="detailView">
%{-- hold everything that Ajax will refill--}%
    <tbody id="sarCartRefill">
    <tr>
        <td>

         <h5><span class="qcartTitle">Query Cart -</span> <span class="qcartResults">Selected Results</span></h5>
         <h6>${queryCartService.totalNumberOfUniqueItemsInCart(shoppingCartService)} results selected</h6>
         <h5>COMPOUNDS</h5>

        <table class="QcartSubsection">
        %{-- hold everything related to assays --}%
            <g:each in="${queryCartService.groupUniqueContentsByType(shoppingCartService)[(QueryCartService.cartCompound)]}"
                    var="elem" status="item">
                <tr>
                    <td>
                        ${elem.toString()}
                    </td>
                    <td>
                        <a href="/bardwebquery/queryCart/remove/${elem.id}" class="removeXMark removeItemFromCart">X</a>
                    </td>
                </tr>
            </g:each>

        </table>

        </td>
    </tr>
    <tr>
        <td>

            <h5>ASSAY DEFINITIONS</h5>
            <table>
            <g:each in="${queryCartService.groupUniqueContentsByType(shoppingCartService)[(QueryCartService.cartAssay)]}" var="elem" status="item">
                <tr>
                    <td>
                        ${elem.toString()}
                    </td>
                    <td>
                        <a href="/bardwebquery/queryCart/remove/${elem.id}" class="removeXMark removeItemFromCart">X</a>
                    </td>
                </tr>
             </g:each>
             </table>

       </td>
    </tr>
    <tr>
        <td>


            <h5>PROJECTS</h5>
            <table>
                <g:each in="${queryCartService.groupUniqueContentsByType(shoppingCartService)[(QueryCartService.cartProject)]}" var="elem" status="item">
                    <tr>
                        <td>
                            ${elem.toString()}
                        </td>
                        <td>
                            <a href="/bardwebquery/queryCart/remove/${elem.id}" class="removeXMark removeItemFromCart">X</a>
                        </td>
                    </tr>
                </g:each>
            </table>

        </td>
    </tr>
    <tr>
        <td>


            <br/>
            <div class="leftofline">
            <button class="visualizebiggerbutton">
            <div class="btn-group" style="vertical-align: bottom;">
                <a class="btn-small dropdown-toggle" data-toggle="dropdown" href="#">
                    <i class="icon-eye-open"></i> Visualize
                    <span class="caret"></span>
                </a>
                <ul class="dropdown-menu">
                    <li><a href="#">Molecular Spreadsheet</a></li>
                    <li><a href="#">Advanced Analysis Client</a></li>
                </ul>
            </div>
            </button>
            </div>

            <div class="rightofline">

            <button  class="visualizebiggerbutton"><a href="#" class="removeAllFromCart">Clear all</a>
                </button>

             </div>

        </td>
    </tr>
    </tbody>
</table>
