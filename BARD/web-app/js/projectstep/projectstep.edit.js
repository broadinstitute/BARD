$(document).ready(function () {
    $("#dialog_confirm_delete_item").dialog({
        resizable:false,
        height:250,
        width:450,
        modal:true,
        autoOpen:false,
        draggable: false,
        zIndex: 3999,
        title:"Delete item?"
    });
});
function deleteItem(experimentId, projectId){
    $("#dialog_confirm_delete_item").dialog("option", "buttons",[
        {
            text: "Delete",
            class: "btn btn-danger",
            click: function(){
                var data = {'experimentId':experimentId, 'projectId': projectId};
                $.ajax({
                    type:'POST',
                    url:'../removeExperimentFromProject',
                    data:data,
                    success:function (data) {
                        //                      $("div#" + assayContextId).replaceWith(data);
                        //                       initDnd();
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
}

function deleteEdge(fromId, toId, projectId){
    $("#dialog_confirm_delete_item").dialog("option", "buttons",[
        {
            text: "Delete",
            class: "btn btn-danger",
            click: function(){
                var data = {'fromExperimentId':fromId, 'toExperimentId': toId, 'projectId': projectId};
                $.ajax({
                    type:'POST',
                    url:'../removeEdgeFromProject',
                    data:data,
                    success:function (data) {
                        //                      $("div#" + assayContextId).replaceWith(data);
                        //                       initDnd();
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
}