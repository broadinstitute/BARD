<div>
    <sec:ifLoggedIn>
        <g:form name="logoutForm" controller="bardLogout">
            Logged in as: <span
                style="font-weight: bold;"><sec:username/></span>&nbsp;&nbsp;
            <button type="submit" class="btn btn-small" id="logoutButton">Logout</button>
        </g:form>
    </sec:ifLoggedIn>
    <sec:ifNotLoggedIn>
        <g:form name="loginForm" controller="bardLogin">
            Not logged in&nbsp;&nbsp;
            <button type="submit" class="btn btn-primary">Login</button>
        </g:form>
    </sec:ifNotLoggedIn>
</div>