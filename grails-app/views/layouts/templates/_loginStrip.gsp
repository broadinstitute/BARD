<div class="span3 offset9">
    <sec:ifLoggedIn>
        <g:form name="logoutForm" controller="logout">
            Welcome Back! Logged in as: <span
                style="font-weight: bold;"><sec:username/></span>&nbsp;&nbsp;
            <button type="submit" class="btn btn-primary">Logout</button>
        </g:form>
    </sec:ifLoggedIn>
    <sec:ifNotLoggedIn>
        <g:form name="loginForm" controller="login">
            Not logged in&nbsp;&nbsp;
            <button type="submit" class="btn btn-primary">Login</button>
        </g:form>
    </sec:ifNotLoggedIn>
</div>
