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
    initProjectFunction();
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

function initProjectFunction() {
    // a list of most distinguishable color
    var kelly = [  "#0000FF", "#FF0000", "#00FF00", "#FFFF00", "#FF00FF",
        "#FF8080", "#808080", "#FFB300", "#803E75", "#FF6800",
        "#A6BDD7", "#C10020", "#CEA262",
        "#817066", "#007D34", "#F6768E",
        "#00538A", "#FF7A5C", "#53377A",
        "#FF8E00", "#B32851", "#F4C800",
        "#7F180D", "#93AA00", "#593315",
        "#F13A13", "#232C16" ];
    var countUsedColor = 0;
    // using this random color generator after we used up all colors above, they may not distinguishable or even identical with some of them above
    // but I am hoping in most cases, number of distinct assays won't exceed number of colors above
    var color1 = new RColor;

    var existingColors = new Array();
    var aidColor = {};
    var template = Handlebars.compile($("#node-selection-template").html())

    var gIsolated = new Graph();

    var graphInJSON = $.parseJSON($('#stepGraph').html());
    var connectedNodes = graphInJSON.connectedNodes;

    var isolatedNodes = graphInJSON.isolatedNodes;
    var renderIsolated = function (r, n) {
        var label = r.text(0, 10, n.label);
        //the Raphael set is obligatory, containing all you want to display  #fc0
        var set = r.set().push(
            r.circle(-10, -13, 10).attr({"fill":n.data.assignedcolor, "stroke-width":1}))
            .push(label);
        set.click(
            function click() {
                var projectId = $('#projectIdForStep').val();
                resetAfterClick();
                var params = {selected:n.data, projectId:projectId}
                $('#selection-details').html(template(params))
            }
        );
        return set;
    };

    for (var i = 0; i < isolatedNodes.length; i++) {
        var keyValues = isolatedNodes[i].keyValues;
        var colorVal = "";
        if (keyValues.assay in aidColor)
            colorVal = aidColor[keyValues.assay];
        else {
            if (countUsedColor < kelly.length) {
                colorVal = kelly[countUsedColor];
                countUsedColor++;
            }
            else {
                colorVal = color1.get(true);
            }
            existingColors.push(colorVal);
            aidColor[keyValues.assay] = colorVal;
        }
        gIsolated.addNode(isolatedNodes[i].id, { label:keyValues.eid + "\n" + keyValues.stage, data:{eid:keyValues.eid, stage:keyValues.stage, assay:keyValues.assay,
            ename:keyValues.ename, aid:keyValues.aid, assignedcolor:colorVal}, render:renderIsolated});
    }

    /* Use our layout implementation that place isolated nodes ordered by experiment id*/
    var layouterIsolated = new Graph.Layout.Isolated(gIsolated, nodeid_sort(gIsolated));

    /* draw the graph using the RaphaelJS draw implementation */
    var totalHeight = 600
    var ratio = isolatedNodes.length / (isolatedNodes.length + connectedNodes.length)
    var isolatedHeight = ratio * totalHeight
    var connectedHeight = totalHeight - isolatedHeight
    if (isolatedHeight < 100) {
        isolatedHeight = 100
        connectedHeight = totalHeight - isolatedHeight
    }
    if (connectedHeight < 300) {
        connectedHeight = 300
        isolatedHeight = totalHeight - connectedHeight
    }

    var rendererIsolated = new Graph.Renderer.Raphael('canvasIsolated', gIsolated, 800, isolatedHeight);

    redraw = function () {
        layouterIsolated.layout();
        rendererIsolated.draw();
        initFunction1();
    };
    redraw();
}

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

    var textgraph = "digraph {graph[fontname=\"Helvetica-Oblique\",fontsize=18];node[shape=polygon,sides=4,fontsize=8,style=\"filled\",fillcolor=\"white\"];";
    for (var i = 0; i < connectedNodes.length; i++) {
        var keyValues = connectedNodes[i].keyValues;
        textgraph = textgraph + connectedNodes[i].id + "[color=salmon2,label=\"" + keyValues.eid + " " + keyValues.stage + "\"" + "];";
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

function initFunction1() {
    $("#canvas").append(generatesvg());
    var graphtitle = $("g title")[0];
    $(".graph").find(graphtitle).text("experiment relationships");
    var graphInJSON = $.parseJSON($('#stepGraph').html());
    var connectedNodes = graphInJSON.connectedNodes;
    var template = Handlebars.compile($("#node-selection-template").html())
    $(".node").click(function () {

        var prevSelectedEdge = $("#selectedEdgeId").text();
        assignEdgeColor(prevSelectedEdge, "black");
        var clickedNode = $(this).find('title').text();
        var thisId = $(this).attr("id");

        var thisPolygon = "#"+thisId + " polygon";
        $(thisPolygon).attr("fill", "#00FFFF")
        var prevSelectedNode = $("#selectedNodeId").text();

        var projectId = $('#projectIdForStep').val();

        for (var i = 0; i < connectedNodes.length; i++) {
            var keyValues = connectedNodes[i].keyValues;
            if (connectedNodes[i].id == clickedNode) {
                assignFillColor(prevSelectedNode, "white");
                assignFillColor(thisId, "#00FFFF");
                var params = {selected:keyValues, projectId:projectId, selectedNodeId:thisId};
                $('#selection-details').html(template(params));
            }
        }
        $('.projectStageId').editable({
            success: function (response, newValue) {
                refreshProjectSteps();
            }
        });
    });
    var template1 = Handlebars.compile($("#edge-selection-template").html());
    var edges = graphInJSON.edges;
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
                $('#selection-details').html(template1(params));
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
