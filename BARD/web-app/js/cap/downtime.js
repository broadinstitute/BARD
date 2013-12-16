$(document).ready(function () {

    $("#downtimenotify").notify({
        speed: 500,
        expires: false
    });
    execPoll();
    poll();
});
//Poll every 10 minutes
function poll() {
    setTimeout(execPoll, 600000);
}
function execPoll() {
    $.ajax({
        type: 'GET',
        url: bardAppContext + '/downTimeScheduler/currentDownTimeInfo',
        success: function (data) {
            if (data) {
                //Clear it out if it already exist
                $("#downtimenotify").html("");
                $("#downtimenotify").notify("create", {
                    title: "Down Time Notification!!",
                    text: data
                });
            }
        },
        complete: poll
    });
}