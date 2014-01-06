$(document).ready(function () {
    var widgetsByContainer = {
        '#valueConstraintContainer': ['input[name="valueConstraintType"]'],
        '#elementValueContainer': [null, '#valueElementId'],
        '#externalOntologyValueContainer': ['#extValueId', '#extValueSearch'],
        '#numericValueContainer': ['#qualifier,#valueNum,#valueNumUnitId'],
        '#numericRangeValueContainer': ['#valueMin,#valueMax']
        //    '#freeTextValueContainer': ['#valueDisplay']
    };

    var disabledInput = $("#disabledInput").attr("value") == "true";

    hideAllValueWidgets();

    // try and pick best focus
    function initialFocus() {
        if ($("#attributeElementId").is(':disabled')) {
            $('.btn-primary').focus();
        }
        else if (!$("#attributeElementId").val()) {
            $("#attributeElementId").select2("open");
        }
    }

    function potentiallyFocus(elementId) {
        if ($("#attributeElementId").is(':disabled')) {
            // do nothing we're in review mode
        }
        else {
            var valueElement = $(elementId);
            if (valueElement.data('select2')) {
                valueElement.select2("open");
            }
            else {
                valueElement.focus();
            }
        }
    }

    function clearAllValueFields() {
        $(':text').val("");
        $('#valueDescription').val('');
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
        var containers = $("[id$=ValueContainer]");
        containers.hide();
        containers.each(function (index, element) {
            var selectors = widgetsByContainer["#" + element.id]
            if (selectors) {
                var normalInputSelector = selectors[0];
                var select2Selector = selectors[1];

                if (normalInputSelector)
                    $(normalInputSelector).attr("disabled", "disabled");

                if (select2Selector)
                    $(select2Selector).select2("enable", false);
            }
        });
    }

    function showWidgets(containerSelector) {
        $(containerSelector).show();
        if (!disabledInput) {
            var selectors = widgetsByContainer[containerSelector];
            if (selectors) {
                var inputSelector = selectors[0];
                var select2Selector = selectors[1];

                if (inputSelector)
                    $(inputSelector).removeAttr("disabled");

                if (select2Selector)
                    $(select2Selector).select2("enable", true);
            }
        }
    }

    function onlyShowWidgetsForExpectedValueType(data) {
        hideAllValueWidgets();
        var expectedValueType = data.expectedValueType;
        if ('NUMERIC' === expectedValueType) {
            showWidgets('#numericValueContainer');
            initializeUnits(data.unitId);
            potentiallyFocus("#valueNum")
        }
        else if ('ELEMENT' === expectedValueType) {
            showWidgets('#elementValueContainer');
            $.ajax(bardAppContext + "/ontologyJSon/getValueDescriptorsV2", {
                data: {attributeId: data.id},
                success: (function (valueData) {
                    initializeValueElementSelect2(valueData);
                }),
                error: handleAjaxError()
            });

        }
        else if ('EXTERNAL_ONTOLOGY' === expectedValueType) {
            showExternalOntologyHelpText(data);
            showWidgets('#externalOntologyValueContainer');
            showWidgets('#freeTextValueContainer');
            potentiallyFocus("#extValueSearch");
        }
        else if ('FREE_TEXT' === expectedValueType) {
            showWidgets('#freeTextValueContainer');
            potentiallyFocus("#valueDisplay");
        }
        else if ('NONE' === expectedValueType) {
            showWidgets('#noneValueContainer');
        }
        else if ('RANGE' === expectedValueType) {
            showWidgets('#numericRangeValueContainer');
            potentiallyFocus("#valueMin");
        }
        else {
            // problem
        }
    }

    function showExternalOntologyHelpText(data) {
        var source;
        if (data.hasIntegratedSearch) {
            source = $('#externalOntologyIntegratedSearchTemplate').html();
        }
        else {
            source = $('#externalOntologyNoIntegratedSearchTemplate').html();
        }
        var template = Handlebars.compile(source);
        var html = template({attributeLabel: data.text, attributeExternalUrl: data.externalUrl});
        $("#externalOntologyInfo").html(html);
    }

    function initializeValueElementSelect2(backingData) {
        var valueElementSelect2 = new DescriptorSelect2('#valueElementId', 'Search for a value');
        valueElementSelect2.initSelect2(backingData);
        $('#valueElementId').on("change", function (e) {
                var selectedData = $("#valueElementId").select2("data");
                $("#valueDisplay").val(selectedData.text);
                $('#valueDescription').val( selectedData.description);
                $('button.btn-primary').focus();
        }).on("select2-highlight", function(e) {
                valueElementSelect2.updateSelect2DescriptionPopover(e.choice);
        });
        $('#elementValueContainer').show();
        potentiallyFocus("#valueElementId");
    }


    $("#extValueSearch").select2({
        minimumInputLength: 1,
        allowClear: true,
        placeholder: "Search for external ontology ids or terms",
        initSelection: function (element, callback) {
            var id = $(element).val();
            if (id !== "") {
                $.ajax(bardAppContext + "/ontologyJSon/findExternalItemById", {
                    data: {
                        elementId: $("#attributeElementId").val(),
                        id: id
                    },
                    dataType: "json",
                    success: function (data) {
                        callback(data);
                        potentiallyFocus("#extValueSearch");
                    },
                    error: handleAjaxError()
                });
            }
        },
        ajax: {
            url: bardAppContext + "/ontologyJSon/findExternalItemsByTerm",
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
            bardAppContext + "/ontologyJSon/getUnits",
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

    function showWidgetsForConstraints(data) {
        if ($("#valueConstraintFree").is(":checked")) {
            onlyShowWidgetsForExpectedValueType({expectedValueType: "UNCONSTRAINED"});
        } else if ($("#valueConstraintRange").is(":checked")) {
            onlyShowWidgetsForExpectedValueType({expectedValueType: "RANGE"});
        } else if ($("#valueConstraintList").is(":checked")) {
            onlyShowWidgetsForExpectedValueType(data);
        } else {
        }
    }

    var updateConstraintWidgets = function(data) {
        if ($("#providedWithResults").is(':checked')) {

            $("#valueConstraintContainer").show();

            // hide/show radio buttons based on expected type
            if ("NUMERIC" === data.expectedValueType) {
                $("#valueConstraintRangeContainer").show();
            } else {
                $("#valueConstraintRangeContainer").hide();
            }

            showWidgetsForConstraints(data)
        } else {
            $("#valueConstraintContainer").hide();
            onlyShowWidgetsForExpectedValueType(data)
        }
    };

    // set up "Value to be provided as part of result upload" checkbox to show the radio buttons
    // for type constraint if checked
    $('#providedWithResults').on('change', function (e) {
        var buttons = $('input:radio[name=valueConstraintType]');
        buttons.prop('checked', false);

        if ($("#providedWithResults").is(':checked')) {
            $('#valueConstraintFree').prop('checked', true);
        } else {

        }
        updateConstraintWidgets($("#attributeElementId").select2('data'));
    })

    var attributeSelect2 = new DescriptorSelect2('#attributeElementId', 'Search for attribute name',{results: []});
    attributeSelect2.initSelect2({results: []});
    $.ajax("/BARD/ontologyJSon/getAttributeDescriptors", {
        success:function (data) {
            attributeSelect2.initSelect2(data,updateConstraintWidgets);
        },
        error: handleAjaxError()
    });

    $("#attributeElementId").on("change", function (e) {
        // on change of the attribute, clear all value fields
        clearAllValueFields();
        // hide any existing error messages, will be redisplayed when user submits with new attribute
        hideAnyErrorMessages();
        // based on the attribute selected only show the appropriate value widgets
        var selectedData = $("#attributeElementId").select2("data");
        updateConstraintWidgets(selectedData);
        $('#attributeDescription').val( selectedData.description);
    }).on("select2-highlight", function(e) {
            attributeSelect2.updateSelect2DescriptionPopover(e.choice);
        });
    initialFocus();

    // set up the radio buttons so that widgets are shown based on which type of constraint
    $('input[name="valueConstraintType"]').on('change', function (e) {
        showWidgetsForConstraints($("#attributeElementId").select2('data'))
    })


//    Update the propose-a-new-term button link to include the elementId
    $('#proposeANewTermButton').on("hover click", function () {
        var currentHref = $(this).attr('href').split('?')[0];
        var attributeElementId = $('#attributeElementId').attr('value');
        $(this).attr('href', currentHref + '?attributeElementId=' + attributeElementId)
    });

});
