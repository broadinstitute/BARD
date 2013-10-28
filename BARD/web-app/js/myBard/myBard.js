$(document).ready(function () {
    $(".myBard").tablesorter({
        headers: {
            0: { sorter: "digit"  },
            3: { sorter: "shortDate"  }
        },
        sortList: [
            [0, 0],
            [2, 0]
        ]
    }).tablesorterPager({container: $("#pager")});


    $("#myExperiments").tablesorter({
        headers: {
            0: { sorter: "digit"  },
            3: { sorter: "digit"  },
            4: { sorter: "shortDate"  }
        },
        sortList: [
            [3, 0],
            [0, 0]
        ]
    }).tablesorterPager({container: $("#pager")});
    $("#overlay").hide();

    //Sorting message
    $(".tablesorter").bind("sortStart",function () {
        $("#overlay").show();
    }).bind("sortEnd", function () {
            $("#overlay").hide();
        });

});