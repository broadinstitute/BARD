$(document).ready(function () {
    $("select").change(function () {
        var selectedValue = $("select option:selected").val();
        switch (selectedValue) {
            case "EID":
                $("#selectBoxMessage").text("Identify experiments to move");
                $("#textBoxMessage").text("ID's of experiments (space separated)");
                break;
            case "AID":
                $("#selectBoxMessage").text("Identify PubChem AID's to move");
                $("#textBoxMessage").text("PubChem AID's (space separated)");
                break;
            case "ADID":
                $("#selectBoxMessage").text("Identify Assay Definitions to move");
                $("#textBoxMessage").text("ID's of assay definitions (space separated)");
                break;

        }
    }).trigger("change");




});
function isChecked(checkboxId) {
    var id = '#' + checkboxId;
    return $(id).is(":checked");
}
function resetSelectAll() {
    // if all checkbox are selected, check the selectall checkbox
    // and viceversa
    if ($(".check").length == $(".check:checked").length) {
        $("#selectAll").attr("checked", "checked");
    } else {
        $("#selectAll").removeAttr("checked");
    }
}
