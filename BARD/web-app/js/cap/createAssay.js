$(document).ready(function () {
    function initializeAssayFormatValueIdSelect2(backingData) {
        $('#assayFormatValueId').off().select2("destroy"); //remove any pre-existing select2 objects and all registered event handlers.
        var assayFormatValueIdSelect2 = new DescriptorSelect2('#assayFormatValueId', 'Search for an assay format');
        assayFormatValueIdSelect2.initSelect2(backingData);
        $('#assayFormatValueId').on("select2-highlight", function (e) {
            assayFormatValueIdSelect2.updateSelect2DescriptionPopover(e.choice);
        });
        $('#assayFormatValueId').on("select2-opening", function (e) {
            var assayFormatId = $('#assayFormatId').val();
            $.ajax("/BARD/ontologyJSon/getValueDescriptorsV2", {
                async: false,
                data: {attributeId: assayFormatId},
                success: (function (valueData) {
                    assayFormatValueIdSelect2.backingData.results = valueData.results;
                    $('#assayFormatValueId').select2("data", valueData);
                }),
                error: handleAjaxError()
            });
        });
    }
    initializeAssayFormatValueIdSelect2({results: []});
});