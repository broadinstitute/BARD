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
                    <td class="valuedLabel"><g:renderContextItemValueDisplay contextItem="${contextItem}"/></td>
                </g:else>
            </tr>
        </g:each>
        </tbody>
    </table>
    <g:render template="/common/guidance" model="[guidanceList: context.guidance]"/>
</div>

