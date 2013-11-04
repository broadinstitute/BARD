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
                <p>
                    <g:if test="${showCheckBoxes}">
                        <%
                            boolean isChecked = false;
                            if (existingContextIds) {
                                if (existingContextIds.contains(context.id)) {
                                    isChecked = true;

                                }
                            }
                        %>
                        <g:checkBox name="contextIds" value="${context.id}" checked="${isChecked}"/>
                    </g:if>

                    ${context.preferredName}
                </p>
                <g:if test="${context.hasProperty('assayContextExperimentMeasures') && context.assayContextExperimentMeasures && existingContextIds}">
                    <g:set var="counter" value="${1}"/>
                    <g:set var="prevCounter" value="${0}"/>
                    <p>Result Types:

                        <g:each in="${context.assayContextExperimentMeasures}" status="i"
                                var="assayContextExperimentMeasure">
                            <g:if test="${assayContextExperimentMeasure?.experimentMeasure?.experiment.id == experimentId}">

                                <a href="#result-type-header" class="treeNode"
                                   id="${assayContextExperimentMeasure?.experimentMeasure?.id}">
                                    <g:if test="${counter > 1}">,</g:if>
                                    ${assayContextExperimentMeasure?.experimentMeasure?.resultType.label}
                                    <g:if test="${assayContextExperimentMeasure?.experimentMeasure?.statsModifier}">
                                        (${assayContextExperimentMeasure?.experimentMeasure?.statsModifier.label})
                                    </g:if>
                                </a>
                                <g:set var="counter" value="${counter + 1}"/>
                            </g:if>

                        </g:each>
                    </p>
                </g:if>
            </div>
        </caption>
        <tbody>
        <g:each in="${context.contextItems}" status="i" var="contextItem">
            <tr id="${contextItem.id}"
                class="context_item_row ${highlightedItemId == contextItem.id ? 'warning' : ''} ${(contextItem.validate()) ? '' : 'validation-failed'}">
                <td>
                    <g:if test="${contextItem.hasProperty("attributeType")}">
                        <g:if test="${contextItem.attributeType == AttributeType.List || contextItem.attributeType == AttributeType.Free || contextItem.attributeType == AttributeType.Range}">
                            <a title="The value for ${contextItem.attributeElement?.label} will be specified as part of the experiment"><i
                                    class="icon-share"></i></a>
                        </g:if>
                    </g:if>
                </td>
                <td class="attributeLabel">
                    <g:if test="${contextItem.attributeElement}">

                        <g:if test="${contextItem.attributeElement.description}">
                            <span class="dictionary editable-click" title="" data-placement="bottom"
                                  data-toggle="tooltip"
                                  href="#" data-original-title="${contextItem.attributeElement?.description}">
                                ${contextItem.attributeElement?.label}</span>
                        </g:if>
                        <g:else>
                            ${contextItem.attributeElement?.label}
                        </g:else>

                    </g:if>

                </td>
                <g:if test="${contextItem.hasProperty("attributeType") && !contextItem.valueDisplay?.trim() && contextItem.attributeType == AttributeType.Free}">
                    <td class="valuedLabel">
                        <i>Value will be provided with the experiment</i>
                    </td>
                </g:if>
                <g:else>
                    <g:if test="${contextItem.attributeElement?.externalURL}">
                        <td class="valuedLabel">
                            <a href="${contextItem.attributeElement.externalURL + contextItem.extValueId}"
                               target="_blank">${contextItem.valueDisplay}</a>
                        </td>
                    </g:if>
                    <g:else>
                        <td class="valuedLabel">${contextItem.valueDisplay}
                        </td>
                    </g:else>
                </g:else>
            </tr>
        </g:each>
        </tbody>
    </table>
    <g:render template="/common/guidance" model="[guidanceList: context.guidance]"/>
</div>

