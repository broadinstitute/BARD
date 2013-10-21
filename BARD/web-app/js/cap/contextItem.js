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

    function updateSelect2DescriptionPopover(elementId, data){
        if(data.description){
            $(elementId).popover({placement:'top',
                                  html: true,
                                  trigger:'manual',
                                  title:'<b>' +data.text + '</b>',
                                  content:data.description});
            $(elementId).popover('show');
        }
        else{
            $(elementId).popover("destroy");
        }
    }

    /***********************************************
     * the backingData will have the form element/descriptor
     * [
     *   [
     *      id: 1,                         // id for the
     *      text: "l1",                    // the label/name of the element/descriptor
     *      description: null,             // the description or definition of the element
     *      expectedValueType: "NUMERIC",  // ( NUMERIC || ELEMENT || EXTERNAL_ONTOLOGY || FREE_TEXT || NONE )
     *      parentFullPath: null,          // the path of the parent descriptor in the hierarchy
     *      fullPath: "somePath",          // fullPath of the attribute within the hierarchy
     *      hasIntegratedSearch: false,    // (true|false) for and externalOntology do we have an integrated search
     *      externalUrl: null,             // (URL|'') for externalOntology the URL
     *      unitId: null,                  // id of unit if this attribute has default units assigned, should be paired with expectedValueType:NUMERIC
     *      addChildMethod: "NO"           // indication of if a child descriptor can be added by the end user, ( RDM_REQUEST || DIRECT || NO )
     *   ]
     *   ...
     *  ]
     * @param backingData
     */
    function initializeAttributeSelect2(backingData) {
        $("attributeElementId").off(); // clear any existing event listeners
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
                            updateConstraintWidgets(data);
                            updateDescriptionField();
                            validateAddToChildren();
                        });
                }
            },
            data: backingData,
            formatResult: function (result, container, query, escapeMarkup) {
                return itemSelect2Format(result, container, query, escapeMarkup);
            },
            query: function (query) {
                var filteredData = {results: []};
                $.each(backingData.results, function (index, value) {
                    if (value.fullPath.toUpperCase().indexOf(query.term.toUpperCase()) >= 0) {
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
            updateConstraintWidgets(selectedData);
            updateDescriptionText('#attributeDescription', selectedData);
            // Update the 'description' field is available
            updateDescriptionField();
            validateAddToChildren();
        }).on("select2-highlight", function(e) {
            updateSelect2DescriptionPopover(".select2-search", e.choice);
        });

        initialFocus();
    };
    initializeAttributeSelect2({results: []});
    $.ajax("/BARD/ontologyJSon/getAttributeDescriptors").done(function (data) {
        initializeAttributeSelect2(data);
    });


    function itemSelect2Format(result, container, query,escapeMarkup) {
        var markup = [];
        window.Select2.util.markMatch(result.parentFullPath, query.term, markup, escapeMarkup);
        markup.push('> ');
        markup.push('<b>');
        window.Select2.util.markMatch(result.text, query.term, markup, escapeMarkup);
        markup.push('</b>');
        return markup.join("");
    }

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
            $.ajax("/BARD/ontologyJSon/getValueDescriptorsV2", {
                data: {attributeId: data.id}
            }).done(function (valueData) {
                    initializeValueElementSelect2(valueData);
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
        $("#valueElementId").off();  // clear any existing event listeners
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
                    }).done(function (data) {
                            callback(data);
                        });
                }
            },
            formatResult: function (result, container, query, escapeMarkup) {
                return itemSelect2Format(result, container, query, escapeMarkup);
            },
            query: function (query) {
                var filteredData = {results: []};
                $.each(backingData.results, function (index, value) {
                    if (value.fullPath.toUpperCase().indexOf(query.term.toUpperCase()) >= 0) {
                        filteredData.results.push(value);
                    }
                });
                query.callback(filteredData);
            }
        }).on("change", function (e) {
                var selectedData = $("#valueElementId").select2("data");
                $("#valueDisplay").val(selectedData.text);
                $('#valueDescription').val( selectedData.description);
                $('button.btn-primary').focus();
        }).on("select2-highlight", function(e) {
            updateSelect2DescriptionPopover(".select2-search", e.choice);
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

    function updateConstraintWidgets(data) {
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
    }

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

    function updateDescriptionField() {
        var attributeElementId = $("#attributeElementId").attr('value');
        $.ajax("/BARD/ontologyJSon/getElement", {
            data: {
                id: attributeElementId
            },
            dataType: "json"
        }).done(function (data) {
                $('#parentDescription').attr('value', data.description)
            });
    }

    //Validate that the element can be used as a parent-element for a newly proposed element (element.addChildMethod==DIRECT)
    function validateAddToChildren() {
        var attributeElementId = $("#attributeElementId").attr('value');

        $.ajax("/BARD/ontologyJSon/getElement", {
            data: {
                id: attributeElementId
            },
            dataType: "json"
        }).done(function (data) {
                if ("DIRECT" === data.addChildMethod) {
                    $('#nextBtn').attr("disabled", false);//enable the NEXT button with a successful selection
                    $('#attributeElementErrorField').empty();
                }
                else {
                    $('#attributeElementErrorField').html('<p class="text-error"><i class="icon-exclamation-sign"></i> That term is not allowed to be used as a parent</p>');
                }
            });
    }
});
