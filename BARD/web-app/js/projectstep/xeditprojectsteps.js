var redraw;

/* only do all this when document has finished loading (needed for RaphaelJS) */
//Shoulw we not use the Jquery ready function here?
//window.onload = function () {
//
//
//
//
//};
$(document).ready(function () {
    $.fn.editable.defaults.mode = 'inline';

    initFunction();
    $('#projectStageId').editable({
        success: function (response, newValue) {
            refreshProjectSteps();
        }
    });
});
function refreshProjectSteps(){

}

