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

var EXPERIMENT_STAGE_PATH =bardAppContext +  "/ontologyJSon/getAttributeDescriptorsNoExpectedValueType?startOfFullPath=project management> experiment> experiment stage";

//handle concentration result types on dose response page
$('#stageId').on('change', function (e) {
    var selectedData = $("#stageId").select2("data");
    $('#stageDescription').val(selectedData.description);
});

var stageSelect2 = new DescriptorSelect2('#stageId', 'Select Experiment Stage', {results: []});
stageSelect2.initSelect2({results: []});
$.ajax(EXPERIMENT_STAGE_PATH, {
        success: function (data) {
            stageSelect2.initSelect2(data, handleStageEditing);
        },
        error: handleAjaxError()
    });

$('#stageId').on("select2-highlight", function (e) {
    stageSelect2.updateSelect2DescriptionPopover(e.choice);
});

//When we are in edit mode, this function is called to populate text boxes
var handleStageEditing = function (data) {
    var t = data.text;
};
