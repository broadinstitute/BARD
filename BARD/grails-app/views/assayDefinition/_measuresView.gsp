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

<r:require module="dynatree"/>
<div>
    <g:if test="${editable == 'canedit'}">
        <div class="row-fluid">
            <div class="span12">
                <g:if test="${measures}">
                    <g:link action="editMeasure" id="${assayInstance?.id}"
                            class="btn">Edit Measures</g:link>
                </g:if>
                <g:else>
                    <g:link action="editMeasure" id="${assayInstance?.id}"
                            class="btn">Add Measures</g:link>
                </g:else>
            </div>
            <br/>
            <br/>
        </div>
    </g:if>
    <g:if test="${measures}">
        <div class="row-fluid">
            <div class="span6">
                <div id="measure-tree"></div>

                <r:script>
                $("#measure-tree").dynatree({
                 onActivate: function(node) {
                   $(".measure-detail-card").hide();
                   $("#measure-details-"+node.data.key).show();
                 },
                 children: <%=measureTreeAsJson%>
                    });
                </r:script>
            </div>

            <div class="span6">

                <div id="measure-details-none" class="measure-detail-card">
                    <strong>Select a measure on the tree to the left and the details about that measure will appear here.</strong>
                </div>
            <%-- Statically render every measurement details as not displayed.  Selecting nodes in tree will display one card --%>
                <g:each in="${measures}" var="measure">
                    <div id="measure-details-${measure.id}" class="measure-detail-card" style="display: none">
                        <h4>${measure.getDisplayLabel()}</h4>

                        <p><strong>Definition:</strong> ${measure.resultType?.description}</p>
                        <g:if test="${measure.assayContextMeasures.empty}">
                            <p>No assay contexts associated with this measure</p>
                        </g:if>
                        <g:else>
                            <h5>Assay Contexts quick view:</h5>
                            <g:each in="${measure.assayContextMeasures}" var="assayContextMeasure">
                                <g:set var="context" value="${assayContextMeasure.assayContext}"/>
                                <a href="#card-${context.id}">Jump to the full ${context.preferredName} context above</a>

                                <div class="card roundedBorder card-table-container">
                                    <table class="table table-hover">
                                        <caption id="${context.id}" class="assay_context">
                                            <div class="cardTitle">
                                                <p>${context.preferredName}</p>
                                            </div>
                                        </caption>
                                        <tbody>
                                        <g:each in="${context.contextItems}" status="i" var="contextItem">
                                            <tr id="${contextItem.id}" class='context_item_row'>
                                                <td class="attributeLabel">${contextItem.attributeElement?.label}</td>
                                                <td class="valuedLabel">${contextItem.valueDisplay}</td>
                                            </tr>
                                        </g:each>
                                        </tbody>
                                    </table>
                                </div>
                            </g:each>
                        </g:else>
                    </div>

                </g:each>
            </div>
        </div>

    </g:if>
    <g:else>
        <span>No Measures found</span>
    </g:else>
</div>
