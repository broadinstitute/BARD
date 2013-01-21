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

    $( "#addExperimentToProject" )
        .button()
        .click(function() {
            clearAvailableExperiment()
            $( "#dialog_add_experiment_step" ).dialog( "open" );
        });

    $( "#dialog_add_experiment_step" ).dialog({
        autoOpen: false,
        height: 400,
        width: 500,
        modal: true,
        buttons: {
            "Add Experiment": function() {
                var projectId = $("#projectIdForStep").val();
                var selectedExperiments = [];
                $("#selectedExperiments option:selected").each(function(i, selected){
                    selectedExperiments[i] = $(selected).val();
                });
                var data = {'selectedExperiments':selectedExperiments, 'projectId':projectId};
                $.ajax
                    ({
                        url:"../associateExperimentsToProject",
                        //dataType:'json',
                        data:data,
                        cache:false,
                        success:function(data) {
                            handleSuccess(data)
                        }
                    });
                $( this ).dialog( "close" );

            },
            Cancel: function() {
                $( this ).dialog( "close" );
            }
        },
        close: function() {
        }
    });

    $( "#linkExperiment" )
        .button()
        .click(function() {
            $( "#dialog_link_experiment" ).dialog( "open" );
        });
    $( "#dialog_link_experiment" ).dialog({
        autoOpen: false,
        height: 400,
        width: 500,
        modal: true,
        buttons: {
            "Link Experiment": function() {
                var projectId = $("#projectIdForStep").val();
                var fromExperimentId = $("#fromExperimentId").val();
                var toExperimentId = $("#toExperimentId").val();
                var inputdata = {'fromExperimentId':fromExperimentId, 'toExperimentId':toExperimentId, 'projectId':projectId};
                $.ajax
                    ({
                        url:"../linkExperiment",
                        //dataType:'jsonP',
                        data:inputdata,
                        cache:false,
                        success:function(data) {
                            handleSuccess(data)
                        }
                    });
                $( this ).dialog( "close" );

            },
            Cancel: function() {
                $( this ).dialog( "close" );
            }
        },
        close: function() {
        }
    });

    $( "#addByExperimentId" ).change(function () {
        var experimentId = $(this).val();
        var projectId = $("#projectIdForStep").val();
        var data = {'experimentId':experimentId, 'projectId': projectId};
        $.ajax
            ({
                url:"../ajaxFindAvailableExperimentById",
                dataType:'json',
                data:data,
                cache:false,
                success:function (data) {
                    setAvailableExperiment(data)
                }
            });

    });
    $( "#addByAssayId" ).change(function () {
        var assayId = $(this).val();
        var projectId = $("#projectIdForStep").val();
        var data = {'assayId':assayId, 'projectId': projectId};
        $.ajax
            ({
                url:"../ajaxFindAvailableExperimentByAssayId",
                dataType:'json',
                data:data,
                cache:false,
                success:function (data) {
                    setAvailableExperiment(data)
                }
            });

    });

    $( "#addByExperimentName" ).autocomplete({
        source: function( request, response ) {
            var experimentName = request.term
            var projectId = $("#projectIdForStep").val();
            var data = {'experimentName':experimentName, 'projectId': projectId};
            $.ajax({
                url:"../ajaxFindAvailableExperimentByName",
                dataType:'json',
                data: data,
                success: function( data ) {
                    response(data);
                },
                minLength: 3
            });
        },
        select: function( event, ui ) {
            setAvailableExperiment(ui.item.label)
        }
    });

    function setAvailableExperiment(data) {
        for (var i = 0; i < data.length; i++) {
            $("#selectedExperiments").append("<option value='" + data + "'>" + data + "</option>");
        }
    }

    function clearAvailableExperiment() {
        $("#selectedExperiments option:ge(0)").remove()
    }

    $("input:radio[name=addExperimentBy]").change(function(){
        var selected = $(this).val();
        var allValues = ['addByExperimentId', 'addByAssayId', 'addByExperimentName']
        findExperimentInput(selected, allValues);
    });

});

function findExperimentInput(selected, allValues){
    for (var i = 0; i < allValues.length; i++) {
        $('#'+allValues[i]).text("")
        if(selected === allValues[i]) {
            $('#'+selected).show();
        }
        else{
            $('#'+allValues[i]).hide();
        }
    }
}

function handleSuccess(data){
    if (data.substring(0, 12) === "serviceError") {
        $("#serviceResponse").css("color","red")
        $("#serviceResponse").text(data)
    }
    else {
        $("#serviceResponse").css("color","green")
        $("#serviceResponse").text("Success! Reload the page to view changes.")
    }
    $("#serviceResponse").show()
}

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
                    success:function(data) {
                        handleSuccess(data)
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
                    success:function(data) {
                        handleSuccess(data)
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