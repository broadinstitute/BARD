// Mozilla Persona Stuff

var signinLink = document.getElementById('signin');
if (signinLink) {
    signinLink.onclick = function() {
        navigator.id.request(); };
}

var signoutLink = document.getElementById('signout');
if (signoutLink) {
    signoutLink.onclick = function() { navigator.id.logout(); };
}
var currentUser = null;

navigator.id.watch({
    loggedInUser: currentUser,
    onlogin: function(assertion) {
        if (sessionStorage['currentUser'] != undefined) {
            return;
        }
        $.ajax({
            type: 'POST',
            url: '/BARD/personaLogin', // This is a URL on your website.
            data: {assertion: assertion},
            success: function(res, status, xhr) {
                sessionStorage['currentUser'] = res;
                window.location.reload();
            },
            error: function(xhr, status, err) {
                navigator.id.logout();
            }
        });
    },
    onlogout: function() {
        // A user has logged out! Here you need to:
        // Tear down the user's session by redirecting the user or making a call to your backend.
        // Also, make sure loggedInUser will get set to null on the next page load.
        // (That's a literal JavaScript null. Not false, 0, or undefined. null.)
        $.ajax({
            type: 'POST',
            url: '/BARD/personaLogin', // This is a URL on your website.
            success: function(res, status, xhr) {
                sessionStorage.removeItem('currentUser');
                window.location.reload();
            },
            error: function(xhr, status, err) { console.log("Logout failure: " + err); }
        });
    }
});