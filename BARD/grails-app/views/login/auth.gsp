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

<!DOCTYPE html>
<g:render template="/layouts/templates/handleOldBrowsers"/>
<html>
<head>
    <title>BioAssay Research Database</title>
    <meta http-equiv="X-UA-Compatible" content="IE=edge"/>
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
        <span class="brand">BARD</span> <strong>offers a convenient way to sign in or create an account</strong> using any existing email address.
    </p>

    <p>
        By logging in, you accept BARD's
        <g:link controller="about" action="termsOfUse" target="_blank">Terms</g:link> and
        <g:link controller="about" action="privacyPolicy" target="_blank">Privacy Policy</g:link>.  If you do not already
        have a BARD account, one will be created automatically for you when you've successfully authenticated.
    </p>

    <p>
    <a class="persona-button persona-orange" title="Sign in with your email"
       id='signin' href="#"><img src="${resource(dir: 'images', file: 'email_sign_in_blue.png')}"
                        alt="Sign in with your Email"/></a>
    </p>

    <div class="btnMessage"><a href="https://login.persona.org/about"
                               target="_blank">Mozilla Persona</a> is a simple sign-in system from the non-profit behind Firefox
    </div>

    <input id="returnToUrl" value="${returnToUrl}" type="hidden">
</div>

<r:script>
    var signinLink = document.getElementById('signin');

    var request;
    var returnToUrl = $("#returnToUrl").val();

    if (window.location.href.match("^https")) {
        request = {
            siteName: 'BioActivity Research Database',
            siteLogo: bardAppContext + '/images/bard_logo_small.png',   //requires https
            termsOfService: bardAppContext + '/about/termsOfUse', //requires https
            privacyPolicy: bardAppContext + '/about/privacyPolicy', //requires https
            returnTo: returnToUrl
        };
    } else {
        request = {
            siteName: 'BioActivity Research Database',
            returnTo: returnToUrl
        };
    }

    signinLink.onclick = function () {
        navigator.id.request(request);
    };

</r:script>

<r:layoutResources/>

</body>
</html>
