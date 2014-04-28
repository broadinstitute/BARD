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

<%@ page import="bard.db.model.AbstractContextOwner; org.springframework.security.acls.domain.BasePermission; bard.db.registration.*" %>
<!DOCTYPE html>
<html>
<head>
    <r:require modules="assaycompare"/>
    <meta name="layout" content="basic"/>
    <title>Create Assay Comparison Report</title>
</head>

<body>
<div class="container-fluid">
    <div class="row-fluid">
        <div class="span3">

        </div>

        <div class="span6">
            <h1>Assay Comparison Report</h1>
        </div>

        <div class="span3">

        </div>
    </div>
</div>

<div class="container-fluid">
    <div class="row-fluid">
        <div class="span3">

        </div>

        <div class="span6">
            <br/>
            <br/>
            <g:formRemote url="[controller: 'assayDefinition', action: 'generateAssayComparisonReport']"
                          name="compareForm"
                          update="[success: 'compare', failure: 'errorMsgId']"
                          onSuccess="handleSuccess()"
                          onFailure="handleError()">
                Enter ADIDs to compare : <g:textField name="assayOneId" class="adid" title="Enter an existing ADID"
                                                      required=""/>
                <g:textField name="assayTwoId" class="adid" title="Enter an existing ADID" required=""/>
                <input type="submit" value="compare"/>

                <div id="errorMsgId"></div>

            </g:formRemote>
        </div>

        <div class="span3">
        </div>
    </div>
</div>
<hr/>

<div id="compare">

</div>

</body>
</html>
