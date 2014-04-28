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

<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>BioAssay Research Database</title>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <r:require modules="jqueryMobile, autocomplete, search, bootstrap, core"/>
    <r:layoutResources/>
</head>

<body>

<!-- page 'search' -->
<div data-role="page" id="search">
    <div>
        <g:render template="/layouts/templates/mobileLoginStrip"/>
    </div>

    <div data-role="content" style="text-align: center">
        <a href="${createLink(controller: 'BardWebInterface', action: 'index')}">
            <img src="${resource(dir: 'images', file: 'bard_logo_lg.jpg')}" align="middle" style="max-width: 80%;"
                 alt="BioAssay Research Database"/>
        </a>

        <div>
            <g:form name="searchFormMobile" controller="bardWebInterface" action="searchResults" method="post"
                    id="searchFormMobile" data-ajax="false">
                <div data-role="fieldcontain">
                    <label for="searchString">Search BARD:</label>
                    <input type="search" id="searchString" name="searchString"
                           value="${flash.searchString ?: params?.searchString ?: ''}"/>
                </div>

                <div data-role="fieldcontain">
                    <g:link controller="bardWebInterface" action="jsDrawEditor" class="ui-link-inherit"
                            data-ajax='false'>
                        <img src="${resource(dir: 'images', file: 'structureEditIcon.png')}"
                             alt="Draw or paste a structure"
                             title="Draw or paste a structure"/> Draw or paste a structure
                    </g:link>
                </div>

                <div data-role="fieldcontain">
                    <button id="searchButton">Search</button>
                </div>
            </g:form>
        </div>

        <div>
            <g:link controller="bardWebInterface" action="turnoffMobileExperience" data-ajax="false">
                <g:message code="mobile.disable.experience" default="Switch to the regular website"/>
            </g:link>
        </div>
    </div><!-- /content -->

<g:link controller="bardWebInterface" data-role="button" data-inline="true" data-icon="back" data-ajax="false"
        data-transition="none">Back</g:link>
</div><!-- /page -->

<r:layoutResources/>
</body>
</html>
