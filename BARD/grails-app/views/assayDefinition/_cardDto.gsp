<div id="card-${card.id}" class="card">
<table class="gridtable">
    <caption id="${card.id}" class="assay_context">
        ${card.title} - [${card.id}]
    </caption>
    <tbody>
    <g:each in="${card.lines}" status="i" var="line">
        <tr id="${line.id}" class='context_item_row'>
            <td class="attributeLabel">${line.attributeLabel}-[${line.id}]</td>
            <td class="valuedLabel">${line.valueLabel}</td>
            <td><div class="deleteButton"><button id="${line.id}">Delete</button></div></td>
        </tr>
    </g:each>
    </tbody>
</table>
</div>