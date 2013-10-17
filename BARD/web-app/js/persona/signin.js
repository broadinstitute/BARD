// Mozilla Persona Stuff

$(document).ready(function () {

    var href = window.location.href;


    var signinLink = document.getElementById('signin');

    if (signinLink) {
        if (href.match("^https")) {
            signinLink.onclick = function () {
                navigator.id.request({
                    siteName: 'BioActivity Research Database',
                    siteLogo: '/BARD/images/bard_logo_small.png',   //requires https
                    termsOfService: '/BARD/about/termsOfUse', //requires https
                    privacyPolicy: '/BARD/about/privacyPolicy' //requires https
                });
            };
        } else {
            signinLink.onclick = function () {
                navigator.id.request({
                    siteName: 'BioActivity Research Database'
                });
            };
        }

    }

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
            if (sessionStorage['currentUser'] != undefined) {
                return;
            }
            $.ajax({
                type: 'POST',
                url: '/BARD/j_spring_persona_security_check', // This is a URL on your website.
                data: {assertion: assertion},
                success: function (res, status, xhr) {
                    sessionStorage['currentUser'] = res;
                    window.location.reload();
                },
                error: function (xhr, status, err) {
                    navigator.id.logout();
                }
            });
        },
        onlogout: function () {
            sessionStorage.removeItem('currentUser');
            currentUser = null;
        }
    });
});