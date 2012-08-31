<div id="card-${card.id}" class="card">
<table class="gridtable">
    <caption id="${card.id}" class="assay_context">
        ${card.title} - [${card.id}]
        <button id="editBtn_${cardId}">Edit</button>
    </caption>
    <tbody>
    <g:each in="${card.lines}" status="i" var="line">
        <tr id="${line.id}" class='context_item_row'>
            <td class="attributeLabel">${line.attributeLabel}-[${line.id}]</td>
            <td class="valuedLabel">${line.valueLabel}</td>
            <td><button id="deleteBtn">Delete</button></td>
        </tr>
    </g:each>
    </tbody>
</table>
</div>