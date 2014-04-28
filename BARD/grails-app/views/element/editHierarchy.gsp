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
  User: dlahr
  Date: 4/22/13
  Time: 11:01 PM
  To change this template use File | Settings | File Templates.
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <r:require modules="core,bootstrap,elementEditHierarchy,elementSelect"/>
    <meta name="layout" content="basic"/>
    <title>Edit Element Hierarchy</title>
</head>

<body>
<sec:ifAnyGranted roles="ROLE_CURATOR">
    <h1>Edit Element Hierarchy</h1>

    <g:if test="${flash.message}">
        <div class="row-fluid">
            <div class="span12">
                <div class="ui-widget">
                    <div class="ui-state-error ui-corner-all" style="margin-top: 20px; padding: 0 .7em;">
                        <p><span class="ui-icon ui-icon-alert" style="float: left; margin-right: .3em;"></span>
                            <strong>${flash.message}</strong>
                        </p>
                    </div>
                </div>
            </div>
        </div>
    </g:if>

    <div class="row-fluid">
        <div class="span10 offset1">
            <h3>Hierarchy Path(s)</h3>
            <g:form controller="element" action="deleteElementPath" class="form-inline" role="form">
                <g:each in="${list}" var="elementAndPath">
                    <div class="form-group">
                        <label for="${elementAndPath} " style="cursor: auto;">${elementAndPath}</label>
                        <g:if test="${elementAndPath.path}">
                            <button id="${elementAndPath}" type="submit" class="btn-small" value="0"
                                    button-role="deleteElementPath" style="margin-left: 10px;">
                                <i class="icon-remove"></i>
                            </button>
                        </g:if>
                        <g:else>
                            <label class="bg-primary"
                                   style="cursor: auto; color: #0000ff">(element is a root)</label>
                        </g:else>
                    </div>
                </g:each>

                <g:hiddenField name="elementId" value="${element.id}"/>
            %{--Updated via JS--}%
                <g:hiddenField name="elementPathToDelete" id="elementPathToDelete" value=""/>
            </g:form>
        </div>
    </div>

    <div class="row-fluid">
        <div class="span11 offset1">
            <h3>Add a New Hierarchy Path</h3>

            <p style="color: #0000ff">Please select a new parent-element for '${element.label}' to be added to the element hierarchy:</p>
        </div>
    </div>

    <div class="row-fluid" style="margin: 5px;">
        <g:form controller="element" action="addElementPath" name="addElementPathForm">

            %{--Select2 widget--}%
            <div id="elementList" name="elementList" class="span10 offset1"></div>

            <g:hiddenField name="elementId" value="${element.id}"/>
            <g:hiddenField name="select2FullPath" id="select2FullPath" value=""/>
            <g:hiddenField name="select2ElementId" id="select2ElementId" value=""/>
        </g:form>
    </div>

</sec:ifAnyGranted>
</body>
</html>
