//TODO: Use Polyfill to handle hashchange events
var supportsOnHashChange = false;
if ("onhashchange" in window) {
    supportsOnHashChange = true;
}
function toggleCollapse() {
     //Common
    if ($('#target-info').hasClass('in')) {
        $('#target-info').collapse('hide');
    }
    if ($('#publication-info').hasClass('in')) {
        $('#publication-info').collapse('hide');
    }

    //ShowProject
    if ($('#annotations-info').hasClass('in')) {
        $('#annotations-info').collapse('hide');
    }
    if ($('#description-info').hasClass('in')) {
        $('#description-info').collapse('hide');
    }
    if ($('#experiments-info').hasClass('in')) {
        $('#experiments-info').collapse('hide');
    }
    if ($('#assays-info').hasClass('in')) {
        $('#assays-info').collapse('hide');
    }

    //ShowAssay
    if ($('#document-info').hasClass('in')) {
        $('#document-info').collapse('hide');
    }
    if ($('#result-info').hasClass('in')) {
        $('#result-info').collapse('hide');
    }
    if ($('#assay-bio-info').hasClass('in')) {
        $('#assay-bio-info').collapse('hide');
    }

}
function locationHashChanged() {
    if (supportsOnHashChange) {
        //common
        if (location.hash === "#target-info") {
            if (!$('#target-info').hasClass('in')) {
                toggleCollapse();
                $('#target-info').trigger('click');
            }

        }
        if (location.hash === "#publication-info") {
            if (!$('#publication-info').hasClass('in')) {
                toggleCollapse();
                $('#publication-info').trigger('click');
            }
        }

        //showAssay
        if (location.hash === "#assay-bio-info") {
            //if this is the active window do nothing
            if (!$('#assay-bio-info').hasClass('in')) {
                toggleCollapse();
                $('#assay-bio-info').trigger('click');
            }
        }
        if (location.hash === "#document-info") {
            if (!$('#document-info').hasClass('in')) {
                toggleCollapse();
                $('#document-info').trigger('click');
            }

        }
        if (location.hash === "#result-info") {
            if (!$('#result-info').hasClass('in')) {
                toggleCollapse();
                $('#result-info').trigger('click');
            }
        }

        //showProject
        if (location.hash === "#assays-info") {
            //if this is the active window do nothing
            if (!$('#assays-info').hasClass('in')) {
                toggleCollapse();
                $('#assays-info').trigger('click');
            }
        }
        if (location.hash === "#experiments-info") {
            if (!$('#experiments-info').hasClass('in')) {
                toggleCollapse();
                $('#experiments-info').trigger('click');
            }

        }
        if (location.hash === "#description-info") {
            if (!$('#description-info').hasClass('in')) {
                toggleCollapse();
                $('#description-info').trigger('click');
            }
        }
        if (location.hash === "#annotations-info") {
            if (!$('#annotations-info').hasClass('in')) {
                toggleCollapse();
                $('#annotations-info').trigger('click');
            }
        }
    }
}

window.onhashchange = locationHashChanged;
$(document).ready(function () {
    $("#accordion").accordion({ autoHeight:false });
    $('.projectTooltip').tooltip();
    $('.collapse').on('show', function () {
        var icon = $(this).siblings().find("i.icon-chevron-right");
        icon.removeClass('icon-chevron-right').addClass('icon-chevron-down');
    });
    $('.collapse').on('hide', function () {
        var icon = $(this).siblings().find("i.icon-chevron-down");
        icon.removeClass('icon-chevron-down').addClass('icon-chevron-right');
    });
    $(".collapse").click(function () {
        //find all with class and toggle
        $(this).collapse('show')
    });
    //Trigger this event just in case someone bookmarks anny of the accordions
    $(window).trigger('hashchange');
}) ;


