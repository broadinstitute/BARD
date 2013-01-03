/**
 * Collapse all open accordions, so that we have only one active at any time
 * @param currentHref
 */
function toggleCollapse(currentHref) {
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

    if ($('#assays-info').hasClass('in')) {
        $('#assays-info').collapse('hide');
    }

    //ShowAssay
    if ($('#document-info').hasClass('in')) {
        $('#document-info').collapse('hide');
    }
    if ($('#result-info').hasClass('in')) { //the result accordion is open
        descriptionHasClass = false;
        if (currentHref.indexOf('accordionDescriptionContent_') > -1) {//the current clicked node is a description node
            descriptionHasClass = true;
            $('.resultsdescriptions').each(function () {  //if any of the description nodes is already open, then close them
                if ($(this).hasClass('in')) {
                    $(this).collapse('hide');

                }
            });
        }
        if (!descriptionHasClass) { //Only close this if all children are closed
            $('#result-info').collapse('hide');
        }

    }
    if ($('#experiments-info').hasClass('in')) {
        //Note that description nodes are embedded inside the experiment node
        descriptionExptHasClass = false;
        if (currentHref.indexOf('accordionDescriptionContent_') > -1) {//the current clicked node is a description node
            descriptionExptHasClass = true;
            $('.resultsdescriptions').each(function () {  //if any of the description nodes is already open, then close them
                if ($(this).hasClass('in')) {
                    $(this).collapse('hide');

                }
            });
        }
        if (!descriptionExptHasClass) { //Only close this if all children are closed
            $('#experiments-info').collapse('hide');
        }
    }
    if ($('#assay-bio-info').hasClass('in')) {
        $('#assay-bio-info').collapse('hide');
    }

}
/**
 * Find the current open accordion and close it
 *
 * Note that we are binding both the showAssay and showProject accordions
 * @param returnLocation
 */
function locationHashChanged(returnLocation) {
    //Common to both pages
    if (returnLocation.hash === "#target-info") {
        if (!$('#target-info').hasClass('in')) {
            toggleCollapse(returnLocation.href);
            $('#target-info').trigger('click');
        }

    }
    if (returnLocation.hash === "#publication-info") {
        if (!$('#publication-info').hasClass('in')) {
            toggleCollapse(returnLocation.href);
            $('#publication-info').trigger('click');
        }
    }

    //showAssay
    if (returnLocation.hash === "#assay-bio-info") {
        //if this is the active window do nothing
        if (!$('#assay-bio-info').hasClass('in')) {
            toggleCollapse(returnLocation.href);
            $('#assay-bio-info').trigger('click');
        }
    }
    if (returnLocation.hash === "#document-info") {
        if (!$('#document-info').hasClass('in')) {
            toggleCollapse(returnLocation.href);
            $('#document-info').trigger('click');
        }

    }
    if (returnLocation.hash === "#result-info") {
        if (!$('#result-info').hasClass('in')) {
            toggleCollapse(returnLocation.href);
            $('#result-info').trigger('click');
        }
    }

    //showProject
    if (returnLocation.hash === "#assays-info") {
        //if this is the active window do nothing
        if (!$('#assays-info').hasClass('in')) {
            toggleCollapse(returnLocation.href);
            $('#assays-info').trigger('click');
        }
    }
    if (returnLocation.hash === "#experiments-info") {
        if (!$('#experiments-info').hasClass('in')) {
            toggleCollapse(returnLocation.href);
            $('#experiments-info').trigger('click');
        }

    }
    if (returnLocation.hash === "#description-info") {
        if (!$('#description-info').hasClass('in')) {
            toggleCollapse(returnLocation.href);
            $('#description-info').trigger('click');
        }
    }
    if (returnLocation.hash === "#annotations-info") {
        if (!$('#annotations-info').hasClass('in')) {
            toggleCollapse(returnLocation.href);
            $('#annotations-info').trigger('click');
        }
    }
    if (returnLocation.hash.startsWith('#accordionDescriptionContent_')) {
        //then this is a description node
        toggleCollapse(returnLocation.href);
        $(returnLocation.hash).trigger('click');

    }

}
$(document).ready(function () {

    // function for the reference is processed when you click on the link
    function handlerAnchors() {
        // keep the link in the browser history
        toggleCollapse(this.href);
        history.pushState(null, null, this.href);
        // do not give a default action
        return false;
    }

    // looking for all the links with class accordion-toggle
    var anchors = $('a.accordion-toggle');

    //bind click events
    for (var j = 0; j < anchors.length; j++) {
        anchors[ j ].onclick = handlerAnchors;
    }

    //find all anchors with  resultsdescriptions class
    anchors = $('a.resultsdescriptions');
    // hang on the event, all references in this document
    for (var i = 0; i < anchors.length; i++) {
        anchors[ i ].onclick = handlerAnchors;
    }

    // hang on popstate event triggered by pressing back/forward in browser
    window.onpopstate = function (event) {

        // we get a normal Location object

        /*
         * Note, this is the only difference when using this library,
         * because the object document.location cannot be overriden,
         * so library the returns generated "location" object within
         * an object window.history, so get it out of "history.location".
         * For browsers supporting "history.pushState" get generated
         * object "location" with the usual "document.location".
         */
        var returnLocation = history.location || document.location;
        locationHashChanged(returnLocation);
    };

    $("#accordion").accordion({ autoHeight:false });
    $('.projectTooltip').tooltip();

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
    //bind click event to accordion
    $(".collapse").click(function () {
        //find all with class and toggle
        $(this).collapse('show');
    });
    //Trigger this event just in case someone bookmarks any of the accordions
    // $(window).trigger('hashchange');
    var returnLocation = history.location || document.location;
    locationHashChanged(returnLocation);

});


