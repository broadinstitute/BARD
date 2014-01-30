// Mozilla Persona Stuff

function forgetCurrentUser() {
    sessionStorage.removeItem('currentUser');
}
$(document).ready(function () {

    var href = window.location.href;

    var signoutLink = document.getElementById('logoutButton');
    if (signoutLink) {
        signoutLink.onclick = function () {
            navigator.id.logout();
        };
    }
    var currentUser = null;

    navigator.id.watch({
        loggedInUser: currentUser,
        onlogin: function (assertion) {
            console.log("onlogin");
            if (sessionStorage['currentUser'] != undefined) {
                console.log("currentUser != undefined");
                return;
            }
            $.ajax({
                type: 'POST',
                url: bardAppContext + '/j_spring_persona_security_check', // This is a URL on your website.
                data: {assertion: assertion},
                success: function (res, status, xhr) {
                    sessionStorage['currentUser'] = res;

                    var returnToUrl = $("#returnToUrl").val();
                    if(returnToUrl) {
                        // redirect browser to the page we logged in from
                        window.location.pathname = returnToUrl;
                    } else {
                        window.location.reload();
                    }
                },
                error: function (xhr, status, err) {
                    navigator.id.logout();
                }
            });
        },
        onlogout: function () {
            forgetCurrentUser();
            currentUser = null;
        }
    });
});

