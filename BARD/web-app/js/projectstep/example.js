var redraw;

/* only do all this when document has finished loading (needed for RaphaelJS) */
window.onload = function () {
    var g = new Graph();
    var gIsolated = new Graph();

    var graphInJSON = $.parseJSON($('#stepGraph').html());

    var connectedNodes = graphInJSON.connectedNodes;
    var render = function (r, n) {
        var label = r.text(0, 10, n.label);

        //the Raphael set is obligatory, containing all you want to display
        var set = r.set().push(
            r.rect(-10, -13, 10, 10).attr({"fill":"#fc0", "stroke-width":1/*, r : "9px"*/}))
            .push(label);

        set.click(
            function click() {
                alert("id " + n.id)
                var edges = n.edges;
                var dis ="";
                for (var i = 0; i < edges.length; i++) {
                    dis = dis + n.data.i  + " " + edges[i].source.id + " " + edges[i].target.id
                }
                alert("clicked" + dis)
            }
        );
        return set;
    };

    for (var i = 0; i < connectedNodes.length; i++) {
        var keyValues = connectedNodes[i].keyValues;
        g.addNode(connectedNodes[i].id, { label:keyValues.eid + "\n" + keyValues.stage, data: {"i": i}, render:render });
    }


    var isolatedNodes = graphInJSON.isolatedNodes;
    var renderIsolated = function (r, n) {
        var label = r.text(0, 10, n.label);
        //the Raphael set is obligatory, containing all you want to display
        var set = r.set().push(
            r.rect(-10, -13, 10, 10).attr({"fill":"#fc0", "stroke-width":1/*, r : "9px"*/}))
            .push(label);
        return set;
    };

    for (var i = 0; i < isolatedNodes.length; i++) {
        var keyValues = isolatedNodes[i].keyValues;
        gIsolated.addNode(isolatedNodes[i].id, { label:keyValues.eid + "\n" + keyValues.stage, render:renderIsolated});
    }

    var edges = graphInJSON.edges;
    for (var i = 0; i < edges.length; i++) {
        g.addEdge(edges[i].from, edges[i].to, {directed:true, stroke:"#bfa", fill:"#f00"});
    }

    /* layout the graph using the Spring layout implementation */
    var layouter = new Graph.Layout.Spring(g);
    /* layout the graph using the Spring layout implementation */
    var layouterIsolated = new Graph.Layout.Spring(gIsolated);

    /* draw the graph using the RaphaelJS draw implementation */
    var renderer = new Graph.Renderer.Raphael('canvas', g, 800, 500);
    var rendererIsolated = new Graph.Renderer.Raphael('canvasIsolated', gIsolated, 800, 150);

    redraw = function () {
        layouter.layout();
        renderer.draw();
        layouterIsolated.layout();
        rendererIsolated.draw();
    };
    redraw();
};

