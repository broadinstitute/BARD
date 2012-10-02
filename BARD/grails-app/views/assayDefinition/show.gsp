<%@ page import="bard.db.registration.*" %>
<!DOCTYPE html>
<html>
<head>
    <r:require modules="core,bootstrap"/>
    <meta name="layout" content="basic"/>
    <link rel="stylesheet" href="${resource(dir: 'css', file: 'card.css')}" type="text/css">
    <link rel="stylesheet" href="${resource(dir: 'css', file: 'bootstrap-plus.css')}" type="text/css">
    <title>Assay Definition</title>
    <r:script>
        $(document).ready(function () {
            $("#accordion").accordion({ autoHeight:false});
            $("#dialog:ui-dialog").dialog("destroy");
        });

    </r:script>
</head>

<body>
	<div class="row-fluid">
	    <div class="span12">
	    	<div class="hero-unit-v1">
	        	<h4>Assay View</h4>
	        </div>
	    </div>
	</div>

    <g:if test="${flash.message}">
	    <div class="row-fluid">
		    <div class="span12">
		        <div class="ui-widget">
		            <div class="ui-state-error ui-corner-all" style="margin-top: 20px; padding: 0 .7em;">
		                <p><span class="ui-icon ui-icon-alert" style="float: left; margin-right: .3em;"></span>
		                    <strong>${flash.message}</strong>
		            </div>
		        </div>
		    </div>
	    </div>
    </g:if>

    <g:if test="${assayInstance?.id}">
	    <div class="row-fluid">
	        <div id="accordion" class="span12">
	            <h3><a href="#">Summary for Assay ID: ${assayInstance?.id}</a></h3>
	            <g:render template="assaySummaryView" model="['assayInstance': assayInstance]"/>

	            <h3><a href="#">Assay and Biology Details</a></h3>
	            <g:render template="cardDtoView" model="['cardDtoMap': cardDtoMap, 'assayId': assayInstance.id]"/>

	            <h3><a href="#">Documents</a></h3>
	            <g:render template="assayDocumentsView" model="['assayInstance': assayInstance]"/>

	            <h3><a href="#">Assay Contexts</a></h3>
	            <g:render template="measureContextsView" model="['assayInstance': assayInstance]"/>

	            <h3><a href="#">Measures</a></h3>
	            <g:render template="measuresView" model="['assayInstance': assayInstance]"/>

	            <h3><a href="#">Assay Context Items</a></h3>
	            <g:render template="measureContextItemsView" model="['assayInstance': assayInstance]"/>


	        </div>    <!-- End accordion -->
	    </div>
    </g:if>

<!-- </div> End body div -->

<r:script>
    $(function () {
        $("#addNewBtn").button({
            icons:{
                primary:"ui-icon-plus"
            }
        }).click(function (event) {
            $("#dialog_edit_card").dialog( {title: "Create Card" });
            $("#dialog_edit_card").dialog("open");
        });
        $("#dialog_edit_card").dialog({
                    height:180,
                    width:500,
                    autoOpen:false,
                    modal:true,
                    draggable: false,
                    zIndex: 3999
                });
                $("#dialog_edit_card").dialog("option", "buttons", [
                    	{
        	            	text: "Save",
        	            	class: "btn btn-primary",
        	            	click: function(){
        	            		$("#edit_card_form").submit();
        	                }
                    	},
                    	{
                    		text: "Cancel",
                    		class: "btn",
        	            	click: function(){
        	                    $(this).dialog("close");
                                $("#edit_card_form").clearForm();
                                $("#assayContextId").val('');
                    		}
                    	}
                 ]);

                $("#edit_card_form").ajaxForm({
            		url:'../createOrEditCardName',
            		type:'POST',
            		beforeSubmit:function(formData, jqForm, options){
            			var form = jqForm[0];
            			var nameValue = form.edit_card_name.value;
            			if(!nameValue || 0 === nameValue || (/^\s*$/).test(nameValue)){
            				alert("Name field is required and cannot be empty");
            				return false;
            			}
                        else {
                            $("#dialog_edit_card").dialog("close");
                        }

            		},
            		success:function(responseText, statusText, xhr, jqForm){
                        $("#edit_card_form").clearForm();
                        $("#assayContextId").val('');
            			$("div#cardHolder").html(responseText);
        	            initDnd();
            		}
            	});



        $("#dialog_confirm_delete_card").dialog({
            height:250,
            width:450,
            title:"Delete card?",
            autoOpen:false,
            draggable: false,
            zIndex: 3999,
            modal:true
        });

        $("#dialog_confirm_delete_item").dialog({
            resizable:false,
            height:250,
            width:450,
            modal:true,
            autoOpen:false,
            draggable: false,
            zIndex: 3999,
            title:"Delete item?",
        });
    });
</r:script>
<r:script>

    var initDnd = function () {
        $("caption.assay_context").dblclick(function () {
            var assayContextId = $(this).attr('id');
            var name = $(this).find('p').text();
            $("#edit_card_name").val(name);
            $("#assayContextId").val(assayContextId);
            $("#dialog_edit_card").dialog({title:"Edit Card Name"});
            $("#dialog_edit_card").dialog("open");
        });

    	$("button", ".deleteCardButton").button({
            icons:{
                primary:"ui-icon-trash"
            },
            text: false
        }).click(function(event){
        	var cardId = $(this).attr('id');

        	$("#dialog_confirm_delete_card").dialog("option", "buttons",[
        		{
					text: "Delete",
					class: "btn btn-danger",
					click: function(){
						var data = {'assay_context_id':cardId };
						$.ajax({
	                        type:'POST',
	                        url:'../deleteEmptyCard',
	                        data:data,
	                        success:function (data) {
	                            $("div#cardHolder").html(data);
	                            initDnd();
	                        }
                    	});
                    	$( this ).dialog( "close" );
					}
				},
				{
					text: "Cancel",
					class: "btn",
					click: function(){
						$( this ).dialog( "close" );
					}
				}
        	]);

        	$("#dialog_confirm_delete_card").dialog("open");

        });

    	$("button", ".deleteItemButton").button({
             icons:{
                primary:"ui-icon-trash"
            },
            text: false
        }).click(function (event) {
        		var itemId = $(this).attr('id');
        		var assayContextId =$(this).parents("div.span4").attr('id');
        		$("#dialog_confirm_delete_item").dialog("option", "buttons",[
				{
					text: "Delete",
					class: "btn btn-danger",
					click: function(){
						//alert("You clicked delete item with id: " + itemId + " and assayContext: " + assayContextId);
						var data = {'assay_context_item_id':itemId };
						$.ajax({
	                        type:'POST',
	                        url:'../deleteItemFromCard',
	                        data:data,
	                        success:function (data) {
//	                        	alert("div#" + assayContextId);
//	                        	alert(data);
	                            $("div#" + assayContextId).replaceWith(data);
	                            initDnd();
	                        }
                    	});
                    	$( this ).dialog( "close" );
					}
				},
				{
					text: "Cancel",
					class: "btn",
					click: function(){
						$( this ).dialog( "close" );
					}
				}
		]);

		$("#dialog_confirm_delete_item").dialog("open");

        });


        $(document).ready(function () {

            $(".attributeLabel").draggable({
                cursor:'move',
                scroll:true,
                revert:true,
                appendTo:'body',
                helper:function (event) {
                    return $('<div class="cardView"><table style="width:33%; z-index:3999;" class="gridtable"></table></div>').find('table').append($(event.target).closest('tr').clone());
                }
            });
        });
        $(document).ready(function () {
            $("tr.context_item_row").droppable({
                hoverClass:"drophover",
                drop:function (event, ui) {
                    var src_assay_context_item_id = ui.draggable.closest('tr').attr('id');
                    var target_assay_context_item_id = $(this).attr('id');
                    var data = {'src_assay_context_item_id':src_assay_context_item_id,
                        'target_assay_context_item_id':target_assay_context_item_id};
                    $.ajax({
                        type:'POST',
                        url:'../addItemToCardAfterItem',
                        data:data,
                        success:function (data) {
                            $("div#cardHolder").html(data);
                            initDnd();
                        }
                    });
                    ui.helper.remove();
                }
            });
        });
        $(document).ready(function () {
            $("div.span4").droppable({
                hoverClass:"drophoverCard",
                drop:function (event, ui) {
                    var src_assay_context_item_id = ui.draggable.attr('id');
                    var target_assay_context_id = $(this).find('caption').attr('id');
                    var data = {'src_assay_context_item_id':src_assay_context_item_id,
                        'target_assay_context_id':target_assay_context_id};
                    $.ajax({
                        type:'POST',
                        url:'../addItemToCard',
                        data:data,
                        success:function (data) {
                        	/*alert("div.span4 addItemToCard  " + data);*/
                            $("div#cardHolder").html(data);
                            initDnd();
                        }
                    });
                    ui.helper.remove();
                }
            });
        });
    };
    $(document).ready(function () {
        initDnd();
    });
</r:script>
</body>
</html>