var redraw;

/* only do all this when document has finished loading (needed for RaphaelJS) */
window.onload = function () {
    initFunction();
};

function initFunction(){
    var kelly = [  "#0000FF", "#FF0000", "#00FF00", "#FFFF00", "#FF00FF",
        "#FF8080", "#808080", "#FFB300", "#803E75", "#FF6800",
        "#A6BDD7", "#C10020", "#CEA262",
        "#817066", "#007D34", "#F6768E",
        "#00538A", "#FF7A5C", "#53377A",
        "#FF8E00", "#B32851", "#F4C800",
        "#7F180D", "#93AA00", "#593315",
        "#F13A13", "#232C16" ];
    var countUsedColor=0;
//    var boynton = [ "Blue", "Red", "Green", "Yellow", "Magenta",
//        "Pink", "Gray", "Brown", "Orange" ];
    //using this random color generator after we used up all colors above
    var color1 = new RColor;

    var existingColors = new Array();
    var aidColor = {};
    var template = Handlebars.compile($("#node-selection-template").html())

    var g = new Graph();
    var gIsolated = new Graph();

    var graphInJSON = $.parseJSON($('#stepGraph').html());

    var connectedNodes = graphInJSON.connectedNodes;
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
                var params = {selected: n, projectId: projectId}
                $('#node-selection-details').html(template(params))
            }
        );
        return set;
    };

    for (var i = 0; i < connectedNodes.length; i++) {
        var keyValues = connectedNodes[i].keyValues;
        var colorVal = "";
        if (keyValues.assay in aidColor)
            colorVal = aidColor[keyValues.assay];
        else {
            // colorVal = calculateColor(existingColors, keyValues.assay);
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
        g.addNode(connectedNodes[i].id, { label:keyValues.eid + "\n" + keyValues.stage, data: {link: keyValues.eid, assay: keyValues.assay,
            ename: keyValues.ename, inCount: keyValues.incount, outCount: keyValues.outcount, aid: keyValues.aid, assignedcolor: colorVal}, render:render });
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
                var params = {selected: n, projectId: projectId}
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
           // colorVal = calculateColor(existingColors, keyValues.assay);
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
        //colorVal =calculateColor(existingColors, keyValues.assay);
       // colorVal = color1.get(true);
        gIsolated.addNode(isolatedNodes[i].id, { label:keyValues.eid + "\n" + keyValues.stage+"\n"+keyValues.assay+"\n"+colorVal, data: {link: keyValues.eid, assay: keyValues.assay,
            ename: keyValues.ename, aid: keyValues.aid, assignedcolor: colorVal},  render:renderIsolated});
    }

    var edges = graphInJSON.edges;
    for (var i = 0; i < edges.length; i++) {
        g.addEdge(edges[i].from, edges[i].to, {directed:true, stroke:"#aaa", fill:"#56f"});
    }

    /* Use our layout implementation that place nodes with no incoming edges at top, and node with outgoing edges at bottom*/
    var layouter = new Graph.Layout.OrderedLevel(g, nodeid_sort(g));
    /* Use our layout implementation that place isolated nodes ordered by experiment id*/
    //var layouterIsolated = new Graph.Layout.Isolated(gIsolated, nodeid_sort(gIsolated));
    var layouterIsolated = new Graph.Layout.Spring(gIsolated);

    /* draw the graph using the RaphaelJS draw implementation */
    var totalHeight = 600
    var ratio = isolatedNodes.length/(isolatedNodes.length + connectedNodes.length)
    var isolatedHeight = ratio * totalHeight
    var connectedHeight = totalHeight - isolatedHeight
    if (isolatedHeight < 100) {
        isolatedHeight = 100
        connectedHeight = totalHeight - isolatedHeight
    }
    if (connectedHeight < 300 ) {
        connectedHeight = 300
        isolatedHeight =  totalHeight - connectedHeight
    }


    var renderer = new Graph.Renderer.Raphael('canvas', g, 800, connectedHeight);
    var rendererIsolated = new Graph.Renderer.Raphael('canvasIsolated', gIsolated, 800, isolatedHeight);

    redraw = function () {
        layouter.layout();
        renderer.draw();
        layouterIsolated.layout();
        rendererIsolated.draw();
    };
    redraw();
}

function resetAfterClick(){
    $('#nodelink').text("")
    $("#edgesTable > tbody").find("tr:gt(0)").remove();
    $("#edgesTable > tbody").find("tr:eq(0)").remove();
    $('#nodeename').text("")
    $('#nodeassay').text("")
    $('#assaylink').attr("href", "")
}

function calculateColor(existingColors, assayid) {
//    for(var rval = 0; rval < 255; rval += 50){
//    for(var gval = 0; gval < 255; gval += 50){
//    for(var bval = 0; bval < 255; bval += 50){
//        var colorVal = "rgb("+rval+","+gval+","+bval+")";
//        if (!contains(existingColors, colorVal)) {
//            return colorVal;
//        }
//    }}}
//    return "";

    var PHI = (1 + Math.sqrt(5))/2
    var n = assayid * PHI - Math.floor(assayid * PHI)

    var hue = Math.floor(n * 256)

    var colorVal = "hsb("+hue/1000+",0.25,1)";
    return colorVal;

}

function contains(a, obj) {
    for (var i = 0; i < a.length; i++) {
        if (a[i] === obj) {
            return true;
        }
    }
    return false;
}


