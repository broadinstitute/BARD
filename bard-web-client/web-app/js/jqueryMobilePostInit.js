/**
 * Created with IntelliJ IDEA.
 * User: gwalzer
 * Date: 11/26/12
 * Time: 3:29 PM
 * Called AFTER jquery-mobile has been loaded.
 */

$(document).bind('pageinit', function () {
    //apply initialization logic here
});

$(document).bind('pageload', function () {
    //apply initialization logic here
});

//Classic JQuery init
$(document).ready(turnoffPageLoadingSpinningWheel);

//Remove the page-loading spinning wheal
function turnoffPageLoadingSpinningWheel() {
    $.mobile.hidePageLoadingMsg()
}