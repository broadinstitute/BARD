<%@ page import="bardqueryapi.FacetFormType; bardqueryapi.ActivityOutcome; bardqueryapi.NormalizeAxis; bard.core.interfaces.ExperimentValues" contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html>experimentInfoUnavailable
<head>
    <r:require
            modules="core,bootstrap,twitterBootstrapAffix,xeditable,experimentsummary,canEditWidget,richtexteditorForEdit, sectionCounter, card,histogram"/>
    <meta name="layout" content="basic"/>
    <r:external file="css/bootstrap-plus.css"/>
    <title>${message}</title>
</head>

<body>
<div class="row-fluid">
   <g:render template="/layouts/templates/restapiunavailable"/>
</div>
</body>
</html>