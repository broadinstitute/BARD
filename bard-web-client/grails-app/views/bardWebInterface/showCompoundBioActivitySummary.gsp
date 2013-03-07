<%@ page import="bardqueryapi.FacetFormType" contentType="text/html;charset=UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="logoSearchCartAndFooter"/>
    <title>BARD : Compound Bio-Activity Summary: ${tableModel?.additionalProperties?.cid}</title>
    <r:require modules="core"/>
</head>

<body>
<div class="row-fluid">
    <g:render template="facets" model="['facets': facets, 'formName': FacetFormType.CompoundBioActivitySummaryForm]"/>
    <g:hiddenField name="compoundId" id='compoundId' value="${params?.id}"/>

    <div class="span9">
        <div id="compoundBioActivitySummaryDiv">
        </div>
    </div>
</div>
</body>
</html>