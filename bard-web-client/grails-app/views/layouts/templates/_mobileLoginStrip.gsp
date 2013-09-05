<div>
    <sec:ifLoggedIn>
        <div style="text-align: right;">
            <g:link controller="bardLogout" action="index" data-ajax="false" data-role="button" class="ui-btn-right"
                    data-inline="true" data-theme="b" data-mini="true">Logout</g:link>
        </div>
    </sec:ifLoggedIn>
    <sec:ifNotLoggedIn>
        <g:form name="loginForm" controller="bardLogin">
            Not logged in&nbsp;&nbsp;
            <button type="submit" class="btn btn-primary">Login</button>
        </g:form>
    </sec:ifNotLoggedIn>
</div>
