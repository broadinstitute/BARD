$(document).ready(function () {
    createHierarchyTree("#element-hierarchy-tree", "BARD Hierarchy Tree", "BARD", null);
    createHierarchyTree("#dictionary-element-hierarchy-tree", "BARD Dictionary Tree", "BARD Dictionary", null);


    var attributeSelect2 = new DescriptorSelect2('#attributeElementId', 'Search for attribute name',{results: []});
    $.ajax("/BARD/ontologyJSon/getAttributeDescriptors").done(function (data) {
        attributeSelect2.initSelect2(data);
    });
    $("#attributeElementId").on("change", function (e) {
        // based on the attribute selected only show the appropriate value widgets
        var selectedData = $("#attributeElementId").select2("data");
        $('#parentDescription').attr('value', selectedData.description);
        validateAddToChildren(selectedData);

    }).on("select2-highlight", function(e) {
            attributeSelect2.updateSelect2DescriptionPopover(e.choice);
    });
});

//Validate that the element can be used as a parent-element for a newly proposed element (element.addChildMethod==DIRECT)
function validateAddToChildren(data) {
    if ("DIRECT" === data.addChildMethod) {
        $('#attributeElementErrorField').empty();
        $('#nextBtn').attr("disabled", false);//enable the NEXT button with a successful selection
        $('#nextBtn').focus();
    }
    else {
        $('#attributeElementErrorField').html('<p class="text-error"><i class="icon-exclamation-sign"></i> That term is not allowed to be used as a parent</p>');
    }
}

function reloadTree() {
    var doNotShowRetired = $("#doNotShowRetiredTerms").is(':checked');
    $("#element-hierarchy-tree").dynatree("option", "initAjax", {
        url: "/BARD/element/buildTopLevelHierarchyTree",
        data: {doNotShowRetired: doNotShowRetired, treeRoot: "BARD"}
    });

    $("#element-hierarchy-tree").dynatree("option", "onLazyRead", function (node) {
        node.appendAjax(
            {
                url: "/BARD/element/getChildrenAsJson",
                dataType: "json",
                data: {elementId: node.data.elementId, doNotShowRetired: doNotShowRetired}
            }
        )
    });
    $("#element-hierarchy-tree").dynatree("getTree").reload();

    $("#saveTerm")[0].reset();
}

/**
 * Build the DYNATREE tree using ajax calls to the ElementController.
 *
 * @param treeElementName
 * @param treeTitle
 * @param treeRoot This is used in CAP's Element table to get to the root of the tree; from the root we start loading the children elements.
 * @param expectedValueType Filter based on the element type (e.g., 'element', 'numeric', etc.
 */
function createHierarchyTree(treeElementName, treeTitle, treeRoot, expectedValueType) {
    var doNotShowRetired = $("#doNotShowRetiredTerms").is(':checked');

    $(treeElementName).dynatree
    (
        {
            title: treeTitle,
            autoFocus: false,
            initAjax: {
                url: "/BARD/element/buildTopLevelHierarchyTree",
                data: {doNotShowRetired: doNotShowRetired, treeRoot: treeRoot, expectedValueType: expectedValueType}
            },
            onActivate: function (node) {
                $("#attributeElementId").select2("data", {id: node.data.elementId, text: node.data.title, addChildMethod: node.data.childMethod});
                $("#parentDescription").val(node.data.description);
                $("#attributeElementId").trigger("change");//trigger the 'change' event on the select2 container

                if (node.data.childMethod == 'DIRECT') {
                    //make fields writable
                    $("#nextBtn").attr("disabled", false);
                }
                else {
                    //make all fields readonly
                    $("#nextBtn").attr("disabled", true);

                }
            },
            onLazyRead: function (node) {
                node.appendAjax
                (
                    {
                        url: "/BARD/element/getChildrenAsJson",
                        dataType: "json",
                        data: {elementId: node.data.elementId, doNotShowRetired: doNotShowRetired, expectedValueType: expectedValueType}

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
}

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
    var win = window.open("", "_self");
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
    var attributeElementId = $("#attributeElementId").val();
    if (attributeElementId) {
        try {//If the tree exists, activate the selected attribute
            $("#element-hierarchy-tree").dynatree("getTree").activateKey(attributeElementId);
        }
        catch(e) {}

        try {//If the tree exists, activate the selected attribute
        $("#dictionary-element-hierarchy-tree").dynatree("getTree").activateKey(attributeElementId);
        }
        catch(e) {}
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
