$(document).ready(function () {

    //bind show event to accordion
    $('.collapse').on('show', function () {
        var icon = $(this).siblings().find("i.icon-chevron-right");
        icon.removeClass('icon-chevron-right').addClass('icon-chevron-down');
    });

    //bind hide event to accordion
    $('.collapse').on('hide', function () {
        var icon = $(this).siblings().find("i.icon-chevron-down");
        icon.removeClass('icon-chevron-down').addClass('icon-chevron-right');
    });
    $('a.treeNode').on('click',function(){
        var measureId = $(this).attr('id');
        $("#measure-tree").dynatree("getTree").activateKey(measureId);
    });
    $('.dictionary').tooltip();



    //Handle Table sorting
    var table = $("table").stupidtable({

    });

    table.on("beforetablesort", function (event, data) {
        // Apply a "disabled" look to the table while sorting.
        // Using addClass for "testing" as it takes slightly longer to render.
        $("#msg").show().text("Sorting...");
        $("table").addClass("disabled");
    });

    table.on("aftertablesort", function (event, data) {
        // Reset loading message.
        $("#msg").html("&nbsp;").hide();
        $("table").removeClass("disabled");

        var th = $(this).find("th");
        th.find(".icon-arrow-up").remove();
        th.find(".icon-arrow-down").remove();
        var dir = $.fn.stupidtable.dir;

        var arrow = data.direction === dir.ASC ? "up" : "down";
        th.eq(data.column).append('<span class="icon-arrow-' + arrow +'"></span>');
    });
    $(table).find("th").eq(0).click();  // Use the index of the th you want in place of 0
});

