/**
 * Created with IntelliJ IDEA.
 * User: gwalzer
 * Date: 11/26/12
 * Time: 3:29 PM
 * Loads after jQuery dependencies but before jquery-mobile. Used to initialize jquery-mobile defaults.
 */

//JQuery Mobile's default setting change
$(document).bind("mobileinit", function () {
    //apply overrides here
//    $.mobile.defaultPageTransition = 'none'
    $.mobile.ajaxEnabled = false
    $.mobile.pushStateEnabled = false
});
