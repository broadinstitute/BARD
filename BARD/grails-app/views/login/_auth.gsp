<g:render template="/layouts/templates/handleOldBrowsers"/>
<!DOCTYPE html>
<html>
<head>
    <title>BioAssay Research Database</title>
    <r:require modules="core,bootstrap,login"/>
    <r:layoutResources/>

</head>

<body>
<div class="container">

    <div>
        <img src="${resource(dir: 'images', file: 'bard_logo_small.png')}" alt="BioAssay Research Database"/>
    </div>
    <br/>

    <g:if env="offline">
        %{--This should only be used for testing. We fall back to our offline way of doing things--}%
        <form action='${postUrl}' method='POST' id='loginForm' autocomplete='off'>

            <g:if test='${flash.message}'>
                <div class='login_message'>
                    <strong>${flash.message}</strong>
                </div>
            </g:if>


            <h2 class="form-signin-heading">Please sign in</h2>

            <input type="text" name='j_username' id='username'
                   placeholder="${message(code: "springSecurity.login.username.label")}">

            <input type="password"
                   placeholder="${message(code: "springSecurity.login.password.label")}" name='j_password' id='password'>

            %{--<label for='remember_me' class="checkbox">--}%
            <input type="checkbox" name='${rememberMeParameter}' id='remember_me'
                   <g:if test='${hasCookie}'>checked='checked'</g:if>>
            <g:message code="springSecurity.login.remember.me.label"/>
            %{--</label>--}%

            <button class="btn btn-medium btn-primary" type="submit">
                <g:message code="bard.springSecurity.login.button"/>
            </button>
        </form>
    </g:if>
    <g:else>
        <p>
            <span class="brand">BARD</span> <strong>offers a convenient way to sign in or create an account.</strong>
        </p>

        <p>
            By logging in, you accept BARD's
            <g:link controller="about" action="termsOfUse" target="_blank">Terms </g:link> and
            <g:link controller="about" action="privacyPolicy" target="_blank">Privacy Policy</g:link>

        </p>

        <a class="btn btn-medium btn-info persona-button persona-orange" title="Sign in with your email"
           id='signin'><img src="${resource(dir: 'images', file: 'email_sign_in_blue.png')}" alt="Sign in with your Email"/></a>
        <br/>
        <br/>

        <div class="btnMessage"><a href="https://login.persona.org/about"
                                   target="_blank">Mozilla Persona</a> is a simple sign-in system from the non-profit behind Firefox
        </div>
    </g:else>
</div>



<r:layoutResources/>

</body>
</html>
