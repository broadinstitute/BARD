$(document).ready(function () {
    setExternalUrlVisibility();
});

$('#expectedValueType').on('change', setExternalUrlVisibility)

function setExternalUrlVisibility() {
    var expectedValueType = $('#expectedValueType').val();

    if (expectedValueType === "EXTERNAL_ONTOLOGY") {
        $('#externalUrlDiv').css('display', '')
    }
    else {
        $('#externalUrlDiv').css('display', 'none');
    }
}