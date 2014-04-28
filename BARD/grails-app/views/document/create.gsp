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

<%@ page import="bard.db.registration.*" %>
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/html">
<head>
    <r:require modules="core,bootstrap,assayshow,richtexteditorForCreate"/>
    <meta name="layout" content="basic"/>
    <title>Create Assay Document</title>

</head>

<body>
<div class="container-fluid">
    <div class="row-fluid">
        <h3>Create a New Document</h3>
    </div>
    <g:render template="/common/message"/>
    <div class="row-fluid">
        <div class="span12">
            <g:form class="form-horizontal" action="save">
                <g:hiddenField name="assayId" value="${document?.assayId}"/>
                <g:hiddenField name="projectId" value="${document?.projectId}"/>
                <g:hiddenField name="experimentId" value="${document?.experimentId}"/>

                <g:render template="editProperties" model="${[document: document]}"/>

                <div class="control-group">
                    <div class="controls">
                        <g:link controller="${document?.ownerController}" action="show" id="${document?.ownerId}"
                                fragment="documents-header" class="btn">Cancel</g:link>
                        <input type="submit" class="btn btn-primary" value="Create">
                    </div>
                </div>

            </g:form>
        </div>
    </div>
</div>
</body>
</html>
