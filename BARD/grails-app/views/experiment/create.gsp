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

<%@ page import="bard.db.command.BardCommand; java.text.SimpleDateFormat; bard.db.project.*" %>
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/html">
<head>
    <r:require modules="core,bootstrap,datePicker"/>
    <meta name="layout" content="basic"/>
    <r:external file="css/bootstrap-plus.css"/>
    <title>Create Experiment</title>
</head>

<body>
<g:if test="${flash.message}">
    <div class="row-fluid">
        <div class="span12">
            <div class="ui-widget">
                <div class="ui-state-error ui-corner-all" style="margin-top: 20px; padding: 0 .7em;">
                    <p><span class="ui-icon ui-icon-alert" style="float: left; margin-right: .3em;"></span>
                        <strong>${flash.message}</strong>
                </div>
            </div>
        </div>
    </div>
</g:if>

<g:form action="save">
    <input type="hidden" name="assayId" value="${experimentCommand.assayId}"/>
    <input type="hidden" name="fromCreatePage" value="true"/>


    <g:hasErrors bean="${experimentCommand}">
        <div class="alert alert-error">
            <button type="button" class="close" data-dismiss="alert">×</button>
            <g:renderErrors bean="${experimentCommand}"/>
        </div>
    </g:hasErrors>

    <g:if test="${experimentCommand.experimentName == null || experimentCommand.errors}">

        <h3>Summary</h3>

        <dl class="dl-horizontal">

            <dt>* Name:</dt>
            <dd>
                <g:textArea class="input-xxlarge" name="experimentName" required="required"
                            value="${fieldValue(bean: experimentCommand, field: "experimentName")}"/>
            </dd>
            <dt>* Owner:</dt>
            <dd>
                <g:if test="${BardCommand.userRoles()}">
                    <g:select name="ownerRole" id="ownerRole" required="required"
                              from="${BardCommand.userRoles()}"
                              value="${experimentCommand?.ownerRole}"
                              optionValue="displayName" optionKey="authority"/>
                </g:if>
                <g:else>
                    You need to be part of a team to create Experiments. Follow this <g:link
                        controller="assayDefinition" action="teams">link</g:link> to the Teams Page
                </g:else>
            </dd>
            <dt>* Substance Identifier:</dt>
            <dd>

                <g:select name="substanceElementValue" id="substanceElementValue" required="required"
                          from="${ExperimentCommand.findSubstanceIdentifiers()}"
                          value="${experimentCommand?.substanceElementValue?.id}"
                          optionValue="label" optionKey="id"/>
            </dd>
            <dt>Description:</dt><dd>
            <g:textArea class="input-xxlarge" name="description"
                        value="${fieldValue(bean: experimentCommand, field: "description")}"/>
        </dd>

            <dd>
                <br/>

                <p>
                    <g:link controller="assayDefinition" action="show" id="${experimentCommand.assayId}"
                            class="btn">Cancel</g:link>
                    <input type="submit" class="btn btn-primary" value="Create"/>

                </p>
            </dd>
        </dl>

        <r:script type="text/javascript">

            $('.exp_datetime').datepicker({
                format: 'mm/dd/yyyy',
                autoclose: true
            });
        </r:script>
    </g:if>
</g:form>

</body>
</html>
