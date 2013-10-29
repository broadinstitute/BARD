/**
 * Builds a Select2 widget for element descriptors that
 * @param elementId
 * @param placeholderText
 * @param backingData
 * @constructor
 */
function DescriptorSelect2(elementId, placeholderText, backingData ) {
    this.elementId = elementId;
    this.placeholder = placeholderText;
    this.backingData = backingData;

    this.initPopover = function(){
        $(this.elementId).on("select2-highlight", function(e) {
            updateSelect2DescriptionPopover(e.choice);
        });
    };

    this.updateSelect2DescriptionPopover = function(data){
        // destroy any existing popover
        $(".select2-search").popover("destroy");
        // if we have any description, create a popover
        if(data.description){
            $(".select2-search").popover({placement:'top',
                html: true,
                trigger:'manual',
                title:'<b>' +data.text + '</b>',
                content:data.description});
            $(".select2-search").popover('show');
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
    this.initSelect2 = function(backingData, onInitDoneCallback) {
        this.backingData = backingData;
        $(this.elementId).select2({
            placeholder: placeholderText,
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
                            if(onInitDoneCallback){
                                onInitDoneCallback(data);
                            }
                        });
                }
            },
            data: this.backingData,
            formatResult: function (result, container, query, escapeMarkup) {
                var markup = [];
                window.Select2.util.markMatch(result.parentFullPath, query.term, markup, escapeMarkup);
                markup.push('> ');
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
    };



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
 * @param elementId to bind select2 to
 * @param placeholderText
 * @param backingData an array of maps as outlined above
 * @param onInitDoneCallback optional function to do something when the ajax call for initSelection returns
 */
//function initializeDescriptorSelect2(elementId, placeholderText, backingData, onInitDoneCallback ) {
//    $(elementId).select2({
//        placeholder: placeholderText,
//        allowClear: true,
//        initSelection: function (element, callback) {
//            var id = $(element).val();
//            if (id !== "") {
//                $.ajax("/BARD/ontologyJSon/getElement", {
//                    data: {
//                        id: id
//                    },
//                    dataType: "json"
//                }).done(function (data) {
//                    callback(data);
//                    if(onInitDoneCallback){
//                        onInitDoneCallback(data)
//                    }
//                });
//            }
//        },
//        data: backingData,
//        formatResult: function (result, container, query, escapeMarkup) {
//            var markup = [];
//            window.Select2.util.markMatch(result.parentFullPath, query.term, markup, escapeMarkup);
//            markup.push('> ');
//            markup.push('<b>');
//            window.Select2.util.markMatch(result.text, query.term, markup, escapeMarkup);
//            markup.push('</b>');
//            return markup.join("");
//        },
//        query: function (query) {
//            var filteredData = {results: []};
//            $.each(backingData.results, function (index, value) {
//                if (value.fullPath.toUpperCase().indexOf(query.term.toUpperCase()) >= 0) {
//                    filteredData.results.push(value);
//                }
//            });
//            query.callback(filteredData);
//        }
//    });
//}

