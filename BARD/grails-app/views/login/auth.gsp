<!DOCTYPE html>
<g:render template="/layouts/templates/handleOldBrowsers"/>
<html>
<head>
    <title>BioAssay Research Database</title>
    <r:require modules="core,bootstrap,login"/>
    <r:script disposition='head'>
        window.bardAppContext = "${request.contextPath}";
    </r:script>
    <r:layoutResources/>
</head>

<body>
<div class="container">

    <div>
        <a class="brand" href="${request.contextPath}">
            <img src="${resource(dir: 'images', file: 'bard_logo_small.png')}" alt="BioAssay Research Database"/>
        </a>
    </div>
    <br/>

    <p>
        <span class="brand">BARD</span> <strong>offers a convenient way to sign in or create an account.</strong> By
        signing into BARD with Persona, a BARD account will automatically be created if you do not already have a
        BARD account.
    </p>

    <p>
        By logging in, you accept BARD's
        <g:link controller="about" action="termsOfUse" target="_blank">Terms</g:link> and
        <g:link controller="about" action="privacyPolicy" target="_blank">Privacy Policy</g:link>.

    </p>

    <a class="btn btn-medium btn-info persona-button persona-orange" title="Sign in with your email"
       id='signin'><img src="${resource(dir: 'images', file: 'email_sign_in_blue.png')}"
                        alt="Sign in with your Email"/></a>
    <br/>
    <br/>

    <div class="btnMessage"><a href="https://login.persona.org/about"
                               target="_blank">Mozilla Persona</a> is a simple sign-in system from the non-profit behind Firefox
    </div>
</div>



<r:layoutResources/>

</body>
</html>
