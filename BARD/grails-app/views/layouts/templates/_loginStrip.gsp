<div id="login-form">
    <sec:ifLoggedIn>
        <g:form name="logoutForm" controller="bardLogout" style="font-weight: bold;color:#000000">
            Logged in as: <span><sec:username/></span>&nbsp;&nbsp;
            <button type="submit" class="btn btn-mini" id="logoutButton">Logout</button>
        </g:form>
    </sec:ifLoggedIn>
    <sec:ifNotLoggedIn>
        <g:form name="loginForm" controller="bardLogin">
            <button type="submit" class="btn btn-mini">Sign in</button>
        </g:form>
    </sec:ifNotLoggedIn>
</div>
