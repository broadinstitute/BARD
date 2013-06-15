
$(document).ready(function () {
    hideAllValueWidgets();

    // try and pick best focus
    function initialFocus() {
        if ($("#attributeElementId").is(':disabled')) {
            $('button.btn-primary').focus();
        }
        else if (!$("#attributeElementId").val()) {
            $("#attributeElementId").select2("open");
        }
    }
    function potentiallyFocus(elementId){
        if ($("#attributeElementId").is(':disabled')) {
            // do nothing we're in review mode
        }
        else {
            var valueElement = $(elementId);
            if(valueElement.data('select2')){
                valueElement.select2("open");
            }
            else{
                valueElement.focus();
            }
        }
    }

    $.ajax("/BARD/ontologyJSon/getAttributeDescriptors").done(function (data) {
        initializeAttributeSelect2(data);
    });


    function initializeAttributeSelect2(backingData){
        $("#attributeElementId").select2({
            placeholder: "Search for attribute name",
            allowClear: true,
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
                            onlyShowWidgetsForExpectedValueType(data);
                        });
                }
            },
            data:backingData,
            formatResult: function(result, container, query) {
                var markup=[];
                window.Select2.util.markMatch(result.fullPath, query.term, markup);
                return markup.join("");
            },
            query: function (query) {
                var filteredData = {results: []};
                $.each(backingData.results, function(index, value){
                    if(value.fullPath.toUpperCase().indexOf(query.term.toUpperCase())>=0){
                        filteredData.results.push(value);
                    }
                });
                query.callback(filteredData);
            }
        });
        $("#attributeElementId").on("change", function (e) {
            // on change of the attribute, clear all value fields
            clearAllValueFields();
            // hide any existing error messages, will be redisplayed when user submits with new attribute
            hideAnyErrorMessages();
            // based on the attribute selected only show the appropriate value widgets
            var selectedData = $("#attributeElementId").select2("data");
            onlyShowWidgetsForExpectedValueType(selectedData);
        });
        initialFocus();
    }
    initializeAttributeSelect2({results:[]});


//    initialFocus();

    function clearAllValueFields() {
        $(':text').val("");
        $("#valueElementId").select2("data", {results: []});
        $("#extValueSearch").select2("data", {results: []});
        $("#valueNumUnitId").select2("data", {results: []});
    }
    function hideAnyErrorMessages() {
        $('.help-inline').hide();
        $('.help-block').hide();
        $('.alert-error').hide();
        $('.error').removeClass('error');
    }

    function hideAllValueWidgets() {
        $("[id$=ValueContainer]").hide();
    }
    function onlyShowWidgetsForExpectedValueType(data) {
        hideAllValueWidgets();
        var expectedValueType = data? data.expectedValueType: '';
        var unitId = data.unitId;
        if ('NUMERIC' === expectedValueType) {
            $('#numericValueContainer').show();
            initializeUnits(unitId);
            potentiallyFocus("#valueNum")

        }
        else if ('ELEMENT' === expectedValueType) {
            $.ajax("/BARD/ontologyJSon/getValueDescriptorsV2",{
                data : {attributeId : data.id}
            }).done(function (valueData) {
                initializeValueElementSelect2(valueData);
            });
            potentiallyFocus("#valueElementId");
        }
        else if ('EXTERNAL_ONTOLOGY' === expectedValueType) {
            showExternalOntologyHelpText(data);
            $('#externalOntologyValueContainer').show();
            $('#freeTextValueContainer').show();
            potentiallyFocus("#extValueSearch");
        }
        else if ('FREE_TEXT' === expectedValueType) {
            $('#freeTextValueContainer').show();
            potentiallyFocus("#valueDisplay");
        }
        else if ('NONE' === expectedValueType) {
            $('#noneValueContainer').show();
        }
        else {
            // problem
        }
    }

    function showExternalOntologyHelpText(data){
        var source;
        if(data.hasIntegratedSearch){
            source = $('#externalOntologyIntegratedSearchTemplate').html();
        }
        else {
            source = $('#externalOntologyNoIntegratedSearchTemplate').html();
        }
        var template = Handlebars.compile(source);
        var html = template({attributeLabel : data.text,attributeExternalUrl : data.externalUrl});
        $("#externalOntologyInfo").html(html);
    }

    function initializeValueElementSelect2(backingData){
        $("#valueElementId").select2({
            allowClear: true,
            placeholder: "Search for value",
            data: backingData,
            initSelection: function (element, callback) {
                var id = $(element).val();
                if (id !== "") {
                    $.ajax("/BARD/ontologyJSon/getElement", {
                        data: {
                            id: id
                        },
                        dataType: "json"
                    }).done(function (data) { callback(data); });
                }
            },
            formatResult: function(result, container, query) {
                var markup=[];
                window.Select2.util.markMatch(result.fullPath, query.term, markup);
                return markup.join("");
            },
            query: function (query) {
                var filteredData = {results: []};
                $.each(backingData.results, function(index, value){
                    if(value.fullPath.toUpperCase().indexOf(query.term.toUpperCase())>=0){
                        filteredData.results.push(value);
                    }
                });
                query.callback(filteredData);
            }
        }).on("change", function (e) {
                $("#valueDisplay").val($("#valueElementId").select2("data").text);
                $('button.btn-primary').focus();
            });
        $('#elementValueContainer').show();
    }


    $("#extValueSearch").select2({
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
                        potentiallyFocus("#extValueSearch");
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
            $("#extValueId").val($("#extValueSearch").select2("data").id);
            $("#valueDisplay").val($("#extValueSearch").select2("data").text);
            $('button.btn-primary').focus();
        });
    if ($("#extValueSearch").val()) {
        $("#extValueSearch").select2("data", {id: $("#extValueSearch").val(), text: $("#extValueText").val()});
    }


    function initializeUnits(attributeUnitId) {
        var unitSelector = '#valueNumUnitId'
        $(unitSelector).select2({
            placeholder: "Select a Unit",
            allowClear: true,
            width: 'resolve',
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
            allowClear: true,
            width: 'resolve',
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



