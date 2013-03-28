<%@ page import="bard.db.context.item.ContextItemController" %>
<%--
  Created by IntelliJ IDEA.
  User: xiaorong
  Date: 12/5/12
  Time: 5:34 PM
  To change this template use File | Settings | File Templates.
--%>


<%-- A template for both project and assay def, edit card --%>
<div id="card-${context.id}" class="card roundedBorder card-table-container">
    <table class="table table-hover">
        <caption id="${context.id}" class="assay_context">
            <div class="cardTitle">${context.preferredName}</div>
            <g:if test="${context instanceof bard.db.registration.AssayContext}">
                <div class="cardMenu">
                    <div class="btn-group dropup">
                        %{-- <a class="btn dropdown-toggle" data-toggle="dropdown" href="#"><span class="caret"></span></a> --}%
                        <a class="btn btn-info dropdown-toggle" data-toggle="dropdown" href="#"><span
                                class="icon-cog"></span></a>
                        <ul class="dropdown-menu" style="z-index:3999;left:-125px;">
                            <li style="text-align:left"><a href="#"
                                                           onclick="editCardName(${context.id}, '${context.preferredName}');
                                                           return false;"><i class="icon-pencil"></i> Edit card name</a>
                            </li>

                            <li style="text-align:left"><a href="#"
                                                           onclick="launchAddItemWizard(${context.owner.id}, ${context.id}, '${cardSection.replace(' > ', '> ')}');
                                                           return false;"><i class="icon-road"></i> Add item wizard</a>
                            </li>

                            <g:if test="${context.contextItems.size() == 0}">
                                <li style="text-align:left"><a href="#" onclick="deleteCard(${context.id});
                                return false;"><i class="icon-trash"></i> Delete card</a></li>
                            </g:if>
                        </ul>
                    </div>
                </div>
            </g:if>
            <g:else>
                <div class="cardMenu">
                    <div class="btn-group dropup">
                        <a class="btn btn-info dropdown-toggle" data-toggle="dropdown" href="#"><span
                                class="icon-cog"></span></a>
                        <ul class="dropdown-menu" style="z-index:3999;left:-125px;">
                            <li style="text-align:left">
                                <g:link controller="contextItem" action="create"
                                        params="${[contextId: context?.id,
                                                contextClass: context?.class?.simpleName,
                                                contextOwnerId: context?.owner?.id]}">Add item</g:link>
                            </li>
                        </ul>
                    </div>
                </div>
            </g:else>
        </caption>
        <tbody>
        <g:each in="${context.contextItems}" status="i" var="contextItem">
            <tr id="${contextItem.id}" class='context_item_row'>
                <td class="attributeLabel">${contextItem.attributeElement?.label}</td>
                <td class="valuedLabel">${contextItem.valueDisplay}</td>
                <td class="deleteItemButton">

                    <div class="btn-group dropup">
                        <a class="btn dropdown-toggle" data-toggle="dropdown" href="#"><span class="caret"></span></a>
                        <ul class="dropdown-menu" style="z-index:10000;min-width: 40px;left: -70px;">
                            <g:if test="${context instanceof bard.db.registration.AssayContext}">
                                <li><a href="#" onclick="moveCardItem(${contextOwner.id}, ${contextItem.id});
                                return false;"><i class="icon-move"></i> Move</a></li>
                                <li><a href="#" onclick="deleteCardItem(${contextItem.id}, ${context.id});
                                return false;"><i class="icon-trash"></i> Delete</a></li>
                            </g:if>
                            <g:else>

                                <g:form controller="contextItem"
                                        onsubmit="return confirm('Are you sure you wish to delete this item?');">
                                    <g:hiddenField name="contextItemId" value="${contextItem.id}"/>
                                    <g:hiddenField name="contextId" value="${context?.id}"/>
                                    <g:hiddenField name="contextClass" value="${context?.class?.simpleName}"/>
                                    <g:hiddenField name="contextOwnerId" value="${context?.owner?.id}"/>
                                    <li>
                                        <button type="submit" name="_action_delete" class="btn btn-link"><i class="icon-trash"></i>Delete</button>
                                    </li>
                                    <li>
                                        <button type="submit" name="_action_edit" class="btn btn-link"><i class="icon-pencil"></i>Edit</button>
                                    </li>
                                </g:form>

                            </g:else>
                        </ul>
                    </div>

                </td>
            </tr>
        </g:each>
        </tbody>
    </table>
</div>


