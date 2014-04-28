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

<%@page defaultCodec="none"%>
<%@ page import="bard.core.rest.spring.util.StructureSearchParams" contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>JSDraw Structure Editor</title>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <r:require modules="jqueryMobilePreInit"/>
    <r:layoutResources/>
    %{--This whole section here of importing JQuery Mobile is needed because of issues with the resources manager and loading order.--}%
    <link rel="stylesheet" type="text/css"
          href="${request.contextPath}/jquery.mobile-1.3.1/jquery.mobile-1.3.1.css"/>
    %{--<link rel="stylesheet" type="text/css"--}%
          %{--href="${request.contextPath}/jquery.mobile-1.3.1/jquery.mobile-1.3.1.min.css"/>--}%
    <link rel="stylesheet" type="text/css"
          href="${request.contextPath}/jquery.mobile-1.3.1/jquery.mobile.structure-1.3.1.css"/>
    %{--<link rel="stylesheet" type="text/css"--}%
          %{--href="${request.contextPath}/jquery.mobile-1.3.1/jquery.mobile.structure-1.3.1.min.css"/>--}%
    <link rel="stylesheet" type="text/css"
          href="${request.contextPath}/jquery.mobile-1.3.1/jquery.mobile.theme-1.3.1.css"/>
    %{--<link rel="stylesheet" type="text/css"--}%
          %{--href="${request.contextPath}/jquery.mobile-1.3.1/jquery.mobile.theme-1.3.1.min.css"/>--}%
    <script type="text/javascript" src="${request.contextPath}/jquery.mobile-1.3.1/jquery.mobile-1.3.1.js"></script>
    %{--<script type="text/javascript"--}%
            %{--src="${request.contextPath}/jquery.mobile-1.3.1//jquery.mobile-1.3.1.min.js"></script>--}%
    <link rel="stylesheet" type="text/css" href="${request.contextPath}/css/bard-mobile.css"/>
    <script type="text/javascript" src="${request.contextPath}/js/jqueryMobilePostInit.js"></script>

    <script type="text/javascript" src="${request.contextPath}/js/dojo-min/dojo/dojo.js"></script>
    <script type="text/javascript" src="${request.contextPath}/js/jsDraw/Scilligence.JSDraw2.js"></script>
    <script type="text/javascript" src="${request.contextPath}/js/jsDraw/license.js"></script>
    <r:script>
        var jsDrawEditor = null;

        dojo.addOnLoad(function () {
            jsDrawEditor = new JSDraw2.Editor("jsDrawEditorDiv", {popup:false});
            var width = dojo.window.getBox().w * 0.95;
            var height = dojo.window.getBox().h * 0.5;
            if (width && height && jsDrawEditor) {
                jsDrawEditor.setSize(width, height)
            }
            jsDrawEditor.readCookie();
        });

        //On clicking the Submit button, switch back to the SearchResult page and submit the search form
        $(document).on('click', '#searchButtonMobile', function () {
            var smiles = jsDrawEditor.getSmiles();
            var structureSearchTypeSelected = $('input:radio[name=structureSearchType]:checked').attr('value');
            var constructedSearch = structureSearchTypeSelected + ":" + smiles;
            var changePageTo = "${createLink(controller: 'bardWebInterface', action: 'searchResults')}" + "?searchString=" + constructedSearch;
            jsDrawEditor.writeCookie();
            window.location.href = changePageTo;
        });
    </r:script>
</head>

<body>
<div data-role="page" id="jsDrawEditorMobile">
    <div data-role="content" style="text-align: center">

        <div data-role="fieldcontain" id="jsDrawEditorDiv" skin='w8'
             style="width: 480px; height: 600px;border:1px solid gray"></div>

        <div data-role="fieldcontain" style="text-align: left">
            <g:radioGroup name="structureSearchType"
                          values="${StructureSearchParams.Type.values()}"
                          value="${StructureSearchParams.Type.Substructure}"
                          labels="${StructureSearchParams.Type.values()}">
                <label class="radio inline">
                    ${it.radio} ${it.label}
                </label>
            </g:radioGroup>
        </div>

        <div data-role="fieldcontain">
            <button id="searchButtonMobile">Search</button>
        </div>
    </div><!-- /content -->

</div><!-- /page -->
<r:layoutResources/>
</body>
</html>
