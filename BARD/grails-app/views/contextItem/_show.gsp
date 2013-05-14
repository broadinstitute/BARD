<%@ page import="bard.db.registration.AttributeType" %>
<%--
  Created by IntelliJ IDEA.
  User: xiaorong
  Date: 12/5/12
  Time: 5:34 PM
  To change this template use File | Settings | File Templates.
--%>


<%-- A template for showing summary for both project and assay def, show as card --%>
<div id="card-${context.id}" class="card roundedBorder card-table-container">
    <table class="table table-hover">
        <caption id="${context.id}" class="assay_context">
            <div class="cardTitle">
                <p>${context.preferredName}</p>
                <g:if test="${context.hasProperty('assayContextMeasures') && context.assayContextMeasures}">
                    <p>Measure<g:if test="${context.assayContextMeasures.size() > 1}">s</g:if>:
                        <g:each in="${context.assayContextMeasures}" status="i" var="assayContextMeasure">
                            <a href="#measures-header" class="treeNode" id="${assayContextMeasure.measure.id}">${assayContextMeasure.measure.displayLabel}<g:if test="${i < context.assayContextMeasures.size() - 1}">,  </g:if></a>
                        </g:each>
                    </p>
                </g:if>
            </div>
        </caption>
        <tbody>
        <g:each in="${context.contextItems}" status="i" var="contextItem">
            <tr id="${contextItem.id}" class="context_item_row ${highlightedItemId==contextItem.id?'warning':''}">
                <td>
                    <g:if test="${contextItem.hasProperty("attributeType")}">
                        <g:if test="${contextItem.attributeType == AttributeType.List || contextItem.attributeType == AttributeType.Free || contextItem.attributeType == AttributeType.Range }">
                            <a title="The value for ${contextItem.attributeElement?.label} will be specified as part of the experiment"><i class="icon-share"></i>
                        </g:if>
                    </g:if>
                </td>
                <td class="attributeLabel">${contextItem.attributeElement?.label}</td>
                <g:if test="${ contextItem.attributeElement?.externalURL }">
                	<td class="valuedLabel">
                		<a href="${contextItem.attributeElement.externalURL + contextItem.extValueId}" target="_blank">${contextItem.valueDisplay}</a>
                	</td>
                </g:if>
                <g:else>
                	<td class="valuedLabel">${contextItem.valueDisplay}</td>
                </g:else>                
            </tr>
        </g:each>
        </tbody>
    </table>
</div>

