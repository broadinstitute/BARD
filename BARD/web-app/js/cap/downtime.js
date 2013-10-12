$(document).ready(function () {
    $("#downtimenotify").notify({
        speed: 500,
        expires: false
    });
    /*
     Register a grailsEvents handler for this window, constructor can take a root URL,
     a path to event-bus servlet and options. There are sensible defaults for each argument
     */
//    var grailsEvents = new grails.Events("/BARD");
//    grailsEvents.on('downTime', function (data) {
//        buildDownTimeDiv(data);
//    });
//
//
//    $.ajax({
//        type: 'GET',
//        url: '/BARD/downTimeScheduler/currentDownTimeInfo',
//        success: function (data) {
//            buildDownTimeDiv(data);
//
//        }
//    });
    execPoll();
    //make an ajax call to find the current scheduled downtime

//    function buildDownTimeDiv(data) {
//        if (data) {
//            $("#downtimenotify").notify("create", {
//                title: "Down Time Notification!!",
//                text: data
//            });
//        }
//    }

});
//Poll every 10 minutes
function poll() {
    setTimeout(execPoll, 600000);
}
function execPoll() {
    $.ajax({
        type: 'GET',
        url: '/BARD/downTimeScheduler/currentDownTimeInfo',
        success: function (data) {
            if (data) {
                $("#downtimenotify").notify("create", {
                    title: "Down Time Notification!!",
                    text: data
                });
            }
        },
        complete: poll
    });
}