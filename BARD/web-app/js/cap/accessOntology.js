/**
 * Created with IntelliJ IDEA.
 * User: xiaorong
 * Date: 2/12/13
 * Time: 10:26 AM
 * To change this template use File | Settings | File Templates.
 */

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