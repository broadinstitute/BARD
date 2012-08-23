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

		$("#deleteBtn_" + idCounter).button({
			icons: {
				primary: "ui-icon-trash"
			}
		}).click(function(event){     		
     		$("#dialog_confirm_delete_card").dialog("open");
   		});
	});
</r:script>

<div class="card">
	<table id="cardTable" class="gridtable">
    	<caption>
    		${card.title}
    		<button id="editBtn_${cardId}">Edit</button>
    		<button id="deleteBtn_${cardId}">Delete</button>
    	</caption>
        <tbody>
       		<g:each in="${card.lines}" status="i" var="line">
            	<!-- <tr class="${(i % 2) == 0 ? 'even' : 'odd'}"> -->
            	<tr>
                	<td>${line.attributeLabel}</td>
                    <td>${line.valueLabel}</td>
           		</tr>
    		</g:each>
		</tbody>
	</table>
</div>