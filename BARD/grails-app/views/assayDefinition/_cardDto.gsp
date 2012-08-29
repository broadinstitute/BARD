<r:script>
	$(function(){
		idCounter = idCounter + 1;
				
		$("#editBtn_" + idCounter).button({
			icons: {
				primary: "ui-icon-pencil"
			}
		}).click(function(event){     		
     		$("#dialog_card").dialog("open");
   		});

		
	});
</r:script>

<div class="card">	
	<table id="cardTable" class="gridtable">
    	<caption>
    		${card.title}
    		<button id="editBtn_${cardId}">Edit</button>   		
    	</caption>
        <tbody>
       		<g:each in="${card.lines}" status="i" var="line">
            	<!-- <tr class="${(i % 2) == 0 ? 'even' : 'odd'}"> -->
            	<g:set var="itemId" value="${itemId + 1}" />
            	<r:script>
            		idItemsCounter = idItemsCounter + 1
            		$("#deleteBtn_" + idItemsCounter).button({
						icons: {
							primary: "ui-icon-trash"
						}
					});
            	</r:script>
            	<tr>
                	<td>${line.attributeLabel}</td>
                    <td>${line.valueLabel}</td>
                    <td>
                    	<g:if test="${line.id}">
                    		<g:formRemote name="${line.id}" before="deleteCardItem(${line.id}, deleteBtn_${itemId})" update="assayView" url="[controller: 'assayDefinition', action: 'deleteItem']" action="deleteItem">
		                    	<input type="hidden" name="assayId" value="${card.assayId}"/>
		                    	<input type="hidden" name="assayContextItemId" value="${line.id}"/>
		                    	<button id="deleteBtn_${itemId}">Delete</button>
	                    	</g:formRemote>
                    	</g:if>
						<g:else>
							<button id="deleteBtn_${itemId}">Delete</button>
						</g:else>
	                    
                    </td>
           		</tr>
    		</g:each>
		</tbody>
	</table>
</div>