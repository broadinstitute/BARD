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