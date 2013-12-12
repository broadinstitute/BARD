/**
 * Builds a Select2 widget for element descriptors that
 * @param elementId
 * @param placeholderText
 * @param backingData
 * @constructor
 */
function DescriptorSelect2(elementId, placeholderText, backingData, delimiter, dropdownCssClass) {
    this.elementId = elementId;
    this.placeholder = placeholderText;
    this.backingData = backingData;
    this.delimiter = delimiter;
    this.dropdownCssClass = dropdownCssClass;

    this.updateSelect2DescriptionPopover = function (data) {
        // destroy any existing popover
        $(".select2-input").popover("destroy");
        // if we have any description, create a popover
        if (data.description) {
            $(".select2-input").popover({placement: 'top',
                html: true,
                trigger: 'manual',
                title: '<b>' + data.text + '</b>',
                content: data.description});
            $(".select2-input").popover('show');
        }
    };

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
     */
    this.initSelect2 = function (backingData, onInitDoneCallback) {
        this.backingData = backingData;
        $(this.elementId).select2({
            dropdownCssClass: dropdownCssClass ? dropdownCssClass : '',
            placeholder: placeholderText,
            allowClear: true,
            initSelection: function (element, callback) {
                var id = $(element).val();
                if (id !== "") {
                    $.ajax(bardAppContext + "/ontologyJSon/getElement", {
                        data: {
                            id: id
                        },
                        dataType: "json"
                    }).done(function (data) {
                            callback(data);
                            if (onInitDoneCallback) {
                                onInitDoneCallback(data);
                            }
                        });
                }
            },
            data: this.backingData,
            formatResult: function (result, container, query, escapeMarkup) {
                var markup = [];
                window.Select2.util.markMatch(result.parentFullPath, query.term, markup, escapeMarkup);
                markup.push(delimiter ? delimiter : '> ');
                markup.push('<b>');
                window.Select2.util.markMatch(result.text, query.term, markup, escapeMarkup);
                markup.push('</b>');
                return markup.join("");
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
        // on change eliminate any popover attached to .select2-input otherwise it can show up out of context
        // in a differenct select2 widget than initially displayed
        $(this.elementId).on("change", function (e) {
            $(".select2-input").popover("destroy");
        });
    };
}
