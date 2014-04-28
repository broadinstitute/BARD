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
    <r:require
            modules="core,bootstrap,moveExperiments"/>
    <meta name="layout" content="basic"/>
    <title>Move Experiments</title>

</head>

<body>
<div class="row-fluid">
    <div class="span2"></div>

    <div class="span8"><h2>Move Experiments</h2><br/><br/></div>

    <div class="span2"></div>

</div>

<div class="row-fluid">
    <div class="span2"></div>

    <div class="span8">
        <b>This page allows you to change the assay definition that describes one or more experiments.
        The primary use for this functionality is when two assay definitions are identified as being duplicates;
        then the experiments from one can be moved to the other and the one without any experiments can be retired.
        </b>
    </div>

    <div class="span2"></div>

</div>

<div class="container-fluid">
    <div class="row-fluid">
        <div class="span2"></div>

        <div class="span8">

            <g:formRemote class="form-horizontal" id="moveExperimentsForm"
                          url="[controller: 'moveExperiments', action: 'confirmMoveExperiments']"
                          name="mergeAssays"
                          update="[success: 'confirmMove', failure: 'confirmMove']">
                <div class="control-group">
                    <label class="control-label" for="idType">Identify experiments to Move:</label>

                    <div class="controls">
                        <g:select name="idType" id="idType"
                                  from="${IdType.values()}"
                                  value="${IdType.EID}"
                                  optionValue="name"/>
                    </div>
                </div>

                <div class="control-group">
                    <label class="control-label" for="idType"><div id="textBoxMessage">Identify Entities to Move</div>
                    </label>

                    <div class="controls">
                        <g:textField class="input-xxlarge" id="sourceEntityIds" name="sourceEntityIds" maxlength="2047" required=""/>
                    </div>
                </div>

                <div class="control-group">
                    <label class="control-label" for="idType">Assay Definition to move to (ADID):</label>

                    <div class="controls">
                        <g:textField id="targetAssayId" class="input-mini" name="targetAssayId" maxlength="6" required=""/>
                    </div>
                </div>

                <div class="control-group">
                    <label class="control-label"></label>
                    <input type="submit" class="btn btn-primary">
                </div>
            </g:formRemote>

        </div>

        <div class="span2"></div>
    </div>
    <br/>
    <hr/>

    <div id="confirmMove">

    </div>
</div>
</body>
</html>
