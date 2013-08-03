<div class="card roundedBorder card-table-container">
    <table class="table table-hover">
        <caption class="assay_context">
            <div class="cardTitle">
                <p>${contextName}</p>
            </div>
        </caption>
        <tbody>
        <g:each in="${contextItemDTOs}" status="i" var="contextItemDTO">
            <tr id="${contextItemDTO.contextItemId}" class="context_item_row">
                <td class="attributeLabel">${contextItemDTO.label}</td>
                <td class="valuedLabel">${contextItemDTO.valueDisplay}</td>
            </tr>
        </g:each>
        </tbody>
    </table>
</div>
