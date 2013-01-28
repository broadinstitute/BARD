function enableAutoCompleteOntology (section, idFieldSelector) {
    $(idFieldSelector).select2({
        minimumInputLength: 2,
        width: "70%",
        placeholder: "Start typing",
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
