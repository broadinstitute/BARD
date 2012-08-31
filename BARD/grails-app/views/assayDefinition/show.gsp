<%@ page import="bard.db.registration.*" %>
<!DOCTYPE html>
<html>
<head>
    <r:require modules="core"/>
    <meta name="layout" content="basic"/>
    <link rel="stylesheet" href="${resource(dir: 'css', file: 'card.css')}" type="text/css">
    <title>Assay Definition</title>
    <r:script>
        $(document).ready(function () {
            $("#accordion").accordion({ autoHeight:false });
            $("#dialog:ui-dialog").dialog("destroy");
        });

        $(document).ready(function () {
            $("#accordion").accordion({ autoHeight:false });
        })
    </r:script>

</head>

<body>

<div>
    <div class="ui-widget"><p>

        <h1>Assay View</h1></p></div>

    <g:if test="${flash.message}">
        <div class="ui-widget">
            <div class="ui-state-error ui-corner-all" style="margin-top: 20px; padding: 0 .7em;">
                <p><span class="ui-icon ui-icon-alert" style="float: left; margin-right: .3em;"></span>
                    <strong>${flash.message}</strong>
            </div>
        </div>
    </g:if>

    <g:if test="${assayInstance?.id}">
        <div id="accordion">

            <h3><a href="#">Summary for Assay ID: ${assayInstance?.id}</a></h3>
            <g:render template="assaySummaryView" model="['assayInstance': assayInstance]"/>

            <h3><a href="#">Assay and Biology Details</a></h3>
            <g:render template="cardDtoView" model="['cardDtoList': cardDtoList]"/>

            <h3><a href="#">Documents</a></h3>
            <g:render template="assayDocumentsView" model="['assayInstance': assayInstance]"/>

            <h3><a href="#">Assay Contexts</a></h3>
            <g:render template="measureContextsView" model="['assayInstance': assayInstance]"/>

            <h3><a href="#">Measures</a></h3>
            <g:render template="measuresView" model="['assayInstance': assayInstance]"/>

            <h3><a href="#">Assay Context Items</a></h3>
            <g:render template="measureContextItemsView" model="['assayInstance': assayInstance]"/>

        </div>    <!-- End accordion -->
    </g:if>
</div><!-- End body div -->

<r:script>
    var idCounter = 0;
    var idItemsCounter = 0;
    $(function () {
        $("#addNewBtn").button({
            icons:{
                primary:"ui-icon-plus"
            }
        }).click(function (event) {
                    $("#dialog_new_card").dialog("open");
                });

        $("#dialog_new_card").dialog({
            height:300,
            width:550,
            title:"New Card",
            autoOpen:false,
            modal:true,
            buttons:{
                "Add new attribute-value pair":function () {
                    $(this).dialog("close");
                },
                Cancel:function () {
                    $(this).dialog("close");
                }
            }
        });

        $("#dialog_card").dialog({
            height:350,
            width:350,
            title:"Edit Card",
            autoOpen:false,
            modal:true
        });

        $("#dialog_confirm_delete_card").dialog({
            resizable:false,
            height:250,
            width:450,
            modal:true,
            autoOpen:false,
            title:"Delete item?",
            buttons:{
                "Delete card":function () {
                    $(this).dialog("close");
                },
                Cancel:function () {
                    $(this).dialog("close");
                }
            }
        });
    });
</r:script>
<r:script>
    $(function () {
        idCounter = idCounter + 1;

        $("#editBtn_" + idCounter).button({
            icons:{
                primary:"ui-icon-pencil"
            }
        }).click(function (event) {
                    $("#dialog_card").dialog("open");
                });


    });
    var initDnd = function () {

        $(document).ready(function () {

            $(".context_item_row").draggable({
                cursor:'move',
                scroll:true,
                revert:true,
                appendTo:'body',
                helper:function (event) {
                    return $('<div class="cardView"><table style="width:33%;" class="gridtable"></table></div>').find('table').append($(event.target).closest('tr').clone());
                }
            });
        });
        $(document).ready(function () {
            $("tr.context_item_row").droppable({
                hoverClass:"drophover",
                drop:function (event, ui) {
                    var src_assay_context_item_id = ui.draggable.attr('id');
                    var target_assay_context_item_id = $(this).attr('id');
                    var data = {'src_assay_context_item_id':src_assay_context_item_id,
                        'target_assay_context_item_id':target_assay_context_item_id};
                    $.ajax({
                        type:'POST',
                        url:'../updateCardItems',
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
            $("caption.assay_context").droppable({
                hoverClass:"drophoverCaption",
                drop:function (event, ui) {
                    var src_assay_context_item_id = ui.draggable.attr('id');
                    var target_assay_context_id = $(this).attr('id');
                    var data = {'src_assay_context_item_id':src_assay_context_item_id,
                        'target_assay_context_id':target_assay_context_id};
                    $.ajax({
                        type:'POST',
                        url:'../updateCardTitle',
                        data:data,
                        success:function (data) {
                            $("div#card-" + target_assay_context_id).replaceWith(data);
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