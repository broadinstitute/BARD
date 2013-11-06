var EXPERIMENT_STAGE_PATH = "/BARD/ontologyJSon/getAttributeDescriptors?startOfFullPath=project management> experiment> experiment stage";

//handle concentration result types on dose response page
$('#stageId').on('change', function (e) {
    var selectedData = $("#stageId").select2("data");
    $('#stageDescription').val(selectedData.description);
});

var stageSelect2 = new DescriptorSelect2('#stageId', 'Select Experiment Stage', {results: []});
stageSelect2.initSelect2({results: []});
$.ajax(EXPERIMENT_STAGE_PATH).done(function (data) {
    stageSelect2.initSelect2(data, handleStageEditing);
});

$('#stageId').on("select2-highlight", function (e) {
    stageSelect2.updateSelect2DescriptionPopover(e.choice);
});

//When we are in edit mode, this function is called to populate text boxes
var handleStageEditing = function (data) {
    var t = data.text;
};