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
                <p>${context.name}</p>
                <g:if test="${!context.relatedMeasures.isEmpty()}">
                    <p>Measure<g:if test="${context.relatedMeasures.size() > 1}">s</g:if>:
                        <g:each in="${context.relatedMeasures}" status="i" var="assayContextMeasure">
                            <a href="#measures-header" class="treeNode" id="${assayContextMeasure.id}">
                                ${assayContextMeasure.comps.first().display}
                                <g:if test="${i < context.relatedMeasures.size() - 1}">,  </g:if></a>
                        </g:each>
                    </p>
                </g:if>
            </div>
        </caption>
        <tbody>
        <g:each in="${context.getContextItems()}" status="i" var="contextItem">
            <tr id="${contextItem.id}" class='context_item_row'>
                <td class="attributeLabel">${contextItem.key}</td>
                <td class="valuedLabel">
                    <g:if test="${contextItem.url}">
                        <a href="${contextItem.url}" target="_blank">${contextItem.display}</a>
                    </g:if>
                    <g:else>
                        ${contextItem.display}
                    </g:else>
                </td>
            </tr>
        </g:each>
        </tbody>
    </table>
</div>

