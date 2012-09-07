<%@ page import="com.metasieve.shoppingcart.ShoppingCartService; bardqueryapi.QueryCartService" %>
<%
   ShoppingCartService  shoppingCartService = grailsApplication.classLoader.loadClass('com.metasieve.shoppingcart.ShoppingCartService').newInstance()
   QueryCartService  queryCartService  = grailsApplication.classLoader.loadClass('bardqueryapi.QueryCartService').newInstance()
   def mapOfUniqueItems = queryCartService.groupUniqueContentsByType(shoppingCartService)
%>
        <table>
            <tbody id="cartIdentRefill">
            <tr><td>

                <div class="row-fluid" style="height: 20px">
                    <div class="clear-fix">
                        <div class="pull-left">
                            <span class="addtocartfont"><i class="icon-shopping-cart"></i>Query Cart </span>
                        </div>
                        <div class="pull-right" style="padding-left: 20px;">
                            <button class="visualizebutton">
                                <div class="btn-group" style="vertical-align: middle;">
                                    <a class="btn-small dropdown-toggle" data-toggle="dropdown" href="#">
                                        Visualize
                                        <span class="caret"></span>
                                    </a>
                                    <ul class="dropdown-menu">
                                        <li><a href="#">Visualize as Molecular Spreadsheet</a></li>
                                        <li><a href="#">Visualize in Advanced Analysis Client</a></li>
                                    </ul>
                                </div>
                            </button>
                        </div>
                    </div>
                </div>
                <div class="row-fluid" style="height: 28px">
                    <hr width="100%" style="border-color: black">
                </div>

                <div class="row-fluid">
                    <div class="qcartDescriber">
                        <g:if test="${queryCartService.totalNumberOfUniqueItemsInCart(mapOfUniqueItems)==0}">
                            Empty
                        </g:if>
                        <g:else>
                            <%
                                Integer numberOfAssays = queryCartService.totalNumberOfUniqueItemsInCart(mapOfUniqueItems,QueryCartService.cartAssay)
                                Integer numberOfCompounds = queryCartService.totalNumberOfUniqueItemsInCart(mapOfUniqueItems,QueryCartService.cartCompound)
                                Integer numberOfProjects = queryCartService.totalNumberOfUniqueItemsInCart(mapOfUniqueItems,QueryCartService.cartProject)
                            %>
                            <table class="leftofcenter">
                                <g:if test="${numberOfAssays>0}">
                                    <tr>
                                        <td class="leftofcenter"><%=numberOfAssays%></td>
                                        <td class="leftofcenter">
                                            <g:if test="${numberOfAssays=1}">
                                               assay definition
                                            </g:if>
                                            <g:else>
                                                assay definitions
                                            </g:else></td>
                                    </tr>
                                </g:if>
                                <g:if test="${numberOfCompounds>0}">
                                    <tr>
                                        <td><%=numberOfCompounds%></td>
                                        <td>
                                            <g:if test="${numberOfCompounds=1}">
                                                 compound
                                            </g:if>
                                            <g:else>
                                                 compounds
                                            </g:else>
                                        </td>
                                    </tr>
                                </g:if>
                                <g:if test="${numberOfProjects>0}">
                                    <tr>
                                        <td> <%=numberOfProjects%></td>
                                        <td>
                                            <g:if test="${numberOfProjects=1}">
                                                 project
                                            </g:if>
                                            <g:else>
                                                 projects
                                            </g:else>
                                        </td>
                                    </tr>
                                </g:if>
                            </table>



                        </g:else>
                    </div>
                </div>
             </td></tr>
            </tbody>
     </table>
