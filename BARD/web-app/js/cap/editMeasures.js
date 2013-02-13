function measureNodeDropped(node, sourceNode, hitMode, ui, draggable) {
    var parentMeasureId = node.data.key
    if(hitMode == "before" || hitMode == "after") {
        // only allowed for root nodes, so assume it's a root
        parentMeasureId = null
    }
    $.getJSON("/BARD/assayDefinition/moveMeasureNode",
        {
            measureId: sourceNode.data.key,
            parentMeasureId: parentMeasureId
        },
        function(data, textStatus) {
            sourceNode.move(node, hitMode);
        }
    )
}
