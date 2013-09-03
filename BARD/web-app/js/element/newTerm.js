$(document).ready(function () {
    $("#element-hierarchy-tree").dynatree
    (
        {
            title: "BARD Hierarchy Tree",
            autoFocus: false,
            initAjax: {
                url: "/BARD/element/buildTopLevelHierarchyTree"
            },
            onActivate: function (node) {
                $("#parentLabel").val(node.data.title);
                $("#parentDescription").val(node.data.description);

                if (node.data.childMethod == 'DIRECT') {
                    //make fields writable
                    $("#termLabelId").attr("readonly", false);
                    $("#termDescriptionId").attr("readonly", false);
                    $("#abbrvId").attr("readonly", false);
                    $("#synonymsId").attr("readonly", false);
                    $("#curationNotesId").attr("readonly", false);
                    $("#saveBtn").attr("disabled", false);
                }
                else {
                    //make all fields readonly
                    $("#termLabelId").attr("readonly", true);
                    $("#termDescriptionId").attr("readonly", true);
                    $("#abbrvId").attr("readonly", true);
                    $("#synonymsId").attr("readonly", true);
                    $("#curationNotesId").attr("readonly", true);
                    $("#saveBtn").attr("disabled", true);

                }
            },
            onLazyRead: function (node) {
                node.appendAjax
                (
                    {
                        url: "/BARD/element/getChildrenAsJson",
                        dataType: "json",
                        data: {elementId: node.data.elementId}

                    }
                );


            }
        }
    )
    ;
    $("#saveTerm").ajaxForm({
        url: '/BARD/element/saveTerm',
        type: 'POST',
        success: function (responseText, statusText, xhr, jqForm) {
            updateTermForm(responseText);
            reloadActiveNode();
        },
        error: function (request, status, error) {
            updateTermForm(responseText);
        }

    });
    selectCurrentElement();
});
/**
 *
 * Some browsers will not allow you to close the window using window.close()
 * unless the script opened the window. This is a little annoying sometimes.
 * But there is a workaround to resolve this issue.
 * If you observe the error message that is thrown by Mozilla Firefox,
 * “Scripts may not close windows that were not opened by the script”,
 * it clearly says that if the script didn’t open the window, you can’t close that.
 * But we open a blank page in the same window using “_self” as the target window and close the same window.
 * In that way, the script opens the window (which is a blank one) and closes the window too.
 *
 * Credit : http://raghunathgurjar.wordpress.com/2012/05/02/how-close-current-window-tab-in-all-browsers-using-javascript/
 */
function closeWindow() {
    var win = window.open("","_self");
    win.close();
}
function reloadActiveNode() {
    var node = $("#element-hierarchy-tree").dynatree("getActiveNode");
    if (node && node.isLazy()) {
        node.reloadChildren(function (node, isOk) {

        });
    }
}

function selectCurrentElement() {
    var currentElementId = $("#currentElementId").val();
    if (currentElementId) {
        $("#element-hierarchy-tree").dynatree("getTree").activateKey(currentElementId);
    }
}

function trimText(input) {
    var s = input.value;
    s = s.replace(/(^\s*)|(\s*$)/gi, "");
    s = s.replace(/[ ]{2,}/gi, " ");
    s = s.replace(/\n /, "\n");
    input.value = s;
}

function updateTermForm(response) {
    $("#addTermForm").html(response);

}
