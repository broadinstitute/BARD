%{-- Copyright (c) 2014, The Broad Institute
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
    * Redistributions of source code must retain the above copyright
      notice, this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright
      notice, this list of conditions and the following disclaimer in the
      documentation and/or other materials provided with the distribution.
    * Neither the name of The Broad Institute nor the
      names of its contributors may be used to endorse or promote products
      derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL The Broad Institute BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 --}%

<%@ page import="bard.db.registration.Assay; bard.db.registration.AttributeType; bard.db.context.item.ContextItemController; bard.db.registration.AssayContextItem; bard.db.registration.AttributeType; bard.db.context.item.ContextItemController" %>
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
            <span class="cardTitle">
                %{--We will put error messages on this card--}%
                <div id="${context.id}_Errors"></div>
                <g:if test="${!disableHeaderEdits}">
                    <g:textInPlaceEdit bean="${context}" field="preferredName" id="updatePreferredName-${context.id}"
                                       controller="contextItem" params="${[contextClass: context.simpleClassName]}"/>
                </g:if>
                <g:else>
                    <strong>${context.preferredName}</strong>
                </g:else>
            </span>
            <g:if test="${!disableHeaderEdits}">
                <span class="btn-group pull-right">
                    <g:link class="btn btn btn-info btn-mini" title="Add item" controller="contextItem"
                            action="create"
                            params="${[contextId: context?.id, contextClass: context?.class?.simpleName, contextOwnerId: context?.owner?.id]}"><i
                            class="icon-plus"></i></g:link>
                    <g:if test="${context.contextItems.size() == 0}">
                        <g:link class="btn btn btn-info btn-mini" title="Delete Card" controller="context"
                                action="deleteEmptyCard"
                                params="[contextClass: context?.class?.simpleName, contextId: context?.id, section: cardSection]"><i
                                class="icon-trash"></i></g:link>
                    </g:if>
                </span>
            </g:if>
        </caption>
        <tbody>
        <g:each in="${context.contextItems}" status="i" var="contextItem">
            <tr id="${contextItem.id}" class="context_item_row ${highlightedItemId==contextItem.id?'warning':''} ${(contextItem.validate())?'':'validation-failed'}">
                <td>
                    <g:if test="${contextItem.hasProperty("attributeType")}">
                        <g:if test="${contextItem.attributeType == AttributeType.List || contextItem.attributeType == AttributeType.Free || contextItem.attributeType == AttributeType.Range}">
                            <a title="The value for ${contextItem.attributeElement?.label} will be specified as part of the experiment"><i
                                class="icon-share"></i>
                        </g:if>
                    </g:if>
                </td>
                <td class="attributeLabel">${contextItem.attributeElement?.label} </td>
                <td class="valuedLabel"><g:renderContextItemValueDisplay contextItem="${contextItem}"/></td>


                <td class="btn-toolbar" style="text-align:right">
                    <div class="btn-group">
                        <g:set var="contextItemParams"
                               value="${[contextItemId: contextItem.id, contextId: context?.id, contextClass: context?.simpleClassName, contextOwnerId: context?.owner?.id]}"></g:set>
                        <g:if test="${!(contextItem instanceof AssayContextItem) || AssayContextItem.canDeleteContextItem(contextItem)}">
                            <g:form class="no-padding" controller="contextItem" action="delete"
                                    params="${contextItemParams}" method="POST">
                                <button type="submit" title="Delete" class="btn btn-mini"
                                        onclick="return confirm('Are you sure you wish to delete this item?');"><i
                                        class="icon-trash"></i></button>
                            </g:form>
                        </g:if>
                        <g:link controller="contextItem" action="edit" params="${contextItemParams}"
                                class="btn btn-mini"><i class="icon-pencil"></i></g:link>
                    </div>
                </td>

            </tr>
        </g:each>
        </tbody>
    </table>
    <g:render template="/common/guidance" model="[guidanceList: context.guidance, editable: 'canedit']" />
</div>


