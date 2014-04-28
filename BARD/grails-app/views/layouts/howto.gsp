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

<%@ page import="bardqueryapi.IDSearchType" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>BARD: <g:layoutTitle default="BioAssay Research Database"/></title>

    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <link rel="shortcut icon" href="${resource(dir: 'images', file: 'favicon.ico')}" type="image/x-icon">
    <link href='https://fonts.googleapis.com/css?family=Lato:400,400italic,700,700italic,900,900italic,300,300italic'
          rel='stylesheet' type='text/css'>
    <r:script disposition='head'>
        window.bardAppContext = "${request.contextPath}";
    </r:script>
    <r:require modules="bardHomepage,idSearch,jquerynotifier,downtime,autocomplete"/>
    <!--[if lt IE 9]><link rel="stylesheet" href="${resource(dir: 'css/bardHomepage', file: 'ieBardHomepage.css')}" media="screen" /><![endif]-->
    <!--[if IE]><script src="${resource(dir: 'js/bardHomepage', file: 'ie.js')}" /></script><![endif]-->

    <g:layoutHead/>

    <noscript>
        <a href="http://www.enable-javascript.com/" target="javascript">
            <img src="${resource(dir: 'images', file: 'enable_js.png')}"
                 alt="Please enable JavaScript to access the full functionality of this site."/>
        </a>
    </noscript>
    <r:layoutResources/>

    <style type="text/css">
    @media (min-width: 768px) {
        /* start of modification for 5 columns.  Must follow after bootstrap definitions */
        .fivecolumns .span2 {
            width: 18.2%;
            *width: 18.2%;
        }
    }

    @media (min-width: 1200px) {
        .fivecolumns .span2 {
            width: 17.9%;
            *width: 17.8%;
        }
    }

    @media (min-width: 768px) and (max-width: 979px) {
        .fivecolumns .span2 {
            width: 17.7%;
            *width: 17.7%;
        }
    }
    </style>

    <ga:trackPageview/>

</head>

<body>

<div id="wrapper">
    %{--The control area at the top of the page is all contained within this header--}%
    <g:render template="/bardWebInterface/homepageHeader"/>

    <div class="container-fluid">
        <div class="row-fluid">
            <div class="span12">
                <div class="spinner-container">
                    <div id="spinner" class="spinner" style="display:none; color: blue;"><g:message code="spinner.alt"
                                                                                                    default=""/></div>
                </div>
                <g:layoutBody/>
            </div>
        </div>
    </div>

    <g:render template="/layouts/templates/footer"/>

</div>
<r:layoutResources/>
</body>
</html>
