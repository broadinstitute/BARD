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

<%@ page import="bard.db.dictionary.StatsModifierTree; bard.db.dictionary.ResultTypeTree; bard.db.command.BardCommand; java.text.SimpleDateFormat; bard.db.project.*" %>
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/html">
<head>
    <r:require modules="core,bootstrap,resulttype"/>
    <meta name="layout" content="basic"/>
    <title>Add Dose Result Types</title>
</head>

<body>
<r:script>


</r:script>
<div class="row-fluid">
    <div class="span2">

    </div>

    <div class="span9">
        <g:form action="saveDoseResultType">
            <input type="hidden" name="experimentId" value="${resultTypeCommand.experimentId}"/>
            <input type="hidden" name="fromCreatePage" value="true"/>


            <g:hasErrors bean="${resultTypeCommand}">
                <div class="alert alert-error">
                    <button type="button" class="close" data-dismiss="alert">×</button>
                    <g:renderErrors bean="${resultTypeCommand}"/>
                </div>
            </g:hasErrors>
            <dl class="dl-horizontal" id="extendwidth2">
                <dd><b>Please select what the primary result type that was used to characterize the dose response.</b><br/>
                    Answering this question will create the result type as well as flagging it as a primary result type.
                    <br/>
                </dd>

                <dt><g:message
                        code="command.doseresponse.resultType.label"/>:</dt>

                <dd>
                    <g:hiddenField id="concentrationResultTypeId" name="concentrationResultTypeId" class="span11"
                                   value="${resultTypeCommand?.concentrationResultType?.id}"/>
                </dd>
                <dt><g:message
                        code="command.resultType.description"/>:</dt>
                <dd>
                    <g:textArea id="concentrationResultTypeDescription" name="concentrationResultTypeDescription" class="span11"
                                value="${resultTypeCommand?.concentrationResultType?.description}" disabled="disabled"/>

                </dd>
                <dt><g:message
                        code="command.parent.resultType.label"/>:</dt>
                <dd>

                    <g:select name="parentExperimentMeasureId" id="doseParentExperimentMeasureId"
                              class="parentExperimentMeasureId"
                              noSelection="${['': 'none']}"
                              from="${currentExperimentMeasures}"
                              value="${resultTypeCommand?.parentExperimentMeasure?.resultType?.label}"
                              optionValue="${{ it.resultType?.label }}" optionKey="id"/>
                </dd>
                <dd>
                    <div id="concentrationMessageId">
                        <b>Note: selecting a result type will add additional result types for 'Hill sinf', 'Hill s0', 'Hill coefficient'" </b>
                    </div>
                </dd>
            </dl>
            <br/>
            <br/>
            <dl class="dl-horizontal" id="extendwidth2">
                <dd><b>Please select the result type that the dose response is computed from.</b><br/>
                    The selected result type will automatically be associated with a context containing
                    "screening concentration". If there isn't one, the system will create one for you
                    <br/>
                </dd>
                <dt><g:message
                        code="command.resultType.label"/>:</dt>
                <dd>
                    <g:hiddenField id="responseResultTypeId" name="responseResultTypeId" class="span11"
                                   value="${resultTypeCommand?.responseResultType?.id}"/>
                </dd>

                <dt><g:message
                        code="command.resultType.description"/>:</dt>
                <dd>
                    <g:textArea id="responseResultTypeDescription" name="responseResultTypeDescription" class="span11"
                                value="${resultTypeCommand?.responseResultType?.description}" disabled="disabled"/>

                </dd>
                <dt><g:message
                        code="command.statsModifier.label"/>:</dt>
                <dd>

                    <g:hiddenField id="statsModifierId" name="statsModifierId" class="span11"
                                   value="${resultTypeCommand?.statsModifier?.id}"/>
                </dd>

                <dt><g:message
                        code="command.statsModifier.description"/>:</dt>
                <dd>
                    <g:textArea id="statsModifierDescription" name="statsModifierDescription" class="span11"
                                value="${resultTypeCommand?.statsModifier?.description}" disabled="disabled"/>

                </dd>

                <dd>
                    <br/>

                    <div class="controls">
                        <g:link controller="experiment" action="show"
                                id="${resultTypeCommand?.experimentId}"
                                class="btn">Cancel</g:link>
                        <input type="submit" class="btn btn-primary" value="Save">
                    </div>
                </dd>
            </dl>
        </g:form>
    </div>
</div>
</body>
</html>
