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
            helper: function(event) {
                    return $('<div><table width="33%"></table></div>').find('table').append($(event.target).closest('tr').clone());
            }
        });
    });
    $(document).ready(function () {
        $("tr.context_item_row").droppable({
            hoverClass: "drophover",
            drop:function (event, ui) {
                alert('src assay_context_item_id : ' + ui.draggable.attr('id') + ' target assay_context_item_id : ' + $(this).attr('id') );
                ui.helper.remove()
            }
        });
    });
//    $(document).ready(function () {
//        $("tr.context_item_row").droppable({
//            drop: function(event, ui) {
//                    var id = ui.helper.find('tr').attr('id');
//                    var name = ui.helper.find('.attributeLabel').html();
//                    $(this).insertAfter('<tr id="' + id + '"><td>' + name + '</td><td></td></tr>');
//                    // Remove original row from table
//                    var row = $(ui.draggable).closest("tr");
//                    row.fadeOut(function(){ row.remove(); });
//                }
//        });
//    });

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