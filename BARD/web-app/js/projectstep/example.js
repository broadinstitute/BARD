var redraw;

/* only do all this when document has finished loading (needed for RaphaelJS) */
window.onload = function () {
    var template = Handlebars.compile($("#node-selection-template").html())

    var g = new Graph();
    var gIsolated = new Graph();

    var graphInJSON = $.parseJSON($('#stepGraph').html());

    var connectedNodes = graphInJSON.connectedNodes;
    var render = function (r, n) {
        var label = r.text(0, 10, n.label);

        //the Raphael set is obligatory, containing all you want to display
        var set = r.set().push(
            //r.rect(-10, -13, 10, 10).attr({"fill":"#fc0", "stroke-width":1/*, r : "9px"*/}))
             r.circle(-10, -13, 10).attr({"fill":"#fc0", "stroke-width":1}))
            .push(label);

        set.click(
            function click() {
                var projectId = $('#projectIdForStep').val();

                resetAfterClick();
//                var edges = n.edges;
//
//                var sources = new Array();
//                var targets = new Array();
//                var myMap = {};
//                for (var i = 0; i < edges.length; i++) {
//                    myMap[edges[i].source.id] = edges[i].target.id;
//                }

                var params = {selected: n, projectId: projectId}
                $('#node-selection-details').html(template(params))

            }
        );
        return set;
    };

    for (var i = 0; i < connectedNodes.length; i++) {
        var keyValues = connectedNodes[i].keyValues;
        g.addNode(connectedNodes[i].id, { label:keyValues.eid + "\n" + keyValues.stage, data: {link: keyValues.eid, assay: keyValues.assay,
            ename: keyValues.ename, inCount: keyValues.incount, outCount: keyValues.outcount}, render:render });
    }

    var isolatedNodes = graphInJSON.isolatedNodes;
    var renderIsolated = function (r, n) {
        var label = r.text(0, 10, n.label);
        //the Raphael set is obligatory, containing all you want to display
        var set = r.set().push(
           // r.rect(-10, -13, 10, 10).attr({"fill":"#fc0", "stroke-width":1/*, r : "9px"*/}))
            r.circle(-10, -13, 10).attr({"fill":"#fc0", "stroke-width":1}))
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
        gIsolated.addNode(isolatedNodes[i].id, { label:keyValues.eid + "\n" + keyValues.stage, data: {link: keyValues.eid, assay: keyValues.assay,
            ename: keyValues.ename}, render:renderIsolated});
    }

    var edges = graphInJSON.edges;
    for (var i = 0; i < edges.length; i++) {
        g.addEdge(edges[i].from, edges[i].to, {directed:true, stroke:"#bfa", fill:"#f00"});
    }
    var layouter = new Graph.Layout.OrderedLevel(g, nodeid_sort(g));

    /* layout the graph using the Spring layout implementation */
    //var layouter = new Graph.Layout.Spring(g);
    /* layout the graph using the Spring layout implementation */
    var layouterIsolated = new Graph.Layout.Isolated(gIsolated, nodeid_sort(gIsolated));

    /* draw the graph using the RaphaelJS draw implementation */
    var renderer = new Graph.Renderer.Raphael('canvas', g, 700, 500);
    var rendererIsolated = new Graph.Renderer.Raphael('canvasIsolated', gIsolated, 700, 150);

    redraw = function () {
        layouter.layout();
        renderer.draw();
        layouterIsolated.layout();
        rendererIsolated.draw();
    };
    redraw();
};

function resetAfterClick(){
    $('#nodelink').text("")
    $("#edgesTable > tbody").find("tr:gt(0)").remove();
    $("#edgesTable > tbody").find("tr:eq(0)").remove();
    $('#nodeename').text("")
    $('#nodeassay').text("")
    $('#assaylink').attr("href", "")
}
