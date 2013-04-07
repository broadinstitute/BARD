var redraw;

/* only do all this when document has finished loading (needed for RaphaelJS) */
window.onload = function () {
    initFunction();
};

function initFunction() {
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

    var g = new Graph();
    var gIsolated = new Graph();

    var render = function (r, n) {
        var label = r.text(0, 10, n.label);

        //the Raphael set is obligatory, containing all you want to display
        var set = r.set().push(
            //r.rect(-10, -13, 10, 10).attr({"fill":"#fc0", "stroke-width":1/*, r : "9px"*/}))    hsb(" + num + ", 0.75, 1)
            r.circle(-10, -13, 10).attr({"fill":n.data.assignedcolor, "stroke-width":1}))
            .push(label);

        set.click(
            function click() {
                var projectId = $('#projectIdForStep').val();
                resetAfterClick();
                var params = {selected:n, projectId:projectId}
                $('#node-selection-details').html(template(params))
            }
        );
        return set;
    };
    var graphInJSON = $.parseJSON($('#stepGraph').html());
    var connectedNodes = graphInJSON.connectedNodes;

    for (var i = 0; i < connectedNodes.length; i++) {
        var keyValues = connectedNodes[i].keyValues;
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
        g.addNode(connectedNodes[i].id, { label:keyValues.eid + "\n" + keyValues.stage, data:{link:keyValues.eid, assay:keyValues.assay,
            ename:keyValues.ename, inCount:keyValues.incount, outCount:keyValues.outcount, aid:keyValues.aid, assignedcolor:colorVal}, render:render });
    }

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
                var params = {selected:n, projectId:projectId}
                $('#node-selection-details').html(template(params))
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
        gIsolated.addNode(isolatedNodes[i].id, { label:keyValues.eid + "\n" + keyValues.stage, data:{link:keyValues.eid, assay:keyValues.assay,
            ename:keyValues.ename, aid:keyValues.aid, assignedcolor:colorVal}, render:renderIsolated});
    }

    var edges = graphInJSON.edges;
    for (var i = 0; i < edges.length; i++) {
        g.addEdge(edges[i].from, edges[i].to, {directed:true, stroke:"#aaa", fill:"#56f"});
    }

    /* Use our layout implementation that place nodes with no incoming edges at top, and node with outgoing edges at bottom*/
    // var layouter = new Graph.Layout.OrderedLevel(g, nodeid_sort(g));
   // var layouter = new Graph.Layout.Spring(g);
    /* Use our layout implementation that place isolated nodes ordered by experiment id*/
    var layouterIsolated = new Graph.Layout.Isolated(gIsolated, nodeid_sort(gIsolated));
    //var layouterIsolated = new Graph.Layout.Spring(gIsolated);

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


    // var renderer = new Graph.Renderer.Raphael('canvas', g, 800, connectedHeight);
    var rendererIsolated = new Graph.Renderer.Raphael('canvasIsolated', gIsolated, 800, isolatedHeight);

    redraw = function () {
//        layouter.layout();
//        renderer.draw();
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

    var textgraph = "digraph {graph[fontname=\"Helvetica-Oblique\",fontsize=18,size = \"8,6\"];node[shape=polygon,sides=4,fontsize=8];";
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

function initFunction1() {
    $("#canvas").append(generatesvg());
    var graphInJSON = $.parseJSON($('#stepGraph').html());
    var connectedNodes = graphInJSON.connectedNodes;
    var template = Handlebars.compile($("#node-selection-template1").html())
    $(".node").click(function () {
        var clickedNode = $(this).find('title').text();

        var projectId = $('#projectIdForStep').val();

        for (var i = 0; i < connectedNodes.length; i++) {
            var keyValues = connectedNodes[i].keyValues;
            if (connectedNodes[i].id == clickedNode) {
                var params = {selected:keyValues, projectId:projectId};
                $('#node-selection-details').html(template(params));
            }
        }
    });
    var template1 = Handlebars.compile($("#edge-selection-template").html());
    var edges = graphInJSON.edges;
    $(".edge").click(function () {
        var clickedEdge = $(this).find('title').text();
        for (var i = 0; i < edges.length; i++) {
            var found = edges[i].from + "->" + edges[i].to;
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
                var params = {fromNode:from, toNode:to};
                $('#edge-selection-details').html(template1(params));
            }
        }
    });
}
