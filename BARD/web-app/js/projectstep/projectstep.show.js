var redraw;

/**
 * This is the js to display graph. During the development process, I have tried using several different js framework to achieve the goal.
 * Currently, isolated nodes are rendered with RaphaelJS library.
 * Connected nodes are rendered with viz framework.
 * I may change to use same framework to display both later based on more feedback of using this.
 */

/* only do all this when document has finished loading (needed for RaphaelJS) */
$(document).ready(function () {
    $.fn.editable.defaults.mode = 'inline';
    layoutGraph();
});

function refreshProjectSteps(){
    var projectId = $("#projectIdForStep").val();
    var inputdata = {'projectId':projectId};
    $.ajax
        ({
            url:"../reloadProjectSteps",
            data:inputdata,
            cache:false,
            success:function(data) {
                handleSuccess(data)
            }
        });
}

var colors = [ "#0000FF", "#FF0000", "#00FF00", "#FFFF00", "#FF00FF",
    "#FF8080", "#808080", "#FFB300", "#803E75", "#FF6800",
    "#A6BDD7", "#C10020", "#CEA262",
    "#817066", "#007D34", "#F6768E",
    "#00538A", "#FF7A5C", "#53377A",
    "#FF8E00", "#B32851", "#F4C800",
    "#7F180D", "#93AA00", "#593315",
    "#F13A13", "#232C16" ];

//var graphInJSON = $.parseJSON($('#stepGraph').html());
//var isolatedNodes = graphInJSON.isolatedNodes;

function resetAfterClick() {
    $('#nodelink').text("")
    $("#edgesTable > tbody").find("tr:gt(0)").remove();
    $("#edgesTable > tbody").find("tr:eq(0)").remove();
    $('#nodeename').text("")
    $('#nodeassay').text("")
    $('#assaylink').attr("href", "")
}

function contains(a, obj) {
    for (var i = 0; i < a.length; i++) {
        if (a[i] === obj) {
            return true;
        }
    }
    return false;
}

function inspect(s) {
    return "<pre>" + s.replace(/</g, "&lt;").replace(/>/g, "&gt;").replace(/\"/g, "&quot;") + "</pre>"
}

function getsrc() {
    var graphInJSON = $.parseJSON($('#stepGraph').html());
    var connectedNodes = graphInJSON.connectedNodes;
    var isolatedNodes = graphInJSON.isolatedNodes;

    var textgraph = "digraph {graph[fontname=\"Helvetica-Oblique\",fontsize=18];node[shape=polygon,sides=4,fontsize=8,style=\"filled\",fillcolor=\"white\"];";

    for (var i = 0; i < connectedNodes.length; i++) {
        var keyValues = connectedNodes[i].keyValues;
        textgraph = textgraph + connectedNodes[i].id + "[color=salmon2,label=\"" + keyValues.eid + " " + keyValues.stage + "\"" + "];";
    }

    // hack: to layout unconnected nodes, pretend there are links between them forcing them into a grid.
    var unconnectedPerRow = 4;
    for (var i = 0; i < isolatedNodes.length; i++) {
        var keyValues = isolatedNodes[i].keyValues;
        textgraph = textgraph + isolatedNodes[i].id + "[color=salmon2,label=\"" + keyValues.eid + " " + keyValues.stage + "\"" + "];";

        if(i >= unconnectedPerRow) {
        var prevIndex = i - unconnectedPerRow;
            textgraph = textgraph + isolatedNodes[prevIndex].id + "->" + isolatedNodes[i].id + "[style=\"invis\"];";
        }
    }

    var edges = graphInJSON.edges;
    for (var i = 0; i < edges.length; i++) {
        textgraph = textgraph + edges[i].from + "->" + edges[i].to + ";";
    }

    return textgraph + "}";
}

function generatesvg() {
    var result;
    try {
        result = Viz(getsrc(),"svg");
        return result;
    } catch (e) {
        return inspect(e.toString());
    }
}

function assignFillColor(selectedNodeId, assignedColor) {
    if (!selectedNodeId)
        return;
    var thisPolygon = "#"+selectedNodeId + " polygon";
    $(thisPolygon).attr("fill", assignedColor);
}

function assignEdgeColor(selectedEdgeId, assignedColor) {
    if (!selectedEdgeId)
        return;
    var thisPolygon = "#"+selectedEdgeId + " polygon";
    $(thisPolygon).attr("fill", assignedColor);
    $(thisPolygon).attr("stroke", assignedColor);
    var thisPath = "#"+selectedEdgeId + " path";
    $(thisPath).attr("fill", assignedColor);
    $(thisPath).attr("stroke", assignedColor);
}

function deselectPrevious() {
    var prevSelectedEdge = $("#selectedEdgeId").text();
    assignEdgeColor(prevSelectedEdge, "black");

    var prevSelectedNode = $("#selectedNodeId").text();
    assignFillColor(prevSelectedNode, "white");
}

function layoutGraph() {
    // generate SVG graph and insert into page
    $("#canvas").append(generatesvg());

    var graphtitle = $("g title")[0];
    $(".graph").find(graphtitle).text("experiment relationships");
    var graphInJSON = $.parseJSON($('#stepGraph').html());
    var connectedNodes = graphInJSON.connectedNodes;
    var nodeSelectionTemplate = Handlebars.compile($("#node-selection-template").html())
    var edgeSelectionTemplate = Handlebars.compile($("#edge-selection-template").html());

    // set up click handling on nodes
    $(".node").click(function () {
        // reset previous selections
        deselectPrevious();

        var clickedNode = $(this).find('title').text();
        var thisId = $(this).attr("id");

        var thisPolygon = "#"+thisId + " polygon";
        $(thisPolygon).attr("fill", "#00FFFF")

        var projectId = $('#projectIdForStep').val();

        for (var i = 0; i < connectedNodes.length; i++) {
            var keyValues = connectedNodes[i].keyValues;
            if (connectedNodes[i].id == clickedNode) {
                assignFillColor(thisId, "#00FFFF");
                var params = {selected:keyValues, projectId:projectId, selectedNodeId:thisId};
                $('#selection-details').html(nodeSelectionTemplate(params));
            }
        }

        $('.projectStageId').editable({
            success: function (response, newValue) {
                refreshProjectSteps();
            }
        });
    });

    var edges = graphInJSON.edges;
    // set up click handler for edges
    $(".edge").click(function () {
        var prevSelectedNode = $("#selectedNodeId").text();
        assignFillColor(prevSelectedNode, "white");
        var clickedEdge = $(this).find('title').text();

        for (var i = 0; i < edges.length; i++) {
            var found = edges[i].from + "->" + edges[i].to;
            var thisId = $(this).attr("id");
            var prevSelectedEdge = $("#selectedEdgeId").text();
            if (clickedEdge == found) {
                var splitstr = found.split("->");
                var from;
                var to;
                for (var j = 0; j < connectedNodes.length; j++) {
                    var keyValues = connectedNodes[j].keyValues;
                    if (connectedNodes[j].id == splitstr[0]) {
                        from = keyValues.eid;
                    }
                    if (connectedNodes[j].id == splitstr[1]) {
                        to = keyValues.eid;
                    }
                }
                assignEdgeColor(prevSelectedEdge, "black");
                assignEdgeColor(thisId, "#00FFFF");
                var params = {fromNode:from, toNode:to, selectedEdgeId:thisId};
                $('#selection-details').html(edgeSelectionTemplate(params));
            }
        }
    });
}

// http://jqueryfordesigners.com/fixed-floating-elements/
$(function () {
    var msie6 = $.browser == 'msie' && $.browser.version < 7;

    if (!msie6) {
        var top = $('#placeholder').offset().top - parseFloat($('#placeholder').css('margin-top').replace(/auto/, 0));

        $(window).scroll(function (event) {
            // what the y position of the scroll is
            var y = $(this).scrollTop();
            // if inside of the experiment box
            if (y >= top && y <= top + $('#showstep').height()- $('#placeholder').height()) {
                // if so, ad the fixed class
                $('#placeholder').addClass('fixed');
            } else {
                // otherwise remove it
                $('#placeholder').removeClass('fixed');
            }
        });
    }
});
