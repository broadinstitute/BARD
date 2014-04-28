%{-- Copyright (c) 2014, The Broad Institute
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
    * Redistributions of source code must retain the above copyright
      notice, this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright
      notice, this list of conditions and the following disclaimer in the
      documentation and/or other materials provided with the distribution.
    * Neither the name of The Broad Institute nor the
      names of its contributors may be used to endorse or promote products
      derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL The Broad Institute BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 --}%

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
