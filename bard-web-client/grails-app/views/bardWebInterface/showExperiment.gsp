<%@ page import="bardqueryapi.FacetFormType; bardqueryapi.ActivityOutcome; bardqueryapi.NormalizeAxis; bard.core.interfaces.ExperimentValues" contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="logoSearchCartAndFooter"/>
    <title>BARD : Experiment Result : ${tableModel.additionalProperties.capExptId}</title>
    <r:require modules="experimentData, bootstrap, compoundOptions, cbas"/>
</head>

<body>
<div class="row-fluid" id="showExperimentDiv">
    <g:render template="facets" model="['facets': facets, 'formName': FacetFormType.ExperimentFacetForm]"/>
    <g:hiddenField name="experimentId" id='experimentId' value="${params?.id}"/>

    <div class="span9">
        <div id="experimentalResults">
            <g:render template='experimentResultData' model='[tableModel: tableModel, innerBorder: false]'/>
        </div>
    </div>
</div>
</body>
</html>