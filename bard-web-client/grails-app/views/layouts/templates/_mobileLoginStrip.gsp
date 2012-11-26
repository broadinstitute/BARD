<div>
    <sec:ifLoggedIn>
        <g:link controller="bardLogout" action="index" data-ajax="false">Logout</g:link>
    </sec:ifLoggedIn>
    <sec:ifNotLoggedIn>
        <g:form name="loginForm" controller="bardLogin">
            Not logged in&nbsp;&nbsp;
            <button type="submit" class="btn btn-primary">Login</button>
        </g:form>
    </sec:ifNotLoggedIn>
</div>
