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

<%@ page import="bard.db.experiment.Experiment; bard.db.registration.Panel" %>
<!DOCTYPE html>
<html>
<head>
    <r:require modules="core,bootstrap,twitterBootstrapAffix,card, panelExperiment"/>
    <meta name="layout" content="basic"/>
    <title>Create a New Panel-Experiment</title>
</head>

<body>
<g:hasErrors>
    <div class="row-fluid">
        <div class="span12">
            <div class="ui-widget">
                <div class="ui-state-error ui-corner-all" style="margin-top: 20px; padding: 0 .7em;">
                    <g:renderErrors bean="${panelExperimentCommand}"/>
                </div>
            </div>
        </div>
    </div>
</g:hasErrors>

<div class="row-fluid">
    <div class="span12">
        <h3>Create a New Panel-Experiment</h3>

        <g:form class="form-horizontal" controller="panelExperiment" action="save">

            <g:hiddenField name="confirmExperimentPanelOverride"
                           value="${panelExperimentCommand.confirmExperimentPanelOverride}"/>
            <g:hiddenField name="panelExperiment.id"
                           value="${panelExperimentCommand.panelExperiment?.id}"/>

            <div class="control-group">
                <label class="control-label" for="panel.id">Panel:</label>

                <div class="controls">
                    <g:select name="panel.id"
                              from="${panelExperimentCommand.panel ?: Panel.list().sort { a, b -> a.id <=> b.id }}"
                              optionKey="id"
                              optionValue="displayName" value="${panelExperimentCommand.panel}"
                              style="width: 900px;"/>
                </div>
            </div>

            <div class="control-group">
                <div class="controls" id="bigSpinnerImage"></div>
            </div>

            <div class="control-group">
                <label class="control-label" for="experimentIds">Experiments:</label>

                <div class="controls">
                    <select multiple id="experimentIds" style="width: 900px;"></select>
                </div>
            </div>

            <div class="control-group">
                <div class="controls">
                    <g:link controller="bardWebInterface" action="index" class="btn">Cancel</g:link>

                    <input type="submit" class="btn btn-primary" value="Create">
                </div>
            </div>
        </g:form>
    </div>
</div>

</body>
</html>
