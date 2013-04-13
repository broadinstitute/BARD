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

<g:hasErrors bean="${experiment}">
  	<div class="alert alert-error">
    	<button type="button" class="close" data-dismiss="alert">×</button>
     	<g:renderErrors bean="${experiment}"/>
   	</div>
</g:hasErrors>

<g:form action="save">
    <input type="hidden" name="assayId" value="${assay.id}"/>

    <p>
        <input type="submit" class="btn btn-primary" value="Create"/>
    </p>

    <g:render template="editFields" model="${[experimentMeasuresAsJsonTree: experimentMeasuresAsJsonTree, assayMeasuresAsJsonTree: assayMeasuresAsJsonTree, experiment: experiment, assay: assay]}"/>
</g:form>

</body>
</html>