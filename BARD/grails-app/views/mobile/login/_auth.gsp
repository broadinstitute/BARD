<%--
  Created by IntelliJ IDEA.
  User: gwalzer
  Date: 11/19/12
  Time: 3:33 PM
  To change this template use File | Settings | File Templates.
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>Login Page</title>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <r:require modules="jqueryMobile"/>
    <r:layoutResources/>
    <r:script type='text/javascript'>
        $.mobile.ajaxEnabled = false;

        //Do not use AJAX for the login page
        $(document).ready(function () {
            $.mobile.ajaxEnabled = false;
        });

        (function () {
            document.forms['loginForm'].elements['j_username'].focus();
        })();
    </r:script>

</head>

<body>

<div data-role="page">
    <div class="right-aligned">
        <g:render template="/layouts/templates/loginStrip"/>
    </div>

    <div data-role="header">
        <div class='fheader'><g:message code="springSecurity.login.header"/></div>
    </div><!-- /header -->

    <g:if test='${flash.message}'>
        <div class='login_message'>${flash.message}</div>
    </g:if>

    <form action='${postUrl}' method='POST' id='loginForm' class='cssform' autocomplete='off' data-ajax=”false”>

        <label for='username'><g:message code="springSecurity.login.username.label"/>:</label>
        <input type='text' class='text_' name='j_username' id='username' value=""/>

        <label for='password'><g:message code="springSecurity.login.password.label"/>:</label>
        <input type='password' class='text_' name='j_password' id='password'/>

        <label for='remember_me'><g:message code="springSecurity.login.remember.me.label"/></label>
        <input type='checkbox' class='chk' name='${rememberMeParameter}' id='remember_me'
               <g:if test='${hasCookie}'>checked='checked'</g:if>/>

        <input type='submit' id="submit" value='${message(code: "springSecurity.login.button")}'/>
    </form>
</div><!-- /page -->
<r:layoutResources/>
</body>
</html>