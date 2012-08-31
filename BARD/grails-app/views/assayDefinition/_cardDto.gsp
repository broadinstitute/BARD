<div class="card">
	<table class="gridtable assay_context">
    	<caption>
    		${card.title}
    		<button id="editBtn_${cardId}">Edit</button>
    	</caption>
        <tbody>
       		<g:each in="${card.lines}" status="i" var="line">
            	<g:set var="itemId" value="${itemId + 1}" />
            	<tr id="${line.id}" class='context_item_row'>
                	<td class="attributeLabel">${line.attributeLabel}-[${line.id}]</td>
                    <td class="valuedLabel">${line.valueLabel}</td>
                    <td><button id="deleteBtn_${itemId}">Delete</button></td>
           		</tr>
    		</g:each>
		</tbody>
	</table>
</div>