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


            <h3>Add a result type</h3>
            <h4>Here you can add a result type which you'll provide in the result submission</h4>

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

                <g:render template="../context/show"
                          model="[contextOwner: contextOwner, contexts: contextOwner.groupContexts(),
                                  uneditable: true, showCheckBoxes: true, existingContextIds: resultTypeCommand.contextIds]"/>
                <g:render template="../context/currentCard"
                          model="[contextOwner: contextOwner, currentCard: contextOwner.groupExperimentalVariables(), subTemplate: 'show', renderEmptyGroups: false,
                                  showCheckBoxes: true, existingContextIds: resultTypeCommand.contextIds
                          ]"/>

            </div>

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