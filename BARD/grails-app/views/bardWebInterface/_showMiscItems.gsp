<%--
  Created by IntelliJ IDEA.
  User: xiaorong
  Date: 12/5/12
  Time: 5:34 PM
  To change this template use File | Settings | File Templates.
--%>


<%-- A template for showing summary for both project and assay def, show as card --%>
<div id="card-${contextItem.id}" class="card roundedBorder card-table-container">
    <table class="table table-hover">
        <caption id="${contextItem.id}" class="assay_context">
            <div class="cardTitle">
                <p>${contextItem.display}</p>
            </div>
        </caption>
        <tbody>
        <tr id="${contextItem.id}" class='context_item_row'>
            <td class="attributeLabel">${contextItem.key}</td>
            <td class="valuedLabel">
                <g:if test="${contextItem.url}">
                    <a href="${contextItem.url}" target="_blank">${contextItem.value}</a>
                </g:if>
                <g:else>
                    ${contextItem.value}
                </g:else>
            </td>
        </tr>
        </tbody>
    </table>
</div>

