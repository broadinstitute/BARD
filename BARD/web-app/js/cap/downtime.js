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

    /*
     Register a grailsEvents handler for this window, constructor can take a root URL,
     a path to event-bus servlet and options. There are sensible defaults for each argument
     */
    //TODO: Remove hard coded localhost
    var responseLocation = '#downtimeMessage';
    var grailsEvents = new grails.Events("/BARD");
    //grailsEvents.send('saveTodo', data); //will send data to server topic 'saveTodo'
    grailsEvents.on('downTime', function (data) {
        var constructedResponse = '<div class="alert-message block-message warning"><a class="close" href="#">×</a><p style="color:red"><strong>' + data + '</strong></p></div>'
        $(responseLocation).html(constructedResponse);
        console.log(data)
    }, {});


    $.ajax({
        type: 'GET',
        url: '/BARD/downTimeScheduler/currentDownTimeInfo',
        success: function (data) {
             $(responseLocation).html(data);
        }
    });
    //make an ajax call to find the current scheduled downtime

});