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

function enableAutoCompleteOntology (section, idFieldSelector) {
    $(idFieldSelector).select2({
        minimumInputLength: 2,
        width: "70%",
        placeholder: "Start typing name",
        query: function(query) {

            $.getJSON(
                "/BARD/ontologyJSon/getLabelsFromTree",
                {
                    label: query.term,
                    tree: section
                },
                function(data, textStatus, jqXHR) {
                    var selectData = {results:[]}
                    $.each(data, function(index, val) {
                        selectData.results.push({id: val.elementId, text: val.label})
                    })
                    query.callback(selectData)
                }
            );
        }
    })
}
