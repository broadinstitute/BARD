<!DOCTYPE html>
<html>
<head>
    <title>BioAssay Research Database</title>
    <r:require modules="core,bootstrap"/>
    <r:layoutResources/>

    <style type="text/css">
    body {
        padding-top: 40px;
        padding-bottom: 40px;
        background-color: #f5f5f5;
    }

    .form-signin {
        max-width: 300px;
        padding: 19px 29px 29px;
        margin: 0 auto 20px;
        background-color: #fff;
        border: 1px solid #e5e5e5;
        -webkit-border-radius: 5px;
        -moz-border-radius: 5px;
        border-radius: 5px;
        -webkit-box-shadow: 0 1px 2px rgba(0, 0, 0, .05);
        -moz-box-shadow: 0 1px 2px rgba(0, 0, 0, .05);
        box-shadow: 0 1px 2px rgba(0, 0, 0, .05);
    }

    .form-signin .form-signin-heading,
    .form-signin .checkbox {
        margin-bottom: 10px;
    }

    .form-signin input[type="text"],
    .form-signin input[type="password"] {
        font-size: 16px;
        height: auto;
        margin-bottom: 15px;
        padding: 7px 9px;
    }

    </style>

    <r:script type='text/javascript'>
        $(document).ready(function () {
            $('input[name="j_username"]').focus();
        });
    </r:script>
</head>

<body>
<div class="container">

    <form class="form-signin" action='${postUrl}' method='POST' id='loginForm' autocomplete='off'>

        <div style="text-align: center;">
            <img src="${resource(dir: 'images', file: 'bard_logo_small.png')}" alt="BioAssay Research Database"/>
        </div>
        <br>

        <g:if test='${flash.message}'>
            <div class='login_message'>
                <strong>${flash.message}</strong>
            </div>
        </g:if>


        <h2 class="form-signin-heading">Please sign in</h2>

        <input type="text" class="input-block-level" name='j_username' id='username'
               placeholder="${message(code: "springSecurity.login.username.label")}">

        <input type="password" class="input-block-level"
               placeholder="${message(code: "springSecurity.login.password.label")}" name='j_password' id='password'>

        <label for='remember_me' class="checkbox">
            <input type="checkbox" name='${rememberMeParameter}' id='remember_me'
                   <g:if test='${hasCookie}'>checked='checked'</g:if>>
            <g:message code="springSecurity.login.remember.me.label"/>
        </label>

        <button class="btn btn-large btn-primary" type="submit">
            <g:message code="springSecurity.login.button"/>
        </button>
    </form>
</div> <!-- /container -->

<r:layoutResources/>

</body>
</html>
