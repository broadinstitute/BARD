//$(document).ready(function () {
//    var grailsEvents = new grails.Events("http://localhost:8080/BARD/");
//   // grailsEvents.send('saveTodo', data); //will send data to server topic 'saveTodo'
//  //  grailsEvents.on('savedTodo', function(data){...}); //will listen for server events on 'savedTodo' topic
//
//    /*
//     Register a grailsEvents handler for this window, constructor can take a root URL,
//     a path to event-bus servlet and options. There are sensible defaults for each argument
//     */
//  //  window.grailsEvents = new grails.Events("${createLink(uri: '')}", {transport:'websocket'});
//    grailsEvents.on('savedTodo', function (data) { //subscribe to down time event scheduler
//        alert("Here");
//        console.log(data);
//        $("#downtimeMessage").html(data);
//    }, {});
//});
$(document).ready(function () {
    $("#downtimenotify").notify({
        speed: 500,
        expires: false
    });
    /*
     Register a grailsEvents handler for this window, constructor can take a root URL,
     a path to event-bus servlet and options. There are sensible defaults for each argument
     */
    var grailsEvents = new grails.Events("/BARD");
    grailsEvents.on('downTime', function (data) {
        buildDownTimeDiv(data);
    }, {});


    $.ajax({
        type: 'GET',
        url: '/BARD/downTimeScheduler/currentDownTimeInfo',
        success: function (data) {
            if (data) {   //If there is data then insert it. Otherwise do nothing.
                buildDownTimeDiv(data);
            }

        }
    });
    //make an ajax call to find the current scheduled downtime

    function buildDownTimeDiv(data) {
        $("#downtimenotify").notify("create", {
            title: "Down Time Notification!!",
            text: data
        });
    }

});