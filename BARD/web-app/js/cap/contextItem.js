$(document).ready(function () {
    $("#attributeElementId").select2({
        minimumInputLength: 1,
        allowClear: true,
        placeholder: "Search for attribute name",
        initSelection: function (element, callback) {
            var id = $(element).val();
            if (id !== "") {
                $.ajax("/BARD/ontologyJSon/getElement", {
                    data: {
                        id: id
                    },
                    dataType: "json"
                }).done(function (data) {
                        callback(data);
                        onlyShowWidgetsForExpectedValueType(data.expectedValueType);
                        if (data.unitId) {
                            initializeUnits(data.unitId);
                        }
                    });
            }
        },
        ajax: {
            url: "/BARD/ontologyJSon/getDescriptors",
            dataType: 'json',
            quietMillis: 100,
            data: function (term) {
                return { term: term};
            },
            results: function (data) {
                return data;
            }
        }
    }).on("change", function (e) {
            // on change the attribute, clear all the other fields
            $(':text').val("");
            $("#valueElementId").select2("data", {results: []});
            $("#extValueId").select2("data", {results: []});
            $("#valueNumUnitId").select2("data", {results: []});
            //initializeUnits($("#attributeElementId").select2("data").unitId);
            onlyShowWidgetsForExpectedValueType($("#attributeElementId").select2("data").expectedValueType);
        });

    function onlyShowWidgetsForExpectedValueType(expectedValueType) {
        hideAllValueWidgets();
        if ('NUMERIC' === expectedValueType) {
            $('#numericValueContainer').show();
        }
        else if ('ELEMENT'=== expectedValueType) {
            $('#elementValueContainer').show();
        }
        else if ('EXTERNAL_ONTOLOGY'=== expectedValueType) {
            $('#externalOntologyContainer').show();
        }
        else if ('FREE_TEXT'=== expectedValueType) {
            $('#freeTextValueContainer').show();
        }
        else if('NONE'=== expectedValueType){
            $('#noneValueContainer').show();
        }
        else{
            // problem
        }
    }

    function hideAllValueWidgets(){
        $("[id$=ValueContainer]").hide();
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
            $('button.btn-primary').focus();
        });
    if ($("#valueElementId").val()) {
        $("#valueElementId").select2("data", {id: $("#valueElementId").val(), text: $("#valueElementText").val()});
    }

    $("#extValueId").select2({
        minimumInputLength: 1,
        allowClear: true,
        placeholder: "Search for external ontology ids or terms",
        initSelection: function (element, callback) {
            var id = $(element).val();
            if (id !== "") {
                $.ajax("/BARD/ontologyJSon/findExternalItemById", {
                    data: {
                        elementId: $("#attributeElementId").val(),
                        id: id
                    },
                    dataType: "json"
                }).done(function (data) {
                        callback(data);
                        initialFocus();
                    });
            }
        },
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
            $('button.btn-primary').focus();
        });
    if ($("#extValueId").val()) {
        $("#extValueId").select2("data", {id: $("#extValueId").val(), text: $("#extValueText").val()});
    }
    //initialFocus();

    // try and pick best focus
    function initialFocus() {
        if ($("#attributeElementId").is(':disabled')) {
            $('button.btn-primary').focus();
        }
        else if (!$("#attributeElementId").val()) {
            $("#attributeElementId").select2("open");
        }
        else if ($("#valueElementId").val()) {
            $("#valueElementId").select2("open");
        }
        else if ($("#extValueId").val()) {
            $("#extValueId").select2("open");
        }
        else if ($("#valueNum").val()) {
            $("#valueNum").focus();
        }
        else if ($("#valueDisplay").val()) {
            $("#valueDisplay").focus();
        }
    }

    function initializeUnits(attributeUnitId) {
        var unitSelector = '#valueNumUnitId'
        $(unitSelector).select2({
            placeholder: "Select a Unit",
            width: "70%",
            data: []
        });
        var unitsData = {results: []};
        $.getJSON(
            "/BARD/ontologyJSon/getUnits",
            {
                toUnitId: attributeUnitId
            },
            function (data, textStatus, jqXHR) {
                $.each(data, function (index, val) {
                    unitsData.results.push({id: val.value, text: val.label})
                });
                populateDataValueUnitId(unitsData.results, attributeUnitId);
            }
        ).done();


    }

    function populateDataValueUnitId(data, selectedId) {
        var unitSelector = '#valueNumUnitId'
        $(unitSelector).select2({
            placeholder: "Select a Unit",
            width: "70%",
            data: data
        });
        if (selectedId) {
            var found = $.map(data, function (val) {
                return val.id == selectedId ? val : null;
            });
            if (found) {
                $(unitSelector).select2("data", found[0]);
            }
        }
    }

});



