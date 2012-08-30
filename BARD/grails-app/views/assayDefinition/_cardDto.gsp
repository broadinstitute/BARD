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
    $(document).ready(function() {
        $(".context_item_row").draggable({
            cursor: 'move',
            scroll: true,
            revert: true,
            appendTo: 'body',
            helper: function(event) {
                    return $('<div class="cardView"><table class="gridtable"></table></div>').find('table').append($(event.target).closest('tr').clone());
            }
        });
    });
    $(document).ready(function () {
        $("tr.context_item_row").droppable({
            hoverClass: "drophover",
            drop:function (event, ui) {
                var src_assay_context_item_id = ui.draggable.attr('id');
                var target_assay_context_item_id = $(this).attr('id');
                var data = {'src_assay_context_item_id' : src_assay_context_item_id,
                            'target_assay_context_item_id': target_assay_context_item_id};
                alert(data.src_assay_context_item_id + " : " + data.target_assay_context_item_id);
                $.ajax({
                  type: 'POST',
                  url: '../updateCardItems',
                  data: data,
                  success: function(data){
                      $("div.cardView").html(data)
                  }
                });
                ui.helper.remove()

            }
        });
    });


</r:script>

<div class="card">
	<table class="gridtable assay_context">
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
					}).click(function(event){
			     		$("#dialog_confirm_delete_card").dialog("open");
			   		});
            	</r:script>
            	<tr id="${line.id}" class='context_item_row'>
                	<td class="attributeLabel">${line.attributeLabel}-[${line.id}]</td>
                    <td class="valuedLabel">${line.valueLabel}</td>
                    <td><button id="deleteBtn_${itemId}">Delete</button></td>
           		</tr>
    		</g:each>
		</tbody>
	</table>
</div>