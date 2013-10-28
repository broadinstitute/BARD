$(document).ready(function () {

    var table = $("table").stupidtable({

    });
    //var table = $("#mytable").stupidtable();

    table.on("beforetablesort", function (event, data) {
        // Apply a "disabled" look to the table while sorting.
        // Using addClass for "testing" as it takes slightly longer to render.
        $("#msg").text("Sorting...");
        $("table").addClass("disabled");
    });

    table.on("aftertablesort", function (event, data) {
        // Reset loading message.
        $("#msg").html("&nbsp;");
        $("table").removeClass("disabled");

        var th = $(this).find("th");
        th.find(".arrow").remove();
        var dir = $.fn.stupidtable.dir;

        var arrow = data.direction === dir.ASC ? "&uarr;" : "&darr;";
        th.eq(data.column).append('<span class="arrow">' + arrow +'</span>');
    });
    $(table).find("th").eq(0).click();  // Use the index of the th you want in place of 0
});