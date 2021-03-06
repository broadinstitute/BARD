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

<%@ page import="bard.db.registration.ExternalSystem; bard.db.registration.ExternalReference" %>
<!DOCTYPE html>
<html>
<head>
    <r:require modules="core,bootstrap,twitterBootstrapAffix,card,histogram"/>
    <meta name="layout" content="basic"/>
    <g:if test="${isEdit}">
        <title>Update an External Reference</title>
    </g:if>
    <g:else>
        <title>Create an External Reference</title>
    </g:else>
</head>

<body>
<g:hasErrors>
    <div class="row-fluid">
        <div class="span12">
            <div class="ui-widget">
                <div class="ui-state-error ui-corner-all" style="margin-top: 20px; padding: 0 .7em;">
                    <g:renderErrors bean="${externalReferenceInstance}"/>
                </div>
            </div>
        </div>
    </div>
</g:hasErrors>

<g:if test="${externalReferenceInstance}">
<div class="row-fluid">
    <div class="span12">
        <g:if test="${isEdit}">
            <h3>Update External Reference</h3>
        </g:if>
        <g:else>
            <h3>Create a New External Reference</h3>
        </g:else>

        <g:form class="form-horizontal" controller="externalReference" action="${isEdit ? "edit" : "save"}">
            <g:hiddenField name="id" value="${externalReferenceInstance.id ?: ''}"/>
            <g:hiddenField name="project.id" value="${externalReferenceInstance.project?.id ?: ''}"/>
            <g:hiddenField name="experiment.id" value="${externalReferenceInstance.experiment?.id ?: ''}"/>

            <g:if test="${externalReferenceInstance.project}">
                <div class="control-group">
                    <label class="control-label" for="projectName">Project Name:</label>

                    <div class="controls">
                        <g:textField id="projectName" name="project.name" disabled="disabled" size="100"
                                     style="width: auto;" value="${externalReferenceInstance.project.name}"/>
                    </div>
                </div>
            </g:if>

            <g:if test="${externalReferenceInstance.experiment}">
                <div class="control-group">
                    <label class="control-label" for="experimentName">Experiment Name:</label>

                    <div class="controls">
                        <g:textField id="experimentName" name="experiment.name" disabled="disabled" size="100"
                                     style="width: auto;"
                                     value="${externalReferenceInstance.experiment.experimentName}"/>
                    </div>
                </div>
            </g:if>

            <div class="control-group">
                <label class="control-label" for="externalSystem.id">External System:</label>

                <div class="controls">
                    <g:select name="externalSystem.id" from="${ExternalSystem.list()}" optionKey="id"
                              optionValue="systemName"/>
                </div>
            </div>

            <div class="control-group">
                <label class="control-label" for="extAssayRef">External Assay Reference:</label>

                <div class="controls">
                    <g:textField id="extAssayRef" name="extAssayRef" value="${externalReferenceInstance.extAssayRef}"/>

                    <small>(please note: links to PubChem AID are formated as 'aid=xxxx' where xxxx is the AID number; links to Common Assay Reporting System (CARS) are formated as 'project_UID=xxxx')</small></label>
                </div>
            </div>

            <div class="control-group">
                <div class="controls">
                    <g:link controller="${externalReferenceInstance.project ? 'project' : 'experiment'}" action="show"
                            id="${externalReferenceInstance.project?.id ?: externalReferenceInstance.experiment.id}"
                            class="btn">Cancel</g:link>
                    <g:if test="${isEdit}">
                        <input type="submit" class="btn btn-primary" value="Update">
                    </g:if>
                    <g:else>
                        <input type="submit" class="btn btn-primary" value="Create">
                    </g:else>
                </div>
            </div>
        </g:form>
    </div>
</div>
</g:if>

</body>
</html>
