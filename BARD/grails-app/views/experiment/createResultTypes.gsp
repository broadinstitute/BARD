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

<%@ page import="bard.db.enums.HierarchyType; bard.db.registration.Assay; bard.db.command.BardCommand; java.text.SimpleDateFormat; bard.db.project.*" %>
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/html">
<head>
    <r:require modules="core,bootstrap,resulttype"/>
    <meta name="layout" content="basic"/>
    <title>Add Result Types</title>
</head>

<body>
<r:script>


</r:script>
<div class="row-fluid" id="showExperimentDiv">
    <div class="span2">

    </div>
    <% Assay contextOwner = resultTypeCommand.experiment.assay %>
    <div class="span9">
        <g:form action="saveResultType">
            <input type="hidden" name="experimentId" value="${resultTypeCommand.experimentId}"/>
            <input type="hidden" name="experimentMeasureId" value="${resultTypeCommand.experimentMeasureId}"/>
            <input type="hidden" name="fromCreatePage" value="true"/>


            <g:hasErrors bean="${resultTypeCommand}">
                <div class="alert alert-error">
                    <button type="button" class="close" data-dismiss="alert">×</button>
                    <g:renderErrors bean="${resultTypeCommand}"/>
                </div>
            </g:hasErrors>

            <g:if test="${canEditMeasures}">
                <h3>Add a result type</h3>
                <h4>Here you can add a result type which you'll provide in the result submission</h4>
            </g:if>
            <g:else>

                <h3>This experiment already has data loaded. While data exists, you can only change the priority element,
                but all other fields require reloading the data.</h3>
            </g:else>


            <dl class="dl-horizontal" id="extendwidth">
            <dt>
             <b><g:message
                code="command.priority.element.label"/>:</b>
            <g:render template="priorityElementDictionary"/>

            </dt>

            <dd>
                <g:checkBox name="priorityElement" value="${resultTypeCommand.priorityElement}"/>
                <br/>
            </dd>
            <g:if test="${canEditMeasures}">
                <dt><g:message
                        code="command.resultType.label"/>:</dt>
                <dd>
                    <g:hiddenField id="resultTypeId" name="resultTypeId" class="span11"
                                   value="${resultTypeCommand?.resultType?.id}"/>
                </dd>

                <dt><g:message
                        code="command.resultType.description"/>:</dt>
                <dd>
                    <g:textArea id="resultTypeDescription" name="resultTypeDescription" class="span11"
                                value="${resultTypeCommand?.resultType?.description}" disabled="disabled"/>

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
                <g:if test="${currentExperimentMeasures}">

                    <dt><g:message
                            code="command.parent.resultType.label"/>:</dt>
                    <dd>
                        <g:select name="parentExperimentMeasureId" id="parentExperimentMeasureId"
                                  class="parentExperimentMeasureId"
                                  noSelection="${['': 'none']}"
                                  from="${currentExperimentMeasures}"
                                  value="${resultTypeCommand?.parentExperimentMeasure?.id}"
                                  optionValue="${{ it.resultType?.label }}" optionKey="id"/>
                    </dd>
                    <dd><br/>Relationship to Parent</dd>
                    <dt>

                    <div id="selectedParentLabelId"></div>
                    </dt>
                    <dd>
                        <g:select name="parentChildRelationship" id="parentChildRelationshipId"
                                  class="parentChildRelationship"
                                  noSelection="${['': 'none']}"
                                  from="${HierarchyType.values()}"
                                  value="${resultTypeCommand?.parentChildRelationship}"
                                  optionValue="id" optionKey="id"/>

                        <span id="hierarchyLabelId"></span>
                    </dd>
                </g:if>
                <br/><br/><br/>


                <div class="row-fluid">
                    <div class="span12">
                        <b style="text-decoration:underline;">Associated independent variables</b> <br/>
                        Each result type can provide additional contextual information by linking the result type to one or more contexts defined on the assay.
                        <br/>Check each context which will have values provided along with this result type.<br/>

                        <p>
                            Some results will need specific data to fully describe them.
                            For example  each "percent activity" will need a "screening concentration (molar)" value.
                            For an experiment done with different variables for each curve, an "AC50" will need an "assay readout name".
                        </p>

                        <p>
                            In this section you can select which of the pre-defined contexts in the assay definition go with each of the result types.
                            Some result types do not need any specific context attached to them.
                            Some results may need to be attached to several different contexts.
                        </p>
                        <br/><br/>
                    </div>

                    <g:render template="../context/currentCard"
                              model="[contextOwner: contextOwner, additionalContexts: contextOwner.groupUnclassified(),
                                      subTemplate: 'show',
                                      showCheckBoxes: true, existingContextIds: resultTypeCommand.contextIds]"/>
                    <g:render template="../context/currentCard"
                              model="[contextOwner: contextOwner, currentCard: contextOwner.groupExperimentalVariables(),
                                      subTemplate: 'show', renderEmptyGroups: false,
                                      showCheckBoxes: true, existingContextIds: resultTypeCommand.contextIds]"/>

                </div>
            </g:if>
            <dd>
                <br/>

                <p>
                    <g:link controller="experiment" action="show" id="${resultTypeCommand.experimentId}"
                            class="btn" fragment="result-type-header">Cancel</g:link>
                    <input type="submit" class="btn btn-primary" value="Save"/>

                </p>
            </dd>
            </dl>
        </g:form>
    </div>
</div>
</body>
</html>
