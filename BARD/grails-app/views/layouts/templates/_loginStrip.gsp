<div id="login-form">
    <sec:ifLoggedIn>
        <g:form name="logoutForm" controller="bardLogout" style="font-weight: bold;color:#000000">
            Logged in as: <span><sec:username/></span>&nbsp;&nbsp;
            <button type="submit" class="btn btn-mini" id="logoutButton" tabindex="6"
                    style="padding: 0 6px; font-size: 10.5px;-webkit-border-radius: 3px;-moz-border-radius: 3px;border-radius: 3px;line-height: 20px;text-align: center;vertical-align: middle;">Logout</button>
        </g:form>
    </sec:ifLoggedIn>
    <sec:ifNotLoggedIn>
        <g:form name="loginForm" controller="bardLogin" action="auth" method="POST">
            <g:hiddenField name="returnToUrl" value="${request.forwardURI}"/>
            Logged in anonymously&nbsp;&nbsp;
            <button id="loginButton" type="submit" class="btn btn-mini" tabindex="6"
                    style="padding: 0 6px; font-size: 10.5px;-webkit-border-radius: 3px;-moz-border-radius: 3px;border-radius: 3px;line-height: 20px;text-align: center;vertical-align: middle;">Sign in</button>
        </g:form>
    </sec:ifNotLoggedIn>
</div>
