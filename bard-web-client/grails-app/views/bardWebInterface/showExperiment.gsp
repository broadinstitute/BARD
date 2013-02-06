<%@ page import="bardqueryapi.ActivityOutcome; bardqueryapi.NormalizeAxis; bard.core.interfaces.ExperimentValues" contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="logoSearchCartAndFooter"/>
    <title>BARD : Experiment Result : ${experimentId}</title>
    <r:require modules="experimentData, bootstrap, compoundOptions"/>
</head>

<body>
<div id="formParameters">
    <g:form action="showExperiment" controller="bardWebInterface" id="${params?.id}" name="showExperimentForm">
        %{--<label for="activityOutcome">Activity Outcome:</label>--}%
        %{--<g:select name="activityOutcome" from="${ActivityOutcome.values()}" value="${params?.activityOutcome}" optionValue="label"/>--}%
        <label for="normalizeYAxis">Plot Axis:</label>
        <g:select name="normalizeYAxis" from="${NormalizeAxis.values()}" value="${params.normalizeYAxis}" optionValue="label"/>  <br/>
        <g:submitButton name="filter" value="filter" class="btn btn-primary span2" id="showExperimentBtnId"/>
    </g:form>
</div>

<div id="experimentalResults">
    <g:render template='experimentResultData' model='[experimentDataMap: experimentDataMap]'/>
</div>
</body>
</html>