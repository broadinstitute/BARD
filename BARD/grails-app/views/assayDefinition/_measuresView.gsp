<r:require module="dynatree"/>
<div>
    <g:if test="${assayInstance?.measures}">
        <div class="row-fluid">
            <div class="span6">
                <g:dynaTree id="measure-tree" measures="${assayInstance.rootMeasures}"/>
            </div>

            <div class="span6">
                <div id="measure-details-none" class="measure-detail-card">
                    <strong>Select a measure on the tree to the left and the details about that measure will appear here.</strong>
                </div>
                <%-- Statically render every measurement details as not displayed.  Selecting nodes in tree will display one card --%>
                <g:each in="${assayInstance.measures}" var="measure">
                    <div id="measure-details-${measure.id}" class="measure-detail-card" style="display: none">
                    <h1>${measure.resultType?.label} ${measure?.statsModifier?.label}</h1>
                    <p><strong>Definition:</strong> ${measure.resultType?.description}</p>
                    <g:if test="${measure.assayContextMeasures.empty}">
                        <p>No assay contexts associated with this measure</p>
                    </g:if>
                    <g:else>
                        <h3>Assay Contexts quick view:</h3>
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