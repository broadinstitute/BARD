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
            </div>
        </caption>
        <tbody>
        <g:each in="${context.getComps()}" status="i" var="contextItem">
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

