$(document).ready(function () {

    //handle experiment measures
    var experimentMeasuresLink = "/BARD/experiment/loadExperimentMeasuresAsJSON/" + $("#experimentId").val();

    //load measures for this experiment
    $.ajax(experimentMeasuresLink).done(function (data) {
        root = parseNodes(data);
        $("#result-type-table").html(root);
    });


    $(document).on("click", "a.deleteMeasuresIcon", function (event) {
        event.preventDefault();

        var answer = confirm("Deleting this result type will also delete all of the child nodes.Are you sure you wish to delete this result type?'!")
        if (answer == true) {
            var experimentMeasureDeleteLink = $(this).attr('href');
            $.ajax(experimentMeasureDeleteLink).done(function (data) {
                //reload the current page
                location.reload();
            });
        }

    });
});

function parseNodes(nodes) { // takes a nodes array and turns it into a <ol>
    var ol = document.createElement("ul");
    for (var i = 0; i < nodes.length; i++) {
        ol.appendChild(parseNode(nodes[i]));
    }
    return ol;
}
function createNodeWithEditDeleteIcons(liNode,measureId,addClass,title){
    var editLink = "/BARD/experiment/editMeasure?measureId=" + measureId + "&amp;experimentId=" + $("#experimentId").val();
    var deleteLink = "/BARD/experiment/deleteMeasure?measureId=" + measureId + "&amp;experimentId=" + $("#experimentId").val();
    //only show icons if user is logged in
    var editIcon = '<a class="icon-pencil editMeasuresIcon" title="Click to edit result type" href="' + editLink + '"></a>';
    var deleteIcon = "<a class='icon-trash deleteMeasuresIcon' title='Click to delete result type' href='" + deleteLink + "'></a>";
    if (addClass == 'priority') {
        liNode.innerHTML = "<span><i class='icon-star'></i></span>   " + title + "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" + editIcon + " " + deleteIcon;
    }
    else {
        liNode.innerHTML = title + "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" + editIcon + " " + deleteIcon;
    }
}
function createNodeWithoutEditDeleteIcons(liNode,measureId,addClass,title){
    if (addClass == 'priority') {
        liNode.innerHTML = "<span><i class='icon-star'></i></span>  "  + title;
    }
    else {
        liNode.innerHTML = title;
    }
}
//key: key, title: title, children: children, expand: true, relationship: relationship?.id, measureId: measureId
function parseNode(node) { // takes a node object and turns it into a <li>
    var li = document.createElement("li");

    if(node.username){
        createNodeWithEditDeleteIcons(li,node.measureId,node.addClass,node.title);
    }
    else{
        createNodeWithoutEditDeleteIcons(li,node.measureId,node.addClass,node.title);
    }
    //li.className = node.addClass;
    if (node.children) {
        li.appendChild(parseNodes(node.children));
    }
    return li;
}

