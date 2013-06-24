
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

    var attributeValueType, attributeUnitId, attributeData;
    var setupAttributeSelection = function(attributeDetailsArrivedCallback) {
        var contCallback = function(data) {
            // save this in variables outside this scope for later use
            attributeValueType = data ? data.expectedValueType : '';
            attributeUnitId = data.unitId;
            attributeData = data;

            attributeDetailsArrivedCallback(data);
        };

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
                            contCallback(data);
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
                // on change of the attribute, clear all value fields
                clearAllValueFields();
                // hide any existing error messages, will be redisplayed when user submits with new attribute
                hideAnyErrorMessages();
                // based on the attribute selected only show the appropriate value widgets
                var data = $("#attributeElementId").select2("data");
                contCallback(data);
        });
    };

    // if this is not present, we're not allowing user to enter specifications, only values
    if($("#providedWithResults").length == 0){
        setupAttributeSelection(onlyShowWidgetsBasedOnResponse)
    } else {
        setupAttributeSelection(onlyShowWidgetsBasedOnResponse)
    }
    initialFocus();

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

    function onlyShowWidgetsBasedOnResponse(data) {
        var expectedValueType = data? data.expectedValueType: '';
        var unitId = data.unitId;

        updateConstraintWidgets();
    }

    function onlyShowWidgetsForExpectedValueType(expectedValueType, unitId, data) {
        hideAllValueWidgets();
        if ('NUMERIC' === expectedValueType) {
            $('#numericValueContainer').show();
            initializeUnits(unitId);
            potentiallyFocus("#valueNum")

        }
        else if ('ELEMENT' === expectedValueType) {
            $('#elementValueContainer').show();
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
        else if ('RANGE' === expectedValueType) {
            $('#numericRangeValueContainer').show();
            potentiallyFocus("#valueMin");
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
        var html = template({attributeLabel : data.text, attributeExternalUrl : data.externalUrl});
        $("#externalOntologyInfo").html(html);
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

    function showWidgetsForConstraints() {
        if($("#valueConstraintFree").is(":checked")) {
            onlyShowWidgetsForExpectedValueType("UNCONSTRAINED", null, null);
        } else if($("#valueConstraintRange").is(":checked")) {
            onlyShowWidgetsForExpectedValueType("RANGE", null, null);
        } else if($("#valueConstraintList").is(":checked")) {
            onlyShowWidgetsForExpectedValueType(attributeValueType, attributeUnitId, attributeData);
        } else {
        }
    }

    function updateConstraintWidgets() {
        if($("#providedWithResults").is(':checked')) {

            $("#valueConstraintContainer").show();

            // hide/show radio buttons based on expected type
            if(attributeValueType == "NUMERIC") {
                $("#valueConstraintRangeContainer").show();
            } else {
                $("#valueConstraintRangeContainer").hide();
            }

            showWidgetsForConstraints()
        } else {
            $("#valueConstraintContainer").hide();

            onlyShowWidgetsForExpectedValueType(attributeValueType, attributeUnitId, attributeData)
        }
    }

    // set up "Value to be provided as part of result upload" checkbox to show the radio buttons
    // for type constraint if checked
    $('#providedWithResults').on('change', function(e){
        var buttons = $('input:radio[name=valueConstraintType]');
        buttons.prop('checked', false);

        if($("#providedWithResults").is(':checked')) {
            $('#valueConstraintFree').prop('checked', true);
        } else {

        }

        updateConstraintWidgets();
    })

    // set up the radio buttons so that widgets are shown based on which type of constraint
    $('input[name="valueConstraintType"]').on('change', function(e) {
        showWidgetsForConstraints()
    })

});



