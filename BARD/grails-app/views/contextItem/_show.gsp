<%--
  Created by IntelliJ IDEA.
  User: xiaorong
  Date: 12/5/12
  Time: 5:34 PM
  To change this template use File | Settings | File Templates.
--%>


<%-- A template for showing summary for both project and assay def, show as card --%>
<div id="card-${context.id}" class="span6 card roundedBorder card-table-container">
    <table class="table table-hover">
        <caption id="${context.id}" class="assay_context">
            <div class="cardTitle">
                 <p>${context.preferredName}</p>
                <g:if test="${context.assayContextMeasures}">
                    <g:each in="${context.assayContextMeasures}" var="assayContextMeasure" >
                        <p>${assayContextMeasure.measure.resultType.label}</p>
                    </g:each>
                </g:if>
                <p></p>
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

