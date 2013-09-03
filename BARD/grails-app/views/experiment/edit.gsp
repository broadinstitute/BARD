<%@ page import="bard.db.project.*" %>
<!DOCTYPE html>
<html>
<head>
    <r:require modules="core,bootstrap"/>
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

<div class="row-fluid">
    <div class="span12">
        <div class="well well-small">
            <div class="pull-left">
                <h4>Edit Experiment Measures</h4>
            </div>
        </div>
    </div>
</div>

<g:form action="update" id="${experiment.id}">
    <p>
        <g:link action="show" id="${experiment?.id}" class="btn">Cancel</g:link>
        <input type="submit" class="btn btn-primary" value="Update"/>
    </p>

    <g:render template="editFields" model="${[experimentMeasuresAsJsonTree: experimentMeasuresAsJsonTree, assayMeasuresAsJsonTree: assayMeasuresAsJsonTree, experiment: experiment, assay: experiment.assay]}"/>
</g:form>

</body>
</html>