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

<%@ page import="bard.db.registration.IdType; bard.db.project.EntityType" %>
<!DOCTYPE html>
<html>
<head>
    <r:require modules="core,bootstrap,addExperimentsToProject"/>
    <meta name="layout" content="basic"/>
    <title>PID ${command?.project?.id}: ${command?.project?.name}</title>
</head>

<body>
<div class="container-fluid">
    <div class="row-fluid">
        <div class="span2"></div>

        <div class="span10"><h4>Add Experiments or Panel-Experiments To Project ${command?.project?.id} - ${command?.project?.name}</h4>
        </div>
    </div>

    <div class="row-fluid">
        <div class="span2"></div>

        <div class="span10">
            <g:form action="showExperimentsToAddProject" controller="project">

                <g:hasErrors bean="${command}">
                    <div class="alert alert-error">
                        <button type="button" class="close" data-dismiss="alert">×</button>
                        <g:renderErrors bean="${command}"/>
                    </div>
                </g:hasErrors>
                <g:if test="${command.errorMessages}">
                    <div class="alert alert-error">
                        <button type="button" class="close" data-dismiss="alert">×</button>

                        <ul>
                            <g:each in="${command.errorMessages}" var="errorMessage">
                                <li>${errorMessage}</li>
                            </g:each>
                        </ul>
                    </div>
                </g:if>

                <input type="hidden" name="fromAddPage" value="true"/>
                <g:hiddenField name="projectId" id="projectId" value="${command?.project?.id}"/>

            %{--make this field read-only if a type has already been selected--}%
                <h5><div>Choose Experiment or Panel-Experiment</div></h5>
                <g:select name="entityType" id="entityType" required=""
                          from="${EntityType.values()}"
                          value="${command.entityType}"
                          optionValue="id"/>

                <h5><div>Select Type of ID to Search By</div></h5>
                <g:select name="idType" id="idType" required=""
                          from="${IdType.values()}"
                          value="${command.idType}"
                          optionValue="name"/>

                <h5><div>Paste Ids to search by (space delimited).<br/>
                    If you chose the AssayDefinition ID, Panel ID, Panel-Experiment ID (or for some PubChem AIDs) above, you would be prompted to select from a list of matching experiments or panel-experiments.
                </div></h5>
                <g:textArea class="input-xxlarge" id="sourceEntityIds" name="sourceEntityIds"
                            value="${command.sourceEntityIds}" required=""/>


                <h5><div>Experiment Stage</div></h5>
                <g:hiddenField id="stageId" name="stageId" class="span10" style="margin-left: 0;"
                               value="${command?.stage?.id}"/>
                <br/>
                <br/>
                <h5><div>Selected Stage's Description</div></h5>
                <g:textArea id="stageDescription" name="stageDescription" class="span10"
                            value="${command.stage?.description}" disabled="disabled"/>

                <div id="selectExperimentsId">
                    <g:render template="selectExperimentsToAddToProjects" model="[command: command]"/>
                </div>

                <div id="selectPanelExperimentsId">
                    <g:render template="selectPanelExperimentsToAddToProjects" model="[command: command]"/>
                </div>

                <g:link controller="project" action="show" fragment="experiment-and-step-header"
                        id="${command.projectId}"
                        class="btn">Cancel</g:link>
                <input type="submit" class="btn btn-primary" name="addExperimentsOrPanelExperimentBtn" value="Add Experiments or Panel-Experiments"/>
            </g:form>
        </div>
    </div>
</div>
</body>
</html>
