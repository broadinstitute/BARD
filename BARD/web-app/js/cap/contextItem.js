$(document).ready(function () {
    $("#attributeElementId").select2({
        minimumInputLength: 1,
        allowClear: true,
        placeholder: "Search for attribute name",
        ajax: {
            url: "/BARD/ontologyJSon/getDescriptors",
            dataType: 'json',
            quietMillis: 100,
            data: function (term) {
                return { term: term};
            },
            results: function (data) {
                var selectData = {results: []}
                $.each(data, function (index, val) {
                    selectData.results.push({id: val.elementId, text: val.label})
                });
                return selectData;
            }
        }
    });
    if($("#attributeElementId").val()){
        $("#attributeElementId").select2("data", {id: $("#attributeElementId").val(), text:$("#attributeElementLabel").val()});
    }
    else{
        $("#attributeElementId").select2("open");
    }

    $("#valueElementId").select2({
        minimumInputLength: 1,
        allowClear: true,
        placeholder: "Search for value",
        ajax: {
            url: "/BARD/ontologyJSon/getValueDescriptors",
            dataType: 'json',
            quietMillis: 100,
            data: function (term) {
                return { term: term,
                    attributeId: $("#attributeElementId").val()
                };
            },
            results: function (data) {
                var selectData = {results: []}
                $.each(data, function (index, val) {
                    selectData.results.push({id: val.elementId, text: val.label})
                });
                return selectData;
            }
        }
    }).on("change", function (e) {
            $("#valueDisplay").val($("#valueElementId").select2("data").text);
        });
    $("#extValueId").select2({
        minimumInputLength: 1,
        allowClear: true,
        placeholder: "Search for external ontology ids or terms",
        ajax: {
            url: "/BARD/ontologyJSon/findExternalItemsByTerm",
            dataType: 'json',
            quietMillis: 100,
            data: function (term) {
                return { term: term,
                    elementId: $("#attributeElementId").val()
                };
            },
            results: function (data) {
                return {results: data.externalItems};
            }
        }
    }).on("change", function (e) {
            $("#valueDisplay").val($("#extValueId").select2("data").display);
        });


});



