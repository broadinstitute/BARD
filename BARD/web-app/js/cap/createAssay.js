/* Copyright (c) 2014, The Broad Institute
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
    * Redistributions of source code must retain the above copyright
      notice, this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright
      notice, this list of conditions and the following disclaimer in the
      documentation and/or other materials provided with the distribution.
    * Neither the name of The Broad Institute nor the
      names of its contributors may be used to endorse or promote products
      derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL The Broad Institute BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

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
