<%@ page import="bard.db.registration.AssayContextItem; bard.db.registration.AttributeType; bard.db.context.item.ContextItemController" %>
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
            <div class="cardTitle">
                %{--We will put error messages on this card--}%
                <div id="${context.id}_Errors"></div>
                ${context.preferredName}
                <g:if test="${context instanceof bard.db.registration.AssayContext}">
                    <a class="btn btn-mini" href="#" title="Edit card name"
                       onclick="editCardName(${context.id}, '${context.preferredName}');
                       return false;"><i class="icon-pencil"></i></a>
                </g:if>
            </div>

            <div class="cardMenu">
                <div class="btn-toolbar">
                    <div class="btn-group">
                        <g:if test="${context instanceof bard.db.registration.AssayContext}">
                            <a class="btn btn-info btn-mini" href="#" title="Add item"
                               onclick="launchAddItemWizard(${context.owner.id}, ${context.id}, '${cardSection.replace(' > ', '> ')}');
                               return false;"><i class="icon-plus"></i></a>
                            <a class="btn btn-info btn-mini" href="#" title="Move card"
                               onclick="moveCard(${context.id}, '${cardSection.replace(' > ', '> ')}');
                               return false;"><i class="icon-move"></i></a>
                            <g:if test="${context.contextItems.size() == 0}">

                                <a class="btn btn-info btn-mini" href="#" title="Delete card"
                                   onclick="deleteCard(${context.id});
                                   return false;"><i class="icon-trash"></i></a>
                            </g:if>
                        </g:if>
                        <g:else>
                            <g:link class="btn btn btn-info btn-mini" title="Add item" controller="contextItem"
                                    action="create"
                                    params="${[contextId: context?.id, contextClass: context?.class?.simpleName, contextOwnerId: context?.owner?.id]}"><i
                                    class="icon-plus"></i></g:link>
                        </g:else>
                    </div>
                </div>
            </div>
        </caption>
        <tbody>
        <g:each in="${context.contextItems}" status="i" var="contextItem">
            <tr id="${contextItem.id}" class='context_item_row'>
                <td>
                    <g:if test="${contextItem.hasProperty("attributeType")}">
                        <g:if test="${contextItem.attributeType == AttributeType.List || contextItem.attributeType == AttributeType.Free || contextItem.attributeType == AttributeType.Range}">
                            <a title="The value for ${contextItem.attributeElement?.label} will be specified as part of the experiment"><i
                                class="icon-share"></i>
                        </g:if>
                    </g:if>
                </td>
                <td class="attributeLabel">${contextItem.attributeElement?.label}</td>
                <td class="valuedLabel">${contextItem.valueDisplay}</td>


                <td class="btn-toolbar" style="text-align:right">
                    <div class="btn-group">
                        <g:if test="${context instanceof bard.db.registration.AssayContext}">
                            <g:if test="${contextItem.valueNum}">
                                <a class="btn btn-mini" href="#" title="Edit"
                                   onclick="editCardItem(${contextItem.id}, ${context.id});
                                   return false;"><i class="icon-pencil"></i></a>
                            </g:if>
                            <a class="btn btn-mini" href="#" title="Move"
                               onclick="moveCardItem(${contextOwner.id}, ${contextItem.id});
                               return false;"><i class="icon-move"></i></a>
                            <g:if test="${AssayContextItem.canDeleteContextItem(contextItem)}">
                                <a class="btn btn-mini" href="#" title="Delete"
                                   onclick="deleteCardItem(${contextItem.id}, ${context.id});
                                   return false;"><i class="icon-trash"></i></a>
                            </g:if>
                        </g:if>
                        <g:else>
                            <g:form controller="contextItem">
                                <g:hiddenField name="contextItemId" value="${contextItem.id}"/>
                                <g:hiddenField name="contextId" value="${context?.id}"/>
                                <g:hiddenField name="contextClass" value="${context?.class?.simpleName}"/>
                                <g:hiddenField name="contextOwnerId" value="${context?.owner?.id}"/>

                                <button type="submit" title="Delete" name="_action_delete" class="btn btn-mini"
                                        onclick="return confirm('Are you sure you wish to delete this item?');"><i
                                        class="icon-trash"></i></button>
                                <button type="submit" title="Edit" name="_action_edit" class="btn btn-mini"><i
                                        class="icon-pencil"></i></button>
                            </g:form>
                        </g:else>
                    </div>
                </td>

            </tr>
        </g:each>
        </tbody>
    </table>
</div>


