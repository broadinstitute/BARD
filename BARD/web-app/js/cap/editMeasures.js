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

    //handle experiment measures


    //if the experiment id exists
    if ($("#experimentId").length != 0) {
        //it doesn't exist
        var experimentId = $("#experimentId").val();
        if (experimentId.length != 0 && experimentId != 'undefined') {//if there is a value
            var experimentMeasuresLink = bardAppContext + "/experiment/loadExperimentMeasuresAsJSON/" + experimentId;

            //load measures for this experiment
            $.ajax(experimentMeasuresLink, {
                success: function (data) {
                    rootUl = parseNodes(data);
                    var root = document.createElement("div");
                    root.className = "span12";
                    root.appendChild(rootUl);
                    $("#result-type-table").html(root);
                },
                error: handleAjaxError()
            });
        }
    }

    $('a.treeNode').on('click', function () {
        $('.measureHi').removeClass().addClass('span6');
        var colorStr = 'green';
        var measureId = $(this).attr('id');
        $("#e_" + measureId).addClass("measureHi");
        setTimeout(function () {
            $("#e_" + measureId).css("background-color", "#ffffff"); // reset background
            $("#e_" + measureId).effect("highlight", {color: colorStr}, 3000); // animate
        }, 3000);

    });
    $(document).on("click", "a.deleteMeasuresIcon", function (event) {
        event.preventDefault();

        var answer = confirm("Deleting this result type will also delete all of the child nodes.Are you sure you wish to delete this result type?'!")
        if (answer == true) {
            var experimentMeasureDeleteLink = $(this).attr('href');
            $.ajax(experimentMeasureDeleteLink,
                {
                    success: function (data) {
                        //reload the current page
                        location.reload();
                    },
                    error: handleAjaxError()
                }
            );
        }

    });
});

function parseNodes(nodes) { // takes a nodes array and turns it into a <ol>


    var ul = document.createElement("ul");
    for (var i = 0; i < nodes.length; i++) {
        ul.appendChild(parseNode(nodes[i]));
    }
    return ul;
}

function createNodeWithEditDeleteIcons(liNode, measureId, addClass, title) {
    var editLink = bardAppContext + "/experiment/editMeasure?measureId=" + measureId + "&amp;experimentId=" + $("#experimentId").val();
    var deleteLink = bardAppContext + "/experiment/deleteMeasure?measureId=" + measureId + "&amp;experimentId=" + $("#experimentId").val();
    //only show icons if user is logged in
    var editIcon = '<a class="icon-pencil editMeasuresIcon" title="Click to edit result type" href="' + editLink + '"></a>';
    var deleteIcon = "<a class='icon-trash deleteMeasuresIcon' title='Click to delete result type' href='" + deleteLink + "'></a>";
    if (addClass == 'priority') {
        liNode.innerHTML = "<div id=e_" + measureId + " class='span6'>" + "<i class='icon-star'></i></span>   " + title +
            "</div><div class='span6'>" + editIcon + " " + deleteIcon + "</div>";
    }
    else {
        liNode.innerHTML = "<div id=e_" + measureId + " class='span6'>" + title + "</div><div class='span6'>" +
            editIcon + " " + deleteIcon + "</div>";
    }
}
function createNodeWithoutEditDeleteIcons(liNode, measureId, addClass, title) {
    if (addClass == 'priority') {
        liNode.innerHTML = "<span><i class='icon-star'></i></span>  " + title;
    }
    else {
        liNode.innerHTML = title;
    }
}
//key: key, title: title, children: children, expand: true, relationship: relationship?.id, measureId: measureId
function parseNode(node) { // takes a node object and turns it into a <li>
    var li = document.createElement("li");
    if (node.username) {
        createNodeWithEditDeleteIcons(li, node.measureId, node.addClass, node.title);
    }
    else {
        createNodeWithoutEditDeleteIcons(li, node.measureId, node.addClass, node.title);
    }
    if (node.children) {
        li.appendChild(parseNodes(node.children));
    }
    return li;
}

